package khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model;

public class MinMaxTypeMultiKnapsackSolution {
	private int[] binOfItem;// binOfItem[i] = -1: item i not scheduled
	
	public int[] getBinOfItem() {
		return binOfItem;
	}

	public void setBinOfItem(int[] binOfItem) {
		this.binOfItem = binOfItem;
	}

	public MinMaxTypeMultiKnapsackSolution(int[] binOfItem) {
		this.binOfItem = binOfItem;
	}

	public MinMaxTypeMultiKnapsackSolution() {
		super();
	}
}
