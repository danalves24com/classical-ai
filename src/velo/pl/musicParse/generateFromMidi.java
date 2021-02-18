package velo.pl.musicParse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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

	private static DecimalFormat df2 = new DecimalFormat("#.00");
	public String[] NOTE_NAMES = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
	private ArrayList<Double> noteList = new ArrayList<Double>();
	ArrayList<Double> octaveList = new ArrayList<Double>();
	private Map<Double[], Double> data = new HashMap<Double[], Double>(), dataO = new HashMap<Double[], Double>();
	private Integer C = 0, D = 0, E = 0, F = 0, G = 0, A = 0, B = 0;
	private Integer mean = 100;

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
						// System.out.println(sm.getData1() + " | " + sm.getData2() + " | " +
						// sm.getCommand());
						Integer key = Integer.valueOf(sm.getData1());
						Double octave = (double) ((key / 12) - 1) / 10;
						Integer note = key % 12;
						Double norm = (double) ((float) note / 12);
						String noteV = NOTE_NAMES[(int) (norm * 12)];
//						System.out.println(norm + " : " + note );
						if (mean != null) {
							if ((noteV.contains("C") && C <= mean) || (noteV.contains("D") && D <= mean)
									|| (noteV.contains("E") && E <= mean) || (noteV.contains("F") && F <= mean)
									|| (noteV.contains("G") && G <= mean) || (noteV.contains("A") && B <= mean)) {
								noteList.add((double) Double.valueOf(df2.format(norm)));
								octaveList.add(octave);
								switch (noteV) {
								case "C":
									this.C++;
									break;
								case "C#":
									this.C += 1;
									break;
								case "D":
									this.D += 1;
									break;
								case "D#":
									this.D += 1;
									break;
								case "E":
									this.E += 1;
									break;
								case "F":
									this.F += 1;
									break;
								case "F#":
									this.F += 1;
									break;
								case "G":
									this.G += 1;
									break;
								case "G#":
									this.G += 1;
									break;
								case "A":
									this.A += 1;
									break;
								case "A#":
									this.A += 1;
									break;
								case "B":
									this.B += 1;
									break;
								default:
									break;
								}
							} else {

							}
						} else {
							noteList.add((double) Double.valueOf(df2.format(norm)));
							octaveList.add(octave);
						}

						// System.out.println(note + " | " + octave);
					}
				}
			}
		} catch (InvalidMidiDataException | IOException e) {
			e.printStackTrace();
		}

	}

	public void makeUniform() {

		for (Double note : noteList) {
//			System.out.println(NOTE_NAMES[(int) (note * 12)]);
			switch (NOTE_NAMES[(int) (note * 12)]) {
			case "C":
				this.C++;
				break;
			case "C#":
				this.C += 1;
				break;
			case "D":
				this.D += 1;
				break;
			case "D#":
				this.D += 1;
				break;
			case "E":
				this.E += 1;
				break;
			case "F":
				this.F += 1;
				break;
			case "F#":
				this.F += 1;
				break;
			case "G":
				this.G += 1;
				break;
			case "G#":
				this.G += 1;
				break;
			case "A":
				this.A += 1;
				break;
			case "A#":
				this.A += 1;
				break;
			case "B":
				this.B += 1;
				break;
			default:
				break;
			}
		}
		Integer[] distr = { C, D, E, F, G, A, B };
		System.out.println(Arrays.deepToString(distr));
		Integer sum = 0;
		for (Integer d : distr) {
			sum += d;
		}
		Integer mean = (int) ((sum / distr.length) * 0.60);
		// this.mean = mean;
		C = 0;
		D = 0;
		E = 0;
		F = 0;
		G = 0;
		A = 0;
		B = 0;
		System.out.println(mean);
		this.generateLists();
	}

	public void generateData() {

		Integer preVector = 20;
		for (Integer i = 0; i < noteList.size() - (preVector + 1); i += 1) {
			Double[] inputs = new Double[preVector];
			for (Integer ii = 0; ii < preVector; ii += 1) {
				Integer pos = i + ii;
				inputs[ii] = noteList.get(pos);
			}
			data.put(inputs, noteList.get(i + preVector));

		}

		for (Integer i = 0; i < octaveList.size() - (preVector + 1); i += 1) {
			Double[] inputs = new Double[preVector];
			for (Integer ii = 0; ii < preVector; ii += 1) {
				Integer pos = i + ii;
				inputs[ii] = octaveList.get(pos);
			}
			dataO.put(inputs, noteList.get(i + preVector));

		}

	}

	public void SaveOctaves(String loc) {
		String output = "";
		for (Entry<Double[], Double> d : dataO.entrySet()) {
			String out = "";
			for (Double in : d.getKey()) {
				out += (in) + ",";
			}
			out += d.getValue();
			output += out + "\n";
		}
		final Path path = Paths.get(loc);

		try (final BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
				StandardOpenOption.CREATE);) {
			writer.write(output);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void SaveData() {
		String output = "";
		for (Entry<Double[], Double> d : data.entrySet()) {
			String out = "";
			for (Double in : d.getKey()) {
				out += (in) + ",";
			}
			out += d.getValue();
			output += out + "\n";
		}
		final Path path = Paths.get(this.name);

		try (final BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
				StandardOpenOption.CREATE);) {
			writer.write(output);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
