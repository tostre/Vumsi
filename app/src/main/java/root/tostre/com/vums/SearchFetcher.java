package root.tostre.com.vums;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Macel on 13.05.17.
 */

public class SearchFetcher extends AsyncTask<String, Void, String> {

    private ArrayList<String> articleList;
    private SearchActivity searchActivity;
    private ProgressBar progressBar;

    // Called when an ArticleFetcher object is created
    public SearchFetcher(SearchActivity searchActivity){
        this.searchActivity = searchActivity;
        articleList = new ArrayList<String>();
    }

    @Override // Start the progress-circle to spin
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar = (ProgressBar) searchActivity.findViewById(R.id.search_results_progressBar);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... params) {
        String searchUrl = createUrl(params[0],params[1]);

        try {
            InputStream inputStream = new URL(searchUrl).openStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();

            String line = null;

            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }

            JSONObject result = new JSONObject(sb.toString());

            JSONArray search = result.getJSONObject("query").getJSONArray("search");

            if(search.length() > 0){

                for(int i = 0; i < search.length(); i++){
                    JSONObject res = search.getJSONObject(i);
                    articleList.add(res.getString("title"));
                }

                inputStream.close();
            } else {
                articleList.add("No results");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }

    // Creates an url from the title of a page
    public String createUrl(String searchTerm, String apiEndpointSearch){
        searchTerm = searchTerm.replaceAll("\\s+", "%20");
        String newSearchUrl = apiEndpointSearch + searchTerm;
        return newSearchUrl;
    }

    @Override
    protected void onPostExecute(String s) {
        //super.onPostExecute(articleList);
        searchActivity.updateSearchResultsList(articleList);
        progressBar.setVisibility(View.GONE);

    }

}
