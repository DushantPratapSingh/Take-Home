package org.dps.gittask.ui.theme.Repository

import org.dps.gittask.ui.theme.APIServices.RetrofitInstance
import org.dps.gittask.ui.theme.model.Repository
import org.dps.gittask.ui.theme.model.User

class GitHubRepository {
    suspend fun getUser(userId: String): User {
        return RetrofitInstance.api.getUser(userId)
    }

    suspend fun getUserRepos(userId: String): List<Repository> {
        return RetrofitInstance.api.getUserRepos(userId)
    }
}