package com.tbohne.util.math;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.view.View;

import com.tbohne.util.math.Float32AnimatedTextSpan.PolynomialClock;
import com.tbohne.util.math.IFloat32ExpL.StringFormatParams;

import java.util.List;

public class Float32AnimatedDrawable extends Drawable implements Animatable {
	private static final Handler handler = new Handler(Looper.getMainLooper());

	private final StringFormatParams params;
	private final PolynomialClock clock;
	private final TextPaint lastPaint;
	private final ValueAnimator animator;
	private ImmutableFloat32ExpL firstTime;

	private List<ImmutableFloat32ExpL> polynomial;

	private final Float32ExpL displayValue = new Float32ExpL();
	private final StringBuilder stringBuilder = new StringBuilder();
	private final View view;
	private final Float32ExpL temp1 = new Float32ExpL();
	private final Float32ExpL temp2 = new Float32ExpL();
	private final Drawable.Callback callback = new Drawable.Callback(){
		@Override
		public void invalidateDrawable(@NonNull Drawable who) {
			Rect rect = getBounds();
			view.postInvalidate(rect.left, rect.top, rect.right, rect.bottom);
		}

		@Override
		public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {
			handler.postDelayed(what, when);
		}

		@Override
		public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {
			handler.removeCallbacks(what);
		}
	};

	public Float32AnimatedDrawable(List<? extends IFloat32ExpL> polynomial, View view, StringFormatParams params, PolynomialClock clock) {
		this(polynomial, view, params, clock, new TextPaint());
	}

	public Float32AnimatedDrawable(List<? extends IFloat32ExpL> polynomial, View view, StringFormatParams params, PolynomialClock clock, TextPaint lastPaint) {
		this.polynomial = Polynomials.toImmutable(polynomial);
		this.params = params;
		this.clock = clock;
		this.lastPaint = lastPaint;
		this.view = view;
		firstTime = clock.getTime().toImmutable();
		this.animator = initAnimator();
		setCallback(callback);
		lastPaint.setAntiAlias(true);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
			setAutoMirrored(false);
		}
		recalculateBounds();
	}

	private ValueAnimator initAnimator() {
		long totalMillis = 86_400_000L;
		ValueAnimator animator = new ValueAnimator();
		animator.setDuration(totalMillis);
		animator.setObjectValues(0L, totalMillis);
		animator.setEvaluator((TypeEvaluator<Long>) (fraction, startValue, endValue) -> (long)(fraction*totalMillis));
		animator.addUpdateListener(animation -> invalidateSelf());
		animator.start();
		return animator;
	}

	ValueAnimator getAnimator() {
		return animator;
	}

	public List<ImmutableFloat32ExpL> getPolynomial() { return polynomial; }
	public void setPolynomial(List<ImmutableFloat32ExpL> polynomial) {
		this.polynomial = polynomial;
		if (!animator.isRunning()) invalidateSelf();
	}

	public void setPaint(Paint src) {
		if (!lastPaint.equals(src)) {
			lastPaint.set(src);
			recalculateBounds();
			if (!animator.isRunning()) invalidateSelf();
		}
	}

	public Typeface getTypeface() { return lastPaint.getTypeface(); }
	// This modifies the typeface _of the Paint object_
	public Typeface setTypeface(Typeface typeface) {
		Typeface result = lastPaint.setTypeface(typeface);
		recalculateBounds();
		if (!animator.isRunning()) invalidateSelf();
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
		animator.start();
		firstTime = clock.getTime().toImmutable();
	}

	@Override
	public void stop() {
		animator.cancel();
	}

	@Override
	public boolean isRunning() {
		return animator.isRunning();
	}


	@Override
	public void draw(Canvas canvas) {
		IFloat32ExpL time = clock.getEstimatedTime(firstTime, (Long) animator.getAnimatedValue());
		Polynomials.at(polynomial, time, displayValue, temp1, temp2);
		stringBuilder.setLength(0);
		displayValue.toString(stringBuilder, params);
		Paint.FontMetricsInt fm = lastPaint.getFontMetricsInt();
		canvas.drawText(stringBuilder, 0, stringBuilder.length(), 0, -fm.top, lastPaint);
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
