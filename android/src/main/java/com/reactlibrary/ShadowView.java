package com.como.RNTShadowView;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Path;

public class ShadowView extends ViewGroup {
    int shadowOffsetX = 0;
    int shadowOffsetY = (int)(-2 * Resources.getSystem().getDisplayMetrics().density);
    int shadowRadius = 0;
    int borderRadius = 0;
    int borderTopLeftRadius = 0;
    int borderTopRightRadius = 0;
    int borderBottomLeftRadius = 0;
    int borderBottomRightRadius = 0;
    int shadowColor;
    int shadowColorToDraw;
    int borderShadowColor;
    int shadowOpacity;
    int margin;
    double borderWidth;

    Paint viewPaint = new Paint();
    Paint borderPaint = new Paint();

    Bitmap shadowBitmap = null;

    public ShadowView(Context context) {
        super(context);
        init();
    }

    public ShadowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShadowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            shadowBitmap = createShadowForView();
            invalidate();
        }
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(Color.TRANSPARENT);
        viewPaint.setColor(color);
        createShadowColor();
        invalidate();
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_NONE, viewPaint);
        viewPaint.setAntiAlias(true);
        borderPaint.setAntiAlias(true);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.BLACK);
        shadowColor = Color.BLACK;
        shadowColorToDraw = Color.BLACK;

        createShadowColor();
        invalidate();
    }

    public void setBorderRadius(double borderRadius) {
        this.borderRadius=(int) (borderRadius * Resources.getSystem().getDisplayMetrics().density);
        invalidate();
    }

    public void setBorderTopLeftRadius(double borderTopLeftRadius) {
        this.borderTopLeftRadius=(int) (borderTopLeftRadius * Resources.getSystem().getDisplayMetrics().density);
        invalidate();
    }

    public void setBorderTopRightRadius(double borderTopRightRadius) {
        this.borderTopRightRadius=(int) (borderTopRightRadius * Resources.getSystem().getDisplayMetrics().density);
        invalidate();
    }

    public void setBorderBottomLeftRadius(double borderBottomLeftRadius) {
        this.borderBottomLeftRadius=(int) (borderBottomLeftRadius * Resources.getSystem().getDisplayMetrics().density);
        invalidate();
    }

    public void setBorderBottomRightRadius(double borderBottomRightRadius) {
        this.borderBottomRightRadius=(int) (borderBottomRightRadius * Resources.getSystem().getDisplayMetrics().density);
        invalidate();
    }

    public void setShadowOffsetX(double shadowOffsetX) {
        this.shadowOffsetX = (int)((shadowOffsetX * Resources.getSystem().getDisplayMetrics().density));
        invalidate();
    }

    public void setShadowOffsetY(double shadowOffsetY) {
        this.shadowOffsetY = (int)((shadowOffsetY * Resources.getSystem().getDisplayMetrics().density));
        invalidate();
    }

    public void setShadowColor(int shadowColor) {
        this.shadowColor = shadowColor;
        createShadowColor();
        invalidate();
    }

    public void setShadowOpacity(double shadowOpacity) {
        shadowOpacity = Math.min(Math.max(0, shadowOpacity), 1);
        this.shadowOpacity = (int)(shadowOpacity * 255);
        this.createShadowColor();
        invalidate();
    }

    public void setShadowRadius(double shadowRadius) {
        shadowRadius = Math.max(0.2, shadowRadius);
        this.shadowRadius = (int)shadowRadius;
        this.margin = (int)(this.shadowRadius * 6.2);
        invalidate();
    }

    public void setBorderColor(int borderColor) {
        borderPaint.setColor(borderColor);
        createShadowColor();
        invalidate();
    }

    public void setBorderWidth(double borderWidth) {
        this.borderWidth = (borderWidth * Resources.getSystem().getDisplayMetrics().density * 1.1);
        invalidate();
    }

    private void createShadowColor() {
        int red = Color.red(shadowColor);
        int green = Color.green(shadowColor);
        int blue = Color.blue(shadowColor);
        int shadowColorAlpha = Color.alpha(shadowColor);
        int borderColorAlpha = Color.alpha(borderPaint.getColor());
        int shadowAlpha = (int)((double)shadowOpacity * ((double)shadowColorAlpha/255.0) );
        int borderShadowAlpha = (int)((double)shadowOpacity * ((double)borderColorAlpha/255.0));
        shadowColorToDraw = Color.argb(shadowAlpha, red, green, blue);
        borderShadowColor = Color.argb(borderShadowAlpha, red, green, blue);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getWidth() == 0) {
            return;
        }

        Rect imageRect = new Rect(0, 0, shadowBitmap.getWidth(), shadowBitmap.getHeight());
        Rect targetRect = new Rect(shadowOffsetX - margin, shadowOffsetY - margin, getWidth() + margin + shadowOffsetX, getHeight() + margin + shadowOffsetY);
        canvas.drawBitmap(shadowBitmap, imageRect, targetRect, viewPaint);

        Object contentRect = new RectF(0, 0, getWidth(), getHeight());
