package com.greedygames.geticons.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.greedygames.geticons.R
import com.greedygames.geticons.data.models.Icon
import com.greedygames.geticons.databinding.ItemIconBinding

class IconListAdapter(
    itemRequestListener: ItemRequestListener,
    itemClickListener: ItemClickListener
) : ListAdapter<Any, RecyclerView.ViewHolder>(DiffCallback()) {

    private val _itemRequestListener = itemRequestListener
    private val _itemClickListener = itemClickListener

    // Dummy object for progress item.
    object DummyProgressItem

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position) is Icon) {
            ITEM_TYPE_ICON
        } else {
            ITEM_TYPE_PROGRESS
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_TYPE_ICON) {
            IconViewHolder.from(parent, _itemClickListener)
        } else {
            ProgressItemHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // Bind data.
        if (holder is IconViewHolder) {
            holder.bind(getItem(position) as Icon)
        }

        if (position == itemCount - 4) {
            enableProgress()
            // Last item almost reached, request more items.
            _itemRequestListener.onMoreItemsRequested()
        }
    }

    private fun enableProgress() {
        if(currentList[currentList.size - 1] !is DummyProgressItem) {
            val newList = currentList.toMutableList()

            // Add dummy items for progress.
            newList.add(DummyProgressItem)
            newList.add(DummyProgressItem)

            submitList(newList)
        }
    }

    fun disableProgress() {
        if (currentList.isNotEmpty() && currentList[currentList.size - 1] is DummyProgressItem) {
            val newList = currentList.toMutableList()

            // Remove progress dummy items.
            newList.removeLast()
            newList.removeLast()

            submitList(newList)
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is Icon && newItem is Icon -> {
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

    class IconViewHolder(
        binding: ItemIconBinding,
        private val itemClickListener: ItemClickListener
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        private val _binding = binding

        init {
            // Click listeners.
            _binding.info.setOnClickListener(this)
            _binding.download.setOnClickListener(this)
            _binding.root.setOnClickListener(this)
        }

        fun bind(icon: Icon) {
            _binding.icon = icon
        }

        override fun onClick(view: View?) {
            // Reference to clicked IconSet object.
            val icon = _binding.icon!!

            itemClickListener.onIconSetItemClicked(
                when (view!!.id) {
                    R.id.info -> {
                        // Info button clicked, request to
                        // show item info.
                        CLICK_ID_SHOW_INFO
                    }
                    R.id.download -> {
                        // Request to donload the icon.
                        CLICK_ID_DOWNLOAD
                    }
                    else -> {
                        // Root view clicked, request to show
                        // item details.
                        CLICK_ID_SHOW_DETAILS
                    }
                },
                icon
            )
        }

        companion object {
            fun from(
                parent: ViewGroup,
                itemClickListener: ItemClickListener
            ): IconViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = DataBindingUtil.inflate<ItemIconBinding>(
                    inflater,
                    R.layout.item_icon,
                    parent,
                    false
                )

                return IconViewHolder(binding, itemClickListener)
            }
        }
    }

    class ProgressItemHolder(view: View) : RecyclerView.ViewHolder(view) {

        companion object {
            fun from(parent: ViewGroup): ProgressItemHolder {
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(
                    R.layout.item_icon_progress,
                    parent,
                    false
                )
                return ProgressItemHolder(view)
            }
        }
    }

    companion object {
        private const val ITEM_TYPE_ICON = 0
        private const val ITEM_TYPE_PROGRESS = 1

        const val CLICK_ID_SHOW_DETAILS = 0
        const val CLICK_ID_SHOW_INFO = 1
        const val CLICK_ID_DOWNLOAD = 2
    }

    interface ItemRequestListener {
        fun onMoreItemsRequested()
    }

    interface ItemClickListener {
        fun onIconSetItemClicked(clickId: Int, icon: Icon)
    }
}