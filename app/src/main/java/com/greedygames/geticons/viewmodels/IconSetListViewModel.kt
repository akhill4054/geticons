package com.greedygames.geticons.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.greedygames.geticons.ERROR_TYPICAL
import com.greedygames.geticons.data.models.IconSet
import com.greedygames.geticons.repositories.IconSetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class IconSetListViewModel(application: Application) : AndroidViewModel(application) {

    private val iconSetRepository = IconSetRepository.getInstance(application)

    private var iconSetList = mutableListOf<IconSet>()

    private val _iconSetListData = MutableLiveData<IconSetListData>()
    val iconSetListData: LiveData<IconSetListData> = _iconSetListData

    init {
        // Initial API call.
        loadItems()
    }

    private fun loadItems() {
        // To avoid multiple fetch requests at once.
        if (_iconSetListData.value !is FetchIsInProgress) {
            // Change state.
            // Notify observers.
            _iconSetListData.value = FetchIsInProgress(iconSetList)

            val after = if (iconSetList.isEmpty()) {
                null
            } else {
                iconSetList[iconSetList.size - 1].id
            }

            viewModelScope.launch(Dispatchers.IO) {
                val result = kotlin.runCatching {
                    iconSetRepository.getIconSets(after)
                }
                result.onSuccess {
                    // Append old list with new items.
                    iconSetList.addAll(it.iconSets)
                    // Notify observers.
                    _iconSetListData.postValue(Success(iconSetList))
                }.onFailure {
                    // Notify observers.
                    _iconSetListData.postValue(Error(currentList = iconSetList))
                }
            }
        }
    }

    fun loadMoreItems() {
        loadItems()
    }

    fun retry() {
        viewModelScope.launch {
            // Wait before retry.
            delay(1500)
            loadItems()
        }
    }

    fun refreshList() {
        // Reset existing list.
        iconSetList = mutableListOf()
        // Request to load new items.
        loadItems()
    }
}

// States provided by this ViewModel.
sealed class IconSetListData
class Success(val newList: List<IconSet>) : IconSetListData()
class FetchIsInProgress(val currentList: List<IconSet>) : IconSetListData()
class Error(
    val code: Int = ERROR_TYPICAL,
    val currentList: List<IconSet>
) : IconSetListData()