package com.example.calculatorapp_233381

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.view.View
import android.widget.Button
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.calculatorapp_233381.databinding.ActivityMainBinding
import com.example.calculatorapp_233381.ui.theme.CalculatorApp_233381Theme
import java.nio.file.attribute.AclFileAttributeView
import android.media.MediaPlayer
import android.content.Intent
import android.net.Uri

class MainActivity : ComponentActivity() {

    private var canAddOperation = false
    private var canAddDecimal = true
    private lateinit var binding: ActivityMainBinding
    private var mediaPlayer: MediaPlayer? = null
    private var mediaPlayer2: MediaPlayer? = null
    private var mediaPlayer3: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val dialButton: Button = findViewById(R.id.callButton)
        dialButton.setOnClickListener { callResult(it) }

        mediaPlayer = MediaPlayer.create(this, R.raw.sound1)
        mediaPlayer2 = MediaPlayer.create(this, R.raw.sound2)
        mediaPlayer3 = MediaPlayer.create(this, R.raw.sound3)
    }

     fun playSound1() {
        mediaPlayer?.start()
    }

    fun playSound2() {
        mediaPlayer2?.start()
    }

    fun playSound3() {
        mediaPlayer3?.start()
    }

    fun numberAction(view: View) {
        if (view is Button) {
            if (view.text == ".") {
                if (canAddDecimal) {
                    binding.workingsTV.append(view.text)
                }
                canAddDecimal = false
            } else {
                binding.workingsTV.append(view.text)
            }

            playSound1()
            canAddOperation = true
        }
    }

    fun operationAction(view: View) {
        if (view is Button && canAddOperation) {
            binding.workingsTV.append(view.text)
            canAddOperation = false
            canAddDecimal = true
        }
        playSound2()
    }

    fun allClearAction(view: View) {
        playSound2()
        binding.workingsTV.text = ""
        binding.resultsTV.text = ""
    }

    fun backSpaceAction(view: View) {
        val length = binding.workingsTV.length()
        if (length > 0) {
            playSound2()
            binding.workingsTV.text = binding.workingsTV.text.subSequence(0, length - 1)
        }
    }

    fun equalsAction(view: View) {
        playSound3()

        val workingText = binding.workingsTV.text.toString()
        if (workingText == "." || workingText == "x" || workingText == "/" || workingText == "-" || workingText == "+") {
            binding.resultsTV.text = "ඞ"
        } else {
            binding.resultsTV.text = calculateResults()
        }
    }

    private fun calculateResults(): String {
        val digitsOperators = digitsOperators()

        if(digitsOperators.isEmpty()) return ""

        val timesDivision = timesDivisionCalculate(digitsOperators)
        if(timesDivision.isEmpty()) return ""

        val result = addSubtractCalculate(timesDivision)
        return result.toString()
    }

    fun callResult(view: View) {
        val result = binding.resultsTV.text.toString()
        if (result.isNotEmpty()) {
            val numTelefono = result.replace("\\.?0*$".toRegex(), "")
            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$numTelefono"))
            startActivity(callIntent)
        }
    }

    private fun addSubtractCalculate(passedList: MutableList<Any>): Float {
        var result = passedList[0] as Float

        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex) {
                val operator = passedList[i]
                val nextDigit = passedList[i + 1] as Float
                if (operator == '+')
                    result += nextDigit
                if (operator == '-')
                    result -= nextDigit
            }
        }

        return result
    }

    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList
        while (list.contains('x') || list.contains('/')) {
            list = calcTimesDiv(list)
        }
        return list
    }

    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size

        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex && i < restartIndex) {
                val operator = passedList[i]
                val prevDigit = passedList[i - 1] as Float
                val nextDigit = passedList[i + 1] as Float
                when (operator) {
                    'x' -> {
                        newList.add(prevDigit * nextDigit)
                        restartIndex = i + 1
                    }
                    '/' -> {
                        newList.add(prevDigit / nextDigit)
                        restartIndex = i + 1
                    }
                    else -> {
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }

            if (i > restartIndex)
                newList.add(passedList[i])
        }

        return newList
    }

    private fun digitsOperators(): MutableList<Any> {
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for (character in binding.workingsTV.text) {
            if (character.isDigit() || character == '.')
                currentDigit += character
            else {
                list.add(currentDigit.toFloat())
                currentDigit = ""
                list.add(character)
            }
        }

        if (currentDigit != "")
            list.add(currentDigit.toFloat())

        return list
    }
}
