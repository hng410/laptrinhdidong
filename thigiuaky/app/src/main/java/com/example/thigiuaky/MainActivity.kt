package com.example.thigiuaky

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thigiuaky.ui.theme.ThigiuakyTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            ThigiuakyTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF5F7FA)) {
                    MainAppNavigation()
                }
            }
        }
    }
}

// ================= CÁC HÀM XỬ LÝ FIREBASE =================
fun saveToFirestore(user: User) { Firebase.firestore.collection("Users").add(user) }
fun deleteUser(userId: String) { if (userId.isNotEmpty()) Firebase.firestore.collection("Users").document(userId).delete() }
fun updateUser(user: User) { if (user.id.isNotEmpty()) Firebase.firestore.collection("Users").document(user.id).set(user) }

// ================= ĐIỀU HƯỚNG =================
enum class Screen { LOGIN, REGISTER, ADMIN, USER_PROFILE }

@Composable
fun MainAppNavigation() {
    var currentScreen by remember { mutableStateOf(Screen.LOGIN) }
    var loggedInUser by remember { mutableStateOf<User?>(null) }

    when (currentScreen) {
        Screen.LOGIN -> LoginScreen(
            onLoginSuccess = { user ->
                loggedInUser = user
                currentScreen = if (user.role == "admin") Screen.ADMIN else Screen.USER_PROFILE
            },
            onNavigateToRegister = { currentScreen = Screen.REGISTER }
        )
        Screen.REGISTER -> RegisterScreen(
            onRegisterSuccess = { currentScreen = Screen.LOGIN },
            onNavigateToLogin = { currentScreen = Screen.LOGIN }
        )
        Screen.ADMIN -> UserListScreen(onLogout = { loggedInUser = null; currentScreen = Screen.LOGIN })
        Screen.USER_PROFILE -> {
            if (loggedInUser != null) {
                UserProfileScreen(currentUser = loggedInUser!!, onLogout = { loggedInUser = null; currentScreen = Screen.LOGIN })
            }
        }
    }
}

// ================= MÀN HÌNH ĐĂNG NHẬP =================
@Composable
fun LoginScreen(onLoginSuccess: (User) -> Unit, onNavigateToRegister: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text("CHÀO MỪNG TRỞ LẠI", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                OutlinedTextField(
                    value = username, onValueChange = { username = it }, label = { Text("Tên đăng nhập") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password, onValueChange = { password = it }, label = { Text("Mật khẩu") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = { IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null) } },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                )

                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = errorMessage, color = Color.Red, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        if (username.isEmpty() || password.isEmpty()) { errorMessage = "Vui lòng nhập đủ thông tin!"; return@Button }
                        isLoading = true; errorMessage = ""
                        Firebase.firestore.collection("Users").whereEqualTo("username", username).whereEqualTo("password", password).get()
                            .addOnSuccessListener { docs ->
                                isLoading = false
                                if (!docs.isEmpty) {
                                    val doc = docs.documents[0]
                                    val user = doc.toObject(User::class.java)
                                    if (user != null) { user.id = doc.id; Toast.makeText(context, "Xin chào ${user.username}!", Toast.LENGTH_SHORT).show(); onLoginSuccess(user) }
                                } else errorMessage = "Sai tài khoản hoặc mật khẩu!"
                            }.addOnFailureListener { isLoading = false; errorMessage = "Lỗi kết nối mạng!" }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp), enabled = !isLoading
                ) { Text(if (isLoading) "Đang kiểm tra..." else "ĐĂNG NHẬP", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onNavigateToRegister) { Text("Chưa có tài khoản? Đăng ký ngay", fontWeight = FontWeight.SemiBold) }
    }
}

