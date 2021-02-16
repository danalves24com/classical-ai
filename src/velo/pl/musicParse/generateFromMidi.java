package velo.pl.musicParse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class generateFromMidi {
	private File file = null;
	private String name;
	public generateFromMidi(String path, String name) {
		this.file = new File(path);
		this.name = name;
	}

	public static final String[] NOTE_NAMES = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
	private ArrayList<Integer> noteList = new ArrayList<Integer>(), octaveList = new ArrayList<Integer>();
	private Map<Integer[], Integer> data = new HashMap<Integer[], Integer>();

	public void generateLists() {
		try {
			Sequence sequence = MidiSystem.getSequence(this.file);
			int trackNumber = 0;
			for (Track track : sequence.getTracks()) {
				trackNumber++;
				for (int i = 0; i < track.size(); i++) {
					MidiEvent event = track.get(i);
					MidiMessage message = event.getMessage();
					if (message instanceof ShortMessage) {
						ShortMessage sm = (ShortMessage) message;
						int key = sm.getData1();
						int octave = (key / 12) - 1;
						int note = key % 12;

						noteList.add(note);
						octaveList.add(octave);

						//System.out.println(note + " | " + octave);
					}
				}
			}
		} catch (InvalidMidiDataException | IOException e) {
			e.printStackTrace();
		}

	}

	public void generateData() {

		for (Integer i = 0; i < noteList.size()-4; i += 1) {
			Integer[] inputs = new Integer[3];
			for (Integer ii = 0; ii < 3; ii += 1) {
				Integer pos = i+ii;
				inputs[ii] = noteList.get(pos);				
			}
			data.put(inputs, noteList.get(i+3));
		}
		 
	}
	public void SaveData() {
		String output = "";
		for(Map.Entry<Integer[], Integer> d : data.entrySet()) {
			String out = "";
			for(Integer in : d.getKey()) {				
				out += (in) + ",";
			}
			out+=d.getValue();			
			output+=out+"\n";
		}
	    final Path path = Paths.get(this.name);

	    try (
	        final BufferedWriter writer = Files.newBufferedWriter(path,
	            StandardCharsets.UTF_8, StandardOpenOption.CREATE);
	    ) {
	        writer.write(output);
	        writer.flush();
	    } catch (IOException e) {
			e.printStackTrace();
		}
	}
}
