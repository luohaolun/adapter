package k.lhl.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.blankj.utilcode.util.Utils

class Adapter<T>(private val data: List<T>, private val layoutId: Int, private val bindView: View.(T, Int) -> Unit) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(Utils.getApp()).inflate(layoutId, parent, false)
        return view.apply { bindView(data[position], position) }
    }

    override fun getItem(position: Int): T = data[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = data.size
}