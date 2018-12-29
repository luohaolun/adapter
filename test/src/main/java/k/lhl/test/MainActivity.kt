package k.lhl.test

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import k.lhl.adapter.Adapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_test.view.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val data = mutableListOf<String>()
        for (i in 0..10) {
            data.add(i.toString())
        }

        lvList.adapter = Adapter(this, data, R.layout.item_test) { v, item, position ->
            v.tvNum.text = item
            v.btn.setOnClickListener { Toast.makeText(this, "点击" + position, Toast.LENGTH_SHORT).show() }
        }
    }
}
