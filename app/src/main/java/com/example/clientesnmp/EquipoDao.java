package com.example.clientesnmp;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface EquipoDao {

    @Insert
    void addEquipo(EquipoEntity equipoEntity);

    @Query("SELECT * from equipos where id_e=(:id_e)")
    EquipoEntity getEquipo(Integer id_e);

    @Query("SELECT * from equipos where IP=(:ip)")
    EquipoEntity getEquipo(String ip);

    @Query("SELECT * from equipos where id_u=(:id_u) and id_g=(:id_g)")
    List<EquipoEntity> getEquipos(Integer id_u, Integer id_g);

    @Query("SELECT * from equipos where id_u=(:id_u)")
    List<EquipoEntity> getEquipos(Integer id_u);

    @Query("UPDATE equipos SET online = :newOnline" +
            " WHERE id_e = :id_e")
    void updateOnline(Integer id_e, Integer newOnline);


}
