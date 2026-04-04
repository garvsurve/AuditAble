package org.garvsurve.accessiblity_analyser.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
public class HtmlParser {

    public Document parseFromUrl(String url) throws Exception {
        return Jsoup.connect(url).get();
    }
}
