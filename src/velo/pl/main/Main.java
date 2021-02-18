package velo.pl.main;

import java.io.File;
import java.io.IOException;

import velo.pl.database.databaseConnection;
import velo.pl.model.FileData;
import velo.pl.model.Model;
import velo.pl.model.Model1;
import velo.pl.testing.testModel;

public class Main {
	private static String getAlphaNumericString(int n)
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
	static Boolean onServer = true;
	
	static String[] winPaths = {"F:\\Invoy\\Projects\\ml-music\\piano-learning\\data\\bethoven_aug"};

	static String[] linuxPaths = {"/home/velo/data/bethoven_aug"};

	static String[] paths = {onServer?linuxPaths[0]:winPaths[0]};
	
	public static void main(String[] args) throws IOException, InterruptedException {
		new databaseConnection().openConnection();
		Model1 model = new Model1();
		Integer outputDims = 12, inputDims = 20;
		model.create(inputDims, outputDims, 16);
		model.setTestingData("/home/velo/data/bethoven_testing/mond_2.mid_AUG.txt");
		Integer itteration = 0;
		File directoryPath = new File("/home/velo/data/bethoven_aug");
		String contents[] = directoryPath.list();
		String gen = getAlphaNumericString(4);
		for(String content : contents) {
			model.train((new FileData()).get("/home/velo/data/bethoven_aug/"+content, inputDims, outputDims));
			model.uploadSelfToDatabase("type3-G_"+gen+"-load-"+itteration, new databaseConnection());
//			
			itteration+=1;			
		}
		model.test();
		
		
	}

}
