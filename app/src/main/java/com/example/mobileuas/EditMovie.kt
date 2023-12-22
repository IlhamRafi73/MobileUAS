package com.example.mobileuas

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.example.mobileuas.database.Movie
import com.example.mobileuas.databinding.ActivityEditMovieBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class EditMovie : AppCompatActivity() {

    private lateinit var binding: ActivityEditMovieBinding
    private var imageUri: Uri? = null
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get movie data from intent
        val movie = intent.getSerializableExtra("movie") as Movie

        // Populate UI with movie data
        binding.apply {
            Glide.with(this@EditMovie)
                .load(movie.imageUrl)
                .into(imageView)
            editTextTitle.setText(movie.title)
            editTextProducer.setText(movie.producer)
            editTextRating.setText(movie.rating)
            editTextDescription.setText(movie.description)
        }

        // Initialize Firebase Storage
        storageReference = FirebaseStorage.getInstance().reference


        // Handle update button click
        binding.btnUpdateMovie.setOnClickListener {
            // Update the movie in Firebase
            updateMovieInFirebase(movie)
        }
    }

    // Constants
    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    // Handle the result of the image picker
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            // Get the selected image URI
            imageUri = data.data
            // Display the selected image
            Glide.with(this)
                .load(imageUri)
                .into(binding.imageView)
        }
    }

    // Update movie in Firebase with the new image
    private fun updateMovieInFirebase(movie: Movie) {
        // Upload the new image to Firebase Storage if it's changed
        if (imageUri != null) {
            uploadImageToFirebase(movie)
        } else {
            // If the image is not changed, update the movie without uploading the image
            updateMovieDataInFirestore(movie, movie.imageUrl)
        }
    }

    // Upload the new image to Firebase Storage
    private fun uploadImageToFirebase(movie: Movie) {
        val imageFileName = UUID.randomUUID().toString()
        val imageRef = storageReference.child("images/$imageFileName")

        imageRef.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                // Image upload successful, get the download URL
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    // Update the movie data in Firestore with the new image URL
                    updateMovieDataInFirestore(movie, uri.toString())
                }
            }
            .addOnFailureListener { e ->
                // Handle image upload failure
                Log.e("EditMovie", "Image upload failed: ${e.message}")
            }
    }

    // Update movie data in Firestore with the new image URL
    private fun updateMovieDataInFirestore(movie: Movie, newImageUrl: String) {
        // Update other movie fields as well
        val updatedMovie = Movie(
            imageUrl = newImageUrl,
            title = binding.editTextTitle.text.toString(),
            producer = binding.editTextProducer.text.toString(),
            rating = binding.editTextRating.text.toString(),
            description = binding.editTextDescription.text.toString()
        )

        if (!movie.id.isNullOrEmpty()) {
            val db = FirebaseFirestore.getInstance()
            val movieRef = db.collection("movies").document(movie.id)
            movieRef.set(updatedMovie)
                .addOnSuccessListener {
                    // Update successful
                    // You may want to finish the activity or show a success message
                    finish()
                }
                .addOnFailureListener { e ->
                    // Handle update failure
                    Log.e("EditMovie", "Update failed: ${e.message}")
                    // You may want to show an error message
                }
        } else {
            Log.e("EditMovie", "Invalid movie ID")
            // You may want to show an error message
        }
    }
}
