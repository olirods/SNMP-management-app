package com.example.clientesnmp;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface LogDao {

    @Insert
    void insertLog(LogEntity logEntity);

    @Query("SELECT * from logs where id_u=(:id_u)")
    List<LogEntity> getLogsFromUser(Integer id_u);
}
