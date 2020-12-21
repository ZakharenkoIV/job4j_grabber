package ru.job4j.grabber;

import java.util.Objects;

public class Post {
    private String name;
    private String text;
    private String link;
    private String created;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Post post = (Post) o;
        return Objects.equals(name, post.name)
                && Objects.equals(text, post.text)
                && Objects.equals(link, post.link)
                && Objects.equals(created, post.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, text, link, created);
    }

    @Override
    public String toString() {
        return "1) " + name + System.lineSeparator()
                + "2) " + text + System.lineSeparator()
                + "3) " + link + System.lineSeparator()
                + "4) " + created + System.lineSeparator()
                + "_____________________________________" + System.lineSeparator();
    }
}
