import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFloatConverter;
import be.tarsos.dsp.io.TarsosDSPAudioInputStream;
import be.tarsos.dsp.io.jvm.AudioPlayer;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.DTMF;
import be.tarsos.dsp.pitch.Goertzel;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import java.io.ByteArrayInputStream;

/*      A simple Java project to take a .txt file with a list of DTMF tone codes, present the names to the user, and "dial" them when selected.
        Copyright (C) 2022 Nayab W.

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <https://www.gnu.org/licenses/>. */

public class Main {

    public static void main(String[] args) throws LineUnavailableException, InterruptedException {
        System.out.println("Hello world!");
        String num = "0123456789ABCD*#";
        char[] chars = num.toCharArray();
        for (char i: chars){
            process(i);
            Thread.sleep(256);
        }
    }

    // Adapted from TarosDSP, a library used in this project.
    // https://github.com/JorenSix/TarsosDSP

    private static final AudioProcessor goertzelAudioProcessor = new Goertzel(44100, 256, DTMF.DTMF_FREQUENCIES, (time, frequencies, powers, allFrequencies, allPowers) -> {
        // There is nothing to handle with each char right now. If there was, this is where it would go.
    });

    /**
     * Process a DTMF character: generate sound.
     * @param character The character.
     * @throws LineUnavailableException
     */
    public static void process(char character) throws LineUnavailableException {
        final float[] floatBuffer = DTMF.generateDTMFTone(character);
        final AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
        JVMAudioInputStream.toTarsosDSPFormat(format);
        final TarsosDSPAudioFloatConverter converter = TarsosDSPAudioFloatConverter.getConverter(JVMAudioInputStream.toTarsosDSPFormat(format));
        final byte[] byteBuffer = new byte[floatBuffer.length * format.getFrameSize()];
        converter.toByteArray(floatBuffer, byteBuffer);
        final ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer);
        final AudioInputStream inputStream = new AudioInputStream(bais, format,floatBuffer.length);
        final TarsosDSPAudioInputStream stream = new JVMAudioInputStream(inputStream);
        final AudioDispatcher dispatcher = new AudioDispatcher(stream, 256, 0);
        dispatcher.addAudioProcessor(goertzelAudioProcessor);
        dispatcher.addAudioProcessor(new AudioPlayer(format));
        new Thread(dispatcher).start();
    }
}