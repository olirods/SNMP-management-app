package com.clientesnmp.app.database;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

/**
 * Data Access Object de la entidad de equipo. Contiene las funciones para acceder a ellos.
 */
@Dao
public interface EquipoDao {

    /**
     * Inserta un equipo nuevo en la base de datos.
     *
     * @param equipoEntity la entidad del equipo en cuestión. Debe haber sido "rellenada" antes.
     */
    @Insert
    void addEquipo(EquipoEntity equipoEntity);

    /**
     * Obtiene un equipo en cuestión, a partir de su identificador.
     *
     * @param id identificador del equipo
     * @return una entidad con los datos del equipo
     */
    @Query("SELECT * from equipos where id=(:id)")
    EquipoEntity getEquipo(Integer id);

     /**
     * Obtiene un equipo en cuestión, a partir de su IP.
     *
     * @param ip dirección IP del equipo
     * @return una entidad con los datos del equipo
     */
    @Query("SELECT * from equipos where IP=(:ip)")
    EquipoEntity getEquipo(String ip);

    /**
     * Obtiene la lista de todos los equipos existentes en la base de datos para un usuario
     * pertenecientes a un grupo (Servidores, Switches, Firewalls) en concreto.
     *
     * @param id_u identificador de usuario
     * @param id_g identificador de grupo
     * @return la lista de equipos
     */
    @Query("SELECT * from equipos where id_u=(:id_u) and id_g=(:id_g)")
    List<EquipoEntity> getEquipos(Integer id_u, Integer id_g);

    /**
     * Obtiene la lista de todos los equipos existentes en la base de datos para un usuario
     *
     * @param id_u identificador de usuario
     * @return la lista de equipos
     */
    @Query("SELECT * from equipos where id_u=(:id_u)")
    List<EquipoEntity> getEquipos(Integer id_u);

    /**
     * Actualizar el estado actual de un equipo
     *
     * @param id      identificador de equipo
     * @param newOnline estado actual del equipo (online-1, offline-0)
     */
    @Query("UPDATE equipos SET online = :newOnline" +
            " WHERE id = :id")
    void updateOnline(Integer id, Integer newOnline);


}
