package k.lhl.refresh

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import k.lhl.adapter.R
import k.lhl.adapter.RecyclerAdapter
import lhl.kotlinextends.click
import lhl.kotlinextends.longClick


class PullRecyclerView : RefreshLayout {
    //PullBaseView是继承LinearLayout，所以我们的头尾布局和RecycleView都是通过addView方法添加的
    lateinit var mRecyclerView: RecyclerView
        private set

    private var itemLayoutId = R.layout.item_default

    private var enableEmpty: Boolean = true

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }


    /**
     * init-初始化方法，为我们的RecyclerView做一些必要的初始化工作
     */
    private fun init(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PullRecyclerView)
        itemLayoutId = typedArray.getResourceId(R.styleable.PullRecyclerView_item, R.layout.item_default)
        val dividerDrawable = typedArray.getDrawable(R.styleable.PullRecyclerView_itemDivider)
        enableEmpty = typedArray.getBoolean(R.styleable.PullRecyclerView_enableEmpty, true)
        typedArray.recycle()
        mRecyclerView = createRecyclerView(context, attrs)
        //设置RecyclerView全屏显示
        mRecyclerView!!.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//        mRecyclerView!!.overScrollMode = View.OVER_SCROLL_NEVER
        if (dividerDrawable != null) {
            val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            divider.setDrawable(dividerDrawable)
            mRecyclerView.addItemDecoration(divider)
        }
        if (isInEditMode) {
            mRecyclerView!!.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val list = if (itemLayoutId == R.layout.item_default) arrayListOf(1) else arrayListOf(1, 2, 3)
            mRecyclerView.adapter = RecyclerAdapterInEditMode(context, list, itemLayoutId) {}
        }
    }

    override fun getContentView(): View {
        return mRecyclerView
    }


    private fun createRecyclerView(context: Context, attrs: AttributeSet): RecyclerView {
        /**
         * 这里返回一个RecyclerView，添加到LinearLayout中
         * 那么，如果你想使用ListView的话呢，那么你在这里返回就行了
         * 当然需要修改父类中的泛型相关的地方(包括列表所使用到的Adapter）
         */
        val recyclerView = RecyclerView(context, attrs)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        return recyclerView
    }

    fun <T> setAdapter(data: List<T>, bindView: View.(T) -> Unit): RecyclerAdapter<T> {
        showEmpty(enableEmpty && data.isEmpty())
        return RecyclerAdapter(data, itemLayoutId, bindView).apply { mRecyclerView.adapter = this }
    }

    fun <T> setAdapter(data: List<T>, layoutId: Int, bindView: View.(T) -> Unit): RecyclerAdapter<T> {
        showEmpty(enableEmpty && data.isEmpty())
        return RecyclerAdapter(data, layoutId, bindView).apply { mRecyclerView.adapter = this }
    }

    fun notifyDataSetChanged() {
        showEmpty(enableEmpty && mRecyclerView.adapter?.itemCount == 0)
        mRecyclerView.adapter?.notifyDataSetChanged()
    }

    fun enableEmpty(enable: Boolean) {
        this.enableEmpty = enable
    }

    /**
     * Created by luohaolun.
     * Date: 2019/7/2
     */

    private class RecyclerAdapterInEditMode<T>(private val context: Context, private val data: List<T>, private val layoutId: Int, private val bindView: View.(T) -> Unit) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private var clickListener: (View.(T) -> Unit)? = null
        private var longClickListener: (View.(T) -> Unit)? = null
        private var clickIntervalTime = 10

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder.itemView.setTag(R.id.adapterPosition, position)
            holder.itemView.run { bindView(data[position]) }
            clickListener?.let { listener -> holder.itemView.click(clickIntervalTime) { listener.invoke(it!!, data[position]) } }
            longClickListener?.let { listener -> holder.itemView.longClick { listener.invoke(it!!, data[position]);true } }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(context).inflate(layoutId, parent, false))

        override fun getItemCount(): Int = data.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    }


}