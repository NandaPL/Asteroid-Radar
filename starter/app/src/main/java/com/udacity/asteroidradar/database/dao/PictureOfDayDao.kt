package com.udacity.asteroidradar.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay

@Dao
interface PictureOfDayDao {
    @Query("""SELECT * FROM ${Constants.PICTURE_TABLE} LIMIT 1""")
    fun getPictureOfToday(): LiveData<PictureOfDay>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPictureOfDay(pictureOfDay: PictureOfDay)
}