package root.tostre.com.vums;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
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
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);
            document.getDocumentElement().normalize();
            Element root = document.getDocumentElement();

            NodeList pageList = document.getElementsByTagName("p");

            if(pageList.getLength() > 0){

                for(int i = 0; i < pageList.getLength(); i++){
                    Node pageNodeX = pageList.item(i);
                    Element pageElementX = (Element) pageNodeX;
                    String aX = pageElementX.getAttribute("title");
                    articleList.add(aX);
                }

                inputStream.close();
            } else {
                articleList.add("No results");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
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
