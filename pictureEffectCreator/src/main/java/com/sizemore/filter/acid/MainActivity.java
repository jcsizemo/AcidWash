package com.sizemore.filter.acid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.Point;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private static final int REQUEST_CODE = 1;
	private static final int CHANGE_KERNEL = 25;
	private Stack<Bitmap> imageStack = new Stack<Bitmap>();
	private Bitmap bitmap;
	private ImageView imageView;
	private ImageToolkit toolkit;
	private int imageHeight;
	private int imageWidth;
	private double[] kernel = {0,0,0,0,1,0,0,0,0};
	private String path;
	private boolean preserveBrightness = false;
	private boolean psychedelic = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		path = Environment.getExternalStorageDirectory().getPath();
		File picFxDirectory = new File(path + "/PictureFX");
		picFxDirectory.mkdirs();
		
		imageView = (ImageView) findViewById(R.id.result);
		toolkit = new ImageToolkit();
	}

	public void pickImage(View View) {
		Mat m = new Mat(3,3,CvType.CV_64F);
		int r = m.rows();
		int c = m.cols();
		m = (MatOfDouble) m.inv();
		imageStack.clear();
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(intent, REQUEST_CODE);
	}
	
	public void modifyKernel(View view) {
		Intent intent = new Intent(this, KernelActivity.class);
		intent.putExtra("kernel", kernel);
		startActivityForResult(intent,CHANGE_KERNEL);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
			try {
				// We need to recycle unused bitmaps
				if (bitmap != null) {
					bitmap.recycle();
				}
				InputStream stream = getContentResolver().openInputStream(
						data.getData());
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				bitmap = BitmapFactory.decodeStream(stream, null, options);
				imageHeight = options.outHeight;
				imageWidth = options.outWidth;
				final int height = options.outHeight;
			    final int width = options.outWidth;
			    int inSampleSize = 1;

			    if (height > 384 || width > 512) {
			        if (width > height) {
			            inSampleSize = Math.round((float)height / (float)384);
			        } else {
			            inSampleSize = Math.round((float)width / (float)512);
			        }
			    }
			    options.inSampleSize = inSampleSize;
			    options.inJustDecodeBounds = false;
			    stream.close();
			    stream = getContentResolver().openInputStream(
						data.getData());
				bitmap = BitmapFactory.decodeStream(stream, null, options);
				stream.close();
				bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
				imageStack.push(bitmap);
				imageView.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		if (requestCode == CHANGE_KERNEL && resultCode == Activity.RESULT_OK) {
			kernel = data.getDoubleArrayExtra("kernel");
			preserveBrightness = data.getBooleanExtra("preserveBrightness", false);
			psychedelic = data.getBooleanExtra("psych", false);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void findFaces() {
		FaceDetector fd;
		FaceDetector.Face[] faces = new FaceDetector.Face[1];
		PointF midpoint = new PointF();
		int[] fpx = null;
		int[] fpy = null;
		int count = 0;
		int w = 200;
		int h = 200;
		try {
			fd = new FaceDetector(w,h,1);
			count = fd.findFaces(bitmap, faces);
		}
		catch (Exception e) {
			
		}
		if (count > 0) {
			fpx = new int[count];
			fpy = new int[count];
			for (int i = 0; i < count ; i++) {
				try {
					faces[i].getMidPoint(midpoint);
					fpx[i] = (int) midpoint.x;
					fpy[i] = (int) midpoint.y;
				}
				catch (Exception e) {
					
				}
			}
		}
//		imageView.setDisplayPoints(fpx,fpy,count,0);
	}

	public void transformImage(View v) {
		if (imageStack.size() == 0) {
			Toast.makeText(this, "No image chosen!", Toast.LENGTH_SHORT).show();
			return;
		}
		if (imageStack.size() == 6) {
			Toast.makeText(this, "Max operations performed!", Toast.LENGTH_SHORT).show();
			return;
		}
		Bitmap convolved = imageStack.peek().copy(Bitmap.Config.ARGB_8888, true);
		int w = convolved.getWidth();
		int h = convolved.getHeight();
		int[] pixels = new int[w*h];
		imageStack.peek().getPixels(pixels, 0, w, 0, 0, w, h);
		pixels = toolkit.convolve(w,h,pixels,kernel,preserveBrightness,psychedelic);
		convolved.setPixels(pixels, 0, w, 0, 0, w, h);
		imageStack.push(convolved);
		imageView.setImageBitmap(convolved);
	}
	
	public void revertImage(View v) {
		if (imageStack.size() == 0) {
			Toast.makeText(this, "No image chosen!", Toast.LENGTH_SHORT).show();
			return;
		}
		if (imageStack.size() == 1) {
			Toast.makeText(this, "Operation not performed!", Toast.LENGTH_SHORT).show();
			return;
		}
		imageStack.pop();
		imageView.setImageBitmap(imageStack.peek());
	}
	
	public void saveImage(View v) {
		if (imageStack.size() == 0) {
			Toast.makeText(this, "No image chosen!", Toast.LENGTH_SHORT).show();
			return;
		}
		if (imageStack.size() == 1) {
			Toast.makeText(this, "It's the same image!", Toast.LENGTH_SHORT).show();
			return;
		}
		Bitmap bmToSave = imageStack.peek();
		bmToSave = Bitmap.createScaledBitmap(bmToSave, imageWidth, imageHeight, false);
		MediaStore.Images.Media.insertImage(getContentResolver(), bmToSave, "", "");
		Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
		if (imageStack.size() > 0) {
			imageView.setImageBitmap(imageStack.peek());
		}
	}
}