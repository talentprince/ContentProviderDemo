package org.weyoung.articles;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import org.weyoung.articles.provider.Articles;

public class ArticleActivity extends Activity implements OnClickListener{
    private final static String LOG_TAG = "org.weyoung.articles.ArticleActivity";

    public final static String EDIT_ARTICLE_ACTION = "EDIT_ARTICLE_ACTION";
    public final static int MODIFY_ARTICLE = 1;
    public final static int DELETE_ARTICLE = 2;

    private int articleId = -1;

    private EditText titleEdit = null;
    private EditText abstractEdit = null;
    private EditText urlEdit = null;

    private Button addButton = null;
    private Button modifyButton = null;
    private Button deleteButton = null;
    private Button cancelButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article);
        titleEdit = (EditText) findViewById(R.id.edit_article_title);
        abstractEdit = (EditText) findViewById(R.id.edit_article_abstract);
        urlEdit = (EditText) findViewById(R.id.edit_article_url);

        addButton = (Button) findViewById(R.id.button_add_article);
        modifyButton = (Button) findViewById(R.id.button_modify);
        deleteButton = (Button) findViewById(R.id.button_delete);
        cancelButton = (Button) findViewById(R.id.button_cancel);
        addButton.setOnClickListener(this);
        modifyButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        Intent intent = getIntent();
        articleId = intent.getIntExtra(Articles.ID, -1);
        if(articleId != -1) {
            titleEdit.setText(intent.getStringExtra(Articles.TITLE));
            abstractEdit.setText(intent.getStringExtra(Articles.ABSTRACT));
            urlEdit.setText(intent.getStringExtra(Articles.URL));
            addButton.setVisibility(View.GONE);
        }else {
            modifyButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        String title = titleEdit.getEditableText().toString();
        String abs = abstractEdit.getEditableText().toString();
        String url = urlEdit.getEditableText().toString();
        Intent result = new Intent();
        if(v.equals(addButton)) {
            result.putExtra(Articles.TITLE, title);
            result.putExtra(Articles.ABSTRACT, abs);
            result.putExtra(Articles.URL, url);
            setResult(Activity.RESULT_OK, result);
        }else if(v.equals(modifyButton)) {
            result.putExtra(Articles.ID, articleId);
            result.putExtra(Articles.TITLE, title);
            result.putExtra(Articles.ABSTRACT, abs);
            result.putExtra(Articles.URL, url);
            result.putExtra(EDIT_ARTICLE_ACTION, MODIFY_ARTICLE);
            setResult(Activity.RESULT_OK, result);
        }else if(v.equals(deleteButton)) {
            result.putExtra(Articles.ID, articleId);
            result.putExtra(EDIT_ARTICLE_ACTION, DELETE_ARTICLE);
            setResult(Activity.RESULT_OK, result);
        }else if(v.equals(cancelButton)) {
            setResult(Activity.RESULT_CANCELED);
        }
        finish();
    }
}
