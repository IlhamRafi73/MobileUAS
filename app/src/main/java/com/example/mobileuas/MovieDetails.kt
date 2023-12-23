package com.example.mobileuas

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
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

        // Set up the order button click listener
        binding.orderButton.setOnClickListener {
            showNotification("Your ticket was successfully bought", "Enjoy the movie!")
        }
    }

    private fun showNotification(title: String, content: String) {
        // Create a notification channel (required for Android Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "default_channel_id"
            val channelName = "Default Channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val notification = NotificationCompat.Builder(this, "default_channel_id")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Show the notification
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }
}
