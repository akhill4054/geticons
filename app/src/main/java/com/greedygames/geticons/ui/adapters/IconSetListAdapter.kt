package com.greedygames.geticons.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.greedygames.geticons.R
import com.greedygames.geticons.data.models.IconSet
import com.greedygames.geticons.databinding.ItemIconSetBinding

class IconSetListAdapter(
    itemRequestListener: ItemRequestListener,
    itemClickListener: ItemClickListener
) : ListAdapter<Any, RecyclerView.ViewHolder>(DiffCallback()) {

    private val _itemRequestListener = itemRequestListener
    private val _itemClickListener = itemClickListener

    // Dummy object for progress item.
    object DummyProgressItem

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position) is IconSet) {
            TYPE_ICON_SET
        } else {
            TYPE_PROGRESS
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ICON_SET) {
            IconSetViewHolder.from(parent, _itemClickListener)
        } else {
            ProgressItemViewHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is IconSetViewHolder) {
            holder.bind(getItem(position) as IconSet)

            if (position == itemCount - 1) {
                // Last item reached, request more items.
                _itemRequestListener.onMoreItemsRequested()
                // UI indication.
                enableProgress()
            }
        }
    }

    private fun enableProgress() {
        val newList = currentList.toMutableList()
        // Add dummy item to show progress.
        newList.add(DummyProgressItem)
        submitList(newList)
    }

    fun disableProgress() {
        if (currentList.isNotEmpty() && currentList[currentList.size - 1] is DummyProgressItem) {
            val newList = currentList.toMutableList()
            // Remove dummy item for progress.
            newList.removeAt(newList.size - 1)
            submitList(newList)
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is IconSet && newItem is IconSet -> {
                    oldItem.id == newItem.id
                }
                oldItem is DummyProgressItem && newItem is DummyProgressItem -> {
                    true
                }
                else -> {
                    false
                }
            }
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            // As contents in the items will never change.
            return true
        }
    }

    class IconSetViewHolder(
        binding: ItemIconSetBinding,
        private val itemClickListener: ItemClickListener
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        private val _binding = binding

        init {
            // Click listeners.
            _binding.info.setOnClickListener(this)
            _binding.root.setOnClickListener(this)
        }

        fun bind(iconSet: IconSet) {
            _binding.iconSet = iconSet
        }

        override fun onClick(view: View?) {
            // Reference to clicked IconSet object.
            val iconSet = _binding.iconSet!!

            itemClickListener.onIconSetItemClicked(
                view!!,
                if (view.id == R.id.info) {
                    // Info button clicked, request to
                    // show item info.
                    CLICK_ID_SHOW_INFO
                } else {
                    // Root view clicked, request to show
                    // item details.
                    CLICK_ID_SHOW_DETAILS
                },
                iconSet,
            )
        }

        companion object {
            fun from(
                parent: ViewGroup,
                itemClickListener: ItemClickListener
            ): IconSetViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = DataBindingUtil.inflate<ItemIconSetBinding>(
                    inflater,
                    R.layout.item_icon_set,
                    parent,
                    false
                )

                return IconSetViewHolder(binding, itemClickListener)
            }
        }
    }

    class ProgressItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        companion object {
            fun from(parent: ViewGroup): ProgressItemViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(
                    R.layout.item_progress,
                    parent,
                    false
                )

                return ProgressItemViewHolder(view)
            }
        }
    }

    companion object {
        // Item types.
        private const val TYPE_ICON_SET = 0
        private const val TYPE_PROGRESS = 1

        const val CLICK_ID_SHOW_DETAILS = 0
        const val CLICK_ID_SHOW_INFO = 1
    }

    interface ItemRequestListener {
        fun onMoreItemsRequested()
    }

    interface ItemClickListener {
        fun onIconSetItemClicked(view: View, clickId: Int, iconSet: IconSet)
    }
}