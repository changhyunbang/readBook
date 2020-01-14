package com.rooms.android.readbook.list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.rooms.android.readbook.R;
import com.rooms.android.readbook.model.BookData;
import com.rooms.android.readbook.model.PageData;

public class PageItemViewer extends LinearLayout {

    TextView tvTitle;

    public PageItemViewer(Context context) {
        super(context);

        init(context);
    }

    public PageItemViewer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.list_item_page, this, true);

        tvTitle = (TextView)findViewById(R.id.TV_TITLE);
    }

    public void setItem(PageData pageData){

    }
}
