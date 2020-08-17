package lhl.kotlinextends

import com.blankj.utilcode.util.ToastUtils

fun String.toast() {
    ToastUtils.showShort(this)
}

fun String.toastLong() {
    ToastUtils.showLong(this)
}

fun toast(msg: Any) {
    ToastUtils.showShort(msg.toString())
}

fun toastLong(msg: Any) {
    ToastUtils.showLong(msg.toString())
}