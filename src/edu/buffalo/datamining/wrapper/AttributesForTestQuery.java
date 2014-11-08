package edu.buffalo.datamining.wrapper;

import java.io.Serializable;
import java.util.ArrayList;

public class AttributesForTestQuery implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Integer>goValues;
	private ArrayList<String>diseaseNames;
	private ArrayList<String>patientNames;
	
	public AttributesForTestQuery(ArrayList<Integer>goValues,ArrayList<String>diseaseNames,ArrayList<String>patientNames) {
		this.goValues=goValues;
		this.diseaseNames=diseaseNames;
		this.patientNames=patientNames;
	}

	public ArrayList<String> getDiseaseNames() {
		return diseaseNames;
	}

	public void setDiseaseNames(ArrayList<String> diseaseNames) {
		this.diseaseNames = diseaseNames;
	}

	public ArrayList<Integer> getGoValues() {
		return goValues;
	}

	public void setGoValues(ArrayList<Integer> goValues) {
		this.goValues = goValues;
	}

	public ArrayList<String> getPatientNames() {
		return patientNames;
	}

	public void setPatientNames(ArrayList<String> patientNames) {
		this.patientNames = patientNames;
	}

}
