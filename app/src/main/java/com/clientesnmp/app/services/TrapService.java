package com.clientesnmp.app.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.KeyEvent;

import com.clientesnmp.app.MainActivity;
import com.clientesnmp.app.R;
import com.clientesnmp.app.database.Database;
import com.clientesnmp.app.database.EquipoDao;
import com.clientesnmp.app.database.EquipoEntity;
import com.clientesnmp.app.database.LogDao;
import com.clientesnmp.app.database.LogEntity;

import java.util.*;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.CommunityTarget;
import org.snmp4j.MessageDispatcher;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.MessageException;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.log.LogFactory;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.StateReference;
import org.snmp4j.mp.StatusInformation;
import org.snmp4j.security.Priv3DES;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TransportIpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.tools.console.SnmpRequest;
import org.snmp4j.transport.AbstractTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;

import java.io.IOException;

/**
 * Servicio de escucha de TRAPS SNMP. Este servicio, que empieza a correr en segundo plano desde que
 * se inicia la aplicación, tiene la función de escuchar por el puerto correspondiente todos los
 * mensajes TRAPS que le lleguen, guardar su contenido en la base de datos como logs y avisar al
 * usuario mediante una notificación.
 */
public class TrapService extends Service implements CommandResponder {
    private int user_id; // Identificador del usuario logueado

    private AbstractTransportMapping transport;

    private AsyncTask task; // Instancia del task que gestionará la escucha
    private NotificationManagerCompat notificationManager;

    private static final String CHANNEL_1_ID = "channel1";

    /**
     * Puerto de recepción de traps. Modificable por el usuario a través del SettingsFragment
     */
    public static String puertoTrap = "1162";

