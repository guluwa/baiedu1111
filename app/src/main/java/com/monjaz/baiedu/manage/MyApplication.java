package com.monjaz.baiedu.manage;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import androidx.core.content.ContextCompat;
import androidx.multidex.MultiDexApplication;
import com.bilibili.boxing.BoxingMediaLoader;
import com.monjaz.baiedu.ui.service.MessageService;
import com.monjaz.baiedu.utils.BoxingGlideLoader;
import com.xdandroid.hellodaemon.DaemonEnv;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * Created by guluwa on 2018/6/1.
 */
public class MyApplication extends MultiDexApplication {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;

        initBoxing();
        initSharePic();
        handleError();
        initService();
    }

    private void initService() {
        DaemonEnv.initialize(this, MessageService.class, 6);
        DaemonEnv.startServiceMayBind(MessageService.class);
    }

    /**
     * RxJava2 当取消订阅后(dispose())，RxJava抛出的异常后续无法接收(此时后台线程仍在跑，可能会抛出IO等异常),全部由RxJavaPlugin接收，需要提前设置ErrorHandler
     * 详情：http://engineering.rallyhealth.com/mobile/rxjava/reactive/2017/03/15/migrating-to-rxjava-2.html#Error Handling
     */
    private void handleError() {
        RxJavaPlugins.setErrorHandler(throwable -> Log.e("error", throwable.getMessage()));
    }

    private void initSharePic() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    private void initBoxing() {
        BoxingGlideLoader loader = new BoxingGlideLoader();
        BoxingMediaLoader.getInstance().init(loader);
    }

    public static Context getContext() {
        return mContext;
    }
}
