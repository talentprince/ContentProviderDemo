package org.weyoung.articles;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.weyoung.articles.modle.Article;
import org.weyoung.articles.modle.ArticlesManager;
import org.weyoung.articles.provider.Articles;

public class MainActivity extends Activity {
    private static final String LOG_TAG = "org.weyoung.articles.MainActivity";
    private static final int ADD_ARTICLE_ACTIVITY = 1;
    private static final int EDIT_ARTICLE_ACTIVITY = 2;

    private ArticlesManager articlesManager = null;
    private ListView articleList = null;
    private ArticleObserver articleObserver = null;
    private Button button = null;
    private ArticleAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        articlesManager = new ArticlesManager(this);
        adapter = new ArticleAdapter(this);
        articleList = (ListView) findViewById(R.id.listview_article);
        articleList.setAdapter(adapter);
        articleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
                Article article = articlesManager.getArticleByPosition(position);
                intent.putExtra(Articles.ID, article.getId());
                intent.putExtra(Articles.TITLE, article.getTitle());
                intent.putExtra(Articles.ABSTRACT, article.getAbs());
                intent.putExtra(Articles.URL, article.getUrl());
                startActivityForResult(intent, EDIT_ARTICLE_ACTIVITY);
            }
        });

        articleObserver = new ArticleObserver(new Handler());
        getContentResolver().registerContentObserver(Articles.CONTENT_URI, true, articleObserver);

        button = (Button) findViewById(R.id.button_add);
        button.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), ArticleActivity.class), ADD_ARTICLE_ACTIVITY);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(articleObserver);
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
    private class ArticleObserver extends ContentObserver {

        public ArticleObserver(Handler handler) {
            super(handler);
        }
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            adapter.notifyDataSetChanged();
        }
        
    }

    private class ArticleAdapter extends BaseAdapter {
        private LayoutInflater inflater = null;

        public ArticleAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return articlesManager.getArticleCount();
        }

        @Override
        public Object getItem(int position) {
            return articlesManager.getArticleByPosition(position);
        }

        @Override
        public long getItemId(int position) {
            return articlesManager.getArticleByPosition(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Article article = articlesManager.getArticleByPosition(position);
            if(convertView == null) {
                convertView = inflater.inflate(R.layout.item, null);
            }
            TextView titleView = (TextView) convertView.findViewById(R.id.textview_article_title);
            titleView.setText("Title: " + article.getTitle());
            TextView absView = (TextView) convertView.findViewById(R.id.textview_article_abstract);
            absView.setText("Abstract: " + article.getAbs());
            TextView urlView = (TextView) convertView.findViewById(R.id.textview_article_url);
            urlView.setText("URL: " + article.getUrl());

            return convertView;
        }
    }
}
