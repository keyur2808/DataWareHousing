package edu.buffalo.datamining.queries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import com.google.common.math.DoubleMath;
import com.google.common.primitives.Doubles;

import edu.buffalo.datamining.services.DBOperations;

public class CorrelationTest {

	private String disease1;
	private String disease2;
	private int goIdValue;

	public CorrelationTest(String disease1, String disease2, int goIdValue) {
		this.disease1 = disease1;
		this.disease2 = disease2;
		this.goIdValue = goIdValue;
	}

	public static void main(String args[]) {
		CorrelationTest tst = new CorrelationTest("ALL", "AML", 7154);
		tst.evaluateAverageCorrelation();
	}

	public String evaluateAverageCorrelation() {
		String averageCorrelation = null;
		DBOperations operations = new DBOperations();
		ArrayList<Double> corr = new ArrayList<>();
		double correlation = -2;
		try {
			long y1=System.currentTimeMillis();
			Map<Integer, ArrayList<Integer>> disease1Values = operations
					.getExpressionValuesPerPatient(disease1, goIdValue);
			Map<Integer, ArrayList<Integer>> disease2Values = null;

			if (!disease1.equalsIgnoreCase(disease2)) {
				disease2Values = operations.getExpressionValuesPerPatient(
						disease2, goIdValue);
				
				for (Map.Entry<Integer, ArrayList<Integer>> disease1Value : disease1Values
						.entrySet()) {
					ArrayList<Integer> values1 = disease1Value.getValue();
					double[] geneExpressionValuesDisease1 = Doubles
							.toArray(values1);
					for (Map.Entry<Integer, ArrayList<Integer>> disease2Value : disease2Values
							.entrySet()) {
						ArrayList<Integer> values2 = disease2Value.getValue();
						double[] geneExpressionValuesDisease2 = Doubles
								.toArray(values2);
						PearsonsCorrelation correlations = new PearsonsCorrelation();
						double val = correlations.correlation(
								geneExpressionValuesDisease1,
								geneExpressionValuesDisease2);
						//System.out.println(val);
						corr.add(Double.valueOf(val));
					}
				}

			} else {
				Set<Integer> disease1Val = disease1Values.keySet();
				List<Integer> disease1Value = new ArrayList<Integer>(
						disease1Val);
				for (int i = 0; i < disease1Values.entrySet().size(); i++) {
					ArrayList<Integer> values1 = disease1Values
							.get(disease1Value.get(i));
					System.out.println(values1);
					double[] geneExpressionValuesDisease1 = Doubles
							.toArray(values1);

					for (int j = i + 1; j < disease1Values.entrySet().size(); j++) {
						ArrayList<Integer> values2 = disease1Values
								.get(disease1Value.get(j));
						double[] geneExpressionValuesDisease2 = Doubles
								.toArray(values2);
						
						PearsonsCorrelation correlations = new PearsonsCorrelation();
						double val = correlations.correlation(
								geneExpressionValuesDisease1,
								geneExpressionValuesDisease2);
						System.out.println(val);
						corr.add(Double.valueOf(val));

					}
				}

			}
			;

			double cor[] = Doubles.toArray(corr);
			correlation = DoubleMath.mean(cor);
			averageCorrelation = String.valueOf(correlation);
			long y2=System.currentTimeMillis();
			System.out.println(averageCorrelation);
			System.out.println("ss:"+(y2-y1));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return averageCorrelation;
	}

	public String getDisease1() {
		return disease1;
	}

	public void setDisease1(String disease1) {
		this.disease1 = disease1;
	}

	public String getDisease2() {
		return disease2;
	}

	public void setDisease2(String disease2) {
		this.disease2 = disease2;
	}

	public int getGoIdValue() {
		return goIdValue;
	}

	public void setGoIdValue(int goIdValue) {
		this.goIdValue = goIdValue;
	}

}