// ================= MÀN HÌNH ĐĂNG KÝ =================
@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit, onNavigateToLogin: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("user") } // Thêm biến chọn quyền

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("TẠO TÀI KHOẢN", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(8.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(24.dp)) {
                OutlinedTextField(
                    value = username, onValueChange = { username = it }, label = { Text("Tên đăng nhập") }, leadingIcon = { Icon(Icons.Default.Person, null) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password, onValueChange = { password = it }, label = { Text("Mật khẩu") }, leadingIcon = { Icon(Icons.Default.Lock, null) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = { IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null) } },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Nhập lại mật khẩu") }, leadingIcon = { Icon(Icons.Default.Lock, null) },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = { IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) { Icon(if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null) } },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Phần chọn quyền (Role)
                Text("Đăng ký với quyền:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.selectableGroup().fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    RadioButton(selected = role == "admin", onClick = { role = "admin" })
                    Text("Admin", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.width(24.dp))
                    RadioButton(selected = role == "user", onClick = { role = "user" })
                    Text("User", fontWeight = FontWeight.SemiBold)
                }

                if (errorMessage.isNotEmpty()) { Spacer(modifier = Modifier.height(12.dp)); Text(text = errorMessage, color = Color.Red, fontSize = 14.sp) }
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) { errorMessage = "Vui lòng điền đủ thông tin!"; return@Button }
                        if (password != confirmPassword) { errorMessage = "Mật khẩu không khớp!"; return@Button }
                        isLoading = true; errorMessage = ""
                        val db = Firebase.firestore
                        db.collection("Users").whereEqualTo("username", username).get().addOnSuccessListener { docs ->
                            if (!docs.isEmpty) { isLoading = false; errorMessage = "Tên đăng nhập đã tồn tại!" }
                            else {
                                // Sử dụng role do người dùng chọn thay vì mặc định là "user"
                                db.collection("Users").add(User(username = username, password = password, role = role, imageUrl = "")).addOnSuccessListener {
                                    isLoading = false; Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show(); onRegisterSuccess()
                                }
                            }
                        }.addOnFailureListener { isLoading = false; errorMessage = "Lỗi kết nối mạng!" }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp), enabled = !isLoading
                ) { Text(if (isLoading) "Đang xử lý..." else "ĐĂNG KÝ", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onNavigateToLogin) { Text("Đã có tài khoản? Đăng nhập", fontWeight = FontWeight.SemiBold) }
    }
}

