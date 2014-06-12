package ro.mihaisurdeanu.redfury.fragments;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import rajawali.RajawaliFragment;
import rajawali.renderer.RajawaliRenderer;
import ro.mihaisurdeanu.redfury.R;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

public abstract class AbstractFragment extends RajawaliFragment implements OnClickListener {
	protected RajawaliRenderer renderer;
	protected ProgressBar progressBarLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (isTransparentSurfaceView())
			setGLBackgroundTransparent(true);

		renderer = createRenderer();
		if (renderer == null)
			renderer = new NullRenderer(getActivity());

		renderer.setSurfaceView(mSurfaceView);
		setRenderer(renderer);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mLayout = (FrameLayout) inflater.inflate(R.layout.rajawali_fragment,
				container, false);

		mLayout.addView(mSurfaceView);

		mLayout.findViewById(R.id.relative_layout_loader_container)
				.bringToFront();

		progressBarLoader = (ProgressBar) mLayout.findViewById(R.id.progress_bar_loader);
		progressBarLoader.setVisibility(View.GONE);

		return mLayout;
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		if (mLayout != null)
			mLayout.removeView(mSurfaceView);
	}

	@Override
	public void onDestroy() {
		try {
			super.onDestroy();
		} catch (Exception e) {
		}
		renderer.onSurfaceDestroyed();
	}

	/**
	 * Creaza un renderer ce va fi folosdit de catre fragment pentru redarea
	 * scenelor 3D. Daca metoda va returna NULL atunci nu se va crea niciun renderer.
	 */
	protected abstract FragmentRenderer createRenderer();

	protected void hideLoader() {
		progressBarLoader.post(new Runnable() {
			@Override
			public void run() {
				progressBarLoader.setVisibility(View.GONE);
			}
		});
	}

	protected boolean isTransparentSurfaceView() {
		return false;
	}

	protected void showLoader() {
		progressBarLoader.post(new Runnable() {
			@Override
			public void run() {
				progressBarLoader.setVisibility(View.VISIBLE);
			}
		});
	}

	protected abstract class FragmentRenderer extends RajawaliRenderer {

		public FragmentRenderer(Context context) {
			super(context);
			// Desenarea se va face mereu la 60 de frame-uri pe secunda.
			setFrameRate(60);
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			showLoader();
			super.onSurfaceCreated(gl, config);
			hideLoader();
		}

	}

	private static final class NullRenderer extends RajawaliRenderer {

		public NullRenderer(Context context) {
			super(context);
		}

		@Override
		public void onSurfaceDestroyed() {
			stopRendering();
		}
	}

}
