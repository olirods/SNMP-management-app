package com.example.clientesnmp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.CardView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

public class CheckService extends Service {
    public static Integer minutosCheck = 1;
    private int user_id;

    private NotificationManagerCompat notificationManager;
    private static final String CHANNEL_2_ID = "channel2";
    private void createNotificationsChannels(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Channel 2",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel2.setDescription("Canal que informa sobre el estado de los dispositivos");

            NotificationManager manager = this.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel2);
        }

    }

    public void sendOnChannel2(Integer online){
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("user_id",user_id);

        String content;

        if (online == 1) {
            content = "Un dispositivo ha recuperado la conexión.";
        } else {
            content = "Un dispositivo ha perdido la conexión.";
        }

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 2, resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.notif)
                .setContentTitle("Un dispositivo ha cambiado de estado")
                .setContentText("Pulse para verlo")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .build();
        notificationManager.notify(2,notification);
    }

    public void onCreate() {


        createNotificationsChannels();
        notificationManager = NotificationManagerCompat.from(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        user_id = intent.getIntExtra("user_id", 0);

        Database database = Database.getDatabase(getApplicationContext());
        final EquipoDao equipoDao = database.equipoDao();

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<EquipoEntity> equipos = equipoDao.getEquipos(new Integer(user_id));
                for (int i = 0; i < equipos.size(); i++) {
                    String id = equipos.get(i).getId_e().toString();
                    String ip = equipos.get(i).getIP();
                    new mAsyncTaskCheck().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id, ip);
                }
            }
        }).start();

        this.stopSelf();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // I want to restart this service again in one hour
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, CheckService.class);

        intent.putExtra("user_id",user_id);

        alarm.set(
                alarm.RTC_WAKEUP,
                System.currentTimeMillis() + (1000 * 60 * minutosCheck),
                PendingIntent.getService(this, 0, intent, 0)
        );
    }

    class mAsyncTaskCheck extends AsyncTask<String, String[], String[]> {

        String[] respuesta =  new String[2];

        protected void onPreExecute() {
            //mSpinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {
            try {
                respuesta[0] = params[0];

                respuesta[1] = new SNMPRequest().sendSnmpGetNext(".1.3.6.1.2.1.1.6", params[1]);

            } catch (Exception e) {
                //  Log.d(TAG,
                //         "Error sending snmp request - Error: " + e.getMessage());
                // tv1.setText(e.getMessage());
                respuesta[1] = "fallo";
            }
            return respuesta;
        }

        protected void onPostExecute(String[] result) {
            Database database = Database.getDatabase(getApplicationContext());
            final EquipoDao equipoDao = database.equipoDao();
            final LogDao logDao = database.logDao();

            final Integer id = new Integer(result[0]);

            Integer online_aux = 0;
            String message_aux;

            if (result[1] != "fallo" && result[1] != "" && result[1] != null) {
                //ONLINE
                online_aux = 1;
                message_aux = " ha recuperado la conexión.";
            } else {
                //OFFLINE
                online_aux = 0;
                message_aux = " ha perdido la conexión.";
            }

            final Integer online = online_aux;
            final String message = message_aux;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    EquipoEntity equipo = equipoDao.getEquipo(id);

                    Integer oldOnline = equipo.getOnline();

                    if (!oldOnline.equals(online)){
                        sendOnChannel2(online);
                        final LogEntity logEntity = new LogEntity();
                        logEntity.setId_u(user_id);
                        logEntity.setCreateDate(Calendar.getInstance().getTime());
                        logEntity.setMessage("El equipo " + equipo.getNombre_e() + " con IP " + equipo.getIP() + message);

                        logDao.insertLog(logEntity);
                        equipoDao.updateOnline(id, online);
                    }
                }
            }).start();




        }
    }
}
