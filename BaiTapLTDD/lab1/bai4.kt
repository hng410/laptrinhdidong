package com.example.lab1.ui.theme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import kotlin.random.Random

class bai4 : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                BaiCoroutineUI()
            }
        }
    }
}

@Composable
fun BaiCoroutineUI() {

    var result by remember { mutableStateOf("Chưa chạy") }
    var job by remember { mutableStateOf<Job?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "BÀI TẬP COROUTINE & KHÁC",
            style = MaterialTheme.typography.titleLarge
        )

        Divider()

        // ===== Coroutine launch =====
        Button(onClick = {
            job = CoroutineScope(Dispatchers.Main).launch {
                result = "Đang chạy..."
                try {
                    val value = getValue()
                    result = "Kết quả: $value"
                } catch (e: Exception) {
                    result = "Lỗi: ${e.message}"
                }
            }
        }) {
            Text("Chạy Coroutine")
        }

        // ===== Cancel Job =====
        Button(onClick = {
            job?.cancel()
            result = "Đã hủy Coroutine"
        }) {
            Text("Hủy Coroutine")
        }

        Divider()

        Text("Kết quả:")
        Text(result)

        Divider()

        // ===== Enum =====
        var direction by remember {
            mutableStateOf(Direction.NORTH)
        }

        Button(onClick = {
            direction = Direction.values().random()
        }) {
            Text("Đổi hướng")
        }

        when (direction) {
            Direction.NORTH -> Text("Hướng Bắc")
            Direction.SOUTH -> Text("Hướng Nam")
            Direction.WEST -> Text("Hướng Tây")
            Direction.EAST -> Text("Hướng Đông")
        }

        Divider()

        // ===== Object =====
        Text("Object DataProviderManager:")
        Text(DataProviderManager.name)
    }
}

//////////////////////////////////////////////////
// ===== Suspend function =====
suspend fun getValue(): Double {
    delay(2000) // giả lập tác vụ lâu
    return Random.nextDouble(0.0, 100.0)
}

// ===== Object =====
object DataProviderManager {
    val name = "Data Provider Ready"
}

// ===== Enum =====
enum class Direction {
    NORTH, SOUTH, WEST, EAST
}
