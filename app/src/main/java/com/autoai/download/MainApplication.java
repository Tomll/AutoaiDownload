package com.autoai.download;

import android.app.Application;

import com.autoai.download.download.AutoaiDownload;

public class MainApplication extends Application /*implements DbUtils.DbUpgradeListener*/ {
/*    public static DbUtils dbUtils;
    public static DownloadManager downloadManager;
    public static String fileDownloadPath;
    public static String dataBaseloadPath;

    public final static String DB_NAME = "ADDB.db";//数据库名称
    public final static int DB_VERSION = 1;//数据库版本
    public final static String DOWNLOAD_FILES_DIR = "files";//文件下载目录
    public final static String DOWNLOAD_DB_DIR = "db";//数据库文件目录*/

    @Override
    public void onCreate() {
        super.onCreate();
        AutoaiDownload.init(this);
    }

    //AutoaiDownload 框架初始化
   /* public void initAutoaiDownload(){
        ////1、初始化框架文件系统
        //框架文件系统根目录
        File root = getExternalFilesDir("AutoaiDownload");
        //创建文件下载目录
        File fileDownloadDirFile = new File(root.getAbsolutePath() + File.separator + DOWNLOAD_FILES_DIR + File.separator);
        if (!fileDownloadDirFile.exists()) {
            fileDownloadDirFile.mkdirs();
        }
        fileDownloadPath = fileDownloadDirFile.getAbsolutePath();
        //创建数据库目录
        File dataBaseDirFile = new File(root.getAbsolutePath() + File.separator + DOWNLOAD_DB_DIR + File.separator);
        if (!dataBaseDirFile.exists()) {
            dataBaseDirFile.mkdirs();
        }
        dataBaseloadPath = dataBaseDirFile.getAbsolutePath();
        ////2、初始化框架数据库，并创建相应的的table
        try {
            dbUtils = DbUtils.create(this, dataBaseloadPath + File.separator + DB_NAME, DB_VERSION, this);
            dbUtils.configAllowTransaction(true);// 开启事务
            dbUtils.createTableIfNotExist(DownloadInfo.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        ////3、初始化框架的下载管理器
        downloadManager = DownloadService.getDownloadManager(this);
    }*/


/*    @Override
    public void onUpgrade(DbUtils dbUtils, int oldVersion, int newVersion) {
        try {
            if (newVersion > oldVersion) {
                dbUtils.dropDb();
                downloadManager.delAllFile(fileDownloadPath);
                dbUtils.createTableIfNotExist(DownloadInfo.class);
            }
        } catch (Exception e) {
            Log.e("message", "onUpgrade message:" + e.getMessage());
        }
    }*/


}
