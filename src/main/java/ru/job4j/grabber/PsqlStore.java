package ru.job4j.grabber;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {

    private Connection cnn;

    public PsqlStore(Properties cfg) throws SQLException {
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        cnn = DriverManager.getConnection(
                cfg.getProperty("jdbc.url"),
                cfg.getProperty("jdbc.username"),
                cfg.getProperty("jdbc.password"));
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement ps = cnn.prepareStatement(
                "INSERT INTO post(name, text, link, created) VALUES ((?), (?), (?), (?));")) {
            ps.setString(1, post.getName());
            ps.setString(2, post.getText());
            ps.setString(3, post.getLink());
            ps.setString(4, post.getCreated());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new LinkedList<>();
        try (Statement statement = cnn.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM post");
            while (resultSet.next()) {
                Post post = new Post();
                post.setLink(resultSet.getString("link"));
                post.setName(resultSet.getString("name"));
                post.setText(resultSet.getString("text"));
                post.setCreated(resultSet.getString("created"));
                posts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public Post findById(String id) {
        Post post = new Post();
        try (PreparedStatement ps = cnn.prepareStatement("SELECT * FROM post WHERE id = (?)")) {
            ps.setInt(1, Integer.parseInt(id));
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                post.setLink(resultSet.getString("link"));
                post.setName(resultSet.getString("name"));
                post.setText(resultSet.getString("text"));
                post.setCreated(resultSet.getString("created"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    public static void main(String[] args) throws SQLException {
        Properties cfg = new Properties();
        try (FileInputStream in = new FileInputStream(
                "./src/main/resources/grabber.properties")) {
            cfg.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PsqlStore psqlStore = new PsqlStore(cfg);
        // Post post1 = new SqlRuParse().detail("https://www.sql.ru/forum/1323839/razrabotchik-java-g-kazan");
        // Post post2 = new SqlRuParse().detail("https://www.sql.ru/forum/1315877/postgresql-dba");
        //  psqlStore.save(post1);
        //  psqlStore.save(post2);
        System.out.println(psqlStore.getAll().get(0).equals(psqlStore.findById("11")));
        System.out.println(psqlStore.getAll().get(1).equals(psqlStore.findById("12")));

    }
}