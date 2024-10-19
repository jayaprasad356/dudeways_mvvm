package com.gmwapp.dudeways

import com.gmwapp.dudeways.model.ChatList
import com.gmwapp.dudeways.model.ChatModel

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
}