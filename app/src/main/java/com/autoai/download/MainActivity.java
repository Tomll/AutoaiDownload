package com.autoai.download;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lidroid.xutils.exception.HttpException;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity implements AutoaiDownload.DownloadListener {
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.startDownload)
    Button startDownload;
    @BindView(R.id.downloading)
    Button downloading;
    @BindView(R.id.resumeDownload)
    Button resumeDownload;
    @BindView(R.id.downloadDone)
    Button downloadDone;
    @BindView(R.id.button4)
    Button button4;

    public final static int DOWNLOAD_BUTTON_STATUS_START = 1;
    public final static int DOWNLOAD_BUTTON_STATUS_LOADING = 2;
    public final static int DOWNLOAD_BUTTON_STATUS_PAUSE = 3;
    public final static int DOWNLOAD_BUTTON_STATUS_DONE = 4;
    private static String downloadUrl = "https://wdcdn.mapbar.com/appstoreapi/apk/84f1539a525c4ddb87a6d0dd0e9ced11.apk";
    DownloadInfo downloadInfo;
    long downloadId = 97658;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);//绑定初始化ButterKnife
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10001);
                return;
            }
        }
    }

/*    @Override
    protected void onResume() {
        super.onResume();
        downloadInfo = AutoaiDownload.getDownloadInfo(downloadId);
        if (null != downloadInfo) {
            Log.e("dongrp", "onResume: downloadInfo.isLoadSuccess : " + downloadInfo.isLoadSuccess());
        }
        if (downloadInfo != null && !downloadInfo.isLoadSuccess()) {
            progressBar.setProgress((int) (downloadInfo.getProgress() * 100 / downloadInfo.getFileLength()));
            setButtonState(DOWNLOAD_BUTTON_STATUS_PAUSE);
        } else if (downloadInfo != null && downloadInfo.isLoadSuccess()) {
            progressBar.setProgress(100);
            setButtonState(DOWNLOAD_BUTTON_STATUS_DONE);
        } else {
            progressBar.setProgress(0);
            setButtonState(DOWNLOAD_BUTTON_STATUS_START);
        }
    }*/

    @OnClick({R.id.startDownload, R.id.downloading, R.id.resumeDownload, R.id.downloadDone, R.id.button4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.startDownload://开始
                Log.d("dongrp", "开始下载");
                setButtonState(DOWNLOAD_BUTTON_STATUS_LOADING);
                String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
                String fileSavePath = AutoaiDownload.fileDownloadPath + File.separator + fileName;
                downloadInfo = AutoaiDownload.addNewDownload(fileName, downloadUrl, fileSavePath, this);
                downloadId = downloadInfo.getDownloadId();
                break;
            case R.id.downloading://暂停
                Log.d("dongrp", "暂停下载");
                setButtonState(DOWNLOAD_BUTTON_STATUS_PAUSE);
                if (downloadInfo != null) {
                    AutoaiDownload.stopDownload(downloadInfo);
                }
                break;
            case R.id.resumeDownload://继续
                Log.d("dongrp", "继续下载");
                setButtonState(DOWNLOAD_BUTTON_STATUS_LOADING);
                if (downloadInfo != null) {
                    AutoaiDownload.resumeDownload(downloadInfo, this);
                }
                break;
            case R.id.button4://取消
                Log.d("dongrp", "取消下载");
                setButtonState(DOWNLOAD_BUTTON_STATUS_START);
                progressBar.setProgress(0);
                if (downloadInfo != null) {
                    AutoaiDownload.removeDownload(downloadInfo);
                    downloadInfo = null;
                }
                break;
            default:
                break;
        }
    }


    private void setButtonState(int state) {
        switch (state) {
            case DOWNLOAD_BUTTON_STATUS_START:
                startDownload.setVisibility(View.VISIBLE);
                downloading.setVisibility(View.GONE);
                resumeDownload.setVisibility(View.GONE);
                downloadDone.setVisibility(View.GONE);
                break;
            case DOWNLOAD_BUTTON_STATUS_LOADING:
                startDownload.setVisibility(View.GONE);
                downloading.setVisibility(View.VISIBLE);
                resumeDownload.setVisibility(View.GONE);
                downloadDone.setVisibility(View.GONE);
                break;
            case DOWNLOAD_BUTTON_STATUS_PAUSE:
                startDownload.setVisibility(View.GONE);
                downloading.setVisibility(View.GONE);
                resumeDownload.setVisibility(View.VISIBLE);
                downloadDone.setVisibility(View.GONE);
                break;
            case DOWNLOAD_BUTTON_STATUS_DONE:
                startDownload.setVisibility(View.GONE);
                downloading.setVisibility(View.GONE);
                resumeDownload.setVisibility(View.GONE);
                downloadDone.setVisibility(View.VISIBLE);
                break;
        }
    }


    @Override
    public void onDownloadStart(long downloadId, int downloadProgress) {
        progressBar.setProgress(downloadProgress);
    }

    @Override
    public void onDownloading(long downloadId, int downloadProgress) {
        progressBar.setProgress(downloadProgress);
    }

    @Override
    public void onDownloadCancel(long downloadId, int downloadProgress) {
    }

    @Override
    public void onDownloadSuccess(long downloadId) {
        Toast.makeText(MainActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
        setButtonState(DOWNLOAD_BUTTON_STATUS_DONE);
    }

    @Override
    public void onDownloadFail(long downloadId, HttpException error, String errorMsg) {
        Toast.makeText(MainActivity.this, "下载失败：" + errorMsg, Toast.LENGTH_LONG).show();
        setButtonState(DOWNLOAD_BUTTON_STATUS_PAUSE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10001 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            finish();
        }
    }















































/*    private class DownloadRequestCallBack extends RequestCallBack<File> {

        @Override
        public void onStart() {
            //开始下载 和 继续下载 走的都是该回调方法
            Log.e("dongrp", "开始下载");
//            setButtonState(Configs.APP_BUTTON_STATUS_PAUSE);
            if (downloadInfo != null && downloadInfo.getFileLength() > 0) {
                progressBar.setProgress((int) (downloadInfo.getProgress() * 100 / downloadInfo.getFileLength()));
            } else {
                progressBar.setProgress(0);
            }
        }

        public void onLoading(long total, long current, boolean isUploading) {
            Log.e("dongrp", "onLoading------");
            //setButtonState(Configs.APP_BUTTON_STATUS_PAUSE);
            progressBar.setProgress((int) (current * 100 / (double) total));
        }

        @Override
        public void onCancelled() {
            //暂停下载 和 取消下载 都走这个回调方法
            Log.e("dongrp", "取消下载");
            //setButtonState(Configs.APP_BUTTON_STATUS_START);
            if (downloadInfo != null) {
                if (downloadInfo.getFileLength() > 0) {
                    progressBar.setProgress((int) (downloadInfo.getProgress() * 100 / downloadInfo.getFileLength()));
                } else {
                    progressBar.setProgress(0);
                }
            }
        }

        @Override
        public void onSuccess(ResponseInfo<File> arg0) {
//            setButtonState(Configs.APP_BUTTON_STATUS_INSTALL);
*//*            try {
                if (downloadInfo != null && !downloadInfo.isLoadSuccess) {
                    downloadInfo.setLoadSuccess(true);
                    AutoaiDownload.dbUtils.saveOrUpdate(downloadInfo);
                    sendLoadSuccessLog(downloadInfo.getAppId(), downloadInfo.getApp_v_id());
                    File file = new File(downloadInfo.getFileSavePath());
                    if (file.exists()) {
                        if (CommonUtil.verifyFileMD5(file, downloadInfo.getMd5())) {//校验通过
                            AppUtil.installApp(mContext, file);
                        } else {//校验失败
                            file.delete();
                            checkApkMd5();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*//*
            Log.e("dongrp", "下载成功");
        }

        @Override
        public void onFailure(HttpException error, String arg1) {
            *//*if (error.getExceptionCode() == 416) {
                setButtonState(Configs.APP_BUTTON_STATUS_INSTALL);
                try {
                    if (downloadInfo != null && !downloadInfo.isLoadSuccess) {
                        downloadInfo.setLoadSuccess(true);
                        AutoaiDownload.dbUtils.saveOrUpdate(downloadInfo);
                        File file = new File(downloadInfo.getFileSavePath());
                        if (file.exists()) AppUtil.installApp(mContext, file);
                        sendLoadSuccessLog(downloadInfo.getAppId(), downloadInfo.getApp_v_id());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                setButtonState(Configs.APP_BUTTON_STATUS_START);
                //Bug #14765：若下载过程中网络错误，弹窗提示
                mAif.showAlert(R.string.dialog_loading_net_error);
                try {
                    if (downloadInfo != null) {
                        if (downloadInfo.getFileLength() > 0) {
                            lcb_download_progress.setProgress((int) (downloadInfo.getProgress() * 100 / downloadInfo.getFileLength()));
                        } else {
                            lcb_download_progress.setProgress(0);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*//*
            Log.e("dongrp", "下载失败");
        }
    }*/


}
