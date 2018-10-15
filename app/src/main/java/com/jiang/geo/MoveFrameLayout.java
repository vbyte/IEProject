package com.jiang.geo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

/**
 * 自定义的移动布局
 */

public class MoveFrameLayout extends FrameLayout implements ValueAnimator.AnimatorUpdateListener {

    // 动画时长
    public static final long ANIM_DURACTION = 500L;

    // 状态常量
    public static final int STATUS_NON_FULL = 0;
    public static final int STATUS_IS_FULL = 1;
    public static final int STATUS_IS_NO_MOVE = 2;
    public static final int STATUS_IS_HIDE = 3;

    // 两种状态的移动距离
    public static float TRANSLATE_Y_NON_FULL = 0;
    public static float TRANSLATE_Y_IS_FULL = 0;

    // 记录最新的触摸事件位置
    private float mLastX;
    private float mLastY;
    // 屏幕高度
    private int screenHeight;
    // 移动监听回调
    private OnMoveListener mOnMoveListener;
    // 默认状态
    public int status = STATUS_NON_FULL;

    // dp 单位转为 px 单位
    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    // 设置回调
    public void setOnMoveListener(OnMoveListener onMoveListener) {
        mOnMoveListener = onMoveListener;
    }

    // 构造器
    public MoveFrameLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public MoveFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MoveFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    // initialise
    private void init() {
        // distance between top and bottom
        TRANSLATE_Y_IS_FULL = 0.f;
        TRANSLATE_Y_NON_FULL = dp2px(getContext(), 354);
        // get height
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        screenHeight = display.getHeight();
        //
        post(new Runnable() {
            @Override
            public void run() {
                if (mOnMoveListener != null) {
                    mOnMoveListener.move(getTranslationY(), Math.max(0, getTranslationY() - TRANSLATE_Y_NON_FULL));
                }
            }
        });
    }

    /**
     * 触摸事件拦截，实现滑动
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 默认不拦截
        boolean intercept = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // remember the orignial
                intercept = false;
                mLastX = ev.getRawX();
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                // move events accoridng to the user
                if (status == STATUS_IS_NO_MOVE) {
                    break;
                }
                // distance between x
                float delX = mLastX - ev.getRawX();
                // distance between y
                float delY = mLastY - ev.getRawY();
                // change the
                mLastX = ev.getRawX();
                mLastY = ev.getRawY();
                //  if y is larger then stop
                if (Math.abs(delX) < Math.abs(delY) && getTranslationY() > 0) {
                    intercept = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                intercept = false;
                break;
        }
        return intercept;
    }

    /**
     * 拦截事件后会执行该方法
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // down event to get the last position
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                // move recording the distance
                float delY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                // set translation to
                setTranslationY(Math.max(0, getTranslationY() + delY));
                // call back
                if (mOnMoveListener != null) {
                    mOnMoveListener.move(getTranslationY(), Math.max(0, getTranslationY() - TRANSLATE_Y_NON_FULL));
                }
                break;
            case MotionEvent.ACTION_UP:
                // up
                float y = getY();
                // not full as the y axis is small than full
                if (y < screenHeight / 2) {
                    status = STATUS_IS_FULL;
                } else {
                    if (y > screenHeight / 4 * 3) {
                        status = STATUS_IS_HIDE;
                    } else {
                        status = STATUS_NON_FULL;
                    }
                }
                // change the animation
                startAnimationToStatus(status);
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    /**
     * 设置状态
     *
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
        // change the
        setTranslationY(status == STATUS_NON_FULL ? TRANSLATE_Y_NON_FULL : TRANSLATE_Y_IS_FULL);
        // call back
        if (status == STATUS_IS_NO_MOVE && mOnMoveListener != null) {
            mOnMoveListener.move(0.f, 0.f);
        }
        // call back
        if (status == STATUS_NON_FULL && mOnMoveListener != null) {
            mOnMoveListener.move(getTranslationY(), Math.max(0, getTranslationY() - TRANSLATE_Y_NON_FULL));
        }
    }

    /**
     * 执行属性动画实现可见变化
     *
     * @param status
     */
    private void startAnimationToStatus(int status) {
        // get anmation
        ViewPropertyAnimator animate = animate();
        // chaneg the full
        if (status == STATUS_IS_HIDE) {
            animate.translationY(getMeasuredHeight());
        }else {
            animate.translationY(status == STATUS_NON_FULL ? TRANSLATE_Y_NON_FULL : TRANSLATE_Y_IS_FULL);
        }
        // duration of animation
        animate.setDuration(ANIM_DURACTION);
        // listener
        animate.setUpdateListener(this);
        // set the animation
        animate.setInterpolator(new AccelerateDecelerateInterpolator());
        // start animation
        animate.start();
    }

    /**
     * 动画更新回调
     *
     * @param animation
     */
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        // listener
        if (mOnMoveListener != null) {
            mOnMoveListener.move(getTranslationY(), Math.max(0, getTranslationY() - TRANSLATE_Y_NON_FULL));
        }
    }

    /**
     * 回调接口定义
     */
    public interface OnMoveListener {
        void move(float translationY, float overSize);
    }
}
