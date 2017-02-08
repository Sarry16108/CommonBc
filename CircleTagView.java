package com.yushi.yunbang.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.yushi.yunbang.R;


/**
 * Created by Yanghj on 2017/2/8.
 */

public class CircleTagView extends View {

    private int mTextColor = Color.WHITE;
    private int mTextSize = 20;
    private int solidColor = Color.GREEN;
    private int mSolidRadius = 20;
    private int ringColor = Color.BLUE;
    private int mRingEdgeColor = Color.LTGRAY;
    private int mRingWidth = 2;
    private int mWidth;
    private int mHeight;
    private String text;

    private int mX;
    private int mY;
    private Shader mGradientShader;

    private Paint mSolidPaint;
    private Paint mRingPaint;
    private Paint mTextPaint;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int getSolidColor() {
        return solidColor;
    }

    public void setSolidColor(int solidColor) {
        this.solidColor = solidColor;
    }

    public int getRingColor() {
        return ringColor;
    }

    public void setRingColor(int ringColor) {
        this.ringColor = ringColor;
    }

    public CircleTagView(Context context) {
        super(context);
        init(context, null);
    }

    public CircleTagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleTagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public CircleTagView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet set) {
        if (null != set) {
            TypedArray typedArray = context.obtainStyledAttributes(set, R.styleable.CircleTagView);
            if (null != typedArray) {
                mTextSize = typedArray.getDimensionPixelOffset(R.styleable.CircleTagView_textSize, 20);
                mTextColor = typedArray.getColor(R.styleable.CircleTagView_textColor, Color.WHITE);
                solidColor = typedArray.getColor(R.styleable.CircleTagView_solidColor, Color.GREEN);
                mSolidRadius = typedArray.getDimensionPixelOffset(R.styleable.CircleTagView_solidRadius, 20);
                ringColor = typedArray.getColor(R.styleable.CircleTagView_ringColor, Color.BLUE);
                mRingWidth = typedArray.getDimensionPixelOffset(R.styleable.CircleTagView_ringWidth, 2);
                text = typedArray.getString(R.styleable.CircleTagView_text);
                mRingEdgeColor = typedArray.getColor(R.styleable.CircleTagView_ringEdgeColor, -1);

                typedArray.recycle();
            }
        }

        mSolidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSolidPaint.setStyle(Paint.Style.FILL);
        mSolidPaint.setColor(solidColor);

        mRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setColor(ringColor);
        mRingPaint.setStrokeWidth(mRingWidth);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int wideMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getMode(heightMeasureSpec);
        int heightMode = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (MeasureSpec.EXACTLY == wideMode || MeasureSpec.EXACTLY == heightMode) {
            mWidth = mHeight = Math.min(width, height);
        } else {
            mWidth = mHeight = 2 * (mSolidRadius + mRingWidth);
        }

        mWidth += getPaddingLeft() + getPaddingRight();
        mHeight += getPaddingTop() + getPaddingBottom();

        //环形重复
        if (-1 != mRingEdgeColor) {
            mGradientShader = new RadialGradient(mWidth/2, mHeight/2, mRingWidth, ringColor, mRingEdgeColor, Shader.TileMode.REPEAT);
            mRingPaint.setShader(mGradientShader);
        }

        //文本范围
        Rect rect = new Rect();
        mTextPaint.getTextBounds(text, 0, 1, rect);
        mX = (mWidth - rect.width()) / 2;
        mY = -rect.top + (mHeight - rect.height()) / 2; //这里涉及到baseline
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(mWidth/2, mHeight/2, mSolidRadius, mSolidPaint);
        canvas.drawCircle(mWidth/2, mHeight/2, mSolidRadius, mRingPaint);
        canvas.drawText(text, mX, mY, mTextPaint);
    }
}
