package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Objects;

public class SqlRuParse {
    public Post fillThePost(Post post, String linkToPost) throws IOException {
        Document doc = Jsoup.connect(linkToPost).get();
        Elements trElements = doc.select("#content-wrapper-forum > table.msgTable > tbody");
        post.setPostName(trElements.first().child(0).child(0).ownText());
        post.setLink(linkToPost);
        post.setAuthor(trElements.first().child(1).child(0).child(0).text());
        post.setText(trElements.first().child(1).child(1).text());
        post.setDate(trElements.first().child(2).child(0).ownText().split("\\[")[0]);
        return new Post();
    }

    public void parse(int firstPage, int lastPage) {
        int page = firstPage;
        while (page <= lastPage) {
            String url = "https://www.sql.ru/forum/job-offers/" + page++;
            Document doc = null;
            try {
                doc = Jsoup.connect(url).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Elements tdElements = Objects.requireNonNull(doc).select("#content-wrapper-forum > table.forumTable > tbody > tr:gt(0)");
            for (Element td : tdElements) {
                if (!td.child(1).text().startsWith("Важно:")) {
                    System.out.println(td.child(1).child(0).attr("href"));
                    System.out.println(td.child(1).child(0).text());
                    System.out.println(td.child(5).text() + System.lineSeparator());
                }
            }
        }
    }

    public static void main(String[] args) {
        new SqlRuParse().parse(1, 5);
    }
}