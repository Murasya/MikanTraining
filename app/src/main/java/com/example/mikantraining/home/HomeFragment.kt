package com.example.mikantraining.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.mikantraining.R
import com.example.mikantraining.database.ExerciseDatabase
import com.example.mikantraining.databinding.FragmentHomeBinding
import kotlinx.coroutines.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    private lateinit var viewModelFactory: HomeFactory
    private lateinit var homeViewModel: HomeViewModel
    private val job = Job()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val application = requireNotNull(this.activity).application
        val dataSource = ExerciseDatabase.getInstance(application).exerciseDao
        viewModelFactory = HomeFactory(dataSource)
        homeViewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)

        binding = FragmentHomeBinding.inflate(inflater,container, false)
        binding.lifecycleOwner = this
        binding.homeViewModel = homeViewModel

        binding.startButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToLearningFragment()
            findNavController().navigate(action)
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.Main + job).launch {
            homeViewModel.setValues()
            setView()
        }
    }

    private fun setView() {
        Log.d("HomeFragment", homeViewModel.correct.toFloat().toString())
        (binding.correctTextView.layoutParams as LinearLayout.LayoutParams).weight = homeViewModel.correct.toFloat()
        (binding.mistakeTextView.layoutParams as LinearLayout.LayoutParams).weight = homeViewModel.incorrect.toFloat()
        (binding.unlearnedTextView.layoutParams as LinearLayout.LayoutParams).weight = homeViewModel.unlearned.toFloat()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
