package com.mango.datasave;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;

import com.mango.datasave.adapter.RecycleAdapter;
import com.mango.datasave.view.DividerItemDecoration;

/**
 * @Description TODO()
 * @author cxy
 * @Date 2018/11/14 10:40
 */
public class ItemPictureAct extends AppCompatActivity {

    private ListView listview;

    int[] images = new int[] {
            R.mipmap.ic_1,R.mipmap.ic_2,R.mipmap.ic_3,
            R.mipmap.ic_4,R.mipmap.ic_5,R.mipmap.ic_6,
            R.mipmap.ic_7,R.mipmap.ic_8,R.mipmap.ic_9,
            R.mipmap.ic_10,R.mipmap.ic_11,R.mipmap.ic_12,
            R.mipmap.ic_13,R.mipmap.ic_14,R.mipmap.ic_15,
            R.mipmap.ic_16,R.mipmap.ic_17,R.mipmap.ic_18,
            R.mipmap.ic_19,R.mipmap.ic_20,R.mipmap.ic_21,
            R.mipmap.ic_22,R.mipmap.ic_1,R.mipmap.ic_2,
            R.mipmap.ic_5,R.mipmap.ic_4,R.mipmap.ic_3};

    private RecyclerView recyvlerView;
    private RecycleAdapter recycleAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_picture2);
        /*listview = (ListView) findViewById(R.id.listview);
        PictureAdapter adapter = new PictureAdapter(this, images);
        listview.setAdapter(adapter);*/
        

        recyvlerView = findViewById(R.id.recyvlerView);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);

//        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);

        recyvlerView.setLayoutManager(gridLayoutManager);

        //添加分割线
        recyvlerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        recycleAdapter = new RecycleAdapter(this);
        recycleAdapter.setList(images);
        recycleAdapter.setmRecyvlerView(recyvlerView);
        recyvlerView.setAdapter(recycleAdapter);


    }

}
