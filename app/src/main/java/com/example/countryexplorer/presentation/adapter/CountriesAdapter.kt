package com.example.countryexplorer.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.countryexplorer.R
import com.example.countryexplorer.data.model.Country
import com.example.countryexplorer.databinding.ItemCountryBinding
import com.example.countryexplorer.toMillionFormat

class CountriesAdapter :
    ListAdapter<Country, CountriesAdapter.CountryViewHolder>(CountryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val binding = ItemCountryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CountryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CountryViewHolder(private val binding: ItemCountryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(country: Country) {
            binding.apply {
                countryNameTextView.text = country.name
                capitalTextView.text = "Capital: ${country.capital}"
                populationTextView.text = "Population: ${country.population.toMillionFormat()}"
                Glide.with(root.context)
                    .load(country.media.flag.takeIf { it.isNotEmpty() } ?: R.drawable.default_image)
                    .placeholder(R.drawable.default_image)
                    .error(R.drawable.default_image)
                    .into(flagImageView)
            }
        }
    }

    class CountryDiffCallback : DiffUtil.ItemCallback<Country>() {
        override fun areItemsTheSame(oldItem: Country, newItem: Country) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Country, newItem: Country) = oldItem == newItem
    }
}