package com.tbohne.asnyc.floatanimatorperf;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tbohne.util.math.Float32AnimatedDrawableSpan;
import com.tbohne.util.math.Float32AnimatedTextSpan;
import com.tbohne.util.math.Float32ExpL;
import com.tbohne.util.math.IFloat32ExpL;
import com.tbohne.util.math.ImmutableFloat32ExpL;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
	private static final int NUM_ROWS = 30;

	RecyclerView recyclerView ;
	Adapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		recyclerView  = findViewById(R.id.list);
		recyclerView.setHasFixedSize(true);
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(layoutManager);
		mAdapter = new Adapter();
		recyclerView.setAdapter(mAdapter);
	}

	public void launchToStringThread(View view) {
		Runnable runnable = () -> {
			int hash = 0;
			Float32ExpL num = new Float32ExpL(9);
			StringBuilder sb = new StringBuilder(30);
			for(int i=0; i<10_000_000; i++) {
				sb.setLength(0);
				num.toString(sb);
				hash += sb.hashCode();
			}
			Log.v("Float", "hash result is " + hash);
		};
		new Thread(runnable).start();
	}

	private static void initPolynomial(int index, List<Float32ExpL> polynomial, Float32ExpL temp) {
		for(int i=1; i<=index; i++) {
			polynomial.get(i).set(ImmutableFloat32ExpL.ONE).divide(1000);
		}
		for(int i=index+1; i<NUM_ROWS; i++) {
			polynomial.get(i).set(ImmutableFloat32ExpL.ZERO);
		}
		polynomial.get(0).set(1);
	}

	private static long initTime = System.currentTimeMillis();
	public static Float32AnimatedTextSpan.DrawableClock drawableClock = new Float32AnimatedTextSpan.DrawableClock() {
		Float32ExpL time = new Float32ExpL();
		@Override
		public IFloat32ExpL getTime() {
			time.set(System.currentTimeMillis()-initTime);
			return time;
		}
	};

	public static class ViewHolder extends RecyclerView.ViewHolder {
		// each data item is just a string in this case
		public final List<Float32ExpL> polynomial;
		public final TextView textView;

		public ViewHolder(TextView v) {
			super(v);
			polynomial = new ArrayList<>(NUM_ROWS);
			for(int i=0; i<NUM_ROWS; i++)
				polynomial.add(new Float32ExpL(ImmutableFloat32ExpL.ZERO));
			textView = v;
		}
	}

	static class Adapter extends RecyclerView.Adapter<ViewHolder> {
		Float32ExpL temp = new Float32ExpL();

		@NonNull
		@Override
		public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
			TextView v = new TextView(parent.getContext());
			v.setSingleLine();
			v.setWidth(1000);
			v.setPadding(16, 16, 16, 16);
			v.setTextColor(Color.BLACK);
			return new ViewHolder(v);
		}

		@Override
		public void onBindViewHolder(@NonNull ViewHolder viewHolder, int index) {
			initPolynomial(index, viewHolder.polynomial, temp);
			SpannableStringBuilder builder = new SpannableStringBuilder();
			builder.append("BEGIN >");
			if (false) {
				Float32AnimatedTextSpan.appendFloat32AnimatedSpan(builder,
						viewHolder.polynomial,
						viewHolder.textView,
						drawableClock);
			} else {
				Float32AnimatedDrawableSpan.appendFloat32AnimatedSpan(builder,
						viewHolder.polynomial,
						viewHolder.textView,
						drawableClock);
			}
			builder.append("< END");
			viewHolder.textView.setText(builder);
		}

		@Override
		public int getItemCount() {
			return NUM_ROWS;
		}
	}
}
