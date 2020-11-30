package com.rooms.android.readbook;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rooms.android.readbook.curl.CurlPage;
import com.rooms.android.readbook.curl.CurlView;

public abstract class BasePageActivity extends AppCompatActivity {

    private final String TAG = BasePageActivity.class.getSimpleName();

    protected CurlView mCurlView;

    protected abstract void onUpdatePage(int nIndex);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setPage(Drawable[] drawables) {
        int index = 0;
        if (getLastNonConfigurationInstance() != null) {
            index = (Integer) getLastNonConfigurationInstance();
        }

//        Drawable[] tempDrawables = { getResources().getDrawable(R.drawable.obama),
//                getResources().getDrawable(R.drawable.road_rage),
//                getResources().getDrawable(R.drawable.taipei_101),
//                getResources().getDrawable(R.drawable.world) };

        mCurlView.setPageProvider(new BasePageActivity.PageProvider(drawables));
        mCurlView.setSizeChangedObserver(new BasePageActivity.SizeChangedObserver());
        mCurlView.setPageChangedObserver(new BasePageActivity.PageChangedObserver());
        mCurlView.setCurrentIndex(index);
        mCurlView.setBackgroundColor(0xFF202830);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCurlView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCurlView.onResume();
    }

    @Nullable
    @Override
    public Object onRetainCustomNonConfigurationInstance() {

        Log.d(TAG, "getCurrentIndex : " + mCurlView.getCurrentIndex());
        return mCurlView.getCurrentIndex();
    }

    /**
     * Bitmap provider.
     */
    private class PageProvider implements CurlView.PageProvider {


//        public PageProvider(int[] bitmapIds) {
//            mBitmapIds = bitmapIds;
//        }
//        // Bitmap resources.
//        private int[] mBitmapIds;
////                = { R.drawable.obama, R.drawable.road_rage, R.drawable.taipei_101, R.drawable.world };
//
//        @Override
//        public int getPageCount() {
//            return mBitmapIds.length + 1;
//        }
//
//        private Bitmap loadBitmap(int width, int height, int index) {
//
//            Log.d(TAG, "loadBitmap : " + index);
//
//            Bitmap b = Bitmap.createBitmap(width, height,
//                    Bitmap.Config.ARGB_8888);
//            b.eraseColor(0xFFFFFFFF);
//            Canvas c = new Canvas(b);
//            Drawable d = getResources().getDrawable(mBitmapIds[index]);
//
//            int margin = 7;
//            int border = 3;
//            Rect r = new Rect(margin, margin, width - margin, height - margin);
//
//            int imageWidth = r.width() - (border * 2);
//            int imageHeight = imageWidth * d.getIntrinsicHeight()
//                    / d.getIntrinsicWidth();
//            if (imageHeight > r.height() - (border * 2)) {
//                imageHeight = r.height() - (border * 2);
//                imageWidth = imageHeight * d.getIntrinsicWidth()
//                        / d.getIntrinsicHeight();
//            }
//
//            r.left += ((r.width() - imageWidth) / 2) - border;
//            r.right = r.left + imageWidth + border + border;
//            r.top += ((r.height() - imageHeight) / 2) - border;
//            r.bottom = r.top + imageHeight + border + border;
//
//            Paint p = new Paint();
//            p.setColor(0xFFC0C0C0);
//            c.drawRect(r, p);
//            r.left += border;
//            r.right -= border;
//            r.top += border;
//            r.bottom -= border;
//
//            d.setBounds(r);
//            d.draw(c);
//
//            return b;
//        }

        private Drawable[] mDrawableArray;

        public PageProvider(Drawable[] drawableArray) {
            mDrawableArray = drawableArray;
        }

        @Override
        public int getPageCount() {
            return mDrawableArray.length;
        }

        private Bitmap loadBitmap(int width, int height, int index) {

            Log.d(TAG, "loadBitmap : " + index);

            Bitmap b = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            b.eraseColor(0xFFFFFFFF);
            Canvas c = new Canvas(b);
            Drawable d = mDrawableArray[index];

            int margin = 7;
            int border = 3;
            Rect r = new Rect(margin, margin, width - margin, height - margin);

            int imageWidth = r.width() - (border * 2);
            int imageHeight = imageWidth * d.getIntrinsicHeight()
                    / d.getIntrinsicWidth();
            if (imageHeight > r.height() - (border * 2)) {
                imageHeight = r.height() - (border * 2);
                imageWidth = imageHeight * d.getIntrinsicWidth()
                        / d.getIntrinsicHeight();
            }

            r.left += ((r.width() - imageWidth) / 2) - border;
            r.right = r.left + imageWidth + border + border;
            r.top += ((r.height() - imageHeight) / 2) - border;
            r.bottom = r.top + imageHeight + border + border;

            Paint p = new Paint();
            p.setColor(0xFFC0C0C0);
            c.drawRect(r, p);
            r.left += border;
            r.right -= border;
            r.top += border;
            r.bottom -= border;

            d.setBounds(r);
            d.draw(c);

            return b;
        }

