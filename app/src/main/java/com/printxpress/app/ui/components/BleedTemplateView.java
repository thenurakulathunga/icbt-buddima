package com.printxpress.app.ui.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.printxpress.app.R;

/**
 * Vector illustration of a business card print template (FR-5.1): the bleed area, the trim line
 * and the safe zone, drawn to scale (95 × 57 mm bleed, 89 × 51 mm trim, 5 mm safe inset). It can
 * render itself to a bitmap so the template can be shared.
 *
 * @since 1.0
 */
public class BleedTemplateView extends View {

    /** Width of the sheet including bleed, in millimetres. */
    private static final float BLEED_W_MM = 95f;
    /** Height of the sheet including bleed, in millimetres. */
    private static final float BLEED_H_MM = 57f;
    /** Bleed on each side, in millimetres. */
    private static final float BLEED_MM = 3f;
    /** Safe inset from the trim, in millimetres. */
    private static final float SAFE_MM = 5f;
    /** Label text height, in millimetres. */
    private static final float TEXT_MM = 3.2f;
    /** Label padding, in millimetres. */
    private static final float PAD_MM = 1.5f;
    /** Stroke width, in millimetres. */
    private static final float STROKE_MM = 0.35f;
    /** Safe-line dash length, in millimetres. */
    private static final float DASH_MM = 1.5f;
    /** Safe-line gap length, in millimetres. */
    private static final float GAP_MM = 1f;
    /** Alpha of the bleed tint. */
    private static final int BLEED_ALPHA = 64;

    /** Bleed fill. */
    private final Paint bleedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /** Card fill. */
    private final Paint cardPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /** Trim stroke. */
    private final Paint trimPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /** Safe zone stroke. */
    private final Paint safePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /** Label text. */
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /** Scratch rectangle. */
    private final RectF rect = new RectF();

    /**
     * Creates the view.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public BleedTemplateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bleedPaint.setColor(ContextCompat.getColor(context, R.color.px_guide_bleed));
        bleedPaint.setAlpha(BLEED_ALPHA);
        cardPaint.setColor(ContextCompat.getColor(context, R.color.px_surface));
        trimPaint.setStyle(Paint.Style.STROKE);
        trimPaint.setColor(ContextCompat.getColor(context, R.color.px_guide_trim));
        safePaint.setStyle(Paint.Style.STROKE);
        safePaint.setColor(ContextCompat.getColor(context, R.color.px_guide_safe));
        textPaint.setColor(ContextCompat.getColor(context, R.color.px_on_surface));
        textPaint.setTypeface(ResourcesCompat.getFont(context, R.font.roboto_flex_medium));
        setContentDescription(context.getString(R.string.template_share_title));
    }

    /**
     * Keeps the 95:57 aspect ratio of the bleed sheet.
     *
     * @param widthMeasureSpec  width constraint
     * @param heightMeasureSpec height constraint
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, Math.round(width * BLEED_H_MM / BLEED_W_MM));
    }

    /**
     * Draws the template.
     *
     * @param canvas the canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        draw(canvas, getWidth(), getHeight());
    }

    /**
     * Draws the template at a given size.
     *
     * @param canvas the canvas
     * @param width  width in pixels
     * @param height height in pixels
     */
    private void draw(Canvas canvas, int width, int height) {
        float mm = width / BLEED_W_MM;
        trimPaint.setStrokeWidth(mm * STROKE_MM);
        safePaint.setStrokeWidth(mm * STROKE_MM);
        safePaint.setPathEffect(new DashPathEffect(new float[]{mm * DASH_MM, mm * GAP_MM}, 0));
        rect.set(0, 0, width, height);
        canvas.drawRect(rect, bleedPaint);
        rect.set(BLEED_MM * mm, BLEED_MM * mm, width - BLEED_MM * mm, height - BLEED_MM * mm);
        canvas.drawRect(rect, cardPaint);
        canvas.drawRect(rect, trimPaint);
        float safe = (BLEED_MM + SAFE_MM) * mm;
        rect.set(safe, safe, width - safe, height - safe);
        canvas.drawRect(rect, safePaint);
        textPaint.setTextSize(mm * TEXT_MM);
        float pad = mm * PAD_MM;
        float line = textPaint.getTextSize() + pad;
        float x = safe + pad;
        float y = safe + line;
        canvas.drawText(getContext().getString(R.string.template_label_trim), x, y, textPaint);
        canvas.drawText(getContext().getString(R.string.template_label_bleed), x, y + line, textPaint);
        canvas.drawText(getContext().getString(R.string.template_label_safe), x, y + line * 2, textPaint);
    }

    /**
     * Renders the template to a bitmap for sharing.
     *
     * @param width the bitmap width in pixels
     * @return the bitmap
     */
    public Bitmap toBitmap(int width) {
        int height = Math.round(width * BLEED_H_MM / BLEED_W_MM);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        draw(new Canvas(bitmap), width, height);
        return bitmap;
    }
}
