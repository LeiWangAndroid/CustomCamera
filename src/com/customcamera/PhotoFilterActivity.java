
package com.customcamera;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.customcamera.utils.CameraConstants;

public class PhotoFilterActivity extends Activity {
	private Button buttonNewPic, buttonGallery;

	private static final int IMAGE_PICK = 1;
	private String filePath;
	private int orientation;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		this.buttonNewPic = (Button) this.findViewById(R.id.button_camera);
		this.buttonNewPic.setOnClickListener(new TakePictureListener());
		this.buttonGallery = (Button) this.findViewById(R.id.button_from_phone);
		this.buttonGallery.setOnClickListener(new ImagePickListener());
	}

	/**
	 * Click Listener for selecting images from phone gallery
	 */
	class ImagePickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			openGallery();
		}
	}

	private void openGallery() {
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/*");
		startActivityForResult(intent, IMAGE_PICK);
	}

	/**
	 * Click listener for taking new picture
	 */
	class TakePictureListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			openCamera();
		}
	}

	private void openCamera() {

		// Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// startActivityForResult(intent, 0);
		Intent intent = new Intent();
		intent.setClass(this, CameraActivity.class);
		intent.putExtra(CameraConstants.CATEGORY_TYPE, 1);
		startActivityForResult(intent, 0);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case IMAGE_PICK:
					retreiveFilePathFromGallery(data);
					Intent intent = new Intent(PhotoFilterActivity.this, PreviewActivity.class);
					intent.putExtra("isGallery", true);
					intent.putExtra("filePath", filePath);
					intent.putExtra("orientation", orientation);
					startActivity(intent);
					break;
			}
		}
	}
	
	/**
	 * Retreive FilePath from Gallery
	 * @param data
	 * @return
	 */
	private void retreiveFilePathFromGallery(Intent data){
		Uri selectedImage = data.getData();
    	String [] filePathColumn = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.ORIENTATION};
    	
    	Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
    	cursor.moveToFirst();
    	
    	int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
    	filePath = cursor.getString(columnIndex);
		int columnIndex_orient = cursor.getColumnIndexOrThrow(filePathColumn[1]);
		orientation = cursor.getInt(columnIndex_orient);
    	cursor.close();
	}


}
