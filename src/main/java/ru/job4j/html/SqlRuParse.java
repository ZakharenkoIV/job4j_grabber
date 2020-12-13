package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SqlRuParse {
    public static void main(String[] args) throws Exception {
        Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers").get();
        Elements line = doc.select("#content-wrapper-forum > table.forumTable > tbody > tr:gt(0)");
        for (Element td : line) {
            if (!td.child(1).text().startsWith("Важно:")) {
                System.out.println(td.child(1).child(0).attr("href"));
                System.out.println(td.child(1).child(0).text());
                System.out.println(td.child(5).text() + System.lineSeparator());
            }
        }
    }
}