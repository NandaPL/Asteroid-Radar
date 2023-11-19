package com.udacity.asteroidradar.database.repository

import androidx.lifecycle.LiveData
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApiService
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.getFinalDate
import com.udacity.asteroidradar.getToday
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepository(private val asteroidApi: AsteroidApiService, private val database: AsteroidDatabase) {
    private val asteroidDAO = database.asteroidDao()

    val asteroids: LiveData<List<Asteroid>> = asteroidDAO.getAllAsteroids()

    suspend fun refreshAsteroids(startDate: String = getToday(), endDate: String = getFinalDate()){
        var asteroidList: ArrayList<Asteroid>
        withContext(Dispatchers.IO) {
            val asteroidsResponseBody = asteroidApi.getAllAsteroids(
                Constants.API_KEY,
                startDate, endDate
            )

            asteroidsResponseBody.body()?.let {
                asteroidList = parseAsteroidsJsonResult(JSONObject(asteroidsResponseBody.toString()))
                asteroidDAO.insertAll(asteroidList)
            }
        }
    }

    fun getAsteroidsOfTheWeek(date: String): LiveData<List<Asteroid>> {
        return asteroidDAO.getAsteroidsOfWeek(date)
    }

    fun getAsteroidsOfTheDay(todayDate: String): LiveData<List<Asteroid>> {
        return asteroidDAO.getTodayAsteroids(todayDate)
    }
}

class PictureOfDayRepository(private val asteroidApiService: AsteroidApiService, private val database: AsteroidDatabase){
    private val pictureDAO = database.pictureOfDayDao()

    val todayPicture: LiveData<PictureOfDay> = pictureDAO.getPictureOfToday()

    suspend fun refreshPictureOfThisDay() {
        withContext(Dispatchers.IO) {
            val pictureResponseBody = asteroidApiService.getPictureOfTheDay(Constants.API_KEY)
            pictureResponseBody.body()?.let {
                pictureDAO.insertPictureOfDay(it)
            }
        }
    }

}