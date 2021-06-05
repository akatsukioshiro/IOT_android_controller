package com.example.chronos

import android.app.DownloadManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.RequestBody
import java.io.IOException
//import javax.security.auth.callback.Callback
import java.security.cert.CertificateException
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val loc=findViewById<TextView>(R.id.bt_01)
        val switch=findViewById<Switch>(R.id.bt_s01)
        val status=findViewById<TextView>(R.id.bt_st01)
        loc.text="Bedroom TubeLight"
        status.text="OFF"
        // Instantiate the RequestQueue.
        //var client = OkHttpClient()
        //var request = OkHttpRequest(client)

        fun getUnsafeOkHttpClient(): OkHttpClient.Builder {
            try {
                // Create a trust manager that does not validate certificate chains
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                    }

                    override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                        return arrayOf()
                    }
                })

                // Install the all-trusting trust manager
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, java.security.SecureRandom())
                // Create an ssl socket factory with our all-trusting manager
                val sslSocketFactory = sslContext.socketFactory

                val builder = OkHttpClient.Builder()
                builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                builder.hostnameVerifier ( hostnameVerifier = HostnameVerifier{ _, _ -> true })

                return builder
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }



//        var client = OkHttpClient()
        var client = getUnsafeOkHttpClient().build()

        fun GET(url: String, callback: Callback): Call {
            val request = Request.Builder()
                    .url(url)
                    .build()

            val call = client.newCall(request)
            call.enqueue(callback)
            return call
        }

        fun getDetails(url: String) {
            var client = OkHttpClient()
            //var request = Request.Builder().url(url).build()

            GET(url, callback = object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    println("Fail: "+e.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseData = response.body.toString()
                    println(responseData)
                }
            })
        }
        val url = "https://chronos.local/led?stat="

        //getDetails(url,status.text.decapitalize())

//        val get = GET(url=url)
//        println(get)

        switch.setOnClickListener {
            if (status.text == "ON") {
                status.text = "OFF"
                val url = "https://chronos.local/led?stat=off"
                getDetails(url)
            }
            else {
                status.text = "ON"
                val url = "https://chronos.local/led?stat=on"
                getDetails(url)
            }
        }
    }
}