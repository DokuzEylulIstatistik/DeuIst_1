package com.deu.istatistik;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aka.qwerty.dbSqLite;
import com.aka.qwerty.obj_IstatistikPaylasim;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class Fragment_IstatistikPaylasim extends Fragment implements
		OnClickListener {

	private static final String TAG_TO_FRAGMENT = "TAG_TO_FRAGMENT";
	private LinearLayout viewroot;
	private Activity activity;
	private String istatistikPaylasimURL = "http://www.aykutasil.com/api/IstatistikApi";
	Kutuphane kutuphane = new Kutuphane();
	dbSqLite db;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		viewroot = (LinearLayout) inflater.inflate(
				R.layout.fragment_istatistikpaylasim, container, false);

		
		activity = getActivity();

		
		ImageLoaderConfiguration conf = new ImageLoaderConfiguration.Builder(
				activity).build();
		ImageLoader.getInstance().init(conf);
		// ///////////
		String actionbarSubTitle = getResources().getString(
				R.string.subtitle_istatistikpaylasim);

		ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(actionbarSubTitle);
		actionBar.setTitle(actionbarSubTitle);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.show();
		setHasOptionsMenu(true);

		// /////
		db = new dbSqLite(activity);
		if (db.getIstatistikList().size() <= 0) {
			new getIstatistikPaylasim().execute(istatistikPaylasimURL);

		} else {
			setIstatistikList();
			showIstatistikList();
		}
		return viewroot;
	}

	private ActionBar getActionBar() {
		return ((ActionBarActivity) getActivity()).getSupportActionBar();
	}

	List<obj_IstatistikPaylasim> listaykutasilIstatistik = null;

	private class getIstatistikPaylasim extends
			AsyncTask<String, Integer, InputStream> {

		@Override
		protected void onPreExecute() {
			listaykutasilIstatistik = new ArrayList<obj_IstatistikPaylasim>();
		}

		@Override
		protected InputStream doInBackground(String... params) {
			InputStream stream = null;
			try {
				// publishProgress(20);
				stream = downloadUrl(params[0]);
				Log.i("Veri Ýndirildi", istatistikPaylasimURL);
				return stream;

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return stream;
		}

		@Override
		protected void onPostExecute(InputStream result) {

			String json = "";

			json = getStringtoInputStream(result);
			JSONObject obje = isJsonParser(json);

			setIstatistikList(obje);
			showIstatistikList();

			Log.i("Veri Çekildi", istatistikPaylasimURL);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {

			// seekbar.setProgress(values[0]);
		}

	}

	private void setIstatistikList(JSONObject obje) {
		try {
			JSONArray veriler = obje.getJSONArray("Data");
			for (int i = 0; i < veriler.length(); i++) {
				obj_IstatistikPaylasim deuist = new obj_IstatistikPaylasim();
				JSONObject veri = veriler.getJSONObject(i);

				deuist.setIst_ID(veri.getInt("ist_ID"));
				deuist.setIst_konu(Kutuphane.changeCharset(veri
						.getString("ist_konu")));
				deuist.setIst_okunmasayisi(veri.getInt("ist_okunmasayisi"));
				deuist.setIst_tarih(veri.getString("ist_tarih"));
				deuist.setIst_tag(veri.getString("ist_tag"));
				deuist.setIst_yazi(veri.getString("ist_yazi"));

				listaykutasilIstatistik.add(deuist);
				db.insertIstatistik(deuist);

				Log.i("Json Array Liste", String.valueOf(i));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void setIstatistikList() {
		listaykutasilIstatistik = db.getIstatistikList();
	}

	private void showIstatistikList() {
		istatistikPaylasimAdaptor adap = new istatistikPaylasimAdaptor(
				activity, listaykutasilIstatistik);
		ListView aykutasilComListView = (ListView) viewroot
				.findViewById(R.id.IstatistikPaylasimListView);
		aykutasilComListView.setAdapter(adap);
	}

	private JSONObject isJsonParser(String json) {
		JSONObject jObj = null;
		try {
			if (json != null) {
				jObj = new JSONObject(json);
			} else {
				jObj = null;
			}

		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data "
					+ e.getMessage().toString());
		}
		return jObj;
	}

	private String getStringtoInputStream(InputStream is) {
		String sonuc = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-9"), 8); // iso-8859-1

			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "/n");
			}
			is.close();
			sonuc = sb.toString();
		} catch (Exception e) {
			Log.i("Buffer Error", "Error converting result " + e.toString());
		}
		return sonuc;

	}

	private InputStream downloadUrl(String urlString) throws IOException {
		// URL url = new URL(urlString);
		// HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// conn.setReadTimeout(10000 /* milliseconds */);
		// conn.setConnectTimeout(15000 /* milliseconds */);
		// conn.setRequestMethod("GET");
		// conn.setDoInput(true);
		// // Starts the query
		// conn.connect();
		//
		// InputStream stream = conn.getInputStream();

		HttpClient httpClient = new DefaultHttpClient();

		HttpGet httpPost = new HttpGet(urlString);

		HttpResponse response = httpClient.execute(httpPost);

		HttpEntity entity = response.getEntity();
		String resultsStringg = EntityUtils.toString(entity, "ISO-8859-9");
		InputStream stream = new ByteArrayInputStream(
				resultsStringg.getBytes("ISO-8859-9"));

		return stream;
	}

	private class istatistikPaylasimAdaptor extends BaseAdapter {
		// private Context context;
		private List<obj_IstatistikPaylasim> list;
		private LayoutInflater inflater;

		public istatistikPaylasimAdaptor(Context context,
				List<obj_IstatistikPaylasim> list) {
			// this.context = context;
			this.list = list;

			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		private class MyViewHolder {
			public TextView title;
			public TextView content;
			public ImageView image;
		}

		@Override
		public View getView(final int arg0, View arg1, ViewGroup arg2) {

			View view = arg1;
			MyViewHolder viewholder;

			if (arg1 == null) {
				view = inflater.inflate(R.layout.fragment_istatikpaylasim_row,
						null);

				viewholder = new MyViewHolder();
				viewholder.title = (TextView) view
						.findViewById(R.id.fragmentEntryUst);
				viewholder.content = (TextView) view
						.findViewById(R.id.fragmentEntryAlt);
				viewholder.image = (ImageView) view
						.findViewById(R.id.fragmanetEntryImage);

				view.setTag(viewholder);

			} else {
				viewholder = (MyViewHolder) view.getTag();
			}

			if (list.size() <= 0) {
				viewholder.title.setText("Veri Yok.");
			} else {
				obj_IstatistikPaylasim aa = list.get(arg0);
				viewholder.title.setText(aa.getIst_konu());
				viewholder.content.setText("DEU Ýstatistik");

				// String url =
				// "http://www.aykutasil.com/Content/images/ResimYukle/30122013AcomA4a099e7c-c597-4132-8747-6a0e0c4a668fAcomAUntitled-262x300.png";

				// ImageLoader.getInstance().displayImage(url,
				// viewholder.image);

				// new getUrlImage(viewholder.image, url);
			}

			view.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {

					int a = (int) event.getX();
					// int b = (int) event.getHistoricalX(1);

					Log.i("a", String.valueOf(a));
					// Log.i("b",String.valueOf(b));
					return false;
				}
			});

			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Log.i("view.setOnClickListener(new OnClickListener()",
							"...");

					Animation anim = AnimationUtils.loadAnimation(activity,
							R.anim.mytranslateanimation);

					v.startAnimation(anim);
					// /////////
					TextView altyazi = (TextView) v
							.findViewById(R.id.fragmentEntryAlt);
					Animation anim_altyazi = AnimationUtils.loadAnimation(
							activity, R.anim.myrotateanimation);
					altyazi.startAnimation(anim_altyazi);
					// ////////

					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							aykutasilOnclickListener(arg0, list.get(arg0)
									.getIst_yazi());

						}
					}, 1000);

				}
			});
			return view;
		}
	}

	private void aykutasilOnclickListener(int position, String icerik) {
		Log.i("aykutasilOnclickListener Týklandý", String.valueOf(position));

		showEntry(icerik);

	}

	private void showEntry(String icerik) {
		Bundle args = new Bundle();
		args.putString("name", "aykut");
		args.putString("icerik", icerik);
		Fragment toFragment = new Fragment_IstatistikPaylasim_Show();
		toFragment.setArguments(args);
		getFragmentManager().beginTransaction()
				.replace(R.id.container, toFragment, TAG_TO_FRAGMENT)
				.addToBackStack(TAG_TO_FRAGMENT).commit();
	}

	@Override
	public void onClick(View v) {
		Log.i("onClick", "...");
	}
}
