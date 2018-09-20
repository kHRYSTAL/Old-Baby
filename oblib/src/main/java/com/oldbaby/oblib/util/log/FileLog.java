package com.oldbaby.oblib.util.log;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * usage: 日志输出至文件的处理
 * author: kHRYSTAL
 * create time: 17/1/5
 * update time:
 * email: 723526676@qq.com
 */

public class FileLog {

    private static final String FILE_PREFIX = "Zhisland_Log_";
    private static final String FILE_FORMAT = ".log";
    private static SimpleDateFormat dateFormat;

    /**
     * Log 执行输出日志到文件
     * @param tag
     * @param targetDirectory
     * @param headString
     * @param msg
     */
    public static void printFile(String tag, File targetDirectory, String headString, String msg) {
        printFile(tag, targetDirectory, null, headString, msg);
    }

    public static void printFile(String tag, File targetDirectory, String fileName, String headString, String msg) {
        fileName = (fileName == null) ? getFileName() : fileName;
        if (save(targetDirectory, fileName, msg)) {
            Log.d(tag, headString + "save log success ! location is >>>" +
                    targetDirectory.getAbsolutePath() + "/" + fileName);
        } else {
            Log.e(tag, headString + "save log fails !");
        }
    }

    private static boolean save(File dic, @NonNull String fileName, String msg) {
        File file = new File(dic, fileName);

        try {
            OutputStream outputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
            outputStreamWriter.write(msg);
            outputStream.flush();
            outputStream.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 如果不设置文件名 则调用此方法 生成文件名为年月日时分秒 + 随机数.log
     * @return
     */
    private static String getFileName() {
        Random random = new Random();
        long l = System.currentTimeMillis();
        Date date = new Date(l);
        if (dateFormat == null)
            dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        System.out.println(dateFormat.format(date));
        return FILE_PREFIX + dateFormat.format(date) + "_" + random.nextInt(9999) + FILE_FORMAT;
    }

}