// ================= MÀN HÌNH PROFILE CÁ NHÂN =================
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun UserProfileScreen(currentUser: User, onLogout: () -> Unit) {
    var username by remember { mutableStateOf(currentUser.username) }
    var password by remember { mutableStateOf(currentUser.password) }
    var imageUrl by remember { mutableStateOf(currentUser.imageUrl) }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri -> if (uri != null) imageUrl = uri.toString() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).systemBarsPadding(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Hồ sơ của tôi", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            TextButton(onClick = onLogout) { Text("Đăng xuất", color = Color.Red, fontWeight = FontWeight.Bold) }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Box(modifier = Modifier.size(140.dp).clip(CircleShape).background(Color.LightGray).border(4.dp, MaterialTheme.colorScheme.primary, CircleShape), contentAlignment = Alignment.Center) {
            if (imageUrl.isNotEmpty()) GlideImage(model = imageUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            else Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color.Gray)
        }
        Spacer(modifier = Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(20.dp)) {
                OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Tên hiển thị") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password, onValueChange = { password = it }, label = { Text("Mật khẩu") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = { IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null) } },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Link ảnh đại diện") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, shape = RoundedCornerShape(12.dp), modifier = Modifier.height(56.dp).padding(top = 8.dp)) { Text("Đổi") }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { updateUser(currentUser.copy(username = username, password = password, imageUrl = imageUrl)); Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show() },
                    modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp)
                ) { Text("LƯU THAY ĐỔI", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

// ================= GIAO DIỆN QUẢN LÝ ADMIN =================
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AddUserForm() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("user") }
    var imageUrl by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val photoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri -> if (uri != null) imageUrl = uri.toString() }

    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), elevation = CardDefaults.cardElevation(2.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Thêm người dùng mới", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = password, onValueChange = { password = it }, label = { Text("Password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = { IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null) } },
                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Quyền:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.selectableGroup()) {
                    RadioButton(selected = role == "admin", onClick = { role = "admin" }); Text("Admin"); Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(selected = role == "user", onClick = { role = "user" }); Text("User")
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Link ảnh / Chọn tệp") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, shape = RoundedCornerShape(12.dp), modifier = Modifier.height(56.dp).padding(top = 8.dp)) { Text("Tệp") }
            }
            if (imageUrl.isNotEmpty()) { Spacer(modifier = Modifier.height(8.dp)); GlideImage(model = imageUrl, contentDescription = null, modifier = Modifier.size(50.dp).clip(CircleShape), contentScale = ContentScale.Crop) }

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { saveToFirestore(User(username = username, password = password, role = role, imageUrl = imageUrl)); username = ""; password = ""; role = "user"; imageUrl = "" },
                modifier = Modifier.fillMaxWidth().height(45.dp), shape = RoundedCornerShape(12.dp)
            ) { Text("Thêm Mới", fontWeight = FontWeight.Bold) }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun EditUserDialog(user: User, onDismiss: () -> Unit, onSave: (User) -> Unit) {
    var username by remember { mutableStateOf(user.username) }
    var password by remember { mutableStateOf(user.password) }
    var role by remember { mutableStateOf(user.role) }
    var imageUrl by remember { mutableStateOf(user.imageUrl) }
    var passwordVisible by remember { mutableStateOf(false) }
    val photoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri -> if (uri != null) imageUrl = uri.toString() }

    AlertDialog(
        onDismissRequest = onDismiss, title = { Text("Cập nhật thông tin", fontWeight = FontWeight.Bold) },
        shape = RoundedCornerShape(16.dp), containerColor = Color.White,
        text = {
            Column {
                OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password, onValueChange = { password = it }, label = { Text("Password") }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = { IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null) } }
                )
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                    RadioButton(selected = role == "admin", onClick = { role = "admin" }); Text("Admin"); Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(selected = role == "user", onClick = { role = "user" }); Text("User")
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                    OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Link / Tệp") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, shape = RoundedCornerShape(12.dp), modifier = Modifier.height(56.dp).padding(top = 8.dp)) { Text("Đổi") }
                }
            }
        },
        confirmButton = { Button(onClick = { onSave(user.copy(username = username, password = password, role = role, imageUrl = imageUrl)) }, shape = RoundedCornerShape(8.dp)) { Text("Lưu") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy", color = Color.Gray) } }
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun UserItem(user: User, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), elevation = CardDefaults.cardElevation(3.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(Color(0xFFE0E0E0)), contentAlignment = Alignment.Center) {
                if (user.imageUrl.isNotEmpty()) GlideImage(model = user.imageUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                else Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(35.dp), tint = Color.Gray)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.username, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                val roleColor = if (user.role == "admin") Color(0xFFD32F2F) else Color(0xFF388E3C)
                Text(text = user.role.uppercase(), style = MaterialTheme.typography.bodySmall, color = roleColor, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = onEditClick) { Icon(Icons.Default.Edit, "Sửa", tint = MaterialTheme.colorScheme.primary) }
            IconButton(onClick = onDeleteClick) { Icon(Icons.Default.Delete, "Xóa", tint = Color.Red) }
        }
    }
}

@Composable
fun UserListScreen(onLogout: () -> Unit) {
    var userList by remember { mutableStateOf(listOf<User>()) }
    var userToEdit by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        val query: com.google.firebase.firestore.Query = Firebase.firestore.collection("Users")
        query.addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            if (snapshot != null) {
                val list = mutableListOf<User>()
                for (doc in snapshot.documents) {
                    val user = doc.toObject(User::class.java)
                    if (user != null) { user.id = doc.id; list.add(user) }
                }
                userList = list
            }
        }
    }

    if (userToEdit != null) { EditUserDialog(user = userToEdit!!, onDismiss = { userToEdit = null }, onSave = { updatedUser -> updateUser(updatedUser); userToEdit = null }) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).systemBarsPadding()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Quản lý hệ thống", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            TextButton(onClick = onLogout) { Text("Đăng xuất", color = Color.Red, fontWeight = FontWeight.Bold) }
        }
        Spacer(modifier = Modifier.height(12.dp))

        AddUserForm()

        Text("DANH SÁCH TÀI KHOẢN (${userList.size})", style = MaterialTheme.typography.bodySmall, color = Color.Gray, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp, top = 8.dp))

        LazyColumn { items(userList) { user -> UserItem(user = user, onEditClick = { userToEdit = user }, onDeleteClick = { deleteUser(user.id) }) } }
    }
}