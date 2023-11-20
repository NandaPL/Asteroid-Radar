package com.udacity.asteroidradar.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.repository.AsteroidRepository
import com.udacity.asteroidradar.database.repository.PictureOfDayRepository
import com.udacity.asteroidradar.getFinalDate
import com.udacity.asteroidradar.getToday
import kotlinx.coroutines.launch

class MainViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val asteroidDatabase = AsteroidDatabase.getDatabase(application)
    private val asteroidApi = AsteroidApi.retrofitService
    private val pictureOfDayRepository = PictureOfDayRepository(asteroidApi, asteroidDatabase)
    private val asteroidRepository = AsteroidRepository(asteroidApi, asteroidDatabase)

    private val _navigateToAsteroidDetail = MutableLiveData<Asteroid>()
    val navigateToAsteroidDetail: MutableLiveData<Asteroid>
        get() = _navigateToAsteroidDetail

    private val _currentAsteroids = MutableLiveData<List<Asteroid>>()
    val currentAsteroids: LiveData<List<Asteroid>>
        get() = _currentAsteroids

    val pictureOfDay = pictureOfDayRepository.todayPicture

    init {
        getAsteroidsOfWeek()
        viewModelScope.launch {
            refreshAsteroids(getToday(), getFinalDate())
            refreshPicture()
        }
    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToAsteroidDetail.value = asteroid
    }

    fun saveAsteroids() {
        viewModelScope.launch {
            asteroidRepository.asteroids.observeForever {
                _currentAsteroids.value = it
            }
        }
    }

    private suspend fun refreshPicture() {
        viewModelScope.launch {
            pictureOfDayRepository.refreshPictureOfThisDay()
        }
    }

    fun getAsteroidsOfWeek() {
        viewModelScope.launch {
            asteroidRepository.getAsteroidsOfTheWeek(getToday()).observeForever {
                _currentAsteroids.value = it
            }
        }
    }

    fun getTodayAsteroids() {
        viewModelScope.launch {
            asteroidRepository.getAsteroidsOfTheDay(getToday()).observeForever {
                _currentAsteroids.value = it
            }
        }
    }

    fun refreshAsteroids(startDate: String, endDate: String) {
        viewModelScope.launch {
            asteroidRepository.refreshAsteroids(startDate, endDate)
        }

    }
}