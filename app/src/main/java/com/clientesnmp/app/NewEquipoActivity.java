package com.clientesnmp.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.clientesnmp.app.database.Database;
import com.clientesnmp.app.database.EquipoDao;
import com.clientesnmp.app.database.EquipoEntity;

public class NewEquipoActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private int user_id;

    EditText ipEditText, nombreEditText, versionEditText;
    Button newButton;
    Spinner grupo;

    int g_id_selected=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_equipo);

        user_id = getIntent().getIntExtra("user_id", 0);

        ipEditText = findViewById(R.id.ip);
        nombreEditText = findViewById(R.id.nombre);
        versionEditText = findViewById(R.id.version);

        grupo = (Spinner) findViewById(R.id.grupo);
        grupo.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.grupos_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        grupo.setAdapter(adapter);


        newButton = findViewById(R.id.register);

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creating User Entity
                final EquipoEntity equipoEntity = new EquipoEntity();
                equipoEntity.setId_u(user_id);
                equipoEntity.setId_g(g_id_selected);
                equipoEntity.setIP(ipEditText.getText().toString());
                equipoEntity.setNombre_e(nombreEditText.getText().toString());
                equipoEntity.setOnline(1);
                if (!versionEditText.getText().toString().isEmpty())
                    equipoEntity.setV_snmp(Integer.valueOf(versionEditText.getText().toString()));

                if (validateInput(equipoEntity)) {
                    // Do insert operation
                    Database database = Database.getDatabase(getApplicationContext());
                    final EquipoDao equipoDao = database.equipoDao();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // Register user
                            equipoDao.addEquipo(equipoEntity);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "¡Dispositivo registrado!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        }
                    }).start();

                } else {
                    Toast.makeText(getApplicationContext(), "¡Hay huecos vacíos!", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        g_id_selected=position+1;
    }

    public void onNothingSelected(AdapterView<?> arg0) {
    }

    private Boolean validateInput(EquipoEntity equipoEntity) {
        if (equipoEntity.getIP().isEmpty() ||
                equipoEntity.getNombre_e().isEmpty()) {
            return false;
        }
        return true;
    }

}
