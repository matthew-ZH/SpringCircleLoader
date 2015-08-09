package com.matthew.sample.CircleLoader;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import java.lang.Math;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by mattlyzheng on 2015/8/9.
 */
public class CircleLoadingView extends View {

    private List<Point> bigPoints = new LinkedList<Point>();
    private Point movePoint = new Point();
    private int count = 8;
    private int normalRadius = dp2px(5);
    private int littleRadius = dp2px(2.5f);
    private int scaleRadius = dp2px(6);
    private int loadingRadius = dp2px(40);
    private int loadingCenterX = dp2px(55);
    private int step_loading = 1;
    private int step;
    private Paint paint;

    private float startX1;
    private float startX2;
    private float startY1;
    private float startY2;
    private float endX1;
    private float endX2;
    private float endY1;
    private float endY2;
    private float controlX1;
    private float controlY1;
    private Path path;

    public CircleLoadingView(Context context) {
        this(context, null);
    }

    public CircleLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        prepareResource();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(dp2px(110), dp2px(110));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(Color.GREEN);
        for (Point t: bigPoints) {
            canvas.drawCircle(t.x, t.y , t.radius, paint);
        }
        moveLoadingPoint(canvas);

    }

    private void moveLoadingPoint(Canvas canvas) {
        paint.setColor(Color.GREEN);
        canvas.drawCircle(movePoint.x, movePoint.y, movePoint.radius, paint);
        movePoint.currentAngle = (step_loading +movePoint.currentAngle)%360;
        movePoint.x = (float) (getPaddingLeft() + loadingCenterX + Math.sin(Math.toRadians(movePoint.currentAngle)) * loadingRadius);
        movePoint.y = (float) (getPaddingLeft() + loadingCenterX + Math.cos(Math.toRadians(movePoint.currentAngle)) * loadingRadius);
        for ( Point t:bigPoints) {
            if (isTouched(movePoint, t)) {
                t.radius = scaleRadius;
            } else {

                t.radius = normalRadius;
            }
            if (isConnect(movePoint, t)) {
                float headOffsetX1 = (float)(normalRadius*Math.sin(Math.atan((movePoint.y - t.y) / (movePoint.x - t.x))));
                float headOffsetY1 = (float)(normalRadius*Math.cos(Math.atan((movePoint.y - t.y) / (movePoint.x - t.x))));
                float footOffsetX1 = (float)(littleRadius*Math.sin(Math.atan((movePoint.y - t.y) / (movePoint.x - t.x))));
                float footOffsetY1 = (float)(littleRadius*Math.cos(Math.atan((movePoint.y - t.y) / (movePoint.x - t.x))));

                startX1 = t.x - headOffsetX1;
                startY1 = t.y + headOffsetY1;

                endX1 = t.x+ headOffsetX1;
                endY1 = t.y - headOffsetY1;

                startX2 = movePoint.x - footOffsetX1;
                startY2 = movePoint.y + footOffsetY1;

                endX2 = movePoint.x + footOffsetX1;
                endY2 = movePoint.y - footOffsetY1;

                controlX1 = (t.x + movePoint.x) / 2;
                controlY1 = (t.y + movePoint.y) / 2;

                path.reset();
                path.moveTo(startX1, startY1);
                path.quadTo(controlX1, controlY1, startX2, startY2);
                path.lineTo(endX2, endY2);
                path.quadTo(controlX1, controlY1, endX1, endY1);
                path.lineTo(startX1, startY1);
                canvas.drawPath(path, paint);
            }
        }
        invalidate();
    }

    private void prepareResource() {
        path = new Path();
        paint = new Paint();
        paint.setAntiAlias(true);
        bigPoints.clear();
        step = 360/count;
        for (int i=0; i <count ; i++) {
            Point temp = new Point();
            temp.currentAngle = step * i;
            temp.radius = normalRadius;
            temp.x = (float) (getPaddingLeft() + loadingCenterX + Math.sin(Math.toRadians(temp.currentAngle)) * loadingRadius);
            temp.y = (float) (getPaddingLeft() + loadingCenterX + Math.cos(Math.toRadians(temp.currentAngle)) * loadingRadius);
            bigPoints.add(temp);
        }
        movePoint.currentAngle = 0;
        movePoint.radius = littleRadius;
        movePoint.x = (float) (getPaddingLeft() + loadingCenterX + Math.sin(Math.toRadians(movePoint.currentAngle)) * loadingRadius);
        movePoint.y = (float) (getPaddingLeft() + loadingCenterX + Math.cos(Math.toRadians(movePoint.currentAngle)) * loadingRadius);
    }

    private boolean isTouched(Point small, Point big) {
        return Math.sqrt(Math.pow(big.y - small.y, 2) + Math.pow(big.x - small.x, 2))<(normalRadius+littleRadius);
    }

    private boolean isConnect(Point a, Point b) {
        float distance = (float)Math.sqrt((a.x - b.x)*(a.x - b.x) + (a.y - b.y)*(a.y - b.y));
        return distance < loadingRadius * Math.cos(Math.toRadians(75));
    }

    public int dp2px(float dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    class Point {
        float x;
        float y;
        int currentAngle;
        float radius;
    }
}
