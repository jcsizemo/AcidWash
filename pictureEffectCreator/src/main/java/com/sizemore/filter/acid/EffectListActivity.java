package com.sizemore.filter.acid;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EffectListActivity extends ListActivity {
	
	double[] kernel;
	Intent intent;
	Map<String,double[]> picEffects;
	String path;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		path = Environment.getExternalStorageDirectory().getPath() + "/PictureFX/";
		intent = getIntent();
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
			try {
				ObjectOutputStream dataOOS = new ObjectOutputStream(new FileOutputStream(path + "effects.ser"));
				dataOOS.writeObject(picEffects);
				dataOOS.close();
			}
			catch (Exception e) {}
		}
		catch (ClassNotFoundException cnfe) {}
		Set<String> effectNames = picEffects.keySet();
		this.setListAdapter(new ArrayAdapter<Object>(this, android.R.layout.simple_list_item_1,
				effectNames.toArray()));
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		TextView selected = (TextView) v;
		String selectedEffectName = (String) selected.getText();
		boolean psychedelic = false;
		if (selectedEffectName.equals("Psychedelic")) {
			psychedelic = true;
		}
		else {
			psychedelic = false;
		}
		kernel = picEffects.get(selectedEffectName);
		intent.putExtra("kernel", kernel);
		intent.putExtra("psych", psychedelic);
		setResult(-1,intent);
		finish();
	}
}