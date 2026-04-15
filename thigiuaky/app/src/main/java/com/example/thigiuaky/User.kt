package com.example.thigiuaky

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId var id: String = "", // Đổi val thành var
    var username: String = "",       // Đổi val thành var
    var password: String = "",       // Đổi val thành var
    var role: String = "user",       // Đổi val thành var
    var imageUrl: String = ""        // Đổi val thành var
)