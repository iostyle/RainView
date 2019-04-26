package com.iostyle.rainview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.Random;

public class FallObject {
    private int initX;
    private int initY;
    private Random random;
    private int parentWidth;//父容器宽度
    private int parentHeight;//父容器高度
    private float objectWidth;//下落物体宽度
    private float objectHeight;//下落物体高度

    private int startTransHeight;//开始透明的高度
    private int transStatusHeight;//透明过程高度

    private float startTransPercent = 0.7f;//开始透明高度屏幕百分比
    private float transStatusPercent = 0.2f;//透明过程屏幕百分比
    private float minScale;
    private float maxScale;

    public int initSpeed;//初始下降速度
    public int initWindLevel;//初始风力等级

    public float presentX;//当前位置X坐标
    public float presentY;//当前位置Y坐标
    public float presentSpeed;//当前下降速度
    private float presentAngle = 0;//当前旋转角度
    private boolean presentWind;//当前风向

    private float angle;//物体下落角度
    private float rotateAngle;//旋转角度

    private Bitmap bitmap;
    public Builder builder;

    private boolean isSpeedRandom;//物体初始下降速度比例是否随机
    private boolean isSizeRandom;//物体初始大小比例是否随机
    private boolean isWindRandom;//物体初始风向和风力大小比例是否随机
    private boolean isWindChange;//物体下落过程中风向和风力是否产生随机变化
    private boolean isRotate;//物体下落过程中是否旋转
    private boolean isTrans;//是否开启透明变化
    private boolean isOnce;//是否只执行一次
    public boolean isGone;//是否执行结束

    private static final int defaultSpeed = 10;//默认下降速度
    private static final int defaultWindLevel = 0;//默认风力等级
    private static final int defaultWindSpeed = 10;//默认单位风速
    private static final float HALF_PI = (float) Math.PI / 2;//π/2

    public FallObject(Builder builder, int parentWidth, int parentHeight) {
        random = new Random();
        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;

        this.builder = builder;
        isSpeedRandom = builder.isSpeedRandom;
        isSizeRandom = builder.isSizeRandom;
        isWindRandom = builder.isWindRandom;
        isWindChange = builder.isWindChange;
        isRotate = builder.isRotate;
        isTrans = builder.isTrans;
        isOnce = builder.isOnce;

        initWindLevel = builder.initWindLevel;
        initSpeed = builder.initSpeed;

        startTransPercent = builder.startTransPercent;
        transStatusPercent = builder.transStatusPercent;
        minScale = builder.minScale;
        maxScale = builder.maxScale;

        startTransHeight = (int) (this.parentHeight * startTransPercent);
        transStatusHeight = (int) (this.parentHeight * transStatusPercent);

        initX = random.nextInt(parentWidth);
        initY = random.nextInt(parentHeight) - parentHeight;
        presentX = initX;
        presentY = initY;

        if (isTrans) {
            vPaint = new Paint();
        }


        randomSpeed();
        randomSize();
        randomWind();
        randomAngle();
    }

    private FallObject(Builder builder) {
        this.builder = builder;
        initSpeed = builder.initSpeed;
        initWindLevel = builder.initWindLevel;
        bitmap = builder.bitmap;

        isSpeedRandom = builder.isSpeedRandom;
        isSizeRandom = builder.isSizeRandom;
        isWindRandom = builder.isWindRandom;
        isWindChange = builder.isWindChange;
        isRotate = builder.isRotate;
    }

    public static final class Builder {
        private int initSpeed;
        private int initWindLevel;
        private Bitmap bitmap;

        private boolean isSpeedRandom;
        private boolean isSizeRandom;
        private boolean isWindRandom;
        private boolean isWindChange;
        private boolean isRotate;
        private boolean isTrans;
        private boolean isOnce;

        private float startTransPercent;
        private float transStatusPercent;
        private float minScale;
        private float maxScale;

        public Builder(Object o) {
            if (o instanceof Drawable) {
                this.bitmap = drawableToBitmap((Drawable) o);
            } else if (o instanceof Bitmap) {
                this.bitmap = (Bitmap) o;
            }
            init();
        }

