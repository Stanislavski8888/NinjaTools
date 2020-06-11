package com.ninjatools;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.Toast;

public class CCService extends AccessibilityService {
    public static final int TYPE_CC = 1;

    public static final int TYPE_CHANGE = 3;

    public static final int TYPE_SLIP = 2;

    public static Button buttonCC;

    public static Button buttonChange;

    public static Button buttonPos;

    public static Button buttonSlip;

    public static boolean ccFlag = false;

    public static int ccTime = 15;

    public static final int[] defaultTouchX;

    public static final int[] defaultTouchY;

    public static boolean inChange = false;

    public static CCService mService;

    public static boolean needChangeCC = false;

    public static int slipCount = 4;

    public static int touchDuration = 15;

    public static int touchTimeIndex;

    public static int[] touchTimeList = { 70, 155 };

    private final String TAG = getClass().getName();

    private int ccTouchX;

    private int ccTouchY;

    private WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

    private int screenHeight;

    private int screenWidth;

    private SharedPreferences sharedPreferences;

    private int slipTouchX;

    private int slipTouchY;

    private WindowManager windowManager;

    static  {
        defaultTouchX = new int[] { 0, -800, -650, -600 };
        defaultTouchY = new int[] { 0, -300, 100, -200 };
    }

    public static boolean isStart() {
        boolean bool;
        if (mService != null) {
            bool = true;
        } else {
            bool = false;
        }
        return bool;
    }

    public void changePos(final int type) {
        int i,j,k;
        final int buttonX = 0;
        buttonPos = new Button(getApplicationContext());
        buttonPos.setBackgroundResource(R.drawable.center);
        if (TYPE_CC == type) {
            i = ccTouchX - screenHeight / 2 - 80;
            j = ccTouchY;
            k = screenWidth / 2;
        } else if (TYPE_SLIP == type) {
            i = slipTouchX - screenHeight / 2 - 80;
            j = slipTouchY;
            k = screenWidth / 2;
        } else {
            return;
        }
        final int buttonY = j - k + 80;
        WindowManager.LayoutParams layoutParams1 = layoutParams;
        layoutParams1.x = i;
        layoutParams1.y = j;
        buttonPos.setOnTouchListener(new View.OnTouchListener() {
            private boolean isComplete = false;

            private int touchIndex;

            private int x;

            private int y;

            public boolean onTouch(View param1View, MotionEvent motionEvent) {
                int i = motionEvent.getAction() & 0xFF;
                if (i != 0) {
                    if (i != 1)
                        if (i != 2) {
                            if (i != 3 && (i == 5 || i != 6))
                                return false;
                        } else {
                            if (isComplete)
                                return false;
                            int j = (int)motionEvent.getRawX();
                            int k = (int)motionEvent.getRawY();
                            int m = x;
                            i = y;
                            x = j;
                            y = k;
                            layoutParams.x += j - m;
                            layoutParams.y += k - i;
                            windowManager.updateViewLayout(param1View, layoutParams);
                            return true;
                        }
                    if (isComplete || motionEvent.getActionIndex() != touchIndex)
                        return false;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    i = type;
                    if (1 == i) {
//                        CCService.access$502(cCService, layoutParams.x + screenHeight / 2 + 80);
//                        CCService.access$602(cCService, layoutParams.y + screenWidth / 2 - 80);
                        editor.putInt("ccTouchX", ccTouchX);
                        editor.putInt("ccTouchY", ccTouchY);
                    } else if (2 == i) {
//                        CCService.access$302(cCService, layoutParams.x + screenHeight / 2 + 80);
//                        CCService.access$402(cCService, layoutParams.y + screenWidth / 2 - 80);
                        editor.putInt("slipTouchX", slipTouchX);
                        editor.putInt("slipTouchY", slipTouchY);
                    }
                    editor.apply();
                    isComplete = true;
                    if (buttonPos != null) {
                        windowManager.removeView(buttonPos);
                        buttonPos = null;
                    }
                    return true;
                }
                x = (int)motionEvent.getRawX();
                y = (int)motionEvent.getRawY();
                layoutParams.x = buttonX;
                layoutParams.y = buttonY;
                touchIndex = motionEvent.getActionIndex();
                isComplete = false;
                return true;
            }
        });
        windowManager.addView(buttonPos, layoutParams);
    }

