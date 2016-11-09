package com.xrtz.xrlive.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xrtz.xrlive.R;
import com.xrtz.xrlive.adapter.NearByAdapter;
import com.xrtz.xrlive.bean.NearByBean;

import java.util.ArrayList;
import java.util.List;

/**
 * HomeFragment里显示的 附近 的子界面
 */
public class NearByFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    RecyclerView recycleView;
    public NearByFragment() {
    }

    public static NearByFragment newInstance(String param1, String param2) {
        NearByFragment fragment = new NearByFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_near_by, container, false);
        recycleView = (RecyclerView) view.findViewById(R.id.recycleView);
        List<NearByBean> list = new ArrayList<>();
        list.add(new NearByBean(R.mipmap.rank_1,R.mipmap.nearby1,"1.1km"));
        list.add(new NearByBean(R.mipmap.rank_1,R.mipmap.nearby2,"1.9km"));
        list.add(new NearByBean(R.mipmap.rank_2,R.mipmap.nearby3,"2.5km"));
        list.add(new NearByBean(R.mipmap.rank_3,R.mipmap.nearby4,"4.1km"));
        recycleView.setLayoutManager(new GridLayoutManager(container.getContext().getApplicationContext(),3));
        recycleView.setAdapter(new NearByAdapter(list));
        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

}
