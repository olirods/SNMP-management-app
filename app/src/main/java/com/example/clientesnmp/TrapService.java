package com.example.clientesnmp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.app.ProgressDialog;

import android.widget.Button;
import android.widget.TextView;
import java.util.*;
import java.lang.Object;

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

public class TrapService extends Service implements CommandResponder {
    private AsyncTask task;
    private ProgressDialog progress;
    private StringBuffer logResult = new StringBuffer();

    public static String puertoTrap = "1162";

    public TrapService() {
    }

    public void onCreate() {
        createNotificationsChannels();
        notificationManager=NotificationManagerCompat.from(this);
        task = new TrapService.mAsyncTaskTrap().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void sendOnChannel1(){
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 1, resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.notif)
                .setContentTitle("Se ha recibido un nuevo trap")
                .setContentText("Pulse para verlo")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .build();
        notificationManager.notify(1,notification);
    }
    private NotificationManagerCompat notificationManager;
    private static final String CHANNEL_1_ID = "channel1";
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
    private AbstractTransportMapping transport;
    public synchronized void listen(TransportIpAddress address) throws IOException
    {
        //AbstractTransportMapping transport;
        transport = new DefaultUdpTransportMapping((UdpAddress) address);

        logResult.append("Se hace el udp");
        ThreadPool threadPool = ThreadPool.create("DispatcherPool", 10);
        MessageDispatcher mtDispatcher = new MultiThreadedMessageDispatcher(threadPool, new MessageDispatcherImpl());

        // add message processing models
        mtDispatcher.addMessageProcessingModel(new MPv1());
        mtDispatcher.addMessageProcessingModel(new MPv2c());

        // add all security protocols
        SecurityProtocols.getInstance().addDefaultProtocols();
        SecurityProtocols.getInstance().addPrivacyProtocol(new Priv3DES());

        //Create Target
        CommunityTarget target = new CommunityTarget();
        target.setCommunity( new OctetString("public"));

        Snmp snmp = new Snmp(mtDispatcher, transport);
        snmp.addCommandResponder(this);

        transport.listen();
        // System.out.println("Listening on " + address);
        logResult.append("Se llega a escuchar");
        try
        {
            this.wait();
        }
        catch (InterruptedException ex)
        {
            logResult.append("er1"+ex);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * This method will be called whenever a pdu is received on the given port specified in the listen() method
     */
    public synchronized void processPdu(CommandResponderEvent cmdRespEvent)
    {
        logResult.append("Se entra en sincronizado");
        String respuesta ="";
        //System.out.println("Received PDU...");
        PDU pdu = cmdRespEvent.getPDU();
        if (pdu != null)
        {

            //System.out.println("Trap Type = " + pdu.getType());
            //System.out.println("Variable Bindings = " + pdu.getVariableBindings());
            //respuesta=""+pdu.getType();
            //notificacion();



            sendOnChannel1();
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
                    //System.out.println(cmdRespEvent.getPDU());
                    cmdRespEvent.getMessageDispatcher().returnResponsePdu(cmdRespEvent.getMessageProcessingModel(),
                            cmdRespEvent.getSecurityModel(), cmdRespEvent.getSecurityName(), cmdRespEvent.getSecurityLevel(),
                            pdu, cmdRespEvent.getMaxSizeResponsePDU(), ref, statusInformation);
                }
                catch (MessageException ex)
                {
                    //System.err.println("Error while sending response: " + ex.getMessage());
                    LogFactory.getLogger(SnmpRequest.class).error(ex);
                }
            }
        }
    }






    /*public void notificacion(){
        NotificationCompat.Builder builder=
                new NotificationCompat.Builder(this,"3")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                            R.mipmap.ic_launcher))
                .setContentTitle("TRAP");
        notificacionq.notify(1,builder.build());

    }*/





    class mAsyncTaskTrap extends AsyncTask<Void, Void, Void> {
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {

            String resp="";

            try
            {

                listen(new UdpAddress("0.0.0.0/" + puertoTrap));
                // snmp4jTrapReceiver.listen(new UdpAddress("localhost/1162"));
            }
            catch (IOException e)
            {
                logResult.append("Error"+e);
                //System.err.println("Error in Listening for Trap");
                //System.err.println("Exception Message = " + e.getMessage());
            }
            //  socket();
            return null;
        }

        protected void onPostExecute(Void result) {
;

        }


    }
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

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
