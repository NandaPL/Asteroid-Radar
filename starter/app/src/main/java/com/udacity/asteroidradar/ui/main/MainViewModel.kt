package com.udacity.asteroidradar.ui.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.repository.AsteroidRepository
import com.udacity.asteroidradar.database.repository.PictureOfDayRepository
import com.udacity.asteroidradar.getToday
import kotlinx.coroutines.launch

class MainViewModel(
    application: Application
) : ViewModel() {
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
            asteroidRepository.refreshAsteroids()
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
}