package com.tbohne.util.math;

import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.TextView;

import java.util.List;

//TODO: Figure out why this only animates about 2x/second
//TODO: Figure out if animating a span is more performant
public class Float32Animator {
	final private TextView textView;
	final private StringBuilder stringBuilder = new StringBuilder();
	private final Float32Animation animation = new Float32Animation();

	@Nullable private List<? extends IFloat32ExpL> polynomial;

	public Float32Animator(TextView textView) {
		this.textView = textView;
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
		private Float32ExpL time = new Float32ExpL();
		private Float32ExpL displayValue = new Float32ExpL();
		private StringBuilder stringBuilder = new StringBuilder();

		Float32Animation() {
			setRepeatCount(Animation.INFINITE);
			setDuration(3000);
			setInterpolator(new NoOpInterpolator());
		}

		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			time.set(System.currentTimeMillis());
			Polynomials.at(polynomial, time, displayValue);
			stringBuilder.setLength(0);
			displayValue.toString(stringBuilder);
			textView.setText(stringBuilder);
		}
	}
}
