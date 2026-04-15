package com.example.woof

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.woof.data.Dog
import com.example.woof.data.dogs
import com.example.woof.ui.theme.WoofTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WoofTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WoofApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WoofApp() {
    // Scaffold cung cấp cấu trúc chuẩn gồm thanh TopBar và phần nội dung
    Scaffold(
        topBar = {
            WoofTopAppBar()
        }
    ) { it ->
        LazyColumn(contentPadding = it) {
            items(dogs) {
                DogItem(
                    dog = it,
                    modifier = Modifier.padding(8.dp) // Khoảng cách giữa các thẻ
                )
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun WoofTopAppBar(modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    modifier = Modifier
                        .size(64.dp)
                        .padding(8.dp),
                    painter = painterResource(R.drawable.ic_woof_logo), // Đảm bảo bạn có icon chân chó trong drawable
                    contentDescription = null
                )
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.displayLarge
                )
            }
        },
        modifier = modifier
    )
}

@Composable
fun DogItem(
    dog: Dog,
    modifier: Modifier = Modifier
) {
    // PHẢI CÓ CARD THÌ MỚI THẤY MÀU NỀN CỦA MỤC
    Card(
        modifier = modifier.padding(8.dp),
        // Bạn có thể ép màu thủ công để kiểm tra:
        // colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DogIcon(dog.imageResourceId)
            DogInformation(dog.name, dog.age)
            Spacer(modifier = Modifier.weight(1f))
            DogItemButton(expanded = false, onClick = {})
        }
    }
}
@Composable
private fun DogItemButton(expanded: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Filled.ExpandMore,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun DogIcon(@DrawableRes dogIcon: Int) {
    Image(
        modifier = Modifier
            .size(64.dp)
            .padding(8.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop,
        painter = painterResource(dogIcon),
        contentDescription = null
    )
}

@Composable
fun DogInformation(@StringRes dogName: Int, dogAge: Int) {
    Column {
        Text(
            text = stringResource(dogName),
            style = MaterialTheme.typography.displayMedium
        )
        Text(
            text = stringResource(R.string.years_old, dogAge),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

// Preview để xem trước cả 2 chế độ
@Preview(showBackground = true)
@Composable
fun WoofLightPreview() {
    WoofTheme(darkTheme = false) {
        WoofApp()
    }
}

@Preview(showBackground = true)
@Composable
fun WoofDarkPreview() {
    WoofTheme(darkTheme = true) {
        WoofApp()
    }
}
