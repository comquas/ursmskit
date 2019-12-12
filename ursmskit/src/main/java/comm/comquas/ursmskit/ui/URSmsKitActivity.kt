package comm.comquas.ursmskit.ui

import android.R
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import comm.comquas.ursmskit.CountryAdapter
import comm.comquas.ursmskit.model.*
import comm.comquas.ursmskit.net.URSmsKitProvideRetrofit
import comm.comquas.ursmskit.uty.MyDialogUty
import comm.comquas.ursmskit.uty.has.MyHmUrSmsKit
import comm.comquas.ursmskit.uty.has.MyUrSmsKitHM
import comm.comquas.ursmskit.uty.rec.MyRecyclerViewRowClickListener
import comm.comquas.ursmskit.uty.rec.setSafeOnClickListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_ursmskit.*
import org.json.JSONObject
import retrofit2.HttpException


class URSmsKitActivity : AppCompatActivity() {

    private var apId5nzQYs: String? = null
    private var apSepJU9gW: String? = null
    private var keHaFPr7K8: String? = null
    private var otTyrBDe8E: Int? = null
    private var accIdsJ6ARE: String? = null
    private var goToConfirm: Boolean = false
    private var resetInterval: CountDownTimer? = null
    private var compositeDisposable = CompositeDisposable()
    private val myApiService by lazy {
        URSmsKitProvideRetrofit.create(
            this
        )
    }
    private var loadingDialog: AppCompatDialog? = null
    private lateinit var countryAdapter: CountryAdapter
    private lateinit var countryList: MutableList<CountryListModel.Data>

    companion object {
        const val passExtra1 = "gmz2yEG4DQgZ9dbp"
        const val passExtra2 = "69cyaXgFnv5bP8Ak"
        const val resultCode = 1234
        const val requestTypeToken = 1
        const val requestTypePhone = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(comm.comquas.ursmskit.R.layout.activity_ursmskit)
        val extra = intent.extras
        if (extra != null) {
            otTyrBDe8E = intent.getIntExtra(passExtra1, 1)
        }
        val ai =
            packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        val bundle = ai.metaData
        apId5nzQYs = bundle.getString("meta_data_ursmskit_app_id")
        apSepJU9gW = bundle.getString("meta_data_ursmskit_app_secret").toString()
        keHaFPr7K8 = MyUrSmsKitHM.getHaInit(this)

        urSMSKitVerify()
        ivCountry.setSafeOnClickListener {
            showCountryListDialog()
        }

        tvPhone.addTextChangedListener {
            btnNext.isEnabled = it.isNotEmpty()
        }
        btnNext.setSafeOnClickListener {
            goToConfirm = true
            groupPhone.visibility = View.GONE
            groupResend.visibility = View.VISIBLE
            tvConfirmOTPTitle.text = getString(
                comm.comquas.ursmskit.R.string.confirm_otp_title,
                "${tvCountryCode.text.toString().trim()}${tvPhone.text.toString().trim()}"
            )
            urSMSKitGenOTP(typeId = "generateOTP")
        }
        btnResend.setSafeOnClickListener {
            btnResend.isEnabled = false
            urSMSKitGenOTP(typeId = "resendOTP")
        }
        etOTP.addTextChangedListener {
            btnContinue.isEnabled = it.isNotEmpty()
        }
        btnContinue.setSafeOnClickListener {
            urSMSKitCheckOTP()
        }
    }

