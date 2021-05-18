package com.example.clientesnmp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;



public class DashboardFragment extends Fragment {

    // UI
    private TextView nameText;
    private TextView descText;
    private TextView locText;
    private TextView contactText;
    private TableLayout tableLayout;
    private int user_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        user_id = getArguments().getInt("user_id");

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TableLayout tableLayout = (TableLayout) view.findViewById(R.id.table);

        Database database = Database.getDatabase(getActivity().getApplicationContext());
        final EquipoDao equipoDao = database.equipoDao();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<EquipoEntity> equipos = equipoDao.getEquiposFromUser(new Integer(user_id));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        });



        Log.d("SERGIO",
                "Pasamos");

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
    class mAsyncTaskGet extends AsyncTask<String, String[], String[]> {

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
