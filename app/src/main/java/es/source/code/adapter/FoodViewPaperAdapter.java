package es.source.code.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class FoodViewPaperAdapter extends PagerAdapter{
    private List<View> mViewList;
    private List<String> mTitleLists;

    public FoodViewPaperAdapter(List<View>mViewList,List<String>mTitleLists){
        this.mViewList = mViewList;
        this.mTitleLists = mTitleLists;
    }

    @Override
    public int getCount() {
        return mTitleLists.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view==o;
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        container.addView(mViewList.get(position));           //添加页卡
        return mViewList.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(mViewList.get(position));         //删除页卡
    }

    public CharSequence getPageTitle(int position){
        return mTitleLists.get(position);             //页卡标题
    }
}
