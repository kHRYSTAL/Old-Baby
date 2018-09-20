package com.oldbaby.oblib.util;

import android.app.Activity;
import android.content.ClipDescription;
import android.content.Context;
import android.text.ClipboardManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Various string manipulation methods.
 */
public class StringUtil {

    public static final String SAFE_HOST = "www.zhisland.com";
    public static final String SAFE_SCHEME = "https";

    public static final String LABEL_REGEX = "[^+\\u4E00-\\u9FA5\\w\\s%'·.-]+";
    public static final String LABEL_END_REGEX = "[.。,，（）()、《》]";

    /**
     * 获取字符数，英文占1个中文占2个
     */
    public static int getLength(String str) {
        int valueLength = 0;
        try {
            String chinese = "[\u4e00-\u9fa5]";
            for (int i = 0; i < str.length(); i++) {
                String temp = str.substring(i, i + 1);
                if (temp.matches(chinese)) {
                    valueLength += 2;
                } else {
                    valueLength += 1;
                }
            }
            return valueLength;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 判断字符串为空
     */
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.equals("");
    }

    /**
     * 两个字符串是否相同
     */
    public static boolean isEquals(String s1, String s2) {
        if (StringUtil.isNullOrEmpty(s1)) {
            if (StringUtil.isNullOrEmpty(s2)) {
                return true;
            } else {
                return false;
            }
        } else {
            return s1.equals(s2);
        }
    }

    /**
     * 判断单个字符是否为中文
     */
    public static boolean isChinese(String c) {
        String chinese = "[\u4e00-\u9fa5]";
        return c.matches(chinese);
    }

    /**
     * 判断是否为股票代码
     */
    public static boolean isStockCode(String code) {
        // 股票代码最多6位 超过则不是股票代码
        if (code.length() > 6) {
            return false;
        }
        String stock = "^[a-zA-Z0-9]{0,6}$";
        return code.matches(stock);
    }

    /**
     * 判断单个字符是否为a-z,A-Z,0-9
     */
    public static boolean isAssicChar(String c) {
        String assic = "[a-zA-Z0-9]";
        return c.matches(assic);
    }

    /**
     * 判断单个字符是否为a-z,A-Z,0-9和中国字
     */
    public static boolean isNormalChar(String c) {
        return isChinese(c) || isAssicChar(c);
    }

    /**
     * 从头截取str的count个字符，英文占1个中文占2个
     */
    public static String subString(String str, int count) {
        StringBuffer sb = new StringBuffer();
        if (str != null) {
            int valueLength = 0;
            try {
                String chinese = "[\u4e00-\u9fa5]";
                for (int i = 0; i < str.length(); i++) {
                    String temp = str.substring(i, i + 1);
                    if (temp.matches(chinese)) {
                        valueLength += 2;
                    } else {
                        valueLength += 1;
                    }
                    if (valueLength > count) {
                        break;
                    } else {
                        sb.append(temp);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 是否是有效的url
     */
    public static String validUrl(String url) {
        if (StringUtil.isNullOrEmpty(url))
            return null;

        if (!url.startsWith("http"))
            return url;

        try {
            URI myUri = new URI(url);
            if (myUri.getHost() == null) {
                return null;
            }
            if (myUri.getHost().equals(SAFE_HOST)
                    && myUri.getScheme().equals(SAFE_SCHEME)) {
                String desUrl = "http://www.zhisland.com";
                int startIndex = SAFE_HOST.length() + SAFE_SCHEME.length() + 3; // 3
                if (startIndex < url.length()) {
                    desUrl = desUrl + url.substring(startIndex);
                    desUrl = desUrl.replaceFirst(":443", "");
                }

                return desUrl;
            }
            ;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return url;

    }

    /**
     * 将一个数组中的元素根据制定分隔符拼接成一个字符串
     */
    public static String convertFromArr(Collection<String> arr, String split) {
        if (arr == null || StringUtil.isNullOrEmpty(split)) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (String str : arr) {
            if (!StringUtil.isNullOrEmpty(str)) {
                sb.append(str + split);
            }
        }
        return sb.toString();
    }

    public static String convertFromArr(String[] arr, String split) {
        if (arr == null || StringUtil.isNullOrEmpty(split)) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (String str : arr) {
            if (!StringUtil.isNullOrEmpty(str)) {
                sb.append(str + split);
            }
        }
        return sb.toString();
    }

    public static String converFromList(List<String> strings, String split) {
        if (strings == null || strings.isEmpty() || StringUtil.isNullOrEmpty(split)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < strings.size(); i++) {
            sb.append(strings.get(i));
            if (i < strings.size() - 1) {
                sb.append(split);
            }
        }

        return sb.toString();
    }

    /**
     * 将字符串分解成字符数组
     */
    public static ArrayList<String> split(String str) {
        if (StringUtil.isNullOrEmpty(str))
            return null;
        String[] ss = str.split(",");

        ArrayList<String> arr = new ArrayList<String>();
        for (String s : ss) {
            arr.add(s);
        }
        return arr;
    }

    /**
     * 将字符串分解成字符数组
     */
    public static ArrayList<String> split(String str, String split) {
        ArrayList<String> arr = new ArrayList<String>();
        if (isNullOrEmpty(split)) {
            arr.add(str);
            return arr;
        }
        if (StringUtil.isNullOrEmpty(str))
            return null;
        String[] ss = str.split(split);
        for (String s : ss) {
            arr.add(s);
        }
        return arr;
    }

    /**
     * 判断是否为合法手机号
     */
    public static boolean phoneFormatCheck(String phone) {
        Pattern p = Pattern.compile("^1\\d{10}$");
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    /**
     * 判断电话是否为手机号码。（前提是传入的phone为电话）
     * 以下情况为座机，其余为手机。
     * 长度为12位并且以0开头。
     * 长度为11位并且以0开头
     * 长度小于11位
     */
    public static boolean isMobileNumber(String phone) {
        if (isNullOrEmpty(phone)) {
            return false;
        } else {
            if (phone.length() < 11) {
                return false;
            } else if (phone.length() <= 12 && phone.startsWith("0")) {
                return false;
            }
            return true;
        }
    }

    /**
     * 判断邮箱是否合法
     */
    public static boolean isEmail(String mail) {
        Pattern pattern = Pattern
                .compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
        Matcher matcher = pattern.matcher(mail);
        return matcher.matches();
    }

    /**
     * 复制文本到剪贴板
     */
    public static void copyToClipboard(Context context, String text) {
        ClipboardManager cmb = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(text);
    }

    /**
     * 获取剪切板文字
     */
    public static String getClipText(Context context) {
        android.content.ClipboardManager myClipboard = (android.content.ClipboardManager) context.getSystemService(Activity.CLIPBOARD_SERVICE);
        if (myClipboard.hasPrimaryClip()) {
            if (myClipboard.getPrimaryClipDescription().hasMimeType(
                    ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                CharSequence cs = myClipboard.getPrimaryClip().getItemAt(0).getText();
                if (cs != null) {
                    return cs.toString();
                }
            }
        }
        return null;
    }

    /**
     * Spannable String 设置部分字体前景色
     */
    public static SpannableString setTextForeground(String content, int startIndex, int endIndex, int foregroundColor) {
        if (isNullOrEmpty(content) || startIndex < 0 || endIndex >= content.length() || startIndex >= endIndex) {
            return null;
        }

        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new ForegroundColorSpan(foregroundColor), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    /**
     * 正则替换手机号4-8位 为'*'
     */
    public static String replacePhoneNumber(String number) {
        if (!TextUtils.isEmpty(number) && number.length() == 11) {
            number = number.replaceAll("(?<=[\\d]{3})\\d(?=[\\d]{4})", "*");
        }
        return number;
    }

    /**
     * 去掉中间多余的空格与左右空格
     */
    public static String replaceMoreSpace(String string) {
        if (!TextUtils.isEmpty(string)) {
            string = string.trim();
            String temp = string.replaceAll("\\s{1,}", " ");
            return temp;
        }
        return null;
    }

    /**
     * 校验用户输入名称
     * 去除多余空格后
     * 若字符数不符合最低要求(2个汉字或3个英文字符)
     * 需toast校验提示 返回空字符串
     * 如果校验成功 返回
     *
     * @param userName     用户昵称
     * @param errorContent 字数不符合规范提示
     * @return
     */
    public static String checkName(String userName, String errorContent) {
        String name = replaceMoreSpace(userName);
        if (!TextUtils.isEmpty(name)) {
            if (getLength(userName) < 3) {
                if (!StringUtil.isNullOrEmpty(errorContent)) {
                    ToastUtil.showShort(errorContent);
                }
                return null;
            }
            return name;
        } else
            return null;
    }

    /**
     * 检查标签格式是否正确
     * (中英文数字空格.+-%)
     *
     * @param labelText
     * @return true 正确 false 不正确
     */
    public static boolean checkLabelFormat(String labelText) {
        Pattern pattern = Pattern.compile(LABEL_REGEX);
        Matcher m = pattern.matcher(labelText);
        return !m.find();
    }

    /**
     * 检查标签是否以特殊字符结束
     */
    public static boolean checkLabelEndFormat(String labelText) {
        Pattern pattern = Pattern.compile(LABEL_END_REGEX);
        if (labelText.length() >= 1) {
            Matcher m = pattern.matcher(labelText.substring(labelText.length() - 1, labelText.length()));
            return m.find();
        }
        return false;
    }

    /**
     * 检查是否有特殊字符
     */
    public static boolean checkLabelHasSpecialChar(String labelText) {
        Pattern pattern = Pattern.compile(LABEL_REGEX);
        Matcher m = pattern.matcher(labelText);
        return m.find();
    }

    /**
     * True if the url is a Http url.
     */
    public static boolean isHttpUrl(String url) {
        if (url == null) {
            return false;
        }
        String urlTrim = url.trim();
        boolean isHttp = (urlTrim.length() > 6) && "http://".equalsIgnoreCase(urlTrim.substring(0, 7));
        boolean isHttps = (urlTrim.length() > 7) && "https://".equalsIgnoreCase(urlTrim.substring(0, 8));
        return isHttp || isHttps;
    }

    public static String handleLinuxWinMac(String str) {
        try {
            String result;
            result = Pattern.compile("\r\n").matcher(str).replaceAll("\n");
            result = Pattern.compile("\r").matcher(result).replaceAll("\n");
            return result;
        } catch (Exception e) {
            return str;
        }
    }

    /**
     * 大于99 显示99+
     */
    public static String getRedDotCount(int count) {
        String countStr;
        if (count > 99) {
            countStr = "99+"; //大于99 显示99+
        } else {
            countStr = String.valueOf(count);
        }
        return countStr;
    }

    public static String getDuration(Integer secondDuration) {
        if (secondDuration == null || secondDuration <= 0) {
            return "";
        }
        int hour = secondDuration / 3600;
        int minute = (secondDuration % 3600) / 60;
        int second = secondDuration % 60;

        String hourStr = "";
        String minuteStr = "";
        String secondStr = "";

        if (hour > 0) {
            hourStr = hour + "小时";
        }

        if (minute > 0 || hour > 0) {
            minuteStr = minute + "分";
        }

        if (second > 0 || minute > 0 || hour > 0) {
            secondStr = second + "秒";
        }

        return hourStr + minuteStr + secondStr;
    }

    /**
     * 获取音频时长
     * 格式： xx:xx:xx,如果小于1小时则不显示小时: xx:xx
     */
    public static String getAudioDuration(int duration) {
        if (duration <= 0) {
            duration = 0;
        }
        StringBuilder builder = new StringBuilder("");
        int hour = duration / 3600;
        int minute = (duration % 3600) / 60;
        int second = duration % 60;

        if (hour > 9) {
            builder.append(hour).append(":");
        } else if (hour > 0) {
            builder.append("0").append(hour).append(":");
        }

        if (minute > 9) {
            builder.append(minute).append(":");
        } else {
            builder.append("0").append(minute).append(":");
        }

        if (second > 9) {
            builder.append(second);
        } else {
            builder.append("0").append(second);
        }

        return builder.toString();
    }

    /**
     * 根据一定规则显示的价格string
     *
     * @param price Double 价格
     * @return String price 价格为空或为负数，返回空字符串；小数前两位有值，返回 String.format("%.2f", price)；小数前两位没值，返回整数。
     */
    public static String getPriceStr(Double price) {
        if (price == null || price < -0.009) {
            //如果price不为空，且price为负数，则返回字符串
            return "";
        }
        if (price - price.longValue() > 0.009) {
            //如果price小数前两位有值。
            return String.format("%.2f", price);
        } else {
            return String.valueOf(price.longValue());
        }
    }

    /**
     * 过滤收尾空格与回车换行
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            str = str.trim();
            Pattern p = Pattern.compile("\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
}
