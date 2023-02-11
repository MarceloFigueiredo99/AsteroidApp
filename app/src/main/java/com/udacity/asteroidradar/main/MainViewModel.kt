package com.udacity.asteroidradar.main

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : ViewModel() {
    private val _navigateToDetail = MutableLiveData<Asteroid>()

    val navigateToDetail: LiveData<Asteroid>
        get() = _navigateToDetail

    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(database)

    init {
        viewModelScope.launch {
            try {
                asteroidsRepository.refreshAsteroids()
                asteroidsRepository.refreshPicture()
            } catch (e: Exception) {
                Log.i("MainViewModel", "Error refreshing data: ${e.message}")
            }
        }
    }

    val asteroids = asteroidsRepository.asteroids

    val pictureOfDay = asteroidsRepository.picture

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToDetail.value = asteroid
    }

    fun onAsteroidDetailNavigated() {
        _navigateToDetail.value = null
    }

    fun updateList(itemId: Int) {
        Log.i("MainViewModel", "setAsteroids itemId: $itemId")
        asteroidsRepository.setAsteroids(itemId)
    }

    fun updatePicture(imageView: ImageView, context: Context) {
        val picasso = Picasso.Builder(context).build()
        picasso.load(pictureOfDay.value?.url).into(imageView)
    }
}
