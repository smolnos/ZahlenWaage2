package de.r3chn3n.zahlenwaage2;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaySound {
    private final int duration = 5; // seconds 10 0
    private final int sampleRate = 8000;
    private final int numSamples = duration * sampleRate;
    private final double[] sample = new double[numSamples];
    private final double freqOfTone = 2000; // hz
//    private final double freqOfTone = 440; // hz
    private AudioTrack audioTrack;
    private boolean running = false;
    private double hertz = 0;

    private final byte[] generatedSnd = new byte[2 * numSamples];

    void genTone() {
        // fill out the array
        hertz = 0;
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i * ((freqOfTone - hertz)/sampleRate));
            if (i % 400 == 0) hertz += 20;
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal /10 * 32767 ));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
    }

    void playSound(){
        audioTrack.play();
        running = true;
    }

    void stopSound() {
//        if (audioTrack != null) {
//            audioTrack.release();
//            audioTrack = null;
//        }
        if (running = true) {
            audioTrack.stop();
            audioTrack.flush();
            running = false;
        }
//        audioTrack.write(generatedSnd, 0, generatedSnd.length);
//        audioTrack.setPlaybackHeadPosition(0);
    }

}
