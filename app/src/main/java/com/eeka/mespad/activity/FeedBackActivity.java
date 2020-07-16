package com.eeka.mespad.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonRecyclerAdapter;
import com.eeka.mespad.adapter.RecyclerViewHolder;
import com.eeka.mespad.bo.PositionInfoBo;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.http.HttpCallback;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.manager.Logger;
import com.eeka.mespad.utils.DateUtil;
import com.eeka.mespad.utils.FileUtil;
import com.eeka.mespad.utils.SmbUtil;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.SystemUtils;
import com.eeka.mespad.utils.UriUtil;
import com.eeka.mespad.view.dialog.ErrorDialog;
import com.eeka.mespad.view.dialog.LoadingDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FeedBackActivity extends BaseActivity {

    private EditText mEt_content;
    private JSONObject mData;

    private ImgAdapter mImgAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_feedback);

        String str = getIntent().getStringExtra("data");
        mData = JSON.parseObject(str);

        initView();
    }

    @Override
    protected void initView() {

        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("试产反馈");

        mEt_content = findViewById(R.id.et_content);

        RecyclerView recyclerView_imgList = findViewById(R.id.recyclerView_imgList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView_imgList.setLayoutManager(layoutManager);
        mList_img = new ArrayList<>();
        mImgAdapter = new ImgAdapter(mContext, mList_img, R.layout.item_img_del, layoutManager);
        recyclerView_imgList.setAdapter(mImgAdapter);

        findViewById(R.id.btn_addImg).setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_submit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_submit:
                submitData();
                break;
            case R.id.btn_addImg:
                if (mList_img.size() >= 6) {
                    showAlert("最多上传 6 张照片");
                    return;
                }
                takePhoto();
                break;
        }
    }

    private Uri mUri;
    private static final int REQUEST_TAKEPHOTO = 0;
    private List<ImgObj> mList_img;//本地路径

    //拍照
    private void takePhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkPermission(Manifest.permission.CAMERA)) {
            requestPermission(new String[]{Manifest.permission.CAMERA});
            return;
        }
        String dirPath = FileUtil.getImagesFolderPath(mContext) + File.separator;
        String mImgName = UUID.randomUUID() + ".jpg";
        File file = new File(dirPath, mImgName);
        if (!file.getParentFile().exists()) {
            file.mkdirs();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mUri = FileProvider.getUriForFile(mContext, "com.eeka.mespad.fileProvider", file);
        } else {
            mUri = Uri.fromFile(file);
        }

        SystemUtils.takePhoto(this, mUri, REQUEST_TAKEPHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (allowAllPermission) {
            takePhoto();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_TAKEPHOTO) {
            final String path = UriUtil.getRealPathFromUri(mContext, mUri);
            Logger.d(path);
            if (mList_img == null) {
                mList_img = new ArrayList<>();
            }
            ImgObj imgObj = new ImgObj();
            imgObj.path = path;
            mList_img.add(imgObj);
            mImgAdapter.notifyDataSetChanged(mList_img);
        }
    }

    private static class ImgAdapter extends CommonRecyclerAdapter<ImgObj> {

        public ImgAdapter(Context context, List<ImgObj> list, int layoutId, RecyclerView.LayoutManager layoutManager) {
            super(context, list, layoutId, layoutManager);
        }

        @Override
        public void convert(RecyclerViewHolder holder, ImgObj item, int position) {
            ImageView imageView = holder.getView(R.id.imageView);
            imageView.setImageURI(Uri.fromFile(new File(item.path)));

            setWidgetClickListener(holder, position, R.id.btn_del);
        }

        @Override
        public void onClick(View v, int position) {
            super.onClick(v, position);
            if (v.getId() == R.id.btn_del) {
                removeData(position);
            }
        }
    }

    //上传图片
    private void submitImg(final ImgObj imgObj) {
        showLoading();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final boolean flag;
                    if (isEmpty(imgObj.path)) {
                        String uriStr = mUri.toString();
                        String fileName = uriStr.substring(uriStr.lastIndexOf("/"));
                        flag = SmbUtil.smbPut(mContext.getContentResolver().openInputStream(mUri), fileName);
                    } else {
                        flag = SmbUtil.smbPut(imgObj.path);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissLoading();
                            if (flag) {
                                imgObj.isUploaded = true;
                                String s = SpUtil.get(SpUtil.KEY_NCIMG_INFO, null);
                                if (!isEmpty(s)) {
                                    PositionInfoBo.NCImgInfo ncImgInfo = JSON.parseObject(s, PositionInfoBo.NCImgInfo.class);
                                    String[] split = imgObj.path.split("/");
                                    String imgName = split[split.length - 1];
                                    imgObj.url = ncImgInfo.getPICTURE_REMOTE().replace("smb", "http").replace("/eeka", "") + File.separator + DateUtil.getCurDate().split(" ")[0] + File.separator + imgName;
                                    Logger.d("图片上传成功：" + imgObj.url);
                                }
                                if (checkImgIsAllUploaded()) {
                                    submitData();
                                }
                            } else {
                                showErrorDialog("图片上传失败,请重新提交");
                            }
                        }
                    });

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showErrorDialog("图片上传失败,请重新提交");
                        }
                    });
                }
            }
        }).start();
    }

    private boolean checkImgIsAllUploaded() {
        for (int i = 0; i < mList_img.size(); i++) {
            if (!mList_img.get(i).isUploaded) {
                return false;
            }
        }
        return true;
    }

    private void submitData() {
        String msg = mEt_content.getText().toString();
        if (isEmpty(msg) && mList_img.size() == 0) {
            ErrorDialog.showAlert(mContext, "请输入反馈内容");
            return;
        }
        boolean isAllUploaded = true;
        StringBuilder urls = new StringBuilder();
        for (int i = 0; i < mList_img.size(); i++) {
            ImgObj imgObj = mList_img.get(i);
            if (!imgObj.isUploaded) {
                isAllUploaded = false;
                submitImg(imgObj);
            } else {
                urls.append(imgObj.url);
                if (i != mList_img.size() - 1) {
                    urls.append(",");
                }
            }
        }
        if (!isAllUploaded) {
            return;
        }
        JSONObject json = new JSONObject();
        json.put("item", mData.getString("item"));
        json.put("router", mData.getString("router"));
        json.put("orderNo", mData.getString("orderNo"));
        json.put("orderType", mData.getString("orderType"));
        json.put("feedbackMsg", msg);
        json.put("feedbackURL", urls.toString());
        List<UserInfoBo> positionUsers = SpUtil.getPositionUsers();
        json.put("feedbackUserName", positionUsers.get(0).getNAME());
        json.put("feedbackUserId", positionUsers.get(0).getEMPLOYEE_NUMBER());
        LoadingDialog.show(mContext);
        HttpHelper.trialFeedBack(json, new HttpCallback() {
            @Override
            public void onSuccess(String url, JSONObject resultJSON) {
                LoadingDialog.dismiss();
                if (HttpHelper.isSuccess(resultJSON)) {
                    Toast.makeText(mContext, "反馈提交成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    ErrorDialog.showAlert(mContext, HttpHelper.getMessage(resultJSON));
                }
            }

            @Override
            public void onFailure(String url, int code, String message) {
                LoadingDialog.dismiss();
                ErrorDialog.showAlert(mContext, message);
            }
        });
    }

    public static Intent getIntent(Context context, String data) {
        Intent intent = new Intent(context, FeedBackActivity.class);
        intent.putExtra("data", data);
        return intent;
    }

    static class ImgObj {
        boolean isUploaded;
        String path;
        String url;
    }

}
