package com.clientesnmp.app.database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

/**
 * Clase principal de la base de datos. Habrá una instancia de ella asociada al ApplicationContext
 * y, a través de ella, accederemos a todas las entidades existentes:
 *  - UserEntity: usuario
 *  - EquipoEntity: equipo o dispositivo a monitorizar
 *  - LogEntity: mensaje de monitorización
 *
 *  Hemos usado una abstracción de SQLite, que nos simplifica mucho el acceso a la base de datos.
 *
 *  El funcionamiento es el siguiente:
 *   1) En la app, con la instancia de la base de datos solicitaremos el DAO (Objetos de Acceso a
 *   Datos) de una entidad.
 *   2) A través de ese DAO, tenemos acceso a las entidades. Usaremos sus funciones para obtener
 *   todos los objetos existentes, para solicitar uno concreto, para cambiar una celda, etc. Habrá
 *   un DAO por cada entidad (Entity).
 *   3) El DAO es el que se comunica directamente con SQL y las Entity, realizando todas las
 *   consultas pertinentes según se haya requerido desde la app.
 *
 */
@androidx.room.Database(entities = {UserEntity.class, EquipoEntity.class, LogEntity.class},
        version = 10)
@TypeConverters({TimestampConverter.class})
public abstract class Database extends RoomDatabase {


    private static final String dbName = "snmpDatabase";
    private static Database database;

    /**
     * Devuelve la base de datos.
     *
     * @param context el ApplicationContext, que será único en toda la app
     * @return la base de datos propia para acceder a ella
     */
    public static synchronized Database getDatabase(Context context) {

        // Si no existe, la creamos. Esto se hará la primera vez que se use la app
        if (database == null) {
            database = Room.databaseBuilder(context, Database.class, dbName)
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return database;
    }

    /**
     * Data Access Object de la entidad de usuario.
     *
     * @return una instancia de un UserDao
     */
    public abstract UserDao userDao();

    /**
     * Data Access Object de la entidad de equipo.
     *
     * @return una instancia de un EquipoDao
     */
    public abstract EquipoDao equipoDao();

    /**
     * Data Access Object de la entidad de log.
     *
     * @return una instancia de un LogDao
     */
    public abstract LogDao logDao();

}
