package com.example.clientesnmp;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface EquipoDao {

    @Insert
    void addEquipo(EquipoEntity equipoEntity);

    @Query("SELECT * from equipos where id_u=(:id_u)")
    List<EquipoEntity> getEquiposFromUser(Integer id_u);
}
