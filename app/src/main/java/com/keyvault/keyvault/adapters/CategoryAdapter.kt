package com.keyvault.keyvault.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.keyvault.keyvault.R
import com.keyvault.keyvault.databinding.ItemHeaderBinding
import com.keyvault.keyvault.models.CategoryModel

class CategoryAdapter(private val onCategoryItemClicked: (CategoryModel) -> Unit) :
    ListAdapter<CategoryModel, CategoryAdapter.CategoryViewHolder>(CategoryDiffUtil()) {

    private var selectedItem: Int = -1

    inner class CategoryViewHolder(private val binding: ItemHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(categoryModel: CategoryModel, isSelected: Boolean) {
            binding.categoryName.text = categoryModel.categoryName

            when (categoryModel.categoryName) {
                "All" -> binding.headerImage.setImageResource(R.drawable.all)
                "Social Media" -> binding.headerImage.setImageResource(R.drawable.social_media)
                "Entertainment" -> binding.headerImage.setImageResource(R.drawable.entertainment)
                "E-commerce" -> binding.headerImage.setImageResource(R.drawable.shopping_online)
                "Banking" -> binding.headerImage.setImageResource(R.drawable.banking)
                "Work" -> binding.headerImage.setImageResource(R.drawable.work)
                "Travel" -> binding.headerImage.setImageResource(R.drawable.travel)
                "Health" -> binding.headerImage.setImageResource(R.drawable.health)
                "Gaming" -> binding.headerImage.setImageResource(R.drawable.gaming)
                "Cloud Services" -> binding.headerImage.setImageResource(R.drawable.cloud)
                "Utilities" -> binding.headerImage.setImageResource(R.drawable.utilities)
            }

            val backgroundColor = if (isSelected) {
                ContextCompat.getColor(itemView.context, R.color.card_selected)
            } else {
                ContextCompat.getColor(itemView.context, R.color.card_unselected)
            }

            binding.cardView.setBackgroundColor(backgroundColor)

            itemView.setOnClickListener {
                val previousSelectedItem = selectedItem
                selectedItem = absoluteAdapterPosition
                notifyItemChanged(previousSelectedItem)
                notifyItemChanged(selectedItem)
                onCategoryItemClicked(categoryModel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val categoryModel = getItem(position)
        holder.bind(categoryModel, position == selectedItem)
    }
}

class CategoryDiffUtil : DiffUtil.ItemCallback<CategoryModel>() {
    override fun areItemsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
        return oldItem.categoryId == newItem.categoryId
    }

    override fun areContentsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
        return oldItem == newItem
    }
}