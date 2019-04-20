package com.monjaz.baiedu.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.os.Environment
import android.preference.PreferenceManager
import android.text.BidiFormatter
import android.text.TextDirectionHeuristics
import android.util.Base64
import android.util.DisplayMetrics
import com.google.gson.internal.LinkedTreeMap
import com.monjaz.baiedu.data.bean.remote.ClassLessonItemBean
import com.monjaz.baiedu.data.bean.remote.ClassLessonsBean
import com.monjaz.baiedu.manage.MyApplication
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.ActivityManager
import android.util.Log
import java.net.HttpURLConnection
import java.net.URL


object AppUtils {

    //获取sharePreference String类型的值
    fun getString(key: String, defaultValue: String): String {
        val settings = PreferenceManager
            .getDefaultSharedPreferences(MyApplication.getContext())
        return if (settings.getString(key, defaultValue) == null) "" else settings.getString(key, defaultValue)!!
    }

    //设置sharePreference String类型的值
    fun setString(key: String, value: String) {
        val settings = PreferenceManager
            .getDefaultSharedPreferences(MyApplication.getContext())
        settings.edit().putString(key, value).apply()
    }

    /**
     * 检测网络是否连接
     */
    val isNetConnected: Boolean
        get() {
            val cm = MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = cm.activeNetworkInfo
            if (networkInfo != null) {
                return true
            }
            return false
        }

    /**
     * 版本名
     */
    fun getVersionName(): String {
        return getPackageInfo(MyApplication.getContext())!!.versionName
    }

