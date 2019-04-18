package com.monjaz.baiedu

import org.junit.Test

import java.util.*


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        main()
    }

    val weeks = arrayOf("星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")

    private fun main() {
        // TODO Auto-generated method stub
        val cale = GregorianCalendar(2009, 0, 31)
        System.out.println(cale.time.toLocaleString())
        println("FirstDayOfWeek MinimalDaysInFirstWeek week")
        for (i in 1..7) {
            cale.firstDayOfWeek = i//1为星期天,7为星期六
            for (j in 1..7) {
                cale.minimalDaysInFirstWeek = j
                println(weeks[i - 1] + " " + j + " " + cale.get(Calendar.WEEK_OF_MONTH))
            }
        }
    }
}
