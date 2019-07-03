package k.lhl.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.blankj.utilcode.util.Utils

/**
 * Created by luohaolun.
 * Date: 2019/1/22
 */
class Adapter<T>(private val data: List<T>, private val layoutId: Int, private val bindView: View.(T) -> Unit) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(Utils.getApp()).inflate(layoutId, parent, false)
        view.setTag(R.id.adapterPosition, position)
        return view.apply { bindView(data[position]) }
    }

    override fun getItem(position: Int): T = data[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = data.size
}




