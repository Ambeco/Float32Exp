package com.tbohne.util.math;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ReplacementSpan;
import android.view.View;

import java.util.List;

public class Float32AnimatedTextSpan extends ReplacementSpan {
	//This is called within the animation so needs to be _really_ fast and not allocate objects,
	//which may cause a GC in the main thread causing stutter.
	public interface DrawableClock {
		IFloat32ExpL getTime();
	}

	public static SpannableString createFloat32AnimatedSpan(List<? extends IFloat32ExpL> polynomial, View view,
			DrawableClock clock) {
		//TODO Pass toString params as object
		//TODO: Appendable instead of StringBuilder.
		StringBuilder stringBuilder = new StringBuilder();
		Float32ExpL displayValue = new Float32ExpL();
		Polynomials.at(polynomial, clock.getTime(), displayValue);
		displayValue.toString(stringBuilder);
		SpannableString string = new SpannableString(stringBuilder);
		string.setSpan(new Float32AnimatedTextSpan(polynomial, view, clock), 0, stringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return string;
	}

	public static SpannableStringBuilder appendFloat32AnimatedSpan(SpannableStringBuilder builder,
			List<? extends IFloat32ExpL> polynomial, View view,
			DrawableClock clock) {
		//TODO Pass toString params as object
		//TODO: Appendable instead of StringBuilder.
		StringBuilder stringBuilder = new StringBuilder();
		Float32ExpL displayValue = new Float32ExpL();
		Polynomials.at(polynomial, clock.getTime(), displayValue);
		displayValue.toString(stringBuilder);
		int offset = builder.length();
		builder.append(stringBuilder);
		builder.setSpan(new Float32AnimatedTextSpan(polynomial, view, clock), offset, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
	private final DrawableClock clock;
	private final StringBuilder stringBuilder = new StringBuilder();
	private final ValueAnimator animator = new ValueAnimator();
	
	private @Nullable Paint lastPaint;
	private float lastX = 0;
	private int lastY = 0;
	private int lastWidth = 0;
	private int lastHeight = 0;
	private final Float32ExpL lastDisplay = new Float32ExpL();
	
	protected Float32AnimatedTextSpan(List<? extends IFloat32ExpL> polynomial, View view, DrawableClock clock) {
		this.polynomial = Polynomials.toImmutable(polynomial);
		this.clock = clock;
		initAnimator(view);
	}

	private void initAnimator(View view) {
		animator.setDuration(360000);
		//TODO: Test Removal
		animator.setObjectValues("Ecl pse", "Eclipse");
		animator.setEvaluator(new TypeEvaluator<CharSequence>()
		{
			@Override
			public CharSequence evaluate(float fraction, CharSequence startValue, CharSequence endValue)
			{
				return startValue;
			}
		});

		animator.addUpdateListener(new AnimatorUpdateListener()
		{
			@Override
			public void onAnimationUpdate(ValueAnimator animation)
			{
				view.postInvalidate((int)lastX, lastY, (int)lastX + lastWidth + 1, lastY + lastHeight);
			}
		});
		animator.start();
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
		Polynomials.at(polynomial, clock.getTime(), lastDisplay);
		stringBuilder.setLength(0);
		lastX = x;
		lastY = y;
		lastDisplay.toString(stringBuilder);
		canvas.drawText(stringBuilder, 0, stringBuilder.length(), x, y, paint);
	}
}
