package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class SqlRuParse implements Parse {

    @Override
    public List<Post> list(String link) {
        List<Post> allPosts = new LinkedList<>();
        Document doc = getDocument(link);
        Elements tdElements = doc.select(
                "#content-wrapper-forum > table.forumTable > tbody > tr:gt(0)");
        for (Element td : tdElements) {
            if (!td.child(1).text().startsWith("Важно:")) {
                Post post = detail(td.child(1).child(0).attr("href"));
                if (post.getName().toLowerCase().matches(".*\\bjava\\b.*")
                        || post.getText().toLowerCase().matches(".*\\bjava\\b.*")) {
                    allPosts.add(post);
                }
            }
        }
        return allPosts;
    }

    @Override
    public Post detail(String link) {
        Post newPost = new Post();
        Document doc = getDocument(link);
        Elements trElements = doc.select(
                "#content-wrapper-forum > table.msgTable > tbody");
        newPost.setName(trElements.first().child(0).child(0).ownText());
        newPost.setText(trElements.first().child(1).child(1).text());
        newPost.setLink(link);
        newPost.setCreated(trElements.first().child(2).child(0).ownText().split("\\[")[0]);
        return newPost;
    }

    private Document getDocument(String link) {
        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Objects.requireNonNull(doc);
    }
}