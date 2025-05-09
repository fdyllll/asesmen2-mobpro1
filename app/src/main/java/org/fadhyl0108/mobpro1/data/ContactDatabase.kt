package org.fadhyl0108.mobpro1.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.fadhyl0108.mobpro1.model.Contact
import org.fadhyl0108.mobpro1.data.entity.RecycleBin
import org.fadhyl0108.mobpro1.data.dao.RecycleBinDao

@Database(entities = [Contact::class, RecycleBin::class], version = 2, exportSchema = false)
abstract class ContactDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun recycleBinDao(): RecycleBinDao

    companion object {
        @Volatile
        private var INSTANCE: ContactDatabase? = null

        fun getDatabase(context: Context): ContactDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactDatabase::class.java,
                    "contact_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 