    /**
     * Configuramos el canal por el que enviaremos las notificaciones al teléfono
     */
    private void createNotificationsChannels(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("Canal que informa sobre traps recibidos");

            NotificationManager manager = this.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }

    }

    /**
     * Instancia un nuevo TrapService
     */
    public TrapService() {
    }

    /**
     * Se ejecutará una vez al crear el servicio.
     */
    public void onCreate() {
        // Habilitamos la gestión de notificaciones en este servicio
        createNotificationsChannels();
        notificationManager=NotificationManagerCompat.from(this);

        /* Ponemos en marcha el task
         * (La opción THREAD_POOL_EXECUTOR nos permite que resuelva varios tasks en paralelo,
         * de lo contrario, tendría que esperar a que terminase uno para resolver otro)
         */
        task = new TrapService.mAsyncTaskTrap().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Se ejecutará una vez se arranque el servicio
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Obtenemos de la MainActivity el identificador del usuario logueado
        user_id = intent.getIntExtra("user_id", 0);

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Hacemos la configuración para empezar a escuchar TRAPS en la dirección IP indicada
     *
     * @param address dirección IP donde se escucha (será la local)
     * @throws IOException posible excepción
     */
    public synchronized void listen(TransportIpAddress address) throws IOException
    {
        // Creamos el socket UDP de escucha
        transport = new DefaultUdpTransportMapping((UdpAddress) address);

        ThreadPool threadPool = ThreadPool.create("DispatcherPool", 10);
        MessageDispatcher mtDispatcher = new MultiThreadedMessageDispatcher(threadPool,
                new MessageDispatcherImpl());

        mtDispatcher.addMessageProcessingModel(new MPv1());
        mtDispatcher.addMessageProcessingModel(new MPv2c());

        // Incorporando protocolos de seguridad
        SecurityProtocols.getInstance().addDefaultProtocols();
        SecurityProtocols.getInstance().addPrivacyProtocol(new Priv3DES());

        // Creamos el target para los traps, configurando que escuche de la comunidad "public"
        CommunityTarget target = new CommunityTarget();
        target.setCommunity( new OctetString("public"));

        // Hacemos uso de la librería SNMP4j para ajustes de la escucha
        Snmp snmp = new Snmp(mtDispatcher, transport);
        snmp.addCommandResponder(this);

        transport.listen(); // ESCUCHANDO

        try
        {
            this.wait();
        }
        catch (InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Este método será llamado cada vez que un TRAP se reciba. En él procesamos el mensaje recibido
     */
    public synchronized void processPdu(CommandResponderEvent cmdRespEvent)
    {
        PDU pdu = cmdRespEvent.getPDU(); // Obtenemos el mensaje recibido

        if (pdu != null)
        {
            // Verificamos que el TRAP es correcto y no hay problemas
            int pduType = pdu.getType();
            if ((pduType != PDU.TRAP) && (pduType != PDU.V1TRAP) && (pduType != PDU.REPORT)
                    && (pduType != PDU.RESPONSE))
            {
                pdu.setErrorIndex(0);
                pdu.setErrorStatus(0);
                pdu.setType(PDU.RESPONSE);
                StatusInformation statusInformation = new StatusInformation();
                StateReference ref = cmdRespEvent.getStateReference();
                try
                {
                    cmdRespEvent.getMessageDispatcher().returnResponsePdu(cmdRespEvent.getMessageProcessingModel(),
                            cmdRespEvent.getSecurityModel(), cmdRespEvent.getSecurityName(), cmdRespEvent.getSecurityLevel(),
                            pdu, cmdRespEvent.getMaxSizeResponsePDU(), ref, statusInformation);
                }
                catch (MessageException ex)
                {
                    LogFactory.getLogger(SnmpRequest.class).error(ex);
                }
            }

            // Obtenemos la IP origen del TRAP
            String[] ipPort = cmdRespEvent.getPeerAddress().toString().split("/");

            final String ip = ipPort[0];
            final String port = ipPort[1];

            // Obtenemos la instancia de la base de datos y los DAOs
            Database database = Database.getDatabase(getApplicationContext());
            final LogDao logDao = database.logDao();
            final EquipoDao equipoDao = database.equipoDao();

            final String trap = pdu.toString(); // Contenido del mensaje TRAP

            /* Hilo donde gestionamos las operaciones con la base de datos. Aquí obtenemos el equipo
             * del que proviene el TRAP y creamos el mensaje de log correspondiente.
             */
            new Thread(new Runnable() {
                @Override
                public void run() {
                    EquipoEntity equipo = equipoDao.getEquipo(ip);

                    String message = "El equipo " + equipo.getNombre_e() + " con IP " +
                            equipo.getIP() + " ha enviado el siguiente trap: " + trap;

                    LogEntity logEntity = new LogEntity();
                    logEntity.setId_u(user_id);
                    logEntity.setCreateDate(Calendar.getInstance().getTime());
                    logEntity.setMessage(message);

                    logDao.insertLog(logEntity);
                    }
            }).start();

            sendOnChannel1(); // Mandamos la notificación al teléfono
        }
    }

    /**
     * Enviar la notificación al teléfono del usuario, esté en la app o no.
     */
    public void sendOnChannel1(){
        // Nos llevará al MainActivity una vez demos click en la notificación
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("user_id",user_id);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 1, resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        // Contenido de la notificación
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.notif)
                .setContentTitle("Se ha recibido un nuevo TRAP")
                .setContentText("Pulse para ver más")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .build();
        notificationManager.notify(1,notification);
    }

    /**
     * Clase que nos permite realizar varias tareas en segundo plano y gestionar su antes, durante
     * y después. En este caso simplemente sirve para lanzar el método "listen", que escuchará
     * los TRAPS.
     */
    class mAsyncTaskTrap extends AsyncTask<Void, Void, Void> {
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                // Ponemos 0.0.0.0 para que sea la dirección local. Públicamente será la dirección
                // del dispositivo en la red
                listen(new UdpAddress("0.0.0.0/" + puertoTrap));
            }
            catch (IOException e)
            {
            }

            return null;
        }

        protected void onPostExecute(Void result) {
        }


    }

    // SIN MODIFICAR
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(task != null && task.getStatus() != AsyncTask.Status.FINISHED){
                try{
                    transport.close();

                }
                catch(IOException e){
                    ;
                }
                //Thread.currentThread().interrupt();
                task.cancel(true);
                //this.finish();
            }
        }
        return onKeyDown(keyCode, event);
    }

    // SIN IMPLEMENTAR
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
