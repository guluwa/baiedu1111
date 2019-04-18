package com.monjaz.baiedu.manage

/**
 * Created by guluwa on 2018/3/14.
 */
object Contacts {

    /**
     * permission申请code
     */
    const val PERMISSION_REQUEST_CODE = 0

    /**
     * 服务器baseUrl
     */

//    const val BASEURL = "http://crh.megglife.com/"//正式

//    const val BASEURL = "http://wanyuge.ali.zj0579.com/api/"//测试

    const val BASEURL = "http://baiedu.ali.zj0579.com/api/"

    const val TOKEN = "user_token"

    const val TYPE = "user_type"

    const val ID = "user_id"

    const val LANGUAGE = "language"

    var TOKEN_NEED_FRESH = false

    const val PUSH_NOTIFICATION_ID = (0x001)

    const val PUSH_CHANNEL_ID = "PUSH_NOTIFY_ID"

    const val PUSH_CHANNEL_NAME = "PUSH_NOTIFY_NAME"
}