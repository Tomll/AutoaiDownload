package com.autoai.download;

import android.content.Context;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;

import java.io.File;

/**
 * 2018/12/06 dongrp
 */
public class AutoaiDownload {

    public static DbUtils dbUtils;
    public static DownloadManager downloadManager;
    public static String fileDownloadPath;//文件下载目录绝对路径
    public static String dataBaseloadPath;//数据库目录绝对路径

    public final static String DB_NAME = "ADDB.db";//数据库名称
    public final static int DB_VERSION = 1;//数据库版本
    public final static String DOWNLOAD_ROOT_DIR = "AutoaiDownload";//文件系统根目录
    public final static String DOWNLOAD_FILES_DIR = "files";//文件下载文件夹
    public final static String DOWNLOAD_DB_DIR = "db";//数据库文件夹

    //AutoaiDownload框架初始化
    public static void init(Context context) {
        //////////////1、初始化框架文件系统
        //创建文件系统根目录
        File externalRoot = context.getExternalFilesDir(DOWNLOAD_ROOT_DIR);
        //File internalRoot = context.getFilesDir();
        //创建文件下载目录
        File fileDownloadDirFile = new File(externalRoot.getAbsolutePath() + File.separator + DOWNLOAD_FILES_DIR + File.separator);
        if (!fileDownloadDirFile.exists()) {
            fileDownloadDirFile.mkdirs();
        }
        fileDownloadPath = fileDownloadDirFile.getAbsolutePath();
        //创建数据库目录
        File dataBaseDirFile = new File(externalRoot.getAbsolutePath() + File.separator + DOWNLOAD_DB_DIR + File.separator);
        if (!dataBaseDirFile.exists()) {
            dataBaseDirFile.mkdirs();
        }
        dataBaseloadPath = dataBaseDirFile.getAbsolutePath();
        /////////////2、初始化框架数据库、dbUtils ，并创建相应的的table
        try {
            dbUtils = DbUtils.create(context, dataBaseloadPath + File.separator + DB_NAME, DB_VERSION, null);
            dbUtils.configAllowTransaction(true);// 开启事务
            dbUtils.createTableIfNotExist(DownloadInfo.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        /////////////3、初始化框架的下载管理器
        downloadManager = new DownloadManager();
    }

    public static DownloadInfo getDownloadInfo(long downloadID) {
        return downloadManager.getDownloadInfo(downloadID);
    }

    /**
     * 添加一个新的下载任务
     *
     * @param fileName         用户定义的下载文件名
     * @param downloadUrl      文件网络下载路径
     * @param fileSavePath     下载文件的本地存储路径
     * @param downloadListener 下载监听器
     * @return 返回唯一的downloadId给用户，用于后期查询
     */
    public static DownloadInfo addNewDownload(String fileName, String downloadUrl, String fileSavePath, DownloadListener downloadListener) {
        DownloadInfo downloadInfo = new DownloadInfo();
        downloadInfo.setFileName(fileName);
        downloadInfo.setDownloadUrl(downloadUrl);
        if (null != fileSavePath) {
            downloadInfo.setFileSavePath(fileSavePath);
        } else {
            downloadInfo.setFileSavePath(fileDownloadPath + File.separator + fileName);
        }
        downloadInfo.setAutoRename(true);
        downloadInfo.setAutoResume(true);
        downloadInfo.setDownloadId(System.currentTimeMillis());
        try {
            downloadManager.addNewDownload(downloadInfo, downloadListener);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return downloadInfo;
    }

    //继续下载指定任务
    public static void resumeDownload(DownloadInfo downloadInfo, DownloadListener downloadListener) {
        try {
            downloadManager.resumeDownload(downloadInfo, downloadListener);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    //停止下载指定的下载任务
    public static void stopDownload(DownloadInfo downloadInfo) {
        try {
            downloadManager.stopDownload(downloadInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    //删除下载指定任务
    public static void removeDownload(DownloadInfo downloadInfo) {
        if (downloadInfo != null) {
            //删除文件
            File file = new File(downloadInfo.getFileSavePath());
            if (file.exists()) file.delete();
            //移除下载项
            try {
                downloadManager.removeDownload(downloadInfo);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }


    //下载状态回调接口
    public interface DownloadListener {
        void onDownloadStart(long downloadId, int downloadProgress);

        void onDownloading(long downloadId, int downloadProgress);

        void onDownloadCancel(long downloadId, int downloadProgress);

        void onDownloadSuccess(long downloadId);

        void onDownloadFail(long downloadId, HttpException error, String errorMsg);
    }

}
