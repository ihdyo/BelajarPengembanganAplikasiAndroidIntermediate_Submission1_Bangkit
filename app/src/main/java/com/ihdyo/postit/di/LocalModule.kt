package com.ihdyo.postit.di

import android.content.Context
import androidx.room.Room
import com.ihdyo.postit.data.local.StoryAppDatabase
import com.ihdyo.postit.data.local.dao.StoryDao
import com.ihdyo.postit.utils.ConstVal.DB_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): StoryAppDatabase {
        return Room.databaseBuilder(context, StoryAppDatabase::class.java, DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideStoryDao(database: StoryAppDatabase): StoryDao = database.getStoryDao()

}