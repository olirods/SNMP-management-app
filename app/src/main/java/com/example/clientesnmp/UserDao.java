package com.example.clientesnmp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {

    @Insert
    void registerUser(UserEntity userEntity);

    @Query("SELECT * from users where userId=(:userId) and password=(:password)")
    UserEntity login(String userId, String password);

    @Query("SELECT * from users where id_u=(:id_u)")
    UserEntity getUser(Integer id_u);
}
