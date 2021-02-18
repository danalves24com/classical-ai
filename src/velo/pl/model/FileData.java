package velo.pl.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.writable.Writable;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

public class FileData {
	private Integer in = 20;
	private float[] getOutputArray(Double outDouble, Integer dims) {
		float[] out = new float[dims];
		Integer index = 0;
		for (float f : out) {
			if (index == Math.round(outDouble * 12)) {
				out[index] = 1;
			} else {
				out[index] = 0;
			}

			index += 1;
		}
		return out;

	}

	public void setParam() {
		
	}
	
	public double[] getSliceOfArray(List<Writable> list, int start, int end) {

		double[] slice = new double[end - start];

		for (int i = 0; i < slice.length; i++) {
			slice[i] = list.get(start + i).toDouble();
		}

		return slice;
	}

	public DataSet get(String path, Integer inC, Integer outC) throws IOException, InterruptedException {
		String trainingData = path;
		RecordReader recordReader = new CSVRecordReader(0, ',');
		recordReader.initialize(new FileSplit(new File(trainingData)));
		ArrayList<double[]> inp = new ArrayList<double[]>();
		ArrayList<float[]> out = new ArrayList<float[]>();
//		recordReader.initialize(new FileSplit(new File(trainingData)));
		Integer it = 0;
		while (recordReader.hasNext()) {
			List<Writable> rec = recordReader.next();
//			System.out.println(Arrays.toString(rec.toArray()));//
			Boolean fail1 = false, fail2 = false;
			try {
//				System.out.println(": " + Arrays.toString(getSliceOfArray(recordReader.next(), 0, 6)) + "  |  " + recordReader.next().get(6));
				getSliceOfArray(rec, 0, 20);				
			} catch (Exception e) {		
//				System.out.println("fail1");
				fail1=true;
			}
			if(!fail1) {
				try {
					getOutputArray(rec.get(inC).toDouble(), outC);
				}
				catch (Exception e) {
					System.out.println("fail2");
					fail2 = true;
				}
				
			}			
			else {
				
			}
			
			if((!fail1)) {
				if(!fail2) {
					inp.add(getSliceOfArray(rec, 0, inC));
					out.add(getOutputArray(rec.get(inC).toDouble(), outC));
				}
			}
			else {
				
			}
			
			it += 1;
		}
		double[][] inputs = new double[inp.size()][inC];
		float[][] outputs = new float[out.size()][outC];
		for (double[] db : inp) {
			inputs[inp.indexOf(db)] = db;
		}
		for (float[] db : out) {
			outputs[out.indexOf(db)] = db;
		}
//		System.out.println(Arrays.deepToString(inputs));
//		System.out.println(path);
//		System.out.println(it);
//		System.out.println(Arrays.deepToString(outputs));
//		RecordReaderDataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, dat, 6, 12);		System.out.println(inputs.length + " | " + outputs.length);		
		DataSet teachingData = new DataSet(Nd4j.create(inputs), Nd4j.create(outputs));
		System.out.println(teachingData);
//		System.out.println(teachingData);
//		System.out.println(teachingData);/
		return teachingData;
	}
}
