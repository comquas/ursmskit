package comm.comquas.ursmskit.net

import comm.comquas.ursmskit.model.*
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface URSmsKitApiService {

    @POST("packageVerify")
    fun urSMSKitVerify(
        @Body reqBody: URSmsKitRVerifyModel
    ): Single<URSmsKitVerifyModel>

    @GET("getCountryList")
    fun urSMSKitCountryList(): Single<CountryListModel>

    @POST("{typeId}")
    fun urSMSKitGenOTP(
        @Body reqBody: URSmsKitRGenOTPModel,
        @Path("typeId") typeId: String
    ): Single<URSmsKitGenOTPModel>

    @POST("checkOTP")
    fun urSMSKitCheckOTP(
        @Body reqBody: URSmsKitRCheckOTPModel
    ): Single<URSmsKitReturnDataModel>
}