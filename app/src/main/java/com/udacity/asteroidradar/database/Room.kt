package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AsteroidDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAsteroids(vararg asteroids: DatabaseAsteroid)

    @Query("SELECT * FROM DatabaseAsteroid ORDER BY closeApproachDate")
    fun getAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM DatabaseAsteroid WHERE closeApproachDate = DATE('now')")
    fun getTodayAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM DatabaseAsteroid WHERE closeApproachDate BETWEEN DATE('now') AND DATE('now', '7 days')")
    fun getWeekAsteroids(): LiveData<List<DatabaseAsteroid>>
}

@Dao
interface PictureOfDayDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPictureOfDay(vararg picture: DatabasePicture)

    @Query("SELECT * FROM DatabasePicture WHERE date = DATE('now')")
    fun getPictureOfDay(): LiveData<DatabasePicture>
}


@Database(entities = [DatabaseAsteroid::class, DatabasePicture::class], version = 1)
abstract class AsteroidsDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
    abstract val pictureOfDayDao: PictureOfDayDao
}

private lateinit var instance: AsteroidsDatabase

fun getDatabase(context: Context): AsteroidsDatabase {
    if (!::instance.isInitialized) {
        instance = Room.databaseBuilder(
            context.applicationContext,
            AsteroidsDatabase::class.java,
            "asteroids"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    return instance
}
