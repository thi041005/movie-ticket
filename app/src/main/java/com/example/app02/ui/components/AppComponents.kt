package com.example.app02.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.app02.R
import com.example.app02.ui.theme.bcl
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app02.nav.Screen
import com.example.app02.models.Movie
import com.example.app02.models.DialogData
import com.example.app02.ui.theme.GradientBtn
import com.example.app02.ui.theme.boxshadow
import com.example.app02.ui.theme.headerColor

import com.example.app02.ui.theme.textFocus
import com.example.app02.viewmodel.CateViewModel
import com.example.app02.viewmodel.LoginViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun BackOnlyTopBar(
    navController: NavController,
    title: String? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(56.dp)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .background(color = headerColor)
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "back",
                tint = Color.White
            )
        }

        title?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        }
    }
}

@Composable
fun CommonHeader(
    title: String,
    navController: NavController,
    rightIcon: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(60.dp)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .background(color = headerColor)
    ) {
        // Nút Back bên trái
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "back",
                tint = Color.LightGray
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.Center),
            color = Color.White
        )

        rightIcon?.let {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
            ) {
                it()
            }
        }
    }
}

@Composable
fun NormalTextComponent(value: String, textAlign: TextAlign = TextAlign.Center) {
    Text(
        text = value,
        style = TextStyle(
            fontSize = 16.sp, fontStyle = FontStyle.Normal
        ),
        color = Color.White,
        textAlign = textAlign
    )
}

@Composable
fun CustomTabRow(tabTitles: List<String>, selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {

    TabRow(
        selectedTabIndex = selectedTabIndex,
        indicator = { tabPositions ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .tabIndicatorOffset(tabPositions[selectedTabIndex])
                    .height(3.dp)
                    .background(textFocus) // Đảm bảo màu cam
            )
        },
        divider = {}
    ) {
        tabTitles.forEachIndexed { index, title ->
            val isSelected = selectedTabIndex == index
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                unselectedContentColor = Color.Gray
            ) {
                Text(
                    text = title,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 16.sp, fontStyle = FontStyle.Normal,
                    style = if (isSelected) {
                        TextStyle(
                            color = textFocus,
                            shadow = Shadow(
                                color = textFocus, // Đổ bóng màu cam
                                offset = Offset(0f, 0f),
                                blurRadius = 12f
                            )
                        )
                    } else {
                        TextStyle(color = Color.Gray)
                    }
                )
            }
        }
    }
}


@Composable
fun RowComponent(cate: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(2f)) {
            LightTextComponent(cate)
        }
        Column(modifier = Modifier.weight(3f)) {
            NormalTextComponent(value, textAlign = TextAlign.Start)
        }
    }
    Spacer(modifier = Modifier.height(8.dp))

}

@Composable
fun RowComponent2(cate: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(0.8f),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(2f)) {
            LightTextComponent(cate, Color.White)
        }
        Column(modifier = Modifier.weight(3f)) {
            NormalTextComponent(value, textAlign = TextAlign.Start)
        }
    }
    Spacer(modifier = Modifier.height(10.dp))

}


@Stable
fun Modifier.customShadow(
    color: Color = Color.Black,
    alpha: Float = 0.5f,
    borderRadius: Dp = 0.dp,
    shadowRadius: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp
) = drawBehind {

    val shadowColor = color.copy(alpha = alpha).toArgb()
    val transparent = color.copy(alpha = 0f).toArgb()

    this.drawIntoCanvas {
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = transparent

        frameworkPaint.setShadowLayer(
            shadowRadius.toPx(),
            offsetX.toPx(),
            offsetY.toPx(),
            shadowColor
        )
        it.drawRoundRect(
            0f,
            0f,
            this.size.width,
            this.size.height,
            borderRadius.toPx(),
            borderRadius.toPx(),
            paint
        )
    }
}

