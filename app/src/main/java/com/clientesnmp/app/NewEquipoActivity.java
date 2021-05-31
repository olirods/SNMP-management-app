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
    private int user_id; // Identificador del usuario logueado

    // UI
    EditText ipEditText, nombreEditText, versionEditText;
    Button newButton;
    Spinner grupo;

    int g_id_selected = 1; // Grupo seleccionado por defecto (Servidores)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_equipo);

        // Obtenemos del DashboardFragment el identificador del usuario logueado
        user_id = getIntent().getIntExtra("user_id", 0);

        // Asociación de UI con el layout XML
        ipEditText = findViewById(R.id.ip);
        nombreEditText = findViewById(R.id.nombre);
        versionEditText = findViewById(R.id.version);
        grupo = (Spinner) findViewById(R.id.grupo);
        newButton = findViewById(R.id.register);

        // Ajustes de los valores de la lista de selección de grupo
        grupo.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.grupos_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        grupo.setAdapter(adapter);

        // Botón de registro del dispositivo que se pulsaría una vez se han introducido los datos
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Creamos el nuevo EquipoEntity con los datos introducidos
                final EquipoEntity equipoEntity = new EquipoEntity();
                equipoEntity.setId_u(user_id);
                equipoEntity.setId_g(g_id_selected);
                equipoEntity.setIP(ipEditText.getText().toString());
                equipoEntity.setNombre_e(nombreEditText.getText().toString());
                // De primeras suponemos online
                equipoEntity.setOnline(1); // online(1), offline(0)

                // Comprobamos que el campo no esté vacío, sino daría error
                if (!versionEditText.getText().toString().isEmpty())
                    equipoEntity.setV_snmp(Integer.valueOf(versionEditText.getText().toString()));

                // Validamos la nueva entidad creada (no campos vacíos)
                if (validateInput(equipoEntity)) {

                    // Obtenemos la instancia de la base de datos y el DAO
                    Database database = Database.getDatabase(getApplicationContext());
                    final EquipoDao equipoDao = database.equipoDao();

                    /* Hilo donde gestionamos las operaciones con la base de datos. Aquí
                     * simplemente añadimos un nuevo equipo a la base de datos, y si hay ido
                     * bien, cerramos la Activity, volviendo al DashboardFragment
                     */
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // Register user
                            equipoDao.addEquipo(equipoEntity);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),
                                            "¡Dispositivo registrado!", Toast.LENGTH_SHORT).show();
                                    finish(); // Cerramos Activity
                                }
                            });
                        }
                    }).start();

                } else {
                    Toast.makeText(getApplicationContext(), "¡Hay huecos vacíos!",
                            Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    // Ajustamos el grupo seleccionado si el usuario toca la lista de selección
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        g_id_selected=position+1;
    }

    public void onNothingSelected(AdapterView<?> arg0) {
    }

    // Validar el equipo introducido (campos no vacíos)
    private Boolean validateInput(EquipoEntity equipoEntity) {
        if (equipoEntity.getIP().isEmpty() ||
                equipoEntity.getNombre_e().isEmpty()) {
            return false;
        }
        return true;
    }

}