    public void hideFloatingWindow(int type) {
        if (TYPE_CC == type) {
            Button button = buttonCC;
            if (button != null) {
                windowManager.removeView(button);
                buttonCC = null;
            }
        } else if (TYPE_SLIP == type) {
            Button button = buttonSlip;
            if (button != null) {
                windowManager.removeView(button);
                buttonSlip = null;
            }
        } else if (TYPE_CHANGE == type) {
            Button button = buttonChange;
            if (button != null) {
                windowManager.removeView(button);
                buttonChange = null;
            }
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
        mService = null;
    }

    public boolean onKeyEvent(KeyEvent keyEvent) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(keyEvent.getAction());
        Log.v(TAG, stringBuilder.toString());
        return false;
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        mService = this;
        windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        sharedPreferences = getSharedPreferences("CC_SP", 0);
        if (Build.VERSION.SDK_INT >= 26) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        WindowManager.LayoutParams layoutParams1 = layoutParams;
        layoutParams1.format = 1;
        layoutParams1.width = 160;
        layoutParams1.height = 160;
        layoutParams1.x = 0;
        layoutParams1.y = 0;
        layoutParams1.flags = 8389160;
        slipTouchX = sharedPreferences.getInt("slipTouchX", 400);
        slipTouchY = sharedPreferences.getInt("slipTouchY", 900);
        ccTouchX = sharedPreferences.getInt("ccTouchX", 100);
        ccTouchY = sharedPreferences.getInt("ccTouchY", 500);
    }

    public Button showButton(int type, String paramString) {
        Button button;
        if (TYPE_CC == type) {
            button = buttonCC;
        } else if (TYPE_SLIP == type) {
            button = buttonSlip;
        } else if (TYPE_CHANGE == type) {
            button = buttonChange;
        } else {
            return null;
        }
//        Button button = button;
        if (button == null) {
            button = new Button(getApplicationContext());
            button.setText(paramString);
            button.setBackgroundResource(R.drawable.shape_circle);
            button.setOnTouchListener(new ButtonTouchListener(type));
            WindowManager.LayoutParams layoutParams1 = layoutParams;

            StringBuilder stringBuilder1 = new StringBuilder();
            stringBuilder1.append(type);
            stringBuilder1.append("X");
            layoutParams1.x = sharedPreferences.getInt(stringBuilder1.toString(), defaultTouchX[type]);
            layoutParams1 = layoutParams;

            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(type);
            stringBuilder2.append("Y");
            layoutParams1.y = sharedPreferences.getInt(stringBuilder2.toString(), defaultTouchY[type]);

            windowManager.addView(button, layoutParams);
        }
        return button;
    }

    public void showFloatingWindow(int type) {
        if (Settings.canDrawOverlays(this))
            if (TYPE_CC == type) {
                buttonCC = showButton(1, "CC");
            } else if (TYPE_SLIP == type) {
                buttonSlip = showButton(2, "滑");
            } else if (TYPE_CHANGE == type) {
                buttonChange = showButton(3, "反");
            }
    }

    private class ButtonTouchListener implements View.OnTouchListener {
        private boolean isChanging = false;

        private boolean isComplete = false;

        private boolean isMoving = false;

        private int mType;

        private long startTime;

        private int touchIndex;

        private int x;

        private int y;

        ButtonTouchListener(int param1Int) { mType = param1Int; }

