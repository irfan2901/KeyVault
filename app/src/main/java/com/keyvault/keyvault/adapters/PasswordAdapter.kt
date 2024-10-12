package com.keyvault.keyvault.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.keyvault.keyvault.databinding.ItemContentBinding
import com.keyvault.keyvault.models.PasswordModel

class PasswordAdapter(private val onPasswordItemClicked: (PasswordModel) -> Unit) :
    ListAdapter<PasswordModel, PasswordAdapter.PasswordViewHolder>(PasswordDiffUtil()) {

    inner class PasswordViewHolder(private val binding: ItemContentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(passwordModel: PasswordModel) {
            val passwordInitial = passwordModel.accountName?.firstOrNull()?.toString() ?: " "
            binding.passwordImage.text = passwordInitial
            binding.accountName.text = passwordModel.accountName
            binding.accountId.text = passwordModel.accountId

            binding.cardView.setOnClickListener {
                Log.d("PasswordAdapter", "Item clicked: ${passwordModel.accountName}")
                onPasswordItemClicked(passwordModel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordViewHolder {
        val binding = ItemContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PasswordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PasswordViewHolder, position: Int) {
        val passwordModel = getItem(position)
        holder.bind(passwordModel)
    }
}

class PasswordDiffUtil : DiffUtil.ItemCallback<PasswordModel>() {
    override fun areItemsTheSame(oldItem: PasswordModel, newItem: PasswordModel): Boolean {
        return oldItem.passwordId == newItem.passwordId
    }

    override fun areContentsTheSame(oldItem: PasswordModel, newItem: PasswordModel): Boolean {
        return oldItem == newItem
    }
}