package com.monjaz.baiedu.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.monjaz.baiedu.manage.Contacts

/**
 * Created by guluwa on 2018/6/20.
 */

open class BaseViewModel : ViewModel() {

    fun judgeUser(map: Map<String, String>): Boolean {
        return map.containsKey(Contacts.TOKEN) && map[Contacts.TOKEN] == ""
    }
}