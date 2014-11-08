package edu.buffalo.datamining.queries;

import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.math3.stat.inference.TTest;

import com.google.common.primitives.Booleans;
import com.google.common.primitives.Doubles;

import edu.buffalo.datamining.services.DBOperations;

public class GeneDiscovery {
	
	private String diseaseName=null;
	private double alpha;
	
	public GeneDiscovery(String diseaseName,double alpha) {
		this.alpha=alpha;
		this.diseaseName=diseaseName;
	}

	public static void main(String args[]){
		GeneDiscovery discovery=new GeneDiscovery("ALL", 0.01);
		discovery.evaluate();
	}
	
	
	public int evaluate() {
		int count=-1;
		DBOperations operations=new DBOperations();
		ArrayList<Integer>informativeGenes=new ArrayList<>();
		try{
			Map<Integer,ArrayList<Integer>>diseaseExpressions=operations.getDiseaseGroupValues(diseaseName);
			Map<Integer,ArrayList<Integer>>controlExpressions=operations.getControlGroupValues(diseaseName);
			
			int i = 0;
			boolean[] result=new boolean[diseaseExpressions.size()];
			for (Map.Entry<Integer,ArrayList<Integer>>expressions:diseaseExpressions.entrySet()){
				Integer key=expressions.getKey();
				ArrayList<Integer>disease=diseaseExpressions.get(key);
				ArrayList<Integer>control=controlExpressions.get(key);
				
				double[] diseaseValues=Doubles.toArray(disease);
				double[] controlValues=Doubles.toArray(control);
				
				TTest tst=new TTest();
				result[i]=tst.homoscedasticTTest(diseaseValues, controlValues,alpha);
				
				if (result[i]){
					informativeGenes.add(key);
				}
				
				i++;
								
								
			}
			
			count=Booleans.countTrue(result);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("No of informative Genes "+count);
		return count;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public String getDiseaseName() {
		return diseaseName;
	}

	public void setDiseaseName(String diseaseName) {
		this.diseaseName = diseaseName;
	}
}
