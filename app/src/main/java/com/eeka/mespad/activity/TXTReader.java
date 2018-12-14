package com.eeka.mespad.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.eeka.mespad.R;
import com.eeka.mespad.utils.SystemUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TXTReader extends Activity {

    private TextView mTv_content;
    private String mFilePath;
    private String mContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_txtreader);

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        EditText et_searchKey = findViewById(R.id.et_searchKey);
        et_searchKey.clearFocus();
        et_searchKey.addTextChangedListener(new TextChangeListener());

        TextView tv_title = findViewById(R.id.tv_title);
        mTv_content = findViewById(R.id.tv_content);
        mTv_content.setMovementMethod(LinkMovementMethod.getInstance());
        mTv_content.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SystemUtils.hideKeyboard(TXTReader.this, v);
                return false;
            }
        });

        Uri data = getIntent().getData();
        mFilePath = data.getPath();
        int index = mFilePath.lastIndexOf(File.separator);
        String name = mFilePath.substring(index + 1, mFilePath.length());
        tv_title.setText(name);

        readContent();
    }

    private class TextChangeListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!TextUtils.isEmpty(mContent)) {
                String key = s.toString();
                if (!TextUtils.isEmpty(key)) {
                    SpannableString spanStr = new SpannableString(mContent);
                    Pattern p = Pattern.compile(key);
                    Matcher m = p.matcher(spanStr);
                    while (m.find()) {
                        int start = m.start();
                        int end = m.end();
                        spanStr.setSpan(new BackgroundColorSpan(Color.RED), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    mTv_content.setText(spanStr);
                } else {
                    mTv_content.setText(mContent);
                }
            }
        }
    }

    private void readContent() {
        try {
            FileInputStream fis = new FileInputStream(mFilePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            String line;
            StringBuilder content = new StringBuilder();
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
            mContent = content.toString();
            mTv_content.setText(mContent);
            br.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void startActivity(Context context, @NonNull String filePath) {
        Intent intent = new Intent(context, TXTReader.class);
        intent.putExtra("filePath", filePath);
        context.startActivity(intent);
    }
}
