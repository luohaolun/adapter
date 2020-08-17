package lhl.kotlinextends

import android.util.Log
import java.lang.Exception
import java.util.*

const val ClassName = "lhl.kotlinextends.LogKt"
private var maxLength = 3900

fun setLogMaxLength(int: Int) {
    maxLength = int
}

fun pe(log: Any) {
    print("e", log, maxLength)
}

fun Any.e() {
    print("e", this, maxLength)
}

fun Any.e(maxLen: Int = maxLength) {
    print("e", this, maxLen)
}

fun Any.d(maxLen: Int = maxLength) {
    print("d", this, maxLen)
}

fun Any.d() {
    print("d", this, maxLength)
}

fun Any.i(maxLen: Int = maxLength) {
    print("i", this, maxLen)
}

fun Any.i() {
    print("i", this, maxLength)
}

fun Any.v(maxLen: Int = maxLength) {
    print("v", this, maxLen)
}

fun Any.v() {
    print("v", this, maxLength)
}

fun Any.w(maxLen: Int = maxLength) {
    print("w", this, maxLen)
}

fun Any.w() {
    print("w", this, maxLength)
}

fun Any.wtf(maxLen: Int = maxLength) {
    print("wtf", this, maxLen)
}

fun Any.wtf() {
    print("wtf", this, maxLength)
}


private fun print(logLevel: String, log: Any, maxLen: Int) {
    var tag = Thread.currentThread().name + try {
        val traces = Thread.currentThread().stackTrace
        val stackElement = traces[traces.indexOfLast { it.className == ClassName } + 1]
        "(${stackElement.fileName}:${stackElement.lineNumber})"
    } catch (e: Exception) {
        "TAG"
    }

    val str = when (log) {
        is Throwable -> {
            var strBuilder = StringBuilder()
            strBuilder.append(log.toString())
            log.stackTrace.forEach {
                strBuilder.append("\n     - ${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})")
            }
            strBuilder.toString()
        }
        else -> log.toString()
    }

    val sliceCount = str.length / maxLen + if ((str.length % maxLen) > 0) 1 else 0

    val method = Log::class.java.getDeclaredMethod(logLevel, String::class.java, String::class.java)
    for (i in 0 until sliceCount) {
        val space = if (i == sliceCount - 1) str.length else (i * maxLen + maxLen)
        method.invoke(null, tag, str.substring(i * maxLen, space))
    }

//    when (logLevel) {
//        "e" -> {
//            for (i in 0 until sliceCount) {
//                val space = if (i == sliceCount - 1) str.length else (i * maxLen + maxLen)
//                Log.e(tag, str.substring(i * maxLen, space))
//            }
//        }
//        "v" -> {
//            for (i in 0 until sliceCount) {
//                val space = if (i == sliceCount - 1) str.length else (i * maxLen + maxLen)
//                Log.v(tag, str.substring(i * maxLen, space))
//            }
//        }
//        "d" -> {
//            for (i in 0 until sliceCount) {
//                val space = if (i == sliceCount - 1) str.length else (i * maxLen + maxLen)
//                Log.d(tag, str.substring(i * maxLen, space))
//            }
//        }
//        "i" -> {
//            for (i in 0 until sliceCount) {
//                val space = if (i == sliceCount - 1) str.length else (i * maxLen + maxLen)
//                Log.i(tag, str.substring(i * maxLen, space))
//            }
//        }
//        "w" -> {
//            for (i in 0 until sliceCount) {
//                val space = if (i == sliceCount - 1) str.length else (i * maxLen + maxLen)
//                Log.w(tag, str.substring(i * maxLen, space))
//            }
//        }
//        "wtf" -> {
//            for (i in 0 until sliceCount) {
//                val space = if (i == sliceCount - 1) str.length else (i * maxLen + maxLen)
//                Log.wtf(tag, str.substring(i * maxLen, space))
//            }
//        }
//        else -> {
//            for (i in 0 until sliceCount) {
//                val space = if (i == sliceCount - 1) str.length else (i * maxLen + maxLen)
//                Log.e(tag, str.substring(i * maxLen, space))
//            }
//        }
//    }
}