@Composable
fun LightTextComponent(
    value: String,
    color: Color? = null,
    alignment: TextAlign? = TextAlign.Start
) {
    Box(
    ) {
        Text(
            text = value,
            modifier = Modifier
                .fillMaxWidth(),
            style = TextStyle(
                fontSize = 15.sp, fontWeight = FontWeight.Light, fontStyle = FontStyle.Normal
            ),
            color = color ?: Color.Gray,
            textAlign = alignment ?: TextAlign.Start
        )
    }
    Spacer(
        modifier = Modifier.height(2.dp)
    )

}


@Composable
fun LightTextComponentCenter(value: String, color: Color? = null) {
    Box(
    ) {
        Text(
            text = value,
            modifier = Modifier
                .fillMaxWidth(),
            style = TextStyle(
                fontSize = 15.sp, fontWeight = FontWeight.Light, fontStyle = FontStyle.Normal
            ),
            color = color ?: Color.Gray,
            textAlign = TextAlign.Center
        )
    }
    Spacer(
        modifier = Modifier.height(2.dp)
    )

}


@Composable
fun BoldTextComponent(value: String) {
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth(),
        style = TextStyle(
            fontSize = 20.sp, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Normal
        ),
        color = colorResource(id = R.color.white),
        textAlign = TextAlign.Center,
    )
    Spacer(
        modifier = Modifier.height(2.dp)
    )

}

@Composable
fun DemoBoldTextComponent(value: String) {
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth(),
        style = TextStyle(
            fontSize = 20.sp, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Normal
        ),
        color = colorResource(id = R.color.white),
    )
    Spacer(
        modifier = Modifier.height(2.dp)
    )

}

fun getHourAndMinute(timeList: List<Int>): List<String> {
    return if (timeList.size >= 5) {
        listOf(timeList[3], timeList[4]).map { it.toString().padStart(2, '0') }
    } else {
        timeList.map { it.toString().padStart(2, '0') }
    }
}

fun getDate(timeList: List<Int>): List<String> {
    return if (timeList.size >= 5) {
        listOf(timeList[0], timeList[1], timeList[2]).map { it.toString().padStart(2, '0') }
    } else {
        timeList.map { it.toString().padStart(2, '0') }
    }
}


@Composable
fun MyTextField(
    labelValue: String, leadingIconVector: ImageVector? = null, value: String? = null,
    onValueChange: (String) -> Unit
) {

    OutlinedTextField(
        value = if (value != null) value else "",
        onValueChange = onValueChange,
        label = { Text(text = labelValue) },
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedLeadingIconColor = Color.White,
            unfocusedLeadingIconColor = Color.White,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.White,
            focusedContainerColor = bcl,
            unfocusedContainerColor = bcl,
            focusedIndicatorColor = Color.White,
            unfocusedIndicatorColor = Color.White,
            unfocusedPlaceholderColor = Color.White,
            focusedTextColor = Color.White,
            cursorColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        leadingIcon = {
            if (leadingIconVector != null)
                Icon(imageVector = leadingIconVector, contentDescription = labelValue)
        })
    Spacer(modifier = Modifier.padding(8.dp))
}

@Composable
fun MyTextFieldPassword(
    labelValue: String,
    leadingIconVector: ImageVector,
    value: String, // Nhận giá trị password từ bên ngoài
    onValueChange: (String) -> Unit,
) {

    val passwordVisible = remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = labelValue) },
        shape = RoundedCornerShape(20.dp),
        colors = TextFieldDefaults.colors(
            focusedLeadingIconColor = Color.White,
            unfocusedLeadingIconColor = Color.White,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.White,
            focusedContainerColor = bcl,
            unfocusedContainerColor = bcl,
            focusedIndicatorColor = Color.White,
            unfocusedIndicatorColor = Color.White,
            unfocusedPlaceholderColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        leadingIcon = {
            Icon(imageVector = leadingIconVector, contentDescription = labelValue)
        },
        trailingIcon = {
            val ic = if (passwordVisible.value) {
                Icons.Filled.Visibility
            } else {
                Icons.Filled.VisibilityOff
            }
            var description = if (passwordVisible.value) {
                "Ẩn"
            } else {
                "Hiện"
            }
            IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                Icon(imageVector = ic, contentDescription = description)
            }
        },
        visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
        ),
    )
}

