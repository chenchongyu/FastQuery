package net.runningcode.utils;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Stack;

/**
 * Created by Administrator on 2016/6/21.
 */
public class StringUtils {
    protected static char[] Digit = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public StringUtils() {
    }

    public static byte[] md5(byte[] byteData, int count) {
        byte[] md5 = null;

        try {
            MessageDigest e = MessageDigest.getInstance("MD5");
            e.update(byteData, 0, count);
            md5 = e.digest();
        } catch (NoSuchAlgorithmException var4) {
            var4.printStackTrace();
        }

        return md5;
    }

    public static String md5(String string) {
        byte[] data = string.getBytes();
        byte[] md5 = md5(data, data.length);
        return md5 != null?byte2hex(md5):null;
    }

    public static String byte2hex(byte[] bytearray) {
        int nSize = bytearray.length;
        char[] charHex = new char[nSize * 2];

        for(int i = 0; i < nSize; ++i) {
            byte hm = bytearray[i];
            charHex[i * 2] = Digit[hm >>> 4 & 15];
            charHex[i * 2 + 1] = Digit[hm & 15];
        }

        return new String(charHex);
    }

    public static byte[] hex2byte(String hex) {
        int len = hex.length() / 2;
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();

        for(int i = 0; i < len; ++i) {
            int pos = i * 2;
            result[i] = (byte)(toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }

        return result;
    }

    public static byte toByte(char achar) {
        return achar >= 65?(byte)(achar + 10 - 65):(byte)(achar - 48);
    }

    public static boolean isEmail(String str) {
        String expr = "^[\\w\\-\\.]+@[\\w\\-\\.]+(\\.\\w+)+$";
        return TextUtils.isEmpty(str)?false:str.matches(expr);
    }

    public final static String[] TRADITIONAL={"零","壹","贰","叁","肆","伍","陆","柒","捌","玖","拾"};
    public final static String[] TRADITIONAL_INDEX={"仟","佰","拾"};


    public static String toTrandition(int num) throws Exception{
        StringBuilder sb = new StringBuilder();
        Stack stack = new Stack();
        int i=0;
        while(num > 10000){
            stack.push(num%10000);
            num /= 10000;
            i++;
        }

        sb.append(getStrBlowW(num));
        while(!stack.isEmpty()){
            if(i == 2){
                sb.append("亿");
                if(((int)stack.peek())< 10000000){
                    sb.append("零");
                }
            }
            if(i == 1){
                sb.append("万");
                if(((int)stack.peek())< 1000){
                    sb.append("零");
                }
            }
            System.out.print(stack.peek()+"->");
            sb.append(getStrBlowW((int) stack.pop()));
            i--;
        }

        return sb.toString();

    }

    private static String getStrBlowW(int num) throws Exception {
        if(num == 10000)
            return "壹万";
        StringBuilder sb = new StringBuilder();
        int rate = 1000;
        if(num > 10000){
            throw new Exception("");
        }
        int i = 0;
        while(num > 10){
            if(num/rate>0){
                sb.append(TRADITIONAL[num/rate]).append(TRADITIONAL_INDEX[i]);
            }
            num%=rate;
            rate/=10;
            i++;
            //整除情况
            if(num == 0)
                break;
            if(num<rate && i<TRADITIONAL_INDEX.length && !sb.toString().equals("")){
                //中间出现0的情况 ； !sb.equals("")的判断是防止出现0开头的情况
                sb.append("零");
                rate/=10;
                i++;
            }


        }

        if(num>0)
            sb.append(TRADITIONAL[num]);
        System.out.println("getStrBlowW:"+sb);
        return sb.toString();
    }
}
