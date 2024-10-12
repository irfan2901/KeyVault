package com.keyvault.keyvault.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.keyvault.keyvault.databinding.ItemHomeBinding
import com.keyvault.keyvault.models.PasswordModel

class HomeAdapter(private val onItemClick: (PasswordModel) -> Unit) :
    ListAdapter<PasswordModel, HomeAdapter.HomeViewHolder>(HomeDiffUtil()) {

    inner class HomeViewHolder(private val binding: ItemHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(passwordModel: PasswordModel) {
            Log.d(
                "HomeAdapter",
                "Binding position: ${absoluteAdapterPosition}, Account Name: ${passwordModel.accountName}"
            )

            val passwordInitial = passwordModel.accountName?.firstOrNull() ?: ' '
            binding.homePasswordImage.text = passwordInitial.toString()
            binding.homeAccountName.text = passwordModel.accountName
            binding.homeAccountId.text = passwordModel.accountId

            binding.cardView.setOnClickListener { onItemClick(passwordModel) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = ItemHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val passwordModel = getItem(position)
        holder.bind(passwordModel)
    }
}

class HomeDiffUtil : DiffUtil.ItemCallback<PasswordModel>() {
    override fun areItemsTheSame(oldItem: PasswordModel, newItem: PasswordModel): Boolean {
        return oldItem.passwordId == newItem.passwordId
    }

    override fun areContentsTheSame(oldItem: PasswordModel, newItem: PasswordModel): Boolean {
        return oldItem == newItem
    }
}