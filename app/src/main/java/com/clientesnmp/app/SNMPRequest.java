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

/**
 * Esta es la clase que gestiona el envío de mensajes SNMP tanto para obtener datos (GET) como para
 * modificarlos (SET) en la base de datos de los dispositivos.
 */
public class SNMPRequest {

    private static final String TAG = "SNMP CLIENT";
    public static String port = "1161"; // Puerto de envío de mensajes
    private static final int SNMP_VERSION = SnmpConstants.version2c; // Versión SNMP utilizada
    private static String community = "public"; // Community utilizada

    /**
     * Se utiliza para enviar un GET NEXT al dispositivo con ipAddress por SNMP, solicitando el
     * objeto SNMP con el correspondiente OID.
     *
     * @param oid       OID solicitado
     * @param ipAddress dirección IP del equipo
     * @return la String con el resultado
     * @throws Exception
     */
    public String sendSnmpGetNext(String oid, String ipAddress) throws Exception {
        String resultado = "";

        // Creamos socket
        TransportMapping<UdpAddress> transport = new DefaultUdpTransportMapping();
        transport.listen();

        Log.d(TAG, "Create Target Address object");

        // Hacemos los ajustes de SNMP
        CommunityTarget comtarget = new CommunityTarget();
        comtarget.setCommunity(new OctetString(community));
        comtarget.setVersion(SNMP_VERSION);

        Log.d(TAG, "-address: " + ipAddress + "/" + port);

        comtarget.setAddress(new UdpAddress(ipAddress + "/" + port));
        comtarget.setRetries(2);
        comtarget.setTimeout(1000);

        Log.d(TAG, "Prepare PDU");

        // Creamos el mensaje a enviar
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(oid)));
        pdu.setType(PDU.GETNEXT);

        Snmp snmp = new Snmp(transport);
        Log.d(TAG, "Sending Request to Agent...");

        // Enviamos el mensaje. Espera hasta que recibe respuesta
        ResponseEvent response = snmp.send(pdu, comtarget);

        // Gestión de la respuesta. Esto se ejecutará cuando haya recibido respuesta (o no)
        if (response != null) {

            // Extramos el mensaje de respuesta (puede ser null si ha sido un TIME OUT)
            PDU responsePDU = response.getResponse();

            // Extraemos la dirección usada por el dispositivo para responder
            Address peerAddress = response.getPeerAddress();
            Log.d(TAG, "peerAddress " + peerAddress);

            if (responsePDU != null) {
                // Gestionamos posibles errores
                int errorStatus = responsePDU.getErrorStatus();
                int errorIndex = responsePDU.getErrorIndex();
                String errorStatusText = responsePDU.getErrorStatusText();

                if (errorStatus == PDU.noError) {
                    // Obtenemos contenido del mensaje
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

    /**
     * Se utiliza para enviar un GET NEXT al dispositivo con ipAddress por SNMP, solicitando la
     * modificación del contenido del objeto SNMP con el correspondiente OID.
     *
     * @param oid OID del objeto a modificar
     * @param texto         nuevo contenido
     * @param tipo          si es Integer(1) o no
     * @param ipAddress     dirección IP del dispositivo
     * @throws Exception
     */
    public static void sendSnmpSet(String oid, String texto, int tipo, String ipAddress) throws Exception
    {

        // Creamos socket
        TransportMapping transport = new DefaultUdpTransportMapping();
        transport.listen();

        // Hacemos los ajustes de SNMP
        CommunityTarget comtarget = new CommunityTarget();
        comtarget.setCommunity(new OctetString(community));
        comtarget.setVersion(SNMP_VERSION);
        comtarget.setAddress(new UdpAddress(ipAddress + "/" + port));
        comtarget.setRetries(2);
        comtarget.setTimeout(1000);

        // Creamos el mensaje a enviar
        PDU pdu = new PDU();

        // Si es un Integer o no
        Variable var;
        if (tipo == 1)
            var = new Integer32(Integer.parseInt(texto));
        else
            var = new OctetString(texto);
        VariableBinding varBind = new VariableBinding(new OID(oid),var);
        pdu.add(varBind);
        pdu.setType(PDU.SET);
        pdu.setRequestID(new Integer32(1));

        Snmp snmp = new Snmp(transport);

        // Enviamos el mensaje. Espera hasta que recibe respuesta
        ResponseEvent response = snmp.set(pdu, comtarget);

        // Gestión de la respuesta. Esto se ejecutará cuando haya recibido respuesta (o no)
        if (response != null)
        {
            PDU responsePDU = response.getResponse();

            if (responsePDU != null)
            {
                // Gestionamos posibles errores
                int errorStatus = responsePDU.getErrorStatus();
                int errorIndex = responsePDU.getErrorIndex();
                String errorStatusText = responsePDU.getErrorStatusText();

                if (errorStatus == PDU.noError)
                {
                    // Si llega hasta aquí, debe haber ido bien
                    Log.d(TAG,
                            "Snmp Set Response = "
                                    + responsePDU.getVariableBindings());
                }
                else
                {
                    Log.d(TAG, "Error: Request Failed");
                    Log.d(TAG, "Error Status = " + errorStatus);
                    Log.d(TAG, "Error Index = " + errorIndex);
                    Log.d(TAG, "Error Status Text = " + errorStatusText);
                }
            }
            else
            {
                Log.d(TAG, "Error: Response PDU is null");
            }
        }
        else
        {
            Log.d(TAG, "Error: Agent Timeout... \n");
        }
        snmp.close();
    }
}
