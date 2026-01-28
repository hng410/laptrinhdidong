package com.example.lab2.ui.theme


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab2.ui.theme.Lab2Theme

class HappyBirthday : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Gọi hàm giao diện Thiệp sinh nhật
                    BirthdayCardScreen(
                        message = "Happy Birthday, Nhung!",
                        from = "From Nhom",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun BirthdayCardScreen(message: String, from: String, modifier: Modifier = Modifier) {
    // Surface tạo nền màu cho thiệp
    Surface(
        color = Color(0xFFD0BCFF), // Màu tím nhạt (Hex code)
        modifier = modifier.fillMaxSize()
    ) {
        // Column sắp xếp các phần tử theo chiều dọc
        Column(
            verticalArrangement = Arrangement.Center, // Căn giữa nội dung theo chiều dọc
            horizontalAlignment = Alignment.CenterHorizontally, // Căn giữa theo chiều ngang
            modifier = Modifier.padding(16.dp)
        ) {
            // Lời chúc mừng sinh nhật
            Text(
                text = message,
                fontSize = 36.sp, // Kích thước chữ lớn
                lineHeight = 40.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Tên người gửi
            Text(
                text = from,
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.End) // Căn lề phải cho tên người gửi
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BirthdayCardPreview() {
    Lab2Theme {
        BirthdayCardScreen("Happy Birthday, Nhung!", "From Nhom")
    }
}
