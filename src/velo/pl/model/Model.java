package velo.pl.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.bytedeco.javacpp.freenect2.Logger;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import velo.pl.database.databaseConnection;

// TODO: Auto-generated Javadoc
/**
 * The Class Model.
 */
public class Model {

	private MultiLayerNetwork model;

	/** The testing. */
	private DataSet teachingData = null, testingData = null;

	/** The out. */
	private Integer in = null, out = null;

	/** The private it. */
	private DataSetIterator iterator = null, privateIt = null;

	public void loadParams(Integer in, Integer out) {
		this.in = in;
		this.out = out;
	}

	/**
	 * Load data.
	 *
	 * @param trainingData the training data
	 * @param testingData  the testing data
	 * @param in           the in
	 * @param out          the out
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException           Signals that an I/O exception has occurred.
	 * @throws InterruptedException  the interrupted exception
	 */
	public void loadData(String trainingData, String testingData, Integer in, Integer out)
			throws FileNotFoundException, IOException, InterruptedException {
		try (RecordReader recordReader = new CSVRecordReader(0, ',')) {
			this.in = in;
			this.out = out;
			if (trainingData != null) {
				recordReader.initialize(new FileSplit(new File(trainingData)));
				Integer linesTeach = Files.readAllLines(Paths.get(trainingData)).size();
				iterator = new RecordReaderDataSetIterator(recordReader, linesTeach, in, out);
				teachingData = iterator.next();
				System.out.println(linesTeach + " : teaching data");
			} else {

			}
			if (testingData != null) {
				recordReader.initialize(new FileSplit(new File(testingData)));
				Integer linesTest = Files.readAllLines(Paths.get(testingData)).size();
				privateIt = new RecordReaderDataSetIterator(recordReader, linesTest, in, out);
				this.testingData = privateIt.next();

			} else {
			}
		}
	}
	
	/**
	 * Train network.
	 *
	 * @param testingData the testing data
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	public void trainNetwork(String testingData) throws IOException, InterruptedException {
		if (testingData != null) {
			RecordReader recordReader = new CSVRecordReader(0, ',');
			recordReader.initialize(new FileSplit(new File(testingData)));
			Integer linesTest = Files.readAllLines(Paths.get(testingData)).size();
			privateIt = new RecordReaderDataSetIterator(recordReader, linesTest, in, out);
			this.testingData = privateIt.next();
		} else {
		}
		model.fit(teachingData);
		System.out.println();
	}
	public void testNetwork() {
		// test the model
		INDArray output = model.output(testingData.getFeatureMatrix());
//		System.out.println(output);
		Evaluation eval = new Evaluation(3);
		eval.eval(testingData.getLabels(), output);
//		System.out.println(model.output(testing.getFeatureMatrix()));
		System.out.println(eval.stats());
	}

	/**
	 * Creates the network.
	 */
	public void createNetwork() {
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().iterations(10000)
				.activation(Activation.RELU).weightInit(WeightInit.XAVIER).learningRate(0.1).regularization(true)
				.l2(0.0001).list().layer(0, new DenseLayer.Builder().nIn(this.in).nOut(10).build())
				.layer(1, new DenseLayer.Builder().nIn(10).nOut(10).build())
				.layer(2,
						new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
								.activation(Activation.SOFTMAX).nIn(10).nOut(this.out).build())
				.backprop(true).pretrain(false).build();

		// compile the model
		model = new MultiLayerNetwork(conf);
		model.init();
	}
	

	public void uploadSelfToDatabase(String name, databaseConnection con) throws IOException {
		con.addModel(this.model, name);
	}
}
