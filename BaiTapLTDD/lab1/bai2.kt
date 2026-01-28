package com.example.lab1.ui.theme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import kotlin.math.PI

class bai2 : ComponentActivity () {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            Column {

                // ===== LỚP & KẾ THỪA =====
                val cabin = SquareCabin(6)
                Text("Material: ${cabin.buildingMaterial}")
                Text("Floor area: ${cabin.floorArea()}")

                // with
                with(cabin) {
                    Text("Capacity: $capacity")
                    Text("Has room? ${hasRoom()}")
                }

                // ===== DANH SÁCH =====
                val numbers = listOf(1, 2, 3, 4, 5, 6)
                Text("List size: ${numbers.size}")
                Text("First element: ${numbers[0]}")
                Text("Reversed: ${listOf("red", "blue", "green").reversed()}")

                val entrees = mutableListOf<String>()
                entrees.add("spaghetti")
                entrees[0] = "lasagna"
                entrees.remove("lasagna")
                Text("Entrees size: ${entrees.size}")

                // ===== VÒNG LẶP =====
                for (n in numbers) {
                    Text("For loop item: $n")
                }

                var index = 0
                while (index < numbers.size) {
                    Text("While loop item: ${numbers[index]}")
                    index++
                }

                // ===== CHUỖI =====
                val name = "Android"
                Text("Length: ${name.length}")

                val number = 10
                val groups = 5
                Text("$number people")
                Text("${number * groups} people")

                // ===== TOÁN TỬ GÁN =====
                var a = 10
                val b = 5
                a += b
                Text("a after += : $a")

                // ===== TOÁN HỌC =====
                val radius = 2.0
                Text("Circle area: ${kotlin.math.PI * radius * radius}")


                // ===== VARARG =====
                Text(addToppings("Cheese", "Tomato", "Onion"))
            }
        }
    }
}

//////////////////////////////////////////////////
// ===== LỚP TRỪU TƯỢNG =====
//////////////////////////////////////////////////

abstract class Dwelling(protected val residents: Int) {

    abstract val buildingMaterial: String
    abstract val capacity: Int

    abstract fun floorArea(): Double

    fun hasRoom(): Boolean {
        return residents < capacity
    }
}

//////////////////////////////////////////////////
// ===== LỚP CON =====
//////////////////////////////////////////////////

open class RoundHut(
    residents: Int,
    private val radius: Double
) : Dwelling(residents) {

    override val buildingMaterial = "Straw"
    override val capacity = 4

    override fun floorArea(): Double {
        return PI * radius * radius
    }
}

class SquareCabin(residents: Int) : Dwelling(residents) {

    override val buildingMaterial = "Wood"
    override val capacity = 6

    override fun floorArea(): Double {
        return 50.0
    }
}

//////////////////////////////////////////////////
// ===== VARARG FUNCTION =====
//////////////////////////////////////////////////

fun addToppings(vararg toppings: String): String {
    return "Toppings: ${toppings.joinToString()}"
}
