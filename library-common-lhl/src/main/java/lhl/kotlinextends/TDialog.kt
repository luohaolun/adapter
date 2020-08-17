package lhl.kotlinextends

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import com.blankj.utilcode.util.ActivityUtils

typealias DialogListener = () -> Unit

const val CANCEL_ALL = 0
const val CANCEL_BACK = 1
const val CANCEL_NO = 2

var TDIALOG_TITLE = "提示"
var TDIALOG_POSITIVE = "确定"
var TDIALOG_NEGATIVE = "取消"

class TDialog(activity: Activity, message: String = "", private val cancel: Int = CANCEL_ALL, positive: DialogListener? = null) {

    constructor(message: String, cancel: Int = CANCEL_ALL, positive: DialogListener? = null) : this(ActivityUtils.getTopActivity(), message, cancel, positive)

    private var builder: AlertDialog.Builder = AlertDialog.Builder(activity)
    private var dialog: Dialog? = null

    init {
        builder.setTitle(TDIALOG_TITLE)
        builder.setMessage(message)
        builder.setPositiveButton(TDIALOG_POSITIVE, null)
        if (cancel == CANCEL_NO)
            builder.setCancelable(false)
        positive?.let { setPositiveButton(listener = it) }
    }

    fun show(): TDialog {
        dialog = builder.create()
        if (cancel != CANCEL_ALL)
            dialog?.setCanceledOnTouchOutside(false)
        dialog?.show()
        return this
    }

    fun isShowing() = dialog?.isShowing ?: false

    fun dismiss(): TDialog {
        dialog?.dismiss()
        return this
    }

    fun setPositiveButton(text: String = TDIALOG_POSITIVE, listener: DialogListener? = null): TDialog {
        builder.setPositiveButton(text) { _, _ -> listener?.invoke() }
        show()
        return this
    }

    fun setNegativeButton(text: String = TDIALOG_NEGATIVE, listener: DialogListener? = null): TDialog {
        builder.setNegativeButton(text) { _, _ -> listener?.invoke() }
        show()
        return this
    }

    fun setButtons(textPositive: String = TDIALOG_POSITIVE, textNegative: String = TDIALOG_NEGATIVE, negative: DialogListener? = null, positive: DialogListener? = null): TDialog {
        builder.setPositiveButton(textPositive) { _, _ -> positive?.invoke() }
        builder.setNegativeButton(textNegative) { _, _ -> negative?.invoke() }
        show()
        return this
    }

    fun setButtons(negative: DialogListener? = null, positive: DialogListener? = null): TDialog {
        builder.setPositiveButton(TDIALOG_POSITIVE) { _, _ -> positive?.invoke() }
        builder.setNegativeButton(TDIALOG_NEGATIVE) { _, _ -> negative?.invoke() }
        show()
        return this
    }

    fun setNeutralButton(text: String, listener: DialogListener? = null): TDialog {
        builder.setNeutralButton(text) { _, _ -> listener?.invoke() }
        return this
    }

    fun setTitle(text: String): TDialog {
        builder.setTitle(text)
        return this
    }

    fun setMessage(text: String): TDialog {
        builder.setMessage(text)
        return this
    }

    fun getDialog() = dialog
}