package k.lhl.adapter

import android.support.v7.widget.RecyclerView
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

typealias RecyclerClickListener<T> = AdapterHolder<T>.() -> Unit

class RecyclerAdapter<T>(private val data: List<T>, private val layoutId: Int, private val bindView: View.(T, Int) -> Unit) : RecyclerView.Adapter<RecyclerAdapter<T>.MyHolder>() {

    private var clickListener: RecyclerClickListener<T>? = null
    private var longClickListener: RecyclerClickListener<T>? = null

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.itemView.run { bindView(data[position], position) }
        clickListener?.let { listener -> holder.itemView.click { bindAdapterHolder(holder, position);listener.invoke(holder.adapterHolder) } }
        longClickListener?.let { listener -> holder.itemView.longClick { bindAdapterHolder(holder, position);listener.invoke(holder.adapterHolder);true } }
    }

    private fun bindAdapterHolder(holder: MyHolder, position: Int) {
        holder.adapterHolder.item = data[position]
        holder.adapterHolder.position = position
        holder.adapterHolder.type = getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LayoutInflater.from(Utils.getApp()).inflate(layoutId, parent, false).run { MyHolder(this, AdapterHolder(this,data.first())) }

    override fun getItemCount(): Int = data.size

    fun setOnItemClickListener(listener: RecyclerClickListener<T>): RecyclerAdapter<T> {
        this.clickListener = listener
        return this
    }

    fun setOnItemLongClickListener(listener: RecyclerClickListener<T>): RecyclerAdapter<T> {
        this.longClickListener = listener
        return this
    }

    inner class MyHolder(view: View, var adapterHolder: AdapterHolder<T>) : RecyclerView.ViewHolder(view)

}


