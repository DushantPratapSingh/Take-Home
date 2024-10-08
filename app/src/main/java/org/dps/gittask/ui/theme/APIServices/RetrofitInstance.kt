package org.dps.gittask.ui.theme.APIServices

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: GitHubApiService by lazy {
        Retrofit.Builder().baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(GitHubApiService::class.java)
    }
}