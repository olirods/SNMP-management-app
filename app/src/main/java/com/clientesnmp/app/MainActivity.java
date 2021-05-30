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

public class MainActivity extends AppCompatActivity {
    private int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_id = getIntent().getIntExtra("user_id", 0);

        Database database = Database.getDatabase(getApplicationContext());
        final UserDao userDao = database.userDao();
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

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Bundle bundle = new Bundle();
        bundle.putInt("user_id", user_id);

        DashboardFragment dash = new DashboardFragment();
        dash.setArguments(bundle);

        loadFragment(dash);

        Intent trapIntent = new Intent(MainActivity.this, TrapService.class);
        Intent checkIntent = new Intent(MainActivity.this, CheckService.class);

        trapIntent.putExtras(bundle);
        checkIntent.putExtras(bundle);

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

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            Bundle bundle = new Bundle();
            bundle.putInt("user_id", user_id);

            switch (item.getItemId()) {

                case R.id.navigation_dashboard:
                    fragment = new DashboardFragment();
                    fragment.setArguments(bundle);
                    break;
                case R.id.navigation_logs:
                    fragment = new LogsFragment();
                    fragment.setArguments(bundle);
                    break;
                case R.id.navigation_settings:
                    fragment = new SettingsFragment();
                    fragment.setArguments(bundle);
                    break;

            }

            return loadFragment(fragment);
        }
    };

}


