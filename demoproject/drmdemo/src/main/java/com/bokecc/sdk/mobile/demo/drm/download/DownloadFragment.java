package com.bokecc.sdk.mobile.demo.drm.download;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.bokecc.sdk.mobile.demo.drm.R;
import com.bokecc.sdk.mobile.demo.drm.adapter.VideoListViewAdapter;
import com.bokecc.sdk.mobile.demo.drm.downloadutil.DownloadController;
import com.bokecc.sdk.mobile.demo.drm.model.VideoInfo;
import com.bokecc.sdk.mobile.demo.drm.util.ConfigUtil;
import com.bokecc.sdk.mobile.demo.drm.util.DataSet;
import com.bokecc.sdk.mobile.demo.drm.view.VideoListView;
import com.bokecc.sdk.mobile.download.Downloader;
import com.bokecc.sdk.mobile.download.OnProcessDefinitionListener;
import com.bokecc.sdk.mobile.exception.DreamwinException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 
 * 下载列表标签页，用于展示待下载的视频ID
 * 
 * @author CC视频
 *
 */
public class DownloadFragment extends Fragment {

	final String POPUP_DIALOG_MESSAGE = "dialogMessage";
	
	final String GET_DEFINITION_ERROR  = "getDefinitionError";
	
	//定义hashmap存储downloader信息
	public static HashMap<String, Downloader> downloaderHashMap = new HashMap<String, Downloader>();
	
	AlertDialog definitionDialog;

	private List<VideoInfo> videoInfos;
	
	private DownloadListViewAdapter downloadListViewAdapter;

	//TODO 待下载视频ID，可根据需求自定义
	public String[] downloadVideoIds = new String[] {
			"36AECE98B8FEA56B9C33DC5901307461",
			"B7CA8FABC220BEB19C33DC5901307461",
			"4BD22ABE28E364759C33DC5901307461",
			"8CAFC9E03FB1FC879C33DC5901307461",
	};
	public String[] downloadVideoLabels = new String[] {
			"76C58PICQ58PICBEagPx3mWtY(mp4)",
			"2018熊出没之变形记TS720P国语中字(mp4)",
			"01_考古学flv(flv)",
			"1500(f4v)",
	};
	private ListView downloadListView;
	private Context context;
	private FragmentActivity activity;
	private String videoId;
	private String title;
	int[] definitionMapKeys;
	HashMap<Integer, String> hm;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		activity = getActivity();
		context = activity.getApplicationContext();
		
		RelativeLayout downloadRelativeLayout = new RelativeLayout(context);
		downloadRelativeLayout.setBackgroundColor(Color.WHITE);
		downloadRelativeLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
		downloadListView = new ListView(context);
		downloadListView.setPadding(10, 10, 10, 10);
		downloadListView.setDivider(getResources().getDrawable(R.drawable.line));
		LayoutParams listViewLayout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		downloadRelativeLayout.addView(downloadListView, listViewLayout);
		
		// 生成动态数组，加入数据
		videoInfos = new ArrayList<>();
		for (int i = 0; i < downloadVideoIds.length; i++) {
			VideoInfo videoInfo = new VideoInfo();
			videoInfo.setVideoId(downloadVideoIds[i]);
			videoInfo.setLabel(downloadVideoLabels[i]);
			videoInfo.setResId(R.drawable.download);
			videoInfos.add(videoInfo);
		}

		downloadListViewAdapter = new DownloadListViewAdapter(context, videoInfos);
		downloadListView.setAdapter(downloadListViewAdapter);
		downloadListView.setOnItemClickListener(onItemClickListener);

		return downloadRelativeLayout;
	}

	Downloader downloader;
	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@SuppressWarnings("unchecked")
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			//点击item时，downloader初始化使用的是设置清晰度方式
//			Pair<String, Integer> pair = (Pair<String, Integer>) parent.getItemAtPosition(position);
			videoId = downloadVideoIds[position];
			title = downloadVideoLabels[position];
			
			downloader = new Downloader(videoId, ConfigUtil.USERID, ConfigUtil.API_KEY);
			downloader.setOnProcessDefinitionListener(onProcessDefinitionListener);
			downloader.getDefinitionMap();
		}
	};
	
	@SuppressLint("HandlerLeak") 
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String message = (String) msg.obj;
			if ( message.equals(POPUP_DIALOG_MESSAGE)) {
				String[] definitionMapValues = new String[hm.size()];
				definitionMapKeys = new int[hm.size()]; 
				Set<Entry<Integer, String>> set = hm.entrySet();
				Iterator<Entry<Integer, String>> iterator = set.iterator();
				int i = 0;
				while(iterator.hasNext()){
					Entry<Integer, String> entry = iterator.next();
					definitionMapKeys[i] = entry.getKey();
					definitionMapValues[i] = entry.getValue();
					i++;
				}
				
				Builder builder = new Builder(activity);
				builder.setTitle("选择下载清晰度");
				builder.setSingleChoiceItems(definitionMapValues, 0, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						int definition = definitionMapKeys[which];
						
						title = title + "-" + definition;
						if (DataSet.hasDownloadInfo(title)) {
							Toast.makeText(context, "文件已存在", Toast.LENGTH_SHORT).show();
							return;
						}
						
                        DownloadController.insertDownloadInfo(videoId, title, definition);
						definitionDialog.dismiss();
						Toast.makeText(context, "文件已加入下载队列", Toast.LENGTH_SHORT).show();
					}
				});
				definitionDialog = builder.create();
				definitionDialog.show();
			}
			
			if ( message.equals(GET_DEFINITION_ERROR)) {
				Toast.makeText(context, "网络异常，请重试", Toast.LENGTH_LONG).show();
			}
			super.handleMessage(msg);
		}
	};
	
	private OnProcessDefinitionListener onProcessDefinitionListener = new OnProcessDefinitionListener(){
		@Override
		public void onProcessDefinition(HashMap<Integer, String> definitionMap) {
			hm = definitionMap;
			if(hm != null){
				Message msg = new Message();
				msg.obj = POPUP_DIALOG_MESSAGE;
				handler.sendMessage(msg);
			} else {
				Log.e("get definition error", "视频清晰度获取失败");
			}
		}

		@Override
		public void onProcessException(DreamwinException exception) {
			Message msg = new Message();
			msg.obj = GET_DEFINITION_ERROR;
			handler.sendMessage(msg);
		}
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	public class DownloadListViewAdapter extends VideoListViewAdapter {
		
		public DownloadListViewAdapter(Context context, List<VideoInfo> pairs){
			super(context, pairs);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			VideoInfo videoInfo = videoInfos.get(position);
			DownloadListView downloadListView = new DownloadListView(context, videoInfo.getVideoId(), videoInfo.getLabel(), videoInfo.getResId());
			downloadListView.setTag(videoInfo.getVideoId());
			return downloadListView;
		}

	}
	
	public class DownloadListView extends VideoListView {
		private Context context;
		
		public DownloadListView(Context context, String text, String label, int resId) {
			super(context, text, label, resId);
			this.context = context;
			setImageListener();
		}
		
		//设置图片点击事件，点击图片的下载方式使用默认下载方式
		void setImageListener(){
			imageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					title = videoView.getText().toString();

					if (DataSet.hasDownloadInfo(title)) {
						Toast.makeText(context, "文件已存在", Toast.LENGTH_SHORT).show();
						return;
					}

					DownloadController.insertDownloadInfo(videoId, title);
					Toast.makeText(context, "文件已加入下载队列", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
}
