
package com.customcamera.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class CropImageView extends ImageView {

	static final int NONE = 0;// No Action
	static final int DRAG = 1; // Action Dragging
	static final int ZOOM = 2; // Action Zooming
	static final int BIGGER = 3; // ZoomIn Image
	static final int SMALLER = 4; // ZoomOutImage
	private int mode = NONE; // Initial ImageView Mode as NoAction

	private float beforeLenght; // Distance of Two Finger points at the Begin
	private float afterLenght; // Distance of Two Finger points at the End
	private float scale = 0.04f; // Scale Factor

	private int screenW;// Area of Moving
	private int screenH;

	private int start_x;// Start Touch Point x, y
	private int start_y;
	private int stop_x;// End Touch Point x, y
	private int stop_y;

	private TranslateAnimation trans; // Bounding Animation
	private static final int transDuration = 500; // Animation Duration
	private Matrix mNewMatrix; // NewUpdated ImageView Matrix

	private Bitmap mOriginImage; // Original ImageView Bitmap

	public CropImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CropImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CropImageView(Context context) {
		super(context);
	}

	public CropImageView(Context context, int w, int h)
	{
		super(context);
		this.setPadding(0, 0, 0, 0);
		screenW = w;
		screenH = h;
	}

	/**
	 * Calculate distance between two finger points
	 * 
	 * @param event
	 * @return
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {// MotionEvent.ACTION_MASK is multiple touch
			case MotionEvent.ACTION_DOWN:
				mode = DRAG;
				// Get LastTime Stopped point x, y
				stop_x = (int) event.getRawX();
				stop_y = (int) event.getRawY();
				// Corresponding to the ImageView Left/Top point
				start_x = stop_x - this.getLeft();
				start_y = stop_y - this.getTop();

				if (event.getPointerCount() == 2)
					beforeLenght = spacing(event);
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				if (spacing(event) > 10f) {
					mode = ZOOM;
					beforeLenght = spacing(event);
				}
				break;
			case MotionEvent.ACTION_UP:
				int disX = 0;
				int disY = 0;
				if (getHeight() <= screenH){
					if (this.getTop() < 0) {
						disY = getTop();
						this.layout(this.getLeft(), 0, this.getRight(), 0 + this.getHeight()); // Reset the ImageView Layout Position
					}else if (this.getBottom() >= screenH) {
						disY = getHeight() - screenH + getTop();
						this.layout(this.getLeft(), screenH - getHeight(), this.getRight(), screenH); // Reset the ImageView Layout Position
					}
				}else {
					int Y1 = getTop();
					int Y2 = getHeight() - screenH + getTop();
					if (Y1 > 0) {
						disY = Y1;
						this.layout(this.getLeft(), 0, this.getRight(), 0 + this.getHeight());
					}
					else if (Y2 < 0) {
						disY = Y2;
						this.layout(this.getLeft(), screenH - getHeight(), this.getRight(), screenH);
					}
				}

				if (getWidth() <= screenW) {
					if (this.getLeft() < 0) {
						disX = getLeft();
						this.layout(0, this.getTop(), 0 + getWidth(), this.getBottom());
					}
					else if (this.getRight() > screenW) {
						disX = getWidth() - screenW + getLeft();
						this.layout(screenW - getWidth(), this.getTop(), screenW, this.getBottom());
					}
				}
				else {
					int X1 = getLeft();
					int X2 = getWidth() - screenW + getLeft();
					if (X1 > 0) {
						disX = X1;
						this.layout(0, this.getTop(), 0 + getWidth(), this.getBottom());
					}
					else if (X2 < 0) {
						disX = X2;
						this.layout(screenW - getWidth(), this.getTop(), screenW, this.getBottom());
					}

				}
				// if current ImageView Height/Width is less the MINIMUN value, need to scale it until reach the MINIMUN
				while (getHeight() < 100 || getWidth() < 100) {
					setScale(scale, BIGGER);
				}
				// Start the Transmission Animation according to the offset
				if (disX != 0 || disY != 0) {
					trans = new TranslateAnimation(disX, 0, disY, 0);
					trans.setDuration(transDuration);
					this.startAnimation(trans);
				}
				mode = NONE;
				break;
			case MotionEvent.ACTION_POINTER_UP:
				mode = NONE;
				break;
			case MotionEvent.ACTION_MOVE:
				if (mode == DRAG) {
					// Keep updating ImageView Position and Layout frame when Dragging
					this.setPosition(stop_x - start_x, stop_y - start_y, stop_x + this.getWidth() - start_x, stop_y
							- start_y + this.getHeight());
					stop_x = (int) event.getRawX();
					stop_y = (int) event.getRawY();

				}
				else if (mode == ZOOM) {
					if (spacing(event) > 10f) {
						afterLenght = spacing(event);
						float gapLenght = afterLenght - beforeLenght;
						if (gapLenght == 0) {
							break;
						}
						// If the distance of zoom has been changed 5 pix, and also the width of ImageView is larger than 70
						// Start the ZoomIn/Out
						else if (Math.abs(gapLenght) > 5f && getWidth() > 70) {
							if (gapLenght > 0) {
								this.setScale(scale, BIGGER);
							}
							else {
								this.setScale(scale, SMALLER);
							}
							beforeLenght = afterLenght; // Reassign current distance between two finger points
						}
					}
				}
				break;
		}
		invalidate();
		return true;
	}

	/**
	 * Set Current Rescaled Image Frame
	 * And reset ImageFrame
	 * @param temp
	 * @param flag
	 */
	private void setScale(float temp, int flag) {
		if (flag == BIGGER) {
			// ZoomIn/Enlarge ImageImage
			this.setFrame(this.getLeft() - (int) (temp * this.getWidth()),
					this.getTop() - (int) (temp * this.getHeight()), this.getRight() + (int) (temp * this.getWidth()),
					this.getBottom() + (int) (temp * this.getHeight()));
		}else if (flag == SMALLER) {
			// ZoomOut/Narrow ImageView
			this.setFrame(this.getLeft() + (int) (temp * this.getWidth()),
					this.getTop() + (int) (temp * this.getHeight()), this.getRight() - (int) (temp * this.getWidth()),
					this.getBottom() - (int) (temp * this.getHeight()));
		}
		mNewMatrix = this.getImageMatrix();		// Get New Updated Matrix of ImageView
	}

	/**
	 * Reset ImageView Position and Frame Layout
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	private void setPosition(int left, int top, int right, int bottom) {
		this.layout(left, top, right, bottom);
	}

	/**
	 * Get CropImage according to the frameView
	 * 
	 * @param v
	 * @return
	 */
	public Bitmap getCropImage(View frameView) {
		Bitmap finalBitmap = null;
		float[] cood = CropFrameView.getCropFrameCoordinates();
		setDrawingCacheEnabled(true);
		buildDrawingCache(true);

		mOriginImage = null; // Clear OriginImage
		mOriginImage = ((BitmapDrawable) this.getDrawable()).getBitmap(); // Get Origin Bitmap from ImageView
		// Each ImageView has 4 point-Left,Right,Top,Bottom, which the CropFrame also has 4 points
		// We don't want the ImageView is inside the CropFrame, which means the 4 points of the ImageView must be >=
		// CropFrame's 4 points, otherwise, we don't crop the ImageView
		int cropLeft = (int) cood[0] - this.getLeft();
		int cropTop = (int) cood[1] - this.getTop();
		int cropRight = (int) cood[2] - this.getRight();
		int cropBottom = (int) cood[3] - this.getBottom();
		if (cropLeft < 0 || cropTop < 0 || cropRight > 0 || cropBottom > 0)
			return mOriginImage;

		finalBitmap = Bitmap.createBitmap(mOriginImage, 0, 0, mOriginImage.getWidth(), mOriginImage.getHeight(), mNewMatrix, true); // Recreate resized or transmitted Image with Matrix
		finalBitmap = Bitmap.createBitmap(finalBitmap, cropLeft, cropTop, (int) cood[2] - (int) cood[0], (int) cood[3] - (int) cood[1]); // Recreate cropped Bitmap according to
																				// the CropFrame and ImageViewPosition

		Log.d("Point", "Left:" + this.getLeft() + ":right:" + this.getRight() + ":Top:" + this.getTop() + ":Bottom:"+ this.getBottom());
		Log.d("CropPoint", "x:" + ((int) cood[0] - this.getLeft()) + ":y:" + ((int) cood[1] - this.getTop()) + ":width:" + ((int) cood[2] - (int) cood[0]) + ":Height:" + ((int) cood[3] - (int) cood[1]));
		return finalBitmap;

	}

}
