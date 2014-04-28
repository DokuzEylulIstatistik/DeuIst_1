package com.deu.istatistik;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment_Matris extends Fragment {

	View rootview;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View vi = inflater.inflate(R.layout.layout_matris, container, false);
		rootview = vi;
		
		
		setHasOptionsMenu(true);
		return vi;
		

	}
}
