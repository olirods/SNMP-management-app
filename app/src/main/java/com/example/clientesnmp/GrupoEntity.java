package com.example.clientesnmp;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "grupos")
public class GrupoEntity {

    @PrimaryKey(autoGenerate = true)
    private Integer id_g;

    @ColumnInfo(name = "nombre_g")
    private String nombre_g;

    public Integer getId_g() {
        return id_g;
    }

    public void setId_g(Integer id_g) {
        this.id_g = id_g;
    }

    public String getNombre_g() {
        return nombre_g;
    }

    public void setNombre_g(String nombre_g) {
        this.nombre_g = nombre_g;
    }
}
