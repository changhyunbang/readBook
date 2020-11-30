package com.rooms.android.readbook.list;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.rooms.android.readbook.R;
import com.rooms.android.readbook.database.DBManager;
import com.rooms.android.readbook.model.BookData;
import com.rooms.android.readbook.model.PageData;

import java.util.ArrayList;
import java.util.Arrays;

public class BookItemViewer extends LinearLayout {

    public static enum ACTION_TYPE {
        BOOK_PLAY,
        BOOK_EDIT,
    }
    public interface IBookItemListener {
        public void onAction(ACTION_TYPE actionType, String bookId);
    }

    TextView tvTitle;
    ImageView ivImage;
    Button btnPlay;
    Button btnEdit;
    IBookItemListener listener;

    public BookItemViewer(Context context, IBookItemListener listener) {
        super(context);
        init(context);

        this.listener = listener;
    }

    public BookItemViewer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.list_item_book, this, true);

        tvTitle = (TextView)findViewById(R.id.TV_TITLE);
        ivImage = (ImageView)findViewById(R.id.IV_IMAGE);
        btnPlay = (Button)findViewById(R.id.BTN_PLAY);
        btnEdit = (Button)findViewById(R.id.BTN_EDIT);
    }

    public void setItem(final BookData bookData){

        tvTitle.setText(bookData.getBookName());
        btnPlay.setVisibility(View.GONE);
        btnEdit.setVisibility(View.GONE);
        btnPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onAction(ACTION_TYPE.BOOK_PLAY, bookData.getBookId());
                }
            }
        });

        btnEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onAction(ACTION_TYPE.BOOK_EDIT, bookData.getBookId());
                }
            }
        });

        if (TextUtils.isEmpty(bookData.getBookId())) {

        } else {
            ArrayList<PageData> pages = DBManager.getInstance(getContext()).selectPageDataByBookId(bookData.getBookId());
            if (pages != null && !pages.isEmpty()) {
                Bitmap bitmap = BitmapFactory.decodeFile(pages.get(0).getImagePath());
                ivImage.setImageBitmap(bitmap);
                btnPlay.setVisibility(View.VISIBLE);
                btnEdit.setVisibility(View.VISIBLE);
            }
        }
    }
}
