package com.example.plailistmaker


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import androidx.core.view.updatePadding

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_settings)



        val toolbar = findViewById<MaterialToolbar>(R.id.nastroiki)
        setSupportActionBar(toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }


        toolbar.setNavigationOnClickListener {
            finish()
        }


        // Кнопка «Поделиться приложением»
        val btnShare = findViewById<ImageButton>(R.id.podelitsya)
        btnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_message))
            }

            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app_text)))
        }

        // Кнопка «Написать в поддержку»
        val btnSupport = findViewById<ImageButton>(R.id.poddergka)
        btnSupport.setOnClickListener {
            val email = getString(R.string.student_email)
            val subject = getString(R.string.email_subject)
            val body = getString(R.string.email_body)


            // кнопка - написать в поддержку




            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822" // Тип для email
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email)) // Получатель
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                // Добавляем createChooser, чтобы пользователь точно увидел список приложений
            }

            if (emailIntent.resolveActivity(packageManager) != null) {
                startActivity(Intent.createChooser(emailIntent, getString(R.string.share_app_text)))
            } else {
                // Если нет приложений для почты, можно показать Toast
                android.widget.Toast.makeText(this, "Нет приложений для отправки почты", android.widget.Toast.LENGTH_SHORT).show()
            }
        }


        // Кнопка «Пользовательское соглашение»


        val btnAgreement = findViewById<ImageButton>(R.id.soglashenie)



        btnAgreement.setOnClickListener {
            val urlString = getString(R.string.offer_url).trim() // Убираем случайные пробелы

            if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
                android.widget.Toast.makeText(this, "Некорректная ссылка в ресурсах", android.widget.Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))

            if (webIntent.resolveActivity(packageManager) != null) {
                startActivity(webIntent)
            } else {
                android.widget.Toast.makeText(this, "Не найдено приложение для открытия ссылок", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }
}





