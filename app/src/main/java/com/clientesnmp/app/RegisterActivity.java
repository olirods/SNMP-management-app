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

public class RegisterActivity extends AppCompatActivity {

    EditText userEditText, passEditText, pass2EditText, nameEditText;
    Button registerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userEditText = findViewById(R.id.username);
        passEditText = findViewById(R.id.password);
        pass2EditText = findViewById(R.id.password2);
        nameEditText = findViewById(R.id.name);

        registerButton = findViewById(R.id.register);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creating User Entity
                final UserEntity userEntity = new UserEntity();
                userEntity.setUserLogin(userEditText.getText().toString());
                userEntity.setPassword(passEditText.getText().toString());
                userEntity.setName(nameEditText.getText().toString());

                if (validateInput(userEntity)) {

                    if (validatePassword(passEditText.getText().toString(), pass2EditText.getText().toString())) {

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
                        Toast.makeText(getApplicationContext(), "¡Las contraseñas no son iguales!", Toast.LENGTH_SHORT).show();
                    }

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

    private Boolean validatePassword(String pass1, String pass2) {
        if (!pass1.equals(pass2)) {
            return false;
        }
        return true;
    }

}
