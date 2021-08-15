package com.example.testfunction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testfunction.databinding.ActivityMainBinding
import com.google.android.material.chip.Chip

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val chips = ArrayList<Chip>()
        chips.add(Chip(this).apply {
            text = "chip crud"
            setOnClickListener { startActivity(Intent(this@MainActivity, ChipActivity::class.java)) }
        })
        chips.add(Chip(this).apply {
            text = "clipboard paste"
            setOnClickListener { startActivity(Intent(this@MainActivity, ClipboardActivity::class.java)) }
        })
        chips.add(Chip(this).apply {
            text = "auto complete"
            setOnClickListener { startActivity(Intent(this@MainActivity, AutoCompleteActivity::class.java)) }
        })
        chips.add(Chip(this).apply {
            text = "OCR tess-two"
            setOnClickListener { startActivity(Intent(this@MainActivity, OcrActivity::class.java)) }
        })

        for (i in chips) {
            binding.chipGroup.addView(i)
        }
    }
}