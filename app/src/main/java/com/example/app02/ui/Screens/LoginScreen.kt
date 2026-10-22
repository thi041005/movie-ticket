package com.example.app02.ui.Screens


import kotlinx.coroutines.flow.collectLatest
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.app02.R
import com.example.app02.data.DataStore
import com.example.app02.nav.Screen
import com.example.app02.ui.components.BoldTextComponent
import com.example.app02.ui.components.Btn
import com.example.app02.ui.components.ClickableLoginTextComponent
import com.example.app02.ui.components.CommonAlertDialog
import com.example.app02.ui.components.MyTextField
import com.example.app02.ui.components.MyTextFieldPassword
import com.example.app02.ui.components.NormalTextComponent
import com.example.app02.ui.components.lineDivide
import com.example.app02.ui.theme.bcl
import com.example.app02.ui.theme.red
import com.example.app02.viewmodel.LoginState
import com.example.app02.viewmodel.LoginViewModel
import com.example.app02.models.DialogData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun LoginScreen(navController: NavController, loginViewModel: LoginViewModel = viewModel()) {
    val loginState by loginViewModel.loginState.collectAsState()
    var email = remember { mutableStateOf("") }
    var password = remember { mutableStateOf("") }
    val context = LocalContext.current
    val dataStore = remember { DataStore(context) }
    var dialogData by remember { mutableStateOf<DialogData?>(null) }

    val imageModifier = Modifier
        .size(150.dp)
        .border(
            BorderStroke(2.dp, Color.Transparent),
            shape = RoundedCornerShape(15.dp) // Thêm bo tròn viền
        )
    LaunchedEffect(Unit) {
        loginViewModel.loginState.collectLatest { state ->
            when (state) {
                is LoginState.Success -> {
                    println("TOKEN: ${state.token}")
                }

                is LoginState.Error -> {
                    println("${state.message}")
                }

                else -> {}
            }

        }
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
            .background(color = bcl)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = bcl)
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {

//                .offset(y = (-50).dp)
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "logo",
                contentScale = ContentScale.Inside,
                modifier = imageModifier
            )
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                BoldTextComponent("ĐĂNG NHẬP")
            }
            Spacer(modifier = Modifier.height(20.dp))


            MyTextField("Email", leadingIconVector = Icons.Default.Email, value = email.value,
                onValueChange = { email.value = it })
            Spacer(modifier = Modifier.height(10.dp))
            MyTextFieldPassword("Mật khẩu",
                leadingIconVector = Icons.Default.Lock,
                value = password.value,
                onValueChange = { password.value = it })

            ClickableLoginTextComponent("?", "Quên mật khẩu", onTextSelected = {
                navController.navigate(Screen.Reset.route)
            })

            Spacer(modifier = Modifier.height(40.dp))

            Btn(
                value = "Đăng nhập",
                onClick = {
                    loginViewModel.login(email.value, password.value) { success ->
                        if (success) {
                            // Lấy role từ DataStore sau khi login
                            CoroutineScope(Dispatchers.Main).launch {
                                val role = dataStore.getRole()
                                when (role) {
                                    "admin" -> navController.navigate("admin_home") {
                                        popUpTo("login") { inclusive = true }
                                    }

                                    "user" -> navController.navigate("user_home") {
                                        popUpTo("login") { inclusive = true }
                                    }

                                    else -> {
                                        dialogData = DialogData(
                                            title = "Không thể xác định quyền truy cập",
                                            message = "Tài khoản chưa được cấp quyền phù hợp. Vui lòng liên hệ quản trị viên.",
                                            dismissText = null,
                                            icon = Icons.Default.ErrorOutline,
                                            onConfirm = { dialogData = null },
                                            onDismiss = { dialogData = null }
                                        )
                                    }
                                }
                            }
                        } else {
                        dialogData = DialogData(
                            title = "Đăng nhập không thành công",
                            message = "Email hoặc mật khẩu chưa chính xác. Vui lòng kiểm tra lại thông tin.",
                            dismissText = null,
                            icon = Icons.Default.ErrorOutline,
                            onConfirm = { dialogData = null },
                            onDismiss = { dialogData = null }
                        )
                        }
                    }
                },
            )



            Spacer(modifier = Modifier.height(16.dp))

            when (loginState) {
                is LoginState.Loading -> CircularProgressIndicator()
                is LoginState.Error -> {}

                else -> {}
            }

    dialogData?.let { CommonAlertDialog(it) }



            lineDivide()
            ClickableLoginTextComponent("Bạn chưa có tài khoản?", "Đăng ký", onTextSelected = {
                navController.navigate(Screen.SignUp.route) // Đảm bảo rằng Screen.SignUpScreen có thuộc tính route
            })

        }


    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewOfLoginScreen() {
    val navController = rememberNavController() // Tạo NavController giả để tránh lỗi
//    LoginScreen(navController = navController)
}


