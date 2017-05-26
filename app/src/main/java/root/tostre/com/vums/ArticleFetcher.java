package root.tostre.com.vums;


import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.jsoup.safety.Whitelist;
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

// Async task should be used to make short calculations
// in the background and publish the results on the ui
// It takes 3 parameters:
// 1: String: Used to pass as parameters for doInBackground
// 2: progress type: Type of variable used in doBackground when publishProgress is called
// 3: result: End result, gets returned by doInBackground, also parameter for onPostExecute
public class ArticleFetcher extends AsyncTask<String, Void , ArrayList<String>>{

    private InputStream inputStream;
    private MainActivity mainActivity;
    private DocumentBuilderFactory documentBuilderFactory;
    private DocumentBuilder documentBuilder;
    private Document document;
    private NodeList tagList;
    private Node tagNode;
    private Element tagElement;
    private String articleXmlUrl;
    ArrayList<String> articleTextArray;
    private ProgressBar progressBar;


    // Called when an ArticleFetcher object is created
    public ArticleFetcher(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    @Override
    protected void onPreExecute() {
        //progressBar = (ProgressBar) mainActivity.findViewById(R.id.text_progressBar);
        progressBar = (ProgressBar) mainActivity.findViewById(R.id.image_progressBar);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected ArrayList<String> doInBackground(String... urls) {
        articleXmlUrl = urls[0];
        articleTextArray = new ArrayList<String>();

        try {
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilder = documentBuilderFactory.newDocumentBuilder();

            inputStream = new URL(articleXmlUrl).openStream();
            document = documentBuilder.parse(inputStream);
            document.getDocumentElement().normalize();




            // Search the title and add it to the articleArray
            tagList = document.getElementsByTagName("parse");
            tagNode = tagList.item(0);
            tagElement = (Element) tagNode;
            articleTextArray.add(tagElement.getAttribute("title"));

            // Search the extract and add it to the articleArray
            /*tagList = document.getElementsByTagName("extract");
            tagNode = tagList.item(0);
            articleTextArray.add(tagNode.getTextContent());*/

            // Get the text-content
            tagList = document.getElementsByTagName("text");
            tagNode = tagList.item(0);
            String text = ((Element) tagNode).getTextContent();
            // Clean the document
            text = new HtmlCleaner().cleanHtmlString(text, new Whitelist().addTags("h2", "h3", "h4", "h5", "h6", "p", "b", "br", "div", "ol", "ul", "span", "i", "li", "ol", "span"));
            articleTextArray.add(text);

            // Add the url to the articleArray
            articleTextArray.add(articleXmlUrl);

            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return articleTextArray;
    }


    @Override // Called when doInBackground is finished
    protected void onPostExecute(ArrayList<String> articleTextArray) {
        // Overwrite the current article
        mainActivity.updateArticleText(articleTextArray.get(0), articleTextArray.get(1));
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }








    /*
            // Get the root element of document
            Element root = document.getDocumentElement();
            String rootName = root.getNodeName();
            Log.d("DBG", rootName);
            // Examine the attributes of an element
            String title = root.getAttribute("title");  //returns specific attribute
            NamedNodeMap attributeMap = root.getAttributes();  //returns a Map (table) of names/values
            // Child elements can inquired in below manner.
            node.getElementsByTagName("subElementName") //returns a list of sub-elements of specified name
            node.getChildNodes()                         //returns a list of all child nodes
            // Create a list of all elements with the same starttag (e.g. food)
            NodeList nList = document.getElementsByTagName("employee");
            System.out.println("============================");
            // Get an specific elemet out of the list, e.g. the 4th or 7th
            Node node = nList.item(0);
            // Cast the node to an element
            Element nodeElement = (Element) node;
            // Print out the values/details from the element
            String a = nodeElement.getAttribute("id"); // Returns the value saved in the id attribute
            String b = nList.item(0).getTextContent();
            */

}


