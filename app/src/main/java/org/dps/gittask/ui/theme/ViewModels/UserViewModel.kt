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
import org.dps.gittask.ui.theme.model.User

sealed class UserUiState {
    object Loading : UserUiState()
    data class Success(val user: User) : UserUiState()
    data class Error(val message: String) : UserUiState()
}

class UserViewModel(private val repository: GitHubRepository) : ViewModel() {

    var user by mutableStateOf<User?>(null)

    private val _uiState = MutableStateFlow<UserUiState>(UserUiState.Loading)
    val uiState: StateFlow<UserUiState> = _uiState

    fun fetchUser(userId: String) {
        viewModelScope.launch {
            _uiState.value = UserUiState.Loading
            try {
                val user = repository.getUser(userId)
                _uiState.value = UserUiState.Success(user)
            } catch (e: Exception) {
                _uiState.value = UserUiState.Error(e.localizedMessage ?: "An error occurred")

            }
        }
    }
}
