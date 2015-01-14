package com.tpr.articles;

import java.util.LinkedList;

import com.tpr.articles.provider.Articles;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class ArticlesAdapter {
    private static final String LOG_TAG = "com.tpr.articles.ArticlesAdapter";

    private ContentResolver resolver = null;
    private String[] projection = new String[] {
            Articles.ID,
            Articles.TITLE,
            Articles.ABSTRACT,
            Articles.URL
    };

    public ArticlesAdapter(Context context) {
        resolver = context.getContentResolver();
    }

    public long insertArticle(Article article) {
        ContentValues values = new ContentValues();
        values.put(Articles.TITLE, article.getTitle());
        values.put(Articles.ABSTRACT, article.getAbs());
        values.put(Articles.URL, article.getUrl());

        Uri uri = resolver.insert(Articles.CONTENT_URI, values);
        String itemID = uri.getPathSegments().get(1);
        Log.i(LOG_TAG, "insertArticle itemID " + itemID);

        return Integer.valueOf(itemID).longValue();
    }

    public boolean updateArticle(Article article) {
        Uri uri = ContentUris.withAppendedId(Articles.CONTENT_URI, article.getId());

        ContentValues values = new ContentValues();
        values.put(Articles.TITLE, article.getTitle());
        values.put(Articles.ABSTRACT, article.getAbs());
        values.put(Articles.URL, article.getUrl());

        int count = resolver.update(uri, values, null, null);
        return count > 0;
    }

    public boolean removeArticle(int id) {
        Uri uri = ContentUris.withAppendedId(Articles.CONTENT_URI, id);

        int count = resolver.delete(uri, null, null);
        return count > 0;
    }

    public LinkedList<Article> getAllArticles() {
        LinkedList<Article> articles = new LinkedList<Article>();

        Cursor cursor = resolver.query(Articles.CONTENT_URI, projection, null, null, Articles.DEFUAL_SORT_ORDER);
        if(cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                String abs = cursor.getString(2);
                String url = cursor.getString(3);
                articles.add(new Article(id, title, abs, url));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return articles;
    }

    public int getArticleCount() {
        int count = 0;

        Cursor cursor = resolver.query(Articles.CONTENT_URI, projection, null, null, Articles.DEFUAL_SORT_ORDER);
        if(cursor.moveToFirst()) {
            do {
                count++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return count;
    }

    public Article getArticleById(int id) {
        Uri uri = ContentUris.withAppendedId(Articles.CONTENT_URI, id);

        Cursor cursor = resolver.query(uri, projection, null, null, Articles.DEFUAL_SORT_ORDER);
        if(!cursor.moveToFirst())
            return null;

        String title = cursor.getString(1);
        String abs = cursor.getString(2);
        String url = cursor.getString(3);
        cursor.close();
        return new Article(id, title, abs, url);
    }

    public Article getArticleByPosition(int pos) {
        Uri uri = ContentUris.withAppendedId(Articles.CONTENT_POS_URI, pos);

        Cursor cursor = resolver.query(uri, projection, null, null, Articles.DEFUAL_SORT_ORDER);
        if(!cursor.moveToFirst())
            return null;

        int id = cursor.getInt(0);
        String title = cursor.getString(1);
        String abs = cursor.getString(2);
        String url = cursor.getString(3);
        cursor.close();
        return new Article(id, title, abs, url);
    }
}
