package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class SqlRuParse {
    public void parse(int firstPage, int lastPage) throws IOException {
        int page = firstPage;
        String reference = "https://www.sql.ru/forum/job-offers/" + page;
        while (page <= lastPage) {
            Document doc = Jsoup.connect(reference).get();
            Elements tdElements = doc.select("#content-wrapper-forum > table.forumTable > tbody > tr:gt(0)");
            for (Element td : tdElements) {
                if (!td.child(1).text().startsWith("Важно:")) {
                    System.out.println(td.child(1).child(0).attr("href"));
                    System.out.println(td.child(1).child(0).text());
                    System.out.println(td.child(5).text() + System.lineSeparator());
                }
            }
            reference = getNewReference(doc, ++page);
            if (reference == null) {
                break;
            }
        }
    }

    private String getNewReference(Document doc, int page) {
        String newReference = null;
        Elements aElements = doc.select("#content-wrapper-forum > table:nth-child(7) > tbody > tr > td:nth-child(1) > a:gt(0)");
        for (Element a : aElements) {
            if (Integer.parseInt(a.text()) == page) {
                newReference = a.attr("href");
            }
            break;
        }
        return newReference;
    }


    public static void main(String[] args) throws Exception {
        new SqlRuParse().parse(1, 5);
    }
}