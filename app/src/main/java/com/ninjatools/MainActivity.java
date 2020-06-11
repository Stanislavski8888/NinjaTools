package com.ninjatools;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TableLayout;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static String TAG = "ninja must die";

    public static MainActivity mainActivity;

    private List<Fragment> mFragment = new ArrayList();

    private List<String> mTitle = new ArrayList();

    public static void hideFloatingWindow(int paramInt) { CCService.mService.hideFloatingWindow(paramInt); }

    public static boolean isServiceStart() {
        List list = ((ActivityManager)mainActivity.getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(100);
        for (byte b = 0; b < list.size(); b++) {
            if (((ActivityManager.RunningServiceInfo)list.get(b)).service.getClassName().equals("com.tool.ninja.CCService"))
                return true;
        }
        return false;
    }

    public static void showFloatingWindow(int type) {
        if (!Settings.canDrawOverlays(mainActivity)) {
            Toast.makeText(mainActivity, "当前无权限，请授权", Toast.LENGTH_SHORT).show();
            MainActivity mainActivity1 = mainActivity;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("package:");
            stringBuilder.append(mainActivity.getPackageName());
            mainActivity1.startActivityForResult(
                    new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION",
                            Uri.parse(stringBuilder.toString())), 0);
        } else {
            CCService.mService.showFloatingWindow(type);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);
        mTitle.add("I Miss Carry");
        mFragment.add(new CCFragment());
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            public int getCount() { return mFragment.size(); }

            public Fragment getItem(int position) { return mFragment.get(position); }

            public CharSequence getPageTitle(int position) { return mTitle.get(position); }
        });
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
    }
}
