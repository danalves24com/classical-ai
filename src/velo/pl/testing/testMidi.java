package velo.pl.testing;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import velo.pl.musicParse.generateFromMidi;

public class testMidi {

	@Test
	public void test() {

		Integer index = 0;

		// Creating a File object for directory
		File directoryPath = new File("F:\\Invoy\\Projects\\ml-music\\piano-learning\\data\\bethoven");
		// List of all files and directories
		String contents[] = directoryPath.list();
		System.out.println("augmentig data:");
		for (int i = 0; i < contents.length; i++) {
			String path = "F:\\Invoy\\Projects\\ml-music\\piano-learning\\data\\bethoven\\" + contents[i];
			generateFromMidi gfm = new generateFromMidi(path, "F:\\Invoy\\Projects\\ml-music\\piano-learning\\data\\bethoven_aug\\" + contents[i] + "_AUG" + ".txt");
			gfm.generateLists();
			gfm.generateData();
			gfm.SaveData();
			index += 1;
		}

	}

}
