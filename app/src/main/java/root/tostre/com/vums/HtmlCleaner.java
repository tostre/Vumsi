package root.tostre.com.vums;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

/**
 * Created by Macel on 12.05.17.
 */

public class HtmlCleaner{

    public HtmlCleaner(){

    }

    public String cleanHtmlString(String dirtyHtml, Whitelist whitelist){

        Elements elementsToDelete;
        Document doc = Jsoup.parse(dirtyHtml);

        // Remove all elememts chosen and their content
        elementsToDelete = doc.select("table.ambox");
        for(int i = 0; i < elementsToDelete.size(); i++){
            elementsToDelete.eq(i).remove();
        }

        elementsToDelete = doc.select("table.infobox");
        for(int i = 0; i < elementsToDelete.size(); i++){
            elementsToDelete.eq(i).remove();
        }

        elementsToDelete = doc.select("table.vertical-navbox");
        for(int i = 0; i < elementsToDelete.size(); i++){
            elementsToDelete.eq(i).remove();
        }

        elementsToDelete = doc.select("sup.reference");
        for(int i = 0; i < elementsToDelete.size(); i++){
            elementsToDelete.eq(i).remove();
        }

        elementsToDelete = doc.select("div.thumb");
        for(int i = 0; i < elementsToDelete.size(); i++){
            elementsToDelete.eq(i).remove();
        }

        elementsToDelete = doc.select("[role=note]");
        for(int i = 0; i < elementsToDelete.size(); i++){
            elementsToDelete.eq(i).remove();
        }

        elementsToDelete = doc.select(".metadata");
        for(int i = 0; i < elementsToDelete.size(); i++){
            elementsToDelete.eq(i).remove();
        }

        doc.select("div#toc").remove();

        // Remove only the tags, but leave the content
        String cleanHtml = doc.toString();
        cleanHtml = Jsoup.clean(cleanHtml, whitelist);

        return cleanHtml;
    }
}
