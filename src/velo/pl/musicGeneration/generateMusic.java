package velo.pl.musicGeneration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.jfugue.player.Player;
import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.*;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import velo.pl.model.FileData;

public class generateMusic {
	private DataSetIterator iterator = null;
	private DataSet data = null;
	public final String[] NOTE_NAMES = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
	String name = null;
	ArrayList<Double> notes = new ArrayList<Double>();

	public void setSeedName(String name) {
		this.name = name;
	}

	public void loadData(String initData) throws IOException, InterruptedException {
		this.data = new FileData().get(initData);
	}

	private void appendNoteToFile(Double note) throws IOException {
		FileReader fileReader = new FileReader(this.name);
		String line = null;
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		while ((line = bufferedReader.readLine()) != null) {
			if (line.contains(",")) {
				System.out.println(line);
				String[] split = line.split(",");

				// vomit worthy -- I know
				for (Integer i = 0; i < 20; i += 1) {
					split[i] = split[i + 1].toString();
				}
				split[20] = Double.toString(note);
				line = String.join(",", split);
				System.out.println(line + " | " + note);
				FileWriter fileWriter = new FileWriter(this.name);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write(line);
				bufferedWriter.close();
			} else {

			}
		}

	}

	public void play() {
		Player player = new Player();
		ArrayList<String> s = new ArrayList<String>();
		for (Double note : notes) {
			String ind = NOTE_NAMES[(int) (note * 12)];
			s.add(ind+"5q");
		}
		player.play(new Pattern(String.join(" ", s)).setTempo(150).setInstrument("Flute"));
	}

	public void saveNotesToFile(String name) {

		ArrayList<String> s = new ArrayList<String>();
		for (Double note : notes) {
			String ind = NOTE_NAMES[(int) (note * 12)];
			s.add(ind);
		}
		String list = String.join(" ", s);
		Pattern pattern = new Pattern(list);
		try {
			MidiFileManager.savePatternToMidi(pattern, new File(name));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static DecimalFormat df2 = new DecimalFormat("#.00");

	public void generate(MultiLayerNetwork model) throws IOException, InterruptedException {
		Integer max = 200, current = 0;
		for (Integer i = 0; i < 200; i += 1) {
			this.loadData("F:\\Invoy\\Projects\\ml-music\\piano-learning\\data\\generation_seeds\\gen_seed_01.txt");
			INDArray evaluation = model.output(this.data.getFeatureMatrix());
			Double mostConfidentIndex = 0.0;
			Double mostConfidentValue = 0.0;
			System.out.println(evaluation);
			for (Integer pI = 0; pI < 12; pI += 1) {
				Double p = evaluation.getColumn(pI).getDouble(0);
//				System.out.println(p);
				if (p > mostConfidentValue) {
					mostConfidentIndex = (double) Double.valueOf(df2.format((double) (((float) pI) / 12)));
					System.out.println(pI + " | " + (float) pI / 12);
					mostConfidentValue = p;
				}
			}
			appendNoteToFile(mostConfidentIndex);
			notes.add(mostConfidentIndex);
//			System.out.println("best note: " + NOTE_NAMES[mostConfidentIndex] + " prob: " + mostConfidentValue);
		}
		System.out.println(Arrays.deepToString(notes.toArray()));
	}
}
