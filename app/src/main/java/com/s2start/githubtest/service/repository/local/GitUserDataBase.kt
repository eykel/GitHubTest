package com.s2start.githubtest.service.repository.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.s2start.githubtest.service.model.GitUser

@Database(entities = [GitUser::class], version = 1)
abstract class GitUserDataBase : RoomDatabase() {

    abstract fun userDAO() : UserDAO

    companion object{

        private lateinit var INSTANCE : GitUserDataBase

        fun getInstance(context: Context) : GitUserDataBase {
            if(!Companion::INSTANCE.isInitialized){
                synchronized(GitUserDataBase::class.java){
                    INSTANCE = Room.databaseBuilder(context, GitUserDataBase::class.java, "gitDB")
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return  INSTANCE
        }
    }
}