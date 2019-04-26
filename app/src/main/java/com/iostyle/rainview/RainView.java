package com.iostyle.rainview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.iostyle.rainview.listener.ClickListener;
import com.iostyle.rainview.listener.FinishListener;

import java.util.ArrayList;
import java.util.List;

public class RainView extends View {

    private static final String TAG = "RainView";

    private List<FallObject> fallObjects;
    private FinishListener finishListener;

    private int viewWidth;
    private int viewHeight;
    private int itemWidth;
    private int itemHeight;

    private int intervalTime = 20;//重绘间隔时间
    private int maxNum;//同屏下落最大数量

    private boolean isPowerMode = true;
    private boolean isDebug;
    private boolean isAutoPlay;
    private boolean isRandomSize;

    private Drawable drawable;
    private Bitmap bitmap;

    private int speed;
    private int wind;
    private boolean isRandomSpeed;
    private boolean isRandomWind;
    private boolean isChangeWind;
    private boolean isOnce;
    private boolean isTrans;
    private boolean isRotate;
    private float startTransPercent = 0;
    private float transStatusPercent = 0;
    private float minScale;
    private float maxScale;

    public RainView(Context context) {
        this(context, null);
    }

    public RainView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RainView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        fallObjects = new ArrayList<>();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RainView);
        isAutoPlay = typedArray.getBoolean(R.styleable.RainView_auto_play, true);
        maxNum = typedArray.getInteger(R.styleable.RainView_max_num, 10);
        isRandomSize = typedArray.getBoolean(R.styleable.RainView_random_size, false);
        itemWidth = (int) typedArray.getDimension(R.styleable.RainView_item_width, 50);
        itemHeight = (int) typedArray.getDimension(R.styleable.RainView_item_height, 50);
        drawable = typedArray.getDrawable(R.styleable.RainView_item_drawable);
        speed = typedArray.getInteger(R.styleable.RainView_speed, 5);
        wind = typedArray.getInteger(R.styleable.RainView_wind, 5);
        isRandomSpeed = typedArray.getBoolean(R.styleable.RainView_random_speed, false);
        isRandomWind = typedArray.getBoolean(R.styleable.RainView_random_wind, false);
        isChangeWind = typedArray.getBoolean(R.styleable.RainView_change_wind, false);
        isOnce = typedArray.getBoolean(R.styleable.RainView_once, false);
        isTrans = typedArray.getBoolean(R.styleable.RainView_trans, false);
        isRotate = typedArray.getBoolean(R.styleable.RainView_rotate, false);

        if (isAutoPlay && drawable != null) play(drawable);
    }

    public void setOnce(boolean once) {
        isOnce = once;
    }

    public void setTrans(boolean trans, float f1, float f2) {
        isTrans = trans;
        startTransPercent = f1;
        transStatusPercent = f2;
    }

    public void setRandomSize(boolean isRandomSize, float f1, float f2) {
        this.isRandomSize = isRandomSize;
        minScale = f1;
        maxScale = f2;
    }

    public void setRotate(boolean rotate) {
        isRotate = rotate;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setWind(int wind) {
        this.wind = wind;
    }

    public void setRandomSpeed(boolean randomSpeed) {
        isRandomSpeed = randomSpeed;
    }

    public void setRandomWind(boolean randomWind) {
        isRandomWind = randomWind;
    }

    public void setChangeWind(boolean changeWind) {
        isChangeWind = changeWind;
    }

    public void setItemWidth(int itemWidth) {
        this.itemWidth = itemWidth;
    }

    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    public void setPowerMode(boolean powerMode) {
        isPowerMode = powerMode;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }

    public void setAutoPlay(boolean autoPlay) {
        isAutoPlay = autoPlay;
    }

    public RainView setClickListener(final ClickListener listener) {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        for (FallObject fallObject : fallObjects) {
                            if (!fallObject.isGone) {
                                if (fallObject.isClickAt(event.getX(), event.getY())) {
                                    listener.onClick();
                                    return true;
                                }
                            }
                        }
                        break;
                }
                return false;
            }
        });
        return this;
    }

    public RainView setFinishListener(final FinishListener listener) {
        finishListener = listener;
        return this;
    }


    //供外部加载网络图片
    public RainView setItemBitmap(Bitmap bitmap) {
        return setItemBitmap(bitmap, false);
    }

    public RainView setItemBitmap(Bitmap bitmap, boolean isPlay) {
        this.bitmap = bitmap;
        if (isPlay) play(this.bitmap);
        return this;
    }

    public RainView setItemDrawable(Drawable drawable) {
        return setItemDrawable(drawable, false);
    }

    public RainView setItemDrawable(Drawable drawable, boolean isPlay) {
        this.drawable = drawable;
        if (isPlay) play(this.drawable);
        return this;
    }

    public void play() {
        if (bitmap != null) {
            play(bitmap);
        } else if (drawable != null) {
            play(drawable);
        }
    }

    // ------------------------------------------------------------------------------------

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureSize(600, widthMeasureSpec);
        int height = measureSize(1000, heightMeasureSpec);
        setMeasuredDimension(width, height);

        viewWidth = width;
        viewHeight = height;
    }

    private int measureSize(int defaultSize, int measureSpec) {
        int result = defaultSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(result, specSize);
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (fallObjects.size() > 0) {
            boolean hasVisibile = false;
            for (int i = 0; i < fallObjects.size(); i++) {
                //然后进行绘制
                fallObjects.get(i).drawObject(canvas);
                if (!fallObjects.get(i).isGone) hasVisibile = true;
            }
            if (hasVisibile) {
                // 隔一段时间重绘一次, 动画效果
                if (isPowerMode) getHandler().post(runnable);
                else getHandler().postDelayed(runnable, intervalTime);
            } else {
                clear();
                if (finishListener != null) finishListener.onFinish();
            }
        }
    }

    // 重绘线程
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isDebug) debug();
            invalidate();
        }
    };

    private long lastTime = 0;

    private void debug() {
        long temp = System.currentTimeMillis();
        if (lastTime != 0) {
            Log.e(TAG, "FPS :  " + (1000 / (temp - lastTime)));
        }
        lastTime = temp;
    }

    /**
     * 向View添加下落物体对象
     *
     * @param fallObject 下落物体对象
     * @param num
     */
    public void addFallObject(final FallObject fallObject, final int num) {
        if (checkActivityDestroy(getContext())) {
            return;
        }
        if (viewWidth <= 0) {
            viewWidth = getScreenSize(getContext()).width();
        }
        if (viewHeight <= 0) {
            viewHeight = getScreenSize(getContext()).height();
        }

        for (int i = 0; i < num; i++) {
            FallObject newFallObject = new FallObject(fallObject.builder, viewWidth, viewHeight);

            fallObjects.add(newFallObject);
        }
        invalidate();
    }

    private void play(Object o) {
        clear();
        if (o == null) return;
        FallObject.Builder builder = new FallObject.Builder(o);
        FallObject fallObject = builder
                .setSpeed(speed, isRandomSpeed)
                .setWind(wind, isRandomWind, isChangeWind)
                .setSize(itemWidth, itemHeight)
                .setRandomSize(isRandomSize, minScale, maxScale)
                .setOnce(isOnce)
                .setTrans(isTrans)
                .setRotate(isRotate)
                .setTransHeight(startTransPercent, transStatusPercent)
                .build();
        addFallObject(fallObject, maxNum);
    }

    public void clear() {
        if (fallObjects != null)
            fallObjects.clear();
    }

    public static Rect getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Rect rect = new Rect();
        wm.getDefaultDisplay().getRectSize(rect);
        return rect;
    }

    private boolean checkActivityDestroy(Context context) {
        if (context instanceof Activity) {
            return checkActivityDestroy((Activity) context);
        }
        return true;
    }

    public static boolean checkActivityDestroy(Activity activity) {
        if (activity == null) {
            return true;
        }
        return activity.isFinishing() || activity.isDestroyed();
    }
}
