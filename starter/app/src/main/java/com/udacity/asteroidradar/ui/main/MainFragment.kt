package com.udacity.asteroidradar.ui.main

import android.os.Bundle
import android.util.Log
import androidx.navigation.fragment.findNavController
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.udacity.asteroidradar.AsteroidsWorker
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.displayPictureOfDay

class MainFragment : Fragment() {
    private val TAG = MainFragment::class.java.name

    private lateinit var asteroidAdapter: AsteroidsAdapter
    private lateinit var dataBinding: FragmentMainBinding
    private lateinit var mViewModel: MainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AsteroidsWorker.setupPeriodicWork(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_main,
            container,
            false
        )
        setHasOptionsMenu(true)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        mViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        mViewModel.pictureOfDay.observe(viewLifecycleOwner) { pictureOfTheDay ->
            pictureOfTheDay?.let {
                dataBinding.activityMainImageOfTheDay.let { imageView ->
                    displayPictureOfDay(imageView, it)
                }
            } ?: Log.e(TAG, "pictureOfDay is null")
        }
        asteroidAdapter = AsteroidsAdapter(AsteroidsAdapter.AsteroidClickListener { asteroid ->
            mViewModel.onAsteroidClicked(asteroid)
        })
        dataBinding.asteroidRecycler.adapter = asteroidAdapter
        observers()

        return dataBinding.root
    }

    private fun observers(){
        mViewModel.currentAsteroids.observe(viewLifecycleOwner) {
            if (it != null) {
                asteroidAdapter.submitList(it)
            }
        }

        mViewModel.navigateToAsteroidDetail.observe(viewLifecycleOwner) { asteroid ->
            if (asteroid != null){
                val action = MainFragmentDirections.actionShowDetail(asteroid)
                findNavController().navigate(action)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_week_asteroids -> mViewModel.getAsteroidsOfWeek()
            R.id.show_today_asteroids -> mViewModel.getTodayAsteroids()
            R.id.show_saved_asteroids -> mViewModel.saveAsteroids()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}
