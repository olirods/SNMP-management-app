package com.example.clientesnmp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class DashboardFragment extends Fragment {

    // UI
    private TextView descText;
    private TextView locText;
    private TextView contactText;
    private Button añadirButton;
    private List<CardView> cardsArray;
    private List<String> ipsArray;

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

        cardsArray = new ArrayList();
        ipsArray = new ArrayList();


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
                Integer arrayIndex = 0;
                for (int i=1; i <= 3; i++) {
                    final Space separacion = new Space(getActivity().getApplicationContext());
                    separacion.setMinimumHeight(50);

                    final TextView title = new TextView(getActivity().getApplicationContext());
                    title.setAllCaps(true);
                    title.setTextSize(30);

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
                            layout.addView(separacion);
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
                        final LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                        final TableLayout tlayout = new TableLayout(getActivity().getApplicationContext());
                        tlayout.setGravity(Gravity.CENTER);

                        final TableLayout.LayoutParams lp = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT);


                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tlayout.setLayoutParams(ll);
                                layout.addView(tlayout);
                            }
                        });


                        for (int z = 0; z < equipos.size(); z+=2) {
                            final TableRow trow = new TableRow(getActivity().getApplicationContext());
                            trow.setGravity(Gravity.CENTER);

                            final TableRow.LayoutParams lpb = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);


                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    trow.setLayoutParams(lp);
                                    tlayout.addView(trow);
                                }
                            });

                            for (int j = z; j < z+2 && j < equipos.size(); j++) {
                                final CardView card = new CardView(getActivity().getApplicationContext());
                                cardsArray.add(card);
                                final TextView ipText = new TextView(getActivity().getApplicationContext());
                                final TextView nombreVersionText = new TextView(getActivity().getApplicationContext());
                                //final TextView onlineText = new TextView(getActivity().getApplicationContext());

                                final LinearLayout cardLayout = new LinearLayout(getActivity().getApplicationContext());
                                cardLayout.setOrientation(LinearLayout.VERTICAL);
                                card.setContentPadding(10,10,10,10);
                                card.setUseCompatPadding(true);
                                card.setMaxCardElevation(10);
                                card.addView(cardLayout);

                                final String ipEquipo = equipos.get(j).getIP();
                                ipsArray.add(ipEquipo);
                                final String nombreEquipo = equipos.get(j).getNombre_e();
                                final Integer versionEquipo = equipos.get(j).getV_snmp();

                                new mAsyncTaskGet().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ".1.3.6.1.2.1.1.6", "online", arrayIndex.toString());

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ipText.setText(ipEquipo);
                                        nombreVersionText.setText(nombreEquipo + "(V" + versionEquipo.toString() + ")");
                                        nombreVersionText.setTypeface(null, Typeface.BOLD_ITALIC);
                                        //onlineText.setText("ONLINE: NO SÉ");

                                        cardLayout.addView(ipText);
                                        cardLayout.addView(nombreVersionText);
                                        //cardLayout.addView(onlineText);

                                        card.setLayoutParams(lpb);

                                        trow.addView(card);
                                    }
                                });

                                arrayIndex++;

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

        Integer arrayIndex = Integer.valueOf(result[2]);
        CardView card = cardsArray.get(arrayIndex);
        LinearLayout cardLayout = (LinearLayout) card.getChildAt(0);

        switch(result[0]) {
            case "name" : {

                final TextView nameText = new TextView(getActivity().getApplicationContext());
                nameText.setText("sysName: " + remplazadofinal);

                cardLayout.addView(nameText);
            }
                break;
            case "desc" : {

                final TextView nameText = new TextView(getActivity().getApplicationContext());
                nameText.setText("sysDescr: " + remplazadofinal);

                cardLayout.addView(nameText);
            }
                break;
            case "loc" : {

                final TextView nameText = new TextView(getActivity().getApplicationContext());
                nameText.setText("sysLocation: " + remplazadofinal);

                cardLayout.addView(nameText);
            }
                break;
            case "contact" : {

                final TextView nameText = new TextView(getActivity().getApplicationContext());
                nameText.setText("sysContact: " + remplazadofinal);

                cardLayout.addView(nameText);
            }
                break;
        }

    }

    // AsyncTask to do job in background
    //AsyncTask<Void, Void, Void> mAsyncTask = new AsyncTask<Void, Void, Void>() {
    class mAsyncTaskGet extends AsyncTask<String, String[], String[]> {

        String[] respuesta =  new String[3];

        protected void onPreExecute() {
            //mSpinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {
            try {
                respuesta[0] = params[1];

                Integer arrayIndex = Integer.valueOf(params[2]);

                respuesta[1] = new SNMPRequest().sendSnmpGetNext(params[0], ipsArray.get(arrayIndex));
                respuesta[2] = params[2];

            } catch (Exception e) {
                //  Log.d(TAG,
                //         "Error sending snmp request - Error: " + e.getMessage());
                // tv1.setText(e.getMessage());
                respuesta[1] = "fallo";
            }
            return respuesta;
        }

        protected void onPostExecute(String[] result) {
            Integer arrayIndex = Integer.valueOf(result[2]);

            if (result[1] != "fallo" && result[1] != "" && result[1] != null) {
                if (result[0] == "online") {
                    CardView card = cardsArray.get(arrayIndex);
                    LinearLayout cardLayout = (LinearLayout) card.getChildAt(0);

                    final TextView nameText = new TextView(getActivity().getApplicationContext());
                    nameText.setText("ONLINE");
                    nameText.setTextColor(Color.GREEN);

                    cardLayout.addView(nameText);

                    new mAsyncTaskGet().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ".1.3.6.1.2.1.1.5", "name", arrayIndex.toString());
                    new mAsyncTaskGet().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ".1.3.6.1.2.1.1.6", "loc", arrayIndex.toString());
                    new mAsyncTaskGet().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ".1.3.6.1.2.1.1.4", "contact", arrayIndex.toString());
                } else
                    escribir(result);
            } else {
                if (result[0] == "online") {

                    CardView card = cardsArray.get(arrayIndex);
                    LinearLayout cardLayout = (LinearLayout) card.getChildAt(0);

                    final TextView nameText = new TextView(getActivity().getApplicationContext());
                    nameText.setText("OFFLINE");
                    nameText.setTextColor(Color.RED);

                    cardLayout.addView(nameText);
                }

            }
        }
    }
}
