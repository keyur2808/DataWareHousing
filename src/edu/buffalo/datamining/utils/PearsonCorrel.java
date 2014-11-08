package edu.buffalo.datamining.utils;

import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import com.google.common.math.DoubleMath;

public class PearsonCorrel {

	public PearsonCorrel() {
		// TODO Auto-generated constructor stub
	}
	
	public double getCorrelation(double[] x,double[] y){
	
		StandardDeviation std =new StandardDeviation();
		double xstd=std.evaluate(x);
		double ystd=std.evaluate(y);
		
		Covariance cor=new Covariance();
		double cov=cor.covariance(x, y);
		
		for (int i=0;i<x.length;i++){
			x[i]=x[i]-DoubleMath.mean(x);
			y[i]=y[i]-DoubleMath.mean(y);
		}


		double sum=0;
		double tmp=0;
		for (int i=0;i<x.length;i++){
			tmp=x[i]*y[i];
			sum=sum+tmp;		
		}
		
	
	
		double denom=xstd*ystd;
		double ans=cov/(denom);	
		
		
		return ans;
		}
	
	
	

}
