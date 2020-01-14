package com.rooms.android.readbook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rooms.android.readbook.database.DBManager;
import com.rooms.android.readbook.list.PageItemViewer;
import com.rooms.android.readbook.model.BookData;
import com.rooms.android.readbook.model.PageData;

import java.util.ArrayList;

public class CreateBookActivity extends AppCompatActivity {

    public static final String KEY_BOOK_ID = "BOOK_ID";

    Context mContext;
    String mBookId = "";
    TextView mTvTitle;
    GridView mGvPageList;
    PagesAdapter mPagesAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        mBookId = getIntent().getStringExtra(KEY_BOOK_ID);

        setContentView(R.layout.activity_createbook);

        mTvTitle = (TextView)findViewById(R.id.TV_TITLE);
        mGvPageList = (GridView)findViewById(R.id.GV_LIST);

        mPagesAdapter = new PagesAdapter();
        mGvPageList.setAdapter(mPagesAdapter);

        mGvPageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PageData pageData = (PageData)view.getTag();

                if (TextUtils.isEmpty(pageData.getPageId())) {
                    Intent intent = new Intent(mContext, CreatePageActivity.class);
                    intent.putExtra(CreatePageActivity.KEY_BOOK_ID, mBookId);
                    startActivity(intent);
                } else {
                    
                }
            }
        });

        refreshData();
    }

    @Override
    protected void onResume() {
        refreshData();
        super.onResume();
    }

    private void refreshData() {

        mPagesAdapter.clearData();

        try {
            ArrayList<BookData> books = DBManager.getInstance(this).selectBookListDataByBookId(mBookId);
            ArrayList<PageData> pages = DBManager.getInstance(this).selectPageDataByBookId(mBookId);

            mTvTitle.setText(books.get(0).getBookName());

            for (PageData pageData : pages) {
                mPagesAdapter.addItem(pageData);
            }

        } catch (Exception e) {

        } finally {
            mPagesAdapter.addItem(new PageData());
            mPagesAdapter.notifyDataSetChanged();
        }
    }

    class PagesAdapter extends BaseAdapter {
        ArrayList<PageData> items = new ArrayList<PageData>();
        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(PageData pageData){
            items.add(pageData);
        }

        @Override
        public PageData getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public void clearData() {
            // clear the data
            items.clear();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            PageItemViewer pageItemViewer = new PageItemViewer(getApplicationContext());
            pageItemViewer.setItem(items.get(i));
            pageItemViewer.setTag(items.get(i));
            return pageItemViewer;
        }
    }
}
