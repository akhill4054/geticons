package com.greedygames.geticons.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.greedygames.geticons.data.models.Author
import com.greedygames.geticons.data.models.IconSet
import com.greedygames.geticons.repositories.IconSetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AuthorDetailsViewModel(
    application: Application,
    private val authorId: Int?,
    private val userId: Int?
) : AndroidViewModel(application) {

    private val iconSetRepository = IconSetRepository.getInstance(application)

    private val _authorDetails = MutableLiveData<AuthorDetailsResponse>()
    val authorDetails: LiveData<AuthorDetailsResponse> = _authorDetails

    private val _authorIconSets = MutableLiveData<AuthorIconSetsResponse>()
    val authorIconSets: LiveData<AuthorIconSetsResponse> = _authorIconSets

    // List to store icon-sets of author
    private val iconSets = mutableListOf<IconSet>()

    init {
        // Initial API calls
        loadAuthorDetails()
        loadIconSets()
    }

    private fun loadAuthorDetails() {
        if (authorDetails.value is AuthorDetailsResponse.Loading) {
            // To avoid multiple API calls at the same time
            return
        }

        // Update state
        _authorDetails.value = AuthorDetailsResponse.Loading

        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                if (authorId != null) {
                    iconSetRepository.getAuthorDetails(authorId)
                } else {
                    iconSetRepository.getUserDetails(userId!!)
                }
            }.onSuccess { author ->
                // Update state
                _authorDetails.postValue(AuthorDetailsResponse.Success(author))
            }.onFailure {
                // Update state
                _authorDetails.postValue(AuthorDetailsResponse.Error)
            }
        }
    }

    fun loadIconSets() {
        if (authorIconSets.value is AuthorIconSetsResponse.Loading) {
            // To avoid multiple API calls at the same time
            return
        }
        _authorIconSets.value = AuthorIconSetsResponse.Loading(iconSets)

        val after = if (iconSets.isNotEmpty()) {
            iconSets[iconSets.size - 1].id
        } else {
            null
        }

        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                if (authorId != null) {
                    iconSetRepository.getAuthorIconSets(authorId, after)
                } else {
                    iconSetRepository.getUserIconSets(userId!!, after)
                }
            }.onSuccess {
                // Append list with new icons
                iconSets.addAll(it.iconSets)

                _authorIconSets.postValue(
                    AuthorIconSetsResponse.Success(iconSets)
                )
            }.onFailure {
                _authorIconSets.postValue(
                    AuthorIconSetsResponse.Error(iconSets)
                )
            }
        }
    }

    fun retry() {
        // Update state
        _authorDetails.value = AuthorDetailsResponse.Loading
        viewModelScope.launch {
            // Wait for some time before retrying
            delay(1500)
            loadAuthorDetails()
            loadIconSets()
        }
    }

    sealed class AuthorDetailsResponse {
        object Loading : AuthorDetailsResponse()
        class Success(val author: Author) : AuthorDetailsResponse()
        object Error : AuthorDetailsResponse()
    }

    sealed class AuthorIconSetsResponse {
        class Loading(val currentList: List<IconSet>) : AuthorIconSetsResponse()
        class Success(val newList: List<IconSet>) : AuthorIconSetsResponse()
        class Error(val currentList: List<IconSet>) : AuthorIconSetsResponse()
    }

    class AuthorDetailsViewModelFactory(
        private val application: Application,
        private val authorId: Int?,
        private val userId: Int?
    ) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AuthorDetailsViewModel(
                application,
                authorId = authorId,
                userId = userId
            ) as T
        }
    }
}