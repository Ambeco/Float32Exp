package com.tbohne.util.math;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ImageSpan;
import android.view.View;

import com.tbohne.util.math.Float32AnimatedTextSpan.PolynomialClock;
import com.tbohne.util.math.IFloat32ExpL.StringFormatParams;

import java.util.List;

import static com.tbohne.util.math.Float32ExpLHelpers.DEFAULT_STRING_PARAMS;

public class Float32AnimatedDrawableSpan extends ImageSpan {
	public static SpannableString createFloat32AnimatedSpan(List<? extends IFloat32ExpL> polynomial,
			View view,
			PolynomialClock clock) {
		return createFloat32AnimatedSpan(polynomial, DEFAULT_STRING_PARAMS, view, clock);
	}
	public static SpannableString createFloat32AnimatedSpan(List<? extends IFloat32ExpL> polynomial,
			StringFormatParams params,
			View view,
			PolynomialClock clock) {
		//TODO: Appendable instead of StringBuilder.
		StringBuilder stringBuilder = new StringBuilder();
		Float32ExpL displayValue = new Float32ExpL();
		Polynomials.at(polynomial, clock.getTime(), displayValue);
		displayValue.toString(stringBuilder, params);
		SpannableString string = new SpannableString(stringBuilder);
		string.setSpan(new Float32AnimatedDrawableSpan(polynomial, params, view, clock), 0, stringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
		Polynomials.at(polynomial, clock.getTime(), displayValue);
		displayValue.toString(stringBuilder, params);
		int offset = builder.length();
		builder.append(stringBuilder);
		builder.setSpan(new Float32AnimatedDrawableSpan(polynomial, params, view, clock), offset, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return builder;
	}

	private final Float32AnimatedDrawable drawable;

	public Float32AnimatedDrawableSpan(List<? extends IFloat32ExpL> polynomial, StringFormatParams params, View view, PolynomialClock clock) {
		this(polynomial, params, view, clock, new TextPaint());
	}

	public Float32AnimatedDrawableSpan(List<? extends IFloat32ExpL> polynomial, StringFormatParams params, View view, PolynomialClock clock, TextPaint textPaint) {
		super(new Float32AnimatedDrawable(polynomial, view, params, clock, textPaint));
		this.drawable = (Float32AnimatedDrawable) getDrawable();
	}

	@Override
	public void updateDrawState(@NonNull TextPaint tp) {
		drawable.setPaint(tp);
		super.updateDrawState(tp);
	}

	@Override
	public void draw(@NonNull Canvas canvas, CharSequence text, @IntRange(from = 0) int start, @IntRange(from = 0) int end, float x,
			int top, int y, int bottom, @NonNull Paint paint) {
		drawable.setPaint(paint);
		super.draw(canvas, text, start, end, x, top, y, bottom, paint);
	}
}
