package com.example.geoquiz

import QuizViewModel
import android.app.Activity
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
//import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.util.Log
//import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.example.geoquiz.databinding.ActivityMainBinding

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val quizViewModel: QuizViewModel by viewModels()

    private val cheatLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Handle the result
        if (result.resultCode == Activity.RESULT_OK) {
            quizViewModel.isCheater =
                result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")

        binding.trueButton.setOnClickListener { view: View ->
            onAnswerSelected(true)
        }

        binding.falseButton.setOnClickListener { view: View ->
            onAnswerSelected(false)
        }

        binding.nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
            resetButtons()
        }

        binding.cheatButton.setOnClickListener {
            // Start CheatActivity
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            cheatLauncher.launch(intent)
        }

        updateQuestion()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            blurCheatButton()
        }

    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }
    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        binding.questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer

        val messageResId = when {
            quizViewModel.isCheater -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun blurCheatButton() {
        val effect = RenderEffect.createBlurEffect(
            10.0f,
            10.0f,
            Shader.TileMode.CLAMP
        )
        binding.cheatButton.setRenderEffect(effect)
    }

    private fun onAnswerSelected(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer

        binding.trueButton.isEnabled = false
        binding.falseButton.isEnabled = false

        val messageResId = if (userAnswer == correctAnswer) {
            R.string.correct_toast
        } else {
            R.string.incorrect_toast
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    private fun resetButtons() {
        binding.trueButton.isEnabled = true
        binding.falseButton.isEnabled = true
    }

}