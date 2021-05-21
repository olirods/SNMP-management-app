package com.example.clientesnmp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    EditText userEditText, passEditText, nameEditText;
    Button registerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userEditText = findViewById(R.id.username);
        passEditText = findViewById(R.id.password);
        nameEditText = findViewById(R.id.name);

        registerButton = findViewById(R.id.register);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creating User Entity
                final UserEntity userEntity = new UserEntity();
                userEntity.setUserId(userEditText.getText().toString());
                userEntity.setPassword(passEditText.getText().toString());
                userEntity.setName(nameEditText.getText().toString());

                if (validateInput(userEntity)) {
                    // Do insert operation
                    Database database = Database.getDatabase(getApplicationContext());
                    final UserDao userDao = database.userDao();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // Register user
                            userDao.registerUser(userEntity);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "¡Usuario registrado!", Toast.LENGTH_SHORT).show();
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

    private Boolean validateInput(UserEntity userEntity) {
        if (userEntity.getName().isEmpty() ||
            userEntity.getPassword().isEmpty() ||
            userEntity.getName().isEmpty()) {
            return false;
        }
        return true;
    }

}
