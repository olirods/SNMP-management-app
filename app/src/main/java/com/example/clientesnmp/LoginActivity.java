package com.example.clientesnmp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private Button botonLogin, botonRegister;
    private EditText userEditText, passEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        botonLogin = findViewById(R.id.sign_in);
        botonRegister = findViewById(R.id.register);
        userEditText = findViewById(R.id.username);
        passEditText = findViewById(R.id.password);

        botonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        botonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userText = userEditText.getText().toString();
                final String passwordText = passEditText.getText().toString();
                if (userText.isEmpty() || passwordText.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "¡Rellena los huecos!", Toast.LENGTH_SHORT).show();
                } else {
                    // Perform query
                    Database database = Database.getDatabase(getApplicationContext());
                    final UserDao userDao = database.userDao();
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
                                i.putExtra("user_id",userEntity.getId_u());
                                startActivity(i);
                            }
                        }
                    }).start();;
                }
            }
        });
    }

}
