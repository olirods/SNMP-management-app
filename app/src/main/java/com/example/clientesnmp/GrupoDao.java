package com.example.clientesnmp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface GrupoDao {

    @Insert
    void addGrupo(GrupoEntity grupoEntity);

    //@Query("SELECT * from users where userId=(:userId) and password=(:password)")
    //UserEntity login(String userId, String password);
}
