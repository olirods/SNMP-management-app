package com.clientesnmp.app.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Clase entidad de un usuario dentro de la base de datos. Sus atributos son las columnas de la
 * tabla en SQL. Contiene métodos para acceder a ellos ya que se mantienen privados dentro de la
 * clase.
 */
@Entity(tableName = "users")
public class UserEntity {

    @PrimaryKey(autoGenerate = true)
    private Integer id; // Identificador de usuario

    @ColumnInfo(name = "userLogin")
    private String userLogin; // Nombre de usuario para logearse

    @ColumnInfo(name = "password")
    private String password; // Contraseña del usuario

    @ColumnInfo(name = "name")
    private String name; // Nombre público del usuario

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
