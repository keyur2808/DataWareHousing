package edu.buffalo.datamining.wrapper;

import java.util.ArrayList;

public class TestResult {

	private boolean result;
	private int count; 
	private ArrayList<Integer>geneExpressionValues;
	
	public TestResult(boolean result,int count,ArrayList<Integer>geneExpressionValues) {
		this.setResult(result);
		this.setCount(count);
		this.setGeneExpressionValues(geneExpressionValues);
	}
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public ArrayList<Integer> getGeneExpressionValues() {
		return geneExpressionValues;
	}
	public void setGeneExpressionValues(ArrayList<Integer> geneExpressionValues) {
		this.geneExpressionValues = geneExpressionValues;
	}

}
