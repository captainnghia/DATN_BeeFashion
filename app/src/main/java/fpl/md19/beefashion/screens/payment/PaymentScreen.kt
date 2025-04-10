package fpl.md19.beefashion.screens.payment

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import fpl.md19.beefashion.R
import fpl.md19.beefashion.viewModels.AddressViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PaymentScreen(
    navController: NavController,
    viewModel: AddressViewModel,
    fullAddress: String? = null,
    customerId: String
) {

    val selectedMethod = remember { mutableStateOf("cod") }
    val context = LocalContext.current
    val selectedAddress by viewModel.selectedAddress1

    LaunchedEffect(Unit) {
        // Trigger khi màn hình xuất hiện lại
        Log.d("PaymentScreen", "Selected address: ${viewModel.selectedAddress1.value}")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(15.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 10.dp,
                    vertical = 6.dp
                ), // Giảm từ 15.dp, 10.dp xuống 10.dp, 6.dp
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = "Back",
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        navController.popBackStack()
                    }
            )
            Text(
                text = "Thanh toán",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(20.dp))
        }

        // Địa chỉ giao hàng
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(8.dp)) // Giảm từ 10.dp xuống 8.dp
                .padding(10.dp) // Giảm từ 15.dp xuống 10.dp
                .clickable {
                    // When navigating to address screen, include the returnToPayment flag
                //    val navigateToAddressScreen = {
                //        navController.navigate("addressScreen/${customerId}/true")
                //    }
                    //navController.navigate("addressScreen")
                    navController.navigate("AddressScreen/${customerId}/true")
                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp) // Giảm kích thước icon
                )
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    val displayAddress = fullAddress ?: selectedAddress

                    if (!displayAddress.isNullOrEmpty()) {
                        Text(text = "Địa chỉ: ${Uri.decode(displayAddress)}", fontSize = 14.sp)
                    } else {
                        Text(text = "Chưa có địa chỉ", fontSize = 14.sp, color = Color.Gray)
                    }
                }
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_right),
                    contentDescription = "Edit Address",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp) // Giảm từ 24.dp xuống 20.dp
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp)) // Giảm từ 10.dp xuống 6.dp

        // Sản phẩm
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(8.dp)) // Giảm từ 10.dp xuống 8.dp
                .padding(10.dp) // Giảm từ 15.dp xuống 10.dp
        ) {
            LazyColumn {
                item {
                    Text(
                        text = "Sản phẩm",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                val paymentProductList = listOf("Sản phẩm 1", "Sản phẩm 2", "Sản phẩm 3")
                items(paymentProductList.size) { index ->
                    PaymentItem(
                        name = paymentProductList[index],
                        imageRes = R.drawable.ao_phong,
                        size = "M", // size mẫu, có thể truyền từ data
                        quantity = 1,
                        price = 22900
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp)) // Giảm từ 10.dp xuống 6.dp

        // Phương thức thanh toán
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(8.dp)) // Giảm từ 10.dp xuống 8.dp
                .padding(4.dp) // Giảm từ 6.dp xuống 4.dp
        ) {
            Column(
                modifier = Modifier.padding(6.dp) // Giảm từ 10.dp xuống 6.dp
            ) {
                Text(
                    text = "Phương thức thanh toán",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp // Giảm từ 16.sp xuống 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp)) // Giảm từ 12.dp xuống 8.dp
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .clickable {
                            selectedMethod.value = "cod"
                            Toast.makeText(
                                context,
                                "Bạn đã chọn thanh toán khi nhận hàng!",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Thanh toán khi nhận hàng",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    RadioButton(
                        selected = selectedMethod.value == "cod",
                        onClick = {
                            selectedMethod.value = "cod"
                            Toast.makeText(
                                context,
                                "Bạn đã chọn thanh toán khi nhận hàng!",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedMethod.value = "zalopay"
                            Toast.makeText(
                                context,
                                "Bạn đã chọn thanh toán bằng ZaloPay!",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Thanh toán bằng ZaloPay",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    RadioButton(
                        selected = selectedMethod.value == "zalopay",
                        onClick = {
                            selectedMethod.value = "zalopay"
                            Toast.makeText(
                                context,
                                "Bạn đã chọn thanh toán bằng ZaloPay!",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp)) // Giảm từ 12.dp xuống 8.dp
            }
        }

        Spacer(modifier = Modifier.height(6.dp)) // Giảm từ 10.dp xuống 6.dp

        // Chi tiết thanh toán
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(8.dp)) // Giảm từ 10.dp xuống 8.dp
                .padding(4.dp) // Giảm từ 6.dp xuống 4.dp
        ) {
            Column(
                modifier = Modifier.padding(6.dp) // Giảm từ 10.dp xuống 6.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Tổng tiền hàng",
                        color = Color.Gray,
                        fontSize = 12.sp // Giảm từ mặc định xuống 12.sp
                    )
                    Text(
                        text = formatCurrency(23900),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp // Giảm từ mặc định xuống 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp)) // Giảm từ 12.dp xuống 8.dp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Phí vận chuyển",
                        color = Color.Gray,
                        fontSize = 12.sp // Giảm từ mặc định xuống 12.sp
                    )
                    Text(
                        text = formatCurrency(5000),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp // Giảm từ mặc định xuống 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp)) // Giảm từ 12.dp xuống 8.dp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Giảm giá vận chuyển",
                        color = Color.Gray,
                        fontSize = 12.sp // Giảm từ mặc định xuống 12.sp
                    )
                    Text(
                        text = "-${formatCurrency(5000)}",
                        fontWeight = FontWeight.Bold,
                        color = Color.Red,
                        fontSize = 12.sp // Giảm từ mặc định xuống 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp)) // Giảm từ 12.dp xuống 8.dp
                Divider()
                Spacer(modifier = Modifier.height(8.dp)) // Giảm từ 12.dp xuống 8.dp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Tổng cộng",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp // Giảm từ mặc định xuống 14.sp
                    )
                    Text(
                        text = formatCurrency(22900),
                        fontWeight = FontWeight.Bold,
                        color = Color.Red,
                        fontSize = 14.sp // Giảm từ mặc định xuống 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Nút đặt hàng
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(10.dp), // Giảm từ 15.dp xuống 10.dp
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Tổng thanh toán",
                    fontSize = 12.sp, // Giảm từ 14.sp xuống 12.sp
                    color = Color.Gray
                )
                Text(
                    text = formatCurrency(22900),
                    fontSize = 16.sp, // Giảm từ 18.sp xuống 16.sp
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            }
            Button(
                onClick = {
                    Toast.makeText(context, "Bạn đã đặt hàng thành công!", Toast.LENGTH_SHORT)
                        .show()

                    navController.navigate("successScreen")
                    NotificationUtils.showOrderSuccessNotification(context)

                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                modifier = Modifier
                    .padding(4.dp)
                    .height(36.dp)
                    .clickable {
                        navController.navigate("successScreen")
                    }
            ) {
                Text(
                    text = "Đặt hàng",
                    color = Color.White,
                    fontSize = 14.sp // Giảm từ 16.sp xuống 14.sp
                )
            }
        }
    }
}

@Composable
fun PaymentItem(
    name: String,
    imageRes: Int,
    size: String,
    quantity: Int,
    price: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = null,
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Size: $size",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "SL: x$quantity",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = formatCurrency(price),
                fontWeight = FontWeight.Bold,
                color = Color.Red,
                fontSize = 14.sp
            )
        }
    }
}

fun formatCurrency(price: Any?): String {
    val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
    return when (price) {
        is Number -> formatter.format(price.toLong()) + " ₫"
        is String -> price.toLongOrNull()?.let { formatter.format(it) + " ₫" } ?: "0 ₫"
        else -> "0 ₫"
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewPaymentScreen() {
    val navController = rememberNavController()
    PaymentScreen(
        navController = navController,
        viewModel = AddressViewModel(),
        fullAddress = "",
        customerId = ""
    )
}

