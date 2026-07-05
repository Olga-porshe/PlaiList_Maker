package com.example.plailistmaker

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.enableEdgeToEdge
import com.google.android.material.appbar.MaterialToolbar
import androidx.appcompat.widget.SearchView
import android.graphics.Color
import android.graphics.PorterDuff




class SearchActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var searchView: SearchView

    // Переменная для хранения текущего запроса (для логики или передачи дальше)
    private var currentQuery = ""

    companion object {
        private const val SAVED_QUERY_KEY = "saved_query"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        // 1. Инициализация тулбара
        toolbar = findViewById(R.id.toolbar_search)
        setSupportActionBar(toolbar)

        // Настройка кнопки "Назад"
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 2. Инициализация SearchView
        searchView = findViewById(R.id.searchView)



        // 3. Настройка SearchView
        setupSearchView()

        // 4. Восстановление сохраненного запроса (если Activity пересоздалась)
        if (savedInstanceState != null) {
            val restoredQuery = savedInstanceState.getString(SAVED_QUERY_KEY)
            if (!restoredQuery.isNullOrBlank()) {
                searchView.setQuery(restoredQuery, false) // false = не запускать поиск сразу
                currentQuery = restoredQuery
            }
        }
    }

    private fun setupSearchView() {
        // Включаем кнопку отправки (лупа справа)
        searchView.isSubmitButtonEnabled = true

        // --- ГЛАВНАЯ ЛОГИКА ПОИСКА ---
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            // Вызывается, когда пользователь печатает текст
            override fun onQueryTextChange(newText: String?): Boolean {
                currentQuery = newText ?: ""
                // Здесь можно делать "живой" поиск (фильтрацию списка)
                // updateRecyclerView(currentQuery)
                return true
            }

            // Вызывается, когда пользователь нажал кнопку "Поиск" (лупа справа)
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentQuery = query ?: ""
                performSearch(currentQuery)
                hideKeyboard()
                return true
            }
        })

        // --- ЛОГИКА КНОПКИ ОЧИСТКИ (КРЕСТИК) ---
        // В SearchView крестик встроен. Нам нужно просто обработать его нажатие.
        searchView.setOnCloseListener {
            currentQuery = ""
            // Здесь логика, если нужно очистить список при нажатии крестика
            // clearResults()
            true // true = считаем, что обработали клик и закрыли поиск
        }

        // Опционально: можно принудительно показать крестик, если он скрыт
        // searchView.query = ""
    }

    private fun performSearch(query: String) {
        Toast.makeText(this, "Выполняем поиск по запросу: \$query", Toast.LENGTH_SHORT).show()
        // Сюда вставляй реальную логику поиска (запрос к базе данных или API)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(searchView.windowToken, 0)
    }

    // Сохраняем состояние при повороте экрана или уходе в фон
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVED_QUERY_KEY, currentQuery)
    }
}