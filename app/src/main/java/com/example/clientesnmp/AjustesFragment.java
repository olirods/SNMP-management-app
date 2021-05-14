package com.example.clientesnmp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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


public class AjustesFragment extends Fragment {


    // UI
    private EditText ipEditText;
    private EditText trapPortEditText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ajustes, container, false);

        ipEditText = view.findViewById(R.id.editText2);
        trapPortEditText = view.findViewById(R.id.editText3);

        ipEditText.setText(SNMPRequest.ipAddress);
        trapPortEditText.setText(TrapService.puertoTrap);

        ipEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                SNMPRequest.ipAddress = editable.toString();

            }
        });

        trapPortEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                TrapService.puertoTrap = editable.toString();

            }
        });


        return view;
    }


    // AsyncTask to do job in background
    //AsyncTask<Void, Void, Void> mAsyncTask = new AsyncTask<Void, Void, Void>() {
    /*class mAsyncTask extends AsyncTask<String, String[], String[]> {

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

        }
    }*/
}
