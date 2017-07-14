package com.sysu.pro.fade;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sysu.pro.fade.R;
import com.sysu.pro.fade.discover.ContentDiscover;
import com.sysu.pro.fade.home.ContentHome;
import com.sysu.pro.fade.message.ContentMessage;
import com.sysu.pro.fade.my.ContentMy;
import com.sysu.pro.fade.utils.Const;
import com.sysu.pro.fade.view.CustomViewPager;
import com.sysu.pro.fade.view.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private CustomViewPager mViewPager;
    private TabLayout mTabLayoutMenu;
    public Toolbar mToolbar;

    /*
    上次back的时间，用于双击退出判断
    当双击 back 键在此间隔内是直接触发 onBackPressed
     */
    private static long lastBackTime = 0;
    private final int BACK_INTERVAL = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mViewPager = (CustomViewPager) findViewById(R.id.container);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //设置底部导航栏以及监听
        mTabLayoutMenu = (TabLayout) findViewById(R.id.tab_layout_menu);
        bindPagerAndTab();
        setupTabIcon();

    }

    //设置底部导航栏图片
    private void setupTabIcon(){
        Resources res = getResources();
        mTabLayoutMenu.addTab(mTabLayoutMenu.newTab().setCustomView(
                createView(res.getDrawable(R.drawable.scenery_normal), "首页")));
        mTabLayoutMenu.addTab(mTabLayoutMenu.newTab().setCustomView(
                createView(res.getDrawable(R.drawable.community_normal), "发现")));
        mTabLayoutMenu.addTab(mTabLayoutMenu.newTab().setCustomView(
                createView(res.getDrawable(R.drawable.route_normal), "消息")));
        mTabLayoutMenu.addTab(mTabLayoutMenu.newTab().setCustomView(
                createView(res.getDrawable(R.drawable.my_normal), "我的")));
    }

    private View createView(Drawable icon, String tab) {
        View view = getLayoutInflater().inflate(R.layout.tab_layout, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.icon);
        TextView title = (TextView) view.findViewById(R.id.title);
        imageView.setImageDrawable(icon);

        title.setText(tab);
        return view;
    }

    //设置滑动事件
    private void bindPagerAndTab() {
        mTabLayoutMenu.setSelectedTabIndicatorHeight(0);//去除指示器
        mTabLayoutMenu.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            /**
             * 选中tab后触发
             * @param tab 选中的tab
             */
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //与pager 关联
//                mViewPager.setCurrentItem(tab.getPosition(), true);
                changeTabSelect(tab);
            }

            /**
             * 退出选中状态时触发
             * @param tab 退出选中的tab
             */
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                changeTabNormal(tab);
            }

            /**
             * 重复选择时触发
             * @param tab 被 选择的tab
             */
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    //设置选择tab图标
    private void changeTabSelect(TabLayout.Tab tab) {
        Resources res = getResources();
        View view = tab.getCustomView();
        ImageView img_title = (ImageView) view.findViewById(R.id.icon);
        TextView txt_title = (TextView) view.findViewById(R.id.title);
        if (tab.getPosition() == Const.HOME-1) {
            img_title.setImageDrawable(res.getDrawable(R.drawable.scenery_selected));
            mViewPager.setCurrentItem(Const.HOME-1,false);
        } else if (tab.getPosition()==Const.DISCOVER-1) {
            img_title.setImageDrawable(res.getDrawable(R.drawable.community_selected));
            mViewPager.setCurrentItem(Const.DISCOVER-1,false);
        }else if (tab.getPosition() == Const.MESSAGE-1) {
            img_title.setImageDrawable(res.getDrawable(R.drawable.route_selected));
            mViewPager.setCurrentItem(Const.MESSAGE-1,false);
        } else {
            img_title.setImageDrawable(res.getDrawable(R.drawable.my_selected));
            mViewPager.setCurrentItem(Const.MY-1,false);
        }
    }

    //设置还原tab图标
    private void changeTabNormal(TabLayout.Tab tab) {
        Resources res = getResources();
        View view = tab.getCustomView();
        ImageView img_title = (ImageView) view.findViewById(R.id.icon);
        TextView txt_title = (TextView) view.findViewById(R.id.title);
        txt_title.setTextColor(Color.GRAY);
        if (tab.getPosition() == Const.HOME -1) {
            img_title.setImageDrawable(res.getDrawable(R.drawable.scenery_normal));
        }  else if (tab.getPosition() == Const.DISCOVER-1) {
            img_title.setImageDrawable(res.getDrawable(R.drawable.community_normal));
        }else if (tab.getPosition() == Const.MESSAGE-1) {
            img_title.setImageDrawable(res.getDrawable(R.drawable.route_normal));
        }else {
            img_title.setImageDrawable(res.getDrawable(R.drawable.my_normal));
        }
    }


    public static class PlaceHolderFragment extends Fragment{

        //四大模块
        private ContentDiscover contentDiscover = null;
        private ContentHome contentHome = null;
        private ContentMessage contentMessage = null;
        private ContentMy contentMy = null;

        private static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public void onResume() {
            //返回时重新加载数据
            super.onResume();
            if(contentHome != null && getArguments().getInt(ARG_SECTION_NUMBER) == Const.HOME){
                contentHome.loadData();
            }
            if(contentDiscover != null && getArguments().getInt(ARG_SECTION_NUMBER) == Const.DISCOVER){
                contentDiscover.loadData();
            }

            if(contentMessage != null && getArguments().getInt(ARG_SECTION_NUMBER) == Const.MESSAGE){
                contentMessage.loadData();
            }

            if(contentMy != null && getArguments().getInt(ARG_SECTION_NUMBER) == Const.MY){
                contentMy.loadData();
            }


        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        public static PlaceHolderFragment newInstance(int sectionNumber) {
            //用于构建一个fragment实例
            PlaceHolderFragment fragment = new PlaceHolderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView =null;
            switch (getArguments().getInt(ARG_SECTION_NUMBER)){
                case Const.HOME:
                    rootView = inflater.inflate(R.layout.fragment_home,container,false);
                    contentHome = new ContentHome(getActivity(),getContext(),rootView);
                    break;

                case Const.DISCOVER:
                    rootView = inflater.inflate(R.layout.fragment_discover,container,false);
                    contentDiscover = new ContentDiscover(getActivity(),getContext(),rootView);
                    break;

                case Const.MESSAGE:
                    rootView = inflater.inflate(R.layout.fragment_message,container,false);
                    contentMessage = new ContentMessage(getActivity(),getContext(),rootView);
                    break;

                case Const.MY:
                    rootView = inflater.inflate(R.layout.fragment_my,container,false);
                    contentMy = new ContentMy(getActivity(),getContext(),rootView);
                    break;
            }
            return rootView;
        }
    }


    //处理一些界面更新
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBackTime < BACK_INTERVAL) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "双击 back 退出", Toast.LENGTH_SHORT).show();
        }
        lastBackTime = currentTime;
    }
}