@Composable
fun Btn(
    value: String,
    onClick: () -> Unit,
    isLoading: Boolean = false

) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,  // Gọi hàm khi nhấn nút
            contentPadding = PaddingValues(),
            colors = ButtonDefaults.buttonColors(Color.Transparent),
            shape = RoundedCornerShape(50.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.45f) // Chiều rộng 80% màn hình
                    .height(45.dp) // Chiều cao 50dp
                    .background(
                        GradientBtn,
                        shape = RoundedCornerShape(50.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(value, color = Color.White)
                }
            }
        }
    }
}


@Composable
fun checkBox(value: String, onTextSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(56.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val checkedState = remember {
            mutableStateOf<Boolean>(false)
        }
        Checkbox(checked = checkedState.value, onCheckedChange = {
            checkedState.value = !checkedState.value
        })
        ClickableTextComponent(value = value, onTextSelected)
    }
}

@Composable
fun ClickableTextComponent(value: String, onTextSelected: (String) -> Unit) {
    val initialText = "Đồng ý với các "
    val privacyPolicyText = "Điều khoản"
    val andText = " của cúng tôi. "

    val annotatedString = buildAnnotatedString {
        // Màu Gray cho toàn bộ văn bản
        withStyle(style = SpanStyle(color = Color.Gray)) {
            append(initialText)
        }
        withStyle(
            style = SpanStyle(
                color = Color.Gray, textDecoration = TextDecoration.Underline // Gạch chân
            )
        ) {
            pushStringAnnotation(tag = privacyPolicyText, annotation = privacyPolicyText)
            append(privacyPolicyText)
            pop()
        }
        withStyle(style = SpanStyle(color = Color.Gray)) {
            append(andText)
        }
    }
    ClickableText(text = annotatedString, onClick = { offset ->
        annotatedString.getStringAnnotations(offset, offset).firstOrNull()?.also { span ->
            Log.d("ClickableTextComponent", "{${span.item}}")

            if (span.item == privacyPolicyText) {
                onTextSelected(span.item)
            }
        }
    })
}

@Composable
fun ClickableLoginTextComponent(
    initialText: String,
    act: String,
    onTextSelected: (String) -> Unit
) {
    val initialText = initialText
    val login = act

    val annotatedString = buildAnnotatedString {
        // Màu Gray cho toàn bộ văn bản
        withStyle(style = SpanStyle(color = Color.Gray)) {
            append(initialText)
        }
        withStyle(
            style = SpanStyle(
                color = Color.White, textDecoration = TextDecoration.Underline
            )
        ) {
            pushStringAnnotation(tag = login, annotation = login)
            append(login)
            pop()
        }

    }
    ClickableText(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        style = TextStyle(textAlign = TextAlign.Center),
        text = annotatedString,
        onClick = { offset ->
            annotatedString.getStringAnnotations(offset, offset).firstOrNull()?.also { span ->
                Log.d("ClickableTextComponent", "{${span.item}}")

                if (span.item == login) {
                    onTextSelected(span.item)
                }
            }
        })
}

