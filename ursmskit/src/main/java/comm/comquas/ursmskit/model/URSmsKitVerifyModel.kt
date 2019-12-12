package comm.comquas.ursmskit.model
import com.google.gson.annotations.SerializedName


data class URSmsKitVerifyModel(
    @SerializedName("message")
    val message: String? = "",
    @SerializedName("success")
    val success: Boolean? = false,
    @SerializedName("error")
    val error: Boolean? = false
)