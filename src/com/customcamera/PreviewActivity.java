
package com.customcamera;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.customcamera.utils.ImageUtils;
import com.customcamera.widget.ViewAreaFrameLayout;

public class PreviewActivity extends Activity implements View.OnClickListener{

	public static Button backButton, retakeButton, saveButton;
	public static ImageView cameraImage;
	
	private LinearLayout mViewAreaLinerarLayout;
	private LinearLayout.LayoutParams mViewAreaLinearLayoutParams;
	private ViewAreaFrameLayout mViewArea;

	Button mTakePhotoButton;
	private String TAG = "-PreviewActivity-";
	private static final int MAX_DETECTED_FACE = 10;
	private static final int imageResize = 1080;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview);

		// Image
		cameraImage = (ImageView) findViewById(R.id.cameraImage);
		
		// Retake Button
		retakeButton = (Button) findViewById(R.id.recapture_button);
		retakeButton.setOnClickListener(this);
		
		saveButton = (Button) findViewById(R.id.save_button);
		saveButton.setOnClickListener(this);

		backButton = (Button) findViewById(R.id.back);
		backButton.setOnClickListener(this);

		showImage();
	}
	
	private void showImage(){
		Bitmap myBitmap = null;
		if(getIntent().getBooleanExtra("isGallery", false)){
			myBitmap = this.imageFromGallery(getIntent().getStringExtra("filePath"));
			int orientation = getIntent().getIntExtra("orientation", 0);
			myBitmap = ImageUtils.RotateImage(myBitmap, orientation);
		}else{
			Bundle bundle = getIntent().getBundleExtra("data");
			byte[] pictureData = bundle.getByteArray("pictureData");
			if (pictureData != null) {
				BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
				bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
				myBitmap = BitmapFactory.decodeByteArray(pictureData, 0, pictureData.length, bitmapFatoryOptions);
			}
			else {
				Log.v(TAG, "picture data == null");
			}
		}
		
		if(myBitmap != null){

			mViewAreaLinerarLayout = (LinearLayout) findViewById(R.id.ll_viewArea);
			mViewAreaLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
			mViewArea = new ViewAreaFrameLayout(PreviewActivity.this, myBitmap);
			mViewAreaLinerarLayout.addView(mViewArea,mViewAreaLinearLayoutParams);
			detectFaceByImage(myBitmap);
	//		cameraImage.setImageBitmap(BitmapFactory.decodeByteArray(pictureData, 0, pictureData.length));
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Toast.makeText(this, "PreviewActivity-onConfigurationChanged", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onConfigurationChanged");

	}
	
	/**
	 * Detect Num of Faces by Bitmap
	 * @param image
	 */
	private void detectFaceByImage(Bitmap image){
		int width = image.getWidth();
		int height = image.getHeight();
		Face[] detectedFaces = new FaceDetector.Face[MAX_DETECTED_FACE];
		FaceDetector faceDetector = new FaceDetector(width, height, MAX_DETECTED_FACE);
		int NUMBER_OF_FACE_DETECTED = faceDetector.findFaces(image, detectedFaces);
		Log.v(TAG, "detected = " + NUMBER_OF_FACE_DETECTED);
		
		Toast.makeText(PreviewActivity.this, NUMBER_OF_FACE_DETECTED + " face has been detected.", Toast.LENGTH_LONG).show();
	}

	/**
     * Image result from gallery
     * @param resultCode
     * @param data
     */
    private Bitmap imageFromGallery(String filePath) {
    	final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		options.inSampleSize = calculateInSampleSize(options, imageResize, imageResize);
		options.inJustDecodeBounds = false;
    	return BitmapFactory.decodeFile(filePath, options);
    }

	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			}
			else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

//	private static Bitmap decodeSampledBitmapFromResource(String pathName, int reqWidth, int reqHeight) {
//
//		final BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inJustDecodeBounds = true;
//		BitmapFactory.decodeFile(pathName, options);
//
//		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//		options.inJustDecodeBounds = false;
//		return BitmapFactory.decodeFile(pathName, options);
//	}
    
    /**
     * Remove Photo
     * @param contentUri
     */
	public void removePhoto(Uri contentUri) {
		getContentResolver().delete(contentUri, null, null);
	}
	
	private String path;

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putString("path", path);

		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		if (savedInstanceState != null) {
			path = savedInstanceState.getString("path");
//			imageView.setImageBitmap(decodeSampledBitmapFromResource(path, 100, 100));
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch(v.getId()){
			case R.id.recapture_button:
				intent = new Intent(PreviewActivity.this, CameraActivity.class);
				startActivity(intent);
				break;
			case R.id.save_button:
				cameraImage.setImageBitmap(mViewArea.getCroppedImage());
				break;
			case R.id.back:
				intent = new Intent(PreviewActivity.this, PhotoFilterActivity.class);
				startActivity(intent);
				break;
		}
		
	}
}
