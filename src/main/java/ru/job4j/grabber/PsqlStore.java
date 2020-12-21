package ru.job4j.grabber;

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
}