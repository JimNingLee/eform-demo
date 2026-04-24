package com.cabsoft.ess.form.signaturepad;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (c) 2005 David Benson
 *  
 * See LICENSE file in distribution for licensing details of this source file
 */
/**
 * Interpolates given points by a bezier curve. The first and the last two
 * points are interpolated by a quadratic bezier curve; the other points by a
 * cubic bezier curve.
 *
 * Let p a list of given points and b the calculated bezier points, then one get
 * the whole curve by:
 *
 * sharedPath.moveTo(p[0]) sharedPath.quadTo(b[0].x, b[0].getY(), p[1].x,
 * p[1].getY());
 *
 * for(int i = 2; i < p.length - 1; i++ ) { Point b0 = b[2*i-3]; Point b1 =
 * b[2*i-2]; sharedPath.curveTo(b0.x, b0.getY(), b1.x, b1.getY(), p[i].x,
 * p[i].getY()); }
 *
 * sharedPath.quadTo(b[b.length-1].x, b[b.length-1].getY(), p[n - 1].x, p[n -
 * 1].getY());
 *
 * @author krueger
 */
public class Bezier {

    public static SPoint[] getMap(SignatureLine[] list){
        SPoint[] pts = new SPoint[list.length];
        for(int i=0; i<list.length; i++){
            pts[i] = new SPoint(list[i].getLx(), list[i].getLy());
        }
        return pts;
    }
    
    public static List<List<SPoint>> getBezierControlPoints(SPoint[] sampledPoints) throws Exception {
        List<List<SPoint>> BS2Beziers = new ArrayList<List<SPoint>>();

        if (sampledPoints.length < 4) {
            List<SPoint> bezierSegmentControlPoints = new ArrayList<SPoint>();
            // We need 4 sampled points to draw a *cubic* bezier through those points
            // These 3 cases are for shorter lengths: single point, line, quadratic
            if (sampledPoints.length == 3) {
                bezierSegmentControlPoints.add(sampledPoints[0]);
                bezierSegmentControlPoints.add(sampledPoints[1]);
                bezierSegmentControlPoints.add(sampledPoints[1]);
                bezierSegmentControlPoints.add(sampledPoints[2]);
                BS2Beziers.add(bezierSegmentControlPoints);
                return BS2Beziers;
            } else if (sampledPoints.length == 2) {
                bezierSegmentControlPoints.add(sampledPoints[0]);
                bezierSegmentControlPoints.add(sampledPoints[0]);
                bezierSegmentControlPoints.add(sampledPoints[1]);
                bezierSegmentControlPoints.add(sampledPoints[1]);
                BS2Beziers.add(bezierSegmentControlPoints);
                return BS2Beziers;
            } else if (sampledPoints.length == 1) {
                bezierSegmentControlPoints.add(sampledPoints[0]);
                bezierSegmentControlPoints.add(sampledPoints[0]);
                bezierSegmentControlPoints.add(sampledPoints[0]);
                bezierSegmentControlPoints.add(sampledPoints[0]);
                BS2Beziers.add(bezierSegmentControlPoints);
                return BS2Beziers;
            };
        }else{
            int N = sampledPoints.length - 2;
            double[][] M = generate141Matrix(N);
            
            double[][] C = generateConstantMatrix(sampledPoints);

            Matrix matrix = new Matrix(M);
            Matrix inv = matrix.inverse();
            
            Matrix B = Matrix.dot(inv, new Matrix(C));
            
            Matrix BSplinePoints = Matrix.BSplineCompisite(B, sampledPoints[0], sampledPoints[sampledPoints.length-1]);
            
            SPoint[] pts = Matrix.convertPoint(BSplinePoints);

            SPoint p0 = new SPoint();
            SPoint p1 = new SPoint();
            SPoint p2 = new SPoint();
            SPoint p3 = new SPoint();

            for(int i=0; i<pts.length-1; i++){
                if(i==0){
                    p0 = pts[0];
                }else{
                    p0 = p3;
                }
                SPoint m10 = mul(pts[i], 2f/3f);
                SPoint m20 = mul(pts[i+1], 1f/3f);
                p1 = add(m10, m20);
                
                SPoint m11 = mul(pts[i], 1f/3f);
                SPoint m21 = mul(pts[i+1], 2f/3f);
                p2 = add(m11, m21);
                
                if(i==pts.length-2){
                    p3 = pts[pts.length-1];
                }else{
                    SPoint m13 = mul(pts[i], 1f/6f);
                    SPoint m23 = mul(pts[i+1], 2f/3f);
                    SPoint m33 = mul(pts[i+2], 1f/6f);
                    p3 = add(m13, m23, m33);
                }
                List<SPoint> bezierSegmentControlPoints = new ArrayList<SPoint>();
                bezierSegmentControlPoints.add(p0);
                bezierSegmentControlPoints.add(p1);
                bezierSegmentControlPoints.add(p2);
                bezierSegmentControlPoints.add(p3);
                BS2Beziers.add(bezierSegmentControlPoints);
            }
        }

        return BS2Beziers;
    }

