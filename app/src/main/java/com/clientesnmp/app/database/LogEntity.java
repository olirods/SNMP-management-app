package com.clientesnmp.app.database;

import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import static androidx.room.ForeignKey.CASCADE;

/**
 * Clase entidad de un mensaje de log dentro de la base de datos. Sus atributos son las columnas de
 * la tabla en SQL. Contiene métodos para acceder a ellos ya que se mantienen privados dentro de
 * la clase.
 *
 * Contiene una ForeignKey (Clave Externa) con referencia a la tabla usuarios para preservar la
 * coherencia de la información.
 *
 * Requiere un TypeConverter (Conversor de tipo) ya que el tipo de Objeto "Date" de Java no es
 * compatible con SQL y requiere que sea convertido a Long para guardarse en la base de datos.
 */
@Entity(tableName = "logs",
        foreignKeys = {
                @ForeignKey(entity = UserEntity.class,
                        parentColumns = "id",
                        childColumns = "id_u",
                        onDelete = CASCADE)})
@TypeConverters({TimestampConverter.class})
public class LogEntity {

    @PrimaryKey(autoGenerate = true)
    private Integer id; // Identificador de un mensaje de log concreto

    @ColumnInfo(name = "created_date")
    private Date createDate; // Fecha y hora en la que fue generado el mensaje de log

    @ColumnInfo(name = "message")
    private String message; // Contenido del mensaje de log

    @ColumnInfo(name = "id_u", index = true)
    private Integer id_u; // Identificador del usuario al que pertenece

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getId_u() {
        return id_u;
    }

    public void setId_u(Integer id_u) {
        this.id_u = id_u;
    }
}
