package edu.buffalo.datamining.queries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.inference.TTest;

import com.google.common.primitives.Booleans;
import com.google.common.primitives.Doubles;

import edu.buffalo.datamining.services.DBOperations;
import edu.buffalo.datamining.wrapper.TestResult;

public class HealthClassifier {
	
	private String diseaseName=null;
	private double alpha;
	private String patientName;
	int count=-1;
	
	public HealthClassifier(String diseaseName,double alpha,String patientName) {
		this.alpha=alpha;
		this.diseaseName=diseaseName;
		this.patientName=patientName;
	}

	public static void main(String args[]){
		String[] tests = {"Test1", "Test2", "Test3", "Test4", "Test5"};
		for(String test:tests){
			HealthClassifier discovery=new HealthClassifier("ALL", 0.01,test);
			ArrayList<Integer>infoGenes=discovery.getInformativeGenes();
			Collections.sort(infoGenes);
			//System.out.println(infoGenes);
			if (discovery.count!=0){
				discovery.evaluate(infoGenes);
			}else{
				System.out.println("Cannot Predicate Insufficient Data");
			}
		}
		
	}
	
	public TestResult getResult(){
		ArrayList<Integer>infoGenes=this.getInformativeGenes();
        TestResult rs=null;
        if (count!=0)
        {
        boolean isAffected=this.evaluate(infoGenes);
        rs=new TestResult(isAffected,count,infoGenes);
        }else{
        	 rs=new TestResult(false,count,infoGenes);
        }
        return rs;
	}
	
	
	
	public ArrayList<Integer> getInformativeGenes() {
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
				//double t=tst.homoscedasticTTest(diseaseValues, controlValues);
				//System.out.println(key+":"+t);
				//System.out.println();
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
		return informativeGenes;
	}

	public boolean evaluate(ArrayList<Integer>infoGenes){
	DBOperations operations=new DBOperations();
	PearsonsCorrelation corr=new PearsonsCorrelation();
	long t1=System.currentTimeMillis();
	Map<Integer,ArrayList<Integer>>diseaseGroupInfoGenes=operations.getDiseaseGroupValues(diseaseName,infoGenes);
    System.out.println(diseaseGroupInfoGenes);
	Map<Integer,ArrayList<Integer>>nonDiseaseGroupInfoGenes=operations.getControlGroupValues(diseaseName,infoGenes);
	System.out.println(nonDiseaseGroupInfoGenes);
	ArrayList<Integer>testValues=operations.getTestExpressionValues(patientName,infoGenes);
    System.out.println(testValues);
	
    double[] testValus=Doubles.toArray(testValues);
	double[] corrDiseaseValues=new double[diseaseGroupInfoGenes.size()];
	double[] corrControlValues=new double[nonDiseaseGroupInfoGenes.size()];
	int i=0;
	
	for (Map.Entry<Integer,ArrayList<Integer>> diseaseGroupInfoGenesValue : diseaseGroupInfoGenes.entrySet()){
		ArrayList<Integer>values=diseaseGroupInfoGenesValue.getValue();
		double[] valu=Doubles.toArray(values);
		corrDiseaseValues[i]=corr.correlation(valu, testValus);
//		System.out.println("Patient: " + diseaseGroupInfoGenesValue.getKey()  + " Correlation: " + corrDiseaseValues[i]);
		i++;
	}
	
	i=0;
	for (Map.Entry<Integer,ArrayList<Integer>> controlGroupInfoGenesValue : nonDiseaseGroupInfoGenes.entrySet()){
		ArrayList<Integer>val=controlGroupInfoGenesValue.getValue();
		double[] vals=Doubles.toArray(val);
		corrControlValues[i]=corr.correlation(vals, testValus);
//		System.out.println("Patient: " + controlGroupInfoGenesValue.getKey()  + " Correlation: " + corrControlValues[i]);
		i++;
	}
	
	TTest tst=new TTest();
	boolean result=tst.homoscedasticTTest(corrDiseaseValues, corrControlValues, alpha);
	double val=tst.homoscedasticTTest(corrDiseaseValues, corrControlValues);
	long t2=System.currentTimeMillis();
	System.out.println(t2-t1);
    System.out.println("Val :"+ val);
	System.out.println(result);
	return result;
	
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
