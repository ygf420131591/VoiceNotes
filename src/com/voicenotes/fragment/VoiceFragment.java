package com.voicenotes.fragment;

import java.util.ArrayList;
import java.util.List;

import com.voicenotes.views.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * A simple {@link Fragment} subclass.
 * 
 */
public class VoiceFragment extends Fragment {

	private ListView listView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_voice, container, false);
		
		listView = (ListView) view.findViewById(R.id.audioRecordListView);
		List<String> list =  new ArrayList<String>();
		list.add("²âÊÔ´úÂë");
		list.add("²âÊÔ´úÂë1");
		list.add("²âÊÔ´úÂë2");
		list.add("²âÊÔ´úÂë3");
		
		listView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list));
		return view;
	}

}
