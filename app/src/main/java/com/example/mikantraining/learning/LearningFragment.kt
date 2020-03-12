package com.example.mikantraining.learning

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.mikantraining.R
import com.example.mikantraining.database.ExerciseDatabase
import com.example.mikantraining.databinding.FragmentLearningBinding
import java.io.IOException

/**
 * A simple [Fragment] subclass.
 */
class LearningFragment : Fragment() {
    private lateinit var binding: FragmentLearningBinding
    private lateinit var learningViewModel: LearningViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_learning,
            container,
            false
        )
        val application = requireNotNull(this.activity).application
        val dataSource = ExerciseDatabase.getInstance(application).exerciseDao
        val viewModelFactory = LearningFactory(dataSource, application)
        learningViewModel = ViewModelProvider(this, viewModelFactory).get(LearningViewModel::class.java)

        binding.learningViewModel = learningViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        learningViewModel.eventGameFinish.observe(viewLifecycleOwner, Observer<Boolean> { hasFinished ->
            if (hasFinished) gameFinished()
        })
        learningViewModel.eventCorrect.observe(viewLifecycleOwner, Observer<Boolean> { isCorrect ->
            if (isCorrect) judgeView(0)
            else judgeView(2)
        })
        learningViewModel.eventIncorrect.observe(viewLifecycleOwner, Observer<Boolean> { isIncorrect ->
            if (isIncorrect) judgeView(1)
            else judgeView(2)
        })
        binding.correctImageView.setImageBitmap(getImage("correct_image.png"))
        binding.incorrectImageView.setImageBitmap(getImage("incorrect_image.png"))

        return binding.root
    }

    override fun onPause() {
        learningViewModel.handler.removeCallbacks(learningViewModel.runnable)
        super.onPause()

    }

    private fun getImage(name: String): Bitmap? {
        val application = requireNotNull(this.activity).application
        var bitmap: Bitmap? = null
        try {
            val istr = application.assets.open(name)
            bitmap =  BitmapFactory.decodeStream(istr)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }

    private fun gameFinished() {
        val action = LearningFragmentDirections.actionLearningFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    private fun judgeView(type: Int) {
        when(type) {
            0 -> {
                binding.answer1Button.visibility = Button.GONE
                binding.answer2Button.visibility = Button.GONE
                binding.answer3Button.visibility = Button.GONE
                binding.answer4Button.visibility = Button.GONE
                binding.questionTextView.visibility = TextView.GONE
                binding.correctImageView.visibility = ImageView.VISIBLE
                binding.incorrectImageView.visibility = ImageView.GONE
            }
            1 -> {
                binding.answer1Button.visibility = Button.GONE
                binding.answer2Button.visibility = Button.GONE
                binding.answer3Button.visibility = Button.GONE
                binding.answer4Button.visibility = Button.GONE
                binding.questionTextView.visibility = TextView.GONE
                binding.correctImageView.visibility = ImageView.GONE
                binding.incorrectImageView.visibility = ImageView.VISIBLE
            }
            2 -> {
                binding.answer1Button.visibility = Button.VISIBLE
                binding.answer2Button.visibility = Button.VISIBLE
                binding.answer3Button.visibility = Button.VISIBLE
                binding.answer4Button.visibility = Button.VISIBLE
                binding.questionTextView.visibility = TextView.VISIBLE
                binding.correctImageView.visibility = ImageView.GONE
                binding.incorrectImageView.visibility = ImageView.GONE
            }
        }
    }
}
