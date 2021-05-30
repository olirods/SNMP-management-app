package com.clientesnmp.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.clientesnmp.app.services.CheckService;
import com.clientesnmp.app.services.TrapService;


public class SettingsFragment extends Fragment {


    // UI
    private EditText getPortEditText;
    private EditText trapPortEditText;
    private EditText checkEditText;
    private Button cambiarButton;

    private int user_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        if (!getArguments().isEmpty()) {
            user_id = getArguments().getInt("user_id");
        }

        getPortEditText = view.findViewById(R.id.editText2);
        trapPortEditText = view.findViewById(R.id.editText3);
        checkEditText = view.findViewById(R.id.checkEditText);
        cambiarButton = view.findViewById(R.id.cambiar);

        cambiarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CambiarPassActivity.class);
                i.putExtra("user_id",user_id);
                startActivity(i);
            }
        });

        getPortEditText.setText(SNMPRequest.port);
        trapPortEditText.setText(TrapService.puertoTrap);
        checkEditText.setText(CheckService.frecuencia.toString());

        getPortEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                SNMPRequest.port = editable.toString();
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
