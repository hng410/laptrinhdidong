package com.example.lab1.ui.theme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class bai3 : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            Bai3UI()
        }
    }
}


@Composable
fun Bai3UI() {

    // ===== 1. Tạo Set từ List =====
    val numbers = listOf(0, 3, 8, 4, 0, 5, 5, 8, 9, 2)
    val setOfNumbers = numbers.toSet()

    // ===== 2. Set =====
    val set1 = setOf(1, 2, 3)
    val set2 = mutableSetOf(3, 4, 5)

    // ===== 3. Map =====
    val peopleAges = mutableMapOf(
        "Fred" to 30,
        "Ann" to 23
    )
    peopleAges["Barbara"] = 42
    peopleAges["Joe"] = 51

    // ===== 4. Map operations =====
    val mapLoopResult = peopleAges.map { "${it.key} is ${it.value}" }
    val filteredNames = peopleAges.filter { it.key.length < 4 }

    // ===== 5. Collection khác =====
    val words = listOf("about", "acute", "balloon", "best", "brief", "class")
    val filteredWords = words
        .filter { it.startsWith("b", ignoreCase = true) }
        .shuffled()
        .take(2)
        .sorted()

    // ===== 6. Lambda =====
    val triple: (Int) -> Int = { it * 3 }

    // ===== 7. Elvis =====
    val quantity: Int? = null

    // ===== UI =====
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        Text(
            text = "BÀI TẬP KOTLIN – COLLECTIONS & KHÁC",
            style = MaterialTheme.typography.titleLarge
        )

        Divider()

        Text("1. Set từ List:")
        Text(setOfNumbers.toString())

        Text("2. Set operations:")
        Text("Intersect: ${set1.intersect(set2)}")
        Text("Union: ${set1.union(set2)}")

        Divider()

        Text("3. Map peopleAges:")
        peopleAges.forEach {
            Text("${it.key} : ${it.value}")
        }

        Divider()

        Text("4. Lặp & chuyển đổi Map:")
        mapLoopResult.forEach {
            Text(it)
        }

        Divider()

        Text("5. Lọc Map (tên < 4 ký tự):")
        Text(filteredNames.toString())

        Divider()

        Text("6. Các phép toán khác trên Collection:")
        Text(filteredWords.toString())

        Divider()

        Text("7. Lambda:")
        Text("triple(5) = ${triple(5)}")

        Divider()

        Text("8. Toán tử Elvis:")
        Text("quantity ?: 0 = ${quantity ?: 0}")
    }
}
