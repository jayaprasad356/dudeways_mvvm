package com.gmwapp.dudeways.model

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.Toast
import com.gmwapp.dudeways.R
import com.vanniktech.emoji.EmojiEditText
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.google.GoogleEmojiProvider


class EmojiPopupHelper(private val context: Context) {

    fun setupEmojiPopup(
        messageEditText: EmojiEditText,
        emojiButton: ImageButton,
        rootView: View


    ) {

        EmojiManager.install(GoogleEmojiProvider())

        // Initialize the EmojiPopup
        val emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(messageEditText)

        // Toggle emoji popup and change the icon dynamically
        emojiButton.setOnClickListener {
            if (emojiPopup.isShowing) {
                emojiPopup.dismiss()
           //     emojiButton.setImageResource(R.drawable.emoji_icon) // Set to emoji icon
                emojiButton.setBackgroundResource(R.drawable.emoji_icon) // Change background for emoji icon
                showKeyboard(messageEditText)
            } else {
                hideKeyboard(messageEditText)
                emojiPopup.show()
//emojiButton.setImageResource(R.drawable.keyboard_icon) // Set to keyboard icon
                emojiButton.setBackgroundResource(R.drawable.keyboard_icon) // Change background for keyboard icon
            }
        }

        messageEditText.setOnClickListener{
            if (emojiPopup.isShowing) {
                emojiPopup.dismiss()
                //     emojiButton.setImageResource(R.drawable.emoji_icon) // Set to emoji icon
                emojiButton.setBackgroundResource(R.drawable.emoji_icon) // Change background for emoji icon
                showKeyboard(messageEditText)
            } else {
//                hideKeyboard(messageEditText)
//                emojiPopup.show()
////emojiButton.setImageResource(R.drawable.keyboard_icon) // Set to keyboard icon
//                emojiButton.setBackgroundResource(R.drawable.keyboard_icon) // Change background for keyboard icon
            }
        }


        // Optionally, manage the icon change on dismissal directly in the button click
//        emojiPopup.setOnEmojiPopupDismissedListener {
//            emojiButton.setImageResource(R.drawable.emoji_icon) // Revert to emoji icon when dismissed
//        }
    }

    private fun showKeyboard(view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view.requestFocus()
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard(view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}