package root.tostre.com.vums;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ImageFetcher extends AsyncTask<String, Void, Bitmap> {

    private InputStream inputStream;
    private MainActivity mainActivity;
    private DocumentBuilderFactory documentBuilderFactory;
    private DocumentBuilder documentBuilder;
    private Document document;
    private NodeList tagList;
    private Node tagNode;
    private Element tagElement;
    private String imgUrl;
    private Bitmap image;
    private String imgXmlUrl;
    private ProgressBar progressBar;

    public ImageFetcher(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        image = null;

        try {
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar = (ProgressBar) mainActivity.findViewById(R.id.image_progressBar);
        progressBar.setVisibility(View.VISIBLE);
    }

    protected Bitmap doInBackground(String... urls) {
        imgXmlUrl = urls[0];

        try {
            inputStream = new URL(imgXmlUrl).openStream();

            document = documentBuilder.parse(inputStream);
            document.getDocumentElement().normalize();

            tagList = document.getElementsByTagName("original");
            tagNode = tagList.item(0);
            tagElement = (Element) tagNode;
            imgUrl = tagElement.getAttribute("source");

            inputStream = new java.net.URL(imgUrl).openStream();
            image = BitmapFactory.decodeStream(inputStream);


            //Bitmap b = BitmapFactory.decode
            image = scaleDownBitmap(image);

            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    protected void onPostExecute(Bitmap image) {
        // Set image in mainactivity
        //((ImageView) mainActivity.findViewById(R.id.article_image)).setImageBitmap(image);
        mainActivity.updateArticleImage(image);
        //progressBar.setVisibility(View.GONE);
    }

    // scales down the bitmap proportionally so that it fits the screens width
    private Bitmap scaleDownBitmap(Bitmap image){

        DisplayMetrics displayMetrics = new DisplayMetrics();
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = displayMetrics.widthPixels;
        int imageHeight = image.getHeight();
        int imageWidth = image.getWidth();
        double scaleFactor = ((double)imageWidth)/screenWidth;

        imageHeight = (int)(imageHeight/scaleFactor);
        imageWidth = screenWidth;

        image = Bitmap.createScaledBitmap(image, imageWidth, imageHeight, true);

        return image;
    }






}
