package de.jonasw.laz.clockface;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import de.jonasw.laz.R;
import de.jonasw.laz.Utils;

/**
 * Created by thriqon on 4/30/15.
 */
public class CircleView extends View {
    private Paint backgroundArc;
    private Paint foregroundArc;
    private Paint textPaint;

    private String text;

    private float progress;

    public float getArcWidth() {
        return foregroundArc.getStrokeWidth();
    }

    public void setArcWidth(float v) {
        foregroundArc.setStrokeWidth(v);
        backgroundArc.setStrokeWidth(v);
        postInvalidate();
    }

    public int getBackgroundArcColor() {
        return backgroundArc.getColor();
    }

    public int getForegroundArcColor() {
        return foregroundArc.getColor();
    }

    public void setBackgroundArcColor(int color) {
        backgroundArc.setColor(color);
        postInvalidate();
    }

    public void setForegroundArcColor(int color) {
        foregroundArc.setColor(color);
        postInvalidate();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float v) {
        validateProgressValue(v);
        progress = v;
        postInvalidate();
    }

    private void validateProgressValue(float v) {
        if (v < 0 || v > 100) {
            throw new IllegalArgumentException("Progress is 0 <= p <= 100");
        }
    }

    private void initializePaints() {
        foregroundArc = new Paint(Paint.ANTI_ALIAS_FLAG);
        foregroundArc.setColor(Color.RED);
        foregroundArc.setStrokeWidth(5);
        foregroundArc.setStyle(Paint.Style.STROKE);

        backgroundArc = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundArc.setColor(Color.WHITE);
        backgroundArc.setStrokeWidth(5);
        backgroundArc.setStyle(Paint.Style.STROKE);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(15);
    }

    public CircleView(Context context) {
        super(context);
        initializePaints();
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializePaints();

        TypedArray ta = null;
        try {
            ta = context.obtainStyledAttributes(attrs, R.styleable.circle_view);


            foregroundArc.setColor(ta.getColor(R.styleable.circle_view_foregroundArcColor, foregroundArc.getColor()));
            backgroundArc.setColor(ta.getColor(R.styleable.circle_view_backgroundArcColor, backgroundArc.getColor()));

            foregroundArc.setStrokeWidth(ta.getDimension(R.styleable.circle_view_arcWidth, foregroundArc.getStrokeWidth()));
            backgroundArc.setStrokeWidth(ta.getDimension(R.styleable.circle_view_arcWidth, backgroundArc.getStrokeWidth()));

            float tentativeProgress = ta.getFloat(R.styleable.circle_view_progress, 0f);
            validateProgressValue(tentativeProgress);
            progress = tentativeProgress;

            textPaint.setColor(ta.getColor(R.styleable.circle_view_textColor, textPaint.getColor()));
            textPaint.setTextSize(ta.getDimension(R.styleable.circle_view_textSize, textPaint.getTextSize()));

            text = ta.getString(R.styleable.circle_view_text);

        } finally {
            if (ta != null) {
                ta.recycle();
            }
        }
    }

    private static final int ANGLE_TOP = 270;
    private static final int FULL_CIRCLE = 360;
    private static final boolean DO_NOT_USE_CENTER = false;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawArc(getCanvasBox(), ANGLE_TOP, FULL_CIRCLE, DO_NOT_USE_CENTER, backgroundArc);
        canvas.drawArc(getCanvasBox(), ANGLE_TOP, getSweepingAngle(), DO_NOT_USE_CENTER, foregroundArc);

        String displayText = text == null ? Float.toString(progress) : text;
        float widthOfText = textPaint.measureText(displayText);
        canvas.drawText(displayText, getWidth() / 2 - widthOfText/2, getHeight() / 2, textPaint);
    }

    private float getSweepingAngle() {
        return progress * 3.6f;
    }

    RectF getCanvasBox() {
        final int xCenter = getWidth() / 2;
        final int yCenter = getHeight() / 2;

        final int r = getRadiusOfArcs();

        return new RectF(xCenter - r, yCenter - r, xCenter + r, yCenter + r);
    }

    int getRadiusOfArcs() {
        int minimum = Utils.min(getHeight(), getWidth());
        return Math.round(minimum * 0.4f);
    }


}
