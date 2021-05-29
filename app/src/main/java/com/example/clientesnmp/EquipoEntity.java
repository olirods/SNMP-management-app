package com.example.clientesnmp;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "equipos",
        foreignKeys = {
        @ForeignKey(entity = UserEntity.class,
            parentColumns = "id",
            childColumns = "id_u",
            onDelete = CASCADE)})
public class EquipoEntity {

    @PrimaryKey(autoGenerate = true)
    private Integer id_e;

    @ColumnInfo(name = "IP")
    private String IP;

    @ColumnInfo(name = "nombre_e")
    private String nombre_e;

    @ColumnInfo(name = "v_snmp")
    private Integer v_snmp;

    @ColumnInfo(name = "online")
    private Integer online;

    @ColumnInfo(name = "id_g")
    private Integer id_g;

    @ColumnInfo(name = "id_u", index = true)
    private Integer id_u;

    public Integer getId_e() {
        return id_e;
    }

    public void setId_e(Integer id_e) {
        this.id_e = id_e;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getNombre_e() {
        return nombre_e;
    }

    public void setNombre_e(String nombre_e) {
        this.nombre_e = nombre_e;
    }

    public Integer getV_snmp() {
        return v_snmp;
    }

    public void setV_snmp(Integer v_snmp) {
        this.v_snmp = v_snmp;
    }

    public Integer getId_u() {
        return id_u;
    }

    public void setId_u(Integer id_u) {
        this.id_u = id_u;
    }

    public Integer getId_g() {
        return id_g;
    }

    public void setId_g(Integer id_g) {
        this.id_g = id_g;
    }

    public Integer getOnline() {
        return online;
    }

    public void setOnline(Integer online) {
        this.online = online;
    }
}
