package root.tostre.com.vums;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private String apiEndpointImg ="https://en.wikipedia.org/w/api.php?action=query&prop=pageimages&piprop=original&format=xml&titles=";
    private String apiEndpointSearch = "https://en.wikipedia.org/w/api.php?action=query&list=search&srlimit=20&format=xml&srsearch=";
    private String apiEndpointArticle = "https://en.wikipedia.org/w/api.php?format=xml&redirects=yes&action=parse&disableeditsection=true&page=";
    private String url;
    private Menu menu;
    private Runnable runnable;
    private ArrayList<String> articleList;
    private ArrayList<String> urlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Creates and populates the WIki-Chooser
        Spinner wikiChooser = (Spinner) findViewById(R.id.wiki_chooser);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.wikis, R.layout.subfragment_spinner);
        // Apply the adapter to the spinner
        wikiChooser.setAdapter(adapter);
        setSpinnerListener(wikiChooser);

        // Sets listener on listView
        startArticleLoaderFromList();
        getSupportActionBar().setElevation(0);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.search_results_progressBar);
        progressBar.setVisibility(View.GONE);
    }

    @Override //
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        this.menu = menu;

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchItem.expandActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Commit search after user has stopped typing for 300ms
                Handler handler = new Handler();
                handler.removeCallbacks(runnable);

                runnable = new Runnable() {
                    @Override
                    public void run() {
                        //SearchFetcher searchFetcher = new SearchFetcher(SearchActivity.this);
                        //searchFetcher.execute(searchView.getQuery().toString(), apiEndpointSearch);
                        loadSearchResults();
                    }
                };

                handler.postDelayed(runnable, 300);

                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
        }
        return false;
    }

    // Listener on the spinner updates endpoints and updates search results
    private void setSpinnerListener(Spinner wikiChooser){
        wikiChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:

                        apiEndpointArticle = "https://en.wikipedia.org/w/api.php?format=xml&redirects=yes&action=parse&disableeditsection=true&page=";
                        apiEndpointImg = "https://en.wikipedia.org/w/api.php?action=query&prop=pageimages&piprop=original&format=xml&titles=";
                        apiEndpointSearch = "https://en.wikipedia.org/w/api.php?action=query&list=search&srlimit=20&format=xml&srsearch=";
                        loadSearchResults();
                        break;
                    case 1:
                        apiEndpointArticle = "https://de.wikipedia.org/w/api.php?format=xml&redirects=yes&action=parse&disableeditsection=true&page=";
                        apiEndpointImg ="https://de.wikipedia.org/w/api.php?action=query&prop=pageimages&piprop=original&format=xml&titles=";
                        apiEndpointSearch = "https://de.wikipedia.org/w/api.php?action=query&list=search&srlimit=20&format=xml&srsearch=";
                        loadSearchResults();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // Start searchFecther based on user-input in search field
    private void loadSearchResults(){
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        if(searchView.getQuery().toString() != null && !searchView.getQuery().toString().isEmpty()){
            SearchFetcher searchFetcher = new SearchFetcher(SearchActivity.this);
            searchFetcher.execute(searchView.getQuery().toString(), apiEndpointSearch);
        }
    }

    // Creates listener for listView that load the article from the listitem
    private void startArticleLoaderFromList(){
        final ListView search_results = (ListView) findViewById(R.id.search_results);

        search_results.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedFromList = (String) search_results.getItemAtPosition(position);

                // Creates a searchable URL from the item clicked in the list
                String url = extractUrl(position);
                String imgUrl = extractImageUrl(position);
                // Ends the activity and sends the url back to the main activity
                Intent intent = new Intent();
                intent.putExtra("articleXmlUrl", url);
                intent.putExtra("imageXmlUrl", imgUrl);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    // Updates the listView with search results gotten from the SearchFetcher
    public void updateSearchResultsList(ArrayList<String> articleList){
        this.articleList = articleList;
        // Populates the listview with the search results
        ListView listView = (ListView) findViewById(R.id.search_results);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.subfragment_listitems, articleList);
        listView.setAdapter(adapter);
    }

    // Creates an url from the title of a page
    public String extractUrl(int position){
        //urlList = new ArrayList<String>();
        String title = articleList.get(position);
        title = title.replaceAll("\\s+", "%20");
        url = apiEndpointArticle + title;
        Log.d("DBG", "URL: " + url);

        return url;
    }

    // Creates an imageurl from the title of a page
    public String extractImageUrl(int position){
        urlList = new ArrayList<String>();
        String title = articleList.get(position);
        title = title.replaceAll("\\s+", "%20");
        url = apiEndpointImg + title;

        return url;
    }
}
