package com.androiddvptteam.helpme;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * 我的任务界面内的ViewPage的适配器
 */
public class MyTaskTabAdapter extends FragmentPagerAdapter
{
	private List<Fragment> list_fragment;                         //fragment列表
	private List<String> list_Title;                              //tab名的列表

	public MyTaskTabAdapter(FragmentManager fm, List<Fragment> list_fragment, List<String> list_Title)
	{
		super(fm);
		this.list_fragment = list_fragment;
		this.list_Title = list_Title;
	}
		  
	@Override
	public Fragment getItem(int position)
	{
		return list_fragment.get(position);
	}
		  
	@Override
	public int getCount()
	{
		return list_Title.size();
	}
		  
	//此方法用来显示tab上的名字
	@Override
	public CharSequence getPageTitle(int position)
	{
		return list_Title.get(position % list_Title.size());
	}
}  