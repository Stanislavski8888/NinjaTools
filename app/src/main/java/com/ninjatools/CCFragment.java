package com.ninjatools;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;


public class CCFragment extends Fragment {
    static TextView itv;

    private String TAG = MainActivity.TAG;

    Button ccControl = null;

    Button changeButton = null;

    Button desButton = null;

    Button fanControl = null;

    private SharedPreferences sharedPreferences;

    Button slipControl = null;

    private void enableService() {
        try {
            FragmentActivity fragmentActivity = getActivity();
            Intent intent = new Intent();
            intent.setAction("android.settings.ACCESSIBILITY_SETTINGS");
            fragmentActivity.startActivity(intent);
        } catch (Exception exception) {
            getActivity().startActivity(new Intent("android.settings.SETTINGS"));
            exception.printStackTrace();
        }
    }

    public static void refreshTextView() {
        String str6;
        StringBuilder stringBuilder3 = new StringBuilder();
        stringBuilder3.append("");
        stringBuilder3.append("服务");
        boolean bool = MainActivity.isServiceStart();
        String str3 = "<font color='#009000'>√</font>";
        if (bool) {
            str6 = "<font color='#009000'>√</font>";
        } else {
            str6 = "<font color='#FF0000'>×</font>";
        }
        stringBuilder3.append(str6);
        String str2 = stringBuilder3.toString();
        StringBuilder stringBuilder5 = new StringBuilder();
        stringBuilder5.append(str2);
        stringBuilder5.append("   ");
        String str5 = stringBuilder5.toString();
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(str5);
        stringBuilder2.append("CC");
        if (CCService.buttonCC != null) {
            str5 = "<font color='#009000'>√</font>";
        } else {
            str5 = "<font color='#FF0000'>×</font>";
        }
        stringBuilder2.append(str5);
        String str1 = stringBuilder2.toString();
        StringBuilder stringBuilder4 = new StringBuilder();
        stringBuilder4.append(str1);
        stringBuilder4.append("   ");
        String str4 = stringBuilder4.toString();
        StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append(str4);
        stringBuilder1.append("反");
        if (CCService.buttonChange != null) {
            str4 = "<font color='#009000'>√</font>";
        } else {
            str4 = "<font color='#FF0000'>×</font>";
        }
        stringBuilder1.append(str4);
        str4 = stringBuilder1.toString();
        stringBuilder1 = new StringBuilder();
        stringBuilder1.append(str4);
        stringBuilder1.append("   ");
        str4 = stringBuilder1.toString();
        stringBuilder1 = new StringBuilder();
        stringBuilder1.append(str4);
        stringBuilder1.append("下滑");
        if (CCService.buttonSlip != null) {
            str4 = str3;
        } else {
            str4 = "<font color='#FF0000'>×</font>";
        }
        stringBuilder1.append(str4);
        str4 = stringBuilder1.toString();
        itv.setText(Html.fromHtml(str4));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_cc, container, false);
        ccControl = view.findViewById(R.id.ccControl);
        slipControl = view.findViewById(R.id.slipControl);
        fanControl = view.findViewById(R.id.fanControl);
        changeButton = view.findViewById(R.id.changeButton);
        desButton = view.findViewById(R.id.desButton);
        itv = view.findViewById(R.id.infoTextView);
        desButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("使用说明");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("本功能需要开启无障碍和悬浮窗，如果没有开启权限会自动跳转。\n");
                stringBuilder.append("CC和下滑可以单独开启，只有点击修改位置才能挪动悬浮按钮。\n");
                stringBuilder.append("修改位置状态下，长按3S弹出点击位置修改，移动十字到希望点击的地方。\n");
                builder.setMessage(stringBuilder.toString());
                builder.show();
            }
        });
        ccControl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (!CCService.isStart()) {
                    enableService();
                    return;
                }
                if (CCService.buttonCC != null) {
                    CCService.ccFlag = false;
                    MainActivity.hideFloatingWindow(CCService.TYPE_CC);
                    refreshTextView();
                    ccControl.setText("开启CC");
                } else {
                    MainActivity.showFloatingWindow(CCService.TYPE_CC);
                    refreshTextView();
                    ccControl.setText("关闭CC");
                }
            }
        });
        slipControl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (!CCService.isStart()) {
                    enableService();
                    return;
                }
                if (CCService.buttonSlip != null) {
                    MainActivity.hideFloatingWindow(CCService.TYPE_SLIP);
                    refreshTextView();
                    slipControl.setText("开启下滑");
                } else {
                    MainActivity.showFloatingWindow(CCService.TYPE_SLIP);
                    refreshTextView();
                    slipControl.setText("关闭下滑");
                }
            }
        });
        fanControl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (!CCService.isStart()) {
                    enableService();
                    return;
                }
                if (CCService.buttonChange != null) {
                    MainActivity.hideFloatingWindow(CCService.TYPE_CHANGE);
                    refreshTextView();
                    fanControl.setText("开启反");
                } else {
                    MainActivity.showFloatingWindow(CCService.TYPE_CHANGE);
                    refreshTextView();
                    fanControl.setText("关闭反");
                }
            }
        });
        changeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (CCService.inChange) {
                    CCService.inChange = false;
                    changeButton.setText("修改位置（会屏蔽功能）");
                } else {
                    CCService.inChange = true;
                    changeButton.setText("保存修改");
                }
            }
        });

        sharedPreferences = getContext().getSharedPreferences("CC_SP", 0);
        Button saveButton = view.findViewById(R.id.saveButton);
        final EditText cc1EditText = view.findViewById(R.id.cc1EditText);
        final EditText cc2EditText = view.findViewById(R.id.cc2EditText);
        final EditText slipCountEditText = view.findViewById(R.id.slipCountEditText);
        final EditText touchDurationEditText = view.findViewById(R.id.touchDurationEditText);
        final EditText ccTimeEditText = view.findViewById(R.id.ccTimeEditText);

        CCService.touchTimeList[0] = sharedPreferences.getInt("cc1", 70);
        CCService.touchTimeList[1] = sharedPreferences.getInt("cc2", 155);
        CCService.slipCount = sharedPreferences.getInt("slipCount", 4);
        CCService.touchDuration = sharedPreferences.getInt("touchDuration", 15);
        CCService.ccTime = sharedPreferences.getInt("ccTime", 15);

        cc1EditText.setText(String.valueOf(CCService.touchTimeList[0]));
        cc2EditText.setText(String.valueOf(CCService.touchTimeList[1]));
        slipCountEditText.setText(String.valueOf(CCService.slipCount));
        touchDurationEditText.setText(String.valueOf(CCService.touchDuration));
        ccTimeEditText.setText(String.valueOf(CCService.ccTime));

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                CCService.touchTimeList[0] = Integer.parseInt(cc1EditText.getText().toString());
                CCService.touchTimeList[1] = Integer.parseInt(cc2EditText.getText().toString());
                CCService.slipCount = Integer.parseInt(slipCountEditText.getText().toString());
                CCService.touchDuration = Integer.parseInt(touchDurationEditText.getText().toString());
                CCService.ccTime = Integer.parseInt(ccTimeEditText.getText().toString());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("cc1", CCService.touchTimeList[0]);
                editor.putInt("cc2", CCService.touchTimeList[1]);
                editor.putInt("slipCount", CCService.slipCount);
                editor.putInt("touchDuration", CCService.touchDuration);
                editor.putInt("ccTime", CCService.ccTime);
                editor.apply();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshTextView();
        if (CCService.buttonCC == null) {
            ccControl.setText("开启CC");
        } else {
            ccControl.setText("关闭CC");
        }
        if (CCService.buttonSlip == null) {
            slipControl.setText("开启下滑");
        } else {
            slipControl.setText("关闭下滑");
        }
        if (!CCService.inChange) {
            changeButton.setText("修改位置（会屏蔽功能）");
        } else {
            changeButton.setText("保存修改");
        }
    }
}
