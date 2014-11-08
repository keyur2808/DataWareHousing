package edu.buffalo.datamining.utils;

public class TStatistic {

	private double x[];
	private double y[];
	private double avg1;
	private double avg2;
	private double var1;
	private double var2;
	private double n1;
	private double n2;
	private double n;

	public TStatistic(double x[], double y[]) {
		this.x = x;
		this.y = y;
		this.n1 = x.length;
		this.n2 = y.length;
	}

	private double average(double z[]) {
		double avg = 0;
		for (int i = 0; i < z.length; i++) {
			avg += z[i];

		}
		avg = avg / z.length;

		return avg;

	}

	public double variance(double z[], double avg) {
		double t = 0;
		double var = 0;
		for (int i = 0; i < z.length; i++) {

			t = Math.pow((avg - z[i]), 2);
			var += t;
		}
		var = var / z.length;
		return var;
	}

	public double tStatisticForUnEqualVariance() {

		avg1 = average(x);
		avg2 = average(y);
        var1 = variance(x,avg1);
        var2  = variance(y,avg2);
        n = avg1 - avg2;
		double d = (var1 * (x.length - 1) + var2 * (y.length - 1))
				/ (x.length + y.length - 2);
		d = Math.pow(d, 0.5);
		double ninv = (double) 1 / n1;
		double n2inv = (double) 1 / n2;
		double d2 = Math.pow(ninv + n2inv, .5);
		double d3 = d * d2;
		double ans = n / d3;

		return ans;
	}

	public double tStatisticForEqualVariance(){
		avg1 = average(x);
		avg2 = average(y);
        var1 = variance(x,avg1);
        var2  = variance(y,avg2);
        n = avg1 - avg2;
        double s1=var1/n1;
        double s2=var2/n2;
        double s3=s1+s2;
        double ans=n/Math.pow(s3, .5);
        return ans;
	}
	
	
	
	public double[] getY() {
		return y;
	}

	public void setY(double y[]) {
		this.y = y;
	}

	public double[] getX() {
		return x;
	}

	public void setX(double x[]) {
		this.x = x;
	}

}
