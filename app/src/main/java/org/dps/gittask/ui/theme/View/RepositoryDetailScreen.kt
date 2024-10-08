package org.dps.gittask.ui.theme.View

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.dps.gittask.ui.theme.Repository.GitHubRepository
import org.dps.gittask.ui.theme.ViewModels.ReposUiState
import org.dps.gittask.ui.theme.ViewModels.RepositoryViewModel
import org.dps.gittask.ui.theme.model.Repository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepositoryDetailScreen(userId: String, repoName: String) {
    val repository = GitHubRepository()
    val repoViewModel = remember { RepositoryViewModel(repository) }

    val reposUiState by repoViewModel.uiState.collectAsState()

    var currentRepo: Repository? by remember { mutableStateOf(null) }
    var totalForks by remember { mutableStateOf(0) }

    LaunchedEffect(key1 = userId) {
        repoViewModel.fetchUserRepos(userId)
    }

    LaunchedEffect(key1 = reposUiState) {
        when (reposUiState) {
            is ReposUiState.Success -> {
                val repos = (reposUiState as ReposUiState.Success).repos
                currentRepo = repos.find { it.name == repoName }
                totalForks = repos.sumOf { it.forks }
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Blue,
                    titleContentColor = Color.White,
                ),
                title = {
                    Text("Repository Detail")
                }
            )
        }
    ) { paddingValues ->
        if (currentRepo != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(paddingValues)
            ) {
                Text(text = currentRepo!!.name, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = currentRepo!!.description ?: "No description", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Stars: ${currentRepo!!.stargazers_count}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Forks: ${currentRepo!!.forks}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Total Forks Across All Repos: $totalForks", style = MaterialTheme.typography.bodyMedium)
                if (totalForks > 5000) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Badge(
                        containerColor = Color.Blue,
                        contentColor = Color.White
                    ) {
                        Text(text = "★", fontSize = 12.sp, color = if (totalForks > 5000) Color.Red else Color.Yellow)
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}