package k.lhl.test

import android.content.Context
import android.view.View
import android.view.ViewGroup
import k.lhl.recyclerview.BaseHeaderOrFooterView

/**
 * Created by luohaolun
 * Date：2020/8/16
 */
class MyFooterView(context: Context, parent: ViewGroup) : BaseHeaderOrFooterView(context, parent, FOOTER) {
    override fun onBindLayoutId() = R.layout.footer
}