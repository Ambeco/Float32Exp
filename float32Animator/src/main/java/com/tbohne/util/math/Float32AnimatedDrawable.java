package com.tbohne.util.math;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextPaint;

import com.tbohne.util.math.Float32AnimatedTextSpan.DrawableClock;

import java.util.List;

public class Float32AnimatedDrawable extends Drawable implements Animatable {
	private final DrawableClock clock;
	private final TextPaint lastPaint;

	private List<ImmutableFloat32ExpL> polynomial;

	private boolean started = true;
	private final Float32ExpL displayValue = new Float32ExpL();
	private final StringBuilder stringBuilder = new StringBuilder();

	public Float32AnimatedDrawable(List<? extends IFloat32ExpL> polynomial, DrawableClock clock) {
		this(polynomial, clock, new TextPaint());
	}

	public Float32AnimatedDrawable(List<? extends IFloat32ExpL> polynomial, DrawableClock clock, TextPaint lastPaint) {
		this.polynomial = Polynomials.toImmutable(polynomial);
		this.clock = clock;
		this.lastPaint = lastPaint;
		lastPaint.setAntiAlias(true);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
			setAutoMirrored(false);
		}
		recalculateBounds();
	}

	public List<ImmutableFloat32ExpL> getPolynomial() { return polynomial; }
	public void setPolynomial(List<ImmutableFloat32ExpL> polynomial) {
		this.polynomial = polynomial;
		if (!started) invalidateSelf();
	}

	public void setPaint(Paint src) {
		if (!lastPaint.equals(src)) {
			lastPaint.set(src);
			recalculateBounds();
			if (!started) invalidateSelf();
		}
	}

	public Typeface getTypeface() { return lastPaint.getTypeface(); }
	// This modifies the typeface _of the Paint object_
	public Typeface setTypeface(Typeface typeface) {
		Typeface result = lastPaint.setTypeface(typeface);
		recalculateBounds();
		if (!started) invalidateSelf();
		return result;
	}

	private void recalculateBounds() {
		int width = getIntrinsicWidth();
		int height = getIntrinsicHeight();
		if (getBounds().width() != width || getBounds().height() != height)
			setBounds(0, 0, width > 0 ? width : 0, height > 0 ? height : 0);
	}

	@Override
	public int getIntrinsicHeight() {
		Paint.FontMetricsInt fm = lastPaint.getFontMetricsInt();
		return fm.bottom - fm.top;
	}

	@Override
	public int getIntrinsicWidth() {
		float max = 0;
		for (String wideString : Float32AnimatedTextSpan.WIDE_STRINGS) {
			float width = lastPaint.measureText(wideString);
			if (width > max) max = width;
		}
		return (int) Math.ceil(max);
	}

	@Override
	public void start() {
		started = true;
	}

	@Override
	public void stop() {
		started = false;
	}

	@Override
	public boolean isRunning() {
		return started;
	}


	@Override
	public void draw(Canvas canvas) {
		Polynomials.at(polynomial, clock.getTime(), displayValue);
		stringBuilder.setLength(0);
		displayValue.toString(stringBuilder);
		Paint.FontMetricsInt fm = lastPaint.getFontMetricsInt();
		canvas.drawText(stringBuilder, 0, stringBuilder.length(), 0, -fm.top, lastPaint);
		if (started) {
			scheduleSelf(this::invalidateSelf, 15);
		}
	}

	@Override
	public void setAlpha(int alpha) {
	}

	@Override
	public void setColorFilter(@Nullable ColorFilter colorFilter) {
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSPARENT;
	}
}
