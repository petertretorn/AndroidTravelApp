package dk.itu.mmad.travelapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		WebView www = (WebView) findViewById(R.id.www);
		WebSettings settings = www.getSettings();
		settings.setBuiltInZoomControls(true);
		
		www.setWebViewClient(new WebViewClient());
		www.loadUrl("http://www.dsb.dk");
		
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
