package com.eeka.mespad.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;

import com.eeka.mespad.R;
import com.eeka.mespad.fragment.MainFragment;
import com.eeka.mespad.fragment.SuspendFragment;

/**
 * Created by Lenovo on 2017/6/12.
 */

public class MainActivity extends BaseActivity {

    private DrawerLayout mDrawerLayout;

    private FragmentManager mFragmentManager;
    private MainFragment mMainFragment;
    private SuspendFragment mSuspendFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_main);

        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        findViewById(R.id.tv_caijian).setOnClickListener(this);
        findViewById(R.id.tv_diaogua).setOnClickListener(this);

    }

    @Override
    protected void initData() {
        super.initData();
        mFragmentManager = getSupportFragmentManager();
        mMainFragment = new MainFragment();
        mSuspendFragment = new SuspendFragment();

        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.add(R.id.layout_content, mMainFragment);
        ft.commit();

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_caijian:
                changeFragment(0);
                mDrawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.tv_diaogua:
                changeFragment(1);
                mDrawerLayout.closeDrawer(Gravity.START);
                break;
        }
    }

    private void changeFragment(int position) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if (position == 0) {
            ft.hide(mSuspendFragment).show(mMainFragment);
        } else if (position == 1) {
            ft.hide(mMainFragment);
            if (mSuspendFragment.isAdded()) {
                ft.show(mSuspendFragment);
            } else {
                ft.add(R.id.layout_content, mSuspendFragment);
            }
        }
        ft.commit();
    }
}
