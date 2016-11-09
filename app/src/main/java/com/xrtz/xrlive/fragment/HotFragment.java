package com.xrtz.xrlive.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.xrtz.xrlive.R;
import com.xrtz.xrlive.activity.SurfacePlayerVideoActivity;
import com.xrtz.xrlive.adapter.HotAdapter;
import com.xrtz.xrlive.bean.User;
import com.xrtz.xrlive.common.CommonValues;
import com.xrtz.xrlive.iservice.UserService;
import com.xrtz.xrlive.listener.RecyclerViewClickListener;
import com.xrtz.xrlive.response.UserListResponse;
import com.xrtz.xrlive.view.EmptyRecyclerView;
import com.xrtz.xrlive.view.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * HomeFragment里显示的 热门 的子界面
 */
public class HotFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    EmptyRecyclerView recyclerView;
    HotAdapter hotAdapter;
    List<User> list = new ArrayList<>();
    //List<HotLiveBean> list = new ArrayList<>();
    public HotFragment() {

    }

    public static HotFragment newInstance(String param1, String param2) {
        HotFragment fragment = new HotFragment();
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
        View view = inflater.inflate(R.layout.fragment_hot, container, false);
        recyclerView = (EmptyRecyclerView) view.findViewById(R.id.recycleView);

        hotAdapter = new HotAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext().getApplicationContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.addItemDecoration(new RecycleViewDivider(container.getContext().getApplicationContext(), LinearLayoutManager.VERTICAL, 5, R.drawable.divider_mileage));
        recyclerView.setAdapter(hotAdapter);

        View emptyView = view.findViewById(R.id.hot_empty);
        recyclerView.setEmptyView(emptyView);//设置空的View

        //调用RecyclerView#addOnItemTouchListener方法能添加一个RecyclerView.OnItemTouchListener对象
        recyclerView.addOnItemTouchListener(new RecyclerViewClickListener(container.getContext().getApplicationContext(),new RecyclerViewClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getContext(), SurfacePlayerVideoActivity.class);
                //intent.putExtra("path","rtmp://211.149.239.170:1935/live/");//默认的测试的看视频的地址

                intent.putExtra("path","rtmp://211.149.239.170:1935/live/"+list.get(position).getUserId());

                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                //Toast.makeText(MainActivity.this,"Long Click "+mData.get(position),Toast.LENGTH_SHORT).show();
            }
        }));

        //开始提交数据
        Retrofit retrofit = new Retrofit.Builder().baseUrl(CommonValues.BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();
        //{"status":1,"data":{"userNicheng":"abcabc","userId":13,"userPassword":"123456","userName":"abcabc","userEmail":"abcabc@126.com"},"msg":"注册成功！"}
        UserService userService = retrofit.create(UserService.class);
        userService.queryAllUser().subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<UserListResponse>() {
            @Override
            public void call(UserListResponse userListResponse) {
                if(userListResponse!=null &&userListResponse.getStatus()==1){
                    List<User> userList = userListResponse.getData();
                    list.clear();
                    list.addAll(userList);
                    hotAdapter.notifyDataSetChanged();
                }
            }
        });
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
