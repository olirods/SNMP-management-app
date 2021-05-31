package com.clientesnmp.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.clientesnmp.app.database.Database;
import com.clientesnmp.app.database.UserDao;
import com.clientesnmp.app.database.UserEntity;
import com.clientesnmp.app.services.CheckService;
import com.clientesnmp.app.services.TrapService;

/**
 * Activity principal. Contiene la app en sí, con acceso mediante un menú inferior a los TRES
 * Fragments o subActivities. Se encarga de poner en marcha estos Fragments, así como los Services
 * de monitorización del estado (CheckService) y de escucha de los TRAPS (TrapService)
 */
public class MainActivity extends AppCompatActivity {
    private int user_id; // Identificador del usuario logueado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtenemos del LoginActivity el identificador del usuario logueado
        user_id = getIntent().getIntExtra("user_id", 0);

        // Obtenemos la instancia de la base de datos y los DAOs
        Database database = Database.getDatabase(getApplicationContext());
        final UserDao userDao = database.userDao();

        /* Hilo donde gestionamos las operaciones con la base de datos. Aquí simplemente
         * accedemos a los datos del usuario para darle la bienvenida llamándolo por su nombre
         * público.
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                final UserEntity userEntity = userDao.getUser(user_id);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Bienvenido, " + userEntity.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();

        // Ajustes del menu inferior
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Extras de opciones para pasar a los Intents de las Activities y Services
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", user_id); // Le pasamos el id del usuario logueado

        // Ajustes para que al iniciarse la Activity se abra primero el Dashboard
        DashboardFragment dash = new DashboardFragment();
        dash.setArguments(bundle);
        loadFragment(dash);

        // Configuramos Services
        Intent trapIntent = new Intent(MainActivity.this, TrapService.class);
        Intent checkIntent = new Intent(MainActivity.this, CheckService.class);
        trapIntent.putExtras(bundle);
        checkIntent.putExtras(bundle);

        // Arrancamos Services
        startService(trapIntent);
        startService(checkIntent);

    }


    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            return true;
        }
        return false;
    }

    // Configura el menu inferior para que según el botón que pulsemos nos lleve al Fragment
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            Bundle bundle = new Bundle();
            bundle.putInt("user_id", user_id); // Le pasamos el id del usuario logueado

            switch (item.getItemId()) {

                case R.id.navigation_dashboard: // DASHBOARD (equipos y su estado)
                    fragment = new DashboardFragment();
                    fragment.setArguments(bundle);
                    break;
                case R.id.navigation_logs: // LOGS (mensajes de monitorización)
                    fragment = new LogsFragment();
                    fragment.setArguments(bundle);
                    break;
                case R.id.navigation_settings: // AJUSTES DE LA APP
                    fragment = new SettingsFragment();
                    fragment.setArguments(bundle);
                    break;

            }

            return loadFragment(fragment);
        }
    };

}


