package com.example.clientesnmp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CambiarPassActivity extends AppCompatActivity {

    EditText oldEditText, new1EditText, new2EditText;
    Button registerButton;

    private int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_pass);

        user_id = getIntent().getIntExtra("user_id", 0);

        oldEditText = findViewById(R.id.old);
        new1EditText = findViewById(R.id.new1);
        new2EditText = findViewById(R.id.new2);

        registerButton = findViewById(R.id.register);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String new1, new2;

                new1 = new1EditText.getText().toString();
                new2 = new2EditText.getText().toString();

                if (!new1.isEmpty() && !new2.isEmpty()) {

                    if (validatePassword(new1, new2)) {

                        // Do insert operation
                        Database database = Database.getDatabase(getApplicationContext());
                        final UserDao userDao = database.userDao();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // Register user
                                userDao.updatePass(user_id, new1);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "¡Contraseña cambiada!", Toast.LENGTH_SHORT).show();
                                        finish();
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


    private Boolean validatePassword(String pass1, String pass2) {
        if (!pass1.equals(pass2)) {
            return false;
        }
        return true;
    }
}