    private fun getPackageInfo(context: Context): PackageInfo? {
        var pi: PackageInfo? = null

        try {
            val pm = context.packageManager
            pi = pm.getPackageInfo(
                context.packageName,
                PackageManager.GET_CONFIGURATIONS
            )

            return pi
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return pi
    }

    fun encodeImage(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        //读取图片到ByteArrayOutputStream
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos) //参数如果为100那么就不压缩
        val bytes = baos.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    fun parseClassLessons(data: Any?): List<ClassLessonItemBean> {
        val list = mutableListOf<ClassLessonItemBean>()
        val map = data as LinkedTreeMap<*, *>
        var index = 1
        for (key in map.keys) {
            parseClassLessonsDay(map[key] as LinkedTreeMap<*, *>, list, index)
            index++
        }
        return list
    }

    private fun parseClassLessonsDay(data: LinkedTreeMap<*, *>, list: MutableList<ClassLessonItemBean>, day: Int) {
        for (key in data.keys) {
            parseClassLessonsSingle(data[key] as LinkedTreeMap<*, *>, list, day, key as String)
        }
    }

//    private fun parseClassLessonsNoon(
//        data: LinkedTreeMap<*, *>,
//        list: MutableList<ClassLessonItemBean>,
//        day: Int,
//        noon: String
//    ) {
//        for (key in data.keys) {
//            parseClassLessonsSingle(data[key] as LinkedTreeMap<*, *>, list, day, noon, key as String)
//        }
//    }

    private fun parseClassLessonsSingle(
        lesson: LinkedTreeMap<*, *>,
        list: MutableList<ClassLessonItemBean>,
        day: Int,
        index: String
    ) {
//        Log.e("error", "$day $noon $index $lesson)")
        val item = ClassLessonItemBean()
        item.day = day
        val start = index.toInt()
        item.start = start
        item.step = 1
        item.weekList = arrayListOf(1)
        for (key in lesson.keys) {
            when (key as String) {
                "id" -> item.id = (lesson[key] as Double).toInt()
                "name" -> item.name = lesson[key] as String
                "teacherid" -> item.teacherId = (lesson[key] as Double).toInt()
                "teachername" -> item.teacher = lesson[key] as String
                "time" -> item.time = lesson[key] as String
                "class" -> item.room = lesson[key] as String
            }
        }
        list.add(item)
    }

    fun getMaxLessonsNum(result: ClassLessonsBean): Int? {
        val numArray = arrayOf(0, 0, 0, 0, 0, 0, 0)
        for (item in result.lessons) {
            if (numArray[item.day - 1] < item.start) {
                numArray[item.day - 1] = item.start
            }
        }
        return numArray.max()
    }

    fun getRandomColor(): String {
        return when (Math.floor(Math.random() * 4).toInt()) {
            0 -> "#EAB185"
            1 -> "#83D4A2"
            2 -> "#E48ABC"
            else -> "#D7BA4B"
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 保存图片
     */
    fun saveBitmap(bitmap: Bitmap, fileName: String, needRecycle: Boolean): File {
        val file = File(Environment.getExternalStorageDirectory().absolutePath + "/baiedu")
        if (!file.exists()) {
            file.mkdirs()
        }
        val realFile = File(file, fileName)
        if (!realFile.exists()) {
            realFile.createNewFile()
        }
        val fos = FileOutputStream(realFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.flush()
        fos.close()
        if (!bitmap.isRecycled && needRecycle) {
            bitmap.recycle()
            System.gc() // 通知系统回收
        }
        return realFile
    }

    //手机屏幕宽高
    fun getDisplayMetrics(context: Context): DisplayMetrics {
        val metric = DisplayMetrics()
        getActivity(context).windowManager.defaultDisplay.getMetrics(metric)
        return metric
    }

    private fun getActivity(c: Context): Activity {
        var context = c
        // Gross way of unwrapping the Activity so we can get the FragmentManager
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            context = context.baseContext
        }
        throw  IllegalStateException("The Context is not an Activity.")
    }

    fun getDateString(date: Date): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return simpleDateFormat.format(date)
    }

    fun getBeginDayOfWeek(): Date {
        val date = Date()
        val cal = Calendar.getInstance()
        cal.time = date
        var dayofweek = cal.get(Calendar.DAY_OF_WEEK)
        if (dayofweek == 1) {
            dayofweek += 7
        }
        cal.add(Calendar.DATE, 2 - dayofweek)
        return cal.time
    }

    fun getBeginDayOfLastWeek(date: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        var dayofweek = cal.get(Calendar.DAY_OF_WEEK)
        if (dayofweek == 1) {
            dayofweek += 7
        }
        cal.add(Calendar.DATE, 2 - dayofweek - 7)
        return cal.time
    }

    fun getBeginDayOfNextWeek(date: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        var dayofweek = cal.get(Calendar.DAY_OF_WEEK)
        if (dayofweek == 1) {
            dayofweek += 7
        }
        cal.add(Calendar.DATE, 2 - dayofweek + 7)
        return cal.time
    }

    fun checkArrayEmpty(list: List<String>): Boolean {
        return when {
            list.isEmpty() -> true
            else -> list[0] == ""
        }
    }

    fun transTextRToL(str: String): String {
        val formatter = BidiFormatter.getInstance();
        return formatter.unicodeWrap(str, TextDirectionHeuristics.LTR)
    }

    fun isBackground(context: Context): Boolean {
        val activityManager = context
            .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager
            .runningAppProcesses
        for (appProcess in appProcesses) {
            if (appProcess.processName == context.packageName) {
                /*
                BACKGROUND=400 EMPTY=500 FOREGROUND=100
                GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
                 */
                Log.i(
                    context.packageName, "此appimportace ="
                            + appProcess.importance
                            + ",context.getClass().getName()="
                            + context.javaClass.name
                )
                if (appProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.i(context.packageName, "处于后台" + appProcess.processName)
                    return true
                } else {
                    Log.i(context.packageName, "处于前台" + appProcess.processName)
                    return false
                }
            }
        }
        return false
    }

    fun syncRequest(appServerUrl: String): String? {
        try {
            val httpConn = URL(appServerUrl).openConnection() as HttpURLConnection
            httpConn.requestMethod = "GET"
            httpConn.connectTimeout = 5000
            httpConn.readTimeout = 10000
            val responseCode = httpConn.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return null
            }

            var length = httpConn.contentLength
            if (length <= 0) {
                length = 16 * 1024
            }
            val inputStream = httpConn.inputStream
            val data = ByteArray(length)
            val read = inputStream.read(data)
            inputStream.close()
            return if (read <= 0) {
                null
            } else String(data, 0, read)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
}