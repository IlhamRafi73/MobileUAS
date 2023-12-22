package com.example.mobileuas

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.mobileuas.database.Movie
import com.example.mobileuas.databinding.ActivityMovieDetailsBinding

class MovieDetails : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve movie details from the intent
        val movie: Movie? = intent.getParcelableExtra("movie")

        // Display movie details in views
        if (movie != null) {
            binding.movieTitle.text = movie.title
            binding.movieProducer.text = movie.producer
            binding.rating.text = movie.rating
            binding.Desription.text = movie.description

            // Use Glide to load the movie image into the ImageView
            Glide.with(this)
                .load(movie.imageUrl)
                .into(binding.movieImage)
        }
    }
}
