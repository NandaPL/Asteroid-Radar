package com.udacity.asteroidradar.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.AsteroidItemBinding

class AsteroidsAdapter(private val clickListener: AsteroidClickListener) : ListAdapter<Asteroid, AsteroidsAdapter.AsteroidViewHolder>(DiffCallback){

    class AsteroidClickListener(val clickListener: (asteroid: Asteroid) -> Unit) {
        fun onClick(asteroid: Asteroid) = clickListener(asteroid)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AsteroidViewHolder {
        return AsteroidViewHolder(AsteroidItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        val asteroid = getItem(position)
        holder.bind(asteroid, clickListener)
    }

    companion object DiffCallback: DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem == newItem
        }
    }

    class AsteroidViewHolder(private var binding: AsteroidItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(asteroid: Asteroid, clickListener: AsteroidClickListener){
            binding.asteroids = asteroid
            binding.clickListener = clickListener
            binding.executePendingBindings()
            if (asteroid.isPotentiallyHazardous) {
                binding.ivAsteroidHazard.contentDescription = binding.root.context.getString(R.string.is_hazardous)
            } else {
                binding.ivAsteroidHazard.contentDescription = binding.root.context.getString(R.string.is_not_hazardous)
            }
        }
    }
}