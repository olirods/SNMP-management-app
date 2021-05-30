package com.clientesnmp.app.database;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

/**
 * Data Access Object de la entidad de mensaje de log. Contiene las funciones para acceder a ellos.
 */
@Dao
public interface LogDao {

    /**
     * Inserta un mensaje de log nuevo en la base de datos.
     *
     * @param logEntity la entidad del mensaje de log en cuestión. Debe haber sido "rellenada" antes.
     */
    @Insert
    void insertLog(LogEntity logEntity);

    /**
     * Obtiene la lista de todos los mensajes de log existentes en la base de datos para un usuario
     *
     * @param id_u identificador de usuario
     * @return la lista completa de todos los logs
     */
    @Query("SELECT * from logs where id_u=(:id_u)")
    List<LogEntity> getLogsFromUser(Integer id_u);
}
