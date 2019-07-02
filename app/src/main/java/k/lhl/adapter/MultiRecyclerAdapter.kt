package k.lhl.adapter

import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.Utils
import lhl.kotlinextends.click
import lhl.kotlinextends.longClick

/**
 * Created by luohaolun.
 * Date: 2019/7/2
 */


class MultiRecyclerAdapter<T>(private val data: List<Pair<Int, T>>, private val layoutId: SparseArray<Int>, private val bindView: View.(T, Int) -> Unit) : RecyclerView.Adapter<MultiRecyclerAdapter<T>.MyHolder>() {

    private var clickListener: RecyclerClickListener<T>? = null

    private var longClickListener: RecyclerClickListener<T>? = null

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.itemView.run { bindView(data[position].second, position) }
        clickListener?.let { listener -> holder.itemView.click { bindAdapterHolder(holder, position);listener.invoke(holder.adapterHolder) } }
        longClickListener?.let { listener -> holder.itemView.longClick { bindAdapterHolder(holder, position);listener.invoke(holder.adapterHolder);true } }
    }

    private fun bindAdapterHolder(holder: MyHolder, position: Int) {
        holder.adapterHolder.item = data[position].second
        holder.adapterHolder.position = position
        holder.adapterHolder.type = getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LayoutInflater.from(Utils.getApp()).inflate(layoutId[viewType], parent, false).run { MyHolder(this, AdapterHolder(this, data.first().second)) }

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int = data[position].first

    fun setOnItemClickListener(listener: RecyclerClickListener<T>): MultiRecyclerAdapter<T> {
        this.clickListener = listener
        return this
    }

    fun setOnItemLongClickListener(listener: RecyclerClickListener<T>): MultiRecyclerAdapter<T> {
        this.longClickListener = listener
        return this
    }

    inner class MyHolder(view: View, var adapterHolder: AdapterHolder<T>) : RecyclerView.ViewHolder(view)

}


