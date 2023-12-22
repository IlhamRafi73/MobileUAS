package com.example.mobileuas


import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.mobileuas.database.Movie
import com.example.mobileuas.databinding.ActivityEditMovieBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class EditMovie : AppCompatActivity() {

    private lateinit var binding: ActivityEditMovieBinding
    private var imageUri: Uri? = null
    private var updateId: String? = null
    private lateinit var storageReference: StorageReference
    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let {
                imageUri = it
                binding.imageView.setImageURI(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storageReference = FirebaseStorage.getInstance().reference

        // Retrieve movie details from the intent
        val movie: Movie? = intent.getParcelableExtra("movie")
        if (movie != null) {
            updateId = movie.id
            Glide.with(this)
                .load(movie.imageUrl)
                .into(binding.imageView)
            binding.editTextTitle.setText(movie.title)
            binding.editTextProducer.setText(movie.producer)
            binding.editTextRating.setText(movie.rating)
            binding.editTextDescription.setText(movie.description)
        }

        binding.btnAddImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            getContent.launch(intent)
        }

        binding.btnUpdateMovie.setOnClickListener {
            updateMovie()
        }
    }

    private fun updateMovie() {
        if (imageUri != null) {
            val imageRef = storageReference.child("images/${System.currentTimeMillis()}.jpg")
            imageRef.putFile(imageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val updatedMovie = Movie(
                            id = updateId ?: "",
                            imageUrl = uri.toString(),
                            title = binding.editTextTitle.text.toString(),
                            producer = binding.editTextProducer.text.toString(),
                            rating = binding.editTextRating.text.toString(),
                            description = binding.editTextDescription.text.toString()
                        )
                        updateMovieInFirestore(updatedMovie)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Image upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // If no new image is selected, update the movie with existing details
            val updatedMovie = Movie(
                id = updateId ?: "",
                imageUrl = "", // Use the existing image URL
                title = binding.editTextTitle.text.toString(),
                producer = binding.editTextProducer.text.toString(),
                rating = binding.editTextRating.text.toString(),
                description = binding.editTextDescription.text.toString()
            )
            updateMovieInFirestore(updatedMovie)
        }
    }

    private fun updateMovieInFirestore(updatedMovie: Movie) {
        val db = FirebaseFirestore.getInstance()
        db.collection("movies")
            .document(updateId ?: "")
            .set(updatedMovie)
            .addOnSuccessListener {
                // Load the updated image into the ImageView
                Glide.with(this)
                    .load(updatedMovie.imageUrl)
                    .into(binding.imageView)

                Toast.makeText(this, "Movie updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error updating movie: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}