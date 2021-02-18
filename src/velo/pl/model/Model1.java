package velo.pl.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import velo.pl.database.databaseConnection;

public class Model1 {
	private MultiLayerNetwork model;
	private Integer in = null, out = null, lSize = null;
	private DataSet testingData = null;

	public void setTestingData(String pathToFile) throws IOException, InterruptedException {
		this.testingData = (new FileData()).get(pathToFile, in , out);
	}

	private Double thisAcc = 0.0;

	public void test() throws FileNotFoundException {
		INDArray output = model.output(testingData.getFeatureMatrix());
		System.out.println(output);
		Evaluation eval = new Evaluation(3);
		eval.eval(testingData.getLabels(), output);
//		System.out.println(model.output(testing.getFeatureMatrix()));
		System.out.println(eval.stats());
		this.thisAcc = eval.accuracy();
//		System.out.println(eval.getCostArray());
	}

	public void train(DataSet dataSet) {

		System.out.print("learning new data");
		Integer ep = 0;
		Double accuracy = 0.0, target = 0.4;
		while (accuracy < 0.8 && ep <= 15) {
			System.out.println("running Epoch: " + ep + " Current ACC: " + accuracy + " prop: " + accuracy / target);
			model.fit(dataSet);
			try {
				this.test();
				accuracy = this.thisAcc;
				ep+=1;
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	public void create(Integer in, Integer out, Integer lSize) {
		this.in = in;
		this.out = out;
		this.lSize = lSize;
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().iterations(1000).activation(Activation.RELU)
				.weightInit(WeightInit.XAVIER).learningRate(0.1).regularization(true).l2(0.0001).list()
				.layer(0, new DenseLayer.Builder().nIn(this.in).nOut(lSize).build())
				.layer(1, new DenseLayer.Builder().nIn(lSize).nOut(lSize).build())
				.layer(2, new DenseLayer.Builder().nIn(lSize).nOut(lSize).build())
				.layer(3,
						new OutputLayer.Builder(LossFunctions.LossFunction.RECONSTRUCTION_CROSSENTROPY)
								.activation(Activation.SOFTMAX).nIn(lSize).nOut(this.out).build())
				.backprop(true).pretrain(false).build();

		// compile the model
		model = new MultiLayerNetwork(conf);
		model.init();
	}

	public void uploadSelfToDatabase(String name, databaseConnection con) throws IOException {
		con.addModel(this.model, name);
	}
}
