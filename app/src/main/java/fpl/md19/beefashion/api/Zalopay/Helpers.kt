package fpl.md19.beefashion.api.Zalopay

import android.annotation.SuppressLint
import fpl.md19.beefashion.api.Zalopay.hmac.HMacUtil
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*

object Helpers {
    private var transIdDefault = 1

    @SuppressLint("DefaultLocale")
    @JvmStatic
    fun getAppTransId(): String {
        if (transIdDefault >= 100000) {
            transIdDefault = 1
        }

        transIdDefault += 1
        val formatDateTime = SimpleDateFormat("yyMMdd_hhmmss", Locale.getDefault())
        val timeString = formatDateTime.format(Date())
        return String.format("%s%06d", timeString, transIdDefault)
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
    @JvmStatic
    fun getMac(key: String, data: String): String {
        return HMacUtil.hmacHexStringEncode(HMacUtil.HMACSHA256, key, data)!!
    }
}