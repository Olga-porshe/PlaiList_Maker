
package com.example.plailistmaker

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkClient(context: Context) {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .client(OkHttpClient.Builder().build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(ItunesApi::class.java)

    fun searchTracks(term: String, callback: Callback<ItunesSearchResponse>) {
        api.search(term).enqueue(callback)
    }
}