package com.clientesnmp.app;

import android.content.Intent;
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
 * Activity inicial. Página de inicio de sesión mediante la introducción de usuario y contraseña,
 * así como de botón para crear una cuenta nueva.
 */
public class LoginActivity extends AppCompatActivity {

    // UI
    private Button botonLogin, botonRegister;
    private EditText userEditText, passEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Asociación de UI con el layout XML
        botonLogin = findViewById(R.id.sign_in);
        botonRegister = findViewById(R.id.register);
        userEditText = findViewById(R.id.username);
        passEditText = findViewById(R.id.password);

        // Botón de crear cuenta que lleva a la Activity correspondiente
        botonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        // Botón de login, que se pulsaría una vez introducidos los datos
        botonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Obtenemos los datos introducidos
                final String userText = userEditText.getText().toString();
                final String passwordText = passEditText.getText().toString();

                // No podemos dejar nada en blanco, en ese caso nos saltará una alerta
                if (userText.isEmpty() || passwordText.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "¡Rellena los huecos!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Obtenemos la instancia de la base de datos y los DAOs
                    Database database = Database.getDatabase(getApplicationContext());
                    final UserDao userDao = database.userDao();

                    /* Hilo donde gestionamos las operaciones con la base de datos. Aquí intentamos
                     * ver si hay correspondencia entre el usuario y la contraseña introducida. Si
                     * es así, damos paso a la MainActivity pasándole al Intent el identificador
                     * del usuario logueado.
                     */
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            UserEntity userEntity = userDao.login(userText, passwordText);
                            if (userEntity == null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Credenciales inválidas", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                i.putExtra("user_id",userEntity.getId());
                                startActivity(i);
                            }
                        }
                    }).start();;
                }
            }
        });
    }

}
