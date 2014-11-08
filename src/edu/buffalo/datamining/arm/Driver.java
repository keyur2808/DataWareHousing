package edu.buffalo.datamining.arm;

import java.util.Properties;

public class Driver {

	/**
	 * @param args
	 */
	public static void drive(String MIN_SUPPORT, String MIN_CONFIDENCE) {
		PreProcess.preprocess();
		Properties prop = new Properties();
		prop.setProperty("ITEMSEP", ",");
		prop.setProperty("TRAIN_DATA_FILE", "train_data.txt");
		prop.setProperty("MIN_SUPPORT", MIN_SUPPORT);
		prop.setProperty("MIN_CONFIDENCE", MIN_CONFIDENCE);
		ReadIn.readIn(prop);
		MineFrequentItemSets.mineFreqItems(prop);
		AssociationRuleMiner.arm(prop);
	}
	
	public static void main(String[] args) {
		PreProcess.preprocess();
		Properties prop = new Properties();
		prop.setProperty("ITEMSEP", ",");
		prop.setProperty("TRAIN_DATA_FILE", "train_data.txt");
		prop.setProperty("MIN_SUPPORT", "0.6");
		prop.setProperty("MIN_CONFIDENCE", "0.7");
		ReadIn.readIn(prop);
		MineFrequentItemSets.mineFreqItems(prop);
		AssociationRuleMiner.arm(prop);
	}

}
