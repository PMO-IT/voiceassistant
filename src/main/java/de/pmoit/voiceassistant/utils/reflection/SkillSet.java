package de.pmoit.voiceassistant.utils.reflection;

import java.util.List;
import java.util.stream.Collectors;

import org.reflections.Reflections;

import de.pmoit.voiceassistant.skills.Skill;
import de.pmoit.voiceassistant.tts.TextToSpeech;


/**
 * Class to handle the skillsets. Runs Skills based on spoken phrase.
 */
public class SkillSet {
    private TextToSpeech tts;
    private List<Skill> skills;

    public SkillSet(List<Skill> skills, TextToSpeech tts) {
        this.skills = skills;
        this.tts = tts;
    }

    /**
     * Run skills by spoken phrase Should it be possible to run more then one
     * skill per accepted phrase?
     */
    public void runAll(String spokenWords) throws Exception {
        for (Skill skill : skills) {
            if (skill.canHandle(spokenWords)) {
                String skillResult = skill.handle(spokenWords);
                tts.speak(skillResult);
                break;
            }
        }
    }

    private static Skill createSkillInstance(Class< ? > controller) {
        String classname = controller.getName();
        Class< ? > newSkillClass;
        try {
            newSkillClass = Class.forName(classname);
            return ( Skill ) newSkillClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    /**
     * Factory method to load skills by package name
     */
    public static SkillSet fromPackage(String skillPackage, TextToSpeech tts) {
        Reflections reflections = new Reflections(skillPackage);
        List<Skill> skills = reflections.getSubTypesOf(Skill.class).stream().map((clazz) -> {
            return createSkillInstance(clazz);
        }).collect(Collectors.toList());
        return new SkillSet(skills, tts);
    }

}
