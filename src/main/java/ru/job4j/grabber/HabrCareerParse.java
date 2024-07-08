package ru.job4j.grabber;

import java.io.IOException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HabrCareerParse {
    private static final String SOURCE_LINK = """
            https://career.habr.com""";
    public static final String PREFIX = "/vacancies?page=";
    public static final String SUFFIX = "&q=Java%20developer&type=all";
    public static final int START_PAGE_NUMBER = 1;
    public static final int END_PAGE_NUMBER = 5;

    public static void main(String[] args) throws IOException {
        HabrCareerParse parser = new HabrCareerParse();
        for (int i = START_PAGE_NUMBER; i <= END_PAGE_NUMBER; i++) {
            String fullLink = "%s%s%d%s".formatted(SOURCE_LINK, PREFIX, i, SUFFIX);
            Connection connection = Jsoup.connect(fullLink);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                String vacancyName = titleElement.text();
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                Element dateElement = row.select(".vacancy-card__date").first();
                Element timeElement = dateElement.child(0);
                String date = timeElement.attr("datetime");
                String description = parser.retrieveDescription(link);
                System.out.printf("%s %s %s%nDescription: %s%n", vacancyName, link, date, description);
            });
        }
    }

    private String retrieveDescription(String link) {
        String vacancyDescription = "";
        Connection connection = Jsoup.connect(link);
       try {
           Document document = connection.get();
           Elements rows = document.select(".faded-content__container");
           if (!rows.isEmpty()) {
               vacancyDescription = rows.first().text();
           }
       } catch (IOException e) {
           e.printStackTrace();
       }
        return vacancyDescription;
    }
}
