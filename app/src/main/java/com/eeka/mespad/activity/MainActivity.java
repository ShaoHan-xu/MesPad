package com.eeka.mespad.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eeka.mespad.R;
import com.eeka.mespad.adapter.CommonRecyclerAdapter;
import com.eeka.mespad.adapter.RecyclerViewHolder;
import com.eeka.mespad.bluetoothPrint.BluetoothHelper;
import com.eeka.mespad.bo.CardInfoBo;
import com.eeka.mespad.bo.ContextInfoBo;
import com.eeka.mespad.bo.PositionInfoBo;
import com.eeka.mespad.bo.PushJson;
import com.eeka.mespad.bo.ReworkWarnMsgBo;
import com.eeka.mespad.bo.UserInfoBo;
import com.eeka.mespad.fragment.CutFragment;
import com.eeka.mespad.fragment.QCFragment;
import com.eeka.mespad.fragment.SewFragment;
import com.eeka.mespad.fragment.SuspendFragment;
import com.eeka.mespad.http.HttpHelper;
import com.eeka.mespad.service.MQTTService;
import com.eeka.mespad.utils.NetUtil;
import com.eeka.mespad.utils.SpUtil;
import com.eeka.mespad.utils.SystemUtils;
import com.eeka.mespad.utils.TopicUtil;
import com.eeka.mespad.utils.UnitUtil;
import com.eeka.mespad.view.dialog.ErrorDialog;
import com.eeka.mespad.view.dialog.ReworkWarnMsgDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页
 * Created by Lenovo on 2017/6/12.
 */

public class MainActivity extends NFCActivity {

    private DrawerLayout mDrawerLayout;
    private CutFragment mCutFragment;
    private SuspendFragment mSuspendFragment;
    private SewFragment mSewFragment;
    private QCFragment mQCFragment;

    private LinearLayout mLayout_controlPanel;

    private PositionInfoBo mPositionInfo;

