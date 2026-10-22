package com.example.app02.models

import androidx.compose.ui.graphics.vector.ImageVector

data class DialogData(
    val title: String,
    val message: String,
    val confirmText: String = "Xác nhận",
    val dismissText: String? = "Hủy", // Null nếu chỉ là popup thông báo 1 nút OK
    val icon: ImageVector? = null,
    val isDestructive: Boolean = false, // True nếu là hành động xóa/nguy hiểm (màu đỏ)
    val onConfirm: () -> Unit = {},
    val onDismiss: () -> Unit = {}
)
