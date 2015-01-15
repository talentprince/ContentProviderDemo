package org.weyoung.articles;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.weyoung.articles.modle.Article;
import org.weyoung.articles.modle.ArticlesManager;
import org.weyoung.articles.provider.Articles;

public class MainActivity extends Activity implements ArticlesManager.ArticleLoader{
    private static final String LOG_TAG = "org.weyoung.articles.MainActivity";

    private static final int ADD_ARTICLE_ACTIVITY = 1;
    private static final int EDIT_ARTICLE_ACTIVITY = 2;

    private ArticlesManager articlesManager = null;
    private ListView articleList = null;
    private ArticleAdapter articleAdapter = null;

    private Button button = null;
    private LoaderManager loaderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        articlesManager = new ArticlesManager(this);
        articleList = (ListView) findViewById(R.id.listview_article);
        articleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
                Article article = (Article) articleList.getAdapter().getItem(position);
                intent.putExtra(Articles.ID, article.getId());
                intent.putExtra(Articles.TITLE, article.getTitle());
                intent.putExtra(Articles.ABSTRACT, article.getAbs());
                intent.putExtra(Articles.URL, article.getUrl());
                startActivityForResult(intent, EDIT_ARTICLE_ACTIVITY);
            }
        });

        button = (Button) findViewById(R.id.button_add);
        button.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), ArticleActivity.class), ADD_ARTICLE_ACTIVITY);
            }
        });

        loaderManager = getLoaderManager();
        loaderManager.initLoader(ArticlesManager.ARTICLES_GET, null, articlesManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        case ADD_ARTICLE_ACTIVITY: {
            if(resultCode == Activity.RESULT_OK) {
                String title = data.getStringExtra(Articles.TITLE);
                String abs = data.getStringExtra(Articles.ABSTRACT);
                String url = data.getStringExtra(Articles.URL);
                articlesManager.insertArticle(new Article(-1, title, abs, url));
            }
            break;
        }
        case EDIT_ARTICLE_ACTIVITY: {
            if(resultCode == Activity.RESULT_OK) {
                int action = data.getIntExtra(ArticleActivity.EDIT_ARTICLE_ACTION, -1);
                int id = data.getIntExtra(Articles.ID, -1);
                if(action == ArticleActivity.MODIFY_ARTICLE) {
                    String title = data.getStringExtra(Articles.TITLE);
                    String abs = data.getStringExtra(Articles.ABSTRACT);
                    String url = data.getStringExtra(Articles.URL);
                    articlesManager.updateArticle(new Article(id, title, abs, url));
                }else {
                    articlesManager.removeArticle(id);
                }
            }
            break;
        }
        }
    }

    //==========================================
    // Article Loader Callback
    //==========================================
    @Override
    public void onArticlesLoad(Cursor cursor) {
        if(articleAdapter == null) {
            articleAdapter = new ArticleAdapter(this, cursor, true);
            articleList.setAdapter(articleAdapter);
        }else {
            articleAdapter.changeCursor(cursor);
        }
    }

    class ArticleAdapter extends CursorAdapter {
        private LayoutInflater inflater = null;

        public ArticleAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View convertView = inflater.inflate(R.layout.item, parent, false);
            populateView(convertView, cursor);
            return convertView;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            populateView(view, cursor);
        }

        @Override
        public Object getItem(int position) {
            Cursor cursor = (Cursor) super.getItem(position);
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String abs = cursor.getString(2);
            String url = cursor.getString(3);
            return new Article(id, title, abs, url);
        }

        private void populateView(View view, Cursor cursor) {
            String title = cursor.getString(1);
            String abs = cursor.getString(2);
            String url = cursor.getString(3);
            TextView titleView = (TextView) view.findViewById(R.id.textview_article_title);
            titleView.setText("Title: " + title);
            TextView absView = (TextView) view.findViewById(R.id.textview_article_abstract);
            absView.setText("Abstract: " + abs);
            TextView urlView = (TextView) view.findViewById(R.id.textview_article_url);
            urlView.setText("URL: " + url);
        }
    }

}
