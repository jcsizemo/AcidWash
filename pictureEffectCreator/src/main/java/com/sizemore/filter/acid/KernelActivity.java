package com.sizemore.filter.acid;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class KernelActivity extends Activity {
	
	double[] kernel;
	EditText kernel0;
	EditText kernel1;
	EditText kernel2;
	EditText kernel3;
	EditText kernel4;
	EditText kernel5;
	EditText kernel6;
	EditText kernel7;
	EditText kernel8;
	CheckBox preserveCheckbox;
	Intent intent;
	Map<String,double[]> picEffects;
	private String path;
	private static final int FAILURE = 0;
	private static final int SUCCESS = -1;
	private static final int SAVE = 8;
	private static final int LOAD = 4;
	private boolean psychedelic = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kernel_activity);
		path = Environment.getExternalStorageDirectory().getPath() + "/PictureFX/";
		kernel0 = (EditText) findViewById(R.id.kernel0);
		kernel1 = (EditText) findViewById(R.id.kernel1);
		kernel2 = (EditText) findViewById(R.id.kernel2);
		kernel3 = (EditText) findViewById(R.id.kernel3);
		kernel4 = (EditText) findViewById(R.id.kernel4);
		kernel5 = (EditText) findViewById(R.id.kernel5);
		kernel6 = (EditText) findViewById(R.id.kernel6);
		kernel7 = (EditText) findViewById(R.id.kernel7);
		kernel8 = (EditText) findViewById(R.id.kernel8);
		preserveCheckbox = (CheckBox) findViewById(R.id.checkBox1);
		intent = getIntent();
		kernel = intent.getDoubleArrayExtra("kernel");
		kernel0.setText(kernel[0] + "");
		kernel1.setText(kernel[1] + "");
		kernel2.setText(kernel[2] + "");
		kernel3.setText(kernel[3] + "");
		kernel4.setText(kernel[4] + "");
		kernel5.setText(kernel[5] + "");
		kernel6.setText(kernel[6] + "");
		kernel7.setText(kernel[7] + "");
		kernel8.setText(kernel[8] + "");
	}
	
	public void returnKernel(View v) {
		double k0 = Double.valueOf(kernel0.getText().toString());
		double k1 = Double.valueOf(kernel1.getText().toString());
		double k2 = Double.valueOf(kernel2.getText().toString());
		double k3 = Double.valueOf(kernel3.getText().toString());
		double k4 = Double.valueOf(kernel4.getText().toString());
		double k5 = Double.valueOf(kernel5.getText().toString());
		double k6 = Double.valueOf(kernel6.getText().toString());
		double k7 = Double.valueOf(kernel7.getText().toString());
		double k8 = Double.valueOf(kernel8.getText().toString());
		kernel = new double[]{k0,k1,k2,k3,k4,k5,k6,k7,k8};
		intent.putExtra("kernel",kernel);
		intent.putExtra("preserveBrightness", preserveCheckbox.isChecked());
		intent.putExtra("psych", psychedelic);
		setResult(-1,intent);
		finish();
	}
	
	public void loadEffect(View v) {
		Intent intent = new Intent(this, EffectListActivity.class);
		startActivityForResult(intent,LOAD);
	}
	
	public void saveEffect(View v) {
		Intent intent = new Intent(this, SaveEffectActivity.class);
		startActivityForResult(intent,SAVE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		if (requestCode == LOAD && resultCode == Activity.RESULT_OK) {
			kernel = data.getDoubleArrayExtra("kernel");
			kernel0.setText(kernel[0] + "");
			kernel1.setText(kernel[1] + "");
			kernel2.setText(kernel[2] + "");
			kernel3.setText(kernel[3] + "");
			kernel4.setText(kernel[4] + "");
			kernel5.setText(kernel[5] + "");
			kernel6.setText(kernel[6] + "");
			kernel7.setText(kernel[7] + "");
			kernel8.setText(kernel[8] + "");
			psychedelic = data.getBooleanExtra("psych", false);
		}
		if (requestCode == SAVE && resultCode == Activity.RESULT_OK) {
			try {
				ObjectInputStream dataOIS = new ObjectInputStream(new FileInputStream(path + "effects.ser"));
				picEffects = (HashMap<String,double[]>) dataOIS.readObject();
				dataOIS.close();
			}
			catch (IOException ioe) {
				picEffects = new HashMap<String,double[]>();
				picEffects.put("Sharpen", new double[]{-0.11f,-0.11f,-0.11f,-0.11f,2,-0.11f,-0.11f,-0.11f,-0.11f});
				picEffects.put("Blur", new double[]{0.11f,0.11f,0.11f,0.11f,0.11f,0.11f,0.11f,0.11f,0.11f});
				picEffects.put("Edge Detect", new double[]{0,-1,0,-1,4,-1,0,-1,0});
				picEffects.put("Psychedelic", new double[]{0,1,0,2,1,2,0,1,0});
			}
			catch (ClassNotFoundException cnfe) {}
			double k0 = Double.valueOf(kernel0.getText().toString());
			double k1 = Double.valueOf(kernel1.getText().toString());
			double k2 = Double.valueOf(kernel2.getText().toString());
			double k3 = Double.valueOf(kernel3.getText().toString());
			double k4 = Double.valueOf(kernel4.getText().toString());
			double k5 = Double.valueOf(kernel5.getText().toString());
			double k6 = Double.valueOf(kernel6.getText().toString());
			double k7 = Double.valueOf(kernel7.getText().toString());
			double k8 = Double.valueOf(kernel8.getText().toString());
			kernel = new double[]{k0,k1,k2,k3,k4,k5,k6,k7,k8};
			String effectName = data.getStringExtra("effectName");
			picEffects.put(effectName,kernel);
			try {
				ObjectOutputStream dataOOS = new ObjectOutputStream(new FileOutputStream(path + "effects.ser"));
				dataOOS.writeObject(picEffects);
				dataOOS.close();
			}
			catch (Exception e) {}
			Toast.makeText(this,"Effect saved!",Toast.LENGTH_SHORT).show();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}