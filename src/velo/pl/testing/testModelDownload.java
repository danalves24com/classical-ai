package velo.pl.testing;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;

import velo.pl.database.databaseConnection;
import velo.pl.model.modelRun;

public class testModelDownload {

	@Test
	public void test() throws SQLException, IOException, InterruptedException {
		(new databaseConnection()).openConnection();
		modelRun mr = new modelRun();
		mr.importModel((new databaseConnection()).getModel("type2-G_pD8l-load-1"));
		mr.testModel("F:\\Invoy\\Projects\\ml-music\\piano-learning\\data\\bethoven_testing\\mond_2.mid_AUG.txt", 3, 12);
	}

}
