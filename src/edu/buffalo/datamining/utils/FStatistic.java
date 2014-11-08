package edu.buffalo.datamining.utils;

import java.util.Collection;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.util.MathUtils;



public class FStatistic {

	public FStatistic() {
		// TODO Auto-generated constructor stub
	}

	public double oneWayAnova(Collection<double[]> sampleCollection)
	throws NullArgumentException, DimensionMismatchException {
		double fValue=0;
		MathUtils.checkNotNull(sampleCollection);
		boolean dataSize = false;
		if (!dataSize) {
			
			if (sampleCollection.size() < 2) {
				throw new DimensionMismatchException(
						LocalizedFormats.TWO_OR_MORE_CATEGORIES_REQUIRED,
						sampleCollection.size(), 2);
			}

			long sz=sampleCollection.size();
			double []means = new double[sampleCollection.size()];
			int []size  = new int[sampleCollection.size()];
			
			SummaryStatistics statistics=new SummaryStatistics();
						
			int i=0;
			for (double [] tmp: sampleCollection){
				means[i]=StatUtils.mean(tmp);
				size[i]=tmp.length;
				i++;
				for (double temp:tmp){
					statistics.addValue(temp);
				}
			}
			
			long n=statistics.getN();
			double mean=statistics.getMean();
			long k=sz;
			long freedomError=n-sz;
			k=k-1;
			
			
			double[] ssbtw=new double[sampleCollection.size()];
			i=0;
		
			for (int j=0;j<sampleCollection.size();j++){
				ssbtw[j]=Math.pow((means[j]-mean),2)*size[j];
			}
			double ssdisease=StatUtils.sum(ssbtw);
			
			int t=0;
			double sum=0;
			for (double [] tmp: sampleCollection){
				
				for (int l=0;l<tmp.length;l++){
					double diff=tmp[l] - means[t];
					diff=diff*diff;
					sum+=diff;
				}
				t++;
			}		
			
			double msDisease=ssdisease/k;
			double msError=sum/freedomError;
			fValue=msDisease/msError;
			
			System.out.println(fValue);
			
			}
	
		return fValue;
	}
	
}
