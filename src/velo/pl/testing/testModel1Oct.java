package velo.pl.testing;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import velo.pl.database.databaseConnection;
import velo.pl.model.FileData;
import velo.pl.model.Model1;

public class testModel1Oct {
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
	@Test
	public void test() throws IOException, InterruptedException {
		new databaseConnection().openConnection();
		Model1 model = new Model1();
		Integer outputDims = 10, inputDims = 20;
		model.create(inputDims, outputDims, 8);
		model.setTestingData("F:\\Invoy\\Projects\\ml-music\\piano-learning\\data\\bethoven_testing_OCT\\mond_3.mid_AUG.txt");
		Integer itteration = 0;
		File directoryPath = new File("F:\\Invoy\\Projects\\ml-music\\piano-learning\\data\\bethoven_aug_octaves");
		String contents[] = directoryPath.list();
		String gen = getAlphaNumericString(4);
		for(String content : contents) {
			model.train((new FileData()).get("F:\\Invoy\\Projects\\ml-music\\piano-learning\\data\\bethoven_aug_octaves\\"+content, inputDims, outputDims));
 			//model.uploadSelfToDatabase("OCT-type5-G_"+gen+"-load-"+itteration, new databaseConnection());//			
			itteration+=1;			
		}
		model.test();
	}

}
