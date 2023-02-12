package com.udacity.asteroidradar.main

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.*
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


    val pictureOfDay = asteroidsRepository.picture

    private val itemId = MutableLiveData<Int>()

    val itemData: LiveData<List<Asteroid>> = Transformations.switchMap(itemId) { id ->
        asteroidsRepository.getItem(id)
    }

    val asteroids = itemData

    fun loadData(id: Int) {
        itemId.value = id
        Log.i("MainViewModel", "loadData itemId: $id")
    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToDetail.value = asteroid
    }

    fun onAsteroidDetailNavigated() {
        _navigateToDetail.value = null
    }

    fun updatePicture(imageView: ImageView, context: Context) {
        val picasso = Picasso.Builder(context).build()
        picasso.load(pictureOfDay.value?.url).into(imageView)
    }
}
