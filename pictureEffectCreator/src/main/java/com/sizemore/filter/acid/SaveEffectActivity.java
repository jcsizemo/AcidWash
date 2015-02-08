package com.sizemore.filter.acid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SaveEffectActivity extends Activity {
	
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
		if (effectName.length() == 0) {
			Toast.makeText(this, "Effect must have a name", Toast.LENGTH_SHORT).show();
		}
		else {
			intent.putExtra("effectName",effectName);
			setResult(-1,intent);
			finish();
		}
	}
}