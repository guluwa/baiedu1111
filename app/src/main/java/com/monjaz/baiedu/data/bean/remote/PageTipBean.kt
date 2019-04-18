package com.monjaz.baiedu.data.bean.remote

import java.io.Serializable

/**
 * Created by guluwa on 2018/4/2.
 * 页面状态0：加载 1：错误 2:数据
 */
class PageTipBean(var tip: String, var src: Int, var status: Int) : Serializable