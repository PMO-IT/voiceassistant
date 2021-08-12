package de.pmoit.voiceassistant.utils.filereader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class for reading responsefilecontent.
 */
public class FileContentReader {
    private BufferedReader reader;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public FileContentReader(String file) {
        try {
            reader = new BufferedReader(new FileReader(new File(file)));
        } catch (FileNotFoundException e) {
            logger.error("Datei " + file + " nicht gefunden!");
        }
    }

    /**
     * Reads responsen in random order from given file and returns them as
     * String
     */
    public String getRandomResponseFromFile(FileContentReader fcr) {
        ArrayList<String> data = fcr.readContent();
        int randomNumber = ( int ) ( Math.random() * data.size() );
        return data.get(randomNumber);
    }

    private ArrayList<String> readContent() {

        ArrayList<String> data = new ArrayList<String>();
        String line;
        try {
            while ( ( line = reader.readLine() ) != null) {
                data.add(line);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                logger.error("Error while closing reader " + e.getMessage(), e);
            }
        }
        return data;
    }
}
