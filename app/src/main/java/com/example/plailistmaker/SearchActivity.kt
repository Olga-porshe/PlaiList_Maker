package com.example.plailistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.enableEdgeToEdge
import com.google.android.material.appbar.MaterialToolbar
import androidx.core.view.updatePadding

class SearchActivity : AppCompatActivity() {

    private lateinit var searchQuery: EditText
    private lateinit var clearButton: ImageButton
    private var currentQuery = ""

    //вставляю компаньон(последняя задача)

    companion object {

        private const val SAVED_QUERY_KEY = "saved_query"
    }
    // здесь конец


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_search)



        /// КНОПКА НАЗАД и отступы

        val toolbar = findViewById<MaterialToolbar>(R.id.topToolbar)
        setSupportActionBar(toolbar)

        /*
        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }

        */

        setSupportActionBar(toolbar)


        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        //КОНЕЦ КОДА


        // Инициализация элементов
        searchQuery = findViewById(R.id.search_query)
        clearButton = findViewById(R.id.clear_button)


        setupSearchField()


        setupClearButton()

        //  Заглушка для будущих задач
        setupSearchLogicStub()
    }


    private fun setupSearchField() {
        searchQuery.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentQuery = s?.toString() ?: ""
                updateClearButtonVisibility()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }


    private fun updateClearButtonVisibility() {
        clearButton.visibility = if (currentQuery.isNotEmpty()) View.VISIBLE else View.GONE
    }


    private fun setupClearButton() {
        clearButton.setOnClickListener {
            searchQuery.setText("")
            currentQuery = ""


            searchQuery.clearFocus()


            hideKeyboard()


        }
    }

    //заглушка
    private fun setupSearchLogicStub() {
        searchQuery.setOnEditorActionListener { v, actionId, event ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {

                Toast.makeText(this, "Поиск по запросу: \$currentQuery", Toast.LENGTH_SHORT).show()
                hideKeyboard()
                true
            } else {
                false
            }
        }
    }


    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(searchQuery.windowToken, 0)
    }
}