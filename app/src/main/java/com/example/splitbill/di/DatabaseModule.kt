package com.example.splitbill.di

import android.content.Context
import androidx.room.Room
import com.example.splitbill.data.local.dao.BillDao
import com.example.splitbill.data.local.dao.BillParticipantDao
import com.example.splitbill.data.local.dao.ExpenseDao
import com.example.splitbill.data.local.dao.FriendDao
import com.example.splitbill.data.local.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideFriendDao(db: AppDatabase): FriendDao = db.friendDao()

    @Provides
    fun provideBillDao(db: AppDatabase): BillDao = db.billDao()

    @Provides
    fun provideBillParticipantDao(db: AppDatabase): BillParticipantDao = db.billParticipantDao()

    @Provides
    fun provideExpenseDao(db: AppDatabase): ExpenseDao = db.expenseDao()
}
