package edu.buffalo.datamining.wrapper;

import java.io.Serializable;
import java.util.ArrayList;

public class Diseases implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<String> nameValues = null;
	private ArrayList<String> typeValues = null;
	private ArrayList<String> descriptionValues = null;
	private ArrayList<String> clusterIds = null;
	private ArrayList<String> measureIds = null;

	public Diseases(ArrayList<String> nameValues, ArrayList<String> typeValues,
			ArrayList<String> descriptionValues, ArrayList<String> clusterId,
			ArrayList<String> mesaureIds) {
		this.nameValues = nameValues;
		this.typeValues = typeValues;
		this.descriptionValues = descriptionValues;
		this.setClusterIds(clusterId);
		this.setMeasureIds(mesaureIds);
	}

	public ArrayList<String> getNameValues() {
		return nameValues;
	}

	public void setNameValues(ArrayList<String> nameValues) {
		this.nameValues = nameValues;
	}

	public ArrayList<String> getTypeValues() {
		return typeValues;
	}

	public void setTypeValues(ArrayList<String> typeValues) {
		this.typeValues = typeValues;
	}

	public ArrayList<String> getDescriptionValues() {
		return descriptionValues;
	}

	public void setDescriptionValues(ArrayList<String> descriptionValues) {
		this.descriptionValues = descriptionValues;
	}

	public ArrayList<String> getClusterIds() {
		return clusterIds;
	}

	public void setClusterIds(ArrayList<String> clusterIds) {
		this.clusterIds = clusterIds;
	}

	public ArrayList<String> getMeasureIds() {
		return measureIds;
	}

	public void setMeasureIds(ArrayList<String> measureIds) {
		this.measureIds = measureIds;
	}

}
