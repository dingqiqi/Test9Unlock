package com.dingqiqi.test9unlock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dingqiqi on 2016/8/29.
 */
public class LockView extends View {
    /**
     * 初始圆半径
     */
    private int mCircleRadius = 13;
    /**
     * 初始圆画笔
     */
    private Paint mPaintCircle;
    /**
     * 选中圆画笔
     */
    private Paint mPaintSelectCircle;
    /**
     * 线画笔
     */
    private Paint mPaintLine;
    /**
     * 画笔画笔
     */
    private int mClrcleColor = Color.BLUE;
    private int mClrcleSelectColor = Color.GREEN;
    private int mLineColor = Color.GREEN;
    /**
     * 存放圆区域
     */
    private List<Rect> mList;
    /**
     * 存放圆心点
     */
    private List<CircleXY> mCircleList;
    /**
     * 存放圆是否选中
     */
    private List<Boolean> mSelectList;
    /**
     * 按下时是否点中某个圆
     */
    private boolean mIsStart = false;
    /**
     * 手指是否抬起
     */
    private boolean mIsUp = false;
    /**
     * 选中的点的路径
     */
    private Path mPath;
    /**
     * 最后选中的一个点的xy轴
     */
    private float mCurX, mCurY;
    /**
     * 手指的位置
     */
    private float mDownX, mDownY;
    /**
     * 用于延时的handler
     */
    private Handler mHandler = new Handler();
    /**
     * 手指是否抬起后图案停留的时间
     */
    private int mDefaultTime = 1000;
    /**
     * 模式
     */
    private Style mMode = Style.SETPWD;
    /**
     * 位置
     */
    private Gravity mGravity = Gravity.BOTTOM;

    /**
     * 保存密码的变量
     */
    private StringBuffer mBuffer;
    /**
     * 密码回调
     */
    private PswListener mListener;

    /**
     * 模式，是验证密码还是设置密码
     */
    public enum Style {
        EQUALPWD,
        SETPWD;
    }

    /**
     * 位置
     */
    public enum Gravity {
        TOP,
        CENTER,
        BOTTOM,;
    }

    public LockView(Context context) {
        super(context, null);
    }

    public LockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mCircleRadius = (int) (context.getResources().getDisplayMetrics().density * mCircleRadius + 0.5);

        mPaintCircle = new Paint();
        mPaintCircle.setColor(mClrcleColor);
        mPaintCircle.setStrokeWidth(4);
        mPaintCircle.setAntiAlias(true);
        mPaintCircle.setStyle(Paint.Style.STROKE);

        mPaintSelectCircle = new Paint(mPaintCircle);
        mPaintSelectCircle.setColor(mClrcleSelectColor);
        mPaintSelectCircle.setStyle(Paint.Style.STROKE);

        mPaintLine = new Paint(mPaintSelectCircle);
        mPaintLine.setColor(mLineColor);
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setStrokeWidth(8);

        mPath = new Path();

