package k.lhl.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import java.text.SimpleDateFormat
import java.util.*

/**
 * 所有HeaderView或者是FooterView的基类
 * 因为需要强制我们的子类实现某些方法，所以这里用的是抽象类
 */
open abstract class BaseHeaderOrFooterView @JvmOverloads constructor(protected val context: Context, root: ViewGroup, private val type: Int, attachToRoot: Boolean = false) {
    var view: View? = null
    var viewHeight = 0
    private var params: LinearLayout.LayoutParams? = null
    private fun createView(context: Context, root: ViewGroup, attachToRoot: Boolean) {
        view = LayoutInflater.from(context).inflate(onBindLayoutId(), root, attachToRoot)
        measureView(view)
        viewHeight = view!!.measuredHeight
        params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, viewHeight)
        // 设置topMargin的值为负的header View高度,即将其隐藏在最上方
        if (type == HEADER) params!!.topMargin = -viewHeight
        onViewCreated(view)
    }

    fun getParams(): LinearLayout.LayoutParams {
        return params!!
    }

    fun getFormatDateString(format: String?): String {
        val sdf = SimpleDateFormat(format)
        return sdf.format(Date())
    }

    protected abstract fun onViewCreated(view: View?)
    protected abstract fun onBindLayoutId(): Int

    /**
     * 当header view的topMargin>=0时，说明已经完全显示出来了,修改header view 的提示状态
     * 释放刷新/加载更多
     */
    open fun onPreRelease() {}

    /**
     * 下拉刷新/上拉加载
     */
    open fun onPreLoading() {}

    /**
     * 执行正在刷新/加载的操作
     *
     * @return
     */
    open fun onLoading() {}

    /**
     * 刷新/加载完成的操作
     * over: 没有更多的内容
     * @return
     */
    open fun onComplete(over: Boolean = false) {}

    /**
     * 这里提供一个方法能够获取下拉头显示的百分比，供动画效果使用
     */
    abstract fun onPercentage(rate: Float)

    private fun measureView(child: View?) {
        var p = child!!.layoutParams
        if (p == null) {
            p = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        val childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width)
        val lpHeight = p.height
        val childHeightSpec: Int
        childHeightSpec = if (lpHeight > 0) {
            View.MeasureSpec.makeMeasureSpec(lpHeight, View.MeasureSpec.EXACTLY)
        } else {
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        }
        child.measure(childWidthSpec, childHeightSpec)
    }

    companion object {
        const val HEADER = 0
        const val FOOTER = 1
    }

    init {
        createView(context, root, attachToRoot)
    }
}