package com.example.mobileuas

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.mobileuas.database.Movie
import com.example.mobileuas.databinding.ActivityAddMovieBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AddMovie : AppCompatActivity() {

    private lateinit var binding: ActivityAddMovieBinding
    private var imageUri: Uri? = null
    private lateinit var storageRef: StorageReference

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
        binding = ActivityAddMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storageRef = FirebaseStorage.getInstance().reference

        binding.btnAddImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            getContent.launch(intent)
        }

        binding.btnAddMovie.setOnClickListener {
            uploadImageAndAddMovie()
        }

    }
    private fun uploadImageAndAddMovie() {
        if (imageUri != null) {
            val imageRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")
            imageRef.putFile(imageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val movie = Movie(
                            id = "", // You may need to generate a unique ID for the movie
                            imageUrl = uri.toString(),
                            title = binding.editTextTitle.text.toString(),
                            producer = binding.editTextProducer.text.toString(),
                            rating = binding.editTextRating.text.toString(),
                            description = binding.editTextDescription.text.toString()
                        )
                        addMovieToFirestore(movie)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Image upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addMovieToFirestore(movie: Movie) {
        val db = FirebaseFirestore.getInstance()
        db.collection("movies")
            .add(movie)
            .addOnSuccessListener { documentReference ->
                // Use the generated document ID as the movie ID
                val movieWithId = movie.copy(id = documentReference.id)

                // Update the document with the movie ID
                documentReference.set(movieWithId)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Movie added successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error updating movie ID: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error adding movie to Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}