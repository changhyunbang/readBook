package com.rooms.android.readbook.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.util.ArrayUtils;
import com.rooms.android.readbook.model.AudioData;
import com.rooms.android.readbook.model.BookData;
import com.rooms.android.readbook.model.PageData;
import com.rooms.android.readbook.tts.TTSManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DBManager {

    static final String TAG = DBManager.class.getSimpleName();
    static final String DATABASE_NAME = "READ_BOOK_DB";
    static final int DATABASE_VERSION = 1;
    static final String TABLE_NAME_BOOK = "TABLE_BOOK";
    static final String TABLE_NAME_PAGE = "TABLE_PAGE";
    static final String TABLE_NAME_AUDIO = "TABLE_AUDIO";

    static DBManager mInstance;
    Context mContext = null;
    SQLiteOpenHelper mSQLiteOpenHelper = null;
    SQLiteDatabase mSqlDB = null;

    public static DBManager getInstance(Context context) {
        if(mInstance == null) {
            synchronized (TTSManager.class) {
                if(mInstance == null) {
                    mInstance = new DBManager(context);
                }
            }
        }
        return mInstance;
    }

    public DBManager(Context context) {

        mContext = context;

        mSQLiteOpenHelper = new MySQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public ArrayList<BookData> selectBookListData() {

        return selectBookListDataByBookId(null);
    }

    public ArrayList<BookData> selectBookListDataByBookId(@Nullable String book_id) {

        ArrayList<BookData> resultData = new ArrayList<>();

        Cursor c = null;
        String query = "";

        try {
            mSqlDB = mSQLiteOpenHelper.getReadableDatabase();

            if (book_id != null) {
                query = "SELECT * FROM "+TABLE_NAME_BOOK+" WHERE book_id=? ORDER BY book_id desc";
                c = mSqlDB.rawQuery(query, new String[] {book_id});
            } else {
                query = "SELECT * FROM "+TABLE_NAME_BOOK+" ORDER BY book_id desc";
                c = mSqlDB.rawQuery(query, null);
            }

            while(c.moveToNext()){

                BookData bookData = new BookData();

                bookData.setBookId(c.getString(0));
                bookData.setBookName(c.getString(1));
                bookData.setImagePath(c.getString(2));

                Log.d(TAG, bookData.toString());

                resultData.add(bookData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return resultData;
    }

    public PageData selectPageData(String book_id, String page_id) {

        PageData resultData = null;

        Cursor c = null;
        String query = "";

        try {
            mSqlDB = mSQLiteOpenHelper.getReadableDatabase();

            query = "SELECT * FROM "+TABLE_NAME_PAGE+" WHERE book_id=? AND page_id=?";
            c = mSqlDB.rawQuery(query, new String[] {book_id, page_id});

            while(c.moveToNext()){

                resultData = new PageData();

                resultData.setPageId(c.getString(0));
                resultData.setBookId(c.getString(1));
                resultData.setPageIndex(c.getString(2));
                resultData.setImagePath(c.getString(3));
                resultData.setText(c.getString(4));

                Log.d(TAG, resultData.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return resultData;
    }

    public ArrayList<PageData> selectPageDataByBookId(@Nullable String book_id) {

        ArrayList<PageData> resultData = new ArrayList<>();;

        Cursor c = null;
        String query = "";

        try {
            mSqlDB = mSQLiteOpenHelper.getReadableDatabase();

            if (book_id != null) {
                query = "SELECT * FROM "+TABLE_NAME_PAGE+" WHERE book_id=? ORDER BY page_index desc";
                c = mSqlDB.rawQuery(query, new String[] {book_id});
            } else {
                query = "SELECT * FROM "+TABLE_NAME_PAGE+" ORDER BY page_index desc";
                c = mSqlDB.rawQuery(query, null);
            }

            while(c.moveToNext()){

                PageData pageData = new PageData();

                pageData.setPageId(c.getString(0));
                pageData.setBookId(c.getString(1));
                pageData.setPageIndex(c.getString(2));
                pageData.setImagePath(c.getString(3));
                pageData.setText(c.getString(4));

                Log.d(TAG, pageData.toString());

                resultData.add(pageData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return resultData;
    }

    public ArrayList<AudioData> selectAudioData(String text) {

        ArrayList<AudioData> resultData = new ArrayList<>();

        Cursor c = null;
        String query = "";

        try {
            mSqlDB = mSQLiteOpenHelper.getReadableDatabase();

            if (text != null) {
                query = "SELECT * FROM "+TABLE_NAME_AUDIO+" WHERE text=?";
                c = mSqlDB.rawQuery(query, new String[] {text});
            } else {
                query = "SELECT * FROM "+TABLE_NAME_AUDIO;
                c = mSqlDB.rawQuery(query, null);
            }

            while(c.moveToNext()){
                AudioData audioData = new AudioData();

                audioData.setText(c.getString(0));
                audioData.setAudioData(c.getString(1));

                Log.d(TAG, audioData.toString());

                resultData.add(audioData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return resultData;
    }

    public String insertAudioData(AudioData audioData) {
        return insertAudioData(audioData.getText(), audioData.getAudioData());
    }

    public String insertAudioData(String text, String audio_data) {

        Log.d(TAG, "insertAudioData text : " + text + " audio_data : " + audio_data);

        String resultAudioId = "";

        ArrayList<AudioData> resultData = new ArrayList<>();

        Cursor c = null;
        String query = "";

        try {

            boolean isExist = !selectAudioData(text).isEmpty();

            mSqlDB = mSQLiteOpenHelper.getWritableDatabase();

            ContentValues value = new ContentValues();
            value.put("text", text);
            value.put("audio_data", audio_data);

            if (isExist) {
                resultAudioId = String.valueOf(mSqlDB.update(TABLE_NAME_AUDIO, value, "text=" + text, null));
            } else {
                resultAudioId = String.valueOf(mSqlDB.insert(TABLE_NAME_AUDIO, null, value));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mSqlDB.close();
        }

        return resultAudioId;
    }

    public String insertBookData(BookData bookData) {

        return insertBookData(bookData.getBookName(), bookData.getImagePath());
    }

    public String insertBookData(String book_name, String image_path) {

        String resultBookId = "";

        try {
            mSqlDB = mSQLiteOpenHelper.getWritableDatabase();

            ContentValues value = new ContentValues();
            value.put("book_name", book_name);
            value.put("image_path", image_path);

            resultBookId = String.valueOf(mSqlDB.insert(TABLE_NAME_BOOK, null, value));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mSqlDB.close();
        }

        return resultBookId;
    }

    public String insertPageData(PageData pageData) {

        return insertPageData(pageData.getBookId(), pageData.getPageIndex(), pageData.getImagePath(), pageData.getText());
    }

    public String insertPageData(@NonNull String book_id, String page_index, String image_path, String text) {

        String resultPageId = "";

        try {
            mSqlDB = mSQLiteOpenHelper.getWritableDatabase();

            ContentValues value = new ContentValues();
            value.put("book_id", book_id);
            value.put("page_index", page_index);
            value.put("image_path", image_path);
            value.put("text", text);

            resultPageId = String.valueOf(mSqlDB.insert(TABLE_NAME_PAGE, null, value));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mSqlDB.close();
        }

        return resultPageId;
    }

    public class MySQLiteOpenHelper extends SQLiteOpenHelper {

        private final String TAG = "MySQLiteOpenHelper";

        public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

            String sql1 = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_BOOK +
                    "(book_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "book_name TEXT," +
                    "image_path TEXT)";

            sqLiteDatabase.execSQL(sql1);

            String sql2 = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_PAGE +
                    "(page_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "book_id TEXT," +
                    "page_index TEXT," +
                    "image_path TEXT," +
                    "text TEXT)";

            sqLiteDatabase.execSQL(sql2);

            String sql3 = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_AUDIO +
                    "(text TEXT PRIMARY KEY, " +
                    "audio_data TEXT)";

            sqLiteDatabase.execSQL(sql3);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            String sql1="drop table if exists " + TABLE_NAME_BOOK;
            sqLiteDatabase.execSQL(sql1);

            String sql2="drop table if exists " + TABLE_NAME_PAGE;
            sqLiteDatabase.execSQL(sql2);

            String sql3="drop table if exists " + TABLE_NAME_AUDIO;
            sqLiteDatabase.execSQL(sql3);

            onCreate(sqLiteDatabase);
        }
    }
}
