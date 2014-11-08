package edu.buffalo.datamining.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.buffalo.datamining.arm.Driver;
import edu.buffalo.datamining.queries.CorrelationTest;
import edu.buffalo.datamining.queries.FTest;
import edu.buffalo.datamining.queries.HealthClassifier;
import edu.buffalo.datamining.queries.TStatisticTest;
import edu.buffalo.datamining.queries.TestQuery1;
import edu.buffalo.datamining.queries.TestQuery2;
import edu.buffalo.datamining.queries.TestQuery3;
import edu.buffalo.datamining.services.DBOperations;
import edu.buffalo.datamining.wrapper.AttributesForTestQuery;
import edu.buffalo.datamining.wrapper.Diseases;
import edu.buffalo.datamining.wrapper.TestResult;


/**
 * Servlet implementation class RequestProcessingServlet
 */
@WebServlet("/RequestProcessingServlet")
public class RequestProcessingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ObjectMapper objectMapper = null;
	private PrintWriter writer = null;
	private DBOperations operations = new DBOperations();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RequestProcessingServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
//		try{
//			String action = request.getParameter("action");
//			@SuppressWarnings("unused")
//			Map<String, String[]> parameters = request.getParameterMap();
//			if (action!=null){
//			String resultString=null;
//			int goIdValue=0;
//			String goId=null;
//			String disease1=null;
//			String disease2=null;
//			String disease=request.getParameter("action");
//			}
//			
//			}
//		    catch (Exception e) {
//				e.printStackTrace();
//			}
		

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response)  {
		try{
		String action = request.getParameter("action");
		@SuppressWarnings("unused")
		Map<String, String[]> parameters = request.getParameterMap();
		if (action!=null){
		String resultString=null;
		int goIdValue=0;
		String goId=null;
		String disease1=null;
		String disease2=null;
		
		switch (action) {

		case "populate":
			ArrayList<String>types=operations.getDiseaseTypes();
			ArrayList<String>names=operations.getDiseaseNames();
			ArrayList<String>descriptions=operations.getDiseaseDescription();
			ArrayList<String>clusterIds=operations.getClusterIds();
			ArrayList<String>measurementUnitIds=operations.getMeasurementUnitIds();
			Diseases disease=new Diseases(names,types,descriptions,clusterIds,measurementUnitIds);
			objectMapper = new ObjectMapper();
			// response.setContentType("application/json");
			writer = response.getWriter();
			objectMapper.writeValue(writer, disease);
			
			writer.flush();
			writer.close();
				
			break;

		case "ttestLoadInfo":

			AttributesForTestQuery values = operations.getTestInfo();
			objectMapper = new ObjectMapper();
			writer = response.getWriter();
			objectMapper.writeValue(writer, values);
			writer.flush();
			writer.close();
			break;
			
		case "patientCount":
		    TestQuery1 patientCount=new TestQuery1(request);
		    resultString=patientCount.evaluate();
		    System.out.println(resultString);
			writer.write(resultString);
			writer.flush();
			writer.close();
			break;

		case  "drugTypes":
			disease1=request.getParameter("disease");
			TestQuery2 drugTypes=new TestQuery2(disease1);
		    List<String>results=drugTypes.evaluate();
		    System.out.println(results);
		    objectMapper = new ObjectMapper();
			writer = response.getWriter();
			objectMapper.writeValue(writer,results);
			writer.flush();
			writer.close();
			break;
			
		case  "expressionList":
			disease1=request.getParameter("disease");
			String clid=request.getParameter("clusterId");
			String muid=request.getParameter("measureId");
			
			if (muid!=null && clid!=null && muid!="" && clid!=""){
				int cl_id=Integer.valueOf(clid);
				int mu_id=Integer.valueOf(muid);
				TestQuery3 query=new TestQuery3(disease1,cl_id,mu_id);
				List<String>result=query.evaluate();
				    
		    System.out.println(result);
		    objectMapper = new ObjectMapper();
			writer = response.getWriter();
			objectMapper.writeValue(writer,result);
			writer.flush();
			writer.close();
			}
			
			break;	
			
	    case "ttest":
			System.out.println("TTest being performed");
			disease1 = request.getParameter("Disease1");
		    disease2 = request.getParameter("Disease2");
			goId=request.getParameter("GO");
			int goValue=0;
			if(goId!=null){
				 goValue = Integer.valueOf(goId);
			}
			TStatisticTest test = new TStatisticTest(disease1, disease2, goValue);
			List<String>result=test.evaluate();
			System.out.println(result.toString());
			objectMapper = new ObjectMapper();
			writer = response.getWriter();
			objectMapper.writeValue(writer,result);
			writer.flush();
			writer.close();
			
			
			break;

		case "ftest":
			System.out.println("FTest being performed");
			disease1 = request.getParameter("Disease1");
			disease2 = request.getParameter("Disease2");
			String disease3 = request.getParameter("Disease3");
			String disease4 = request.getParameter("Disease4");
			goId=request.getParameter("GO");
			goIdValue=0;
			if(goId!=null){
				 goIdValue = Integer.valueOf(goId);
			}
			FTest ftest = new FTest(disease1, disease2,disease3,disease4, goIdValue);
			resultString=ftest.evaluate();
			System.out.println(resultString);
			writer = response.getWriter();
			writer.write(resultString);
			writer.flush();
			writer.close();
		
			break;
		
		case "correlation":
			System.out.println("Average Correlation being calculated");
			disease1 = request.getParameter("Disease1");
			disease2 = request.getParameter("Disease2");
			goId=request.getParameter("GO");
			goIdValue=0;
			if(goId!=null){
				 goIdValue = Integer.valueOf(goId);
			}
			CorrelationTest ctest = new CorrelationTest(disease1, disease2, goIdValue);
			resultString=ctest.evaluateAverageCorrelation();
			System.out.println(resultString);
			writer = response.getWriter();
			writer.write(resultString);
			writer.flush();
			writer.close();
		
			break;
			
		case "affectedPatients":
			System.out.println("Finding No of Affected Patients");
			disease1=request.getParameter("Disease");
			String patient=request.getParameter("Patient");
			String a=request.getParameter("alpha");
			a="0.01";
			double alpha=Double.valueOf(a);
			HealthClassifier classifier =new HealthClassifier(disease1,alpha,patient);
			TestResult rslt=classifier.getResult();
			objectMapper = new ObjectMapper();
			writer = response.getWriter();
			objectMapper.writeValue(writer,rslt);
			writer.flush();
			writer.close();
			
			break;
		
		case "arm":
			String MIN_SUPPORT = request.getParameter("MIN_SUPPORT");
			String MIN_CONFIDENCE = request.getParameter("MIN_CONFIDENCE");
			Driver.drive(MIN_SUPPORT, MIN_CONFIDENCE);
			uploadFile(request, MIN_SUPPORT, MIN_CONFIDENCE);
		
		}
		;
		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	


	public void uploadFile(HttpServletRequest request, String MIN_SUPPORT, String MIN_CONFIDENCE) {

		File file = new File("associationRules_" + MIN_SUPPORT + "_" + MIN_CONFIDENCE + ".txt");
		
		Socket socket;
		FileInputStream fis = null;
		try {
			socket = new Socket(request.getRemoteHost(), request.getRemotePort());
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

			oos.writeObject(file.getName());

			fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			Integer bytesRead = 0;

			while ((bytesRead = fis.read(buffer)) > 0) {
				oos.writeObject(bytesRead);
				oos.writeObject(Arrays.copyOf(buffer, buffer.length));
			}

			oos.close();
			ois.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if(fis != null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