        public Builder(Bitmap bitmap) {
            this.bitmap = bitmap;
            init();
        }

        public Builder(Drawable drawable) {
            this.bitmap = drawableToBitmap(drawable);
            init();
        }

        public void init() {
            this.initSpeed = defaultSpeed;
            this.initWindLevel = defaultWindLevel;

            this.isSpeedRandom = false;
            this.isSizeRandom = false;
            this.isWindRandom = false;
            this.isWindChange = false;
            this.isRotate = false;
            this.isTrans = false;
            this.isOnce = false;

            this.startTransPercent = 0.7f;
            this.transStatusPercent = 0.2f;
            this.minScale = 0.5f;
            this.maxScale = 1.5f;
        }

        public Builder setOnce(boolean isOnce) {
            this.isOnce = isOnce;
            return this;
        }

        public Builder setRotate(boolean isRotate) {
            this.isRotate = isRotate;
            return this;
        }

        public Builder setTrans(boolean isTrans) {
            this.isTrans = isTrans;
            return this;
        }

        /**
         * 设置透明消失位置
         *
         * @param f1 从屏幕的某个高度开始消失
         * @param f2 消失过程高度
         */
        public Builder setTransHeight(float f1, float f2) {
            if (f1 != 0 && f2 != 0) {
                this.startTransPercent = f1;
                this.transStatusPercent = f2;
            }
            return this;
        }

        /**
         * 设置物体的初始下落速度
         *
         * @param speed
         * @param isRandomSpeed 物体初始下降速度比例是否随机
         * @return
         */
        public Builder setSpeed(int speed, boolean isRandomSpeed) {
            this.initSpeed = speed;
            this.isSpeedRandom = isRandomSpeed;
            return this;
        }

        /**
         * 设置物体大小
         *
         * @return
         */
        public Builder setSize(int w, int h) {
            this.bitmap = changeBitmapSize(this.bitmap, w, h);
            return this;
        }

        /**
         * 设置物体大小随机
         *
         * @param isRandomSize 是否随机
         * @param minScale     最小倍率
         * @param maxScale     最大倍率
         */
        public Builder setRandomSize(boolean isRandomSize, float minScale, float maxScale) {
            this.isSizeRandom = isRandomSize;
            if (minScale > 0 && maxScale > 0 && maxScale > minScale) {
                this.minScale = minScale;
                this.maxScale = maxScale;
            }
            return this;
        }

        /**
         * 设置风力等级、方向以及随机因素
         *
         * @param level        风力等级（绝对值为 5 时效果会比较好），为正时风从左向右吹（物体向X轴正方向偏移），为负时则相反
         * @param isWindRandom 物体初始风向和风力大小比例是否随机
         * @param isWindChange 在物体下落过程中风的风向和风力是否会产生随机变化
         */
        public Builder setWind(int level, boolean isWindRandom, boolean isWindChange) {
            this.initWindLevel = level;
            this.isWindRandom = isWindRandom;
            this.isWindChange = isWindChange;
            return this;
        }

        public FallObject build() {
            return new FallObject(this);
        }
    }

    private Paint vPaint = null;

