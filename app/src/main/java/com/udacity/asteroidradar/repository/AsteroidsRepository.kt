package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.asDatabaseModel
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidsRepository(private val database: AsteroidsDatabase) {

    // By default shows all asteroids
    lateinit var asteroids: LiveData<List<Asteroid>>

    val picture: LiveData<PictureOfDay> = Transformations.map(
        database.pictureOfDayDao.getPictureOfDay()
    ) {
        it?.let { it.asDomainModel() }
    }

    init {
        transformAsteroids()
    }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val response = AsteroidApi.retrofitService.getAsteroids()
            val asteroidList: MutableList<Asteroid> =
                parseAsteroidsJsonResult(JSONObject(response.body()))
            Log.i("AsteroidsRepository", "asteroidList: $asteroidList")
            database.asteroidDao.insertAsteroids(*asteroidList.asDatabaseModel())
        }
    }

    suspend fun refreshPicture() {
        withContext(Dispatchers.IO) {
            val pictureOfDay = AsteroidApi.retrofitService.getPicture()
            Log.i("AsteroidsRepository", "picture: $pictureOfDay")
            database.pictureOfDayDao.insertPictureOfDay(pictureOfDay.asDatabaseModel())
        }
    }

    private fun transformAsteroids(filter: LiveData<List<DatabaseAsteroid>> = getSavedAsteroids()) {
        asteroids = Transformations.map(filter) {
            it.asDomainModel()
        }
    }

    private fun getTodayAsteroids(): LiveData<List<DatabaseAsteroid>> {
        Log.i("AsteroidsRepository", "getTodayAsteroids")
        return database.asteroidDao.getTodayAsteroids()
    }

    private fun getWeekAsteroids(): LiveData<List<DatabaseAsteroid>> {
        Log.i("AsteroidsRepository", "getWeekAsteroids")
        return database.asteroidDao.getWeekAsteroids()
    }

    private fun getSavedAsteroids(): LiveData<List<DatabaseAsteroid>> {
        Log.i("AsteroidsRepository", "getSavedAsteroids")
        return database.asteroidDao.getAsteroids()
    }

    fun setAsteroids(itemId: Int) {
        when (itemId) {
            R.id.show_today -> transformAsteroids(getTodayAsteroids())
            R.id.show_week -> transformAsteroids(getWeekAsteroids())
            R.id.show_saved -> transformAsteroids(getSavedAsteroids())
        }
    }
}
