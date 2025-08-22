package ru.job4j.grabber.service;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.utils.DateTimeParser;
import java.io.IOException;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {
    private static final Logger LOG = Logger.getLogger(HabrCareerParse.class);
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PREFIX = "/vacancies?page=";
    private static final String SUFFIX = "&q=Java%20developer&type=all";
    private static final int COUNT_PAGE = 3;
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> fetch() {
        var result = new ArrayList<Post>();
        try {
            for (int pageNumber = 1; pageNumber <= COUNT_PAGE; pageNumber++) {
                String fullLink = "%s%s%d%s".formatted(SOURCE_LINK, PREFIX, pageNumber, SUFFIX);
                var connection = Jsoup.connect(fullLink);
                var document = connection.get();
                var rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> {
                    var titleElement = row.select(".vacancy-card__title").first();
                    var dataElement = row.select(".vacancy-card__date").first();
                    if (titleElement == null || dataElement == null) {
                        LOG.warn("Skip row: missing title/date elements");
                        return;
                    }
                    var linkElement = titleElement.child(0);
                    String vacancyName = titleElement.text();
                    String link = String.format("%s%s", SOURCE_LINK,
                            linkElement.attr("href"));
                    String isoDate = dataElement.child(0).attr("datetime"); // ISO-строка
                    var post = new Post();
                    post.setTitle(vacancyName);
                    post.setLink(link);
                    post.setDescription(retrieveDescription(link));
                    post.setTime(dateTimeParser.parse(isoDate).toEpochSecond(ZoneOffset.UTC));
                    result.add(post);
                });
            }
        } catch (IOException e) {
            LOG.error("When load page", e);
        }
        return result;
    }

    private String retrieveDescription(String link) {
        try {
            var connection = Jsoup.connect(link);
            Document document = connection.get();
            var el = document.selectFirst(".vacancy-description__text .style-ugc");
            if (el == null) {
                throw new IllegalStateException("Description block not found for link: " + link);
            }
            el.select("h3").remove();
            return el.wholeText().trim();
        } catch (IOException | IllegalStateException e) {
            LOG.error("When load description", e);
            throw new RuntimeException(e);
        }
    }
}