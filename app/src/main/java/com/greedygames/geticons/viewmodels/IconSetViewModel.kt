package com.greedygames.geticons.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.greedygames.geticons.data.models.Icon
import com.greedygames.geticons.data.models.IconSet
import com.greedygames.geticons.repositories.IconSetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class IconSetViewModel(
    application: Application,
    private val iconSetId: Int
) : AndroidViewModel(application) {

    private val iconSetRepository = IconSetRepository.getInstance(application)

    private val _iconSetDetailsData = MutableLiveData<IconSetDetailsResponse>()
    val iconSetDetailsData: LiveData<IconSetDetailsResponse> = _iconSetDetailsData

    private val _iconListData = MutableLiveData<IconSetIconsResponse>()
    val iconSetIconsData: LiveData<IconSetIconsResponse> = _iconListData

    init {
        // Initial API calls.
        loadIcons()
        loadIconSetDetails()
    }

    private fun loadIconSetDetails() {
        // Update state
        _iconSetDetailsData.value = IconSetDetailsResponse.Loading

        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                iconSetRepository.getIconSetDetails(iconSetId).body()!!
            }.onSuccess {
                _iconSetDetailsData.postValue(
                    IconSetDetailsResponse.Success(it)
                )
            }.onFailure {
                _iconSetDetailsData.postValue(
                    IconSetDetailsResponse.Error
                )
            }
        }
    }

    private fun loadIcons() {
        if (iconSetIconsData.value is IconSetIconsResponse.Loading) {
            // To avoid multiple API calls at once.
            return
        }
        // Update state
        _iconListData.value = IconSetIconsResponse.Loading

        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                iconSetRepository.getIconSetIcons(iconSetId)
            }.onSuccess { result ->
                // Notify observers
                _iconListData.postValue(
                    IconSetIconsResponse.Success(result.icons)
                )
            }.onFailure {
                // Notify observers
                _iconListData.postValue(
                    IconSetIconsResponse.Error
                )
            }
        }
    }

    fun retry() {
        // Update state
        _iconSetDetailsData.value = IconSetDetailsResponse.Loading
        viewModelScope.launch {
            delay(1500)
            loadIconSetDetails()
            loadIcons()
        }
    }

    fun loadMoreIcons() {
        loadIcons()
    }

    // States provided by this viewModel.
    sealed class IconSetDetailsResponse {
        object Loading : IconSetDetailsResponse()
        class Success(val details: IconSet) : IconSetDetailsResponse()
        object Error : IconSetDetailsResponse()
    }

    sealed class IconSetIconsResponse {
        object Loading : IconSetIconsResponse()
        class Success(val icons: List<Icon>) : IconSetIconsResponse()
        object Error : IconSetIconsResponse()
    }

    class IconSetViewModelFactory(
        private val application: Application,
        private val iconSetId: Int
    ) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return IconSetViewModel(application, iconSetId) as T
        }
    }
}