package edu.buffalo.datamining.queries;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.inference.TTest;

import edu.buffalo.datamining.services.DBOperations;

public class TStatisticTest {
	
	private String diseaseName;
	private String diseaseName2;
	private int goId;

	public TStatisticTest(String diseaseName, String diseaseName2, int goId) {
		this.setDiseaseName(diseaseName);
		this.setDiseaseName2(diseaseName2);
		this.setGoId(goId);
	}
	
	public static void main(String args[]){
		TStatisticTest tst=new TStatisticTest("ALL", "ALLOthers", 12502);
		tst.evaluate();
	}
	
	public List<String> evaluate(){
		List<String> result=new ArrayList<>();
		double t = 0;
		double tValue=0;
		try{
			if (diseaseName2!=null && diseaseName2.equalsIgnoreCase("AllOthers")) {
				diseaseName2 = " <> '" + diseaseName;
			} else {
				diseaseName2 = " = '" + diseaseName2;
			}
			diseaseName = " = '" + diseaseName;
			DBOperations operations=new DBOperations();
			double[] expressionValues1=operations.StatisticsQueryForTAndFTest(diseaseName,goId);
			double[] expressionValues2=operations.StatisticsQueryForTAndFTest(diseaseName2, goId);
			
			
			TTest tst=new TTest();
			t=tst.homoscedasticTTest(expressionValues1, expressionValues2);
			tValue=tst.homoscedasticT(expressionValues1, expressionValues2);
			double tUnEqual=tst.t(expressionValues1, expressionValues2);
			result.add(String.valueOf(tValue));
			result.add(String.valueOf(t));
			result.add(String.valueOf(tUnEqual));
			System.out.println("T-value "+tValue);
			System.out.println("P-value of TTest "+t);
			System.out.println("P-value of TTest "+tUnEqual);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}

	public String getDiseaseName() {
		return diseaseName;
	}

	public void setDiseaseName(String diseaseName) {
		this.diseaseName = diseaseName;
	}

	public String getDiseaseName2() {
		return diseaseName2;
	}

	public void setDiseaseName2(String diseaseName2) {
		this.diseaseName2 = diseaseName2;
	}

	public int getGoId() {
		return goId;
	}

	public void setGoId(int goId) {
		this.goId = goId;
	}

}
