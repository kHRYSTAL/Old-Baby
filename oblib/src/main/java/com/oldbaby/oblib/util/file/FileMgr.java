package com.oldbaby.oblib.util.file;

import android.os.Environment;


import com.oldbaby.oblib.component.application.OGApplication;

import java.io.File;

/**
 * 文件类型，文件路径管理类
 */
public class FileMgr {

    public enum DirType {
        LARGE_IMAGE, TMP, LOG, APK, UPLOAD, CHAT, IMAGE, HTML
    }

    private static FileMgr instance;

    private FileMgr() {
    }

    public static FileMgr Instance() {
        if (instance == null) {
            synchronized (FileMgr.class) {
                if (instance == null) {
                    instance = new FileMgr();
                }
            }
        }
        return instance;
    }

    /**
     * 获取app根目录
     */
    public String getRootFolder() {
        // TODO: 18/9/20
        return Environment.getExternalStorageDirectory() + File.separator
                + OGApplication.APP_CONFIG.getRootDir();
    }

    /**
     * 获取指定类型目录
     *
     */
    public File getDir(DirType dirType) {
        String dirPath = getRootFolder() + File.separator + dirType.name();
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 获取指定类型，指定文件名的路径
     *
     * @param dirName  文件类型
     * @param filename 文件名
     * @return 路径
     */
    public String getFilepath(DirType dirName, String filename) {
        File dir = getDir(dirName);
        return dir.getAbsolutePath() + File.separator + filename;
    }
}