    /**
     * 绘制物体对象
     *
     * @param canvas
     */
    public void drawObject(Canvas canvas) {
        moveObject();
        if (!isGone) {
            if (!isRotate && !isTrans) {
                canvas.drawBitmap(bitmap, presentX, presentY, null);
            } else {
                canvas.save();
                canvas.translate(presentX + bitmap.getWidth() / 2, presentY + bitmap.getHeight() / 2);
                if (isRotate) {
                    presentAngle += rotateAngle;
                    canvas.rotate(presentAngle);
                }

                if (isTrans && (presentY + objectHeight) > startTransHeight) {
                    if (vPaint == null) {
                        vPaint = new Paint();
                    }
                    vPaint.setStyle(Paint.Style.STROKE);
                    int alpha = (int) ((startTransHeight + transStatusHeight - (presentY + objectHeight)) / (transStatusHeight) * 255);
                    vPaint.setAlpha(alpha);
                } else {
                    if (vPaint != null) {
                        vPaint.setAlpha(255);
                    }
                }

                canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2, -bitmap.getHeight() / 2, vPaint);
                canvas.restore();
            }
        }
    }

    public synchronized boolean isClickAt(float x, float y) {
        float pX = presentX;
        float pY = presentY;
        if (x > pX && y > pY) {
            float w = bitmap.getWidth() + pX;
            float h = bitmap.getHeight() + pY;
            return x < w && y < h;
        }
        return false;
    }

    /**
     * 移动物体对象
     */
    private void moveObject() {
        moveX();
        moveY();
        if (!isTrans) {
            if (presentY > parentHeight || presentX < -bitmap.getWidth() || presentX > parentWidth + bitmap.getWidth()) {
                if (!isOnce)
                    reset();
                else
                    isGone = true;
            }
        } else {
            if ((presentY + objectHeight) > (startTransHeight + transStatusHeight) || presentX < -bitmap.getWidth() || presentX > parentWidth + bitmap.getWidth()) {
                if (!isOnce)
                    reset();
                else
                    isGone = true;
            }
        }
    }

    /**
     * X轴上的移动逻辑
     */
    private void moveX() {
        presentX += defaultWindSpeed * Math.sin(angle);
        if (isWindChange) {
            angle += (float) (random.nextBoolean() ? -1 : 1) * Math.random() * 0.0025;
        }
    }

    /**
     * Y轴上的移动逻辑
     */
    private void moveY() {
        presentY += presentSpeed;
    }

    /**
     * 重置object位置
     */
    private void reset() {
        presentX = random.nextInt(parentWidth);
        presentY = -objectHeight;
        randomSpeed();//记得重置时速度也一起重置，这样效果会好很多
        randomWind();//记得重置一下初始角度，不然雪花会越下越少（因为角度累加会让雪花越下越偏）
        randomAngle();
    }

    /**
     * 随机物体初始下落速度
     */
    private void randomSpeed() {
        if (isSpeedRandom) {
            presentSpeed = (float) ((random.nextInt(3) + 1) * 0.1 + 1) * initSpeed;//这些随机数大家可以按自己的需要进行调整
        } else {
            presentSpeed = initSpeed;
        }
    }

    /**
     * 随机物体初始大小比例
     */
    private void randomSize() {
        if (isSizeRandom) {
            float r = (random.nextInt(11) * 0.1f * maxScale);
            if (r < minScale) r = minScale;
            Log.e("RainView", r + "");
            float rW = r * builder.bitmap.getWidth();
            float rH = r * builder.bitmap.getHeight();
            bitmap = changeBitmapSize(builder.bitmap, (int) rW, (int) rH);
        } else {
            bitmap = builder.bitmap;
        }
        objectWidth = bitmap.getWidth();
        objectHeight = bitmap.getHeight();
    }

    /**
     * 随机风的风向和风力大小比例，即随机物体初始下落角度
     */
    private void randomWind() {
        if (isWindRandom) {
            angle = (float) ((random.nextBoolean() ? -1 : 1) * Math.random() * initWindLevel / 50);
        } else {
            angle = (float) initWindLevel / 50;
        }
//        Log.e("SKY", "angle:" + angle);

        //限制angle的最大最小值
        if (angle > HALF_PI) {
            angle = HALF_PI;
        } else if (angle < -HALF_PI) {
            angle = -HALF_PI;
        }
    }

    private void randomAngle() {
        rotateAngle = (float) (Math.random() + 0.2);
    }

    /**
     * drawable图片资源转bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 改变bitmap的大小
     *
     * @param bitmap 目标bitmap
     * @param newW   目标宽度
     * @param newH   目标高度
     * @return
     */
    public static Bitmap changeBitmapSize(Bitmap bitmap, int newW, int newH) {
        int oldW = bitmap.getWidth();
        int oldH = bitmap.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newW) / oldW;
        float scaleHeight = ((float) newH) / oldH;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, oldW, oldH, matrix, true);
        return bitmap;
    }
}
