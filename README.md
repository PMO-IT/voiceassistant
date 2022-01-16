[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](https://opensource.org/licenses/Apache-2.0)

#voice-assistant
Nova, a Java based voice assistant. Runnable on Raspberry Pi. 


# Description
Nova uses MaryTTS as text-to-speech engine and Mozilla´s DeepSpeech server as speech-to-text. The assistant reacts as soon as there is loud enough sound, after that it reacts as soon as the call-to-action phrase is spoken.
Nova has a skill engine which loads skills dynamically based on the matching spoken words. Additional skills can be added with the skill interface. Currently the assistant is designed for the German language, but it can easly be changed.


## Basic configuration
The Property-file

This "config.properties" file lies in the resources folder.

### Parameters
```
name = Nova
voice = bits1-hsmm
call_to_action1 = hallo nova
call_to_action2 = hey nova
stop_action = nova stop
server_adress=http://192.168.xxx.xxx:8080/stt
additional_listening_time=1000
```

Name of the assistant
The used voice, in this case a female German voice
The phrase which triggers the assistant to listen
A stop action phrase, which can be used for skills
The DeepSpeech server address
The additional listening time in milliseconds is the time the assistant will listen even after there is no sound.


## Skillengine
Use the Skill interface and add the action-keywords. The handle-method is the skill-entrypoint. If an action keywords is matched, the handle-method will start.
You can implement your own canHandle-method and change the course how the keywords are matched or stick to the default. The skills return always a string which Nova will call out.

### Existing skills
There are several skills currently in development and will be released in the near future.

#### Weatherskill
The Weatherskill uses the Openweather api, so you have to add your key into the weather.config.properties. Its possible to ask for today's temperature, the weather for today and tomorrow for different cities, weather at different times, if it rains or if the sun shines. You can change or add responses in the text files which are in the output folder. Each response has their own response file.

##### Parameters
```
api=xxxxxxx
defaultcity=Frankfurt am Main
```

The Openweather api-key
The defaultcity, if the speaker doesn´t give a cityname with the statement.

##### Example
```
"Wie wird das Wetter morgen"
"Wie warm wird es"
"Wie kalt wird es in Berlin"
"Regnet es heute in Frankfurt"
```

#### DateTime
This skill returns the current time, date and day. You can change or add responses in the text files which are in the output folder. Each response has their own responsefile.


##### Example
```
"Welcher Tag ist heute"
"Wie spät ist es"
"Welcher Tag ist morgen"
```

### More skills currently in development
- Youtube
- Spotify
- Phillips Hue lights
- Rssreader
- Timer / Intervalltimer
- Appointments / Reminder

### Notice
Added the recent repository for mary to the pom. This repository could change in the near future.
In that case you can download the libraries here: https://github.com/marytts/marytts/releases/tag/v5.2

You need the following files:
   - marytts-runtime-5.2-jar-with-dependencies.jar
   - marytts-lang-de-5.2.jar (or you own language)
   - the voices you want to use


### Special Thanks to
SLU-IT

PaddyEsch

## Contact
[@Keeper_pmo](https://twitter.com/Keeper_pmo)  
[linkedin](https://www.linkedin.com/in/pascal-moll-14980520b/)  
[pmo-it.de](https://pmo-it.de)
