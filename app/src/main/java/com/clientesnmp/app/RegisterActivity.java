package com.clientesnmp.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.clientesnmp.app.database.Database;
import com.clientesnmp.app.database.UserDao;
import com.clientesnmp.app.database.UserEntity;

/**
 * Actividad para crear una cuenta en la base de datos. Se inicia al tocar el botón correspondiente
 * en la LoginActivity. Nos permite introducir los datos de un nuevo usuario en la base de datos.
 */
public class RegisterActivity extends AppCompatActivity {

    // UI
    EditText userEditText, passEditText, pass2EditText, nameEditText;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Asociación de UI con el layout XML
        userEditText = findViewById(R.id.username);
        passEditText = findViewById(R.id.password);
        pass2EditText = findViewById(R.id.password2);
        nameEditText = findViewById(R.id.name);
        registerButton = findViewById(R.id.register);

        // Botón de registrar que se pulsaría una vez se han introducido los datos
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Creamos la UserEntity con los datos introducidos
                final UserEntity userEntity = new UserEntity();
                userEntity.setUserLogin(userEditText.getText().toString());
                userEntity.setPassword(passEditText.getText().toString());
                userEntity.setName(nameEditText.getText().toString());

                // Si son válidos (no vacíos)
                if (validateInput(userEntity)) {

                    // Si las dos contraseñas coinciden
                    if (validatePassword(passEditText.getText().toString(), pass2EditText.getText().toString())) {

                        // Obtenemos la instancia de la base de datos y el DAO
                        Database database = Database.getDatabase(getApplicationContext());
                        final UserDao userDao = database.userDao();

                        /* Hilo donde gestionamos las operaciones con la base de datos. Aquí
                         * simplemente añadimos un nuevo usuario en la base de datos, y si hay ido
                         * bien, cerramos la Activity, volviendo a la LoginActivity
                         */
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // Register user
                                userDao.registerUser(userEntity);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "¡Usuario registrado!", Toast.LENGTH_SHORT).show();
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

    // Valida si los datos que se encuentran dentro de la UserEntity no están vacíos
    private Boolean validateInput(UserEntity userEntity) {
        if (userEntity.getName().isEmpty() ||
            userEntity.getPassword().isEmpty() ||
            userEntity.getUserLogin().isEmpty()) {
            return false;
        }
        return true;
    }

    // Valida si las dos contraseñas pasadas son iguales, para verificar que no hay errores al
    // introducirlas
    private Boolean validatePassword(String pass1, String pass2) {
        if (!pass1.equals(pass2)) {
            return false;
        }
        return true;
    }

}
