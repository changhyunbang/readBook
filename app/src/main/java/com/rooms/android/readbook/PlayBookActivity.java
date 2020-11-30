package com.rooms.android.readbook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rooms.android.readbook.curl.CurlPage;
import com.rooms.android.readbook.curl.CurlView;
import com.rooms.android.readbook.database.DBManager;
import com.rooms.android.readbook.model.AudioData;
import com.rooms.android.readbook.model.PageData;
import com.rooms.android.readbook.utils.AudioPlayer;
import com.rooms.android.readbook.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class PlayBookActivity extends BasePageActivity {

    private static final String TAG = PlayBookActivity.class.getSimpleName();

    ArrayList<PageData> mPages = null;
    MediaPlayer mMediaPlayer = new MediaPlayer();
    TextView tvText = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playbook);

        mCurlView = (CurlView) findViewById(R.id.curl);
        tvText = findViewById(R.id.tv_text);
        tvText.setMovementMethod(new ScrollingMovementMethod());

        String bookId = getIntent().getStringExtra(Constants.KEY_BOOK_ID);
        mPages = DBManager.getInstance(this).selectPageDataByBookId(bookId);

        if (setPageData()) {
            onUpdatePage(0);
        };

        // This is something somewhat experimental. Before uncommenting next
        // line, please see method comments in CurlView.
        // mCurlView.setEnableTouchPressure(true);
    }

    private boolean setPageData() {

        try {
            ArrayList<Drawable> images = new ArrayList<>();

            for (PageData page : mPages) {
                Bitmap bitmap = BitmapFactory.decodeFile(page.getImagePath());
                images.add(new BitmapDrawable(bitmap));
            }

            super.setPage(images.toArray(new Drawable[images.size()]));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    protected void onUpdatePage(int nIndex) {
        Log.d(TAG, "onUpdatePage : " + nIndex);

        if (mPages.isEmpty() || mPages.size() <= nIndex) {
            return;
        }

        try {
            mMediaPlayer.stop();
            Log.d(TAG, "text : " + mPages.get(nIndex).getText());
            tvText.setText(mPages.get(nIndex).getText());
            ArrayList<AudioData> audioDatas = DBManager.getInstance(this).selectAudioData(mPages.get(nIndex).getText());


            if (!audioDatas.isEmpty()) {
                String base64EncodedString = audioDatas.get(0).getAudioData();
                String url = "data:audio/mp3;base64," + base64EncodedString;
                mMediaPlayer.setDataSource(url);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } else {
                Log.d(TAG, "audioDatas.isEmpty()");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
