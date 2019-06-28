# AbsListView通用适配器


### 添加依赖

> Project build.gradle

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	
> Module build.gradle

	dependencies {
	        implementation 'com.github.luohaolun:adapter:1.2'
	}


### 使用

> 通常

	val data = mutableListOf<String>()
	for (i in 0..10) {
		data.add(i.toString())
	}
	
	lvList.adapter = Adapter(this, data, R.layout.item_test) { view, item, position ->
		view.tvNum.text = item
		view.btn.setOnClickListener { Toast.makeText(this, "点击$position", Toast.LENGTH_SHORT).show() }
	}



> 多类型Item


	val data = mutableListOf<Pair<Int, String>>()
	
	for (i in 0..20) {
		if (i < 4)
                data.add(Pair(0, i.toString()))
            else
                data.add(Pair(1, i.toString()))
        }
	
	lvList.adapter = MultiAdapter(this, data, SparseArray<Int>().apply { put(0, R.layout.item_test);put(1, R.layout.item_test_2) }) { view, item, type, position ->
	
	when (type) {
		0 -> {
                    view.tvNum.text = "$item  类型0"
                    view.btn.setOnClickListener { Toast.makeText(this, "点击$position   类型0", Toast.LENGTH_SHORT).show() }
                }
                1 -> {
                    view.tvNum.text = "$item  类型1"
                    view.btn.setOnClickListener { Toast.makeText(this, "点击$position   类型1", Toast.LENGTH_SHORT).show() }
                }
            }
	}
