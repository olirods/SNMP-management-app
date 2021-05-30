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


public class LogsFragment extends Fragment {


    // UI
    private EditText ipEditText;
    private EditText trapPortEditText;

    private int user_id;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_log, container, false);

        final ScrollView scroll = (ScrollView) view.findViewById(R.id.scroll);

        if (!getArguments().isEmpty()) {
            user_id = getArguments().getInt("user_id");
        }

        final LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout);

        Database database = Database.getDatabase(getActivity().getApplicationContext());
        final LogDao logDao = database.logDao();

        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<LogEntity> logs = logDao.getLogsFromUser(new Integer(user_id));

                for (int i = 0; i < logs.size(); i++) {
                    final TextView logTextView = new TextView(getActivity().getApplicationContext());
                    logTextView.setTextColor(Color.WHITE);
                    logTextView.setPadding(0,5,0,0);

                    final Date date = logs.get(i).getCreateDate();
                    final String message = logs.get(i).getMessage();

                    Boolean end_aux = false;

                    if (i == logs.size()-1){
                        end_aux = true;
                    }

                    final Boolean end = end_aux;

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

        scroll.postDelayed(new Runnable() {
            @Override
            public void run() {
                scroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        },1000);


        return view;
    }

}
