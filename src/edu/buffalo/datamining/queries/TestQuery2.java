package edu.buffalo.datamining.queries;

import java.util.ArrayList;

import edu.buffalo.datamining.services.DBOperations;

public class TestQuery2 {

	private String diseaseDescription = null;

	public TestQuery2(String diseaseDescription) {
		this.diseaseDescription = diseaseDescription;
	}

	public static void main(String args[]) {
		TestQuery2 query=new TestQuery2("tumor");
		query.evaluate();
	}

	public ArrayList<String> evaluate(){
		DBOperations operations=new DBOperations();
		ArrayList<String>drugTypes=null;
		try{
			drugTypes=operations.getDrugTypes(diseaseDescription);
						
		}catch(Exception e){
			e.printStackTrace();
		}
		return drugTypes;
	}
	
	public String getDiseaseDescription() {
		return diseaseDescription;
	}

	public void setDiseaseDescription(String diseaseDescription) {
		this.diseaseDescription = diseaseDescription;
	}
}
