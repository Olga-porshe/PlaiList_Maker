package com.example.plailistmaker

import android.os.Bundle
import android.view.inputmethod.InputMethodManager


import android.view.View
import android.view.inputmethod.EditorInfo

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import com.google.android.material.appbar.MaterialToolbar
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView


import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class SearchActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var searchQuery: EditText
    private lateinit var clearButton: ImageButton

    private var currentQuery = ""

    companion object {
        private const val SAVED_QUERY_KEY = "saved_query"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        toolbar = findViewById(R.id.topToolbar)
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        searchQuery = findViewById(R.id.search_query)
        clearButton = findViewById(R.id.clear_button)


        if (savedInstanceState != null) {
            restoreState(savedInstanceState)
        } else {
            // Инициализация для первого запуска
            currentQuery = searchQuery.text.toString()
            updateClearButtonVisibility(currentQuery)
        }

        setupSearchField()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVED_QUERY_KEY, currentQuery)
    }


    private fun restoreState(savedInstanceState: Bundle) {
        val restoredQuery = savedInstanceState.getString(SAVED_QUERY_KEY, "")
        currentQuery = restoredQuery
        searchQuery.setText(restoredQuery)
        updateClearButtonVisibility(restoredQuery)
    }

    private fun setupSearchField() {

        clearButton.setOnClickListener {
            searchQuery.text.clear()
            currentQuery = ""
            hideKeyboard()
            updateClearButtonVisibility("")
        }

        searchQuery.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentQuery = s.toString()
                updateClearButtonVisibility(currentQuery)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        searchQuery.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currentQuery = searchQuery.text.toString()
                performSearch(currentQuery)
                hideKeyboard()
                true
            } else {
                false
            }
        }


        val recyclerView =
            findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = TrackAdapter(tracksList) { track ->

            Toast.makeText(this, "Выбран трек: ${track.trackName}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateClearButtonVisibility(text: String) {
        clearButton.visibility = if (text.isBlank()) View.GONE else View.VISIBLE
    }

    private fun performSearch(query: String) {
        Toast.makeText(this, "Выполняем поиск по запросу: $query", Toast.LENGTH_SHORT).show()
    }

    private fun hideKeyboard() {
        val view = currentFocus
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        if (view != null && imm != null) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    data class Track(
        val trackName: String,
        val artistName: String,
        val trackTime: String,
        val artworkUrl100: String
    )

    val tracksList = arrayListOf(
        Track(
            trackName = "Smells Like Teen Spirit",
            artistName = "Nirvana",
            trackTime = "5:01",
            artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
        ),
        Track(
            trackName = "Billie Jean",
            artistName = "Michael Jackson",
            trackTime = "4:35",
            artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
        ),
        Track(
            trackName = "Stayin' Alive",
            artistName = "Bee Gees",
            trackTime = "4:10",
            artworkUrl100 = "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
        ),
        Track(
            trackName = "Whole Lotta Love",
            artistName = "Led Zeppelin",
            trackTime = "5:33",
            artworkUrl100 = "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
        ),
        Track(
            trackName = "Sweet Child O'Mine",
            artistName = "Guns N' Roses",
            trackTime = "5:03",
            artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
        )
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

            Glide.with(itemView)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .centerCrop()
                .into(trackImage)
        }
    }

    class TrackAdapter(
        private val tracks: List<Track>,
        private val onItemClicked: (Track) -> Unit
    ) : RecyclerView.Adapter<TrackViewHolder>() {

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


}