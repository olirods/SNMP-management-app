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

/**
 * Fragment de Ajustes. Nos permite ajustar los puertos SNMP, la frecuencia de monitorización de
 * estado y cambiar la contraseña del usuario.
 */
public class SettingsFragment extends Fragment {
    private int user_id; // Identificador del usuario logueado

    // UI
    private EditText getPortEditText;
    private EditText trapPortEditText;
    private EditText checkEditText;
    private Button cambiarButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Obtenemos del MainActivity el identificador del usuario logueado
        if (!getArguments().isEmpty()) {
            user_id = getArguments().getInt("user_id");
        }

        // Asociación de UI con el layout XML
        getPortEditText = view.findViewById(R.id.editText2);
        trapPortEditText = view.findViewById(R.id.editText3);
        checkEditText = view.findViewById(R.id.checkEditText);
        cambiarButton = view.findViewById(R.id.cambiar);

        // Botón de cambiar contraseña, que nos lleva a la correspondiente Activity
        cambiarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CambiarPassActivity.class);
                i.putExtra("user_id",user_id);
                startActivity(i);
            }
        });

        // Valores actuales de los puertos y la frecuencia
        getPortEditText.setText(SNMPRequest.port);
        trapPortEditText.setText(TrapService.puertoTrap);
        checkEditText.setText(CheckService.frecuencia.toString());

        // Ajustar el valor nuevo del puerto si el usuario lo modifica
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

        // Ajustar el valor nuevo del puerto si el usuario lo modifica
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

}
