package com.udacity.asteroidradar

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun getToday(): String {
    val calendar = Calendar.getInstance()
    return formatDate(calendar.time)
}

fun getFinalDate(): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, 7)
    return formatDate(calendar.time)
}

private fun formatDate(date: Date): String {
    val dateFormat = SimpleDateFormat("yyyy-mm-dd", Locale.getDefault())
    return dateFormat.format(date)
}

fun displayPictureOfDay(imageView: ImageView, pictureOfDay: PictureOfDay) {
    val context = imageView.context
    pictureOfDay.let {
        if (it.mediaType == "image"){
            Picasso.with(context)
                .load(it.url)
                .into(imageView)

            imageView.contentDescription = String.format(
                context.getString(R.string.nasa_picture_of_day_content_description_format),
                pictureOfDay.title
            )
        }
    }
}