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
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

    public static void main(String[] args) throws LineUnavailableException, InterruptedException, FileNotFoundException {
        List<dtmf_code> codes = new ArrayList<>();

        File file = new File("/Users/nayab/IdeaProjects/DTMF Tones/out/W5AC-v20.txt");
        Scanner fileScan = new Scanner(file);
        String currentCat = null;
        while(fileScan.hasNextLine()) {
            String line = fileScan.nextLine();

            // Section headers are denoted as ; -----
            if (line.contains("; -")) {
                currentCat = line.substring(8);
            }

            // Ignore all comments
            if (!line.equals("") && line.charAt(0) != ';') {
                String[] bits = line.split(";");
                String code = bits[0];
                String name = bits[0];
                // If a code has a name, find it.
                if (bits.length == 2)
                    name = bits[1];
                codes.add(new dtmf_code(name, code, currentCat));
            }
        }
        for (dtmf_code code: codes) {
            playCode(code);
            System.out.println("Played " + code.getName() + ". From " + code.getCategory());
        }
    }

    public static void playCode(dtmf_code code) throws LineUnavailableException, InterruptedException {
        playCode(code.getCode());
    }
    public static void playCode(String code) throws LineUnavailableException, InterruptedException {
        char[] chars = code.toCharArray();
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