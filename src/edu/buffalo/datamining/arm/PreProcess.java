package edu.buffalo.datamining.arm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.buffalo.datamining.queries.HealthClassifier;
import edu.buffalo.datamining.services.DBOperations;

public class PreProcess {

	/**
	 * @param args
	 */
	public static void preprocess() {
		HealthClassifier hc = new HealthClassifier("ALL", 0.01, null);
		ArrayList<Integer> genes = hc.getInformativeGenes();
		DBOperations operations = new DBOperations();
		ResultSet rs = operations.getExpressionValuesForRandomPatients(genes);
		File outFile = new File("trainData/train_data.txt");
		FileWriter fw = null;
		BufferedWriter bw = null;
		int patId = -1;
		int count = 1;
		try {
			fw = new FileWriter(outFile.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			while (rs.next()) {

				int value = rs.getInt("EXP");
				int newPatId = rs.getInt("P_ID");

				if (newPatId != patId) {
					patId = newPatId;
					if (count != 1)
						bw.newLine();
					bw.write("Sample" + count + "\t");
					count++;
				}

				if (value < 100) {
					bw.write("DOWN");
				} else {
					bw.write("UP");
				}
				bw.write("\t");
			}
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
