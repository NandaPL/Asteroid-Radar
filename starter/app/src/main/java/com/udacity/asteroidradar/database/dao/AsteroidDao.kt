package com.udacity.asteroidradar.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants

@Dao
interface AsteroidDao {
    @Query("""SELECT * FROM ${Constants.ASTEROID_TABLE} ORDER BY date(closeApproachDate) ASC""")
    fun getAllAsteroids(): LiveData<List<Asteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(asteroids: List<Asteroid>)

    @Query("""SELECT * FROM ${Constants.ASTEROID_TABLE} WHERE closeApproachDate >= :date ORDER BY date(closeApproachDate) ASC""")
    fun getAsteroidsOfWeek(date: String): LiveData<List<Asteroid>>

    @Query("""SELECT * FROM ${Constants.ASTEROID_TABLE} WHERE closeApproachDate=:day ORDER BY closeApproachDate DESC""")
    fun getTodayAsteroids(day: String): LiveData<List<Asteroid>>
}