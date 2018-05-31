package com.bokecc.sdk.mobile.demo.drm.play;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.bokecc.sdk.mobile.demo.drm.R;
import com.bokecc.sdk.mobile.demo.drm.adapter.VideoListViewAdapter;
import com.bokecc.sdk.mobile.demo.drm.model.VideoInfo;

/**
 * 播放列表标签页，用于展示待播放的视频ID
 * 
 * @author CC视频
 *
 */
public class PlayFragment extends Fragment {
	
	private List<VideoInfo> videoInfos;

	private VideoListViewAdapter videoIdListViewAdapter;
	
	//TODO 待播放视频ID列表，可根据需求自定义
	public static String[] playVideoIds = new String[] {
			"36AECE98B8FEA56B9C33DC5901307461",
			"B7CA8FABC220BEB19C33DC5901307461",
			"4BD22ABE28E364759C33DC5901307461",
			"8CAFC9E03FB1FC879C33DC5901307461",
	};

	public static String[] playVideoLabels = new String[] {
			"76C58PICQ58PICBEagPx3mWtY(mp4)",
			"2018熊出没之变形记TS720P国语中字(mp4)",
			"01_考古学flv(flv)",
			"1500(f4v)",
	};

	private ListView playListView;
	private Context context;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = getActivity().getApplicationContext();
		RelativeLayout playLayout = new RelativeLayout(context);
		playLayout.setBackgroundColor(Color.WHITE);
		LayoutParams playLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		playLayout.setLayoutParams(playLayoutParams);
		
		playListView = new ListView(context);
		playListView.setDivider(getResources().getDrawable(R.drawable.line));
		playListView.setDividerHeight(2);
		playListView.setPadding(10, 10, 10, 10);
		LayoutParams playListLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		playLayout.addView(playListView, playListLayoutParams);

		// 生成动态数组，加入数据
		videoInfos = new ArrayList<>();
		for (int i = 0; i < playVideoIds.length; i++) {
			VideoInfo videoInfo = new VideoInfo();
			videoInfo.setVideoId(playVideoIds[i]);
			videoInfo.setLabel(playVideoLabels[i]);
			videoInfo.setResId(R.drawable.play);
			videoInfos.add(videoInfo);
		}

		videoIdListViewAdapter = new VideoListViewAdapter(context, videoInfos);
		playListView.setAdapter(videoIdListViewAdapter);
		playListView.setOnItemClickListener(onItemClickListener);

		return playLayout;
	}
	
	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@SuppressWarnings("unchecked")
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent intent = new Intent(context, MediaPlayActivity.class);
//			Intent intent = new Intent(context, SpeedIjkMediaPlayActivity.class);
			intent.putExtra("videoId", playVideoIds[position]);
			intent.putExtra("label", playVideoLabels[position]);
			startActivity(intent);
		}
	};

}
