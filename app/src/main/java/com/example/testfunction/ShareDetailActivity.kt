package com.example.testfunction

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testfunction.databinding.ActivityShareDetailBinding

class ShareDetailActivity : AppCompatActivity() {
    private val binding by lazy { ActivityShareDetailBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val action = intent.action
        val type = intent.type
        var sharedText: String? = null
        var imageUri: Uri? = null

        if ((Intent.ACTION_SEND == action) && (type != null)) {
            if ("text/plain" == type) {
                sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            } else if (type.startsWith("image/")) {
                imageUri = Uri.parse(intent.getParcelableExtra(Intent.EXTRA_STREAM))
            }
        }

        var str = ""
        sharedText.let { str += "sharedText: $it\n" }
        imageUri.let { str += "imageUri: $it\n" }

        binding.tv.text = str
    }
}