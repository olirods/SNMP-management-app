package com.clientesnmp.app;

import android.content.Intent;
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

import com.clientesnmp.app.database.Database;
import com.clientesnmp.app.database.EquipoDao;
import com.clientesnmp.app.database.EquipoEntity;

import java.util.ArrayList;
import java.util.List;


/**
 * Fragment del Dashboard. Es el que se inicia al crearse la MainActivity. Aquí aparecerán todos los
 * equipos existentes en la base de datos para el usuario logueado, organizado por grupos (Servidores,
 * Switches y Firewalls) y con información de su estado (ONLINE u OFFLINE) junto con algunos
 * parámetros de información obtenidos vía SNMP (sysLocation, sysName, sysContact).
 *
 * El funcionamiento es el siguiente: obtiene todos los equipos de la base de datos. Por cada grupo,
 * crea una tabla con sus columnas y filas, y va metiendo una "Card" de cada equipo. En esa "Card",
 * primero comprueba si está online mandándole un mensaje SNMP, y si es así, le manda otros tres
 * más pidiéndole el sysLocation, sysName y sysContact.
 */
public class DashboardFragment extends Fragment {
    private int user_id; // Identificador del usuario logueado

    // UI
    private Button añadirButton;
    private List<CardView> cardsArray;
    private List<String> ipsArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Obtenemos del MainActivity el identificador del usuario logueado
        if (!getArguments().isEmpty()) {
            user_id = getArguments().getInt("user_id");
        }

