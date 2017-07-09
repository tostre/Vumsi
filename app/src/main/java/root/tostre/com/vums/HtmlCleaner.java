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

        Document doc = Jsoup.parse(dirtyHtml);
        String cleanHtml = Jsoup.clean(doc.toString(), whitelist);

        return cleanHtml;
    }
}
