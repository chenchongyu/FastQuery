package net.runningcode.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import net.runningcode.R;

/**
 * Created by Administrator on 2016/5/6.
 */
public class CircleTextView extends View {
    private Paint circlePaint,txtPaint;
    private int circleColor = Color.RED,textColor = Color.RED,radius = 50;
    private float circleWidth = 2,textSize = 20;
    private String text;
    public CircleTextView(Context context) {
        super(context);
        initPaint();
    }

    public CircleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttrs(context.obtainStyledAttributes(attrs, R.styleable.CircleTextView));
    }

    private void parseAttrs(TypedArray a) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        text = a.getString(R.styleable.CircleTextView_text);
        radius = a.getInt(R.styleable.CircleTextView_radius,50);
        circleWidth = a.getDimension(R.styleable.CircleTextView_border_width, 1);
        circleColor = a.getColor(R.styleable.CircleTextView_border_color, Color.RED);
        textColor = a.getColor(R.styleable.CircleTextView_text_color,Color.RED);
        textSize = a.getDimension(R.styleable.CircleTextView_text_size,20f);
        initPaint();
    }

    private void initPaint() {
        txtPaint = new Paint();
        txtPaint.setAntiAlias(true);


        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        circlePaint.setColor(circleColor);
        circlePaint.setStrokeWidth(circleWidth);
        txtPaint.setColor(textColor);
        txtPaint.setTextSize(textSize);

        int centerX = getMeasuredWidth()/2;
        int centerY = getMeasuredHeight()/2;
        if (circlePaint != null){
            canvas.drawCircle(centerX,centerY,radius,circlePaint);
        }

        if (!TextUtils.isEmpty(text)){
            float offset = txtPaint.measureText(text) / 2;
            canvas.drawText(text,centerX-offset,centerY+offset/2,txtPaint);
        }
    }

    public void setCircleColor(int circleColor) {
        this.circleColor = circleColor;
        invalidate();
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        invalidate();
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        invalidate();
    }

    public void setCircleWidth(float circleWidth) {
        this.circleWidth = circleWidth;
        invalidate();
    }
}