        // Asociación de UI con el layout XML
        final LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout);
        añadirButton = view.findViewById(R.id.button);

        cardsArray = new ArrayList(); // Lista que contiene todos los layouts de dispositivos
        ipsArray = new ArrayList(); // Lista que contiene todas las IPs de dispositivos

        // Botón que nos sirve para añadir un nuevo equipo a la base de datos en la Activity correspondiente
        añadirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), NewEquipoActivity.class);
                i.putExtra("user_id",user_id);
                startActivity(i);
            }
        });

        // Obtenemos la instancia de la base de datos y el DAO
        Database database = Database.getDatabase(getActivity().getApplicationContext());
        final EquipoDao equipoDao = database.equipoDao();

        /* Hilo donde gestionamos las operaciones con la base de datos. Aquí accedemos a ella para
         * obtener la lista completa de equipos e ir colocándolos junto con su información en la UI.
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                Integer arrayIndex = 0; // índice de los equipos introducidos

                // Para cada uno de los tres grupos: título y tabla de dispositivos
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

                    /* Necesitamos este método para realizar acciones en la UI dentro de los Threads
                     * de acceso a la base de datos.
                     */
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            title.setText(grupo);
                            layout.addView(separacion);
                            layout.addView(title); // Añadimos el título del grupo
                        }
                    });

                    // Equipos existentes para ese grupo y ese usuario logueado
                    final List<EquipoEntity> equipos = equipoDao.getEquipos(new Integer(user_id),
                            i);

                    if (equipos.isEmpty()) {
                        // En el caso de que no haya ningún equipo
                        final TextView emptyText = new TextView(getActivity().getApplicationContext());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                emptyText.setText("No hay dispositivos de este grupo que mostrar");
                                layout.addView(emptyText);
                            }
                        });
                    } else {

                        // Ajustes de UI
                        final LinearLayout.LayoutParams ll =
                                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT);

                        final TableLayout tlayout = new TableLayout(getActivity().getApplicationContext());
                        tlayout.setGravity(Gravity.CENTER);

                        final TableLayout.LayoutParams lp =
                                new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                        TableLayout.LayoutParams.MATCH_PARENT);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tlayout.setLayoutParams(ll);
                                layout.addView(tlayout); // Añadimos la tabla creada
                            }
                        });

                        // Por cada uno de los equipos, pero de dos en dos, ya que en cada fila habrá
                        // sólo DOS equipos (columnas), será una tabla de ???x2
                        for (int z = 0; z < equipos.size(); z+=2) {

                            // Ajustes de UI
                            final TableRow trow = new TableRow(getActivity().getApplicationContext());
                            trow.setGravity(Gravity.CENTER);

                            final TableRow.LayoutParams lpb = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    trow.setLayoutParams(lp);
                                    tlayout.addView(trow); // Añadimos la fila creada
                                }
                            });

                            // Para cada uno de los DOS posibles equipos de esa fila
                            for (int j = z; j < z+2 && j < equipos.size(); j++) {

                                // Creamos la UI
                                final CardView card = new CardView(getActivity().getApplicationContext());
                                cardsArray.add(card);
                                final TextView ipText = new TextView(getActivity().getApplicationContext());
                                final TextView nombreVersionText = new TextView(getActivity().getApplicationContext());

                                // Ajustes de UI
                                final LinearLayout cardLayout = new LinearLayout(getActivity().getApplicationContext());
                                cardLayout.setOrientation(LinearLayout.VERTICAL);
                                card.setContentPadding(10,10,10,10);
                                card.setUseCompatPadding(true);
                                card.setMaxCardElevation(10);
                                card.addView(cardLayout);

                                // Datos del equipo de la base de datos
                                final String ipEquipo = equipos.get(j).getIP();
                                ipsArray.add(ipEquipo);
                                final String nombreEquipo = equipos.get(j).getNombre_e();
                                final Integer versionEquipo = equipos.get(j).getV_snmp();

                                /* Ponemos en marcha el task
                                 * (La opción THREAD_POOL_EXECUTOR nos permite que resuelva varios tasks en paralelo,
                                 * de lo contrario, tendría que esperar a que terminase uno para resolver otro)
                                 *
                                 * Aquí hemos añadido TRES argumentos: oid, el objetivo del task,
                                 * y el indice de equipos introducidos (pasado a String porque todos
                                 * los argumentos tienen que ser del mismo tipo)
                                 */
                                new mAsyncTaskGet().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ".1.3.6.1.2.1.1.6", "online", arrayIndex.toString());

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ipText.setText(ipEquipo);
                                        nombreVersionText.setText(nombreEquipo + "(V" + versionEquipo.toString() + ")");
                                        nombreVersionText.setTypeface(null, Typeface.BOLD_ITALIC);

                                        cardLayout.addView(ipText);
                                        cardLayout.addView(nombreVersionText);

                                        card.setLayoutParams(lpb);

                                        trow.addView(card); // Añadimos la Card a la fila
                                    }
                                });

                                arrayIndex++;

                            }
                        }

                    }
                }

            }
        }).start();

        return view;
    }

    // Nos permite escribir el contenido real del mensaje SNMP recibido
    private void escribir(String[] result) {

        String[] parts = result[1].split("=");
        String part1 = parts[2]; // 123
        String extraerp = part1.substring(0, 1); // Extraigo la primera letra
        String extraeru = part1.substring(part1.length() - 1); //Extraigo la ultima letra letra
        String remplazado = part1.replace(extraerp, ""); // quitamos el primer caracter
        String remplazadofinal = remplazado.replace(extraeru, "");// se quita el ultimo caracter

        // Para obtener la Card a la que pertenece
        Integer arrayIndex = Integer.valueOf(result[2]);
        CardView card = cardsArray.get(arrayIndex);
        LinearLayout cardLayout = (LinearLayout) card.getChildAt(0);

        // Segun el objetivo del task
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

    /**
     * Clase que nos permite realizar varias tareas en segundo plano y gestionar su antes, durante
     * y después. En este caso recibe TRES argumentos en un array String: el oid solicitado, el
     * objetivo del task (si es para ver si está online, si es para obtener un sys??? en concreto,
     * etc.) y el índice del array de dispositivos, para poder acceder a su Card y a su IP después.
     */
    class mAsyncTaskGet extends AsyncTask<String, String[], String[]> {
        String[] respuesta =  new String[3]; // Lo que viajará en la TASK del doInBackground() al
                                             // onPostExecute(): el objetivo, el resultado del
                                             // mensaje enviado y el índice de dispositivos

        protected void onPreExecute() {
        }

        /**
         * Se activa una vez comienza a ejecutarse la tarea. Lo hace en segundo plano. Lanza el
         * mensaje SNMP GET.
         */
        @Override
        protected String[] doInBackground(String... params) {
            try {
                respuesta[0] = params[1]; // Ponemos ahora el objetivo en primer lugar

                Integer arrayIndex = Integer.valueOf(params[2]);

                // Cambiamos el oid por el resultado del mensaje SNMP
                respuesta[1] = new SNMPRequest().sendSnmpGetNext(params[0], ipsArray.get(arrayIndex));
                respuesta[2] = params[2]; // Se mantiene igual (índice de dispositivos)

            } catch (Exception e) {
                respuesta[1] = "fallo";
            }
            return respuesta;
        }

        /**
         * Se activa una vez finaliza la tarea.
         */
        protected void onPostExecute(String[] result) {
            Integer arrayIndex = Integer.valueOf(result[2]); // Pasamos a Integer el índice

            // Si se ha recibido respuesta: o bien esta online, o bien tenemos que dar la info. del
            // mensaje recibido (según el parámetro "objetivo")
            if (result[1] != "fallo" && result[1] != "" && result[1] != null) {
                // Chequeo del parámetro "objetivo"
                if (result[0] == "online") {

                    // Añadimos la info. de que está online
                    CardView card = cardsArray.get(arrayIndex);
                    LinearLayout cardLayout = (LinearLayout) card.getChildAt(0);

                    final TextView nameText = new TextView(getActivity().getApplicationContext());
                    nameText.setText("ONLINE");
                    nameText.setTextColor(Color.GREEN);

                    cardLayout.addView(nameText);

                    // Como está online, vamos a chequear algunos valores de información
                    new mAsyncTaskGet().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                            ".1.3.6.1.2.1.1.5", "name", arrayIndex.toString());
                    new mAsyncTaskGet().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                            ".1.3.6.1.2.1.1.6", "loc", arrayIndex.toString());
                    new mAsyncTaskGet().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                            ".1.3.6.1.2.1.1.4", "contact", arrayIndex.toString());

                } else // Será entonces que quiere el contenido de sysLocation, sysName o sysContact
                    escribir(result);
            } else {
                // No se ha recibido respuesta, por lo que está offline
                if (result[0] == "online") {

                    // Añadimos la info. de que está offline
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