    private static double[][] generate141Matrix(int N){
        double[][] result = new double[N][N];
        
        for(int row=0; row<N; row++){
            for(int col=0; col<N; col++){
                if(col==row){
                    result[row][col] = 4f;
                }else if(Math.abs(row-col)==1){
                    result[row][col] = 1f;
                }else{
                    result[row][col] = 0f;
                }
            }
        }
        return result;
    }
    
    private static double [][] generateConstantMatrix(SPoint[] sampledPoints){
        double[][] result = new double[sampledPoints.length-2][2];
        
        SPoint p = sub(mul(sampledPoints[1], 6), sampledPoints[0]);
        result[0][0] = p.getX();
        result[0][1] = p.getY();
        
        for (int i = 1; i < sampledPoints.length - 3; i++) {
            p = mul(sampledPoints[i+1], 6);
            result[i][0] = p.getX();
            result[i][1] = p.getY();
        }
        
        p = sub(mul(sampledPoints[sampledPoints.length - 3], 6), sampledPoints[sampledPoints.length - 2]);
        result[sampledPoints.length - 3][0] = p.getX();
        result[sampledPoints.length - 3][1] = p.getY();
        
        return result;
    }
    
    @SuppressWarnings("unused")
	private static int sub(int a0, int a1){
        return a0-a1;
    }
    
    @SuppressWarnings("unused")
	private static SPoint sub(SPoint a0, int a1){
        SPoint result = new SPoint(a0.getX()-a1, a0.getY()-a1);
        return result;
    }
    
    private static SPoint sub(SPoint a0, SPoint a1){
        SPoint result = new SPoint(a0.getX()-a1.getX(), a0.getY()-a1.getY());
        
        return result;
    }
    
    private static SPoint add(SPoint a0, SPoint a1){
        SPoint result = new SPoint(a0.getX()+a1.getX(), a0.getY()+a1.getY());
        
        
        return result;
    }
    
    private static SPoint add(SPoint a0, SPoint a1, SPoint a2){
        SPoint result = new SPoint(a0.getX()+a1.getX()+a2.getX(), a0.getY()+a1.getY()+a2.getY());
        
        return result;
    }
    
    @SuppressWarnings("unused")
	private static int mul(int a0, int a1){
        return a0*a1;
    }
    
    private static SPoint mul(SPoint a0, int a1){
        SPoint result = new SPoint(a0.getX()*a1, a0.getY()*a1);
        
        return result;
    }
    
    private static SPoint mul(SPoint a0, float a1){
        SPoint result = new SPoint(a0.getX()*a1, a0.getY()*a1);
        return result;
    }
    
    public static SignatureLine[] mul(SignatureLine[] line, float f){
        SignatureLine[] result = new SignatureLine[line.length];
        
        for(int i=0; i<line.length; i++){
            result[i] = new SignatureLine();
            result[i].setLx(f*line[i].getLx());
            result[i].setLy(f*line[i].getLy());
            result[i].setMx(f*line[i].getMx());
            result[i].setMy(f*line[i].getMy());
        }
        
        return result;
    }
    
    @SuppressWarnings("unused")
	private static SPoint mul(SPoint a0, SPoint a1){
        SPoint result = new SPoint(a0.getX()-a1.getX(), a0.getY()-a1.getY());
      
        return result;
    }
}
