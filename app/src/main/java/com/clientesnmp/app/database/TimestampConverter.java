package com.clientesnmp.app.database;

import java.util.Date;

import androidx.room.TypeConverter;

/**
 * Conversor de tipo de "Time" y "Long". Transforma la fecha y hora concreta a un entero de tipo
 * Long para que sea compatible con SQL. Necesario para el atributo "created_date" de la entidad Log
 */
public class TimestampConverter {
    @TypeConverter
    public static Date fromDate(Long dateLong) {
        return dateLong == null ? null : new Date(dateLong);
    }

    @TypeConverter
    public static Long toDate(Date date) {
        return date == null ? null : date.getTime();
    }

}
