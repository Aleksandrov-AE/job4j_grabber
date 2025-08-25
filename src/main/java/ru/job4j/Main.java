package ru.job4j;

import ru.job4j.grabber.service.*;
import ru.job4j.grabber.stores.JdbcStore;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static ru.job4j.grabber.service.Config.LOG;

public class Main {
    public static void main(String[] args) {
        var config = new Config();
        config.load("application.properties");
        try (Connection connection = DriverManager.getConnection(
                config.get("db.url"),
                config.get("db.username"),
                config.get("db.password")
        ); var scheduler = new SchedulerManager()) {
            var store = new JdbcStore(connection);
            DateTimeParser dateTimeParser = new HabrCareerDateTimeParser();
            HabrCareerParse habrCareerParse = new HabrCareerParse(dateTimeParser);
            scheduler.init();
            scheduler.load(
                    Integer.parseInt(config.get("rabbit.interval")),
                    SuperJobGrab.class,
                    store, habrCareerParse);
            new Web(store).start(Integer.parseInt(config.get("server.port")));
            Thread.currentThread().join();
        } catch (SQLException e) {
            LOG.error("When create a connection", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}