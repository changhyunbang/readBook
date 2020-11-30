package com.rooms.android.readbook;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.rooms.android.readbook.database.DBManager;
import com.rooms.android.readbook.list.BookItemViewer;
import com.rooms.android.readbook.model.BookData;
import com.rooms.android.readbook.utils.Constants;

import java.util.ArrayList;

public class BookListActivity extends AppCompatActivity {

    Context mContext;
    GridView mGvBookList;
    BooksAdapter mBooksAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        setContentView(R.layout.activity_booklist);

        mGvBookList = (GridView)findViewById(R.id.gridView);

        mBooksAdapter = new BooksAdapter();
        mGvBookList.setAdapter(mBooksAdapter);

        mGvBookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BookData bookData = (BookData)view.getTag();

                if (TextUtils.isEmpty(bookData.getBookId())) {

                    final EditText input = new EditText(mContext);
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Title");
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String bookId = DBManager.getInstance(mContext).insertBookData(input.getText().toString(), "");

                            Intent intent = new Intent(mContext, CreateBookActivity.class);
                            intent.putExtra(Constants.KEY_BOOK_ID, bookId);
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();

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

        mBooksAdapter.clearData();
        mBooksAdapter.addItem(new BookData("ADD", ""));

        ArrayList<BookData> books = DBManager.getInstance(this).selectBookListData();

        for (BookData bookData : books) {
            mBooksAdapter.addItem(bookData);
        }

        mBooksAdapter.notifyDataSetChanged();
    }

    class BooksAdapter extends BaseAdapter {
        ArrayList<BookData> items = new ArrayList<BookData>();
        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(BookData bookData){
            items.add(bookData);
        }

        @Override
        public BookData getItem(int i) {
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
            BookItemViewer bookItemViewer = new BookItemViewer(getApplicationContext(), new BookItemViewer.IBookItemListener() {
                @Override
                public void onAction(BookItemViewer.ACTION_TYPE actionType, String bookId) {

                    Intent intent = null;

                    switch (actionType) {
                        case BOOK_PLAY:
                            intent = new Intent(mContext, PlayBookActivity.class);
                            intent.putExtra(Constants.KEY_BOOK_ID, bookId);
                            break;
                        case BOOK_EDIT:
                            intent = new Intent(mContext, CreateBookActivity.class);
                            intent.putExtra(Constants.KEY_BOOK_ID, bookId);
                            break;
                    }

                    if (intent != null) {
                        startActivity(intent);
                    }
                }
            });
            bookItemViewer.setItem(items.get(i));
            bookItemViewer.setTag(items.get(i));
            return bookItemViewer;
        }
    }
}
