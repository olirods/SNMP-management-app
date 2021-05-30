package com.clientesnmp.app.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

/**
 * Data Access Object de la entidad de usuario. Contiene las funciones para acceder a ellos.
 */
@Dao
public interface UserDao {

    /**
     * Inserta un usuario nuevo en la base de datos.
     *
     * @param userEntity entidad con los datos del usuario. Debe haber sido "rellenada" antes
     */
    @Insert
    void registerUser(UserEntity userEntity);

    /**
     * Comprueba si la tupla usuario-contraseña es correcta y corresponde a un usuario concreto de
     * la base de datos. Si es así, lo devuelve.
     *
     * @param userLogin   nombre de usuario
     * @param password contraseña
     * @return una entidad con los datos del usuario
     */
    @Query("SELECT * from users where userLogin=(:userLogin) and password=(:password)")
    UserEntity login(String userLogin, String password);

    /**
     * Obtiene la entidad con los datos del usuario a partir de su identificador
     *
     * @param id identificador de usuario
     * @return una entidad con los datos del usuario
     */
    @Query("SELECT * from users where id=(:id)")
    UserEntity getUser(Integer id);

    /**
     * Cambia la contraseña de un usuario en cuestión
     *
     * @param id      identificador de usuario
     * @param newPass nueva contraseña
     */
    @Query("UPDATE users SET password = :newPass" +
            " WHERE id = :id")
    void updatePass(Integer id, String newPass);
}
