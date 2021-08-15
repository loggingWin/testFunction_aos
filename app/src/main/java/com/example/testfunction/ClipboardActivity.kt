package com.example.testfunction

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.testfunction.databinding.ActivityClipboardBinding

class ClipboardActivity : AppCompatActivity() {
    private val binding by lazy { ActivityClipboardBinding.inflate(layoutInflater) }
    private val TAG = "@@TAG@@"
    private val clipboard by lazy { getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

    var pasteData: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btn.setOnClickListener {
            Log.d(TAG, "onCreate: 버튼 클릭함")
            paste()
        }
    }

    private fun paste() {
        // 클립보드에 데이터가 없거나 텍스트 타입이 아닌 경우
        if (!(clipboard.hasPrimaryClip())) {
            Log.d(TAG, "onCreate: 클립보드에 데이터 없음")
        } else {
            if (clipboard.primaryClip == null) {
                Log.d(TAG, "paste: primary clip이 null임")
            } else {
                clipboard.primaryClip?.let {
                    if (it.getItemAt(0) == null)
                        Log.d(TAG, "paste: primary clip의 0번째 데이터가 null임")
                }
            }
            clipboard.primaryClip?.let { pasteData = it.getItemAt(0).text.toString() }
        }

        pasteData.let { binding.editText1.setText(it) }
    }

//    // Declares a MIME type constant to match against the MIME types offered by the provider
//    const val MIME_TYPE_CONTACT = "vnd.android.cursor.item/vnd.example.contact"
//    fun pasteCodeFromGoogle() {
//// Gets a handle to the Clipboard Manager
//        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//
//// Gets a content resolver instance
//        val cr = contentResolver
//        // Gets the clipboard data from the clipboard
//        val clip: ClipData? = clipboard.primaryClip
//
//        clip?.run {
//
//            // Gets the first item from the clipboard data
//            val item: ClipData.Item = getItemAt(0)
//
//            // Tries to get the item's contents as a URI
//            val pasteUri: Uri? = item.uri
//
//            // If the clipboard contains a URI reference
//            pasteUri?.let {
//
//                // Is this a content URI?
//                val uriMimeType: String? = cr.getType(it)
//
//                // If the return value is not null, the Uri is a content Uri
//                uriMimeType?.takeIf {
//
//                    // Does the content provider offer a MIME type that the current application can use?
//                    it == MIME_TYPE_CONTACT
//                }?.apply {
//
//                    // Get the data from the content provider.
//                    cr.query(pasteUri, null, null, null, null)?.use { pasteCursor ->
//
//                        // If the Cursor contains data, move to the first record
//                        if (pasteCursor.moveToFirst()) {
//
//                            // get the data from the Cursor here. The code will vary according to the
//                            // format of the data model.
//                        }
//
//                        // Kotlin `use` will automatically close the Cursor
//                    }
//                }
//            }
//        }
//    }
}