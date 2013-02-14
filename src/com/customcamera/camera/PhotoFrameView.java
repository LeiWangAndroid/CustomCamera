package com.customcamera.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class PhotoFrameView extends View {

	private Rect rect;
	private int frameColor = Color.BLACK;

	// on touch actions
	static final int NONE = 0;
	static final int ZOOM = 1;
	int mode = NONE;

	// Zooming paramsF
	PointF midPoint = new PointF();
	float oldDist = 1f;
	float minDistDiff = 10f;
	int zoomPrc = 0;

	private float screenSize = -1;
	private float dx = -1;

	private float getScreenSize() {
		if (screenSize > 0)
			return screenSize;

		screenSize = (float) Math.sqrt((getWidth() * getWidth())
				+ (getHeight() * getHeight()));

		return screenSize;
	}

	private float getMinDiff() {
		if (dx < 0) {
			dx = (float) (((int) (getScreenSize() - 10f)) / CameraController
					.getController().getMaxZoom());
			dx /= 3;
		
			
			if(dx<10f)
			{				
				CameraController
				.getController().setZoomModifier((int)(10/dx));
				dx = 10f;
			}
		}
		return dx;
	}

	public PhotoFrameView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	/**
	 * we obtain camera controller and draw frame
	 * that indicate area where user should place product cover,
	 * rest of preview is covered with black, a bit transparent layer
	 */
	protected void onDraw(Canvas canvas) {
//		this.rect = CameraController.getController().getFrameCoordinates();
		if (rect == null) {
			return;
		}
		Paint p = new Paint();
		p.setColor(Color.BLACK);
		p.setAlpha(125);
		canvas.drawRect(0, 0, canvas.getWidth(), rect.top, p);
		canvas.drawRect(0, rect.top, rect.left, canvas.getHeight(), p);
		canvas.drawRect(rect.left, rect.bottom, canvas.getWidth(),
				canvas.getHeight(), p);
		canvas.drawRect(rect.right, rect.top, canvas.getWidth(), rect.bottom, p);
		p.setAlpha(0);
		p.setColor(frameColor);
		canvas.drawRect(rect.left, rect.top, rect.right + 1, rect.top + 2, p);
		canvas.drawRect(rect.left, rect.top + 2, rect.left + 2,
				rect.bottom - 1, p);
		canvas.drawRect(rect.right - 1, rect.top, rect.right + 1,
				rect.bottom - 1, p);
		canvas.drawRect(rect.left, rect.bottom - 1, rect.right + 1,
				rect.bottom + 1, p);

	}

	int counter = 0;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if(!CameraController.getController().isZoomSupported()) return true;
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_POINTER_2_UP:
		case MotionEvent.ACTION_UP:
			// clear action after release
			mode = NONE;
			break;
		case MotionEvent.ACTION_POINTER_DOWN: // works like POINTER_1_DOWN - not
												// ANY pointer down..
		case MotionEvent.ACTION_POINTER_2_DOWN: //
			// save reference distance
			oldDist = distance(event);
			Log.d("ZOOM", "pointer down" + oldDist);
			// avoid distance errors
			if (oldDist > 10f) {
				mode = ZOOM;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == ZOOM) {
				float newDist = distance(event);
				float diff = Math.abs(newDist - oldDist);
				// avoid touch err (>10f) && test sensitivity (diff)

				if (newDist > 10f && diff > getMinDiff()) {

					if (newDist > oldDist) {
						CameraController.getController().zoomByRange((int)(diff/getMinDiff()));//.zoomIn();
						counter++;
						Log.d("ZOOM", "+");
					} else {
						CameraController.getController().zoomByRange((int)-(diff/getMinDiff()));//.zoomOut();
						counter--;
						Log.d("ZOOM", "-");
					}
					Log.d("ZOOM", "counter = " + counter);
					oldDist = newDist;

					return true;

				}
			}
		}
		return true;
	}

	private float distance(MotionEvent event) {
		try {
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return FloatMath.sqrt(x * x + y * y);
		} catch (Exception exc) {
			Log.e("ZOOM", exc.getMessage());
			return 0;
		}
	}

	public void redraw(int color, long delayInMilis) {
		frameColor = color;
		postInvalidateDelayed(delayInMilis);
	}

}
