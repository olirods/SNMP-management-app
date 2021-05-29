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

    @Query("SELECT * from users where id=(:id)")
    UserEntity getUser(Integer id);

    @Query("UPDATE users SET password = :newPass" +
            " WHERE id = :id")
    void updatePass(Integer id, String newPass);
}
