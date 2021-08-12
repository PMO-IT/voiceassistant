package de.pmoit.voiceassistant.utils.rssreader;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;


public class RssReader {
    static final String TITLE = "title";
    static final String DESCRIPTION = "description";
    static final String LANGUAGE = "language";
    static final String LINK = "link";
    static final String ITEM = "item";
    static final String PUB_DATE = "pubDate";

    final URL url;

    public RssReader(String feedUrl) {
        try {
            this.url = new URL(feedUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public RssFeed readFeed() {
        RssFeed feed = null;
        try {
            boolean isFeedHeader = true;
            String description = "";
            String title = "";
            String link = "";
            String language = "";
            String copyright = "";
            String pubdate = "";

            // First create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // Setup a new eventReader
            InputStream in = read();
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
            // read the XML document
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement()) {
                    String localPart = event.asStartElement().getName().getLocalPart();
                    switch (localPart) {
                        case ITEM:
                            if (isFeedHeader) {
                                title = getCharacterData(eventReader);
                                title = getCharacterData(eventReader);
                                isFeedHeader = false;
                                RssDataObject rssDO = new RssDataObject();
                                rssDO.setTitle(title);
                                rssDO.setLink(link);
                                rssDO.setDescription(description);
                                rssDO.setLanguage(language);
                                rssDO.setCopyright(copyright);
                                rssDO.setPubdate(pubdate);
                                feed = new RssFeed(rssDO);
                            }
                            event = eventReader.nextEvent();
                            break;
                        case TITLE:
                            title = getCharacterData(eventReader);
                            break;
                        case DESCRIPTION:
                            description = getCharacterData(eventReader);
                            break;
                        case LINK:
                            link = getCharacterData(eventReader);
                            break;
                        case LANGUAGE:
                            language = getCharacterData(eventReader);
                            break;
                        case PUB_DATE:
                            pubdate = getCharacterData(eventReader);
                            break;
                    }
                } else if (event.isEndElement()) {
                    if (event.asEndElement().getName().getLocalPart() == ( ITEM )) {
                        RssFeedMessage message = new RssFeedMessage();
                        message.setDescription(description);
                        message.setLink(link);
                        message.setTitle(title);
                        feed.getMessages().add(message);
                        event = eventReader.nextEvent();
                        continue;
                    }
                }
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return feed;
    }

    private String getCharacterData(XMLEventReader eventReader) throws XMLStreamException {
        String result = " ";
        XMLEvent event = eventReader.nextEvent();
        if (event instanceof Characters) {
            Characters asCharacters = event.asCharacters();
            if (asCharacters.toString().equals("<")) {
                eventReader.nextEvent();
                eventReader.nextEvent();
                XMLEvent event4 = eventReader.nextEvent();
                return event4.asCharacters().getData();
            }
            result = asCharacters.getData();
        }

        return result;
    }

    private InputStream read() {
        try {
            return url.openStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<RssFeedMessage> getAllMessages() {
        RssFeed result = readFeed();
        return result.getMessages();
    }
}
