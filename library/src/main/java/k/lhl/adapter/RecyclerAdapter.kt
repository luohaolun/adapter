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

class RecyclerAdapter<T>(private val data: List<T>, private val layoutId: Int, private val bindView: View.(T) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var clickListener: (View.(T) -> Unit)? = null
    private var longClickListener: (View.(T) -> Unit)? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.setTag(R.id.adapterPosition, position)
        holder.itemView.run { bindView(data[position]) }
        clickListener?.let { listener -> holder.itemView.click { listener.invoke(it!!, data[position]) } }
        longClickListener?.let { listener -> holder.itemView.longClick { listener.invoke(it!!, data[position]);true } }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(Utils.getApp()).inflate(layoutId, parent, false))

    override fun getItemCount(): Int = data.size

    fun setOnItemClickListener(listener: View.(T) -> Unit): RecyclerAdapter<T> {
        this.clickListener = listener
        return this
    }

    fun setOnItemLongClickListener(listener: View.(T) -> Unit): RecyclerAdapter<T> {
        this.longClickListener = listener
        return this
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

}


