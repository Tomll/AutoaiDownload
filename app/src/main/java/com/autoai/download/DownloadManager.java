package com.autoai.download;

import android.content.Context;
import android.database.Cursor;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.converter.ColumnConverter;
import com.lidroid.xutils.db.converter.ColumnConverterFactory;
import com.lidroid.xutils.db.sqlite.ColumnDbType;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 2018/12/06 dongrp
 */
public class DownloadManager {

    private int maxDownloadThread = 10;
    private List<DownloadInfo> downloadInfoList = new ArrayList<DownloadInfo>();
    private Map<Long, DownloadInfo> downloadInfoMap = new HashMap<>();

    public DownloadManager(Context appContext) {
        ColumnConverterFactory.registerColumnConverter(HttpHandler.State.class, new HttpHandlerStateConverter());
        try {
            downloadInfoList = AutoaiDownload.dbUtils.findAll(Selector.from(DownloadInfo.class));
            for (DownloadInfo downloadInfo : downloadInfoList) {
                downloadInfoMap.put(downloadInfo.getDownloadId(), downloadInfo);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public int getDownloadInfoListCount() {
        return downloadInfoList.size();
    }

    public DownloadInfo getDownloadInfo(long downloadID) {
        return downloadInfoMap.get(downloadID);
    }

    public void setMaxDownloadThread(int maxDownloadThread) {
        //设置最大下载线程数
        this.maxDownloadThread = maxDownloadThread;
    }


    /**
     * 添加一个新的下载任务
     *
     * @param fileName     用户定义的下载文件名
     * @param downloadUrl  文件网络下载路径
     * @param fileSavePath 下载文件的本地存储路径
     * @param downloadListener
     * @return 返回唯一的downloadId给用户，用于后期查询
     */
    public DownloadInfo addNewDownload(String fileName, String downloadUrl, String fileSavePath, AutoaiDownload.DownloadListener downloadListener) throws DbException {
        DownloadInfo downloadInfo = new DownloadInfo();
        downloadInfo.setFileName(fileName);
        downloadInfo.setDownloadUrl(downloadUrl);
        if (null != fileSavePath){
            downloadInfo.setFileSavePath(fileSavePath);
        }else {
            downloadInfo.setFileSavePath(AutoaiDownload.fileDownloadPath+ File.separator + fileName);
        }
        downloadInfo.setAutoRename(true);
        downloadInfo.setAutoResume(true);
        downloadInfo.setDownloadId(System.currentTimeMillis());
//        downloadInfo.setDownloadId(97658);
        addNewDownload(downloadInfo, downloadListener);
        return downloadInfo;
    }

    /**
     * <p>功能描述</p>添加一个新的下载文件
     */
    public void addNewDownload(DownloadInfo downloadInfo, AutoaiDownload.DownloadListener downloadListener) throws DbException {
        if (downloadInfoList.contains(downloadInfo) || downloadInfoMap.containsKey(downloadInfo.getDownloadId())) {
            return;
        } else {
            HttpUtils http = new HttpUtils();
            http.configRequestThreadPoolSize(maxDownloadThread);
            HttpHandler<File> handler = http.download(downloadInfo.getDownloadUrl(), downloadInfo.getFileSavePath(), downloadInfo.isAutoResume(),
                    downloadInfo.isAutoRename(), new ManagerCallBack(downloadInfo, downloadListener));
            downloadInfo.setHandler(handler);
            downloadInfo.setState(handler.getState());
            downloadInfoList.add(downloadInfo);
            downloadInfoMap.put(downloadInfo.getDownloadId(), downloadInfo);
            AutoaiDownload.dbUtils.saveBindingId(downloadInfo);
        }
    }

    /**
     * <p>功能描述</p>重新下载指定文件
     */
    public void resumeDownload(DownloadInfo downloadInfo, AutoaiDownload.DownloadListener downloadListener) throws DbException {
        HttpUtils http = new HttpUtils();
        http.configRequestThreadPoolSize(maxDownloadThread);
        HttpHandler<File> handler = http.download(downloadInfo.getDownloadUrl(), downloadInfo.getFileSavePath(), downloadInfo.isAutoResume(),
                downloadInfo.isAutoRename(), new ManagerCallBack(downloadInfo, downloadListener));
        downloadInfo.setHandler(handler);
        downloadInfo.setState(handler.getState());
        AutoaiDownload.dbUtils.saveOrUpdate(downloadInfo);
    }

    /**
     * <p>功能描述</p>停止下载指定的下载文件
     */
    public void stopDownload(DownloadInfo downloadInfo) throws DbException {
        HttpHandler<File> handler = downloadInfo.getHandler();
        if (handler != null && !handler.isCancelled()) {
            handler.cancel();
        } else {
            downloadInfo.setState(HttpHandler.State.CANCELLED);
        }
        AutoaiDownload.dbUtils.saveOrUpdate(downloadInfo);
    }

    /**
     * <p>功能描述</p>删除下载指定文件
     */
    public void removeDownload(DownloadInfo downloadInfo) throws DbException {
        HttpHandler<File> handler = downloadInfo.getHandler();
        if (handler != null && !handler.isCancelled()) {
            handler.cancel();
        }
        downloadInfoList.remove(downloadInfo);
        downloadInfoMap.remove(downloadInfo.getDownloadId());
        AutoaiDownload.dbUtils.delete(downloadInfo);
    }

//    /**
//     * <p>功能描述</p>重新下载所有文件的
//     */
//    public void resumeAllDownload() throws DbException {
//        for (final DownloadInfo downloadInfo : downloadInfoList) {
//            try {
//                HttpHandler<File> handler = downloadInfo.getHandler();
//                if (handler != null) {
//                    RequestCallBack<File> callback = handler.getRequestCallBack();
//                    resumeDownload(downloadInfo, callback);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    /**
//     * <p>功能描述</p>停止所有文件的下载
//     */
//    public void stopAllDownload() throws DbException {
//        for (DownloadInfo downloadInfo : downloadInfoList) {
//            HttpHandler<File> handler = downloadInfo.getHandler();
//            if (handler != null && !handler.isCancelled()) {
//                handler.cancel();
//            } else {
//                downloadInfo.setState(HttpHandler.State.CANCELLED);
//            }
//        }
//        AutoaiDownload.dbUtils.saveOrUpdateAll(downloadInfoList);
//    }
//
//    /**
//     * <p>功能描述</p>后台继续下载所有文件
//     */
//    public void backupDownloadInfoList() throws DbException {
//        for (DownloadInfo downloadInfo : downloadInfoList) {
//            HttpHandler<File> handler = downloadInfo.getHandler();
//            if (handler != null) {
//                downloadInfo.setState(handler.getState());
//            }
//        }
//        AutoaiDownload.dbUtils.saveOrUpdateAll(downloadInfoList);
//    }

    public class ManagerCallBack extends RequestCallBack<File> {
        private DownloadInfo downloadInfo;
        private AutoaiDownload.DownloadListener downloadListener;

        private ManagerCallBack(DownloadInfo downloadInfo, AutoaiDownload.DownloadListener downloadListener) {
            this.downloadInfo = downloadInfo;
            this.downloadListener = downloadListener;
        }

        /*@Override
        public Object getUserTag() {
            if (baseCallBack == null) return null;
            return baseCallBack.getUserTag();
            return null;
        }
        @Override
        public void setUserTag(Object userTag) {
            if (baseCallBack == null) return;
            baseCallBack.setUserTag(userTag);
        }*/

        @Override
        public void onStart() {
            HttpHandler<File> handler = downloadInfo.getHandler();
            if (handler != null) {
                downloadInfo.setState(handler.getState());
            }
            try {
                AutoaiDownload.dbUtils.saveOrUpdate(downloadInfo);
            } catch (DbException e) {
                e.printStackTrace();
            }
            if (downloadListener != null) {
                if (downloadInfo != null && downloadInfo.getFileLength() > 0) {
                    downloadListener.onDownloadStart(downloadInfo.getDownloadId(), (int) (downloadInfo.getProgress() * 100 / downloadInfo.getFileLength()));
                } else {
                    downloadListener.onDownloadStart(downloadInfo.getDownloadId(), 0);
                }
            }
        }

        @Override
        public void onCancelled() {
            HttpHandler<File> handler = downloadInfo.getHandler();
            if (handler != null) {
                downloadInfo.setState(handler.getState());
            }
            try {
                AutoaiDownload.dbUtils.saveOrUpdate(downloadInfo);
            } catch (DbException e) {
                e.printStackTrace();
            }
            if (downloadListener != null) {
                if (downloadInfo != null && downloadInfo.getFileLength() > 0) {
                    downloadListener.onDownloadCancel(downloadInfo.getDownloadId(), (int) (downloadInfo.getProgress() * 100 / downloadInfo.getFileLength()));
                } else {
                    downloadListener.onDownloadCancel(downloadInfo.getDownloadId(), 0);
                }
            }
        }

        @Override
        public void onLoading(long total, long current, boolean isUploading) {
            HttpHandler<File> handler = downloadInfo.getHandler();
            if (handler != null) {
                downloadInfo.setState(handler.getState());
            }
            downloadInfo.setFileLength(total);
            downloadInfo.setProgress(current);
            try {
                AutoaiDownload.dbUtils.saveOrUpdate(downloadInfo);
            } catch (DbException e) {
                e.printStackTrace();
            }
            if (downloadListener != null) {
                downloadListener.onDownloading(downloadInfo.getDownloadId(), (int) (current * 100 / total));
            }
        }

        @Override
        public void onSuccess(ResponseInfo<File> responseInfo) {
            HttpHandler<File> handler = downloadInfo.getHandler();
            if (handler != null) {
                downloadInfo.setState(handler.getState());
            }
            if (downloadListener != null) {
                downloadListener.onDownloadSuccess(downloadInfo.getDownloadId());
            }
        }

        @Override
        public void onFailure(HttpException error, String msg) {
            HttpHandler<File> handler = downloadInfo.getHandler();
            if (handler != null) {
                downloadInfo.setState(handler.getState());
            }
            try {
                AutoaiDownload.dbUtils.saveOrUpdate(downloadInfo);
            } catch (DbException e) {
                e.printStackTrace();
            }
            if (downloadListener != null) {
                downloadListener.onDownloadFail(downloadInfo.getDownloadId(), error, msg);
            }
        }
    }


    private class HttpHandlerStateConverter implements ColumnConverter<HttpHandler.State> {

        @Override
        public HttpHandler.State getFieldValue(Cursor cursor, int index) {
            return HttpHandler.State.valueOf(cursor.getInt(index));
        }

        @Override
        public HttpHandler.State getFieldValue(String fieldStringValue) {
            if (fieldStringValue == null) return null;
            return HttpHandler.State.valueOf(fieldStringValue);
        }

        @Override
        public Object fieldValue2ColumnValue(HttpHandler.State fieldValue) {
            return fieldValue.value();
        }

        @Override
        public ColumnDbType getColumnDbType() {
            return ColumnDbType.INTEGER;
        }
    }


    /**
     * 删除某个目录下的所有文件
     */
/*    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                flag = true;
            }
        }
        return flag;
    }*/

}
