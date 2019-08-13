package k.lhl.adapter

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.blankj.utilcode.util.ActivityUtils

/**
 * Created by luohaolun.
 * Date: 2019/1/22
 */
class MultiAdapter<T>(private val data: List<Pair<Int, T>>, private val layoutIds: SparseArray<Int>, private val bindView: View.(T) -> Unit) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val layoutId = layoutIds[getItemViewType(position)] ?: error("缺少类型布局 type = ${getItemViewType(position)} , position = $position")
        val view = convertView ?: LayoutInflater.from(ActivityUtils.getTopActivity()).inflate(layoutId, parent, false)
        view.setTag(R.id.adapterPosition, position)
        view.setTag(R.id.adapterType, getItemViewType(position))
        return view.apply { bindView(getItem(position)) }
    }

    override fun getItemViewType(position: Int): Int = data[position].first

    override fun getViewTypeCount(): Int = layoutIds.size()

    override fun getItem(position: Int): T = data[position].second

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount(): Int = data.size

}