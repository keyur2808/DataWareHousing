package edu.buffalo.datamining.queries;

import java.util.ArrayList;

import edu.buffalo.datamining.services.DBOperations;

public class TestQuery3 {

	private String diseaseName;
	private int cl_id;
	private int mu_id;
	
	public TestQuery3(String diseaseName,int cl_id,int mu_id) {
		this.setDiseaseName(diseaseName);
		this.setCl_id(cl_id);
		this.setMu_id(mu_id);
	}

	public static void main(String args[]){
		TestQuery3 query3=new TestQuery3("ALL",2,1);
		query3.evaluate();
	}
	
	public ArrayList<String> evaluate(){
		DBOperations operations=new DBOperations();
		ArrayList<String>expValues=operations.getExpressionValuesByCluster(diseaseName, cl_id, mu_id);
		String count="Count is "+expValues.size();
		//expValues.add(count);
		System.out.println(count);
		return expValues;
	}
	
	
	public String getDiseaseName() {
		return diseaseName;
	}

	public void setDiseaseName(String diseaseName) {
		this.diseaseName = diseaseName;
	}

	public int getCl_id() {
		return cl_id;
	}

	public void setCl_id(int cl_id) {
		this.cl_id = cl_id;
	}

	public int getMu_id() {
		return mu_id;
	}

	public void setMu_id(int mu_id) {
		this.mu_id = mu_id;
	}

}
