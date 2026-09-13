package com.example.plailistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.nastroiki)

        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }



        val tvShare = findViewById<MaterialTextView>(R.id.podelitsya)
        tvShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_message))
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app_text)))
        }

        val tvSupport = findViewById<MaterialTextView>(R.id.poddergka)
        tvSupport.setOnClickListener {
            val email = getString(R.string.student_email)
            val subject = getString(R.string.email_subject)
            val body = getString(R.string.email_body)

            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }

            if (emailIntent.resolveActivity(packageManager) != null) {
                startActivity(Intent.createChooser(emailIntent, getString(R.string.share_app_text)))
            } else {
                Toast.makeText(this, "Нет приложений для отправки почты", Toast.LENGTH_SHORT).show()
            }
        }

        val tvAgreement = findViewById<MaterialTextView>(R.id.soglashenie)
        tvAgreement.setOnClickListener {
            val urlString = getString(R.string.offer_url).trim()

            if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
                Toast.makeText(this, "Некорректная ссылка в ресурсах", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))

            if (webIntent.resolveActivity(packageManager) != null) {
                startActivity(webIntent)
            } else {
                Toast.makeText(this, "Не найдено приложение для открытия ссылок", Toast.LENGTH_SHORT).show()
            }
        }
    }
}






