package com.autoai.download;

import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Transient;
import com.lidroid.xutils.http.HttpHandler;

import java.io.File;

@Table(name = "DownloadInfo")
public class DownloadInfo {
    @Transient
    private HttpHandler<File> handler;
    private HttpHandler.State state;

    private long id;//数据库主键id
    private long downloadId;//下载id,返回给用户存储，后期查询
    private String fileName;//文件名
    private String downloadUrl;//文件网络下载路径
    private String fileSavePath;//下载文件存储路径
    private long progress;//已经下载的量kb
    private long fileLength;//文件的总长度
    private boolean autoResume;//是否开启断点续传
    private boolean autoRename;
    public boolean isLoadSuccess = false;//是否下载完成
    /*private int official_flag;
    private String app_v_id;
    private String logoUrl;
    @Id(column = "appId")
    private String appId;
    private String md5;*/

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

    public HttpHandler<File> getHandler() {
        return handler;
    }

    public void setHandler(HttpHandler<File> handler) {
        this.handler = handler;
    }

    public HttpHandler.State getState() {
        return state;
    }

    public void setState(HttpHandler.State state) {
        this.state = state;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSavePath() {
        return fileSavePath;
    }

    public void setFileSavePath(String fileSavePath) {
        this.fileSavePath = fileSavePath;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public boolean isAutoResume() {
        return autoResume;
    }

    public void setAutoResume(boolean autoResume) {
        this.autoResume = autoResume;
    }

    public boolean isAutoRename() {
        return autoRename;
    }

    public void setAutoRename(boolean autoRename) {
        this.autoRename = autoRename;
    }

    public boolean isLoadSuccess() {
        return isLoadSuccess;
    }

    public void setLoadSuccess(boolean isLoadSuccess) {
        this.isLoadSuccess = isLoadSuccess;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof DownloadInfo))
            return false;

        DownloadInfo that = (DownloadInfo) o;
        return id == (that.id);
    }





/*    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

       public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public int getOfficial_flag() {
        return official_flag;
    }

    public void setOfficial_flag(int official_flag) {
        this.official_flag = official_flag;
    }

    public String getApp_v_id() {
        return app_v_id;
    }

    public void setApp_v_id(String app_v_id) {
        this.app_v_id = app_v_id;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }*/


}
