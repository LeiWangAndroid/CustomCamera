
package com.customcamera;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.content.ContentValues;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.customcamera.camera.CameraController;
import com.customcamera.utils.CameraConstants;

public class CameraActivity extends Activity implements SurfaceHolder.Callback, Camera.PictureCallback, OnClickListener {

	private CameraController cameraController;
	private int categoryType;
	private SurfaceHolder holder;
	private SurfaceView previewView;
	private RelativeLayout shootPhotoBar;
	private Button mTakeBtn, mSwitchBtn, mFlashBtn;

	private boolean takingPhoto = false;
	Uri imageFileUri;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_preview);

		categoryType = getIntent().getIntExtra(CameraConstants.CATEGORY_TYPE, 0);
		// frameView = (PhotoFrameView) findViewById(R.id.bookCoverFrame);
		try {
			initCamera(categoryType);
		}
		catch (IOException e) {
			Log.e("onCreate()", "error in initCamera()");
		}

	}

	private void initCamera(int categoryType) throws IOException {
		CameraController.init(CameraActivity.this, categoryType);
		cameraController = CameraController.getController();
//		cameraController.setTaskId(categoryType);
		// cameraLayout.setOnKeyListener(mKeyListener);
		mTakeBtn = (Button) findViewById(R.id.shootPhoto);
		mTakeBtn.setOnClickListener(this);

		mSwitchBtn = (Button) findViewById(R.id.switch_button);
		mSwitchBtn.setOnClickListener(this);
		
		mFlashBtn = (Button) findViewById(R.id.flash_button);
		mFlashBtn.setOnClickListener(this);

		previewView = (SurfaceView) findViewById(R.id.preview);
		holder = previewView.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		shootPhotoBar = (RelativeLayout) findViewById(R.id.shootPhotoBar);
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

		Log.e("surfaceChanged:", "camera surfaceChanged");
		try {
			cameraController.cameraOpen(holder);
			cameraController.startPreview(previewView, shootPhotoBar);
		}
		catch (Exception e) {

			Log.e("Error in surfaceCreated:", "camera controller problem");

		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
//		try {
//			cameraController.cameraOpen(holder);
//			cameraController.startPreview(previewView);
//		}
//		catch (Exception e) {
//
//			Log.e("Error in surfaceCreated:", "camera controller problem");
//
//		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		cameraController.stopPreview();
		cameraController.releaseCamera();
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		imageFileUri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, new ContentValues());
		try {
			Toast.makeText(this, "imageFileUri:" + imageFileUri.toString(), Toast.LENGTH_LONG).show();
			OutputStream imageFileOS = getContentResolver().openOutputStream(imageFileUri);
			imageFileOS.write(data);
			imageFileOS.flush();
			imageFileOS.close();
			//
		}
		catch (FileNotFoundException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		catch (IOException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		// case R.id.photo_cancel:
		// setResult(Activity.RESULT_CANCELED, null);
		// finish();
		// break;
			case R.id.shootPhoto:
				if (!takingPhoto) {

					((Button) v).setEnabled(false);
					takingPhoto = true;
					CameraController.isTakingPhoto = true;
					cameraController.autoFocus();
				}
				break;
			case R.id.switch_button:
				try {
					cameraController.restartPreview();
					CameraController.getController().cameraOpen(holder);
					CameraController.getController().startPreview(previewView, shootPhotoBar);
				}
				catch (Exception e) {
					Log.e("Error in surfaceCreated:", "camera controller problem");

				}
				break;
			case R.id.flash_button:
				try {
					CameraController.getController().switchFlash(false);
				}
				catch (Exception e) {
					Log.e("Error in surfaceCreated:", "camera controller problem");

				}
				break;
		}
	}
}