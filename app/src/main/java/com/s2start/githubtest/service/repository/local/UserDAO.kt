package com.s2start.githubtest.service.repository.local

import androidx.room.*
import com.s2start.githubtest.service.model.GitUser


@Dao
interface UserDAO {

    @Insert
    fun insert(user: GitUser) : Long

    @Delete
    fun delete(user: GitUser) : Int

    @Query("UPDATE USer SET nickName = :nick, avatar = :avatar  WHERE id = :id")
    fun updateFields(nick: String, avatar: String, id: Int) : Int

    @Query("SELECT * FROM User WHERE id = :id")
    fun get(id: Int) : GitUser

    @Query("SELECT * FROM User")
    fun getAll() : MutableList<GitUser>

    @Query("SELECT * FROM User where favorite = 1")
    fun getFavoriteList(): MutableList<GitUser>

    @Query("SELECT * FROM User where id = :id")
    fun getFavorite(id: Int): GitUser
}