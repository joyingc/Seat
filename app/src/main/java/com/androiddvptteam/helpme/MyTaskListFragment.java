package com.androiddvptteam.helpme;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.androiddvptteam.helpme.MyTaskActivity.*;

public class MyTaskListFragment extends BaseFragment
{
	public static MyApplication myApplication;//生成MyTaskListFragment对象时不一定能getActivity()，所以通过这种方式获得MyApplication实例
	public static MyTaskActivity myTaskActivity;//同上
	private MissionAdapter adapter;
	public SwipeRefreshLayout swipeRefreshLayout;

	//google不让搞Fragment的构造函数，我能怎么办，我也很无奈啊
	private int tabType = -1;
	public void setTabType(int tabType)
	{
		if (this.tabType == -1)
			this.tabType = tabType;
		else
			Log.e(TAG+getTag(), "Multiple setting tab type.");
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(TAG+tabType, "onCreate");
		adapter = new MissionAdapter(new ArrayList<Mission>(), myTaskActivity);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		Log.d(TAG+tabType, "onCreateView");
		View view = inflater.inflate(R.layout.my_task_list_fragment, container, false);
		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.myTask_recyclerView);
		LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
		recyclerView.setLayoutManager(layoutManager);
		recyclerView.setAdapter(adapter);
		swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.myTask_swipeRefresh);
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						myApplication.refreshMyMissions();
						myTaskActivity.runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								refreshMissionList();
							}
						});
					}
				}).start();
			}
		});
		refreshMissionList();
		return view;
	}

	/**
	 * 刷新列表，基于已刷新的myApplication.myMissions
	 * */
	public void refreshMissionList()
	{
		Log.d(TAG+tabType, "refreshMissionList");

		PersonalInformation personalInformation = myApplication.getPersonalInformation();
		List<Mission> myMissions = myApplication.myMissions;
		if (myMissions == null || personalInformation == null)
		{
			adapter.myMissionList = new ArrayList<>();
			return;
		}
		switch (tabType)
		{
			case ALL_TAB:
				adapter.myMissionList.clear();
				for (Mission mission : myMissions)
					adapter.myMissionList.add(mission);
				break;
			case ACCEPTED_TAB:
				adapter.myMissionList.clear();
				for(Mission mission : myMissions)
					if (mission.getRecipient() != null
						&& mission.getRecipient().getSchoolNumber().equals(personalInformation.getSchoolNumber())
						&& (mission.getState() == Mission.STATE_FINISHED || mission.getState() == Mission.STATE_CANCELED))
						adapter.myMissionList.add(mission);
			break;
			case RELEASED_TAB:
				adapter.myMissionList.clear();
				for(Mission mission : myMissions)
					if (mission.getPublisher().getSchoolNumber().equals(personalInformation.getSchoolNumber()))
						adapter.myMissionList.add(mission);
				break;
			case DOING_TAB:
				adapter.myMissionList.clear();
				for(Mission mission : myMissions)
					if (mission.getRecipient() != null
						&& mission.getRecipient().getSchoolNumber().equals(personalInformation.getSchoolNumber())
						&& mission.getState() == Mission.STATE_DOING)
						adapter.myMissionList.add(mission);
				break;
		}
		myTaskActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				adapter.notifyDataSetChanged();
				swipeRefreshLayout.setRefreshing(false);
			}
		});
	}
}
