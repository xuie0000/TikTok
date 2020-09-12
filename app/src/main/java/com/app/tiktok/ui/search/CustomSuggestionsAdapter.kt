package com.app.tiktok.ui.search

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cursoradapter.widget.CursorAdapter
import com.app.tiktok.R

/**
 * @author Jie Xu
 * @date 2019/10/30
 */
class CustomSuggestionsAdapter(context: Context) : CursorAdapter(context, null, 0) {
  private val inflater by lazy {
    LayoutInflater.from(context)
  }

  private var listener: OnClickListener? = null

  override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View =
    inflater.inflate(R.layout.search_drop, parent, false)

  override fun bindView(view: View, context: Context, cursor: Cursor) {
    val key = cursor.getString(1)
    val order = cursor.getInt(2)
    val tvKey: TextView = view.findViewById(R.id.tv_key)

    tvKey.text = key
//    tvKey.setOnClickListener {
//      listener?.click(it, cursor.position, key)
//    }

  }

  fun setOnClickListener(listener: OnClickListener) {
    this.listener = listener
  }

  interface OnClickListener {
    fun click(view: View, position: Int, key: String): Boolean
  }

}
