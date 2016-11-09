package com.xrtz.xrlive.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xrtz.xrlive.R;
import com.xrtz.xrlive.adapter.MyAdapter;

import java.util.ArrayList;
import java.util.List;
import android.support.design.widget.TabLayout;
/**
 * HomeFragment，顶部有三个分类，关注，热门，附近
 */
public class HomeFragment extends Fragment {
    ImageView mImageSearch;
    ImageView mImageMore;

    List<Fragment> fragments = new ArrayList<>();
    ViewPager viewPager;
    TabLayout tabLayout;
    MyAdapter adapter;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.home_viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.homeTitleTab);
        List<String> list = new ArrayList<>();

        list.add("关注");
        list.add("热门");
        list.add("附近");

        fragments.add(AttentionFragment.newInstance(null, null));
        fragments.add(HotFragment.newInstance(null, null));
        fragments.add(NearByFragment.newInstance(null, null));
        //ViewPager的adapter
        adapter = new MyAdapter(getChildFragmentManager(), list, fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

}