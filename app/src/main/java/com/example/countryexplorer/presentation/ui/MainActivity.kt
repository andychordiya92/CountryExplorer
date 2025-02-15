package com.example.countryexplorer.presentation.ui

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.countryexplorer.databinding.ActivityMainBinding
import com.example.countryexplorer.presentation.adapter.CountriesAdapter
import com.example.countryexplorer.presentation.viewmodel.CountriesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CountriesViewModel by viewModels()
    private val adapter = CountriesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupSearch()
        setupFilter()

        if (!isConnected()) {
            Toast.makeText(this, "No internet connectivity", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.countries.observe(this) { countries ->
            adapter.submitList(countries)
            binding.tvNoResults.isVisible = countries.isEmpty()
            binding.recyclerView.isVisible = countries.isNotEmpty()
        }

        viewModel.error.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchCountry(newText.orEmpty())
                return true
            }
        })
    }

    private fun setupFilter() {
        val populationOptions = listOf("All", "<1M", "<5M", "<10M")
        binding.spinnerFilter.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, populationOptions)

        binding.btnFilter.setOnClickListener {
            binding.spinnerFilter.isVisible = !binding.spinnerFilter.isVisible
        }

        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedOption = populationOptions[position]
                viewModel.filterByPopulation(
                    when (selectedOption) {
                        "<1M" -> 1_000_000
                        "<5M" -> 5_000_000
                        "<10M" -> 10_000_000
                        else -> null
                    }
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun isConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo?.isConnected == true
    }
}
