package lhl.kotlinextends

import com.blankj.utilcode.util.ConvertUtils
import java.text.DecimalFormat

private var defaultNumber = 2
fun setDefaultNumber(num: Int) {
    defaultNumber = num
}
/**
 * 保留defaultNumber位小数  省略小数点最后的零
 */
fun Double.kp(num: Int = defaultNumber): String {
    if (num <= 0)
        return DecimalFormat("#").format(this)
    return DecimalFormat("#.${Array(num) { it }.joinToString("") { "#" }}").format(this)
}

fun Float.kp(num: Int = defaultNumber): String {
    return this.toDouble().kp(num)
}

fun Double.kps(num: Int = defaultNumber): String {
    if (num < 0)
        return String.format("%.0f", this)
    return String.format("%.${num}f", this)
}

fun Float.kps(num: Int = defaultNumber): String {
    return this.toDouble().kps(num)
}

fun Int.dp(): Int = this.toFloat().dp()
fun Double.dp(): Int = this.toFloat().dp()
fun Float.dp(): Int = ConvertUtils.dp2px(this)
