package com.zhengsr.viewpagerlib.indicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zhengsr.viewpagerlib.R;
import com.zhengsr.viewpagerlib.bean.RectBean;

/**
 * @author by  zhengshaorui on 2019/10/8
 * Describe: 矩形的 indicator，支持移动功能
 */
public class RectIndicator extends LinearLayout {
    private static final String TAG = "RectIndicator";
    private int mCount;

    /**
     * attrs
     */
    private int mMargin;
    private int mDefaultColor;
    private int mSelectedColor;
    private boolean isCanMove;
    private int mRectWidth;
    private int mRectHeight;
    private int mRoundSize;

    /**
     * logic
     */
    private int mMoveDistance;
    private int mMoveSize;
    private RectF mRect;
    private Paint mPaint;


    public RectIndicator(Context context) {
        this(context,null);
    }

    public RectIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RectIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RectIndicator);

        mMargin = ta.getDimensionPixelSize(R.styleable.RectIndicator_rect_horizon_margin, 20);
        mRectWidth = ta.getDimensionPixelSize(R.styleable.RectIndicator_rect_width, 100);
        mDefaultColor = ta.getColor(R.styleable.RectIndicator_rect_normalColor, Color.GRAY);
        mSelectedColor = ta.getColor(R.styleable.RectIndicator_rect_selectedColor, Color.WHITE);
        isCanMove = ta.getBoolean(R.styleable.RectIndicator_rect_canMove, true);
        mRectHeight = ta.getDimensionPixelSize(R.styleable.RectIndicator_rect_height, 50);
        mRoundSize = ta.getDimensionPixelSize(R.styleable.RectIndicator_rect_round_size,10);
        ta.recycle();

        mRect = new RectF();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mSelectedColor);
    }


    /**
     * 添加数据
     * @param viewPager
     */
    public void addPagerData(int count, ViewPager viewPager) {

        if (count == 0) {
            return;
        }
        mCount = count;
        if (mCount == 0) {
            return;
        }
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setSize(mRectWidth, mRectHeight);
        drawable.setCornerRadius(mRoundSize);
        drawable.setColor(mDefaultColor);
        for (int i = 0; i < mCount; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i == mCount - 1) {
                params.setMargins(mMargin, 0, mMargin, 0);
            } else {
                params.setMargins(mMargin, 0, 0, 0);
            }

            ImageView imageView = new ImageView(getContext());

            imageView.setBackground(drawable);
            imageView.setLayoutParams(params);
            addView(imageView);
        }
        if (viewPager != null) {
            viewPager.addOnPageChangeListener(new PagerListener());
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        ImageView child = (ImageView) getChildAt(0);
        if (child != null) {
            float cl = child.getLeft();
            float ct = child.getTop();
            float cr = cl + child.getMeasuredWidth();
            float cb = ct + child.getMeasuredHeight();
            mRect.set(cl, ct, cr, cb);
            mMoveSize = mMargin + mRectWidth;
        }
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (!mRect.isEmpty()) {
            canvas.save();
            canvas.translate(mMoveDistance, 0);
            canvas.drawRoundRect(mRect, mRoundSize, mRoundSize, mPaint);
            canvas.restore();
        }
    }

    /**
     * 配置动态数据
     * @param bean
     */
    public void addRectBean(RectBean bean) {
        if (bean.isCanMove != isCanMove){
            isCanMove  =bean.isCanMove;
        }

        if (bean.normalColor != -2) {
            mDefaultColor = bean.normalColor;
        }
        if (bean.selectedColor != -2){
            mSelectedColor = bean.selectedColor;
        }

        if (bean.horizonMargin != 0){
            mMargin = bean.horizonMargin;
        }

        if (bean.width != 0){
            mRectWidth = bean.width;
        }

        if (bean.height != 0){
            mRectHeight = bean.height;
        }

        if (bean.roundRadius != 0){
            mRoundSize = bean.roundRadius;
        }

    }

    class PagerListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            /**
             * 由于距离时确定的，所以很好判断移动的渐变
             */
            if (isCanMove) {
                if (position % mCount == (mCount - 1) && positionOffset > 0) {
                    mMoveDistance = 0;
                } else {
                    mMoveDistance = (int) (positionOffset * mMoveSize + position % mCount * mMoveSize);
                }
                invalidate();
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (!isCanMove) {
                /**
                 * 处理不移动的情况
                 */

                position = position % mCount;

                mMoveDistance =   position * mMoveSize;

                invalidate();
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }
}
