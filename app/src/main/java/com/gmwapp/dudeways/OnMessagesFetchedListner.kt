package com.gmwapp.dudeways

import android.content.Context.INPUT_METHOD_SERVICE
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat.getSystemService
import com.gmwapp.dudeways.model.ChatList
import com.vanniktech.emoji.EmojiPopup

interface OnMessagesFetchedListner {

    /**
     * Called when messages are fetched from the API.
     * Used to fetch the initial conversation.
     */
    fun onMessagesFetched(conversations: MutableList<ChatList?>)

    /**
     * Called when a new message is added.
     * Used to update the conversation.
     */
    fun onMessageAdded(chatModel: ChatList?)

    /**
     * Called when a message is changed.
     * Used to update the conversation.
     */
    fun onMessageChanged(chatModel: ChatList?)

    /**
     * Called when a message is removed.
     * Used to update the conversation.
     */
    fun onMessageRemoved(chatModel: ChatList?)

    /**
     * Called when an error occurs.
     * Used to handle errors.
     */
    fun onError(errorMessage: String)




//    private fun emojiPopup(
//        messageEditText: EmojiEditText,
//        emojiButton: ImageButton,
//        rootView: View
//    ) {
//        val emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(messageEditText)
//
//        // Toggle emoji popup and change the icon dynamically
//        emojiButton.setOnClickListener {
//            if (emojiPopup.isShowing) {
//                // If emoji popup is open, close it and switch to keyboard icon
//                emojiPopup.dismiss()
//                emojiButton.setImageResource(R.drawable.emoji_icon) // Emoji icon
//                showKeyboard(messageEditText)
//            } else {
//                // If emoji popup is closed, show it and switch to keyboard icon
//                hideKeyboard(messageEditText)
//                emojiPopup.toggle()
//                emojiButton.setImageResource(R.drawable.keyboard_icon) // Keyboard icon
//            }
//        }
//
//        // Listener to detect when the emoji popup is dismissed
//        emojiPopup.setPopupDismissListener {
//            emojiButton.setImageResource(R.drawable.ic_emoji) // Switch back to emoji icon
//        }
//    }
//
//    private fun showKeyboard(view: View) {
//        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//        view.requestFocus()
//        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
//    }
//
//    private fun hideKeyboard(view: View) {
//        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.hideSoftInputFromWindow(view.windowToken, 0)
//    }


}