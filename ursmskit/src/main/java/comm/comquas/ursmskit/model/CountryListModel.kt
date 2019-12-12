package comm.comquas.ursmskit.model
import com.google.gson.annotations.SerializedName


data class CountryListModel(
    @SerializedName("data")
    val `data`: List<Data?>? = listOf()
) {
    data class Data(
        @SerializedName("code")
        val coUBhK4K: String? = "",
        @SerializedName("country")
        val cou5h2ER7: String? = "",
        @SerializedName("photo")
        var phQr4Q2d: String? = ""
    )
}