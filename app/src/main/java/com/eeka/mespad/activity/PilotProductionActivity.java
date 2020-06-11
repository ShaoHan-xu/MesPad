package com.eeka.mespad.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonRecyclerAdapter;
import com.eeka.mespad.adapter.RecyclerViewHolder;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.SystemUtils;

import java.util.List;

public class PilotProductionActivity extends BaseActivity {

    private TextView mTv_loginUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_pilotproduction);

        initView();
    }

    private long mLastMillis;

    @Override
    public void onBackPressed() {
        long curMillis = System.currentTimeMillis();
        if (curMillis - mLastMillis <= 2000) {
            finish();
            System.exit(0);
        } else {
            mLastMillis = curMillis;
            toast("再按一次返回键退出应用");
        }
    }

    @Override
    protected void initView() {
        mTv_loginUser = findViewById(R.id.tv_batchCut_loginUser);
        mTv_loginUser.setOnClickListener(this);

        findViewById(R.id.iv_pilotProd_login).setOnClickListener(this);

        refreshLoginUser();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.iv_pilotProd_login:
                showLoginDialog();
                break;
            case R.id.tv_pilotProd_loginUsers:
                showLogoutDialog();
                break;
        }
    }

    @Override
    public void onClockIn(boolean success) {
        if (success) {
            if (mLoginDialog != null)
                mLoginDialog.dismiss();
            refreshLoginUser();
        }
    }

    private Dialog mLogoutDialog;
    private CommonRecyclerAdapter mLogoutAdapter;
    private String mLogoutUserId;//离岗员工ID

    private void showLogoutDialog() {
        mLogoutDialog = new Dialog(mContext);
        mLogoutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dlg_logout, null);
        RecyclerView listView = view.findViewById(R.id.lv_logout);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        listView.setLayoutManager(manager);
        List<UserInfoBo> loginUsers = SpUtil.getPositionUsers();
        mLogoutAdapter = new CommonRecyclerAdapter<UserInfoBo>(mContext, loginUsers, R.layout.item_logout, manager) {
            @Override
            public void convert(RecyclerViewHolder holder, final UserInfoBo item, int position) {
                holder.setText(R.id.tv_userName, item.getNAME());
                holder.setText(R.id.tv_userNum, item.getEMPLOYEE_NUMBER());
                holder.getView(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showLoading();
                        mLogoutUserId = item.getEMPLOYEE_NUMBER();
                        HttpHelper.positionLogout(mLogoutUserId, PilotProductionActivity.this);
                    }
                });
            }
        };
        listView.setAdapter(mLogoutAdapter);

        mLogoutDialog.setContentView(view);
        mLogoutDialog.show();
        Window window = mLogoutDialog.getWindow();
        assert window != null;
        window.setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.5), (int) (SystemUtils.getScreenHeight(mContext) * 0.5));
    }

    private void logoutSuccess() {
        List<UserInfoBo> loginUsers = SpUtil.getPositionUsers();
        if (loginUsers != null) {
            for (int i = 0; i < loginUsers.size(); i++) {
                UserInfoBo user = loginUsers.get(i);
                if (user.getEMPLOYEE_NUMBER().equals(mLogoutUserId)) {
                    loginUsers.remove(user);
                    break;
                }
            }
            SpUtil.savePositionUsers(loginUsers);
        }
        if (mLogoutAdapter != null) {
            List<UserInfoBo> data = mLogoutAdapter.getData();
            for (int i = 0; i < data.size(); i++) {
                UserInfoBo user = data.get(i);
                if (user.getEMPLOYEE_NUMBER().equals(mLogoutUserId)) {
                    mLogoutAdapter.removeData(i);
                    break;
                }
            }
            if (data.size() == 0) {
                if (mLogoutDialog != null) {
                    mLogoutDialog.dismiss();
                }
                mLogoutDialog = null;
            }
        }
        refreshLoginUser();
    }

    private void refreshLoginUser() {
        StringBuilder loginUser = new StringBuilder();
        List<UserInfoBo> loginUsers = SpUtil.getPositionUsers();
        for (UserInfoBo user : loginUsers) {
            loginUser.append(user.getNAME()).append("、");
        }
        String userName = loginUser.toString();
        if (userName.endsWith("、")) {
            userName = userName.substring(0, userName.lastIndexOf("、"));
        }
        mTv_loginUser.setText(userName);
    }

}