    private TextView mTv_searchType;
    private EditText mEt_orderNum;
    private EditText mEt_position;
    private String mTopic;
    private CardInfoBo mCardInfo;
    private boolean isSearchOrder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_main);

        initView();
        mCardInfo = new CardInfoBo();

        EventBus.getDefault().register(this);
        MQTTService.actionStart(mContext);
        SpUtil.cleanDictionaryData();

        registerReceiver(mConnectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        if (BluetoothHelper.isConnectedScannerDevice()) {
            //如果平板连接着蓝牙输入设备，则在应用启动1秒后拉起使输入框获取焦点，拉起键盘
            //否则扫码输入时第一位会获取不到
            mHandler.sendEmptyMessageDelayed(WHAT_KEYBOARD, 1000);
        }
    }

    private static final int WHAT_KEYBOARD = 0;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHAT_KEYBOARD) {
                mEt_orderNum.requestFocus();
                SystemUtils.showSoftInputFromWindow(mContext);
            }
        }
    };

    /**
     * 网络状态发生变化接收器
     */
    private final BroadcastReceiver mConnectivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (NetUtil.isNetworkAvalible(mContext)) {
                checkResource();
            }
        }
    };

    @Override
    protected void onDestroy() {
        dismissLoading();
        ErrorDialog.dismiss();
        EventBus.getDefault().unregister(this);
        MQTTService.actionStop(mContext);
        unregisterReceiver(mConnectivityReceiver);
        super.onDestroy();
    }

    /**
     * 收到推送消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPushMsgReceive(PushJson push) {
        String type = push.getType();
        if (PushJson.TYPE_LOGIN.equals(type) || PushJson.TYPE_LOGOUT.equals(type)) {
            if (PushJson.TYPE_LOGIN.equals(type)) {
                toast("用户刷卡上岗");
            } else {
                toast("用户刷卡离岗");
            }
            if (checkResource()) {
                showLoading();
                HttpHelper.getPositionLoginUsers(this);
            }
        } else if (PushJson.TYPE_WARNING.equals(type)) {
            String message = push.getMessage();
            if (!isEmpty(message)) {
                List<ReworkWarnMsgBo> data = JSON.parseArray(message, ReworkWarnMsgBo.class);
                new ReworkWarnMsgDialog(mContext, data).show();
            }
        } else if (PushJson.TYPE_FINISH_MAIN.equals(type)) {
            finish();
        } else if (PushJson.TYPE_TOAST.equals(type)) {
            toast(push.getMessage());
        } else if (PushJson.TYPE_ErrDialogDismiss.equals(type)) {
            if (BluetoothHelper.isConnectedScannerDevice()) {
                mHandler.sendEmptyMessage(WHAT_KEYBOARD);
            }
        } else if (PushJson.TYPE_EXIT.equals(type)) {
            finish();
            System.exit(0);
        } else {
            String content = push.getContent();
            if (TopicUtil.TOPIC_SUSPEND.equals(mTopic) && mSuspendFragment.inputRFID(content)) {
                //上裁站刷卡绑定RFID
                return;
            }
            if ((TopicUtil.TOPIC_MANUAL.equals(mTopic) || TopicUtil.TOPIC_SEW.equals(mTopic)) && mSewFragment.inputRFID(content)) {
                //手工段刷卡通过RFID卡号重新上架
                return;
            }
            if (TopicUtil.TOPIC_QC.equals(mTopic) && mQCFragment.inputRFID(content)) {
                //质检段刷卡通过RFID卡号重新上架
                return;
            }
            toast("正在刷新页面");
            mEt_orderNum.setText(content);
            isSearchOrder = true;
            if (checkResource()) {
                searchOrder();
            }
        }
    }

    @Override
    protected void initView() {
        super.initView();
        mDrawerLayout = findViewById(R.id.drawerLayout);
        mLayout_controlPanel = findViewById(R.id.layout_controlPanel);

        findViewById(R.id.tv_caijian).setOnClickListener(this);
        findViewById(R.id.tv_diaogua).setOnClickListener(this);
        findViewById(R.id.tv_sew).setOnClickListener(this);
        findViewById(R.id.tv_sewQC).setOnClickListener(this);
        findViewById(R.id.tv_setting).setOnClickListener(this);

        findViewById(R.id.btn_searchOrder).setOnClickListener(this);
        findViewById(R.id.btn_searchPosition).setOnClickListener(this);

        mTv_searchType = findViewById(R.id.tv_main_searchType);
        mTv_searchType.setOnClickListener(this);
        mEt_position = findViewById(R.id.et_position);
        mEt_position.setText(HttpHelper.getPadIp());
        mEt_orderNum = findViewById(R.id.et_orderNum);
        mEt_orderNum.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    String orderNum = mEt_orderNum.getText().toString();
                    if (!isEmpty(mLastNum)) {
                        mLastNum = orderNum.replaceFirst(mLastNum, "");
                    } else {
                        mLastNum = orderNum;
                    }
                    mEt_orderNum.setText(mLastNum);
                    isSearchOrder = true;
                    if (checkResource())
                        searchOrder();
                    return true;
                }
                return false;
            }
        });
    }

    private String mLastNum;//上次搜索的卡号

    @Override
    protected void initData() {
        super.initData();
        showLoading();
        UserInfoBo loginUser = SpUtil.getLoginUser();
        HttpHelper.login(loginUser.getUSER(), loginUser.getPassword(), this);
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

    private void initButton(List<PositionInfoBo.BUTTONINFORBean> buttons) {
        mLayout_controlPanel.removeAllViews();
        for (PositionInfoBo.BUTTONINFORBean item : buttons) {
            if (item.getBUTTON_ID().equals("BINDING")) {
                continue;
            }
            Button button = (Button) LayoutInflater.from(mContext).inflate(R.layout.layout_button, null);
            button.setOnClickListener(this);
            switch (item.getBUTTON_ID()) {
                case "REWORK_LIST":
                    button.setText("返修工序");
                    button.setId(R.id.btn_reworkList);
                    break;
                case "SORTING_BUTTON":
                    button.setText("分拣扫码");
                    button.setId(R.id.btn_sorting);
                    break;
                case "CHANG_BUTTON":
                    button.setText("换片下线");
                    button.setId(R.id.btn_change);
                    break;
                case "MANUAL_COMPLETE":
                    button.setText("手工完成");
                    button.setId(R.id.btn_manualComplete);
                    break;
                case "MANUAL_START":
                    button.setText("手工开始");
                    button.setId(R.id.btn_manualStart);
                    break;
                case "PRODUCT_ON":
                    button.setText("成衣上架");
                    button.setId(R.id.btn_productOn);
                    break;
                case "PRODUCT_OFF":
                    button.setText("成衣下架");
                    button.setId(R.id.btn_productOff);
                    break;
                case "POCKET_SIZE":
                    button.setText("袋口尺寸");
                    button.setId(R.id.btn_pocketSize);
                    break;
                case "MATERIALRETURN":
                    button.setText("退料");
                    button.setId(R.id.btn_materialReturn);
                    break;
                case "MATERIALFEEDING":
                    button.setText("补料");
                    button.setId(R.id.btn_materialFeeding);
                    break;
                case "JOBLIST":
                    button.setText("查看作业清单");
                    button.setId(R.id.btn_jobList);
                    break;
                case "DATACOLLECT":
                    button.setText("数据记录");
                    button.setId(R.id.btn_dataCollect);
                    break;
                case "RESSTATE":
                    button.setText("设备状态更新");
                    button.setId(R.id.btn_resState);
                    break;
                case "SIGNOFF":
                    button.setText("注销在制品");
                    button.setId(R.id.btn_signOff);
                    break;
                case "VIDEO":
                    button.setText("视频查看");
                    button.setId(R.id.btn_video);
                    break;
                case "LOGIN":
                    button.setText("上岗登录");
                    button.setId(R.id.btn_login);
                    break;
                case "LOGOUT":
                    button.setText("离岗登出");
                    button.setId(R.id.btn_logout);
                    break;
                case "NCRECORD":
                    button.setText("不良录入");
                    button.setId(R.id.btn_NcRecord);
                    break;
                case "QA_NCRECORD":
                    button.setText("QA不良录入");
                    button.setId(R.id.btn_QaNcRecord);
                    break;
                case "START":
                    button.setText("开始");
                    button.setId(R.id.btn_start);
                    break;
                case "BINDING":
                    button.setText("绑定");
                    button.setId(R.id.btn_binding);
                    break;
                case "UNBIND":
                    button.setText("制卡/解绑");
                    button.setId(R.id.btn_unbind);
                    break;
                case "FUSING_METHOD":
                    button.setText("粘朴方式");
                    button.setId(R.id.btn_sticky);
                    break;
                case "QA_TO_QC":
                    button.setText("QA去QC");
                    button.setId(R.id.btn_qaToQc);
                    break;
                case "NOR_TO_QC":
                    button.setText("去质检");
                    button.setId(R.id.btn_gotoQC);
                    break;
                case "NOR_TO_QA":
                    button.setText("去QA");
                    button.setId(R.id.btn_gotoQA);
                    break;
                case "SEWING_MAT_BT":
                    button.setText("缝制退补料申请");
                    button.setId(R.id.btn_returnForSew);
                    break;
                case "AUTO_PICK":
                    button.setText("拣选");
                    button.setId(R.id.btn_autoPicking);
                    break;
                case "GY_PIC":
                    button.setText("工艺图");
                    button.setId(R.id.btn_gyPic);
                    break;
                case "SUB_START":
                    button.setText("绣花开始");
                    button.setId(R.id.btn_subStart);
                    break;
                case "SUB_COMPLETE":
                    button.setText("绣花完成");
                    button.setId(R.id.btn_subComplete);
                    break;
                case "CUT_RECORD":
                    button.setText("裁剪计件");
                    button.setId(R.id.btn_cutRecord);
                    break;
                case "LINE_BUTTON":
                    button.setText("线迹显示");
                    button.setId(R.id.btn_lineColor);
                    break;
                case "CUT_PICTURE":
                    button.setText("裁剪图片");
                    button.setId(R.id.btn_cutBmp);
                    break;
                case "XH_MESSAGE":
                    button.setText("绣花信息");
                    button.setId(R.id.btn_embroiderInfo);
                    break;
                case "PATTERN_MESSAGE":
                    button.setText("显示纸样");
                    button.setId(R.id.btn_pattern);
                    break;
                case "COMPLETE":
                    if (mCutFragment != null)
                        mCutFragment.showCompleteButton();
                    continue;
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            button.setLayoutParams(params);
            mLayout_controlPanel.addView(button);
        }
    }

    /**
     * 设置配置的按钮的状态/设置可点击与不可点击
     */
    public void setButtonState(int buttonId, boolean state) {
        int childCount = mLayout_controlPanel.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = mLayout_controlPanel.getChildAt(i);
            if (buttonId == childAt.getId()) {
                childAt.setEnabled(state);
                break;
            }
        }
    }

    private synchronized void changeFragment() {
        if (mPositionInfo == null) {
            toast("请先获取站位数据");
            return;
        }
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        Fragment fragment = null;
        switch (mTopic) {
            case TopicUtil.TOPIC_PACKING:
                if (mSewFragment == null)
                    mSewFragment = new SewFragment();
                fragment = mSewFragment;
                break;
            case TopicUtil.TOPIC_MANUAL:
                if (mSewFragment == null)
                    mSewFragment = new SewFragment();
                fragment = mSewFragment;
                break;
            case TopicUtil.TOPIC_CUT:
                if (mCutFragment == null)
                    mCutFragment = new CutFragment();
                fragment = mCutFragment;
                break;
            case TopicUtil.TOPIC_SEW:
                if (mSewFragment == null)
                    mSewFragment = new SewFragment();
                fragment = mSewFragment;
                break;
            case TopicUtil.TOPIC_SUSPEND:
                if (mSuspendFragment == null)
                    mSuspendFragment = new SuspendFragment();
                fragment = mSuspendFragment;
                break;
            case TopicUtil.TOPIC_QC:
                if (mQCFragment == null)
                    mQCFragment = new QCFragment();
                fragment = mQCFragment;
                break;
        }
        if (fragment != null) {
            //因为缝制主题公用于手工与包装主题，所以在此传值，用于在fragment内区分
            Bundle bundle = new Bundle();
            bundle.putString("topic", mTopic);
            fragment.setArguments(bundle);
            if (fragment.isAdded()) {
                ft.show(fragment);
            } else {
                ft.add(R.id.layout_content, fragment);
            }
            ft.commitAllowingStateLoss();
        }
    }

    /**
     * 显示搜索类型选择弹框
     */
    private void showSearchTypeWindow() {
        final List<String> list = new ArrayList<>();
        list.add("卡号");
        list.add("工单号");
        final ListPopupWindow ppw = new ListPopupWindow(mContext);
        ppw.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, list));
        ppw.setWidth(UnitUtil.dip2px(mContext, 120));
        ppw.setHeight(ListPopupWindow.WRAP_CONTENT);
        ppw.setAnchorView(mTv_searchType);
        ppw.setModal(true);
        ppw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = list.get(position);
                mTv_searchType.setText(s);
                if ("卡号".equals(s)) {
                    mEt_orderNum.setHint("请输入卡号搜索");
                } else if ("工单号".equals(s)) {
                    mEt_orderNum.setHint("请输入工单号搜索");
                }
                ppw.dismiss();
            }
        });
        ppw.show();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        SystemUtils.hideKeyboard(mContext, v);
        switch (v.getId()) {
            case R.id.tv_main_searchType:
                showSearchTypeWindow();
                break;
            case R.id.tv_setting:
                mDrawerLayout.closeDrawer(Gravity.START);
                startActivity(new Intent(mContext, SettingActivity.class));
                return;
            case R.id.btn_video:
                if (TopicUtil.TOPIC_SEW.equals(mTopic)) {
                    mSewFragment.playVideo();
                } else if (TopicUtil.TOPIC_CUT.equals(mTopic)) {
                    mCutFragment.playVideo();
                } else {
                    toast("该站位无视频");
                }
                return;
            case R.id.btn_login:
            case R.id.tv_startWorkAlert:
                showLoginDialog();
                return;
            case R.id.btn_searchOrder:
                isSearchOrder = true;
                if (checkResource())
                    searchOrder();
                return;
            case R.id.btn_searchPosition:
                String position1 = mEt_position.getText().toString();
                if (!isEmpty(position1)) {
//                    HttpHelper.PAD_IP = position1;
                } else {
                    toast("请输入完整的站位");
                }
                showLoading();
                HttpHelper.initData(this);
                return;
            case R.id.btn_cutBmp:
                List<Integer> bmpRes = new ArrayList<>();
                bmpRes.add(R.drawable.clothingparts1);
                bmpRes.add(R.drawable.clothingparts2);
                bmpRes.add(R.drawable.clothingparts3);
                bmpRes.add(R.drawable.clothingparts4);
                bmpRes.add(R.drawable.clothingparts5);
                bmpRes.add(R.drawable.clothingparts6);
                startActivity(ImageBrowserActivity.getIntent(mContext, bmpRes, true));
                return;
            case R.id.btn_embroiderInfo:
                if (TopicUtil.TOPIC_CUT.equals(mTopic)) {
                    startActivity(EmbroiderActivity.getIntent(mContext, null, mCardInfo.getValue(), mTopic));
                } else if (TopicUtil.TOPIC_SEW.equals(mTopic)) {
                    mSewFragment.showEmbroiderInfo();
                }
                return;
        }

        //以下所有按钮需要用户登录后才能操作
        List<UserInfoBo> loginUsers = SpUtil.getPositionUsers();
        if (loginUsers == null || loginUsers.size() == 0) {
            ErrorDialog.showAlert(mContext, "请先登录再操作");
            return;
        }
        switch (v.getId()) {
            case R.id.btn_reworkList:
                if (mSewFragment != null) {
                    mSewFragment.showReworkList();
                }
                break;
            case R.id.btn_sorting:
                if (mSewFragment != null) {
                    mSewFragment.sorting();
                }
                break;
            case R.id.btn_change:
                if (mQCFragment != null) {
                    mQCFragment.change();
                }
                break;
            case R.id.btn_manualStart:
                if (mSewFragment != null) {
                    String searchKey = mEt_orderNum.getText().toString();
                    mSewFragment.manualStart(searchKey);
                }
                break;
            case R.id.btn_manualComplete:
                if (mSewFragment != null) {
                    String searchKey = mEt_orderNum.getText().toString();
                    mSewFragment.manualComplete(searchKey);
                }
                break;
            case R.id.btn_productOn:
                if (TopicUtil.TOPIC_SEW.equals(mTopic)) {
                    if (mSewFragment != null) {
                        mSewFragment.productOn();
                    }
                } else if (TopicUtil.TOPIC_QC.equals(mTopic)) {
                    if (mQCFragment != null) {
                        mQCFragment.productOn();
                    }
                }
                break;
            case R.id.btn_productOff:
                if (TopicUtil.TOPIC_SEW.equals(mTopic)) {
                    if (mSewFragment != null) {
                        mSewFragment.productOff();
                    }
                } else if (TopicUtil.TOPIC_QC.equals(mTopic)) {
                    if (mQCFragment != null) {
                        mQCFragment.productOff();
                    }
                }
                break;
            case R.id.btn_pattern:
                if (mCutFragment != null) {
                    mCutFragment.showPattern();
                }
                break;
            case R.id.btn_pocketSize:
                if (mSewFragment != null) {
                    mSewFragment.showPocketSizeInfo();
                }
                break;
            case R.id.btn_materialReturn:
                if (mCutFragment != null) {
                    mCutFragment.returnAndFeedingMat(true);
                }
                break;
            case R.id.btn_materialFeeding:
                if (mCutFragment != null) {
                    mCutFragment.returnAndFeedingMat(false);
                }
                break;
            case R.id.btn_dataCollect:
                if (TopicUtil.TOPIC_CUT.equals(mTopic))
                    mCutFragment.showRecordLabuDialog();
                break;
            case R.id.btn_jobList:
                startActivity(new Intent(mContext, WorkOrderListActivity.class));
                break;
            case R.id.btn_NcRecord:
                if (TopicUtil.TOPIC_CUT.equals(mTopic)) {
                    mCutFragment.recordNC();
                } else if (TopicUtil.TOPIC_QC.equals(mTopic)) {
                    mQCFragment.recordNC(QCFragment.TYPE_QC);
                }
                break;
            case R.id.btn_QaNcRecord:
                if (TopicUtil.TOPIC_QC.equals(mTopic)) {
                    mQCFragment.recordNC(QCFragment.TYPE_QA);
                }
                break;
            case R.id.btn_logout:
                if (loginUsers.size() != 0) {
                    showLogoutDialog();
                }
                break;
            case R.id.btn_signOff:
                if (isEmpty(mTopic)) {
                    toast("请先获取站位数据");
                    return;
                }
                switch (mTopic) {
                    case TopicUtil.TOPIC_CUT:
                        if (mCutFragment != null) {
                            mCutFragment.signOff();
                        }
                        break;
                }
                break;
            case R.id.btn_start:
                switch (mTopic) {
                    case TopicUtil.TOPIC_CUT:
                        if (mCardInfo != null) {
                            mCutFragment.startWork();
                        }
                        break;
                    case TopicUtil.TOPIC_SEW:
                        break;
                }
                break;
            case R.id.btn_binding:
                if (mSuspendFragment != null) {
                    mSuspendFragment.binding();
                }
                break;
            case R.id.btn_unbind:
                if (TopicUtil.TOPIC_SUSPEND.equals(mTopic)) {
                    if (mSuspendFragment != null) {
                        mSuspendFragment.unBind();
                    }
                } else if (TopicUtil.TOPIC_SEW.equals(mTopic)) {
                    if (mSewFragment != null) {
                        mSewFragment.unBind();
                    }
                } else if (TopicUtil.TOPIC_QC.equals(mTopic)) {
                    if (mQCFragment != null) {
                        mQCFragment.unBind();
                    }
                }
                break;
            case R.id.btn_sticky:
                if (mCutFragment != null) {
                    mCutFragment.sticky();
                }
            case R.id.btn_gotoQC:
                if (mSewFragment != null) {
                    mSewFragment.gotoQC();
                }
                break;
            case R.id.btn_gotoQA:
                if (mSewFragment != null) {
                    mSewFragment.gotoQA();
                }
                break;
            case R.id.btn_qaToQc:
                if (mQCFragment != null) {
                    mQCFragment.qaToQc();
                }
                break;
            case R.id.btn_returnForSew:
                if (mSewFragment != null) {
                    mSewFragment.returnOrFeeding();
                }
            case R.id.btn_autoPicking:
                if (mCutFragment != null) {
                    mCutFragment.autoPicking();
                } else if (mSuspendFragment != null) {
                    mSuspendFragment.autoPicking();
                }
                break;
            case R.id.btn_gyPic:
                if (mSewFragment != null) {
                    mSewFragment.lookOutlinePic();
                }
                break;
            case R.id.btn_subStart:
                if (mSewFragment != null) {
                    mSewFragment.subStart();
                }
                break;
            case R.id.btn_subComplete:
                if (mSewFragment != null) {
                    mSewFragment.subComplete();
                }
                break;
            case R.id.btn_cutRecord:
                if (mCutFragment != null) {
                    mCutFragment.recordQty();
                }
                break;
            case R.id.btn_lineColor:
                if (mSewFragment != null) {
                    mSewFragment.showLineColor();
                }
                break;
        }
    }

    /**
     * 检查本地资源，如果已有资源，返回true，否则返回false,并从服务器获取资源
     */
    private boolean checkResource() {
        ContextInfoBo contextInfo = SpUtil.getContextInfo();
        if (contextInfo == null || mPositionInfo == null) {
            showLoading();
            HttpHelper.initData(this);
            return false;
        }
        return true;
    }

    private void searchOrder() {
        String cardNum = mEt_orderNum.getText().toString();
        if (isEmpty(cardNum)) {
            toast("请输入RFID卡号");
        } else {
            mCardInfo.setCardNum(cardNum);
            switch (mTopic) {
                //三个主题共用一个界面
                case TopicUtil.TOPIC_PACKING:
                case TopicUtil.TOPIC_MANUAL:
                case TopicUtil.TOPIC_SEW:
                    mSewFragment.searchOrder(cardNum, true);
                    break;
                case TopicUtil.TOPIC_CUT:
                    String searchType = mTv_searchType.getText().toString();
                    if ("工单号".equals(searchType)) {
                        mCutFragment.searchOrderByOrderNum(cardNum);
                    } else {
                        getCardInfo(cardNum);
                    }
                    break;
                case TopicUtil.TOPIC_SUSPEND:
                    mSuspendFragment.searchOrder(cardNum);
                    break;
                case TopicUtil.TOPIC_QC:
                    mQCFragment.searchOrder(cardNum);
                    break;
            }
        }
    }

    private Dialog mLogoutDialog;
    private CommonRecyclerAdapter mLogoutAdapter;
    private int mLogoutIndex;

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
            public void convert(RecyclerViewHolder holder, final UserInfoBo item, final int position) {
                holder.setText(R.id.tv_userName, item.getNAME());
                holder.setText(R.id.tv_userNum, item.getEMPLOYEE_NUMBER());
                holder.getView(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showLoading();
                        mLogoutIndex = position;
                        HttpHelper.positionLogout(item.getCARD_NUMBER(), MainActivity.this);
                    }
                });
            }
        };
        listView.setAdapter(mLogoutAdapter);

        mLogoutDialog.setContentView(view);
        mLogoutDialog.show();
        Window window = mLogoutDialog.getWindow();
        window.setLayout((int) (SystemUtils.getScreenWidth(mContext) * 0.5), (int) (SystemUtils.getScreenHeight(mContext) * 0.5));
    }

    @Override
    public void onSuccess(String url, JSONObject resultJSON) {
        dismissLoading();
        if (HttpHelper.isSuccess(resultJSON)) {
            if (HttpHelper.login_url.equals(url)) {
                HttpHelper.initData(this);
            } else if (HttpHelper.queryPositionByPadIp_url.equals(url)) {
                ContextInfoBo contextInfoBo = JSON.parseObject(HttpHelper.getResultStr(resultJSON), ContextInfoBo.class);
                SpUtil.saveContextInfo(contextInfoBo);
                List<UserInfoBo> positionUsers = contextInfoBo.getLOGIN_USER_LIST();
                SpUtil.savePositionUsers(positionUsers);

                HttpHelper.findProcessWithPadId(this);
            } else if (HttpHelper.findProcessWithPadId_url.equals(url)) {
                mPositionInfo = JSON.parseObject(HttpHelper.getResultStr(resultJSON), PositionInfoBo.class);
                SpUtil.save(SpUtil.KEY_RESOURCE, JSON.toJSONString(mPositionInfo.getRESR_INFOR()));
                List<PositionInfoBo.OPERINFORBean> operInfo = mPositionInfo.getOPER_INFOR();
                if (operInfo != null && operInfo.size() != 0) {
                    PositionInfoBo.OPERINFORBean bean = operInfo.get(0);
                    mTopic = bean.getTOPIC();
                }
                if (TopicUtil.TOPIC_CUT.equals(mTopic)) {
                    mTv_searchType.setVisibility(View.VISIBLE);
                } else {
                    mTv_searchType.setVisibility(View.GONE);
                }
                if (TopicUtil.TOPIC_SUBCONTRACT.equals(mTopic)) {
                    startActivity(new Intent(mContext, SubcontractReceiveAty.class));
                    finish();
                    return;
                }
                refreshView(url, resultJSON);

                if (mPositionInfo.getBUTTON_INFOR() != null) {
                    initButton(mPositionInfo.getBUTTON_INFOR());
                }

                if (isSearchOrder) {
                    isSearchOrder = false;
                    searchOrder();
                }
            } else if (HttpHelper.getCardInfo.equals(url)) {
                JSONObject result = resultJSON.getJSONObject("result");
                String orderType = result.getString("ORDER_TYPE");
                mCardInfo.setCardType(orderType);
                mCardInfo.setValue(result.getString("RI"));
                switch (mTopic) {
                    case TopicUtil.TOPIC_CUT:
                        if ("M".equals(orderType)) {
                            //刷的是员工卡，如果是“裁剪计件”在录入记录人则不向下执行
                            if (mCutFragment.inputRecordUser(mCardInfo.getCardNum())) {
                                return;
                            } else {
                                String cardNum = mCardInfo.getCardNum();
                                List<UserInfoBo> users = SpUtil.getPositionUsers();
                                if (users != null && users.size() != 0) {
                                    for (UserInfoBo user : users) {
                                        if (user.getCARD_NUMBER().equals(cardNum)) {
                                            clockOut(cardNum);
                                            break;
                                        }
                                    }
                                }
                                clockIn(cardNum);
                            }
                        } else {
                            mEt_orderNum.setText(mCardInfo.getCardNum());
                            mCutFragment.searchOrder(orderType, mCardInfo.getCardNum(), mPositionInfo.getRESR_INFOR().getRESOURCE_BO(), mCardInfo.getValue());
                        }
                        break;
                }
            } else if (HttpHelper.positionLogin_url.equals(url)) {
                toast("用户上岗成功");
                List<UserInfoBo> positionUsers = JSON.parseArray(resultJSON.getJSONArray("result").toString(), UserInfoBo.class);
                SpUtil.savePositionUsers(positionUsers);
                onClockIn(true);
            } else if (HttpHelper.positionLogout_url.equals(url)) {
                toast("用户下岗成功");
                logoutSuccess();
            } else if (HttpHelper.getPositionLoginUser_url.equals(url)) {
                List<UserInfoBo> positionUsers = JSON.parseArray(resultJSON.getJSONArray("result").toString(), UserInfoBo.class);
                SpUtil.savePositionUsers(positionUsers);
                refreshLoginUser();
            }
        } else {
            String message = resultJSON.getString("message");
            if (HttpHelper.positionLogout_url.equals(url) && !isEmpty(message) && message.contains("用户未在站点登录")) {
                toast("用户下线成功");
                logoutSuccess();
            } else {
                showErrorDialog(message);
            }
        }
    }

    private void refreshView(String url, JSONObject resultJSON) {
        changeFragment();
        switch (mTopic) {
            case TopicUtil.TOPIC_CUT:
                mCutFragment.onSuccess(url, resultJSON);
                break;
            case TopicUtil.TOPIC_SUSPEND:
                mSuspendFragment.onSuccess(url, resultJSON);
                break;
        }
    }

    /**
     * 刷新登录用户表，用户登录/登出时需要调用
     */
    private void refreshLoginUser() {
        switch (mTopic) {
            case TopicUtil.TOPIC_CUT:
                mCutFragment.refreshLoginUsers();
                break;
            case TopicUtil.TOPIC_SEW:
                mSewFragment.refreshLoginUsers();
                break;
            case TopicUtil.TOPIC_SUSPEND:
                mSuspendFragment.refreshLoginUsers();
                break;
            case TopicUtil.TOPIC_QC:
                mQCFragment.refreshLoginUsers();
                break;
        }
    }

    private void logoutSuccess() {
        List<UserInfoBo> loginUsers = SpUtil.getPositionUsers();
        if (loginUsers != null && loginUsers.size() > mLogoutIndex) {
            loginUsers.remove(mLogoutIndex);
            SpUtil.savePositionUsers(loginUsers);
            if (mLogoutAdapter != null) {
                mLogoutAdapter.removeData(mLogoutIndex);
            }
            if (loginUsers.size() == 0) {
                if (mLogoutDialog != null) {
                    mLogoutDialog.dismiss();
                }
                mLogoutDialog = null;
            }
        }
        refreshLoginUser();
    }

    @Override
    public void onClockIn(boolean success) {
        if (success) {
            if (mLoginDialog != null)
                mLoginDialog.dismiss();
            if (mPositionInfo == null) {
                HttpHelper.findProcessWithPadId(this);
            } else {
                refreshLoginUser();
            }
        }
    }

}
