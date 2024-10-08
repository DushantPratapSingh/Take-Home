package org.dps.gittask.ui.theme.View

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import org.dps.gittask.ui.theme.Repository.GitHubRepository
import org.dps.gittask.ui.theme.ViewModels.ReposUiState
import org.dps.gittask.ui.theme.ViewModels.UserUiState
import org.dps.gittask.ui.theme.ViewModels.UserViewModel
import org.dps.gittask.ui.theme.ViewModels.RepositoryViewModel
import org.dps.gittask.ui.theme.model.Repository


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(userId: String, onRepoClick: (String) -> Unit) {
    val repository = GitHubRepository()
    val userViewModel = remember { UserViewModel(repository) }
    val repoViewModel = remember { RepositoryViewModel(repository) }

    val userUiState by userViewModel.uiState.collectAsState()
    val reposUiState by repoViewModel.uiState.collectAsState()

    LaunchedEffect(key1 = userId) {
        userViewModel.fetchUser(userId)
        repoViewModel.fetchUserRepos(userId)
    }
    Scaffold(topBar = {
        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Blue,
            titleContentColor = Color.White,
        ), title = {
            Text("User Profile")
        })

    }) { paddingValues ->
        when (userUiState) {
            is UserUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is UserUiState.Success -> {
                val user = (userUiState as UserUiState.Success).user
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    Image(
                        painter = rememberImagePainter(data = user.avatar_url),
                        contentDescription = "User Avatar",
                        modifier = Modifier
                            .size(140.dp)
                            .padding(end = 16.dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = user.name ?: "No Name",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    when (reposUiState) {
                        is ReposUiState.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        }

                        is ReposUiState.Success -> {
                            val repos = (reposUiState as ReposUiState.Success).repos
                            LazyColumn {
                                items(repos) { repo ->
                                    RepositoryItem(
                                        repo = repo,
                                        onClick = { onRepoClick(repo.name) })
                                    Divider()
                                }
                            }
                        }

                        is ReposUiState.Error -> {
                            Text(
                                text = (reposUiState as ReposUiState.Error).message,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }


            is UserUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (userUiState as UserUiState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

}

@Composable
fun RepositoryItem(repo: Repository, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 4.dp)
            .bottomShadow(3.dp),
        shape = RoundedCornerShape(6.dp),
        shadowElevation = 8.dp,
        color = Color.White,
    ) {
        Box(
            modifier = Modifier.background(color = Color.White)
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp)) {
                Text(text = repo.name, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = repo.description ?: "No description",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }


}

fun Modifier.bottomShadow(
    elevation: Dp,
    shadowColor: Color = Color.Black,
    alpha: Float = 0.2f
) = this.drawBehind {
    drawBottomShadow(elevation, shadowColor, alpha)
}

fun DrawScope.drawBottomShadow(elevation: Dp, shadowColor: Color, alpha: Float) {
    val shadowHeight = elevation.toPx()
    val path = Path().apply {
        moveTo(0f, size.height)
        lineTo(size.width, size.height)
        lineTo(size.width, size.height + shadowHeight)
        lineTo(0f, size.height + shadowHeight)
        close()
    }

    drawPath(
        path,
        color = shadowColor.copy(alpha = alpha)
    )
}