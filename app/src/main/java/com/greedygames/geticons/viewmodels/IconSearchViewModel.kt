package com.greedygames.geticons.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.greedygames.geticons.ERROR_TYPICAL
import com.greedygames.geticons.data.models.Category
import com.greedygames.geticons.data.models.Icon
import com.greedygames.geticons.data.models.Style
import com.greedygames.geticons.repositories.IconRepository
import com.greedygames.geticons.repositories.IconRepository.IconSearchFilter
import kotlinx.coroutines.*

class IconSearchViewModel(application: Application) : AndroidViewModel(application) {

    private val iconRepository = IconRepository.getInstance(application)

    @Volatile
    private var icons = mutableListOf<Icon>()

    @Volatile
    private var searchQuery = ""

    private var lastDispatchedSearch: Job? = null

    // LiveData for searched icon list
    private val _searchResult = MutableLiveData<IconListData>()
    val searchResult: LiveData<IconListData> = _searchResult

    var searchFilter = IconSearchFilter()
        private set

    init {
        // Initial API call.
        loadItems()
    }

    fun searchIcons(query: String?) {
        // Update locally stored query(for future ues).
        this.searchQuery = query ?: ""
        // Refresh list with new query.
        refreshList()
    }

    fun updatePremiumType(isPremium: Boolean?) {
        if (searchFilter.premium != isPremium) {
            searchFilter = searchFilter.copy(
                premium = isPremium
            )
            refreshList()
        }
    }

    /**
     * @param license
     * @see IconSearchFilter.license
     * */
    fun updateLicenseFilter(license: Int) {
        if (searchFilter.license != license) {
            searchFilter = searchFilter.copy(
                license = license
            )
            refreshList()
        }
    }

    fun clearFilter() {
        val newFilter = IconSearchFilter()
        if (searchFilter != newFilter) {
            searchFilter = newFilter
            refreshList()
        }
    }

    private fun loadItems() {
        // Change state.
        // Notify observers.
        _searchResult.value = IconListData.Loading(
            isFreshSearch = icons.isEmpty()
        )

        val offset = icons.size
        val query = searchQuery
        val filter = searchFilter

        viewModelScope.launch(Dispatchers.IO) {
            if (query.isNotEmpty()) {
                // Wait before performing the search,
                // as the query might change.
                delay(200)
            }

            // To avoid multiple API calls.
            lastDispatchedSearch?.cancelAndJoin()

            lastDispatchedSearch = launch {
                try {
                    val result = iconRepository.searchIcons(
                        offset = offset,
                        searchQuery = query,
                        searchFilter = filter
                    )
                    // Append old list with new items.
                    icons.addAll(result.icons)
                    // Notify observers.
                    _searchResult.postValue(IconListData.Success(icons))
                } catch (e: Exception) {
                    // Ignore job cancellation exceptions.
                    if (e !is CancellationException) {
                        // Notify observers.
                        _searchResult.postValue(
                            IconListData.Error(
                                currentList = icons
                            )
                        )
                    }
                }
            }
        }
    }

    fun loadMoreItems() {
        loadItems()
    }

    fun retry() {
        viewModelScope.launch {
            // Notify observers.
            _searchResult.value = IconListData.Loading(
                isFreshSearch = icons.isEmpty()
            )
            // Wait before retry.
            delay(1500)
            loadItems()
        }
    }

    fun refreshList() {
        // Reset existing icon list.
        icons = mutableListOf()
        // Request to load new items.
        loadItems()
    }

    // States provided by this ViewModel.
    sealed class IconListData {
        class Loading(val isFreshSearch: Boolean = false) : IconListData()
        class Success(val newList: List<Icon>) : IconListData()
        class Error(
            val code: Int = ERROR_TYPICAL,
            val currentList: List<Icon>
        ) : IconListData()
    }
}