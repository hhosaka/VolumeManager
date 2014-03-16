package com.nag.android.ringmanager;

import com.nag.android.volumemanager.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;
import android.webkit.WebView;

public class WebViewActivity extends Activity{

	private static final String assets_url = "file:///android_asset/";
	public static final String PARAM_MODE = "mode";
	public static final int MODE_HELP = 0;
	private WebView webview = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		int mode = getIntent().getIntExtra( PARAM_MODE, -1 );
		webview = (WebView)findViewById(R.id.webView1);
//		webview.setWebViewClient(new WebViewClient());
		switch( mode )
		{
		case MODE_HELP:
			webview.loadUrl( assets_url+"jp/"+"help/"+"application.html" );// TODO
			break;
		default:
			finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK &&  webview.canGoBack() ) {
			webview.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
