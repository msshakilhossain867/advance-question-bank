package com.example.questionbank_practiceexam.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ScoreRingView extends View {

    private Paint backgroundPaint;
    private Paint progressPaint;

    private float percentage = 80f;

    private final RectF rectF = new RectF();

    public ScoreRingView(Context context) {
        super(context);
        init();
    }

    public ScoreRingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScoreRingView(
            Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(dp(14));
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        backgroundPaint.setColor(0xFFEEF0F2);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(dp(14));
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setColor(0xFF3DBA82);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        float radius = Math.min(getWidth(), getHeight()) / 2f
                - dp(10);

        rectF.set(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius
        );

        // Background ring
        canvas.drawArc(
                rectF,
                0,
                360,
                false,
                backgroundPaint
        );

        // Green progress ring
        float sweepAngle = (percentage / 100f) * 360f;

        canvas.drawArc(
                rectF,
                -90,
                sweepAngle,
                false,
                progressPaint
        );
    }

    public void setPercentage(float percentage) {

        this.percentage = Math.max(
                0,
                Math.min(100, percentage)
        );

        invalidate();
    }

    public void setScore(int correct, int total) {

        float percentage = 0f;

        if (total > 0) {

            percentage =
                    (correct * 100f) / total;
        }

        setPercentage(percentage);
    }

    public float getPercentage() {
        return percentage;
    }

    private float dp(float value) {
        return value * getResources()
                .getDisplayMetrics()
                .density;
    }
}