        mBuffer = new StringBuffer();
    }

    /**
     * 设置密码回调
     *
     * @param mListener
     */
    public void setListener(PswListener mListener) {
        this.mListener = mListener;
    }

    /**
     * 设置密码键盘模式（设置模式或者验证密码模式）
     * @param mMode
     */
    public void setMode(Style mMode) {
        this.mMode = mMode;
    }

    /**
     * 设置密码键盘位置
     * @param mGravity
     */
    public void setGravity(Gravity mGravity) {
        this.mGravity = mGravity;
    }

    public void initPwdView() {
        mBuffer.delete(0, mBuffer.length());
        mIsUp = false;
        mIsStart = false;
        mPath.reset();

        for (int i = 0; i < mSelectList.size(); i++) {
            mSelectList.set(i, false);
        }
        postInvalidate();
    }

    private void initCircleData() {
        mList = new ArrayList<>();
        mCircleList = new ArrayList<>();
        mSelectList = new ArrayList<>();
        //初始化九个圆均为选中
        for (int i = 0; i < 9; i++) {
            mSelectList.add(false);
        }

        //单个圆圈所处区域高度宽度
        int height = getMeasuredHeight() / 3 * 2 / 3;
        int width = getMeasuredWidth() / 3;

        int initHeight = 0;
        if (mGravity == Gravity.TOP) {
            initHeight = 0;
        } else if (mGravity == Gravity.CENTER) {
            initHeight = getMeasuredHeight() / 3 / 2;
        } else {
            initHeight = getMeasuredHeight() / 3;
        }

        //初始化圆圈区域，圆心点
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int circleX = width * j + width / 2;
                int circleY = initHeight + height * i + height / 2;
                //*3是为了放大范围（为了更好的体验）
                Rect rect = new Rect(circleX - 3 * mCircleRadius, circleY - 3 * mCircleRadius, circleX + 3 * mCircleRadius, circleY + 3 * mCircleRadius);
                mList.add(rect);

                CircleXY circleXY = new CircleXY(circleX, circleY);
                mCircleList.add(circleXY);
            }
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //用于button有一个默认长度等
        //setMeasuredDimension(getMeasureValue(widthMeasureSpec, 1), getMeasureValue(heightMeasureSpec, 2));
    }

    public int getMeasureValue(int spec, int flag) {
        int value;

        if (flag == 1) {
            value = getMeasuredWidth() + getPaddingLeft() + getPaddingRight();
        } else {
            value = getMeasuredHeight() + getPaddingTop() + getPaddingBottom();
        }

        int size = MeasureSpec.getMode(spec);
        int mode = MeasureSpec.getMode(spec);

        switch (mode) {
            case MeasureSpec.EXACTLY:
                value = size;
                break;
        }

        return value;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mList == null) {
            initCircleData();
        }
        //画初始九宫格
        for (int i = 0; i < mCircleList.size(); i++) {
            mPaintCircle.setStyle(Paint.Style.STROKE);
            mPaintCircle.setColor(mClrcleColor);
            canvas.drawCircle(mCircleList.get(i).getCircleX(), mCircleList.get(i).getCircleY(), mCircleRadius, mPaintCircle);
        }

        //画选中九宫格
        for (int i = 0; i < mSelectList.size(); i++) {
            if (mSelectList.get(i)) {
                mPaintCircle.setStyle(Paint.Style.FILL);
                canvas.drawCircle(mCircleList.get(i).getCircleX(), mCircleList.get(i).getCircleY(), mCircleRadius * 2, mPaintSelectCircle);
            }
        }
        //只有当按下是选中某个圆才开始画线
        if (mIsStart) {
            //画圆与圆之间的线
            canvas.drawPath(mPath, mPaintLine);

            //手指抬起后就不画这个线（只画圆与圆之间的线）
            if (!mIsUp) {
                //画最后选中的圆与手指之间的线
                canvas.drawLine(mCurX, mCurY, mDownX, mDownY, mPaintLine);
            }
        }

        //画选中九宫格
        for (int i = 0; i < mSelectList.size(); i++) {
            if (mSelectList.get(i)) {
                mPaintCircle.setStyle(Paint.Style.FILL);
                mPaintCircle.setColor(mClrcleSelectColor);
                canvas.drawCircle(mCircleList.get(i).getCircleX(), mCircleList.get(i).getCircleY(), mCircleRadius, mPaintCircle);
            }
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDownX = event.getX();
        mDownY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (int i = 0; i < mList.size(); i++) {
                    //当按下的坐标存在圆的感应区域,则表明开始画线了
                    if (mList.get(i).contains((int) mDownX, (int) mDownY)) {
                        mIsStart = true;
                        mCurX = mCircleList.get(i).getCircleX();
                        mCurY = mCircleList.get(i).getCircleY();
                        mPath.moveTo(mCurX, mCurY);
                        mSelectList.set(i, true);

                        mBuffer.append(i);

                        break;
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (mIsStart) {
                    //抬起后清空手指与最后一个选中的圆之间的线
                    mIsUp = true;
                    invalidate();

                    //验证密码
                    if (mMode == Style.SETPWD) {
                        mDefaultTime = 800;
                    } else {
                        mDefaultTime = 300;
                    }

                    //延时清空所画的线,用于记录密码是所用
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mIsUp = false;
                            mIsStart = false;
                            mPath.reset();

                            for (int i = 0; i < mSelectList.size(); i++) {
                                mSelectList.set(i, false);
                            }
                            postInvalidate();

                            if (mListener != null) {
                                mListener.returnPwd(mBuffer.toString());
                            }
                            mBuffer.delete(0, mBuffer.length());
                        }
                    }, mDefaultTime);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsStart) {
                    for (int i = 0; i < mList.size(); i++) {
                        //是否在圆的范围内
                        if (mList.get(i).contains((int) mDownX, (int) mDownY)) {
                            //当当前这个圆之前没选中过才能选中
                            if (!mSelectList.get(i)) {
                                mSelectList.set(i, true);
                                mIsStart = true;

                                mCurX = mCircleList.get(i).getCircleX();
                                mCurY = mCircleList.get(i).getCircleY();

                                mPath.lineTo(mCurX, mCurY);

                                mBuffer.append(i);
                            }
                            break;
                        }
                    }
                }
                invalidate();
                break;
        }

        return true;
    }

    /**
     * 用于记录圆心点的实体类
     */
    private class CircleXY {
        private int circleX;
        private int circleY;

        public CircleXY(int circleX, int circleY) {
            this.circleX = circleX;
            this.circleY = circleY;
        }

        public int getCircleX() {
            return circleX;
        }

        public int getCircleY() {
            return circleY;
        }
    }

    public interface PswListener {
        public void returnPwd(String pwd);
    }

}
