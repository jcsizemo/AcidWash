package com.sizemore.filter.acid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class HelpActivity extends Activity {
	
	String effectName;
	Intent intent;
	EditText effectNameField;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.save_effect_activity);
		effectNameField = (EditText) findViewById(R.id.effectNameField);
		intent = getIntent();
	}
	
	public void saveEffectReturn(View v) {
		effectName = effectNameField.getText().toString();
		intent.putExtra("effectName",effectName);
		setResult(-1,intent);
		finish();
	}
}