package com.example.mobileuas.adapter

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.mobileuas.EditMovie
import com.example.mobileuas.database.Movie
import com.example.mobileuas.databinding.ItemMovieBinding

class MovieAdapterAdmin(private var movies: List<Movie>,
                        private val onItemClick: (Movie) -> Unit,
                        private val onItemLongClick: (Movie) -> Unit) : RecyclerView.Adapter<MovieAdapterAdmin.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)

        // Set click listener
        holder.itemView.setOnClickListener {
            onItemClick(movie)
        }

        // Set long click listener
        holder.itemView.setOnLongClickListener {
            onItemLongClick(movie)
            true // consume the long click event
        }
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    fun updateMoviesList(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.apply {
                // Load image using Glide
                Glide.with(root.context)
                    .load(movie.imageUrl)
                    .into(imageMovieDisplay)
                MovieTitle.text = movie.title
                MovieRating.text = movie.rating
            }
        }
    }
}
