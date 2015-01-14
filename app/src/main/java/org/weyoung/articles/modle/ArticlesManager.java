package org.weyoung.articles.modle;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.weyoung.articles.provider.Articles;

public class ArticlesManager implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = "org.weyoung.articles.ArticlesAdapter";

    private ContentResolver resolver = null;
    private String[] projection = new String[] {
            Articles.ID,
            Articles.TITLE,
            Articles.ABSTRACT,
            Articles.URL
    };
    private Context context;

    public interface ArticleLoader {
        void onArticlesLoad(Cursor cursor);
    }

    public static final int ARTICLES_GET = 007;

    private ArticleLoader loader;

    public ArticlesManager(Context context) {
        this.context = context;
        this.loader = (ArticleLoader)context;
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = new CursorLoader(context);
        switch (id) {
            case ARTICLES_GET:
                loader.setUri(Articles.CONTENT_URI);
                loader.setProjection(projection);
             break;
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (this.loader == null)
            return;
        switch (loader.getId()) {
            case ARTICLES_GET:
                this.loader.onArticlesLoad(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
