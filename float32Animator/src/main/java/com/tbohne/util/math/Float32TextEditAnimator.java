package com.tbohne.util.math;

import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.TextView;

import com.tbohne.util.math.Float32AnimatedTextSpan.PolynomialClock;

import java.util.List;

public class Float32TextEditAnimator {
	private final TextView textView;
	private final PolynomialClock clock;
	private final Float32Animation animation;

	@Nullable private List<? extends IFloat32ExpL> polynomial;

	public Float32TextEditAnimator(TextView textView, PolynomialClock clock) {
		this.textView = textView;
		this.clock = clock;
		animation = new Float32Animation();
	}

	public void setPolynomial(List<? extends IFloat32ExpL> polynomial) {
		if (!Looper.getMainLooper().equals(Looper.myLooper())) {
			throw new IllegalStateException("setPolynomial must be called on main thread");
		}
		this.polynomial = polynomial;
		animation.cancel();
		if (polynomial.size() > 1) {
			animation.reset();
			textView.setAnimation(animation);
			animation.start();
			if (!animationsEnabled()) {

			}
		}
	}

	private boolean animationsEnabled() {
		try {
			return Settings.System.getLong(textView.getContext().getContentResolver(), Settings.System.WINDOW_ANIMATION_SCALE) == 0;
		} catch (Settings.SettingNotFoundException e) {
			return true;
		}
	}

	private static class NoOpInterpolator implements Interpolator {
		@Override
		public float getInterpolation(float input) {
			return input;
		}
	}

	private class Float32Animation extends Animation {
		private static final long TOTAL_MILLIS = 3_600_000L;
		private final long firstTime;
		private final Float32ExpL displayValue = new Float32ExpL();
		private final StringBuilder stringBuilder = new StringBuilder();
		private Float32ExpL displayTime = new Float32ExpL();

		Float32Animation() {
			setRepeatCount(Animation.INFINITE);
			setDuration(TOTAL_MILLIS);
			setInterpolator(new NoOpInterpolator());
			firstTime = clock.getTime();
		}

		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			long estimatedMillis = (long)(interpolatedTime*TOTAL_MILLIS);
			displayTime.set(clock.getEstimatedTime(firstTime, estimatedMillis));
			Polynomials.at(polynomial, displayTime, displayValue);
			stringBuilder.setLength(0);
			displayValue.appendString(stringBuilder);
			textView.setText(stringBuilder);
		}
	}
}
