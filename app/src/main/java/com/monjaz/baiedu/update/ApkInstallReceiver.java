package com.monjaz.baiedu.update;

/**
 * Created by 俊康 on 2017/9/4.
 */

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.monjaz.baiedu.R;
import com.monjaz.baiedu.base.BaseActivity;
import com.monjaz.baiedu.utils.FinishActivityManager;
import com.monjaz.baiedu.utils.ToastUtil;

import java.io.File;

import androidx.core.content.FileProvider;

/**
 * 版本更新升级 广播接受者
 */
public class ApkInstallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            long downloadApkId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            System.out.println("Download successfully" + downloadApkId);
            installApk(context, downloadApkId);
        }
    }

    /**
     * 安装apk
     */
    public static void installApk(Context context, long downloadApkId) {
        // 获取存储ID
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        long downId = sp.getLong(DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
        if (downloadApkId == downId) {
            File downloadApkFile = queryDownloadedApk(context, downloadApkId);
            if (downloadApkFile != null) {
                Intent install = new Intent(Intent.ACTION_VIEW);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //兼容8.0
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        boolean hasInstallPermission = context.getPackageManager().canRequestPackageInstalls();
                        if (!hasInstallPermission) {
                            ToastUtil.Companion.getInstance().showToast(context.getString(R.string.open_permission));
                            if (FinishActivityManager.Companion.getInstance().currentActivity() instanceof BaseActivity)
                                ((BaseActivity) FinishActivityManager.Companion.getInstance().currentActivity()).startInstallPermissionSettingActivity();
                            return;
                        } else {
                            System.out.println(context.getString(R.string.open_permission));
                        }
                    }
                    Uri uri = FileProvider.getUriForFile(context, "com.monjaz.baiedu.provider", downloadApkFile);
                    install.setDataAndType(uri, "application/vnd.android.package-archive");
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    install.setDataAndType(Uri.fromFile(downloadApkFile), "application/vnd.android.package-archive");
                }
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(install);
            } else {
                Toast.makeText(context, context.getString(R.string.download_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 通过downLoadId查询下载的apk，解决6.0以后安装的问题
     *
     * @param context
     * @return
     */
    public static File queryDownloadedApk(Context context, long downloadId) {
        File targetApkFile = null;
        DownloadManager downloader = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadId != -1) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
            Cursor cur = downloader.query(query);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    String uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    if (!TextUtils.isEmpty(uriString)) {
                        targetApkFile = new File(Uri.parse(uriString).getPath());
                    }
                }
                cur.close();
            }
        }
        return targetApkFile;

    }
}
