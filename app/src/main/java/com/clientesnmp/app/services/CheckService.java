package com.clientesnmp.app.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.clientesnmp.app.MainActivity;
import com.clientesnmp.app.R;
import com.clientesnmp.app.SNMPRequest;
import com.clientesnmp.app.database.Database;
import com.clientesnmp.app.database.EquipoDao;
import com.clientesnmp.app.database.EquipoEntity;
import com.clientesnmp.app.database.LogDao;
import com.clientesnmp.app.database.LogEntity;

import java.util.Calendar;
import java.util.List;

/**
 * Servicio de monitorización del estado de los dispositivos. Este servicio, que empieza a correr
 * en segundo plano desde que se inicia la aplicación, tiene la función de mandar cada cierto tiempo
 * (según la frecuencia indicada) un mensaje GET de SNMP para comprobar que se recibe respuesta
 * por parte del dispositivo, lo que significará que se encuentra online.
 *
 * Funciona de la siguiente forma: se enviará un mensaje y se espera su respuesta, y según ello
 * comprueba si varía el estado del dispositivo con respecto al guardado en la base de datos.
 * Tras ello, se destruye el servicio y se programa una nueva comprobación para el futuro, según
 * la frecuencia indicada.
 */
public class CheckService extends Service {
    private int user_id; // Identificador del usuario logueado

    private NotificationManagerCompat notificationManager;

    private static final String CHANNEL_2_ID = "channel2";

    /**
     * Frecuencia de envío de mensajes. Modificable por el usuario a través del SettingsFragment
     */
    public static Integer frecuencia = 1; // (MINUTOS)

    /**
     * Configuramos el canal por el que enviaremos las notificaciones al teléfono
     */
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

    /**
     * Se ejecutará una vez al crear el servicio. En este caso sólo una vez.
     */
    public void onCreate() {
        // Habilitamos la gestión de notificaciones en este servicio
        createNotificationsChannels();
        notificationManager = NotificationManagerCompat.from(this);
    }

    /**
     * Se ejecutará una vez se arranque el servicio. En este caso será una vez cada tiempo indique
     * la frecuencia.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Obtenemos de la MainActivity el identificador del usuario logueado
        user_id = intent.getIntExtra("user_id", 0);

        // Obtenemos la instancia de la base de datos y el DAO
        Database database = Database.getDatabase(getApplicationContext());
        final EquipoDao equipoDao = database.equipoDao();

        /* Hilo donde gestionamos las operaciones con la base de datos. Aquí obtenemos los equipos
         * actuales y enviamos ponemos en marcha un task que envíe un mensaje de comprobación a
         * cada uno.
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<EquipoEntity> equipos = equipoDao.getEquipos(new Integer(user_id));
                for (int i = 0; i < equipos.size(); i++) {
                    String id_e = equipos.get(i).getId().toString();
                    String ip = equipos.get(i).getIP();

                    /* Ponemos en marcha el task
                     * (La opción THREAD_POOL_EXECUTOR nos permite que resuelva varios tasks en paralelo,
                     * de lo contrario, tendría que esperar a que terminase uno para resolver otro)
                     *
                     * Aquí hemos añadido DOS argumentos: id e ip
                     */
                    new mAsyncTaskCheck().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id_e, ip);
                }
            }
        }).start();

        stopSelf(); // Destruimos una vez realizada la tarea. De aquí nos vamos a onDestroy()
        return START_NOT_STICKY;
    }

    /**
     * Enviar la notificación al teléfono del usuario, esté en la app o no.
     */
    public void sendOnChannel2(Integer online){
        // Nos llevará al MainActivity una vez demos click en la notificación
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("user_id",user_id);

        String content;

        // Variamos el mensaje según haya sido el cambio
        if (online == 1) {
            content = "Un dispositivo ha recuperado la conexión.";
        } else {
            content = "Un dispositivo ha perdido la conexión.";
        }

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 2, resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        // Contenido de la notificación
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.notif)
                .setContentTitle(content)
                .setContentText("Pulse para ver más")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .build();
        notificationManager.notify(2,notification);
    }

    // SIN IMPLEMENTAR
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Una vez destruido, crearemos una alarma para programar un nuevo arranque del servicio
     */
    @Override
    public void onDestroy() {
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, CheckService.class);

        intent.putExtra("user_id",user_id);

        // Configuramos la alarma (El tiempo está en milisegundos, de ahí la fórmula)
        alarm.set(
                alarm.RTC_WAKEUP,
                System.currentTimeMillis() + (1000 * 60 * frecuencia),
                PendingIntent.getService(this, 0, intent, 0)
        );
    }

    /**
     * Clase que nos permite realizar varias tareas en segundo plano y gestionar su antes, durante
     * y después. En este caso recibe DOS argumentos en un array String: el id del equipo y su IP.
     */
    class mAsyncTaskCheck extends AsyncTask<String, String[], String[]> {

        String[] respuesta =  new String[2]; // Lo que viajará en la TASK del doInBackground() al
                                             // onPostExecute(): el id del equipo y la referencia
                                             // al mensaje enviado.

        protected void onPreExecute() {
        }

        /**
         * Se activa una vez comienza a ejecutarse la tarea. Lo hace en segundo plano. Lanza el
         * mensaje SNMP GET con un OID cualquiera para ver si hay respuesta.
         */
        @Override
        protected String[] doInBackground(String... params) {
            try {
                String ip = params[1]; // Direccion IP del equipo

                // DOS parámetros internos del AsyncTask:
                respuesta[0] = params[0]; // id del equipo

                // Referencia al mensaje enviado
                respuesta[1] = new SNMPRequest().sendSnmpGetNext(".1.3.6.1.2.1.1.6", ip);

            } catch (Exception e) {
                respuesta[1] = "fallo";
            }
            return respuesta;
        }

        /**
         * Se activa una vez finaliza la tarea.
         */
        protected void onPostExecute(String[] result) {
            final Integer id = new Integer(result[0]); // id del equipo

            /**
             * Ajustamos el contenido de la variable "online" y el posible contenido del mensaje
             * según se haya podido recibir respuesta o no.
             *
             * Necesitamos variables auxiliares ya que para ser usadas en el hilo Thread tendrían
             * que ser "final" y por tanto no podríamos modificarlas dentro del if.
             */
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

            // Obtenemos la instancia de la base de datos y los DAOs
            Database database = Database.getDatabase(getApplicationContext());
            final EquipoDao equipoDao = database.equipoDao();
            final LogDao logDao = database.logDao();

            final Integer online = online_aux;
            final String message = message_aux;

            /* Hilo donde gestionamos las operaciones con la base de datos. Aquí accedemos a los
             * datos del equipo y, en su caso, a los logs para añadir uno nuevo.
             */
            new Thread(new Runnable() {
                @Override
                public void run() {
                    EquipoEntity equipo = equipoDao.getEquipo(id);

                    Integer oldOnline = equipo.getOnline();

                    // Si hay un cambio mandamos notificación. Sino no es necesario.
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
