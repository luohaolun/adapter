package k.lhl.adapter

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

class MultiAdapter<T>(private val context: Context?, private val data: List<Pair<Int, T>>, private val layoutId: SparseArray<Int>, private val bindView: (View, T, Int, Int) -> Unit) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(layoutId[data[position].first], parent, false)
        bindView(view, data[position].second, data[position].first, position)
        return view
    }

    override fun getItemViewType(position: Int): Int = data[position].first

    override fun getViewTypeCount(): Int = layoutId.size()

    override fun getItem(position: Int): T = data[position].second

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount(): Int = data.size
}