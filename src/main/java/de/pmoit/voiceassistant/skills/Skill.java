package de.pmoit.voiceassistant.skills;

import java.util.Set;


public interface Skill {

    /**
     * Basic entry point for the skills. Based on the spoken phrase the skill
     * should do something and return a response as String
     */
    public String handle(String spokenPhrase);

    /**
     * The action keywords. A matched keyword will activate the skill and call
     * the handle-method
     */
    public Set<String> getActionWords();

    /**
     * Basic implementation to match skills to spoken words
     */
    public default boolean canHandle(String spokenWords) {
        String lowerCasedSpokenWords = spokenWords.toLowerCase();
        for (String singleElement : getActionWords()) {
            if (lowerCasedSpokenWords.contains(singleElement)) {
                return true;
            }
        }
        return false;
    }
}
