package k.lhl.adapter

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.blankj.utilcode.util.Utils

/**
 * Author： luohaolun
 * Date： 2019/7/3
 */
class MulAdapter<T>(private val data: List<Pair<Int, T>>, type: Int, layoutId: Int, bindView: View.(T) -> Unit) : BaseAdapter() {

    private val itemTypeList = SparseArray<Pair<Int, View.(T) -> Unit>>()

    init {
        itemTypeList.put(type, Pair(layoutId, bindView))
    }

    fun addItemType(type: Int, layoutId: Int, bindView: View.(T) -> Unit): MulAdapter<T> {
        itemTypeList.put(type, Pair(layoutId, bindView))
        return this
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val pair = itemTypeList[getItemViewType(position)] ?: error("缺少类型布局 type = ${getItemViewType(position)} , position = $position")
        val view = convertView ?: LayoutInflater.from(Utils.getApp()).inflate(pair.first, parent, false)
        view.setTag(R.id.adapterPosition, position)
        view.setTag(R.id.adapterType, getItemViewType(position))
        pair.second(view, getItem(position))
        return view
    }

    override fun getItemViewType(position: Int): Int = data[position].first

    override fun getViewTypeCount(): Int = itemTypeList.size()

    override fun getItem(position: Int): T = data[position].second

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount(): Int = data.size


}