@Composable
fun lineDivide() {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = Color.Gray,
            thickness = 1.dp
        )
        Text(modifier = Modifier.padding(8.dp), text = "hoặc", fontSize = 14.sp, color = Color.Gray)
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = Color.Gray,
            thickness = 1.dp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonScaffold(
    isLogged: Boolean,
    navController: NavController,
    loginViewModel: LoginViewModel,
    role: String,
    showTopBar: Boolean = true,
    showBackButton: Boolean = false,
    cateViewModel: CateViewModel = viewModel(),
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val cates = cateViewModel.cate.value
    val scope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        cateViewModel.fetchCate()
    }
    ModalNavigationDrawer(
        scrimColor = Color.Gray,
        drawerState = drawerState,

        drawerContent = {
            Column {
                Text(
                    "Tài khoản", modifier = Modifier
                        .padding(16.dp)
                        .clickable {
                            navController.navigate(Screen.Bill.route)
                        }
                )
                Spacer(modifier = Modifier.padding(16.dp))
                if (role == "admin") {
                    Text(
                        "Quản lý phim", modifier = Modifier
                            .padding(16.dp)
                            .clickable {
                                navController.navigate(Screen.MovieManage.route)
                                expanded = false
                            }
                    )
                    Text(
                        "Quản lý suất chiếu", modifier = Modifier
                            .padding(16.dp)
                            .clickable {
                                navController.navigate(Screen.Showtime.route)
                                expanded = false
                            }
                    )
                }
                Box(modifier = Modifier) {

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        cates.forEach { cate ->
                            DropdownMenuItem(
                                text = { Text(cate.name, color = Color.Black) },
                                onClick = {
                                    navController.navigate("movie_manage")
                                    expanded = false
                                }
                            )
                        }

                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (showTopBar) {
                    TopAppBar(
                        title = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.logo),
                                    contentDescription = "Logo",
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, Color.Transparent, CircleShape)
                                )

                                IconButton(
                                    onClick = {
                                        navController.navigate(Screen.Bill.route)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Profile",
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        },
                        navigationIcon = {
                            when {
                                showBackButton -> {
                                    IconButton(onClick = { navController.popBackStack() }) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowBack,
                                            contentDescription = "Back",
                                            tint = Color.White
                                        )
                                    }
                                }

                                role == "admin" -> {
                                    IconButton(onClick = {
                                        scope.launch {
                                            drawerState.open()
                                        }
                                    }) {
                                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                                    }
                                }

                                else -> {
                                    Spacer(modifier = Modifier.width(0.dp))
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = headerColor
                        )

                    )
                }
            },
            bottomBar = {
                BottomAppBar(
                    backgroundColor = headerColor
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        when (role) {
                            "user" -> {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { navController.navigate(Screen.UserHome.route) },
                                    contentAlignment = Alignment.Center,

                                    ) {
                                    Icon(
                                        modifier = Modifier
                                            .size(35.dp)
                                            .fillMaxSize(),
                                        imageVector = Icons.Default.Home,
                                        contentDescription = "home",
                                        tint = Color.Gray
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { }, contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "LocationOn",
                                        tint = Color.Gray,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .fillMaxSize()
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { }, contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ticket),
                                        contentDescription = "ticket",
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(CircleShape)
                                            .border(2.dp, Color.Transparent, CircleShape)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            if (!isLogged) {
                                                navController.navigate(Screen.Login.route)
                                            } else {
                                                loginViewModel.logout() { success ->
                                                    if (success) {
                                                        navController.navigate(Screen.Login.route) {
                                                            popUpTo(navController.graph.id) {
                                                                inclusive = true
                                                            }
                                                            launchSingleTop = true
                                                        }
                                                    }
                                                }
                                            }
                                        }, contentAlignment = Alignment.Center
                                ) {

                                    Icon(
                                        imageVector = if (!isLogged) Icons.Default.Login else Icons.Default.Logout,
                                        contentDescription = if (!isLogged) "Login" else "Profile",
                                        tint = Color.Gray,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .fillMaxSize()
                                    )

                                }

                            }

                            "admin" -> {


                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clickable {
                                            navController.navigate("admin_home") {
                                                launchSingleTop = true
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        modifier = Modifier.size(35.dp),
                                        imageVector = Icons.Default.Home,
                                        contentDescription = "Home",
                                        tint = Color.Gray
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {}, contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "schedule",
                                        tint = Color.Gray,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .fillMaxSize()
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { }, contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "LocationOn",
                                        tint = Color.Gray,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .fillMaxSize()
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clickable {
                                            if (!isLogged) {
                                                navController.navigate(Screen.Login.route)
                                            } else {
                                                loginViewModel.logout() { success ->
                                                    navController.navigate(Screen.Login.route) {
                                                        popUpTo(Screen.Login.route) {
                                                            inclusive = true
                                                        }
                                                        launchSingleTop = true
                                                    }

                                                }
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {

                                    Icon(
                                        imageVector = if (!isLogged) Icons.Default.Login else Icons.Default.Logout,
                                        contentDescription = if (!isLogged) "Login" else "logout",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(35.dp),

                                        )

                                }
                            }
                        }
                    }
                }
            }
        ) { padding ->
            content(padding)
        }
    }
}


