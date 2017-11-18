package edu.xyc.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * 自定义ProgressBar
 */
public class CustomProgressBar extends View {

    /**
     * 完整圆环的背景颜色
     */
    private int mBackgroundColor;

    /**
     * 部分圆环的背景颜色
     */
    private int mProgressColor;

    /**
     * 圆环的宽度
     */
    private float mAnnulusWidth;

    /**
     * 画完整圆环的画笔
     */
    private Paint mWholeAnnulusPaint;

    /**
     * 画部分圆环的画笔
     */
    private Paint mMutilatedAnnulusPaint;

    private RectF mRectF;

    public CustomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        initAttrs(context, attrs);
        initPaint();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomProgressBarStyle, 0, 0);

        mProgressColor = typeArray.getColor(R.styleable.CustomProgressBarStyle_progressColor, 0xFF28C996);
        mBackgroundColor = typeArray.getColor(R.styleable.CustomProgressBarStyle_backgroundColor, 0xFFC9C9C9);
        mAnnulusWidth = typeArray.getDimension(R.styleable.CustomProgressBarStyle_width, 15);

        typeArray.recycle();
    }

    private void initPaint() {

        // 设置画完整圆环的画笔
        mWholeAnnulusPaint = new Paint();
        // 防锯齿
        mWholeAnnulusPaint.setAntiAlias(true);
        // Style.STROKE：空心,Style.FILL：实心
        mWholeAnnulusPaint.setStyle(Paint.Style.STROKE);
        mWholeAnnulusPaint.setColor(mBackgroundColor);
        mWholeAnnulusPaint.setStrokeWidth(mAnnulusWidth);

        // 设置画部分圆环的画笔
        mMutilatedAnnulusPaint = new Paint();
        mMutilatedAnnulusPaint.setAntiAlias(true);
        mMutilatedAnnulusPaint.setStyle(Paint.Style.STROKE);
        mMutilatedAnnulusPaint.setColor(mProgressColor);
        mMutilatedAnnulusPaint.setStrokeWidth(mAnnulusWidth);

        mRectF = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 圆环默认的大小
        int mDefaultSize = 100;

        // 分别获取测量模式 和 测量大小
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);

        // 如果是精确度模式,就按xml中定义的来
        // 如果是最大值模式,就按我们定义的来
        if (widthMode == View.MeasureSpec.AT_MOST && heightMode == View.MeasureSpec.AT_MOST) {
            setMeasuredDimension(mDefaultSize, mDefaultSize);
        } else if (widthMode == View.MeasureSpec.AT_MOST) {
            setMeasuredDimension(mDefaultSize, heightSize);
        } else if (heightMode == View.MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize, mDefaultSize);
        } else {
            setMeasuredDimension(widthSize, heightSize);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int mWidth = getWidth();
        int mHeight = getHeight();

        // 将画布坐标原点移动到中心位置
        canvas.translate(mWidth / 2, mHeight / 2);

        // 弧半径
        float r = (float) (Math.min(mWidth, mHeight) / 2 * 0.8);

        mRectF.left = -r;
        mRectF.top = -r;
        mRectF.right = r;
        mRectF.bottom = r;

        // 画弧的方法
        canvas.drawArc(mRectF, 0, 360, false, mWholeAnnulusPaint);
        canvas.drawArc(mRectF, -90, 120, false, mMutilatedAnnulusPaint);

        setRotationAnim();
    }

    /**
     * 旋转动画
     */
    private void setRotationAnim() {
        // 值为0-360
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 360);
        // 每转一圈的时间是1秒
        valueAnimator.setDuration(1000);
        // 重复无限次
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        // 模式是重新开始
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                CustomProgressBar.this.setRotation(value);
            }
        });
        // 开启旋转动画
        valueAnimator.start();
    }
}
