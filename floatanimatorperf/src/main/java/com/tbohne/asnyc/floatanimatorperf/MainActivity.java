package com.tbohne.asnyc.floatanimatorperf;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tbohne.util.math.Float32Animator;
import com.tbohne.util.math.Float32ExpL;
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

	private static void initPolynomial(int index, ImmutableFloat32ExpL initTime, List<Float32ExpL> polynomial, Float32ExpL temp) {
		for(int i=0; i<=index; i++) {
			polynomial.get(i).set(ImmutableFloat32ExpL.ONE);
		}
		for(int i=index+1; i<NUM_ROWS; i++) {
			polynomial.get(i).set(ImmutableFloat32ExpL.ZERO);
		}
		polynomial.get(0).set(initTime).negate().add(index);
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		// each data item is just a string in this case
		public final List<Float32ExpL> polynomial;
		public final TextView textView;
		public final Float32Animator animator;

		public ViewHolder(TextView v) {
			super(v);
			textView = v;
			animator = new Float32Animator(textView);
			polynomial = new ArrayList<>(NUM_ROWS);
			for(int i=0; i<NUM_ROWS; i++)
				polynomial.add(new Float32ExpL(ImmutableFloat32ExpL.ZERO));
		}
	}

	static class Adapter extends RecyclerView.Adapter<ViewHolder> {
		ImmutableFloat32ExpL initTime = new ImmutableFloat32ExpL(System.currentTimeMillis());
		Float32ExpL temp = new Float32ExpL();

		@NonNull
		@Override
		public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
			TextView v = new TextView(parent.getContext());
			v.setSingleLine();
			v.setWidth(500);
			v.setHeight(150);
			return new ViewHolder(v);
		}

		@Override
		public void onBindViewHolder(@NonNull ViewHolder viewHolder, int index) {
			initPolynomial(index, initTime, viewHolder.polynomial, temp);
			viewHolder.animator.setPolynomial(viewHolder.polynomial);
		}

		@Override
		public int getItemCount() {
			return NUM_ROWS;
		}
	}
}
