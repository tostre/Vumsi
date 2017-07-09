package root.tostre.com.vums;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private int loadingStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                // Calls the activity but expects an result value, once it's finished
                // Result is handled withon onActivityResult
                startActivityForResult(intent, 1);
            }
        });

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.image_progressBar);
        progressBar.setVisibility(View.GONE);

        WebView wv = (WebView) findViewById(R.id.content_text);
        wv.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override // Gets called when searchActivity has finished, gets xmlUrls from its intent
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // Receives the url from the searchActivity and loads the article
        if(requestCode == 1 && resultCode == RESULT_OK && networkInfo != null){
            // One instance of an AsyncTask can only be executed once, therefore
            // for every exectuion there must be created a new instance
            ArticleFetcher articleFetcher = new ArticleFetcher(this);
            ImageFetcher imageFetcher = new ImageFetcher(this);

            articleFetcher.execute(data.getStringExtra("articleXmlUrl"));
            imageFetcher.execute(data.getStringExtra("imageXmlUrl"));
        }
    }

    // Updates the text-related values in the article, updates view; called from articleFetcher
    public void updateArticleText(String title, String text){

        ((CollapsingToolbarLayout) findViewById(R.id.toolbar_layout)).setTitle(title);

        ((WebView) findViewById(R.id.content_text)).loadData(text, "text/html; charset=utf-8", "utf-8");


        loadingStatus++;
        if(loadingStatus == 2){
            (findViewById(R.id.image_progressBar)).setVisibility(View.GONE);
            loadingStatus = 0;
        }
    }

    // Updates the img-related values in the article, updates view; called from imageFetcher
    public void updateArticleImage(Bitmap image){
        // Check if there's an image article
        if(image != null){
            ((ImageView) findViewById(R.id.article_image)).setImageBitmap(image);
        } else {
            // Set the default image
            ((ImageView) findViewById(R.id.article_image)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.debug_stack));
        }

        loadingStatus++;
        if(loadingStatus == 2) {
            (findViewById(R.id.image_progressBar)).setVisibility(View.GONE);
            loadingStatus = 0;
        }
    }

}
