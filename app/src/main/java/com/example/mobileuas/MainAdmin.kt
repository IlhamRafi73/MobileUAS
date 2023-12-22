package com.example.mobileuas

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mobileuas.adapter.MovieAdapterAdmin
import com.example.mobileuas.database.Movie
import com.example.mobileuas.databinding.ActivityMainAdminBinding
import com.google.firebase.firestore.FirebaseFirestore

class MainAdmin : AppCompatActivity() {

    private lateinit var binding: ActivityMainAdminBinding
    private lateinit var movieAdapterAdmin: MovieAdapterAdmin
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        binding.MyRecyclerView.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)

        movieAdapterAdmin = MovieAdapterAdmin(emptyList()) { movie ->
            // Handle item click, start EditMovie activity with movie data
            val intent = Intent(this, EditMovie::class.java)
            intent.putExtra("movie", movie)
            startActivity(intent)
        }

        binding.MyRecyclerView.adapter = movieAdapterAdmin

        retrieveMoviesFromFirestore()


        binding.logoutButton.setOnClickListener {
            with(sharedPreferences.edit()) {
                putBoolean("isLoggedIn", false)
                remove("userRole")
                apply()
            }

            val intent = Intent(this@MainAdmin, LogRegActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.ButtonAddMovie.setOnClickListener {
            val intent = Intent(this, AddMovie::class.java)
            startActivity(intent)
        }
    }

    private fun retrieveMoviesFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("movies")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Handle errors
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val moviesList = mutableListOf<Movie>()
                    for (document in snapshot.documents) {
                        val movie = document.toObject(Movie::class.java)
                        movie?.let { moviesList.add(it) }
                    }
                    movieAdapterAdmin.updateMoviesList(moviesList)
                }
            }
    }
}