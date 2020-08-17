package lhl.kotlinextends

import android.view.View


private var lastClickTime: Long = 0
private const val CLICK_INTERVAL_TIME = 500

fun isFastDoubleClick(interval: Int = CLICK_INTERVAL_TIME): Boolean {
    val time = System.currentTimeMillis()
    if (time - lastClickTime < interval) {
        return true
    }
    lastClickTime = time
    return false
}

open class SingleClick(private val interval: Int = CLICK_INTERVAL_TIME, private val listener: (View?) -> Unit) : View.OnClickListener {
    override fun onClick(v: View?) {
        if (!isFastDoubleClick(interval))
            listener.invoke(v)
    }
}

fun View.click(interval: Int = CLICK_INTERVAL_TIME, listener: (View?) -> Unit) {
    setOnClickListener(SingleClick(interval, listener))
}

fun View.longClick(listener: (View?) -> Boolean) {
    isLongClickable = true
    setOnLongClickListener(listener)
}

var View.visible: Boolean
    get() = this.visibility == View.VISIBLE
    set(value) {
        this.visibility = if (value) View.VISIBLE else View.GONE
    }

var View.invisible: Boolean
    get() = this.visibility == View.INVISIBLE
    set(value) {
        this.visibility = if (value) View.INVISIBLE else View.VISIBLE
    }