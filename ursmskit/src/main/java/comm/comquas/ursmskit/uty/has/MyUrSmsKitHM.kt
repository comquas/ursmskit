package comm.comquas.ursmskit.uty.has

import android.content.Context
import android.content.pm.PackageManager
import android.util.Base64
import java.security.MessageDigest

object MyUrSmsKitHM {
    fun getHaInit(context: Context): String? {
        try {
            val info = context.packageManager
                .getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                return String(Base64.encode(md.digest(), 0))
            }
        } catch (e: Exception) {
        }
        return null
    }
}