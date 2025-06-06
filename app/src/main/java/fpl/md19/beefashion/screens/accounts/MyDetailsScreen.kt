package fpl.md19.beefashion.screens.accounts

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import fpl.md19.beefashion.GlobalVarible.UserSesion
import fpl.md19.beefashion.R
import fpl.md19.beefashion.requests.UpdateUserRequest
import fpl.md19.beefashion.requests.createMultipartBody
import fpl.md19.beefashion.viewModels.MyDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDetailsScreen(
    navController: NavController,
    onBackClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onSubmit: () -> Unit,
    myDetailsViewModel: MyDetailViewModel = viewModel()
) {
    val user = UserSesion.currentUser
    Log.d("user information: ", user.toString())

    var fullName by remember { mutableStateOf(user?.fullName ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var dateOfBirth by remember { mutableStateOf(user?.dateOfBirth ?: "") }
    var gender by remember { mutableStateOf(user?.gender ?: "") }
    var phone by remember { mutableStateOf(user?.phone ?: "") }

    // Thêm trạng thái cho lỗi số điện thoại
    var phoneError by remember { mutableStateOf<String?>(null) }

    var isGenderDropdownExpanded by remember { mutableStateOf(false) }

    // Danh sách các lựa chọn giới tính
    val genderOptions = listOf("Nam", "Nữ", "Khác")

    var avatarUri by remember {mutableStateOf<Uri?>(
        if (user?.image.isNullOrEmpty())
            null
        else
            Uri.parse(user?.image)
    )
    }

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Sử dụng ActivityResultContracts.GetContent() để chọn ảnh
    val getImageLauncher: ActivityResultLauncher<String> =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            avatarUri = uri // Cập nhật URI của ảnh đã chọn
        }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            // Permission granted, proceed to pick an image
            getImageLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "Quyền truy cập hình ảnh bị từ chối", Toast.LENGTH_SHORT).show()
        }
    }

    val avatarModel = avatarUri ?: R.drawable.ao_phong

    // Hàm validate số điện thoại Việt Nam
    fun validateVietnamesePhoneNumber(phone: String): Boolean {
        // Kiểm tra số điện thoại Việt Nam (bắt đầu bằng 0, sau đó là mã mạng và có tổng 10 ký tự)
        val phoneRegex = "^(0)[35789][0-9]{8}$".toRegex()
        return phoneRegex.matches(phone)
    }

    val isFormValid = fullName.isNotEmpty() && email.isNotEmpty() && dateOfBirth.isNotEmpty() &&
            gender.isNotEmpty() && phone.isNotEmpty() && phoneError == null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(15.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { navController.popBackStack() }
                )
                Text(
                    text = "Thông tin cá nhân",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    painter = painterResource(id = R.drawable.bell),
                    contentDescription = "Notifications",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onNotificationClick() }
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            // Avatar
            AsyncImage(
                model = avatarModel,
                fallback = painterResource(id = R.drawable.ao_phong),
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .clickable {
                        // Check for permission before picking an image
                        when {
                            // If permission is already granted, open the image picker
                            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED -> {
                                getImageLauncher.launch("image/*")
                            }
                            // If the app is running on Android 13+, request permission
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                            }
                            // If running on Android 12 or lower, request the old permission
                            else -> {
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                        }
                    }
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                "Thay đổi hình ảnh hồ sơ",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF777777),
                style = TextStyle(textDecoration = TextDecoration.Underline),
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Full Name
            Text(
                text = "Họ tên",
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                placeholder = { Text("Nhập họ tên của bạn") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email Address
            Text(
                text = "Email",
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Nhập email của bạn") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date of Birth
            Text(
                text = "Ngày sinh",
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = dateOfBirth,
                onValueChange = { dateOfBirth = it },
                placeholder = { Text("Nhập ngày sinh của bạn") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = Color.Black
                ),
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.calendar),
                        contentDescription = "Select date",
                        modifier = Modifier.size(24.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Gender
            Text(
                text = "Giới tính",
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Dropdown cho giới tính
            ExposedDropdownMenuBox(
                expanded = isGenderDropdownExpanded,
                onExpandedChange = { isGenderDropdownExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Chọn giới tính") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color.Black
                    ),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isGenderDropdownExpanded)
                    }
                )

                ExposedDropdownMenu(
                    expanded = isGenderDropdownExpanded,
                    onDismissRequest = { isGenderDropdownExpanded = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    genderOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                gender = option
                                isGenderDropdownExpanded = false
                            },
                            // Hiển thị checkbox cho tùy chọn được chọn
                            trailingIcon = {
                                if (gender == option) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_arrow_down),
                                        contentDescription = "Selected",
                                        tint = Color.Black
                                    )
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Number
            Text(
                text = "Số điện thoại",
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it
                    // Kiểm tra số điện thoại mỗi khi người dùng nhập
                    phoneError = when {
                        it.isEmpty() -> null
                        !it.all { char -> char.isDigit() } -> "Số điện thoại chỉ được chứa số"
                        it.length != 10 -> "Số điện thoại phải có 10 chữ số"
                        !validateVietnamesePhoneNumber(it) -> "Số điện thoại không đúng định dạng Việt Nam"
                        else -> null
                    }
                },
                placeholder = { Text("Nhập SDT của bạn") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                isError = phoneError != null,
                supportingText = {
                    phoneError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = if (phoneError != null) MaterialTheme.colorScheme.error else Color.LightGray,
                    focusedBorderColor = if (phoneError != null) MaterialTheme.colorScheme.error else Color.Black
                ),
                leadingIcon = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.vietnam),
                            contentDescription = "Vietnam flag",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
        }

        // Nút lưu thay đổi luôn ở dưới cùng
        Button(
            onClick = {
                if (fullName.isEmpty() || email.isEmpty() || dateOfBirth.isEmpty() || gender.isEmpty() || phone.isEmpty()) {
                    Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin.", Toast.LENGTH_SHORT)
                        .show()
                    return@Button
                }

                // Kiểm tra số điện thoại trước khi submit
                if (!validateVietnamesePhoneNumber(phone)) {
                    Toast.makeText(context, "Số điện thoại không đúng định dạng Việt Nam.", Toast.LENGTH_SHORT)
                        .show()
                    return@Button
                }

                // Tạo request để cập nhật thông tin
                val updatedUser = UpdateUserRequest(
                    fullName = fullName,
                    email = email,
                    gender = if (gender.isBlank()) null else gender,
                    phone = if (phone.isBlank()) null else phone,
                    dateOfBirth = if (dateOfBirth.isBlank()) null else dateOfBirth,
                    image = avatarUri?.let { createMultipartBody(it, context) }  // Include the avatar file
                )

                myDetailsViewModel.updateProfile(
                    updatedUser = updatedUser,
                    onSuccess = { updatedUserModel ->
                        UserSesion.currentUser = updatedUserModel
                        Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                        onSubmit()
                        navController.popBackStack()
                    },
                    onError = { errorMessage ->
                        Toast.makeText(context, "Lỗi: $errorMessage", Toast.LENGTH_LONG).show()
                        Log.e("MyDetailsScreen", "Lỗi cập nhật: $errorMessage")
                    }
                )
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isFormValid) Color(0xFFFF5722) else Color.LightGray
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = isFormValid,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .height(50.dp)
                .background(if (isFormValid) Color.White else Color.LightGray)
        ) {
            Text(
                text = "Lưu",
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun MyDetailsScreenPreview() {
    val navController = rememberNavController()
    MyDetailsScreen(
        navController,
        onBackClick = { /* Do nothing or mock back click action */ },
        onNotificationClick = { /* Do nothing or mock notification click action */ },
        onSubmit = { /* Do nothing or mock submit action */ }
    )
}