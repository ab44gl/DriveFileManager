package com.abhishek.drivefilemanager

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.children
import androidx.core.view.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Utils {
    companion object{
        const val  TAG="-----------------"
        fun logD(msg: Any,t:Throwable?=null) {
            Log.d(TAG,msg.toString(),t)
        }
        suspend fun showMessageToast(msg: String,context: Context) {
            withContext(Dispatchers.Main){
                Toast.makeText(context,msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

}

fun LinearLayout.setLayoutVisible(i: Int) {
    children.forEach {
        it.visibility= View.GONE
    }
    if(childCount!=0 && i<=childCount){
        get(i).visibility= View.VISIBLE
    }
}

