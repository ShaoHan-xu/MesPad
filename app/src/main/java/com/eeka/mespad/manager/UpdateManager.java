package com.eeka.mespad.manager;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.eeka.mespad.R;
import com.eeka.mespad.utils.SystemUtils;
import com.eeka.mespad.view.dialog.ErrorDialog;

import java.io.File;

import cn.finalteam.okhttpfinal.FileDownloadCallback;
import cn.finalteam.okhttpfinal.HttpRequest;

/**
 * Created by Lenovo on 2017/9/4.
 */

public class UpdateManager {

    private static File apkFile;

    private static AlertDialog mDownloadDialog;
    private static ProgressBar mProgress;

    private static void initPath() {
        // 判断SD卡是否存在，并且是否具有读写权限
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 获得存储卡的路径
            String sdpath = Environment.getExternalStorageDirectory() + "/";
            String apkPath = sdpath + "download";
            apkFile = new File(apkPath, "MesPad.apk");
        }
    }

    public static void downloadApk(final Context context,String apkUrl) {
        initPath();
        HttpRequest.download(apkUrl, apkFile, new FileDownloadCallback() {

            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog(context);
            }

            @Override
            public void onProgress(int progress, long networkSpeed) {
                super.onProgress(progress, networkSpeed);
                mProgress.setProgress(progress);
            }

            @Override
            public void onDone() {
                super.onDone();
                mDownloadDialog.dismiss();
                Toast.makeText(context, "下载成功", Toast.LENGTH_SHORT).show();
                SystemUtils.installApk(context, apkFile.getPath());
            }

            @Override
            public void onFailure() {
                super.onFailure();
                mDownloadDialog.dismiss();
                ErrorDialog.showAlert(context, "下载失败");
            }

        });
    }

    private static void showProgressDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("下载进度");
        // 给下载对话框增加进度条
        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.layout_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.progressBar);
        builder.setView(v);
        mDownloadDialog = builder.create();
        mDownloadDialog.setCancelable(false);
        mDownloadDialog.show();
    }

}
