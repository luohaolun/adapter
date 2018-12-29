package k.lhl.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

class Adapter<T>(private val context: Context?, private val data: List<T>, private val layoutId: Int, private val bindView: (View, T, Int) -> Unit) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView ?: LayoutInflater.from(context).inflate(layoutId, parent, false)
        bindView(view, data[position]!!, position)
        return view
    }

    override fun getItem(position: Int): T {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return data.size
    }
}