//        canvas.drawRoundRect((RectF)contentRect, borderRadius, borderRadius, viewPaint);
        Path path = RoundedRect(
                (RectF)contentRect,
                borderTopLeftRadius > 0 ? borderTopLeftRadius : borderRadius,
                borderTopRightRadius > 0 ? borderTopRightRadius : borderRadius,
                borderBottomRightRadius > 0? borderBottomRightRadius : borderRadius,
                borderBottomLeftRadius > 0 ? borderBottomLeftRadius : borderRadius
        );
        canvas.drawPath(path, viewPaint);
    }

    public Bitmap createShadowForView() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth() + margin * 2, getHeight()+ margin * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setColor(shadowColorToDraw);

        Object contentRect = new RectF(margin, margin, bitmap.getWidth() - margin, bitmap.getHeight() - margin);
//        canvas.drawRoundRect((RectF)contentRect, borderRadius, borderRadius, shadowPaint);
        Path path = RoundedRect(
                (RectF)contentRect,
                borderTopLeftRadius > 0 ? borderTopLeftRadius : borderRadius,
                borderTopRightRadius > 0 ? borderTopRightRadius : borderRadius,
                borderBottomRightRadius > 0? borderBottomRightRadius : borderRadius,
                borderBottomLeftRadius > 0 ? borderBottomLeftRadius : borderRadius
        );
        canvas.drawPath(path, shadowPaint);
        return BlurBuilder.blur(getContext(), bitmap, shadowRadius);
    }

    public static Path RoundedRect(RectF rect, float tl, float tr, float br, float bl) {
        float left = rect.left;
        float top = rect.top;
        float right = rect.right;
        float bottom = rect.bottom;
        Path path = new Path();

        if (tl < 0) tl = 0;
        if (tr < 0) tr = 0;
        if (br < 0) br = 0;
        if (bl < 0) bl = 0;
        float width = right - left;
        float height = bottom - top;

        float tlTop = tl > width / 2 ? width / 2 : tl;
        float tlLeft = tl > height / 2 ? height / 2 : tl;
        float trTop = tr > width / 2 ? width / 2 : tr;
        float trRight = tr > height / 2 ? height / 2 : tr;
        float brBottom = br > width / 2 ? width / 2 : br;
        float brRight = br > height / 2 ? height / 2 : br;
        float blBottom = bl > width / 2 ? width / 2 : bl;
        float blLeft = bl > height / 2 ? height / 2 : bl;

        float topWidthMinusCorners = (width - tlTop - trTop);
        float bottomWidthMinusCorners = (width - blBottom - brBottom);
        float leftHightMinusCorners = (height - tlLeft - blLeft);
        float rightHightMinusCorners = (height - trRight - brRight);

        path.moveTo(right, top + trRight);
        if (tr > 0)
            path.rQuadTo(0, -trRight, -trTop, -trRight);//top-right corner
        else{
            path.rLineTo(0, -trRight);
            path.rLineTo(-trTop,0);
        }
        path.rLineTo(-topWidthMinusCorners, 0);

        if (tl > 0)
            path.rQuadTo(-tlTop, 0, -tlTop, tlLeft); //top-left corner
        else{
            path.rLineTo(-tlTop, 0);
            path.rLineTo(0,tlLeft);
        }
        path.rLineTo(0, leftHightMinusCorners);

        if (bl > 0)
            path.rQuadTo(0, blLeft, blBottom, blLeft);//bottom-left corner
        else{
            path.rLineTo(0, blLeft);
            path.rLineTo(blBottom,0);
        }
        path.rLineTo(bottomWidthMinusCorners, 0);

        if (br > 0)
            path.rQuadTo(brBottom, 0, brBottom, -brRight); //bottom-right corner
        else{
            path.rLineTo(brBottom,0);
            path.rLineTo(0, -brRight);
        }
        path.rLineTo(0, -bottomWidthMinusCorners);

        path.close();//Given close, last lineto can be removed.

        return path;
    }
}
