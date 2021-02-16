package velo.pl.testing;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import velo.pl.database.databaseConnection;
import velo.pl.model.Model;

public class testModel {
	private String getAlphaNumericString(int n)
    {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                    + "0123456789"
                                    + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            int index = (int)(AlphaNumericString.length()* Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }

        return sb.toString();
    }
	Boolean onServer = false;
	
	String[] winPaths = {"F:\\Invoy\\Projects\\ml-music\\piano-learning\\data\\bethoven_aug"},
			linuxPaths = {"/home/velo/pl/data/bethoven_aug"},
			paths = {onServer?linuxPaths[0]:winPaths[0]};
	
	@Test
	public void test() throws FileNotFoundException, IOException, InterruptedException {
		Model model = new Model();		
		Integer itteration = 0;
		File directoryPath = new File(paths[0]);
		String contents[] = directoryPath.list();
		new databaseConnection().openConnection();
		model.loadParams(3,  12);
		model.createNetwork();
		String gen = getAlphaNumericString(4);
		for (String file : contents) {
			System.out.println("training model for: " + file);
			model.loadData(paths[0]+ "\\" + file, null, 3, 12);
			model.trainNetwork(null);
			model.uploadSelfToDatabase("gen"+gen+"-load-"+itteration, new databaseConnection());
			itteration+=1;
		}

//		model.loadData(null, "F:\\Invoy\\Projects\\ml-music\\piano-learning\\data\\bethoven_testing\\mond_3.mid_AUG.txt", 3, 12);
//		model.testNetwork();
		
	}

}
