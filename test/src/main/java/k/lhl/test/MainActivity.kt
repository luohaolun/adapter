package k.lhl.test

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.SparseArray
import android.widget.Toast
import k.lhl.adapter.Adapter
import k.lhl.adapter.MultiRecyclerAdapter
import k.lhl.adapter.RecyclerAdapter
import k.lhl.adapter.positon
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
//
//        recyList.layoutManager = LinearLayoutManager(this).apply { orientation = LinearLayoutManager.VERTICAL }
//
//        recyList.adapter = RecyclerAdapter(data, R.layout.item_test) { item, position ->
//            tvNum.text = item
//            btn.setOnClickListener { Toast.makeText(this@MainActivity, "点击" + position, Toast.LENGTH_SHORT).show() }
//        }.setOnItemClickListener {
//            Toast.makeText(this@MainActivity, "点击    $position", Toast.LENGTH_SHORT).show()
//        }.setOnItemLongClickListener {
//            Toast.makeText(this@MainActivity, "长按    $position", Toast.LENGTH_SHORT).show()
//        }

//
        lvList.adapter = Adapter(data, R.layout.item_test) {
            tvNum.text = this.item
            btn.setOnClickListener { Toast.makeText(this@MainActivity, "点击" + item, Toast.LENGTH_SHORT).show() }
        }

//        val data = mutableListOf<Pair<Int, String>>()
//        for (i in 1..20) {
//            if (i < 4)
//                data.add(Pair(0, i.toString()))
//            else
//                data.add(Pair(1, i.toString()))
//        }
//
//        recyList.layoutManager = LinearLayoutManager(this).apply { orientation = LinearLayoutManager.VERTICAL }
//        recyList.adapter = MultiRecyclerAdapter(data, SparseArray<Int>().apply { put(0, R.layout.item_test);put(1, R.layout.item_test_2) }) { item, position ->
//            tvNum.text = item
//            btn.setOnClickListener { Toast.makeText(this@MainActivity, "点击" + position, Toast.LENGTH_SHORT).show() }
//        }.setOnItemClickListener {
//            Toast.makeText(this@MainActivity, "点击    $item", Toast.LENGTH_SHORT).show()
//        }.setOnItemLongClickListener {
//            Toast.makeText(this@MainActivity, "长按    $position", Toast.LENGTH_SHORT).show()
//        }


//
//
//        lvList.adapter = MultiAdapter(data, SparseArray<Int>().apply { put(0, R.layout.item_test);put(1, R.layout.item_test_2) }) { type, item, position ->
//            when (type) {
//                0 -> {
//                    tvNum.text = "$item  类型0"
//                    btn.setOnClickListener { Toast.makeText(this@MainActivity, "点击$position   类型0", Toast.LENGTH_SHORT).show() }
//                }
//                1 -> {
//                    tvNum.text = "$item  类型1"
//                    btn.setOnClickListener { Toast.makeText(this@MainActivity, "点击$position   类型1", Toast.LENGTH_SHORT).show() }
//                }
//            }
//        }


    }
}
