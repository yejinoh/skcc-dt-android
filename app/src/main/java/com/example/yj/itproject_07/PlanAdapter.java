package com.example.yj.itproject_07;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2018-02-06.
 */

public class PlanAdapter extends PagerAdapter {

    List<String> listPlanNames;
    List<String> listPlanCosts;
    Context context;
    LayoutInflater layoutInflater;

    public PlanAdapter(List<String> listString1, List<String> listString2, Context context) {
        this.listPlanNames=listString1;
        this.listPlanCosts=listString2;

        this.context = context;
        layoutInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return listPlanNames.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.plan_item, container, false);
        TextView textViewPlanName = (TextView)view.findViewById(R.id.textViewPlanName);
        TextView textViewPlanCost = (TextView)view.findViewById(R.id.textViewPlanCost);

        textViewPlanName.setText(listPlanNames.get(position)); //setImageResource
        textViewPlanCost.setText(listPlanCosts.get(position)); //setImageResource

        container.addView(view);
        return view;
    }

    @Override
    public void startUpdate(ViewGroup container) {
        // 위로 날릴 때 사용?
        super.startUpdate(container);
    }

}