        public boolean onTouch(View param1View, MotionEvent motionEvent) {
            StringBuilder stringBuilder2;
            int i = motionEvent.getActionMasked();
            if (i != 0) {
                if (i != 1)
                    if (i != 2) {
                        if (i != 3 && (i == 5 || i != 6))
                            return true;
                    } else {
                        if (isComplete)
                            return false;
                        if (!inChange)
                            return true;
                        if (!isChanging) {
                            int j = (int)motionEvent.getRawX();
                            int k = (int)motionEvent.getRawY();
                            i = j - x;
                            int m = k - y;
                            if (isMoving) {
                                x = j;
                                y = k;
                                layoutParams.x += i;
                                layoutParams.y += m;
                                windowManager.updateViewLayout(param1View, layoutParams);
                            } else if (i * i + m * m >= 20000) {
                                isMoving = true;
                            }
                        }
                        if (!isChanging && !isMoving && System.currentTimeMillis() - startTime > 2000L) {
                            if (mType == 3)
                                return true;
                            isChanging = true;
                            Toast.makeText(CCService.this, "isChanging", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                if (isComplete || motionEvent.getActionIndex() != touchIndex)
                    return false;
                if (isChanging) {
                    changePos(mType);
                } else if (isMoving) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(mType);
                    stringBuilder2.append("X");
                    editor.putInt(stringBuilder2.toString(), layoutParams.x);
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(mType);
                    stringBuilder2.append("Y");
                    editor.putInt(stringBuilder2.toString(), layoutParams.y);
                    editor.apply();
                }
                isChanging = false;
                isMoving = false;
                isComplete = true;
                return true;
            }
            x = (int)motionEvent.getRawX();
            y = (int)motionEvent.getRawY();
//            WindowManager.LayoutParams layoutParams = layoutParams;
//            SharedPreferences sharedPreferences1 = sharedPreferences;

            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append(mType);
            stringBuilder3.append("X");
            layoutParams.x = sharedPreferences.getInt(stringBuilder3.toString(), defaultTouchX[mType]);

//            layoutParams = layoutParams;
//            SharedPreferences sharedPreferences2 = sharedPreferences;
            StringBuilder stringBuilder1 = new StringBuilder();
            stringBuilder1.append(mType);
            stringBuilder1.append("Y");
            layoutParams.y = sharedPreferences.getInt(stringBuilder1.toString(), defaultTouchY[mType]);

            startTime = System.currentTimeMillis();
            touchIndex = motionEvent.getActionIndex();
            isComplete = false;
            if (buttonPos != null) {
                windowManager.removeView(buttonPos);
                buttonPos = null;
            }
            if (!inChange) {
                i = mType;
                if (2 == i) {
                    (new Thread(new Runnable() {
                        public void run() {
                            for (byte b = 0; b < slipCount; b++) {
                                Path path = new Path();
                                path.moveTo((slipTouchX - 5) + (float)Math.random() * 10.0F, (slipTouchY - 5) + (float)Math.random() * 10.0F);
                                dispatchGesture((new GestureDescription.Builder()).addStroke(new GestureDescription.StrokeDescription(path, 0L, 1L)).build(), null, null);
                                try {
                                    Thread.sleep(touchDuration);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    })).start();
                } else if (3 == i) {
                    if (ccFlag)
                        needChangeCC = true;
                } else if (1 == i) {
                    if (ccFlag) {
                        ccFlag = false;
                        Toast.makeText(CCService.this, "CC关闭", Toast.LENGTH_SHORT).show();
                    } else {
                        ccFlag = true;
                        touchTimeIndex = 0;
                        ((GradientDrawable)buttonCC.getBackground()).setColor(Color.parseColor("#90ff0000"));
                        (new Thread(new Runnable() {
                            public void run() {
                                final long startTime = System.currentTimeMillis();
                                while (ccFlag) {
                                    if (System.currentTimeMillis() - startTime > (ccTime * 1000)) {
                                        ccFlag = false;
                                        needChangeCC = false;
                                        break;
                                    }
                                    if (needChangeCC) {
                                        if (touchTimeIndex == 0) {
                                            Path path1 = new Path();
                                            path1.moveTo((ccTouchX - 5) + (float)Math.random() * 10.0F, (ccTouchY - 5) + (float)Math.random() * 10.0F);
                                            dispatchGesture((new GestureDescription.Builder()).addStroke(new GestureDescription.StrokeDescription(path1, 0L, 1L)).build(), null, null);
                                        }
                                        try {
                                            Thread.sleep(500);
                                        } catch (InterruptedException interruptedException) {
                                            interruptedException.printStackTrace();
                                        }
                                        touchTimeIndex = 0;
                                        needChangeCC = false;
                                        continue;
                                    }
                                    Path path = new Path();
                                    path.moveTo((ccTouchX - 5) + (float)Math.random() * 10.0F, (ccTouchY - 5) + (float)Math.random() * 10.0F);
                                    dispatchGesture((new GestureDescription.Builder()).addStroke(new GestureDescription.StrokeDescription(path, 0L, 1L)).build(), new AccessibilityService.GestureResultCallback() {
                                        public void onCancelled(GestureDescription param3GestureDescription) {
                                            super.onCancelled(param3GestureDescription);
                                            StringBuilder stringBuilder = new StringBuilder();
                                            stringBuilder.append("cancel wtf");
                                            stringBuilder.append(System.currentTimeMillis() - startTime);
                                            Log.v(TAG, stringBuilder.toString());
                                        }

                                        public void onCompleted(GestureDescription param3GestureDescription) {
                                            super.onCompleted(param3GestureDescription);
                                            touchTimeIndex = (touchTimeIndex + 1) % 2;
                                        }
                                    },null);
                                    try {
                                        Thread.sleep(touchTimeList[touchTimeIndex]);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                buttonCC.post(new Runnable() {
                                    public void run() { ((GradientDrawable)buttonCC.getBackground()).setColor(Color.parseColor("#90ffffff")); }
                                });
                            }
                        })).start();
                        Toast.makeText(CCService.this, "CC开启", Toast.LENGTH_SHORT).show();
                    }
                    CCFragment.refreshTextView();
                }
            }
            return true;
        }
    }
}
