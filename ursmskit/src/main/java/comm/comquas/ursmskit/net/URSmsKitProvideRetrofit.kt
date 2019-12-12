package comm.comquas.ursmskit.net

import android.content.Context
import com.google.gson.GsonBuilder
import com.readystatesoftware.chuck.ChuckInterceptor
import comm.comquas.ursmskit.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class URSmsKitProvideRetrofit {
    companion object {
        fun create(context: Context, removeLog: Boolean = false): URSmsKitApiService {
            val baseUrl = "http://acckit.comquas.com/api/"
            val defaultTimeOut = 45L

            fun release(): Retrofit {
                return Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(
                        GsonConverterFactory.create(
                            GsonBuilder()
//                                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                .setLenient()
                                .create()
                        )
                    )
                    .baseUrl(baseUrl)
                    .client(OkHttpClient().newBuilder()
                        .connectTimeout(defaultTimeOut, TimeUnit.SECONDS)
                        .readTimeout(defaultTimeOut, TimeUnit.SECONDS)
                        .writeTimeout(defaultTimeOut, TimeUnit.SECONDS)
//                        .addInterceptor(ChuckInterceptor(context))
                        .build())
                    .build()
            }

            fun debug(): Retrofit {
                return if (removeLog) {
                    Retrofit.Builder()
                        .addConverterFactory(
                            GsonConverterFactory.create(
                                GsonBuilder()
//                                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                    .setLenient()
                                    .create()
                            )
                        )
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .baseUrl(baseUrl)
                        .client(OkHttpClient().newBuilder()
                            .connectTimeout(defaultTimeOut, TimeUnit.SECONDS)
                            .readTimeout(defaultTimeOut, TimeUnit.SECONDS)
                            .writeTimeout(defaultTimeOut, TimeUnit.SECONDS)
//                            .addInterceptor(ChuckInterceptor(context))
                            .build())
                        .build()
                } else {
                    Retrofit.Builder()
                        .addConverterFactory(
                            GsonConverterFactory.create(
                                GsonBuilder()
//                                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                    .setLenient()
                                    .create()
                            )
                        )
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .baseUrl(baseUrl)
//                        .client(
//                            OkHttpClient.Builder()
//                                .addInterceptor(ChuckInterceptor(context))
//                                .build()
//                        )
                        .client(OkHttpClient().newBuilder()
                            .connectTimeout(defaultTimeOut, TimeUnit.SECONDS)
                            .readTimeout(defaultTimeOut, TimeUnit.SECONDS)
                            .writeTimeout(defaultTimeOut, TimeUnit.SECONDS)
                            .addInterceptor(ChuckInterceptor(context))
                            .build())
                        .build()
                }
            }
            return if (BuildConfig.DEBUG) {
                debug().create(URSmsKitApiService::class.java)
            } else
                release().create(URSmsKitApiService::class.java)
        }
    }
}