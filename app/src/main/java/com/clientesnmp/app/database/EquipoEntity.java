package com.clientesnmp.app.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

/**
 * Clase entidad de un equipo dentro de la base de datos. Sus atributos son las columnas de
 * la tabla en SQL. Contiene métodos para acceder a ellos ya que se mantienen privados dentro de
 * la clase.
 *
 * Contiene una ForeignKey (Clave Externa) con referencia a la tabla usuarios para preservar la
 * coherencia de la información.
 */
@Entity(tableName = "equipos",
        foreignKeys = {
        @ForeignKey(entity = UserEntity.class,
            parentColumns = "id",
            childColumns = "id_u",
            onDelete = CASCADE)})
public class EquipoEntity {

    @PrimaryKey(autoGenerate = true)
    private Integer id; // Identificador del equipo

    @ColumnInfo(name = "IP")
    private String IP; // Dirección IP del equipo para monitorizarlo por SNMP

    @ColumnInfo(name = "nombre_e")
    private String nombre_e; // Nombre personal del equipo

    @ColumnInfo(name = "v_snmp")
    private Integer v_snmp; // Versión de SNMP compatible con el equipo

    @ColumnInfo(name = "online")
    private Integer online; // Estado actual del equipo (0-offline, 1-online)

    @ColumnInfo(name = "id_g")
    private Integer id_g; // Identificador de grupo (Servidores-Switches-Firewalls, en ese orden)

    @ColumnInfo(name = "id_u", index = true)
    private Integer id_u; // Identificador del usuario asociado al equipo

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
