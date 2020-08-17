package k.lhl.test

import android.content.Context
import android.view.View
import android.view.ViewGroup
import k.lhl.refresh.BaseHeaderOrFooterView

/**
 * Author:  LuoHaoLun
 * Email :  506503279@qq.com
 * Date  :  2020/8/14
 */
class MyHeaderView(context: Context, parent: ViewGroup) : BaseHeaderOrFooterView(context, parent, HEADER) {
    override fun onLoading() {
    }

    override fun onPreRelease() {
    }

    override fun onViewCreated(view: View?) {
    }

    override fun onBindLayoutId(): Int {
        return R.layout.header
    }

    override fun onComplete(over: Boolean) {
    }

    override fun onPercentage(rate: Float) {
    }

    override fun onPrePull() {
    }
}