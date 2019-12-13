package comm.comquas.ursmskit.model

import com.google.gson.annotations.SerializedName


data class URSmsKitReturnDataModel(
    @SerializedName("application")
    val application: Application? = Application(),
    @SerializedName("created_at")
    val createdAt: Int? = 0,
    @SerializedName("error")
    val error: Boolean? = false,
    @SerializedName("phone")
    val phone: Phone? = Phone(),
    @SerializedName("expire_time")
    val expireTime: Int? = 0,
    @SerializedName("hash")
    val hash: String? = "",
    @SerializedName("token")
    val token: String? = ""
) {
    data class Application(
        @SerializedName("id")
        val id: String? = ""
    )

    data class Phone(
        @SerializedName("country_prefix")
        val countryPrefix: String? = "",
        @SerializedName("national_number")
        val nationalNumber: String? = "",
        @SerializedName("number")
        val number: String? = ""
    )
}