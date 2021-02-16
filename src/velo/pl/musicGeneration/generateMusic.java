package velo.pl.musicGeneration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

public class generateMusic {
	private DataSetIterator iterator = null;
	private DataSet data = null;
	public final String[] NOTE_NAMES = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
	public void loadData(String initData) throws IOException, InterruptedException {
		
		RecordReader recordReader = new CSVRecordReader(0, ',');
		recordReader.initialize(new FileSplit(new File(initData)));
		Integer linesTeach = Files.readAllLines(Paths.get(initData)).size();
		iterator = new RecordReaderDataSetIterator(recordReader, linesTeach, 3, 12);
		this.data = iterator.next();
	}
	public void generate(MultiLayerNetwork model) {				
		INDArray evaluation = model.output(data.getFeatureMatrix());
		Integer mostConfidentIndex = 0;
		Double mostConfidentValue = 0.0;
		for(Integer pI = 0 ; pI < 12 ; pI+=1) {
			Double p = evaluation.getColumn(pI).getDouble(0);
			System.out.println(p);
			if(p>mostConfidentValue) {
				mostConfidentIndex = pI;
				mostConfidentValue = p;
			}
		}
		System.out.println("best note: " + NOTE_NAMES[mostConfidentIndex]);	
		
	}
}
