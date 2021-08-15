package com.example.testfunction

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testfunction.databinding.ActivityChipBinding
import com.google.android.material.chip.Chip


class ChipActivity : AppCompatActivity() {
    private val binding by lazy { ActivityChipBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btn.setOnClickListener {
            val string = binding.edt.text
            if (string.isNullOrEmpty()) {
                showToast("chip 이름을 입력해주세요")
            } else {
                binding.chipGroup.addView(Chip(this).apply {
                    text = string
                    isCloseIconVisible = true
                    setOnCloseIconClickListener { binding.chipGroup.removeView(this) }
                })
            }
        }
        binding.btnCheck.setOnClickListener {
            val chipList = ArrayList<String>()
            for (i: Int in 1..binding.chipGroup.childCount) {
                val chip: Chip = binding.chipGroup.getChildAt(i - 1) as Chip
                chipList.add(chip.text.toString())
            }

            var output = "count: ${chipList.size}\n"
            for (i in chipList) {
                output += "$i / "
            }
            showToast(output)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }
}