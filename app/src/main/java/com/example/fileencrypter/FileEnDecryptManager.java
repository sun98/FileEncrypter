package com.example.fileencrypter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Nibius at 2018/6/7 20:24.
 */
public class FileEnDecryptManager {
    public static String key = "wenchen"; // 加密解密key
    private String prefKey = "encrypted_files";
    private Context context;

    FileEnDecryptManager(Context context) {
        this.context = context;
    }

    /**
     * 加密入口
     *
     * @param fileUrl 文件绝对路径
     * @return
     */
    public boolean doEncrypt(String fileUrl) {
        if (isDecrypted(fileUrl)) {
            if (encrypt(fileUrl)) {
                // 加密文件，同时在SharedPreference设置该文件已被加密过
                SharedPreferences encrypted_files = context.getSharedPreferences(prefKey, MODE_PRIVATE);
                SharedPreferences.Editor editor = encrypted_files.edit();
                editor.putBoolean(fileUrl, false);
                editor.apply();Toast.makeText(context, "加密成功！", Toast.LENGTH_LONG).show();
                return true;
            } else {
                Toast.makeText(context, "加密失败！", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        Toast.makeText(context, "文件已被加密过！", Toast.LENGTH_LONG).show();
        return false;
    }

    private final int REVERSE_LENGTH = 28; // 加解密长度(Encryption length)

    /**
     * 加密或解密
     *
     * @param strFile 源文件绝对路径
     * @return
     */
    private boolean encrypt(String strFile) {
        int len = REVERSE_LENGTH;
        try {
            File f = new File(strFile);
            if (f.exists()) {
                RandomAccessFile raf = new RandomAccessFile(f, "rw");
                long totalLen = raf.length();

                if (totalLen < REVERSE_LENGTH)
                    len = (int) totalLen;

                FileChannel channel = raf.getChannel();
                MappedByteBuffer buffer = channel.map(
                        FileChannel.MapMode.READ_WRITE, 0, REVERSE_LENGTH);
                byte tmp;
                for (int i = 0; i < len; ++i) {
                    byte rawByte = buffer.get(i);
                    if (i <= key.length() - 1) {
                        tmp = (byte) (rawByte ^ key.charAt(i)); // 异或运算(XOR operation)
                    } else {
                        tmp = (byte) (rawByte ^ i);
                    }
                    buffer.put(i, tmp);
                }
                buffer.force();
                buffer.clear();
                channel.close();
                raf.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 解密入口
     *
     * @param fileUrl 源文件绝对路径
     */
    public void doDecrypt(String fileUrl) {
        try {
            if (!isDecrypted(fileUrl)) {
//                如果没有被解密，则解密
                decrypt(fileUrl);
                Toast.makeText(context, "解密成功！", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "文件未被加密过！", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void decrypt(String fileUrl) {
        if (encrypt(fileUrl)) {
            // 在SharedPreference里设置这个文件已经被解密
            SharedPreferences.Editor editor = context.getSharedPreferences(prefKey, MODE_PRIVATE).edit();
            editor.putBoolean(fileUrl, true);
            editor.apply();
        }
    }

    /**
     * fileName 文件是否已经解密
     *
     * @param filePath
     * @return
     */
    private boolean isDecrypted(String filePath) {
        SharedPreferences encrypted_files = context.getSharedPreferences(prefKey, MODE_PRIVATE);
        Log.i("nib", "isDecrypted: " + filePath + encrypted_files.getBoolean(filePath, true));
        return encrypted_files.getBoolean(filePath, true);
    }
}
