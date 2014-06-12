package ro.mihaisurdeanu.redfury.views;

import ro.mihaisurdeanu.redfury.R;
import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

/**
 * Clasa se afiseaza un logo mic in partea dreapta-jos a fragmentului principal.
 * 
 * @author		: Mihai Surdeanu
 * @version		: 1.0.0
 * 
 * =====================
 * Lista de modificari :
 * =====================
 * 		[1.0.0] : Versiune initiala.
 */
public class LogoView extends View {
	private static final ArgbEvaluator ARGB_Evaluator = new ArgbEvaluator();
	private Canvas backgroundCanvas = new Canvas();
	private Bitmap background, mask = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
	private Paint paint = new Paint();
	private Rect drawingRect;
	private boolean direction;
	private long lastChange;
	/** Un ciclu va cuprinde fix 5 secunde. */
	protected int duration = 5000;

	public LogoView(Context context) {
		super(context);
	}

	public LogoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LogoView(Context context, AttributeSet attrs, int definitionStyle) {
		super(context, attrs, definitionStyle);
	}

	@Override
	protected void onSizeChanged(int width,
								 int height,
								 int oldWidth,
								 int oldHeight) {
		super.onSizeChanged(width, height, oldWidth, oldHeight);

		drawingRect = new Rect(0, 0, width, height);

		Bitmap bitMask = convertToAlphaMask(Bitmap.createScaledBitmap(mask, width, height, false));
		paint.setShader(new BitmapShader(bitMask, Shader.TileMode.MIRROR, Shader.TileMode.MIRROR));

		background = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
		backgroundCanvas.setBitmap(background);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		long currentTime = SystemClock.uptimeMillis();
		int difference = (int) (currentTime - lastChange);
		// S-a incheiat un ciclu? Vom schimba sensul acestuia?
		if (difference > duration) {
			direction = !direction;
			lastChange = currentTime;
		}

		// Calculeaza procentul din intervalul de culoare ce se va atribui.
		int remaining = difference % duration;
		float percent = (direction ? remaining : duration - remaining) / (float) duration;

		// La fiecare moment de timp culoarea de fundal va fi intre valorile
		// 0xff8e8000 si 0xff8ea0ff in functie de numarul de millisecunde trecute.
		paint.setColor((Integer) ARGB_Evaluator.evaluate(percent, 0xff8e8000, 0xff8ea0ff));

		// Logo-ul se va desena cu o noua culoare.
		backgroundCanvas.drawRect(drawingRect, paint);
		// Se deseneaza bitmap-ul intern.
		canvas.drawBitmap(background, 0, 0, paint);

		// Se face redesenarea propriu-zisa.
		invalidate();
	}

	protected static Bitmap convertToAlphaMask(Bitmap b) {
		final Bitmap a = Bitmap.createBitmap(b.getWidth(), b.getHeight(),
				Bitmap.Config.ALPHA_8);
		final Canvas c = new Canvas(a);
		c.drawBitmap(b, 0.0f, 0.0f, null);
		return a;
	}
}
