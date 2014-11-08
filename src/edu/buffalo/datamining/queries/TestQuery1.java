/**
 * 
 */
package edu.buffalo.datamining.queries;

import javax.servlet.http.HttpServletRequest;

import edu.buffalo.datamining.services.DBOperations;

/**
 * @author Keyur
 *
 */
public class TestQuery1 {

	/**
	 * 
	 */
	
	private HttpServletRequest request=null;
	
	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public TestQuery1(HttpServletRequest request) {
		this.request=request;
	}
	
	public static void main(String args[]){
 		TestQuery1 query=new TestQuery1(null);
 		query.evaluate();
	}
	
	public String evaluate(){
	
	Integer count = 0;
	DBOperations operations=new DBOperations();
	try{
	if (request.getParameter("diseaseDescription") != null) {
	
		String description = request.getParameter("diseaseDescription");
		count = operations.patientCountByDiseaseDescription(description);
	}
	
	if (request.getParameter("diseaseName") != null) {
		String name = request.getParameter("diseaseName");
		System.out.println(name);
		count = operations.patientCountByDiseaseName(name);
		
		
	}
	
	if (request.getParameter("diseaseType") != null) {
		String type = request.getParameter("diseaseType");
		count = operations.patientCountByDiseaseType(type);
	}
	}catch(Exception e){
		e.printStackTrace();
	}	
    String resultString=count.toString();
	return resultString;
    }

}
