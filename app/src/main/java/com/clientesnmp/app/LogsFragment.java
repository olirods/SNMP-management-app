package com.clientesnmp.app;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.clientesnmp.app.database.Database;
import com.clientesnmp.app.database.LogDao;
import com.clientesnmp.app.database.LogEntity;

import java.util.Date;
import java.util.List;

/**
 * Fragment de mensajes logs. Nos permite ver el historial completo de mensajes de logs para todos
 * los dispositivos, almacenado en la base de datos. Tiene una página scrollable, y que una vez
 * iniciado el Fragment baja hacia abajo de forma automática para ver la última línea de log
 */
public class LogsFragment extends Fragment {
    private int user_id; // Identificador del usuario logueado

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_log, container, false);

        // Obtenemos del MainActivity el identificador del usuario logueado
        if (!getArguments().isEmpty()) {
            user_id = getArguments().getInt("user_id");
        }

        // Asociación de UI con el layout XML
        final ScrollView scroll = (ScrollView) view.findViewById(R.id.scroll);
        final LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout);

        // Obtenemos la instancia de la base de datos y el DAO
        Database database = Database.getDatabase(getActivity().getApplicationContext());
        final LogDao logDao = database.logDao();

        /* Hilo donde gestionamos las operaciones con la base de datos. Aquí accedemos a ella para
         * obtener la lista completa de logs almacenados y mostrarlos.
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<LogEntity> logs = logDao.getLogsFromUser(new Integer(user_id));

                // Para cada mensaje de log
                for (int i = 0; i < logs.size(); i++) {
                    final TextView logTextView = new TextView(getActivity().getApplicationContext());
                    logTextView.setTextColor(Color.WHITE);
                    logTextView.setPadding(0,5,0,0); // Espacio entre logs

                    final Date date = logs.get(i).getCreateDate();
                    final String message = logs.get(i).getMessage();

                    /* Necesitamos este método para realizar acciones en la UI dentro de los Threads
                     * de acceso a la base de datos. Lo usamos para añadir el contenido del log
                     * en la TextView y añadirlo al layout.
                     */
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            logTextView.setText(date.toString() + " - " + message);
                            layout.addView(logTextView);
                        }
                    });

                }
            }
        }).start();

        // Bajar hasta el final de la ScrollView, para poder ver el último log existente
        scroll.postDelayed(new Runnable() {
            @Override
            public void run() {
                scroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        },1000);


        return view;
    }

}
