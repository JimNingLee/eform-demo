package com.cabsoft.ess.form.signaturepad;

public class SPoint implements java.io.Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4535416561546384676L;
	double x;
    double y;

    public SPoint(){
        x = 0.0f;
        y = 0.0f;
    }
    
    public SPoint(double x, double y){
        this.x = x;
        this.y = y;
    }
    
    public void setLocation(double x, double y){
        this.x = x;
        this.y = y;
    }
    
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
    
    public String toString(){
        return String.valueOf(x) + "," + String.valueOf(y);
    }
}