@Composable
fun ScheduleTimeSettings(
    fromDate: LocalDate,
    toDate: LocalDate,
    dailyStart: LocalTime,
    dailyEnd: LocalTime,
    onDateRangeClick: () -> Unit,
    onStartTimeClick: () -> Unit,
    onEndTimeClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ScheduleSettingRow(
                label = "Từ ngày",
                value = fromDate.format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                ),
                icon = Icons.Default.CalendarToday,
                onClick = onDateRangeClick
            )

            ScheduleSettingRow(
                label = "Đến ngày",
                value = toDate.format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                ),
                icon = Icons.Default.CalendarToday,
                onClick = onDateRangeClick
            )

            ScheduleSettingRow(
                label = "Bắt đầu",
                value = dailyStart.format(
                    DateTimeFormatter.ofPattern("HH:mm")
                ),
                icon = Icons.Default.AccessTime,
                onClick = onStartTimeClick
            )

            ScheduleSettingRow(
                label = "Kết thúc",
                value = dailyEnd.format(
                    DateTimeFormatter.ofPattern("HH:mm")
                ),
                icon = Icons.Default.AccessTime,
                onClick = onEndTimeClick
            )
        }
    }
}

@Composable
private fun ScheduleSettingRow(
    label: String,
    value: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color(0xFFBDBDBD)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = value,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = onClick,
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        Color(0xFF2A2A2A),
                        RoundedCornerShape(10.dp)
                    )
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Chỉnh sửa $label",
                    tint = Color(0xFFFFD54F),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizableSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    searchResults: List<Movie>,
    onResultClick: (Int) -> Unit
) {

    var expanded by rememberSaveable { mutableStateOf(false) }
    Box {
        OutlinedTextField(
            value = query,
            onValueChange = {
                onQueryChange(it)
                expanded = it.isNotBlank() && searchResults.isNotEmpty()
            },
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .fillMaxWidth(),
            label = { Text("Tìm kiếm phim...") },
            colors = TextFieldDefaults.colors(
                focusedLeadingIconColor = Color.White,
                unfocusedLeadingIconColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                focusedContainerColor = bcl,
                unfocusedContainerColor = bcl,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White,
                unfocusedPlaceholderColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
            ),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },

            )


    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
    onDismiss: () -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 0.dp,
            color = Color(0xFF121212),
            modifier = Modifier.padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Chọn phạm vi ngày",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                DateRangePicker(
                    state = dateRangePickerState,
                    title = null,
                    showModeToggle = false,
                    colors = DatePickerDefaults.colors(
                        containerColor = Color(0xFF121212),
                        titleContentColor = Color.White,
                        headlineContentColor = Color.White,
                        weekdayContentColor = Color(0xFFBDBDBD),
                        navigationContentColor = Color.White,
                        dayContentColor = Color.White,
                        selectedDayContainerColor = Color(0xFFFFD54F),
                        selectedDayContentColor = Color(0xFF111111),
                        todayContentColor = Color(0xFFFFD54F),
                        todayDateBorderColor = Color(0xFFFFD54F),
                        dayInSelectionRangeContainerColor = Color(0x66FFD54F),
                        dayInSelectionRangeContentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(480.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { onDismiss() }
                    ) {
                        Text(
                            text = "Hủy",
                            color = Color(0xFFBDBDBD)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onDateRangeSelected(
                                Pair(
                                    dateRangePickerState.selectedStartDateMillis,
                                    dateRangePickerState.selectedEndDateMillis
                                )
                            )
                            onDismiss()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD54F),
                            contentColor = Color(0xFF111111)
                        )
                    ) {
                        Text("Xác nhận", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialExample(
    initialTime: LocalTime = LocalTime.now(),
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true,
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 0.dp,
            color = Color(0xFF121212),
            modifier = Modifier.padding(12.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Chọn giờ",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = Color(0xFF1F1F1F),
                        clockDialSelectedContentColor = Color(0xFF111111),
                        clockDialUnselectedContentColor = Color.White,
                        selectorColor = Color(0xFFFFD54F),
                        containerColor = Color(0xFF121212),
                        periodSelectorBorderColor = Color(0xFF2A2A2A),
                        periodSelectorSelectedContainerColor = Color(0xFFFFD54F),
                        periodSelectorSelectedContentColor = Color(0xFF111111),
                        periodSelectorUnselectedContentColor = Color.White,
                        timeSelectorSelectedContainerColor = Color(0xFFFFD54F),
                        timeSelectorSelectedContentColor = Color(0xFF111111),
                        timeSelectorUnselectedContainerColor = Color(0xFF1F1F1F),
                        timeSelectorUnselectedContentColor = Color.White
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { onDismiss() }
                    ) {
                        Text(
                            text = "Hủy",
                            color = Color(0xFFBDBDBD)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val selectedTime = LocalTime.of(
                                timePickerState.hour,
                                timePickerState.minute
                            )
                            onConfirm(selectedTime)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD54F),
                            contentColor = Color(0xFF111111)
                        )
                    ) {
                        Text("Xác nhận", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun previewTicket() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bcl)
            .customShadow(
                color = boxshadow,
                alpha = 0.2f,
                borderRadius = 16.dp,
                shadowRadius = 10.dp,
                offsetX = 2.dp,
                offsetY = 2.dp
            )
            .clip(RoundedCornerShape(16.dp))
            .padding(15.dp)

    )
    {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = boxshadow
            )

        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "thi",
                        style = TextStyle(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.LightGray
                        ), modifier = Modifier.fillMaxWidth(0.5f)
                    )
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star Icon",
                        tint = Color.Yellow, modifier = Modifier.fillMaxWidth(0.2f)
                    )
                    Text(
                        "8.5",
                        style = TextStyle(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Red
                        ), modifier = Modifier.fillMaxWidth()
                    )


                }
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd,
                ) {

                    LightTextComponent(
                        "2025:01:01:T01:01",
                        Color.White,
                        alignment = TextAlign.End
                    )
                }

            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                LightTextComponent("rate.commentokok", Color.White)
            }
        }
    }
    Spacer(modifier = Modifier.padding(8.dp))

}

