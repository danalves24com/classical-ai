package velo.pl.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

public class modelRun {
	private MultiLayerNetwork model= null;
	private DataSet testingData = null;
	private DataSetIterator privateIt = null;
	public void importModel(MultiLayerNetwork md) {
		this.model = md;
	}
	public void testModel(String file, Integer in, Integer out) throws IOException, InterruptedException {
		RecordReader recordReader = new CSVRecordReader(0, ',');
		recordReader.initialize(new FileSplit(new File(file)));
		Integer linesTest = Files.readAllLines(Paths.get(file)).size();
		privateIt = new RecordReaderDataSetIterator(recordReader, linesTest, in, out);
		this.testingData = privateIt.next();
		// test the model
		INDArray output = model.output(testingData.getFeatureMatrix());
		System.out.println(output);
		Evaluation eval = new Evaluation(3);
		eval.eval(testingData.getLabels(), output);		
		System.out.println(eval.stats());		
	}
	
}
