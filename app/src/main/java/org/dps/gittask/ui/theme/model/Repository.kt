package org.dps.gittask.ui.theme.model

data class Repository(
    val name: String,
    val description: String?,
    val stargazers_count: Int,
    val forks: Int
)
