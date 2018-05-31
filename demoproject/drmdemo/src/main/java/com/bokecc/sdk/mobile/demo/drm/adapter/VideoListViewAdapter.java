package com.bokecc.sdk.mobile.demo.drm.adapter;

import java.util.List;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bokecc.sdk.mobile.demo.drm.model.VideoInfo;
import com.bokecc.sdk.mobile.demo.drm.view.VideoListView;

/**
 * 
 * 显示视频ID列表的适配器
 * 
 * @author CC视频
 *
 */
public class VideoListViewAdapter extends BaseAdapter{
	
	protected List<VideoInfo> videoInfos;
	
	protected Context context;
	
	public VideoListViewAdapter(Context context, List<VideoInfo> videoInfos){
		this.videoInfos = videoInfos;
		this.context = context;
	}

	@Override
	public int getCount() {
		return videoInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return videoInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		VideoInfo videoInfo = videoInfos.get(position);
		VideoListView videoListView = new VideoListView(context, videoInfo.getVideoId(), videoInfo.getLabel(), videoInfo.getResId());
		videoListView.setTag(videoInfo.getVideoId());
		
		return videoListView;
		
	}

}
