package com.customcamera.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.impl.client.DefaultHttpClient;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

public class ImageUtils {
	
	/**
	 * Get Exif Orientation value (1, 6, 3, 8)
	 */
	public static int GetOrientationFromExifMetadata(String filename) {
		int orientation = 0;
		
        ExifInterface exif = null;
		try {
			exif = new ExifInterface(filename);
            int exifOrientation = exif.getAttributeInt(
            		ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            
            orientation = ImageUtils.ExifOrientationToDegrees(exifOrientation);            
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return orientation;
	}
	
	/**
	 * Convert Exif Orientation value (1, 6, 3, 8) to rotate degree (1, 90, 180, 270)
	 */
	private static int ExifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }        
        return 0;
    }
	
	/**
	 * Rotate bitmap according to the rotate degree
	 */
	public static Bitmap RotateImage(Bitmap sourceBitmap, int orientation) {
		int x = 0;
		int y = 0;
		int imageWidth = sourceBitmap.getWidth();
		int imageHeight = sourceBitmap.getHeight(); 
		
		Matrix matrix = new Matrix();
		
//		String phone_model = Build.MODEL;		
//		if (phone_model.contentEquals("Nexus")) { }
			
		if (orientation != 0) {
		     matrix.preRotate(orientation);
		}
		
		Bitmap resizedBitmap = Bitmap.createBitmap(
		     sourceBitmap, x, y, imageWidth, imageHeight,
		     matrix, true);
		
		return resizedBitmap;
	}
	
	/**
	 * Load image from URL
	 */
	public static Bitmap LoadImageFromURL(String URL) {
		return LoadImageFromURL(URL, null);
	}
	
	public static Bitmap LoadImageFromURL(String URL, DefaultHttpClient httpClient) {
		Bitmap bitmap = null;
		InputStream in = null;
		
		BitmapFactory.Options bmOptions;
		bmOptions = new BitmapFactory.Options();
		bmOptions.inSampleSize = 1;
		
		try {
			if(httpClient != null) {
				in = DownloadUtils.GetInputStreamFromHttpClient(URL, httpClient);
			} else {
				in = DownloadUtils.GetInputStreamFromURLConnection(URL);
			}
			bitmap = BitmapFactory.decodeStream(in, null, bmOptions);
			if(in != null){
				in.close();
			}	
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return bitmap;
	}
	
	/**
	 * Make a smaller image size according to required image width and height
	 */
    public static Bitmap DecodeSampledBitmapFromFile(String pathName, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

	/**
	 * Make a smaller image size according to required image width and height
	 */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
    
    /**
     * Invoked by DecodeSampledBitmapFromResource method to calculate the options.inSampleSize
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

}
