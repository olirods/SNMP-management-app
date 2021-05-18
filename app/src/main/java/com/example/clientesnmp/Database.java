package com.example.clientesnmp;

import android.content.Context;
import android.widget.Toast;

import androidx.room.Room;
import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {UserEntity.class, GrupoEntity.class, EquipoEntity.class}, version = 1)
public abstract class Database extends RoomDatabase {

    private static final String dbName = "snmpDatabase";
    private static Database database;

    public static synchronized Database getDatabase(Context context) {

        if (database == null) {
            database = Room.databaseBuilder(context, Database.class, dbName)
                    .fallbackToDestructiveMigration()
                    .build();

            final GrupoEntity firewalls = new GrupoEntity();
            firewalls.setNombre_g("Firewall");

            final GrupoEntity switches = new GrupoEntity();
            switches.setNombre_g("Switch");

            final GrupoEntity servidores = new GrupoEntity();
            servidores.setNombre_g("Servidor");

            final GrupoDao grupoDao = database.grupoDao();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    grupoDao.addGrupo(firewalls);
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    grupoDao.addGrupo(switches);
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    grupoDao.addGrupo(servidores);
                }
            }).start();

            final UserEntity admin = new UserEntity();
            admin.setUserId("admin");
            admin.setPassword("admin");
            admin.setName("Administrador");

            final UserDao userDao = database.userDao();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    userDao.registerUser(admin);
                }
            }).start();

            final EquipoEntity windows = new EquipoEntity();
            windows.setIP("192.168.0.23");
            windows.setNombre_e("Windows");
            windows.setV_snmp(2);
            windows.setId_u(admin.getId_u());
            windows.setId_g(switches.getId_g());

            final EquipoDao equipoDao = database.equipoDao();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    equipoDao.addEquipo(windows);
                }
            }).start();

        }

        return database;
    }

    public abstract UserDao userDao();

    public abstract EquipoDao equipoDao();

    public abstract GrupoDao grupoDao();

}
