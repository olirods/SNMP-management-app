package com.clientesnmp.app;

import android.util.Log;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SNMPRequest {

    private static final String TAG = "SNMP CLIENT";
    public static String port = "1161";

    // command to request from Server
    private static final int SNMP_VERSION = SnmpConstants.version2c;
    private static String community = "public";

    private static String  sysContactValue  = "aplicasion";

    public static Snmp snmp;
    public static CommunityTarget comtarget;
    static PDU pdu;
    static OID oid;
    static VariableBinding req;


    public String sendSnmpGetNext(String cmd, String ipAddress) throws Exception {
        String resultado = "";
        // Create TransportMapping and Listen
        TransportMapping<UdpAddress> transport = new DefaultUdpTransportMapping();
        transport.listen();

        Log.d(TAG, "Create Target Address object");
        // Create Target Address object
        CommunityTarget comtarget = new CommunityTarget();
        comtarget.setCommunity(new OctetString(community));
        comtarget.setVersion(SNMP_VERSION);

        Log.d(TAG, "-address: " + ipAddress + "/" + port);

        comtarget.setAddress(new UdpAddress(ipAddress + "/" + port));
        comtarget.setRetries(2);
        comtarget.setTimeout(1000);

        Log.d(TAG, "Prepare PDU");
        // create the PDU
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(cmd)));
        pdu.setType(PDU.GETNEXT);

        Snmp snmp = new Snmp(transport);
        Log.d(TAG, "Sending Request to Agent...");

        // send the PDU
        ResponseEvent response = snmp.send(pdu, comtarget);

        // Process Agent Response
        if (response != null) {
            // extract the response PDU (could be null if timed out)
            PDU responsePDU = response.getResponse();
            // extract the address used by the agent to send the response:
            Address peerAddress = response.getPeerAddress();
            Log.d(TAG, "peerAddress " + peerAddress);
            if (responsePDU != null) {
                int errorStatus = responsePDU.getErrorStatus();
                int errorIndex = responsePDU.getErrorIndex();
                String errorStatusText = responsePDU.getErrorStatusText();

                if (errorStatus == PDU.noError) {
                    resultado = "Snmp Get Response = " + responsePDU.getVariableBindings();
                    Log.d(TAG,
                            "Snmp Get Response = "
                                    + responsePDU.getVariableBindings());
                } else {
                    Log.d(TAG, "Error: Request Failed");
                    Log.d(TAG, "Error Status = " + errorStatus);
                    Log.d(TAG, "Error Index = " + errorIndex);
                    Log.d(TAG, "Error Status Text = " + errorStatusText);
                }
            } else {
                Log.d(TAG, "Error: Response PDU is null");
            }
        } else {
            Log.d(TAG, "Error: Agent Timeout... \n");
        }
        snmp.close();
        return resultado;
    }

    public static void sendSnmpSet(String sysContactOid, String texto, int tipo, String ipAddress) throws Exception
    {

        // Create TransportMapping and Listen
        TransportMapping transport = new DefaultUdpTransportMapping();
        transport.listen();

        // Create Target Address object
        CommunityTarget comtarget = new CommunityTarget();
        comtarget.setCommunity(new OctetString(community));
        comtarget.setVersion(SNMP_VERSION);
        comtarget.setAddress(new UdpAddress(ipAddress + "/" + port));
        comtarget.setRetries(2);
        comtarget.setTimeout(1000);

        // Create the PDU object
        PDU pdu = new PDU();

        // Setting the Oid and Value for sysContact variable
        OID oid = new OID(sysContactOid);
        //Variable var = new OctetString(texto);
        Variable var;
        if (tipo == 1)
            var = new Integer32(Integer.parseInt(texto));
        else
            var = new OctetString(texto);
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
}
