
package net.runningcode.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileLock;

public class StreamUtil {
    public final static int BUFFER_SIZE = 4 * 1024;
	private static final int MAX_COUNT = 32768;

    public static String InputStream2String(InputStream is){
        if (is == null) {
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[BUFFER_SIZE];
        int len = 0;
        int total = 0;
        try {
            while ((len = is.read(buf,0,1)) != -1) {
                byte ch = (byte)buf[0];
                if (ch == '\n') {
                    break;
                }
                baos.write(buf, 0, len);
//                total+=len;
                total++;
//                if(total >=MAX_COUNT)
//                    break;
            }
        } catch (IOException e) {
           L.e("InputStream2String error:"+e);
        }

        String result = null;

        byte[] byteArray = baos.toByteArray();
        int size = MAX_COUNT>byteArray.length?byteArray.length:MAX_COUNT;
		result = new String(byteArray,0,size);

        return result;
    }

    public static void InputStream2File(InputStream ins, File file){
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = ins.read(buffer, 0, BUFFER_SIZE)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            L.e("InputStream2File IOException"+ e);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (Exception e) {
                //ignore
            }
            
            try {
                if (ins != null) {
                    ins.close();
                }
            } catch (Exception e) {
                //ignore
            }
           
        }
    }

    public static void String2InputStream(InputStream ins, String str){
        try {
            if (!TextUtils.isEmpty(str)) {
                ins = new ByteArrayInputStream(str.getBytes("UTF-8"));
            }
        } catch (IOException e) {
            L.e("InputStream2File IOException"+ e);
        }
    }

    public static void File2Stream(InputStream ins, File file){
        try {
            if (file.isFile()) {
                ins = new FileInputStream(file);
            }
        } catch (FileNotFoundException e) {
            L.e("InputStream2File IOException"+ e);
        }
    }

    public static boolean copy(File src, File dest, long size) {
        FileInputStream in = null;
        FileOutputStream out = null;
        FileLock locker = null;

        try {
            size = size < 0L?src.length():size;
            if(!src.getCanonicalFile().equals(dest.getCanonicalFile()) || size < src.length()) {
                in = new FileInputStream(src);
                out = new FileOutputStream(dest, true);
                locker = out.getChannel().lock();
                byte[] e = new byte[8192];
                long pos = 0L;

                do {
                    int len;
                    if((len = in.read(e, 0, (int) Math.min((long)e.length, size - pos))) < 0) {
                        return true;
                    }

                    out.write(e, 0, len);
                    pos += (long)len;
                } while(pos < size);

                return true;
            }
        } catch (Exception var30) {
            Log.e("FileUtils", "Failed copy \"" + src + "\" to \"" + dest + "\"");
            return false;
        } finally {
            try {
                locker.release();
            } catch (Throwable var29) {
                var29.printStackTrace();
                ;
            }

            try {
                in.close();
            } catch (Throwable var28) {
                var28.printStackTrace();
            }

            try {
                out.close();
            } catch (Throwable var27) {
                var27.printStackTrace();
            }

            if(dest.length() == src.length()) {
                dest.setLastModified(src.lastModified());
            }

        }

        return true;
    }

    public static void byte2File(byte[] buf, String filePath)
    {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try
        {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory())
            {
                dir.mkdirs();
            }
            file = new File(filePath);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(buf);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (bos != null)
            {
                try
                {
                    bos.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (fos != null)
            {
                try
                {
                    fos.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