        @Override
        public void updatePage(CurlPage page, int width, int height, int index) {

            Log.d(TAG, "updatePage : " + index);

//            if (index == 0) {
//                Bitmap front = loadBitmap(width, height, 0);
//                page.setTexture(front, CurlPage.SIDE_FRONT);
////                page.setColor(Color.rgb(180, 180, 180), CurlPage.SIDE_BACK);
////            } else if (index >= mDrawableArray.length -1) {
////                Bitmap front = loadBitmap(width, height, index);
////                page.setTexture(front, CurlPage.SIDE_BOTH);
////                page.setColor(Color.argb(127, 255, 255, 255), CurlPage.SIDE_BACK);
//            } else
            if (index < mDrawableArray.length){
                Bitmap front = loadBitmap(width, height, index);
                Bitmap back = loadBitmap(width, height, index);
                page.setTexture(front, CurlPage.SIDE_FRONT);
                page.setTexture(back, CurlPage.SIDE_BACK);
            }
//            switch (index) {
//                // First case is image on front side, solid colored back.
//                case 0: {
//                    Bitmap front = loadBitmap(width, height, 0);
//                    page.setTexture(front, CurlPage.SIDE_FRONT);
//                    page.setColor(Color.rgb(180, 180, 180), CurlPage.SIDE_BACK);
//                    break;
//                }
//                // Second case is image on back side, solid colored front.
//                case 1: {
//                    Bitmap back = loadBitmap(width, height, 2);
//                    page.setTexture(back, CurlPage.SIDE_BACK);
//                    page.setColor(Color.rgb(127, 140, 180), CurlPage.SIDE_FRONT);
//                    break;
//                }
//                // Third case is images on both sides.
//                case 2: {
//                    Bitmap front = loadBitmap(width, height, 1);
//                    Bitmap back = loadBitmap(width, height, 3);
//                    page.setTexture(front, CurlPage.SIDE_FRONT);
//                    page.setTexture(back, CurlPage.SIDE_BACK);
//                    break;
//                }
//                // Fourth case is images on both sides - plus they are blend against
//                // separate colors.
//                case 3: {
//                    Bitmap front = loadBitmap(width, height, 2);
//                    Bitmap back = loadBitmap(width, height, 1);
//                    page.setTexture(front, CurlPage.SIDE_FRONT);
//                    page.setTexture(back, CurlPage.SIDE_BACK);
//                    page.setColor(Color.argb(127, 170, 130, 255),
//                            CurlPage.SIDE_FRONT);
//                    page.setColor(Color.rgb(255, 190, 150), CurlPage.SIDE_BACK);
//                    break;
//                }
//                // Fifth case is same image is assigned to front and back. In this
//                // scenario only one texture is used and shared for both sides.
//                case 4:
//                    Bitmap front = loadBitmap(width, height, 0);
//                    page.setTexture(front, CurlPage.SIDE_BOTH);
//                    page.setColor(Color.argb(127, 255, 255, 255),
//                            CurlPage.SIDE_BACK);
//                    break;
//            }

            Log.d(TAG, "getCurrentPage : " + mCurlView.getCurrentPage());

//            onUpdatePage(mCurlView.getCurrentPage());
        }
    }

    /**
     * CurlView size changed observer.
     */
    private class SizeChangedObserver implements CurlView.SizeChangedObserver {
        @Override
        public void onSizeChanged(int w, int h) {

            Log.d(TAG, "onSizeChanged");

            if (w > h) {
                mCurlView.setViewMode(CurlView.SHOW_TWO_PAGES);
                mCurlView.setMargins(.1f, .05f, .1f, .05f);
            } else {
                mCurlView.setViewMode(CurlView.SHOW_ONE_PAGE);
                mCurlView.setMargins(.1f, .1f, .1f, .1f);
            }
        }
    }

    private class PageChangedObserver implements CurlView.PageChangedObserver {

        @Override
        public void onPageChanged(int nPage) {
            Log.d(TAG, "onPageChanged : " + nPage);
            onUpdatePage(nPage);
        }
    }
}
