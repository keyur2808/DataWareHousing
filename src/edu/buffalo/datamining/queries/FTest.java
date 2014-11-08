package edu.buffalo.datamining.queries;

import java.util.ArrayList;
import java.util.Collection;

import edu.buffalo.datamining.services.DBOperations;
import edu.buffalo.datamining.utils.FStatistic;

public class FTest {

	private String disease1;
	private String disease2;
	private int goIdValue;
	private String disease3;
	private String disease4;

	public FTest(String disease1, String disease2, String disease3,
			String disease4, int goIdValue) {
		this.disease1 = disease1;
		this.disease2 = disease2;
		this.disease3 = disease3;
		this.disease4 = disease4;
		this.goIdValue = goIdValue;
	}

	public static void main(String args[]) {
		FTest tst = new FTest("ALL", "AML", "Colon tumor", "Breast tumor", 7154);
		tst.evaluate();

	}

	public String evaluate() {

		String result = null;

		try {

			disease1 = " = '" + disease1;
			disease2 = " = '" + disease2;
			disease3 = " = '" + disease3;
			disease4 = " = '" + disease4;
			
			DBOperations operations = new DBOperations();
			long t3=System.currentTimeMillis();
			double[] expressionValues1 = operations
					.StatisticsQueryForTAndFTest(disease1, goIdValue);
			double[] expressionValues2 = operations
					.StatisticsQueryForTAndFTest(disease2, goIdValue);
			double[] expressionValues3 = operations
					.StatisticsQueryForTAndFTest(disease3, goIdValue);
			double[] expressionValues4 = operations
					.StatisticsQueryForTAndFTest(disease4, goIdValue);
			
			Collection<double[]> expressionValueCollection = new ArrayList<>();
			expressionValueCollection.add(expressionValues1);
			expressionValueCollection.add(expressionValues2);
			expressionValueCollection.add(expressionValues3);
			expressionValueCollection.add(expressionValues4);

			FStatistic anova = new FStatistic();
			double resultValue = anova.oneWayAnova(expressionValueCollection);
			long t4=System.currentTimeMillis();
			System.out.println(t4-t3);
			result = String.valueOf(resultValue);
			System.out.println(result);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
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
