package com.clientesnmp.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.clientesnmp.app.database.Database;
import com.clientesnmp.app.database.UserDao;

/**
 * Activity para cambiar la contraseña del usuario logueado. Accedemos a ella desde el botón
 * correspondiente en la SettingsFragment.
 */
public class CambiarPassActivity extends AppCompatActivity {
    private int user_id; // Identificador del usuario logueado

    // UI
    EditText oldEditText, new1EditText, new2EditText;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_pass);

        // Obtenemos del SettingsFragment el identificador del usuario logueado
        user_id = getIntent().getIntExtra("user_id", 0);

        // Asociación de UI con el layout XML
        oldEditText = findViewById(R.id.old);
        new1EditText = findViewById(R.id.new1);
        new2EditText = findViewById(R.id.new2);
        registerButton = findViewById(R.id.register);

        // Botón para proceder al cambio de contraseña tras haber introducido los datos
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Nueva contraseña introducida en los dos campos
                final String new1, new2;
                new1 = new1EditText.getText().toString();
                new2 = new2EditText.getText().toString();

                // Si los campos nos son vacíos y ambas son iguales, procedemos
                if (!new1.isEmpty() && !new2.isEmpty()) {
                    if (validatePassword(new1, new2)) {

                        // Obtenemos la instancia de la base de datos y el DAO
                        Database database = Database.getDatabase(getApplicationContext());
                        final UserDao userDao = database.userDao();

                        /* Hilo donde gestionamos las operaciones con la base de datos. Aquí
                         * simplemente cambiamos la contraseña del usuario logueado partiendo
                         * de su identificador, y si ha ido bien, cerramos la Activity volviendo
                         * al SettingsFragment
                         */
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                userDao.updatePass(user_id, new1);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "¡Contraseña cambiada!", Toast.LENGTH_SHORT).show();
                                        finish(); // Cerramos Activity
                                    }
                                });
                            }
                        }).start();

                    } else {
                        Toast.makeText(getApplicationContext(), "¡Las contraseñas no son iguales!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "¡Hay huecos vacíos!", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    // Validamos contraseñas (si son iguales) para evitar errores de introducción
    private Boolean validatePassword(String pass1, String pass2) {
        if (!pass1.equals(pass2)) {
            return false;
        }
        return true;
    }
}
