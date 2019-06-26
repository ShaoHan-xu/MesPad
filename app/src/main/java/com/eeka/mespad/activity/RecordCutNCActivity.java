package com.eeka.mespad.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonRecyclerAdapter;
import com.eeka.mespad.adapter.RecyclerViewHolder;
import com.eeka.mespad.bo.PositionInfoBo;
import com.eeka.mespad.bo.RecordNCBo;
import com.eeka.mespad.bo.TailorInfoBo;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.manager.Logger;
import com.eeka.mespad.utils.FileUtil;
import com.eeka.mespad.utils.SmbUtil;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.SystemUtils;
import com.eeka.mespad.utils.UriUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 记录裁剪不良界面
 * Created by Lenovo on 2017/7/19.
 */

public class RecordCutNCActivity extends BaseActivity {

    private static final int REQUEST_TAKEPHOTO = 0;

    private TailorInfoBo mTailorInfo;
    private List<RecordNCBo> mList_badRecord;
    private ItemAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_record_cutnc);

        mTailorInfo = (TailorInfoBo) getIntent().getSerializableExtra("data");
        if (mTailorInfo == null) {
            toast("数据错误");
            return;
        }
        mList_badRecord = (List<RecordNCBo>) getIntent().getSerializableExtra("badList");
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        TextView tv_orderNum = findViewById(R.id.tv_recordBad_orderNum);
        tv_orderNum.setText(mTailorInfo.getSHOP_ORDER_INFOR().getSHOP_ORDER());

        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 4);
        mAdapter = new ItemAdapter(mContext, mList_badRecord, R.layout.gv_item_recordnc, layoutManager);

        RecyclerView recyclerView = findViewById(R.id.gv_recordBad);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        findViewById(R.id.btn_recordBad_save).setOnClickListener(this);
        findViewById(R.id.btn_recordBad_cancel).setOnClickListener(this);

        if (mList_badRecord == null || mList_badRecord.size() == 0) {
            showLoading();
            HttpHelper.getBadList(this);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btn_recordBad_save) {
            StringBuilder sb = new StringBuilder("确认保存所选的不良记录吗？\n");
            final List<RecordNCBo> list = new ArrayList<>();
            for (RecordNCBo item : mList_badRecord) {
                if (item.getQTY() > 0) {
                    list.add(item);
                    sb.append(",").append(item.getDESCRIPTION());
                }
            }
            if (list.size() == 0) {
                toast("请选择要记录的不良代码");
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(sb.toString().replaceFirst(",", ""));
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    save(list);
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } else if (v.getId() == R.id.btn_recordBad_cancel) {
            onBackPressed();
        }
    }

    private void save(List<RecordNCBo> list) {
        showLoading();
        JSONObject json = new JSONObject();
        json.put("RFID", mTailorInfo.getRFID());
        json.put("SFC_BO", mTailorInfo.getSFC_BO());
        json.put("ORDER_TYPE", mTailorInfo.getOrderType());
        json.put("SHOP_ORDER", mTailorInfo.getSHOP_ORDER_INFOR().getSHOP_ORDER());
        json.put("SHOP_ORDER_BO", mTailorInfo.getSHOP_ORDER_INFOR().getSHOP_ORDER_BO());
        PositionInfoBo.RESRINFORBean resource = SpUtil.getResource();
        if (resource != null)
            json.put("RESOURCE_BO", resource.getRESOURCE_BO());
        json.put("NC_CODES", list);
        json.put("OPERATION", mTailorInfo.getOPER_INFOR().get(0).getOPERATION());
        json.put("OPERATION_BO", mTailorInfo.getOPER_INFOR().get(0).getOPERATION_BO());
        HttpHelper.saveBadRecord(json, this);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("badList", (Serializable) mList_badRecord);
        setResult(RESULT_OK, intent);
        finish();
    }

    private class ItemAdapter extends CommonRecyclerAdapter<RecordNCBo> {

        ItemAdapter(Context context, List<RecordNCBo> list, int layoutId, RecyclerView.LayoutManager layoutManager) {
            super(context, list, layoutId, layoutManager);
        }

        @Override
        public void convert(RecyclerViewHolder holder, final RecordNCBo item, final int position) {
            TextView tv_count = holder.getView(R.id.tv_recordNc_count);
            final int[] badCount = {item.getQTY()};
            if (badCount[0] == 0) {
                tv_count.setVisibility(View.GONE);
            } else {
                tv_count.setVisibility(View.VISIBLE);
                tv_count.setText(String.valueOf(badCount[0]));
            }

            TextView tv_type = holder.getView(R.id.tv_recordNc_type);
            tv_type.setText(item.getDESCRIPTION());

            setWidgetClickListener(holder, position, R.id.layout_recordNc_type);
            setWidgetClickListener(holder, position, R.id.btn_recordNc_sub);
        }

        @Override
        public void onClick(View v, int position) {
            super.onClick(v, position);
            RecordNCBo recordNCBo = mList_badRecord.get(position);
            if (v.getId() == R.id.layout_recordNc_type) {
                mCurPosition = position;
                if (recordNCBo.getQTY() == 0) {
                    //第一次则需要拍照
                    takePhoto();
                } else {
                    itemCountAdd();
                }
            } else if (v.getId() == R.id.btn_recordNc_sub) {
                if (recordNCBo.getQTY() > 0) {
                    recordNCBo.setQTY(recordNCBo.getQTY() - 1);
                    notifyItemChanged(position);
                }
            }
        }
    }

    private Uri mUri;
    private String mImgName;

    //拍照
    private void takePhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkPermission(Manifest.permission.CAMERA)) {
            requestPermission(new String[]{Manifest.permission.CAMERA});
            return;
        }
        String dirPath = FileUtil.getImagesFolderPath(mContext) + File.separator;
        mImgName = UUID.randomUUID() + ".jpg";
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
            submitImg(path);
        }
    }

    //上传图片
    private void submitImg(final String path) {
        showLoading();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final boolean flag;
                    if (isEmpty(path)) {
                        String uriStr = mUri.toString();
                        String fileName = uriStr.substring(uriStr.lastIndexOf("/"));
                        flag = SmbUtil.smbPut(mContext.getContentResolver().openInputStream(mUri), fileName);
                    } else {
                        flag = SmbUtil.smbPut(path);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissLoading();
                            if (flag) {
                                itemCountAdd();
                            } else {
                                showErrorDialog("图片上传失败");
                            }
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showErrorDialog("图片上传失败");
                        }
                    });
                }
            }
        }).start();
    }

    private int mCurPosition;

    /**
     * 拍照上传成功后记录+1
     */
    private void itemCountAdd() {
        RecordNCBo recordNCBo = mList_badRecord.get(mCurPosition);
        recordNCBo.setQTY(recordNCBo.getQTY() + 1);
        String s = SpUtil.get(SpUtil.KEY_NCIMG_INFO, null);
        if (!isEmpty(s)) {
            PositionInfoBo.NCImgInfo ncImgInfo = JSON.parseObject(s, PositionInfoBo.NCImgInfo.class);
            String imgLocation = ncImgInfo.getPICTURE_REMOTE().replace("smb", "http").replace("/eeka", "") + File.separator + mImgName;
            recordNCBo.setNC_IMAGE_LOCATION(imgLocation);
        }
        mAdapter.notifyItemChanged(mCurPosition);
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        super.onSuccess(url, resultJSON);
        if (HttpHelper.isSuccess(resultJSON)) {
            if (HttpHelper.getBadList.equals(url)) {
                mList_badRecord = JSON.parseArray(resultJSON.getJSONArray("result").toString(), RecordNCBo.class);
                mAdapter.setData(mList_badRecord);
            } else if (HttpHelper.saveBadRecord.equals(url)) {
                toast("保存成功");
                //保存成功后清除记录并关闭界面
                mList_badRecord.clear();
                onBackPressed();
            }
        }
    }

    public static Intent getIntent(Context context, TailorInfoBo data, List<RecordNCBo> badList) {
        Intent intent = new Intent(context, RecordCutNCActivity.class);
        intent.putExtra("data", data);
        intent.putExtra("badList", (Serializable) badList);
        return intent;
    }
}
