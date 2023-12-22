package com.example.mobileuas

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mobileuas.adapter.MovieAdapterAdmin
import com.example.mobileuas.adapter.MovieAdapterPublic
import com.example.mobileuas.database.Movie
import com.example.mobileuas.databinding.FragmentUserHomeBinding
import com.example.mobileuas.databinding.FragmentUserSettingsBinding
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserHomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserHomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentUserHomeBinding
    private lateinit var movieAdapterUser: MovieAdapterPublic // Reusing the same adapter as MainAdmin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserHomeBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        // Initialize RecyclerView
        binding.MyRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)

        movieAdapterUser = MovieAdapterPublic(emptyList()) { movie ->
            // Handle item click
            val intent = Intent(requireContext(), MovieDetails::class.java)
            intent.putExtra("movie", movie)
            startActivity(intent)
        }

        binding.MyRecyclerView.adapter = movieAdapterUser

        // Retrieve movies from Firestore
        retrieveMoviesFromFirestore()

        return view
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
                    movieAdapterUser.updateMoviesList(moviesList)
                }
            }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserHomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserHomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}