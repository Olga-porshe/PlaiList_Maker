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

        // Если есть сохраненное состояние (например, поворот экрана), восстанавливаем его сразу
        if (savedInstanceState != null) {
            restoreState(savedInstanceState)
        } else {
            // Инициализация для первого запуска
            currentQuery = searchQuery.text.toString()
            updateClearButtonVisibility(currentQuery)
        }

        setupSearchField()
    }

    // Метод для сохранения состояния
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVED_QUERY_KEY, currentQuery)
    }

    // Метод для восстановления состояния
    private fun restoreState(savedInstanceState: Bundle) {
        val restoredQuery = savedInstanceState.getString(SAVED_QUERY_KEY, "")
        currentQuery = restoredQuery
        searchQuery.setText(restoredQuery)
        updateClearButtonVisibility(restoredQuery)
    }

    private fun setupSearchField() {
        // Кнопка очистки
        clearButton.setOnClickListener {
            searchQuery.text.clear()
            currentQuery = ""
            hideKeyboard() // Требование куратора: скрывать клавиатуру при очистке
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
    }

    private fun updateClearButtonVisibility(text: String) {
        clearButton.visibility = if (text.isBlank()) View.GONE else View.VISIBLE
    }

    private fun performSearch(query: String) {
        Toast.makeText(this, "Выполняем поиск по запросу: \$query", Toast.LENGTH_SHORT).show()
    }

    private fun hideKeyboard() {
        val view = currentFocus
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        if (view != null && imm != null) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}