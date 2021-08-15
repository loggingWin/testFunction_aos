package com.example.testfunction


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.example.testfunction.databinding.ActivityAutoCompleteBinding


class AutoCompleteActivity : AppCompatActivity() {
    val wordList = mutableListOf<String>()
    val binding by lazy { ActivityAutoCompleteBinding.inflate(layoutInflater) }
//    val searchBinding by lazy { binding.searchBar }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.searchBar.autoTv.threshold = 1

        binding.enrollBtn.setOnClickListener {
            wordList.add(binding.enrollInput.text.toString())
            val adapter =
                ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, wordList)
            binding.searchBar.autoTv.setAdapter(adapter)

            binding.enrollInput.setText("")
        }

        // search_image를 클릭했을 때
        binding.searchBar.searchImage.setOnClickListener {
            binding.result.text =
                "선택된 단어는 ${binding.searchBar.autoTv.text}이며,\n${binding.searchBar.autoTv.text.length}글자로 이루어져 있습니다."
            binding.searchBar.autoTv.setText("")
        }

        // AutoCompleteTextView에 나열된 항목을 클릭했을 경우 바로 적용 되도록
        binding.searchBar.autoTv.setOnItemClickListener { parent, view, position, id ->
            binding.result.text =
                "선택된 단어는 ${binding.searchBar.autoTv.text}이며,\n${binding.searchBar.autoTv.text.length}글자로 이루어져 있습니다."
            binding.searchBar.autoTv.setText("")
        }
    }
}