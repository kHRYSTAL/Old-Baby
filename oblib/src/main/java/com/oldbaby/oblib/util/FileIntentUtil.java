package com.oldbaby.oblib.util;

import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.util.HashMap;

public class FileIntentUtil {

    private static final int INTENT_IMAGE = 1;
    private static final int INTENT_AUDIO = 2;
    private static final int INTENT_VIDEO = 3;
    private static final int INTENT_PACHAGE = 4;
    private static final int INTENT_WEBTEXT = 5;
    private static final int INTENT_TEXT = 6;
    private static final int INTENT_WORD = 7;
    private static final int INTENT_EXCEL = 8;
    private static final int INTENT_PPT = 9;
    private static final int INTENT_PDF = 10;
    private static final int INTENT_CHM = 11;
    private static final int INTENT_MAX = 12;

    private static FileIntentUtil _instance;
    private static Object lockObj = new Object();
    private final HashMap<String, Integer> map;

    public Intent getFileIntent(String filepath) {
        return getFileIntent(new File(filepath));
    }

    public Intent getFileIntent(File file) {

        String filename = file.getName();
        String[] ss = filename.split("\\.");
        int type = -1;
        if (ss.length > 1) {
            String ext = ss[ss.length - 1];
            if (ext != null && map.containsKey(ext)) {
                type = map.get(ext);
            }
        }
        if (type > 0 && type < INTENT_MAX) {

            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);

            switch (type) {
                case INTENT_AUDIO:
                    intent.putExtra("oneshot", 0);
                    intent.putExtra("configchange", 0);
                    intent.setDataAndType(uri, "audio/*");
                    break;
                case INTENT_CHM:
                    intent.setDataAndType(uri, "application/x-chm");
                    break;
                case INTENT_EXCEL:
                    intent.setDataAndType(uri, "application/vnd.ms-excel");
                    break;
                case INTENT_IMAGE:
                    intent.setDataAndType(uri, "image/*");
                    break;
                case INTENT_PDF:
                    intent.setDataAndType(uri, "application/pdf");
                    break;
                case INTENT_PACHAGE:
                    intent.setDataAndType(Uri.fromFile(file),
                            "application/vnd.android.package-archive");
                    break;
                case INTENT_PPT:
                    intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
                    break;
                case INTENT_TEXT:
                    intent.setDataAndType(uri, "text/plain");
                    break;
                case INTENT_VIDEO:
                    intent.putExtra("oneshot", 0);
                    intent.putExtra("configchange", 0);
                    intent.setDataAndType(uri, "video/*");
                    break;
                case INTENT_WEBTEXT:
                    uri = Uri.fromFile(file).buildUpon()
                            .encodedAuthority("com.android.htmlfileprovider")
                            .scheme("content").encodedPath(file.toString()).build();
                    intent.setDataAndType(uri, "text/html");

                    break;
                case INTENT_WORD:
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(uri, "application/msword");
                    break;
            }
            return intent;
        }
        return null;
    }

    private FileIntentUtil() {
        map = new HashMap<String, Integer>();

        // images
        map.put("png", INTENT_IMAGE);
        map.put("gif", INTENT_IMAGE);
        map.put("jpg", INTENT_IMAGE);
        map.put("jpeg", INTENT_IMAGE);
        map.put("bmp", INTENT_IMAGE);

        // audio
        map.put("mp3", INTENT_AUDIO);
        map.put("wav", INTENT_AUDIO);
        map.put("ogg", INTENT_AUDIO);
        map.put("midi", INTENT_AUDIO);

        // video
        map.put("mp4", INTENT_VIDEO);
        map.put("rmvb", INTENT_VIDEO);
        map.put("avi", INTENT_VIDEO);
        map.put("flv", INTENT_VIDEO);

        // package
        map.put("jar", INTENT_PACHAGE);
        map.put("zip", INTENT_PACHAGE);
        map.put("rar", INTENT_PACHAGE);
        map.put("gz", INTENT_PACHAGE);
        map.put("apk", INTENT_PACHAGE);
        map.put("img", INTENT_PACHAGE);

        // web text
        map.put("htm", INTENT_WEBTEXT);
        map.put("html", INTENT_WEBTEXT);
        map.put("php", INTENT_WEBTEXT);
        map.put("jsp", INTENT_WEBTEXT);

        // text
        map.put("txt", INTENT_TEXT);
        map.put("java", INTENT_TEXT);
        map.put("c", INTENT_TEXT);
        map.put("cpp", INTENT_TEXT);
        map.put("py", INTENT_TEXT);
        map.put("xml", INTENT_TEXT);
        map.put("json", INTENT_TEXT);
        map.put("log", INTENT_TEXT);

        // word
        map.put("doc", INTENT_WORD);
        map.put("docx", INTENT_WORD);

        // excel
        map.put("xls", INTENT_EXCEL);
        map.put("xlsx", INTENT_EXCEL);

        // ppt
        map.put("ppt", INTENT_PPT);
        map.put("pptx", INTENT_PPT);

        // pdf
        map.put("pdf", INTENT_PDF);

        // chm
        map.put("chm", INTENT_CHM);

    }

    public static FileIntentUtil instance() {
        if (_instance == null) {
            synchronized (lockObj) {
                if (_instance == null) {
                    _instance = new FileIntentUtil();
                }
            }
        }
        return _instance;
    }

}
