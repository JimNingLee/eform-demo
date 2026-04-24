package com.cabsoft.ess.form.signaturepad;

public class SignatureLine implements java.io.Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8806813471581761125L;
	private float lx, ly, mx, my;

    public SignatureLine() {
    }

    public float getLx() {
        return lx;
    }

    public void setLx(float lx) {
        this.lx = lx;
    }

    public float getLy() {
        return ly;
    }

    public void setLy(float ly) {
        this.ly = ly;
    }

    public float getMx() {
        return mx;
    }

    public void setMx(float mx) {
        this.mx = mx;
    }

    public float getMy() {
        return my;
    }

    public void setMy(float my) {
        this.my = my;
    }
    
    public String toString(){
        StringBuilder sb = new StringBuilder();
        
        sb.append("{\"lx\":").append(String.valueOf(lx)).append(",");
        sb.append("\"ly\":").append(String.valueOf(ly)).append(",");
        sb.append("\"mx\":").append(String.valueOf(mx)).append(",");
        sb.append("\"my\":").append(String.valueOf(my)).append("}");
        
        return sb.toString();
    }
}
