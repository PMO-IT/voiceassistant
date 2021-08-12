package de.pmoit.voiceassistant.utils.rssreader;

import java.util.ArrayList;
import java.util.List;


public class RssFeed {
    final String title;
    final String link;
    final String description;
    final String language;
    final String pubDate;

    final List<RssFeedMessage> entries = new ArrayList<RssFeedMessage>();

    public RssFeed(RssDataObject rssDO) {
        this.title = rssDO.getTitle();
        this.link = rssDO.getLink();
        this.description = rssDO.getDescription();
        this.language = rssDO.getLanguage();
        this.pubDate = rssDO.getPubdate();
    }

    public List<RssFeedMessage> getMessages() {
        return entries;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getLanguage() {
        return language;
    }

    public String getPubDate() {
        return pubDate;
    }

    @Override
    public String toString() {
        return "Feed [description=" + description + ", language=" + language + ", link=" + link + ", pubDate=" + pubDate
            + ", title=" + title + "]";
    }
}
