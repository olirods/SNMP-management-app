package com.example.clientesnmp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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


public class InicioFragment extends Fragment {

    // UI
    private TextView nameText;
    private TextView descText;
    private TextView locText;
    private TextView contactText;
    private Button botonUDP;
    private Button botonIP;
    private Button botonRoute;
    private Button botonTcp;
    private Button botonIf;
    private Button botonSTP;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inicio, container, false);
        botonUDP = view.findViewById(R.id.buttonUDP);
        botonIP = view.findViewById(R.id.buttonIP);
        botonRoute = view.findViewById(R.id.buttonRoute);
        botonTcp = view.findViewById(R.id.buttonTcp);
        botonIf = view.findViewById(R.id.buttonIf);
        botonSTP = view.findViewById(R.id.buttonSTP);

        botonUDP.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //try{sendSnmpRequest(OIDVALUE);}catch(Exception e){}
                Intent intent = new Intent(getActivity(), Activity_tabla_udp.class);
                //intent.putExtra("com.example1.clientesnmp.MESSAGE", ".1.3.6.1.2.1.7.5");
                startActivity(intent);
                //       logResult.append("El resultado es:"+);
            }
        });
        botonIP.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //try{sendSnmpRequest(OIDVALUE);}catch(Exception e){}
                Intent intent = new Intent(getActivity(), Activity_tabla_ip.class);
                //intent.putExtra("com.example1.clientesnmp.MESSAGE", ".1.3.6.1.2.1.7.5");
                startActivity(intent);
                //       logResult.append("El resultado es:"+);
            }
        });
        botonRoute.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //try{sendSnmpRequest(OIDVALUE);}catch(Exception e){}
                Intent intent = new Intent(getActivity(), Activity_tabla_route.class);
                //intent.putExtra("com.example1.clientesnmp.MESSAGE", ".1.3.6.1.2.1.7.5");
                startActivity(intent);
                //       logResult.append("El resultado es:"+);
            }
        });
        botonTcp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //try{sendSnmpRequest(OIDVALUE);}catch(Exception e){}
                Intent intent = new Intent(getActivity(), Activity_tabla_tcp.class);
                //intent.putExtra("com.example1.clientesnmp.MESSAGE", ".1.3.6.1.2.1.7.5");
                startActivity(intent);
                //       logResult.append("El resultado es:"+);
            }
        });
        botonIf.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //try{sendSnmpRequest(OIDVALUE);}catch(Exception e){}
                Intent intent = new Intent(getActivity(), Activity_tabla_if.class);
                //intent.putExtra("com.example1.clientesnmp.MESSAGE", ".1.3.6.1.2.1.7.5");
                startActivity(intent);
                //       logResult.append("El resultado es:"+);
            }
        });

        botonSTP.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //try{sendSnmpRequest(OIDVALUE);}catch(Exception e){}
                Intent intent = new Intent(getActivity(), Activity_tabla_stp.class);
                //intent.putExtra("com.example1.clientesnmp.MESSAGE", ".1.3.6.1.2.1.7.5");
                startActivity(intent);
                //       logResult.append("El resultado es:"+);
            }
        });
        nameText = view.findViewById(R.id.nameText);
        descText = view.findViewById(R.id.descText);
        locText = view.findViewById(R.id.locText);
        contactText = view.findViewById(R.id.contactText);

        new mAsyncTask().execute(".1.3.6.1.2.1.1.5", "name");
        new mAsyncTask().execute(".1.3.6.1.2.1.1.1", "desc");
        new mAsyncTask().execute(".1.3.6.1.2.1.1.6", "loc");
        new mAsyncTask().execute(".1.3.6.1.2.1.1.4", "contact");

        return view;
    }




    private void escribir(String[] result) {

        String[] parts = result[1].split("=");
        String part1 = parts[2]; // 123
        String extraerp = part1.substring(0, 1); // Extraigo laprimera letra
        String extraeru = part1.substring(part1.length() - 1); //Extraigo la ultima letra letra
        String remplazado = part1.replace(extraerp, ""); // quitamos el primer caracter
        String remplazadofinal = remplazado.replace(extraeru, "");// se quita el ultimo caracter

        switch(result[0]) {
            case "name" : nameText.append(remplazadofinal);
                break;
            case "desc" : descText.append(remplazadofinal);
                break;
            case "loc" : locText.append(remplazadofinal);
                break;
            case "contact" : contactText.append(remplazadofinal);
                break;
        }

    }

    // AsyncTask to do job in background
    //AsyncTask<Void, Void, Void> mAsyncTask = new AsyncTask<Void, Void, Void>() {
    class mAsyncTask extends AsyncTask<String, String[], String[]> {

        String[] respuesta =  new String[2];

        protected void onPreExecute() {
            //mSpinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {
            try {
                respuesta[0] = params[1];
                respuesta[1] = new SNMPRequest().sendSnmpGetNext(params[0]);

            } catch (Exception e) {
                //  Log.d(TAG,
                //         "Error sending snmp request - Error: " + e.getMessage());
                // tv1.setText(e.getMessage());
                respuesta[1] = "fallo";
            }
            return respuesta;
        }

        protected void onPostExecute(String[] result) {
            // console.setText("");
            // console.append(logResult);
            //mSpinner.setVisibility(View.GONE);
            //i++;
            //if(i<3) {
            //	new mAsyncTask().execute();
            //}
            if (result[1] != "fallo" && result[1] != "" && result[1] != null) {
                escribir(result);
            }
        }
    }
}
