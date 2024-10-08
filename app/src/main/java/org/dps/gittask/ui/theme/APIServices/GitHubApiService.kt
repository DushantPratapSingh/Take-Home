package org.dps.gittask.ui.theme.APIServices

import org.dps.gittask.ui.theme.model.Repository
import org.dps.gittask.ui.theme.model.User
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubApiService{
    @GET("users/{userId}")
    suspend fun getUser(@Path("userId")userId:String):User

    @GET("users/{userId}/repos")
    suspend fun getUserRepos(@Path("userId")userId: String):List<Repository>
}