package com.example.clientesnmp;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.Integer32;
import org.w3c.dom.Text;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PruebaSet extends AppCompatActivity {
    private Button sendBtn;
    private TextView tv1;
    private static String  sysContactValue  = "aplicasion";
    private TextView tv2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba_set);
        iniUI();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //try{sendSnmpRequest(OIDVALUE);}catch(Exception e){}
                new PruebaSet.mAsyncTask().execute(".1.3.6.1.2.1.1.5.0");
                //logResult.append("El resultado es:"+respuesta);
            }
        });
    }

    private void iniUI() {
        sendBtn = findViewById(R.id.sendBtn);
        tv1 = findViewById(R.id.result);
        tv2 = findViewById(R.id.escribir);
        //mSpinner = (ProgressBar) findViewById(R.id.progressBar);
    }
    private static String ipAddress = "192.168.0.33";
    private static final String port = "161";

    private static int    snmpVersion  = SnmpConstants.version2c;
    private static String  community  = "public";

    public static void sendSnmpGet(String sysContactOid) throws Exception
    {

        // Create TransportMapping and Listen
        TransportMapping transport = new DefaultUdpTransportMapping();
        transport.listen();

        // Create Target Address object
        CommunityTarget comtarget = new CommunityTarget();
        comtarget.setCommunity(new OctetString(community));
        comtarget.setVersion(snmpVersion);
        comtarget.setAddress(new UdpAddress(ipAddress + "/" + port));
        comtarget.setRetries(2);
        comtarget.setTimeout(1000);

        // Create the PDU object
        PDU pdu = new PDU();

        // Setting the Oid and Value for sysContact variable
        OID oid = new OID(sysContactOid);
        Variable var = new OctetString(sysContactValue);
        VariableBinding varBind = new VariableBinding(oid,var);
        pdu.add(varBind);

        pdu.setType(PDU.SET);
        pdu.setRequestID(new Integer32(1));

        // Create Snmp object for sending data to Agent
        Snmp snmp = new Snmp(transport);

        //System.out.println("\nRequest:\n[ Note: Set Request is sent for sysContact oid in RFC 1213 MIB.");
        //System.out.println("Set operation will change the sysContact value to " + sysContactValue );
        //System.out.println("Once this operation is completed, Querying for sysContact will get the value = " + sysContactValue + " ]");

        //System.out.println("Request:\nSending Snmp Set Request to Agent...");
        ResponseEvent response = snmp.set(pdu, comtarget);

        // Process Agent Response
        if (response != null)
        {
          //  System.out.println("\nResponse:\nGot Snmp Set Response from Agent");
            PDU responsePDU = response.getResponse();

            if (responsePDU != null)
            {
                int errorStatus = responsePDU.getErrorStatus();
                int errorIndex = responsePDU.getErrorIndex();
                String errorStatusText = responsePDU.getErrorStatusText();

                if (errorStatus == PDU.noError)
                {
                    //System.out.println("Snmp Set Response = " + responsePDU.getVariableBindings());
                }
                else
                {
                    //System.out.println("Error: Request Failed");
                    //System.out.println("Error Status = " + errorStatus);
                   // System.out.println("Error Index = " + errorIndex);
                   // System.out.println("Error Status Text = " + errorStatusText);
                }
            }
            else
            {
                //System.out.println("Error: Response PDU is null");
            }
        }
        else
        {
            //System.out.println("Error: Agent Timeout... ");
        }
        snmp.close();
    }
    class mAsyncTask extends AsyncTask<String, Void, Void> {
        protected void onPreExecute() {
            //mSpinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                sysContactValue=tv2.getText().toString();
                sendSnmpGet(params[0]);
                tv1.append("SET realizado con éxito :)");
            } catch (Exception e) {
                //Log.d(TAG,
                //        "Error sending snmp request - Error: " + e.getMessage());
                tv1.append("Error al realizar el SET");
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            //console.setText("");
            //console.append(logResult);
            //mSpinner.setVisibility(View.GONE);
            //i++;
            //if(i<3) {
            //	new mAsyncTask().execute();
            //}
            //escribir();
        }

    }

}
