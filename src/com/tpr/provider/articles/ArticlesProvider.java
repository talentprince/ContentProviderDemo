package com.tpr.provider.articles;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class ArticlesProvider extends ContentProvider {
    private static final String LOG_TAG = "com.tpr.provider.articles.ArticlesProvider";

    private static final String DB_NAME = "Articles.db";
    private static final String DB_TABLE = "ArticlesTable";
    private static final int DB_VERSION = 1;

    private static final String DB_CREATE = "create table " + DB_TABLE + "("
                                            + Articles.ID + " integer primary key autoincrement,"
                                            + Articles.TITLE + " text not null,"
                                            + Articles.ABSTRACT + " text not null"
                                            + Articles.URL + " text not null);";

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(Articles.AUTHORITY, "item/", Articles.ITEM);
        uriMatcher.addURI(Articles.AUTHORITY, "item/#", Articles.ITEM_ID);
        uriMatcher.addURI(Articles.AUTHORITY, "pos/#", Articles.ITEM_POS);
    }

    private static final HashMap<String, String> articleProjectionMap;
    static {
        articleProjectionMap = new HashMap<String, String>();
        articleProjectionMap.put(Articles.ID, Articles.ID);
        articleProjectionMap.put(Articles.TITLE, Articles.TITLE);
        articleProjectionMap.put(Articles.ABSTRACT, Articles.ABSTRACT);
        articleProjectionMap.put(Articles.URL, Articles.URL);
    }

    private DBHelper dbHelper = null;
    private ContentResolver resolver = null;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        resolver = context.getContentResolver();
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);

        Log.i(LOG_TAG, "Article onCreate");
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
        case Articles.ITEM:
            return Articles.CONTENT_TYPE;
        case Articles.ITEM_ID:
        case Articles.ITEM_POS:
            return Articles.CONTENT_ITME_TYPE;
        default:
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if(uriMatcher.match(uri ) != Articles.ITEM) {
            throw new IllegalArgumentException();
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long id = db.insert(DB_TABLE, Articles.ID, values);
        if(id < 0) {
            throw new SQLiteException("Unable insert " + values + "for" + uri);
        }

        Uri newUri = ContentUris.withAppendedId(uri, id);
        resolver.notifyChange(newUri, null);

        return newUri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        Log.i(LOG_TAG, "Provider query " + uri);
        
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, CursorFactory factory,int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
            onCreate(db);
        }
        
    }
}
