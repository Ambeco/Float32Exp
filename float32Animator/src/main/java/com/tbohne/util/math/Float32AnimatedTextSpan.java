package com.tbohne.util.math;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ReplacementSpan;
import android.view.View;

import com.tbohne.util.math.IFloat32ExpL.StringFormatParams;

import java.util.List;

import static com.tbohne.util.math.Float32ExpLHelpers.DEFAULT_STRING_PARAMS;

public class Float32AnimatedTextSpan extends ReplacementSpan {
	//This is called within the animation so needs to be _really_ fast and should minimize
	//allocations, which may cause a GC in the main thread causing stutter.
	public interface PolynomialClock {
		IFloat32ExpL getTime();
		IFloat32ExpL getEstimatedTime(ImmutableFloat32ExpL firstTime, long estimatedMillis);
	}

	public static SpannableString createFloat32AnimatedSpan(List<? extends IFloat32ExpL> polynomial, View view,
			PolynomialClock clock) {
		return createFloat32AnimatedSpan(polynomial, DEFAULT_STRING_PARAMS, view, clock);
	}
	public static SpannableString createFloat32AnimatedSpan(List<? extends IFloat32ExpL> polynomial, StringFormatParams params, View view,
			PolynomialClock clock) {
		//TODO: Appendable instead of StringBuilder.
		StringBuilder stringBuilder = new StringBuilder();
		Float32ExpL displayValue = new Float32ExpL();
		Polynomials.at(polynomial, clock.getTime(), displayValue, new Float32ExpL(), new Float32ExpL());
		displayValue.toString(stringBuilder, params);
		SpannableString string = new SpannableString(stringBuilder);
		string.setSpan(new Float32AnimatedTextSpan(polynomial, params, view, clock), 0, stringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return string;
	}

	public static SpannableStringBuilder appendFloat32AnimatedSpan(SpannableStringBuilder builder,
			List<? extends IFloat32ExpL> polynomial,
			View view,
			PolynomialClock clock) {
		return appendFloat32AnimatedSpan(builder, polynomial, DEFAULT_STRING_PARAMS, view, clock);
	}
	public static SpannableStringBuilder appendFloat32AnimatedSpan(SpannableStringBuilder builder,
			List<? extends IFloat32ExpL> polynomial,
			StringFormatParams params,
			View view,
			PolynomialClock clock) {
		//TODO: Appendable instead of StringBuilder.
		StringBuilder stringBuilder = new StringBuilder();
		Float32ExpL displayValue = new Float32ExpL();
		Polynomials.at(polynomial, clock.getTime(), displayValue, new Float32ExpL(), new Float32ExpL());
		displayValue.toString(stringBuilder, params);
		int offset = builder.length();
		builder.append(stringBuilder);
		builder.setSpan(new Float32AnimatedTextSpan(polynomial, params, view, clock), offset, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return builder;
	}
	
	static final String[] WIDE_STRINGS = {
			"-0.00000000000e-0000000000",
			"-1.11111111111e-1111111111",
			"-2.22222222222e-2222222222",
			"-3.33333333333e-3333333333",
			"-4.44444444444e-4444444444",
			"-5.55555555555e-5555555555",
			"-6.66666666666e-6666666666",
			"-7.77777777777e-7777777777",
			"-8.88888888888e-8888888888",
			"-9.99999999999e-9999999999",
	};

	private final List<ImmutableFloat32ExpL> polynomial;
	private final StringFormatParams params;
	private final PolynomialClock clock;
	private final StringBuilder stringBuilder = new StringBuilder();
	private final ValueAnimator animator;
	private ImmutableFloat32ExpL firstTime;
	
	private @Nullable Paint lastPaint;
	private float lastX = 0;
	private int lastY = 0;
	private int lastWidth = 0;
	private int lastHeight = 0;
	private final Float32ExpL lastDisplay = new Float32ExpL();
	private final Float32ExpL temp1 = new Float32ExpL();
	private final Float32ExpL temp2 = new Float32ExpL();
	
	protected Float32AnimatedTextSpan(List<? extends IFloat32ExpL> polynomial, StringFormatParams params, View view, PolynomialClock clock) {
		this.polynomial = Polynomials.toImmutable(polynomial);
		this.params = params;
		this.clock = clock;
		firstTime = clock.getTime().toImmutable();
		animator = initAnimator(view);
	}

	private ValueAnimator initAnimator(View view) {
		long totalMillis = 86_400_000L;
		ValueAnimator animator = new ValueAnimator();
		animator.setDuration(totalMillis);
		animator.setObjectValues(0L, totalMillis);
		animator.setEvaluator((TypeEvaluator<Long>) (fraction, startValue, endValue) -> (long)(fraction*totalMillis));
		animator.addUpdateListener(animation -> view.postInvalidate((int)lastX, lastY, (int)lastX + lastWidth + 1, lastY + lastHeight));
		animator.start();
		return animator;
	}
	
	@Override
	public int getSize(@NonNull Paint paint,
			CharSequence text,
			int start,
			int end,
			@Nullable Paint.FontMetricsInt fm) {
		if (paint.equals(lastPaint) &&  lastWidth > 0) {
			return lastWidth;
		}
		float max = 0;
		for (String wideString : WIDE_STRINGS) {
			float width = paint.measureText(wideString);
			if (width > max) max = width;
		}
		lastPaint = paint;
		lastWidth = (int) Math.ceil(max);
		lastHeight = fm.bottom - fm.top;
		return lastWidth;
	}

	@Override
	public void draw(@NonNull Canvas canvas,
			CharSequence text,
			int start,
			int end,
			float x,
			int top,
			int y,
			int bottom,
			@NonNull Paint paint) {
		IFloat32ExpL time = clock.getEstimatedTime(firstTime, (Long) animator.getAnimatedValue());
		Polynomials.at(polynomial, time, lastDisplay, temp1, temp2);
		stringBuilder.setLength(0);
		lastX = x;
		lastY = y;
		lastDisplay.toString(stringBuilder, params);
		canvas.drawText(stringBuilder, 0, stringBuilder.length(), x, y, paint);
	}
}
