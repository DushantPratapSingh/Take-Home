package org.dps.gittask.ui.theme.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.dps.gittask.ui.theme.Repository.GitHubRepository
import org.dps.gittask.ui.theme.model.Repository

sealed class ReposUiState {
    object Loading : ReposUiState()
    data class Success(val repos: List<Repository>) : ReposUiState()
    data class Error(val message: String) : ReposUiState()
}

class RepositoryViewModel(private val repository: GitHubRepository) : ViewModel() {
    var repos by mutableStateOf<List<RepositoryViewModel>>(emptyList())

    private val _uiState = MutableStateFlow<ReposUiState>(ReposUiState.Loading)
    val uiState: StateFlow<ReposUiState> = _uiState

    fun fetchUserRepos(userId: String) {
        viewModelScope.launch {
            _uiState.value = ReposUiState.Loading
            try {
                val repos = repository.getUserRepos(userId)
                _uiState.value = ReposUiState.Success(repos)
            } catch (e: Exception) {
                _uiState.value = ReposUiState.Error(e.localizedMessage ?: "An error occurred")

            }
        }
    }
}
