package com.example.clientesnmp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class DashboardFragment extends Fragment {

    // UI
    private TextView nameText;
    private TextView descText;
    private TextView locText;
    private TextView contactText;
    private Button añadirButton;
    private int user_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (!getArguments().isEmpty()) {
            user_id = getArguments().getInt("user_id");
        }

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout);

        Database database = Database.getDatabase(getActivity().getApplicationContext());

        añadirButton = view.findViewById(R.id.button);

        añadirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), NewEquipoActivity.class);
                i.putExtra("user_id",user_id);
                startActivity(i);
            }
        });

        final EquipoDao equipoDao = database.equipoDao();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=1; i <= 3; i++) {
                    final TextView title = new TextView(getActivity().getApplicationContext());
                    String grupo_aux = "Desconocido";
                    switch(i) {
                        case 1: grupo_aux = "Servidores";
                                break;
                        case 2: grupo_aux = "Switches";
                            break;
                        case 3: grupo_aux = "Firewalls";
                            break;
                    }

                    final String grupo = grupo_aux;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            title.setText(grupo);
                            layout.addView(title);
                        }
                    });

                    final List<EquipoEntity> equipos = equipoDao.getEquiposFromUserAndGroup(new Integer(user_id),
                            i);

                    if (equipos.isEmpty()) {
                        final TextView emptyText = new TextView(getActivity().getApplicationContext());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                emptyText.setText("No hay dispositivos de este grupo que mostrar");
                                layout.addView(emptyText);
                            }
                        });
                    } else {
                        final TableLayout tlayout = new TableLayout(getActivity().getApplicationContext());

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                layout.addView(tlayout);
                            }
                        });


                        for (int z = 0; z < equipos.size(); z+=2) {
                            final TableRow trow = new TableRow(getActivity().getApplicationContext());

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tlayout.addView(trow);
                                }
                            });

                            for (int j = 0; j < equipos.size(); j++) {
                                final CardView card = new CardView(getActivity().getApplicationContext());
                                final TextView ipText = new TextView(getActivity().getApplicationContext());
                                final TextView nombreText = new TextView(getActivity().getApplicationContext());
                                final TextView versionText = new TextView(getActivity().getApplicationContext());
                                final TextView onlineText = new TextView(getActivity().getApplicationContext());

                                final String ipEquipo = equipos.get(j).getIP();
                                final String nombreEquipo = equipos.get(j).getNombre_e();
                                final Integer versionEquipo = equipos.get(j).getV_snmp();

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ipText.setText(ipEquipo);
                                        nombreText.setText(nombreEquipo);
                                        versionText.setText(versionEquipo);
                                        onlineText.setText("ONLINE: NO SÉ");

                                        card.addView(ipText);
                                        card.addView(nombreText);
                                        card.addView(versionText);
                                        card.addView(onlineText);

                                        trow.addView(card);
                                    }
                                });
                            }
                        }

                    }
                }

            }
        }).start();



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
