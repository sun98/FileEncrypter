package com.example.fileencrypter;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private TextView tBasePath;
    private EditText eFilePath;
    private Button bEn, bDe;
    private String basePath, filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tBasePath = findViewById(R.id.textView);
        basePath = getSDPath();
        if (basePath.equals("检测不到SD卡")) {
            Toast.makeText(getApplicationContext(), basePath, Toast.LENGTH_LONG).show();
        }
        tBasePath.setText(getSDPath());
        eFilePath = findViewById(R.id.edit_text);
        bEn = findViewById(R.id.btnEn);
        bDe = findViewById(R.id.btnDe);
        final FileEnDecryptManager fileEnDecryptManager = new FileEnDecryptManager(getApplicationContext());
        bEn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                加密给定文件
                filePath = basePath + eFilePath.getText();
                Log.i("nib", "onClick: " + filePath);
                fileEnDecryptManager.doEncrypt(filePath);
            }
        });
        bDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                给指定文件解密
                filePath = basePath + eFilePath.getText();
                Log.i("nib", "onClick: " + filePath);
                fileEnDecryptManager.doDecrypt(filePath);
            }
        });
    }

//    得到SD卡根目录路径
    public String getSDPath() {
        File sdDir;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取根目录
            return sdDir.toString() + "/";
        } else {
            return "检测不到SD卡";
        }
    }
}
