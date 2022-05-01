package org.xk.crawler.utils;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xk.crawler.global.GlobalConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParseUtils {
    public static List<String> getSubUrl(String url){
        List<String> urlList = new ArrayList<>();
        Document document = null;
		try {
			document = Jsoup.connect(url).userAgent(GlobalConfig.USER_AGENT).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//get sub documents
        Elements elements = document.getElementsByTag("a");
		if (elements.size() != 0) {
			for (Element element : elements) {
				String targetUrl = element.absUrl("href");
				urlList.add(targetUrl);
			}
		}
        return urlList;
    }
}
