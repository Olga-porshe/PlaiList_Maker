package com.example.plailistmaker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import android.view.inputmethod.InputMethodManager

import android.view.inputmethod.EditorInfo

class SearchActivity : AppCompatActivity() {

    private lateinit var searchQuery: EditText
    private lateinit var clearButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholderNoResults: View
    private lateinit var placeholderError: View
    private lateinit var refreshButton: MaterialButton

    private lateinit var adapter: TrackAdapter
    private lateinit var networkClient: NetworkClient

    private var currentQuery = ""
    private var lastQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val toolbar = findViewById<MaterialToolbar>(R.id.topToolbar)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        searchQuery = findViewById(R.id.search_query)
        clearButton = findViewById(R.id.clear_button)
        recyclerView = findViewById(R.id.recyclerView)
        placeholderNoResults = findViewById(R.id.placeholderNoResults)
        placeholderError = findViewById(R.id.placeholderError)
        refreshButton = findViewById(R.id.refreshButton)

        networkClient = NetworkClient(this)

        adapter = TrackAdapter { track ->
            Toast.makeText(this, "Выбран трек: ${track.trackName}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        setupSearchField()

        if (savedInstanceState != null) {
            currentQuery = savedInstanceState.getString(SAVED_QUERY_KEY, "")
            searchQuery.setText(currentQuery)
            updateClearButtonVisibility(currentQuery)
            if (currentQuery.isNotBlank()) performSearch(currentQuery)
        }
    }

    companion object {
        private const val SAVED_QUERY_KEY = "saved_query"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVED_QUERY_KEY, currentQuery)
    }

    private fun setupSearchField() {
        clearButton.setOnClickListener {
            searchQuery.setText("")
            currentQuery = ""
            hideKeyboard()
            adapter.updateTracks(emptyList())
            placeholderNoResults.visibility = View.GONE
            placeholderError.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }

        searchQuery.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch(searchQuery.text.toString())
                true
            } else {
                false
            }
        }

        refreshButton.setOnClickListener {
            performSearch(lastQuery)
        }
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) return

        currentQuery = query
        lastQuery = query
        updateClearButtonVisibility(query)
        placeholderNoResults.visibility = View.GONE
        placeholderError.visibility = View.GONE

        networkClient.searchTracks(query, object : retrofit2.Callback<ItunesSearchResponse> {
            override fun onResponse(
                call: retrofit2.Call<ItunesSearchResponse>,
                response: retrofit2.Response<ItunesSearchResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val tracks = response.body()!!.results.map { dto ->
                        Track(
                            trackId = dto.trackId ?: 0L,
                            trackName = dto.trackName ?: "",
                            artistName = dto.artistName ?: "",
                            trackTime = formatTrackTime(dto.trackTimeMillis),
                            artworkUrl100 = dto.artworkUrl100
                        )
                    }
                    if (tracks.isEmpty()) {
                        showPlaceholderNoResults()
                    } else {
                        hideKeyboard()
                        placeholderError.visibility = View.GONE
                        placeholderNoResults.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        adapter.updateTracks(tracks)
                    }
                } else {
                    showPlaceholderError()
                }
            }

            override fun onFailure(call: retrofit2.Call<ItunesSearchResponse>, t: Throwable) {
                showPlaceholderError()
            }
        })
    }

    private fun formatTrackTime(millis: Long?): String {
        if (millis == null || millis <= 0) return ""
        // "mm:ss" считает минуты и секунды от нуля,
        // поэтому часовой пояс на результат не влияет.
        val sdf = SimpleDateFormat("mm:ss", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(millis)
    }





    private fun showPlaceholderNoResults() {
        recyclerView.visibility = View.GONE
        placeholderError.visibility = View.GONE
        placeholderNoResults.visibility = View.VISIBLE

        showKeyboard()
    }

    private fun showKeyboard() {
        searchQuery.requestFocus()
        searchQuery.postDelayed({
            val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
                ?: return@postDelayed

            val shown = imm.showSoftInput(searchQuery, 0)
            Log.d("SEARCH_DEBUG", "showSoftInput returned: $shown")
            if (!shown) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                Log.d("SEARCH_DEBUG", "fallback toggleSoftInput выполнен")
            }
        }, 300)
    }

    private fun showPlaceholderError() {
        recyclerView.visibility = View.GONE
        placeholderNoResults.visibility = View.GONE
        placeholderError.visibility = View.VISIBLE
    }

    private fun updateClearButtonVisibility(text: String) {
        clearButton.visibility = if (text.isBlank()) View.GONE else View.VISIBLE
    }

    private fun hideKeyboard() {
        val view = currentFocus
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        if (view != null && imm != null) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}


    data class Track(
        val trackId: Long,
        val trackName: String,
        val artistName: String,
        val trackTime: String,
        val artworkUrl100: String?
    )

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val trackImage: ImageView = itemView.findViewById(R.id.trackImage)
        private val trackName: TextView = itemView.findViewById(R.id.trackName)
        private val artistName: TextView = itemView.findViewById(R.id.artistName)
        private val trackTime: TextView = itemView.findViewById(R.id.trackTime)

        fun bind(track: Track) {
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackTime.text = track.trackTime

            if (track.artworkUrl100.isNullOrBlank()) {
                trackImage.setImageResource(R.drawable.placeholder)
            } else {
                Glide.with(itemView)
                    .load(track.artworkUrl100)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .centerCrop()
                    .into(trackImage)
            }
        }
    }

    class TrackAdapter(
        private val onItemClicked: (Track) -> Unit
    ) : RecyclerView.Adapter<TrackViewHolder>() {

        private val tracks = mutableListOf<Track>()

        fun updateTracks(newTracks: List<Track>) {
            tracks.clear()
            tracks.addAll(newTracks)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_track, parent, false)
            return TrackViewHolder(view)
        }

        override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
            holder.bind(tracks[position])
            holder.itemView.setOnClickListener { onItemClicked(tracks[position]) }
        }

        override fun getItemCount(): Int = tracks.size
    }

