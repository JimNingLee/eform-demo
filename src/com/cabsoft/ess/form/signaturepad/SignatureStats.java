package com.cabsoft.ess.form.signaturepad;

import java.util.Stack;

public class SignatureStats {
	private float length = 0;
        private float sum = 0;
	private float mean = 0;
	private float variance = 0;
	private float deviation = 0;
	
	public float getLength() {
		return length;
	}
	
	public void setLength(float length) {
		this.length = length;
	}

	public float getMean() {
		return mean;
	}

	public void setMean(float mean) {
		this.mean = mean;
	}

	public float getVariance() {
		return variance;
	}

	public void setVariance(float variance) {
		this.variance = variance;
	}

	public float getDeviation() {
		return deviation;
	}

	public void setDeviation(float deviation) {
		this.deviation = deviation;
	}

    public float getSum() {
        return sum;
    }

	public void stats(Stack<Float> a){
		int t = a.size();
		float s = 0f;
		for(int i=0; i<t; i++) {
                    s += a.get(i);
                }
                
                sum = s;
		
		mean = s/t;
		
		s = 0f;
		for(int i=0; i<t; i++) s +=  Math.pow(a.get(i)-mean, 2);
		
		variance = s/t;
		deviation = (float)Math.sqrt(variance);
	}
}
