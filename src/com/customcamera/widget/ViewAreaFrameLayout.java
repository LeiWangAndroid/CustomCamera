package com.customcamera.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.FrameLayout;

public class ViewAreaFrameLayout extends FrameLayout {

	private int imgDisplayW;
	private int imgDisplayH;
	private int imgW;
	private int imgH;
	private CropImageView mCropImageView;
	private CropFrameView mCropFrameView;
	
	public ViewAreaFrameLayout(Context context) {
		super(context);
	}
	public ViewAreaFrameLayout(Context context, Bitmap originImage) {
		super(context);
		// Get Screen Display Width and Height
		imgDisplayW = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
		imgDisplayH = ((Activity) context).getWindowManager().getDefaultDisplay().getHeight();

		mCropImageView = new CropImageView(context, imgDisplayW, imgDisplayH);	// Initial CropImageView
		mCropImageView.setImageBitmap(originImage);
		mCropImageView.setDrawingCacheEnabled(true);
		imgW = originImage.getWidth();
		imgH = originImage.getHeight();
		// Initial Orientation according to ImageSize
		int layout_w = imgW > imgDisplayW ? imgDisplayW : imgW;
		int layout_h = imgH > imgDisplayH ? imgDisplayH : imgH;

		//If the Image Width > Height && image Width > ScreenDisplay Width, we need to compress the image Height according to Ratio of width
		//otherwise, we need to compress the image Width according to Ratio of height
		if (imgW >= imgH) {
			if (layout_w == imgDisplayW) {
				layout_h = (int) (imgH * ((float) imgDisplayW / imgW));
			}
		}else {
			if (layout_h == imgDisplayH) {
				layout_w = (int) (imgW * ((float) imgDisplayH / imgH));
			}
		}
		mCropImageView.setLayoutParams(new FrameLayout.LayoutParams(layout_w, layout_h));// Set CropImageView layout, and the touchable View layout
		
		// Initial CropFrameView
		mCropFrameView = new CropFrameView(context);
		mCropFrameView.setLayoutParams(new FrameLayout.LayoutParams(imgDisplayW, imgDisplayH));// set CropFrameView Layout
		this.addView(mCropImageView);
		this.addView(mCropFrameView);
	}

	/**
	 * Get Cropped Image
	 * @return
	 */
	public Bitmap getCroppedImage() {
		return mCropImageView.getCropImage(mCropFrameView);
	}

}
