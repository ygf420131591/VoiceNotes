package com.voicenotes.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.voicenotes.views.R;
import com.voicenotes.views.VoicePlayActivity;
import com.voicenotes.views.utils.AudioAdapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * A simple {@link Fragment} subclass.
 * 
 */
public class VoiceFragment extends Fragment {

	static {
		System.loadLibrary("Opus");
	}
	private ListView mListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_voice, container, false);
		
		mListView = (ListView) view.findViewById(R.id.audioRecordListView);
		
		//BaseAdapter
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		updateList(list);
		
		AudioAdapter audioAdapter = new AudioAdapter(VoiceFragment.this.getActivity(), list, R.layout.list_item_view, new String[]{"itemTitle", "itemText", "ItemImage"}, new int[]{R.id.TitleEditText, R.id.ContentEditText, R.id.imageView1});
		mListView.setAdapter(audioAdapter);
		
		
		//simpleAdapter
//		SimpleAdapter simpleAdapter = new SimpleAdapter(VoiceFragment.this.getActivity(), list, R.layout.list_item_view, new String[]{"ItemImage", "itemTitle", "itemText"}, new int[]{R.id.imageView1, R.id.editText1, R.id.editText2});
//		mListView.setAdapter(simpleAdapter);
		
		//arrayList
//		List<String> list =  new ArrayList<String>();		
//		updateList(list);
//		mListView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice, list));
//		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String content = (String) mListView.getItemAtPosition(arg2);
				Intent intent = new Intent(VoiceFragment.this.getActivity(), VoicePlayActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("FileName", content);
				startActivity(intent);
			}
			
		});
		
		return view;
	}
	
	private void updateList(ArrayList<HashMap<String, Object>> list) {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
		File file = new File(path);
		File[] files = file.listFiles();
//		
//		for (int i = 0; i < files.length; i ++) {
//			File currentFile = files[i];
//			String currentName = currentFile.getName();
//			if (currentName.contains(".opus")) {
//				list.add(currentName);
//			}
//		}
		
		for (int i = 0; i < files.length; i++) {
			File currentFile = files[i];
			String currentName = currentFile.getName();
			if (currentName.contains(".opus")) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("ItemImage", R.drawable.common_back);
				map.put("itemTitle", currentName);
				map.put("itemText", "这是第" + i + "行");
				list.add(map);
			}
		}
	}
		
}
