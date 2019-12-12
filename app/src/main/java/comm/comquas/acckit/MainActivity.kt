package comm.comquas.acckit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import comm.comquas.ursmskit.model.URSmsKitReturnDataModel
import comm.comquas.ursmskit.ui.URSmsKitActivity
import comm.comquas.ursmskit.uty.rec.setSafeOnClickListener
import kotlinx.android.synthetic.main.activity_main.*
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnLogin.setSafeOnClickListener {
            startActivityForResult(
                Intent(this, URSmsKitActivity::class.java)
                    .putExtra(URSmsKitActivity.passExtra1, URSmsKitActivity.requestTypePhone)
                ,
                URSmsKitActivity.resultCode
            )
        }
//        printHashKey(this)
//        val time = System.currentTimeMillis()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == URSmsKitActivity.resultCode) {
            if (resultCode == Activity.RESULT_OK) {
                val returnString = data?.getStringExtra("data")
                val returnModel = Gson().fromJson(returnString, URSmsKitReturnDataModel::class.java)
                if (TextUtils.isEmpty(returnModel.token))
                    textView.text = "${returnModel.phoneNo?.number}"

                else
                    textView.text = "${returnModel.token}"
            }
        }
    }

    fun printHashKey(context: Context) {
        try {
            val info = context.packageManager
                .getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i("AppLog", "key:$hashKey=")
                Log.e("asdf", "printHashKey: $hashKey")
                Toast.makeText(context, hashKey, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            Log.e("AppLog", "error:", e)
        }
    }
}
