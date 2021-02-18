package velo.pl.testing;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.writable.Writable;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.junit.Test;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

public class dataLoadingTesting {
	private float[] getOutputArray(Double outDouble, Integer dims) {
		float[] out = new float[dims];
		Integer index = 0;
		for(float f : out) {			
			if(index==Math.round(outDouble*12)) {
				out[index] = 1;
			}else {out[index]=0;}
			
			index+=1;
		}
		return out;
		
	}
	public double[] getSliceOfArray(List<Writable> list, int start, int end) {


		double[] slice = new double[end - start];


		for (int i = 0; i < slice.length; i++) {
			slice[i] = list.get(start + i).toDouble();
		}


		return slice;
	}

	@Test
	public void test() throws IOException, InterruptedException {
		String trainingData = "F:\\Invoy\\Projects\\ml-music\\piano-learning\\data\\bethoven_testing\\mond_3.mid_AUG.txt";
		RecordReader recordReader = new CSVRecordReader(0, ',');
		recordReader.initialize(new FileSplit(new File(trainingData)));
		ArrayList<double[]> inp = new ArrayList<double[]>();
		ArrayList<float[]> out = new ArrayList<float[]>();
//		recordReader.initialize(new FileSplit(new File(trainingData)));
		Integer it = 0;
		while (recordReader.hasNext()) {
			try {
//				System.out.println(": " + Arrays.toString(getSliceOfArray(recordReader.next(), 0, 6)) + "  |  " + recordReader.next().get(6));
				inp.add(getSliceOfArray(recordReader.next(), 0, 5));
				out.add(getOutputArray(recordReader.next().get(6).toDouble(), 12));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			it += 1;
		}
		double[][] inputs = new double[inp.size()][6];
		float[][] outputs = new float[out.size()][12];
		for(double[] db : inp) {
			inputs[inp.indexOf(db)] = db;
		}
		for(float[] db : out) {
			outputs[out.indexOf(db)] = db;
		}
//		System.out.println(Arrays.deepToString(inputs));
		System.out.println(it);
//		System.out.println(Arrays.deepToString(outputs));
//		RecordReaderDataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, dat, 6, 12);		System.out.println(inputs.length + " | " + outputs.length);		
		DataSet teachingData = new DataSet(Nd4j.create(inputs), Nd4j.create(outputs));		
		System.out.println(teachingData);
//		System.out.println(teachingData);/
	}

}
