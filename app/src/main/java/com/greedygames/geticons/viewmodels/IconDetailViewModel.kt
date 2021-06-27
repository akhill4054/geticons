package com.greedygames.geticons.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.greedygames.geticons.data.models.Icon
import com.greedygames.geticons.repositories.IconRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class IconDetailsViewModel(
    application: Application,
    private val iconId: Int
) : AndroidViewModel(application) {

    private val iconRepository = IconRepository.getInstance(application)

    private val _iconDetails = MutableLiveData<IconDetailsResponse>()
    val iconDetails: LiveData<IconDetailsResponse> = _iconDetails

    init {
        // Initial API call.
        loadIconDetails()
    }

    fun loadIconDetails() {
        if (_iconDetails.value is IconDetailsResponse.Loading) {
            // To avoid multiple API calls at the same time
            return
        }

        // Update state
        _iconDetails.value = IconDetailsResponse.Loading

        viewModelScope.launch(Dispatchers.IO) {
            runCatching { iconRepository.getIconDetails(iconId) }
                .onSuccess { icon ->
                    // Update state
                    _iconDetails.postValue(IconDetailsResponse.Success(icon))
                }
                .onFailure {
                    // Update state
                    _iconDetails.postValue(IconDetailsResponse.Error)
                }
        }
    }

    sealed class IconDetailsResponse {
        object Loading : IconDetailsResponse()
        class Success(val icon: Icon) : IconDetailsResponse()
        object Error : IconDetailsResponse()
    }

    class IconDetailsViewModelFactory(
        private val application: Application,
        private val iconId: Int
    ) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return IconDetailsViewModel(application, iconId) as T
        }
    }
}