    private fun goBackWithOK(dataURSmsKit: URSmsKitReturnDataModel) {
        val intent = Intent()
        intent.putExtra("data", Gson().toJson(dataURSmsKit))
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun EditText.addTextChangedListener(testFunction: (text: String) -> Unit) {
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                testFunction(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun urSMSKitVerify() {
        val requestBody = URSmsKitRVerifyModel()
        requestBody.ap7mAZn = apId5nzQYs
        requestBody.paNhw2sC6 = this.packageName
        requestBody.kekM7tu3 = keHaFPr7K8
        val hValue = MyHmUrSmsKit.hURSMSKitVerifyKit(
            text = "${requestBody.ap7mAZn}${requestBody.paNhw2sC6}${requestBody.kekM7tu3}".trim(),
            sText = apSepJU9gW.toString()
        )
        requestBody.haQcW4Ue = hValue

        compositeDisposable.add(
            myApiService.urSMSKitVerify(reqBody = requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    loadingDialog?.dismiss()
                    loadingDialog =
                        MyDialogUty.showLoadingDialog(
                            context = this
                        )
                }//show loading
                .doAfterTerminate { loadingDialog?.dismiss() }//hide loading
                .subscribe({ responseData ->
                    Toast.makeText(
                        this@URSmsKitActivity,
                        responseData.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    urSMSKitCountryList()
                }, { error ->
                    var message = error.message
                    var title = "Error!"
                    when (error) {
                        is HttpException -> {
                            when {
                                error.response()?.code() == 404 -> {
                                    val responseBody = error.response()?.errorBody()
                                    val jsonObject = JSONObject(responseBody?.string())
                                    message = jsonObject.getString("message")
                                    title = error.message()
                                }
                                error.response()?.code() == 422 -> {
                                    val responseBody = error.response()?.errorBody()
                                    val jsonObject = JSONObject(responseBody?.string())
                                    message = jsonObject.getString("message")
                                    title = error.message()
                                }
                            }
                        }
                    }
                    MyDialogUty.showInfoDialog(
                        this,
                        object :
                            MyDialogUty.MyDialogCallback<String> {
                            override fun myDialogCallback(
                                action: String,
                                pressOk: Boolean,
                                requireData: String?
                            ) {
                                if (pressOk)
                                    onBackPressed()
                            }
                        },
                        "",
                        title,
                        message.toString(),
                        getString(R.string.yes)
                    )
                })
        )
    }

    private fun urSMSKitCountryList() {
        compositeDisposable.add(
            myApiService.urSMSKitCountryList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    loadingDialog?.dismiss()
                    loadingDialog =
                        MyDialogUty.showLoadingDialog(
                            context = this
                        )
                }//show loading
                .doAfterTerminate { loadingDialog?.dismiss() }//hide loading
                .subscribe({ responseData ->
                    groupPhone.visibility = View.VISIBLE
                    countryList = ArrayList()
                    countryList.addAll(responseData.data as List<CountryListModel.Data>)
                    countryList.forEach {
                        if (it.cou5h2ER7?.toLowerCase().toString() == "myanmar") {
                            tvCountryCode.text = it.coUBhK4K
                            Picasso.get().load(it.phQr4Q2d)
                                .into(ivCountry)
                        }
                    }
                }, { error ->
                    var message = error.message
                    when (error) {
                        /*is HttpException -> {
                            if (error.response()?.code() == 422) {
                                val responseBody = error.response()?.errorBody()
                                val jsonObject = JSONObject(responseBody?.string())
//                                message = jsonObject.getString("message")
                            }
                        }*/
                    }
                    MyDialogUty.showInfoDialog(
                        this, null, "", "Error!",
                        message.toString(), getString(R.string.yes)
                    )
                })
        )
    }

    private fun showCountryListDialog(
        title: String? = null
    ) {
        val dialog = AppCompatDialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(comm.comquas.ursmskit.R.layout.dialog_country_list)
        val recyclerView =
            dialog.findViewById<RecyclerView>(comm.comquas.ursmskit.R.id.recyclerView)

        recyclerView?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            this,
            RecyclerView.VERTICAL,
            false
        )
        countryAdapter =
            CountryAdapter(countryList, object :
                MyRecyclerViewRowClickListener {
                override fun onRowClicked(position: Int) {
                    dialog.dismiss()
                    tvCountryCode.text = countryList[position].coUBhK4K
                    Picasso.get().load(countryList[position].phQr4Q2d)
                        .into(ivCountry)
                }
            })
        recyclerView?.adapter = countryAdapter
        dialog.show()
    }

    private fun urSMSKitGenOTP(typeId: String) {
        val requestBody = URSmsKitRGenOTPModel()
        requestBody.pNd8TSwp = "${tvPhone.text.toString().trim()}"
        requestBody.coPGg7rVA = "${tvCountryCode.text.toString().trim()}"
        requestBody.apI8DxS9BD = apId5nzQYs
        val hValue = MyHmUrSmsKit.hURSMSKitVerifyKit(
            text = "${requestBody.pNd8TSwp}${requestBody.apI8DxS9BD}".trim(),
            sText = apSepJU9gW.toString()
        )
        requestBody.haB8xsAD = hValue

        compositeDisposable.add(
            myApiService.urSMSKitGenOTP(reqBody = requestBody, typeId = typeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    loadingDialog?.dismiss()
                    loadingDialog =
                        MyDialogUty.showLoadingDialog(
                            context = this
                        )
                }//show loading
                .doAfterTerminate { loadingDialog?.dismiss() }//hide loading
                .subscribe({ responseData ->

                    //                    val resendTimeInterval: Long = 3000L
                    val resendTimeInterval: Long = responseData.rePtLY9z!! * 60000L
                    resetInterval?.cancel()
                    resetInterval = object : CountDownTimer(resendTimeInterval, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            btnResend.text = "Send SMS Again in " + millisUntilFinished / 1000
                        }

                        override fun onFinish() {
                            btnResend.text = "Send SMS Again"
                            btnResend.isEnabled = true
                        }
                    }.start()
//                    if (BuildConfig.DEBUG)
                    etOTP.setText("${responseData.otCoT5TkHJ}")
                    accIdsJ6ARE = responseData.acIzV257P.toString()
                }, { error ->
                    var message = error.message
                    var title = "Error!"
                    when (error) {
                        is HttpException -> {
                            if (error.response()?.code() == 422) {
                                val responseBody = error.response()?.errorBody()
                                val jsonObject = JSONObject(responseBody?.string())
                                message = jsonObject.getString("message")
                                title = error.message()
                            }
                        }
                    }
                    MyDialogUty.showInfoDialog(
                        this, null, "", title,
                        message.toString(), getString(R.string.yes)
                    )
                })
        )
    }

    private fun urSMSKitCheckOTP() {
        val requestBody = URSmsKitRCheckOTPModel()
        requestBody.otCBz8wF = "${etOTP.text.toString().trim()}"
        requestBody.accI9DqVC = accIdsJ6ARE
        requestBody.tyFqC5y3 = otTyrBDe8E
        val hValue = MyHmUrSmsKit.hURSMSKitVerifyKit(
            text = "${requestBody.otCBz8wF}${requestBody.accI9DqVC}".trim(),
            sText = apSepJU9gW.toString()
        )
        requestBody.haMe8VyV = hValue

        compositeDisposable.add(
            myApiService.urSMSKitCheckOTP(reqBody = requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    loadingDialog?.dismiss()
                    loadingDialog =
                        MyDialogUty.showLoadingDialog(
                            context = this
                        )
                }//show loading
                .doAfterTerminate { loadingDialog?.dismiss() }//hide loading
                .subscribe({ responseData ->
                    goBackWithOK(responseData)
                }, { error ->
                    var message = error.message
                    var title = "Error!"
                    when (error) {
                        is HttpException -> {
                            if (error.response()?.code() == 422) {
                                val responseBody = error.response()?.errorBody()
                                val jsonObject = JSONObject(responseBody?.string())
                                message = jsonObject.getString("message")
                                title = error.message()
                            }
                        }
                    }
                    MyDialogUty.showInfoDialog(
                        this, null, "", title,
                        message.toString(), getString(R.string.yes)
                    )
                })
        )
    }

    override fun onBackPressed() {
        if (goToConfirm) {
            groupResend.visibility = View.GONE
            groupPhone.visibility = View.VISIBLE
            goToConfirm = false
        } else
            super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
