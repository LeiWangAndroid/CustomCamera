package com.customcamera.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.customcamera.R;


@SuppressLint("DrawAllocation")
public class CropFrameView extends View{

	public static int STROKE_WIDTH = 3;
	private static float centerX, centerY;
	private static float FRAME_HALF_WIDTH, FRAME_HALF_HEIGHT;
	public CropFrameView(Context context) {
		super(context);
		centerX = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth() / 2 ;
		centerY = ((Activity) context).getWindowManager().getDefaultDisplay().getHeight() / 2;
		
		float density = context.getResources().getDisplayMetrics().density;
		FRAME_HALF_WIDTH = context.getResources().getDimension(R.dimen.crop_frame_width_half) * density;
		FRAME_HALF_HEIGHT = context.getResources().getDimension(R.dimen.crop_frame_height_half) * density;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint myPaint = new Paint();
		myPaint.setColor(Color.GREEN);
		myPaint.setStyle(Paint.Style.STROKE);
		myPaint.setStrokeWidth(STROKE_WIDTH);

		canvas.drawRect(centerX - FRAME_HALF_WIDTH, centerY - FRAME_HALF_HEIGHT, centerX + FRAME_HALF_WIDTH, centerY + FRAME_HALF_HEIGHT, myPaint);
	}
	
	public static float[] getCropFrameCoordinates(){
		float[] coord = new float[]{centerX - FRAME_HALF_WIDTH, centerY - FRAME_HALF_HEIGHT, centerX + FRAME_HALF_WIDTH, centerY + FRAME_HALF_HEIGHT};
		return coord;
	}
}
