package com.gmwapp.dudeways.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.gmwapp.dudeways.activity.LoginActivity

class Session(activity: Context?) {
    val PRIVATE_MODE: Int = 0
    var pref: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    var _activity: Context? = null


    private var sharedPreferences: SharedPreferences? = null


    init {
        try {
            this._activity = activity
            pref = _activity!!.getSharedPreferences(PREFER_NAME, PRIVATE_MODE)
            sharedPreferences =
                _activity!!.getSharedPreferences("user_session", Context.MODE_PRIVATE)
            editor = pref?.edit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun setData(id: String?, `val`: String?) {
        editor!!.putString(id, `val`)
        editor!!.commit()
    }

    fun setBoolean(id: String?, `val`: Boolean) {
        editor!!.putBoolean(id, `val`)
        editor!!.commit()
    }

    fun getData(id: String?): String? {
        return pref!!.getString(id, "")
    }


    fun logoutUser(activity: Activity) {
        val i = Intent(activity, LoginActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(i)
        activity.finish()
        editor!!.clear()
        editor!!.commit()
        clearData()
        Session(_activity).setBoolean("is_logged_in", false)
    }

    fun getBoolean(id: String?): Boolean {
        return pref!!.getBoolean(id, false)
    }


    fun clearData() {
        val editor = sharedPreferences!!.edit()
        editor.clear()
        editor.apply()
    }


    companion object {
        const val PREFER_NAME: String = "smartgram"
    }
}