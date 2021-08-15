package com.example.testfunction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testfunction.databinding.ActivityShareSimpleBinding

class ShareSimpleActivity : AppCompatActivity() {
    private val binding by lazy { ActivityShareSimpleBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}