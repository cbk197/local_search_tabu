package khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model;

public class MinMaxTypeMultiKnapsackInputItem {
	private double w; // trong so 1
	private double p; // trong so 2
	private int t;// the loai
	private int r;// lop 
	
	private int[] binIndices; // danh sach bin co the chua item
	
	
	public MinMaxTypeMultiKnapsackInputItem(double w, double p, int t, int r,
			int[] binIndices) {
		super();
		this.w = w;
		this.p = p;
		this.t = t;
		this.r = r;
		this.binIndices = binIndices;
	}


	public double getW() {
		return w;
	}


	public void setW(double w) {
		this.w = w;
	}


	public double getP() {
		return p;
	}


	public void setP(double p) {
		this.p = p;
	}


	public int getT() {
		return t;
	}


	public void setT(int t) {
		this.t = t;
	}


	public int getR() {
		return r;
	}


	public void setR(int r) {
		this.r = r;
	}


	public int[] getBinIndices() {
		return binIndices;
	}


	public void setBinIndices(int[] binIndices) {
		this.binIndices = binIndices;
	}


	public MinMaxTypeMultiKnapsackInputItem() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
