package com.mango.datasave.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mango.datasave.R;
import com.mango.datasave.net.AsyncImageLoader;
import com.mango.datasave.tools.DisplayTools;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * Author:Mangoer
 * Time:2018/11/17 17:40
 * Version:
 * Desc:TODO()
 */
public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

    private Context mContext;
    private String[] list;

    private boolean isShouldBeLoaded = true;

    public RecycleAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setList(String[] list) {
        this.list = list;
    }

    public void setmRecyvlerView(RecyclerView mRecyvlerView) {
        mRecyvlerView.addOnScrollListener(scrollListener);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View content = LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(content);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final String imgUrl = list[position];
        // 给 ImageView 设置一个 tag
        holder.view.setTag(imgUrl);
        // 预设一个图片
        holder.view.setImageResource(R.mipmap.ic_launcher);
        if (!TextUtils.isEmpty(imgUrl) && isShouldBeLoaded) {
            AsyncImageLoader.getInstance(mContext).loadImage(holder.view, imgUrl, DisplayTools.dp2px(mContext,80), DisplayTools.dp2px(mContext,80));
        }
    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = (ImageView) itemView.findViewById(R.id.userimage);
        }
    }

    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (SCROLL_STATE_IDLE == newState) {
                isShouldBeLoaded = true;
                notifyDataSetChanged();
            } else {
                isShouldBeLoaded = false;
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };
}
