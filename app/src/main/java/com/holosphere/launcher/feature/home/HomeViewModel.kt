package com.holosphere.launcher.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.holosphere.launcher.domain.model.LauncherApp
import com.holosphere.launcher.domain.repository.AppsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val title: String = "HoloSphere",
    val subtitle: String = "O teu espaço de aplicações",
    val apps: List<LauncherApp> = emptyList(),
    val isLoading: Boolean = true
)

class HomeViewModel(private val appsRepository: AppsRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            appsRepository.observeApps().collect { apps -> _uiState.update { it.copy(apps = apps, isLoading = false) } }
        }
        appsRepository.refresh()
    }

    fun launch(app: LauncherApp) = appsRepository.launch(app)
}

class HomeViewModelFactory(private val appsRepository: AppsRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = HomeViewModel(appsRepository) as T
}
