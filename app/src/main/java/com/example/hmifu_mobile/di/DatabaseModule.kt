package com.example.hmifu_mobile.di

import android.content.Context
import androidx.room.Room
import com.example.hmifu_mobile.data.local.HmifDatabase
import com.example.hmifu_mobile.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing database dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): HmifDatabase = Room.databaseBuilder(
        context,
        HmifDatabase::class.java,
        "hmif_database"
    ).build()

    @Provides
    @Singleton
    fun provideUserDao(database: HmifDatabase): UserDao = database.userDao()
}
