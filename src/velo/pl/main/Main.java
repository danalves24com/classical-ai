package velo.pl.main;

import java.io.File;
import java.io.IOException;

import velo.pl.database.databaseConnection;
import velo.pl.model.Model;
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
			model.loadData(paths[0]+ (onServer?"/":"\\") + file, null, 3, 12);
			model.trainNetwork(null);
			model.uploadSelfToDatabase("gen"+gen+"-load-"+itteration, new databaseConnection());
			itteration+=1;
		}

	}

}
