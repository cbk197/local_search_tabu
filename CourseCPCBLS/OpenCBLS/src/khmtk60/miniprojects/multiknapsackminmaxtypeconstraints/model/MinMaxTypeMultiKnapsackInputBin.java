package khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model;

public class MinMaxTypeMultiKnapsackInputBin {
	private double capacity; // suc chua cho trong so 1
	private double minLoad; // tai toi thieu cho trong so 1 
	private double p; // suc chua cho trong so 2
	private int t;//so luong Item toi da cho moi the loai trong bin 
	private int r;// so luong item toi da cho moi lop trong bin 
	public double getCapacity() {
		return capacity;
	}
	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}
	public double getMinLoad() {
		return minLoad;
	}
	public void setMinLoad(double minLoad) {
		this.minLoad = minLoad;
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
	public MinMaxTypeMultiKnapsackInputBin(double capacity, double minLoad,
			double p, int t, int r) {
		super();
		this.capacity = capacity;
		this.minLoad = minLoad;
		this.p = p;
		this.t = t;
		this.r = r;
	}
	public MinMaxTypeMultiKnapsackInputBin() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
}
