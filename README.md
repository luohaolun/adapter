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


	val data = mutableListOf<String>()
	for (i in 0..10) {
		data.add(i.toString())
	}
	
	lvList.adapter = Adapter(this, data, R.layout.item_test) { view, item, position ->
		view.tvNum.text = item
		view.btn.setOnClickListener { Toast.makeText(this, "点击$position", Toast.LENGTH_SHORT).show() }
	}
