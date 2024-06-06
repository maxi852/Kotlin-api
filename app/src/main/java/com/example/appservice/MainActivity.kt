package com.example.appservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DogsAdapter
    private lateinit var spinner: Spinner
    private var dogsListImage = mutableListOf<String>()
    private var breedsList = mutableListOf<String>()

    val job = Job()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_main)
        recyclerView.layoutManager = LinearLayoutManager(this)
        spinner = findViewById(R.id.spinner)
        adapter = DogsAdapter(dogsListImage)
        recyclerView.adapter = adapter

        getListOfBreeds()
    }

    private fun getListOfBreeds() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(ApiService::class.java).getListOfBreed("breeds/list/all")
            val response = call.body()

            runOnUiThread {
                if (call.isSuccessful) {
                    val breedsMap = response?.message
                    if (breedsMap != null) {
                        for (breed in breedsMap.keys)
                            breedsList.add(breed)
                        setSpinner()

                    }
                }
            }

        }
    }

    private fun setSpinner() {
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, breedsList)
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                getListImages(breedsList[position])
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

    }

    private fun getListImages(breed: String?) {
        CoroutineScope(Dispatchers.IO + job).launch {
            val call = getRetrofit().create(ApiService::class.java).getListaImagenes("breed/$breed/images")
            val response = call.body()
            runOnUiThread {
                if (call.isSuccessful) {
                    //si la response es null, seteo listado vacio para que no rompa
                    val images = response?.images ?: emptyList()
                    dogsListImage.clear()
                    dogsListImage.addAll(images)
                    adapter.notifyDataSetChanged()
                }
            }

        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(URL_DOGS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    companion object {
        const val URL_DOGS = "https://dog.ceo/api/"
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}