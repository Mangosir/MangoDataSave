package com.mango.datasave.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.mango.clib.net.EasyImageLoader;
import com.mango.clib.tools.DisplayTools;
import com.mango.datasave.R;

/**
 * @Description TODO()
 * @author cxy
 * @Date 2018/11/14 10:43
 */
public class PictureAdapter extends BaseAdapter {

    private String[] list;
    private Context context;

    public PictureAdapter(Context context, String[] list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.length;
    }

    @Override
    public Object getItem(int position) {
        return list[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder ;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, null);
            holder.img = (ImageView) convertView.findViewById(R.id.userimage);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final String imgUrl = list[position];
        // 给 ImageView 设置一个 tag
        holder.img.setTag(imgUrl);
        // 预设一个图片
        holder.img.setImageResource(R.mipmap.ic_launcher);

        if (!TextUtils.isEmpty(imgUrl)) {
            EasyImageLoader.getInstance(context).loadImage(holder.img, imgUrl, DisplayTools.dip2px(context,40),DisplayTools.dip2px(context,40));
        }

        return convertView;
    }

    class ViewHolder {
        ImageView img;
    }
}
