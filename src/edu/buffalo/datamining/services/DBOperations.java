package edu.buffalo.datamining.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

import edu.buffalo.datamining.wrapper.AttributesForTestQuery;
import edu.buffalo.datamining.wrapper.TableColumnNamesPair;

public class DBOperations {

	//@Resource(name = "jdbc/oracledb")
	private static DataSource dataSource;
	
	public static DataSource getDataSource(){
		InitialContext initCtx;
		try {
			if (dataSource==null){
				initCtx = new InitialContext();
				 Context envCtx = (Context) initCtx.lookup("java:comp/env"); 
				 dataSource= (DataSource)envCtx.lookup("jdbc/oracledb");
			}
			
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataSource;
	}
	
	public  Connection getConnection() {
		 Connection connection=null; 
		try {
			 
			 connection=dataSource.getConnection();
			 System.out.println("Connection Established successfully");
			

		} catch (SQLException e) {
			System.out
					.println("Connection Failed Please verify your connectivity to host");
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connection;
	}
	
	private void closeConnection(Connection con,ResultSet rs,PreparedStatement ps){
		try {
			rs.close();
			ps.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public ArrayList<TableColumnNamesPair> getAllColumnNames() {
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		ArrayList<TableColumnNamesPair> names = new ArrayList<>();
		String getAllColumnNames = "Select TABLE_NAME,COLUMN_NAME from USER_TAB_COLUMNS";
		try {
			conn = this.getConnection();
			if (conn != null) {
				ps = conn.prepareStatement(getAllColumnNames);
				rs = ps.executeQuery();
				while (rs.next()) {
					String tableName = rs.getString("TABLE_NAME");
					String columnName = rs.getString("COLUMN_NAME");
					TableColumnNamesPair pair = new TableColumnNamesPair(
							tableName, columnName);
					names.add(pair);

				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return names;
	}

	@SuppressWarnings("resource")
	public AttributesForTestQuery getTestInfo() {
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		ArrayList<String> names = new ArrayList<>();
		ArrayList<Integer> goValues = new ArrayList<>();
		ArrayList<String> testNames = new ArrayList<>();
		AttributesForTestQuery info = null;
		String getDiseaseNames = "Select NAME from Disease";
		String getGoValues = "Select GO_ID from GO UNION Select GO_ID from gene_fact";
		String getTestPatients = "SELECT column_name FROM USER_TAB_COLUMNS WHERE table_name = 'TEST_SAMPLES' AND column_name <> 'S_ID'";
		try {
			conn = this.getConnection();
			if (conn != null) {
				 ps = conn.prepareStatement(getDiseaseNames);
				 rs = ps.executeQuery();
				while (rs.next()) {
					String diseaseName = rs.getString("NAME");
					names.add(diseaseName);

				}

				ps = conn.prepareStatement(getGoValues);
				rs = ps.executeQuery();
				while (rs.next()) {
					int goValue = rs.getInt("GO_ID");
					goValues.add(goValue);
				}

				ps = conn.prepareStatement(getTestPatients);
				rs=ps.executeQuery();
				while (rs.next()) {
					String patientName = rs.getString("COLUMN_NAME");
					testNames.add(patientName);
				}

				info = new AttributesForTestQuery(goValues, names,testNames);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			closeConnection(conn, rs,ps);
		}

		return info;
	}

	public double[] StatisticsQueryForTAndFTest(String diseaseName, int goId) {
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		double disease[] = null;
		try {
			conn = this.getConnection();
			if (conn != null) {

				String sqlQueryDiseaseExpressionValues = "select exp from microarray_fact where s_id in (select s_id from clinicalsample,diagnosis where clinicalsample.s_id IS NOT null AND clinicalsample.p_id=diagnosis.p_id AND diagnosis.ds_id in (Select ds_id from DISEASE where name "
						+ diseaseName
						+ "'))AND pb_id in (select pb_id from goannotation,probe where goannotation.go_id="
						+ goId + " AND goannotation.uid1=probe.uid1)";
				System.out.println(sqlQueryDiseaseExpressionValues);

				 ps = conn
						.prepareStatement(sqlQueryDiseaseExpressionValues);
				long t1 = System.currentTimeMillis();
				rs = ps.executeQuery();
				rs.setFetchSize(1000);
				long t2 = System.currentTimeMillis();
				System.out.println("Execution time(ms)   " + (t2 - t1));

				ArrayList<Double> diseaseExpressions = new ArrayList<>();
				while (rs.next()) {
					double value = rs.getDouble("EXP");
					diseaseExpressions.add(value);
				}

				// Second Disease
				disease = Doubles.toArray(diseaseExpressions);

			}
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			closeConnection(conn, rs, ps);
		}
		return disease;
	}

	public Map<Integer, ArrayList<Integer>> getExpressionValuesPerPatient(
			String diseaseName, int goIdValue) {
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		Map<Integer, ArrayList<Integer>> expressionValuesPerPatient = new TreeMap<>();
		String sqlQueryALL = "SELECT diagnosis.P_ID,probe.UID1,microarray_fact.EXP FROM disease,diagnosis, clinicalsample, microarray_fact, goannotation, probe WHERE diagnosis.DS_ID =disease.DS_ID AND diagnosis.P_ID = clinicalsample.P_ID AND microarray_fact.PB_ID = probe.PB_ID AND clinicalsample.S_ID = microarray_fact.S_ID AND goannotation.UID1 = probe.UID1 AND disease.NAME='"
				+ diseaseName
				+ "' AND goannotation.GO_ID = "
				+ goIdValue
				+ " ORDER BY diagnosis.P_ID,probe.UID1";
		try {
			conn = this.getConnection();
			if (conn != null) {
				 ps = conn.prepareStatement(sqlQueryALL);
				long t1 = System.currentTimeMillis();
				 rs = ps.executeQuery();
				long t2 = System.currentTimeMillis();
				System.out.println("Execution time(ms)   " + (t2 - t1));
				rs.setFetchSize(100);
				int value = -1;
				int person = -1;

				while (rs.next()) {

					value = rs.getInt("EXP");
					person = rs.getInt("P_ID");

					if (!expressionValuesPerPatient.containsKey(person)) {
						ArrayList<Integer> tmp = new ArrayList<>();
						tmp.add(value);
						expressionValuesPerPatient.put(person, tmp);
					} else {
						ArrayList<Integer> tmp = expressionValuesPerPatient
								.get(person);
						tmp.add(value);
					}
				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			closeConnection(conn, rs, ps);
		}
		return expressionValuesPerPatient;
	}

	public int patientCountByDiseaseDescription(String diseaseDescription) {
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		int count = 0;
		String sqlQueryPatientsByDescription = "select count(p_id) AS count from diagnosis,disease where diagnosis.ds_id=disease.ds_id AND disease.DESCRIPTION='"
				+ diseaseDescription + "'";
		try {
			conn = this.getConnection();
			if (conn != null) {
				 ps = conn
						.prepareStatement(sqlQueryPatientsByDescription);
				long t1 = System.currentTimeMillis();
				rs = ps.executeQuery();
				long t2 = System.currentTimeMillis();
				System.out.println("Execution time(ms)   " + (t2 - t1));

				while (rs.next()) {
					count = rs.getInt("COUNT");

				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			closeConnection(conn, rs, ps);
		}
		return count;
	}

	public int patientCountByDiseaseType(String diseaseType) {
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		int count = 0;
		String sqlQueryPatientsByDescription = "select count(p_id) AS count from diagnosis,disease where diagnosis.ds_id=disease.ds_id AND disease.TYPE='"
				+ diseaseType + "'";
		try {
			conn = this.getConnection();
			if (conn != null) {
				 ps = conn
						.prepareStatement(sqlQueryPatientsByDescription);
				long t1 = System.currentTimeMillis();
				rs = ps.executeQuery();
				long t2 = System.currentTimeMillis();
				System.out.println("Execution time(ms)   " + (t2 - t1));

				while (rs.next()) {
					count = rs.getInt("COUNT");

				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			closeConnection(conn, rs, ps);
		}
		return count;
	}

	public int patientCountByDiseaseName(String diseaseName) {
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		int count = 0;
		String sqlQueryPatientsByDescription = "select count(p_id) AS count from diagnosis,disease where diagnosis.ds_id=disease.ds_id AND disease.NAME='"
				+ diseaseName + "'";
		try {
			conn = this.getConnection();
			if (conn != null) {
				 ps = conn
						.prepareStatement(sqlQueryPatientsByDescription);
				long t1 = System.currentTimeMillis();
				rs = ps.executeQuery();
				long t2 = System.currentTimeMillis();
				System.out.println("Execution time(ms)   " + (t2 - t1));

				while (rs.next()) {
					count = rs.getInt("COUNT");

				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}

	public Map<Integer, ArrayList<Integer>> getDiseaseGroupValues(
			String diseaseName) {
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		Map<Integer, ArrayList<Integer>> expressionValues = new TreeMap<>();
		String query = "SELECT microarray_fact.EXP,probe.UID1 from diagnosis,disease, clinicalsample, microarray_fact, probe WHERE disease.NAME='"
				+ diseaseName
				+ "' AND diagnosis.DS_ID = disease.DS_ID AND diagnosis.P_ID = clinicalsample.P_ID AND microarray_fact.PB_ID = probe.PB_ID AND clinicalsample.S_ID = microarray_fact.S_ID  ORDER BY probe.UID1";
		try {
			conn = this.getConnection();
			if (conn != null) {
				 ps = conn.prepareStatement(query);
				long t1 = System.currentTimeMillis();
				rs = ps.executeQuery();
				rs.setFetchSize(1000);
				long t2 = System.currentTimeMillis();
				System.out.println("Execution time(ms)   " + (t2 - t1));

				int value = -1;
				int geneId = -1;
				while (rs.next()) {
					value = rs.getInt("EXP");
					geneId = rs.getInt("UID1");
					if (!expressionValues.containsKey(geneId)) {
						ArrayList<Integer> tmp = new ArrayList<>();
						tmp.add(value);
						expressionValues.put(geneId, tmp);
					} else {
						ArrayList<Integer> tmp = expressionValues.get(geneId);
						tmp.add(value);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}finally{
			closeConnection(conn, rs, ps);
		}

		return expressionValues;
	}

	public ArrayList<Integer> getTestExpressionValues(String patientName,
			ArrayList<Integer> genes) {
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		ArrayList<Integer> values = new ArrayList<>();
		int[] geneList = Ints.toArray(genes);
		String inClause = StringUtils.join(geneList, ',');
		if (inClause == null || inClause.equalsIgnoreCase("")){
			return null;
		}
		String query = "Select " + patientName
				+ " from test_samples where s_id in (" + inClause
				+ ") ORDER BY s_id";
		try {
			conn = this.getConnection();
			if (conn != null) {
				 ps = conn.prepareStatement(query);
				rs = ps.executeQuery();

				while (rs.next()) {

					int value = rs.getInt(patientName);
					values.add(value);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}finally{
			closeConnection(conn, rs, ps);
		}
		return values;
	}

	public Map<Integer, ArrayList<Integer>> getDiseaseGroupValues(
			String diseaseName, ArrayList<Integer> genes) {
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		Map<Integer, ArrayList<Integer>> expressionValues = new TreeMap<>();
		String query=null;
		int[] geneList = Ints.toArray(genes);
		String inClause = StringUtils.join(geneList, ',');
		System.out.println(inClause);
		if (inClause==null || inClause.equalsIgnoreCase("")){
			inClause="";
		}else{
			
		     query = "SELECT distinct microarray_fact.EXP,diagnosis.P_ID,probe.UID1 from diagnosis,disease, clinicalsample, microarray_fact, probe WHERE disease.NAME='"
				+ diseaseName
				+ "' AND diagnosis.DS_ID = disease.DS_ID AND diagnosis.P_ID = clinicalsample.P_ID AND microarray_fact.PB_ID = probe.PB_ID AND clinicalsample.S_ID = microarray_fact.S_ID AND probe.UID1 in ("+inClause+") ORDER BY diagnosis.p_id,probe.UID1";
		}
		try {
			conn = this.getConnection();
			if (conn != null) {
				 ps = conn.prepareStatement(query);
				long t1 = System.currentTimeMillis();
				rs = ps.executeQuery();
				rs.setFetchSize(1000);
				long t2 = System.currentTimeMillis();
				System.out.println("Execution time(ms)   " + (t2 - t1));

				while (rs.next()) {

					int value = rs.getInt("EXP");
					int geneId = rs.getInt("P_ID");

					if (!expressionValues.containsKey(geneId)) {
						ArrayList<Integer> tmp = new ArrayList<>();
						tmp.add(value);
						expressionValues.put(geneId, tmp);
					} else {
						ArrayList<Integer> tmp = expressionValues.get(geneId);
						tmp.add(value);
					}
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			closeConnection(conn, rs, ps);
		}

		return expressionValues;
	}

	public Map<Integer, ArrayList<Integer>> getControlGroupValues(
			String diseaseName, ArrayList<Integer> genes) {
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		Map<Integer, ArrayList<Integer>> expressionValues = new TreeMap<>();
		String query=null;
		int[] geneList = Ints.toArray(genes);
		String inClause = StringUtils.join(geneList, ',');
		if (inClause==null){
			inClause="";
			return null;
		}
		else{
			query = "SELECT distinct microarray_fact.EXP,diagnosis.P_ID,probe.UID1 from diagnosis,disease, clinicalsample, microarray_fact, probe WHERE disease.NAME <> '"
		
				+ diseaseName
				+ "' AND diagnosis.DS_ID = disease.DS_ID AND diagnosis.P_ID = clinicalsample.P_ID AND microarray_fact.PB_ID = probe.PB_ID AND clinicalsample.S_ID = microarray_fact.S_ID AND probe.UID1 in("
				+ inClause + ") ORDER BY diagnosis.p_id,probe.UID1";
		}
		try {
			conn = this.getConnection();
			if (conn != null) {
				 ps = conn.prepareStatement(query);
				long t1 = System.currentTimeMillis();
				rs = ps.executeQuery();
				rs.setFetchSize(1000);
				long t2 = System.currentTimeMillis();
				System.out.println("Execution time(ms)   " + (t2 - t1));

				while (rs.next()) {

					int value = rs.getInt("EXP");
					int geneId = rs.getInt("P_ID");

					if (!expressionValues.containsKey(geneId)) {
						ArrayList<Integer> tmp = new ArrayList<>();
						tmp.add(value);
						expressionValues.put(geneId, tmp);
					} else {
						ArrayList<Integer> tmp = expressionValues.get(geneId);
						tmp.add(value);
					}
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}finally{
			closeConnection(conn, rs, ps);
		}

		return expressionValues;
	}

	public Map<Integer, ArrayList<Integer>> getSampleExpressionValues(
			String diseaseName) {
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		Map<Integer, ArrayList<Integer>> expressionValues = new TreeMap<>();
		String query = "SELECT test_samples.S_ID,test_samples.Test1 from test_samples";
		try {
			conn = this.getConnection();
			if (conn != null) {
				 ps = conn.prepareStatement(query);
				rs = ps.executeQuery();
				
				while (rs.next()) {

					int geneId = rs.getInt("S_ID");
					int value = rs.getInt("Test1");

					if (!expressionValues.containsKey(geneId)) {
						ArrayList<Integer> tmp = new ArrayList<>();
						tmp.add(value);
						expressionValues.put(geneId, tmp);
					} else {
						ArrayList<Integer> tmp = expressionValues.get(geneId);
						tmp.add(value);
					}
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}finally{
			closeConnection(conn, rs, ps);
		}

		return expressionValues;
	}

	public Map<Integer, ArrayList<Integer>> getControlGroupValues(
			String diseaseName) {
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		Map<Integer, ArrayList<Integer>> expressionValues = new TreeMap<>();
		String query = "SELECT microarray_fact.EXP,probe.UID1 from diagnosis, clinicalsample, microarray_fact, probe,disease WHERE disease.NAME<>'"
				+ diseaseName
				+ "' AND diagnosis.DS_ID = disease.DS_ID AND diagnosis.P_ID = clinicalsample.P_ID AND microarray_fact.PB_ID = probe.PB_ID AND clinicalsample.S_ID = microarray_fact.S_ID  ORDER BY probe.UID1";
		try {
			conn = this.getConnection();
			if (conn != null) {
				 ps = conn.prepareStatement(query);
				long t1 = System.currentTimeMillis();
				rs = ps.executeQuery();
				rs.setFetchSize(1000);
				long t2 = System.currentTimeMillis();
				System.out.println("Execution time(ms)   " + (t2 - t1));

				while (rs.next()) {

					int value = rs.getInt("EXP");
					int geneId = rs.getInt("UID1");

					if (!expressionValues.containsKey(geneId)) {
						ArrayList<Integer> tmp = new ArrayList<>();
						tmp.add(value);
						expressionValues.put(geneId, tmp);
					} else {
						ArrayList<Integer> tmp = expressionValues.get(geneId);
						tmp.add(value);
					}
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			closeConnection(conn, rs, ps);
		}
		return expressionValues;
	}

	public ArrayList<String> getExpressionValuesByCluster(String diseaseName,
			int cl_id, int mu_id) {
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		ArrayList<String> expValues = new ArrayList<>();
		String tmp = null;
		String query = "select exp from disease,diagnosis,clinicalsample,microarray_fact,probe,clmeasure where disease.Name = '"
				+ diseaseName
				+ "' AND disease.ds_id=diagnosis.ds_id AND diagnosis.p_id=clinicalsample.p_id AND clinicalsample.s_id=microarray_fact.s_id AND microarray_fact.pb_id=probe.pb_id AND probe.uid1=clmeasure.uid1 AND clmeasure.cl_id= "
				+ cl_id + " AND microarray_fact.mu_id= " + mu_id;
		try {
			conn = this.getConnection();
			if (conn != null) {
				 ps = conn.prepareStatement(query);
				long t1 = System.currentTimeMillis();
				rs = ps.executeQuery();
				rs.setFetchSize(100);
				long t2 = System.currentTimeMillis();
				System.out.println("Execution time(ms)   " + (t2 - t1));
				while (rs.next()) {
					tmp = rs.getString("EXP");
					expValues.add(tmp);

				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			closeConnection(conn, rs, ps);
		}

		return expValues;

	}

	public ArrayList<String> getDiseaseTypes() {
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		ArrayList<String> types = new ArrayList<>();
		String tmp = null;
		String diseaseType = "select distinct(disease.TYPE) from disease";
		try {
			conn = this.getConnection();
			if (conn != null) {
				 ps = conn.prepareStatement(diseaseType);
				 rs = ps.executeQuery();
				while (rs.next()) {
					tmp = rs.getString("TYPE");
					types.add(tmp);

				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return types;

	}

	public ArrayList<String> getDrugTypes(String diseaseDescription) {
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		ArrayList<String> drugTypes = new ArrayList<>();
		String tmp = null;
		String drugType = "select distinct(drug.TYPE) from drug,drugusage,diagnosis,disease where disease.DS_ID=diagnosis.DS_ID AND diagnosis.P_ID=drugusage.P_ID AND drugusage.DR_ID=drug.DR_ID AND disease.Description='"
				+ diseaseDescription + "'";
		try {
			conn = this.getConnection();
			if (conn != null) {
				 ps = conn.prepareStatement(drugType);
				long t1 = System.currentTimeMillis();
				rs = ps.executeQuery();
				rs.setFetchSize(100);
				long t2 = System.currentTimeMillis();
				System.out.println("Execution time(ms)   " + (t2 - t1));
				while (rs.next()) {
					tmp = rs.getString("TYPE");
					drugTypes.add(tmp);

				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			closeConnection(conn, rs, ps);
		}
		System.out.println("No of drugTypes " + drugTypes.size());
		return drugTypes;
	}

	public ArrayList<String> getDiseaseNames() {
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		ArrayList<String> names = new ArrayList<>();
		String tmp = null;
		String diseaseName = "select disease.NAME from disease";
		try {
			conn = this.getConnection();
			if (conn != null) {
				 ps = conn.prepareStatement(diseaseName);
				 rs = ps.executeQuery();
				while (rs.next()) {
					tmp = rs.getString("NAME");
					names.add(tmp);

				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
closeConnection(conn, rs, ps);
		}

		return names;

	}

	public ArrayList<String> getDiseaseDescription() {
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		ArrayList<String> desc = new ArrayList<>();
		String tmp = null;
		String diseaseDescription = "select distinct(disease.DESCRIPTION) from disease";
		try {
			conn = this.getConnection();
			if (conn != null) {
				 ps = conn
						.prepareStatement(diseaseDescription);
			     rs = ps.executeQuery();
				while (rs.next()) {
					tmp = rs.getString("DESCRIPTION");
					desc.add(tmp);

				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			closeConnection(conn, rs, ps);
		}

		return desc;

	}

	public ArrayList<String> getClusterIds() {
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		ArrayList<String> desc = new ArrayList<>();
		String tmp = null;
		String clusterId = "select distinct(clusterg.cl_id) from clusterg order by clusterg.cl_id";
		try {
			conn = this.getConnection();
			if (conn != null) {
				 ps = conn.prepareStatement(clusterId);
				 rs = ps.executeQuery();
				while (rs.next()) {
					tmp = rs.getString("CL_ID");
					desc.add(tmp);

				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			closeConnection(conn, rs, ps);
		}

		return desc;

	}

	public ArrayList<String> getMeasurementUnitIds() {
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		ArrayList<String> desc = new ArrayList<>();
		String tmp = null;
		String measurementUnitId = "select distinct(microarray_fact.mu_id) from microarray_fact";
		try {
			conn = this.getConnection();
			if (conn != null) {
				 ps = conn.prepareStatement(measurementUnitId);
				 rs = ps.executeQuery();
				while (rs.next()) {
					tmp = rs.getString("MU_ID");
					desc.add(tmp);

				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			closeConnection(conn, rs, ps);
		}

		return desc;
	}
	
	
	public ResultSet getExpressionValuesForRandomPatients(ArrayList<Integer> genes) {
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		String query = null;
		int[] geneList = Ints.toArray(genes);
		String inClause = StringUtils.join(geneList, ',');
		System.out.println(inClause);
		if (inClause == null || inClause.equalsIgnoreCase("")) {
			inClause = "";
		} else {

			query = "SELECT distinct microarray_fact.EXP, diagnosis.P_ID, probe.UID1 from diagnosis,disease, clinicalsample, microarray_fact, probe WHERE diagnosis.DS_ID = disease.DS_ID AND diagnosis.P_ID = clinicalsample.P_ID AND microarray_fact.PB_ID = probe.PB_ID AND clinicalsample.S_ID = microarray_fact.S_ID AND probe.UID1 in ("
					+ inClause + ") AND diagnosis.P_ID in (SELECT P_ID FROM (SELECT P_ID, SYS.DBMS_RANDOM.RANDOM FROM PATIENT ORDER BY 2)  WHERE rownum <= 100 ) ORDER BY diagnosis.p_id, probe.UID1";
		}
		try {
			conn = this.getConnection();
			if (conn != null) {
				 ps = conn.prepareStatement(query);
				long t1 = System.currentTimeMillis();
				 rs = ps.executeQuery();
				rs.setFetchSize(1000);
				long t2 = System.currentTimeMillis();
				System.out.println("Execution time(ms)   " + (t2 - t1));
				return rs;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeConnection(conn, rs, ps);
		}
		return null;
	}

}