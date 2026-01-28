package com.example.lab1.ui.theme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text

class bai1 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val age: Int = 5
            val name: String = "Rover"

            var roll: Int = 6


            val randomNumber = rollDice()

            Column {


                Text(text = "Hello, world!")
                Text(text = "This is the text to print!")

                Text(text = "You are already $age!")
                Text(text = "You are already $age days old, $name!")

                Text(text = printHello())

                Text(text = printBorder("*", 10))

                val sum = 3 + 5
                val isGreater = sum > 5
                Text(text = "Sum = $sum")
                Text(text = "Is sum > 5? $isGreater")

                Text(text = "Random dice number: $randomNumber")
            }
        }
    }
}


fun printHello(): String {
    return "Hello Kotlin"
}


fun printBorder(border: String, timesToRepeat: Int): String {
    return border.repeat(timesToRepeat)
}


fun rollDice(): Int {
    return (1..6).random()
}
