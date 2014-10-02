package com.deu.istatistik;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

public class Fragment_IstatistikPaylasim_Show extends Fragment {

	Activity activity;
	LinearLayout viewroot;
	Kutuphane kutuphane = new Kutuphane();

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		viewroot = (LinearLayout) inflater.inflate(
				R.layout.fragement_istatistikpaylasim_show, container, false);
		activity = getActivity();
		// ////////////

		// ActivityResult result = new ActivityResult(10, activity.getIntent());
		// Intent intent = result.getResultData();
		// String name = intent.getStringExtra("name");

		Bundle args = getArguments();
		String name = args.getString("name");
		String icerik = args.getString("icerik");

		WebView web = (WebView) viewroot
				.findViewById(R.id.fragmentshowentry_webview);
		web.setContentDescription("Description");
		web.setAlwaysDrawnWithCacheEnabled(true);
		// web.getSettings().setPluginState(PluginState.ON);

		WebSettings settings = web.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setBuiltInZoomControls(true);
		// settings.setAllowContentAccess(true);
		settings.setBlockNetworkImage(false);
		settings.setLoadsImagesAutomatically(true);

		String data = icerik;
		String content = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
				+ "<html><head>"
				+ "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />"
				+ "<head><body>";

		content += data + "</body></html>";

		web.loadData(content, "text/html; charset=utf-8", "UTF-8");

		
		// getView().setFocusableInTouchMode(true);
		// getView().setOnKeyListener(new OnKeyListener() {
		//
		// @Override
		// public boolean onKey(View v, int keyCode, KeyEvent event) {
		// if (keyCode == KeyEvent.KEYCODE_BACK) {
		// Log.i(activity.getPackageName(), "keyCode == KeyEvent.KEYCODE_BACK");
		// return true;
		//
		// }
		// return false;
		// }
		// });

		return viewroot;
	}
}