@Composable
fun CommonAlertDialog(
    title: String,
    message: String,
    confirmText: String = "Xác nhận",
    dismissText: String? = "Hủy",
    icon: ImageVector? = null,
    isDestructive: Boolean = false, // Nút confirm đổi sang màu đỏ khi xóa
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val accentColor = if (isDestructive) Color(0xFFFF6B6B) else Color(0xFFFFD54F)
    val dialogColor = Color(0xFF202020)
    val secondaryTextColor = Color(0xFFBDBDBD)

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        containerColor = dialogColor,
        icon = {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = accentColor
                    )
                }
            }
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = secondaryTextColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = accentColor,
                    contentColor = if (isDestructive) Color.White else Color(0xFF111111)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(confirmText, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            if (dismissText != null) {
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = secondaryTextColor
                    )
                ) {
                    Text(dismissText)
                }
            }
        }
    )
}

@Composable
fun CommonAlertDialog(dialogData: DialogData) {
    CommonAlertDialog(
        title = dialogData.title,
        message = dialogData.message,
        confirmText = dialogData.confirmText,
        dismissText = dialogData.dismissText,
        icon = dialogData.icon,
        isDestructive = dialogData.isDestructive,
        onConfirm = dialogData.onConfirm,
        onDismiss = dialogData.onDismiss
    )
}