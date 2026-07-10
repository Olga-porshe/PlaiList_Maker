package com.example.plailistmaker

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.enableEdgeToEdge
import com.google.android.material.appbar.MaterialToolbar
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView

class SearchActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var searchQuery: EditText      // Было searchView
    private lateinit var clearButton: ImageButton   // Новая переменная
    private lateinit var searchIcon: ImageView      // Новая переменная

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
        searchIcon = findViewById(R.id.search_icon)

        setupSearchField()

        if (savedInstanceState != null) {
            val restoredQuery = savedInstanceState.getString(SAVED_QUERY_KEY)
            if (!restoredQuery.isNullOrBlank()) {
                searchQuery.setText(restoredQuery)
                currentQuery = restoredQuery
                clearButton.visibility = View.VISIBLE
            }
        }
    }

    private fun setupSearchField() {
        // Кнопка очистки
        clearButton.setOnClickListener {
            searchQuery.text.clear()
            currentQuery = ""
            clearButton.visibility = View.GONE
        }

        // Ввод текста
        searchQuery.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentQuery = s.toString()
                // Показываем крестик, если что-то ввели
                clearButton.visibility = if (s.isNullOrBlank()) View.GONE else View.VISIBLE
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
    }

    private fun performSearch(query: String) {
        Toast.makeText(this, "Выполняем поиск по запросу: \$query", Toast.LENGTH_SHORT).show()

    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(searchQuery.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVED_QUERY_KEY, currentQuery)
    }
}