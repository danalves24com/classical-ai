package velo.pl.testing;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Test;

import velo.pl.database.databaseConnection;
import velo.pl.musicGeneration.generateMusic;

public class testGenerator {

	@Test
	public void test() throws IOException, InterruptedException, SQLException {
		new databaseConnection().openConnection();
		generateMusic gen = new generateMusic();
		gen.setSeedName("F:\\Invoy\\Projects\\ml-music\\piano-learning\\data\\generation_seeds\\gen_seed_01.txt");		
		gen.generate((new databaseConnection()).getModel("type4-G_m7JF-load-4"));
		gen.generateOctaves((new databaseConnection()).getModel("type4-G_tCJL-load-0"));
		//gen.play();
		//gen.saveNotesToFile("flute_composition.mid");
	}
	
}
