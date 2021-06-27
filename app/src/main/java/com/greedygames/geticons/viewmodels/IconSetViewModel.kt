package com.greedygames.geticons.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.greedygames.geticons.data.IconSetDetails
import com.greedygames.geticons.data.models.Icon
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

    // List of icons under the icon set.
    @Volatile
    private var icons = mutableListOf<Icon>()

    private val _iconListData = MutableLiveData<IconListResponse>()
    val iconListData: LiveData<IconListResponse> = _iconListData

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
        if (iconListData.value is IconListResponse.Loading) {
            // To avoid multiple API calls at once.
            return
        }
        // Update state
        _iconListData.value = IconListResponse.Loading(icons)

        val offset = icons.size

        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                iconSetRepository.getIconSetIcons(
                    iconSetId,
                    offset
                )
            }.onSuccess { result ->
                icons.addAll(result.icons)
                // Notify observers
                _iconListData.postValue(
                    IconListResponse.Success(icons)
                )
            }.onFailure {
                // Notify observers
                _iconListData.postValue(
                    IconListResponse.Error(icons)
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
        class Success(val details: IconSetDetails) : IconSetDetailsResponse()
        object Error : IconSetDetailsResponse()
    }

    sealed class IconListResponse {
        class Loading(val currentList: List<Icon>) : IconListResponse()
        class Success(val newList: List<Icon>) : IconListResponse()
        class Error(val currentList: List<Icon>) : IconListResponse()
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