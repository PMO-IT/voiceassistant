package de.pmoit.voiceassistant.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import de.pmoit.voiceassistant.utils.readproperties.GlobalConfiguration;


/**
 * Mic input format for DeepSpeech: PCM_SIGNED 16000.0 Hz, 16 bit, mono, 2
 * bytes/frame, little-endian
 * 
 * The DeepSpeech Client. Connects to a DeepSpeech server and handles the
 * audiostream.
 */
public class DeepSpeechClient {

    private static final String SERVERADDRESS = GlobalConfiguration.getServerAdress();

    private final CustomSilenceDetector silenceDetector = new CustomSilenceDetector();
    private final Microphone microphone = new Microphone();

    private AudioDispatcher dispatcher;
    private String result;
    private Thread dispatcherThread;

    public void prepareTranscription() throws Exception {

        if (dispatcher != null) {
            throw new IllegalStateException("Prepare transcription called more then once!");
        }

        JVMAudioInputStream audioStream = microphone.start();
        dispatcher = new AudioDispatcher(audioStream, 4096, 2048);
        dispatcher.addAudioProcessor(silenceDetector);
        dispatcher.addAudioProcessor(new AudioProcessorImpl(silenceDetector, this::transcribe));
        dispatcherThread = new Thread(dispatcher, "Audio dispatching");
        dispatcherThread.start();
    }

    private ByteArrayOutputStream createAudioOutputStream(ByteArrayOutputStream out) throws IOException {
        int frameSizeInBytes = microphone.getFrameSize();
        byte audioBytes[] = out.toByteArray();

        AudioInputStream audioInputStream = new AudioInputStream(new ByteArrayInputStream(out.toByteArray()), microphone
            .getAudioFormat(), audioBytes.length / frameSizeInBytes);

        ByteArrayOutputStream byos = new ByteArrayOutputStream();
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, byos);
        audioInputStream.close();
        return byos;
    }

    /**
     * Opens up a HTTP connection and sends a ByteArrayOutputStream. After
     * sending the expected result is a http entity. In case its not null it
     * will be translated to an UTF-8 String and returned.
     * 
     * @param byos
     * @return deepspeech result as String or 'Kein Ergebnis' if null
     * @throws IOException
     * @throws ClientProtocolException
     */
    private String sendDataToServer(ByteArrayOutputStream byos) throws IOException, ClientProtocolException {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(SERVERADDRESS);
        ByteArrayEntity bae = new ByteArrayEntity(byos.toByteArray());
        httppost.setEntity(bae);

        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            try (InputStream instream = entity.getContent()) {
                return IOUtils.toString(instream, "UTF-8");
            }
        }
        return "Kein Ergebnis";
    }

    private void transcribe(ByteArrayOutputStream out) {
        ByteArrayOutputStream byos;
        try {
            byos = createAudioOutputStream(out);
            setResult(sendDataToServer(byos));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getResult() {
        synchronized ( dispatcherThread ) {
            return result;
        }
    }

    public void resetResult() {
        setResult(null);
    }

    private void setResult(String result) {
        synchronized ( dispatcherThread ) {
            this.result = result;
        }
    }
}
