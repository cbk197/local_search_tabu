package khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.TabuSearch;



public class MinMaxTypeMultiKnapsackSolution2 {
	int _F  = 1000;
	LocalSearchManager mgr;
	VarIntLS X[][], Y[][], Z[][];
	int nBins, nItems;
	int[] w, p, Max_W, Min_W, Max_P;
	int[] t, r, Max_T, Max_R;
	Set<Integer>[] BinIndices;
	Map mapR;
	Map mapT;
	
	MinMaxTypeMultiKnapsackInputItem[] Items;
	MinMaxTypeMultiKnapsackInputBin[] Bins;
	
	ConstraintSystem S;
	
	int nR=0;
	int nT=0;
	
	public void init() {
		w = new int[nItems];
		p = new int[nItems];
		Max_W = new int[nBins];
		Min_W = new int[nBins];
		Max_P = new int[nBins];
		t = new int[nItems];
		r = new int[nItems];
		Max_T = new int[nBins];
		Max_R = new int[nBins];
		BinIndices = new Set[nItems];
	}
	
	public void readData(String fileName) {
		MinMaxTypeMultiKnapsackInput input = new MinMaxTypeMultiKnapsackInput().loadFromFile(fileName);

		nBins = input.getBins().length;
		nItems = input.getItems().length;
		
		init();

		for(int i=0; i<nItems; i++) {
			w[i] = (int)(input.getItems()[i].getW()*_F);
			p[i] = (int)(input.getItems()[i].getP()*_F);
			t[i] = (int)(input.getItems()[i].getT()*_F);
			r[i] = input.getItems()[i].getR();
			BinIndices[i] = new HashSet<>();
			for(int j : input.getItems()[i].getBinIndices()) {
				BinIndices[i].add(j);
			}
		}
		
		for(int k=0; k<nBins; k++) {
			Max_W[k] = (int)(input.getBins()[k].getCapacity()*_F);
			Min_W[k] = (int)(input.getBins()[k].getMinLoad()*_F);
			Max_P[k] = (int)(input.getBins()[k].getP()*_F);
			Max_T[k] = input.getBins()[k].getT();
			Max_R[k] = input.getBins()[k].getR();
		}
		
		Map mapR = new HashMap();
		Map mapT = new HashMap();
		
		for(int i=0; i<r.length; i++) {
			if(!mapR.containsKey(r[i])) {
				mapR.put(r[i], nR);
				nR++;
			}
			r[i] = (int) mapR.get(r[i]);
		}
		
		for(int j=0; j<t.length; j++) {
			if(!mapT.containsKey(t[j])) {
				mapT.put(t[j], nT);
				nT++;
			}
			t[j] = (int) mapT.get(t[j]);
		}

		System.out.println("Read data!" + ", nR:" + nR + ", nT:" + nT + ", nBins:" + nBins + ", nItems:" + nItems);
		
	}
	
	public void stateModel() {
		LocalSearchManager mgr = new LocalSearchManager();
		S = new ConstraintSystem(mgr);
		X = new VarIntLS[nBins][nItems];
		Y = new VarIntLS[nBins][nT];
		Z = new VarIntLS[nBins][nR];
		
		for(int i=0; i<nBins; i++) {
			for(int m=0; m<nT; m++) {
				Y[i][m] = new VarIntLS(mgr, 0, 1);
			}
			
			for(int n=0; n<nR; n++) {
				Z[i][n] = new VarIntLS(mgr, 0, 1);
			}
			
			for(int j=0; j<nItems; j++) {
				X[i][j] = new VarIntLS(mgr, 0, 1);
				
				S.post(new LessOrEqual(X[i][j], Y[i][t[j]]));
				S.post(new LessOrEqual(X[i][j], Z[i][r[j]]));
			}
			
			S.post(new LessOrEqual(new ConditionalSum(X[i], w, 1), Max_W[i]));

			S.post(new LessOrEqual(Min_W[i], new ConditionalSum(X[i], w, 1)));

			S.post(new LessOrEqual(new ConditionalSum(X[i], p, 1), Max_P[i]));

			S.post(new LessOrEqual(new Sum(Z[i]), Max_R[i]));

			S.post(new LessOrEqual(new Sum(Y[i]), Max_T[i]));
			
		}
		
		for(int i=0; i<nItems; i++) {
			VarIntLS[] tmp = new VarIntLS[nBins];
			for(int j=0; j<nBins; j++) {
				tmp[j] = X[j][i];
			}

			S.post(new IsEqual(new Sum(tmp), 1));

			VarIntLS[] tmp2 = new VarIntLS[BinIndices[i].size()];
			int c = 0;
			for(int loc : BinIndices[i]) {
				tmp2[c] = X[loc][i];
				c ++;
			}
			S.post(new IsEqual(new Sum(tmp2), 1));
		}
		
		greedy_1();
		mgr.close();
	}
	
	public void greedy_1() {
		for(int i=0; i<nItems; i++) {
			int bin_index = 0;
			for(Integer x: BinIndices[i]) {
				bin_index = x;
				break;
			}
			X[bin_index][i].setValue(1);
			
//			Y[bin_index][t[i]].setValuePropagate(1);
//			Z[bin_index][r[i]].setValuePropagate(1);
//			System.out.println(Y[bin_index][t[i]].getValue() + "-" + Z[bin_index][r[i]].getValue());
		}
	}

	public void search(int maxIter) throws FileNotFoundException {
//		HillClimbingSearch s = new HillClimbingSearch();
//		s.hillClimbing(S, maxIter);
//		Search s =  new Search();
//		s.search(S);
//		
		TabuSearch ts = new TabuSearch();
		ts.search(S, 50, 10000, maxIter, 50);
		printResults();
		visualizeHTML();
	}
	
	public void visualizeHTML() throws FileNotFoundException {
		VisualizeToHTML visualize = new VisualizeToHTML();
		String fileNameHTML = "tableResults_2.html";
		File outFile = new File(fileNameHTML);
		PrintWriter f = new PrintWriter(fileNameHTML);
		visualize.writeTag(f, "html", "");
		visualize.writeTag(f, "head", "tg");
		visualize.writeTagLinkCSS(f, "table.css", "");
		visualize.writeTag(f, "/head", "");
		visualize.writeTag(f, "body", "tg");
		visualize.writeTag(f, "table", "tg");
		visualize.writeTag(f, "caption", "caption");
		f.println("Table Results For Project (solution 2)");
		visualize.writeTag(f, "tr", "");
		visualize.writeTagTh(f, "Bin", "tg-qnmb");
		visualize.writeTagTh(f, "Loaded items", "tg-qnmb");
		visualize.writeTagTh(f, "Loaded w", "tg-qnmb");
		visualize.writeTagTh(f, "Loaded p", "tg-qnmb");
		visualize.writeTagTh(f, "Loaded types", "tg-qnmb");
		visualize.writeTagTh(f, "Loaded classes", "tg-qnmb");
		visualize.writeTag(f, "/tr", "");
		for(int i=0; i<nBins; i++) {
			visualize.writeTag(f, "tr", "");
			visualize.writeTagTd(f, "" + (i+1), "tg-s268");
			String s = "";
			int bin_w = 0;
			int bin_p = 0;
			int bin_types = 0;
			int bin_classes = 0;
			
			for(int j=0; j<nItems; j++) {
				if(X[i][j].getValue() == 1) {
					s += j + " ";
					bin_w += w[j];
					bin_p += p[j];
				}
			}
			
			for(int j=0; j<nT; j++) {
				if(Y[i][j].getValue() == 1) {
					bin_types += 1;
				}
			}
			
			for(int j=0; j<nR; j++) {
				if(Z[i][j].getValue() == 1) {
					bin_classes += 1;
				}
			}
			visualize.writeTagTd(f, s, "tg-s268");
			visualize.writeTagTd(f, "" + bin_w, "tg-s268");
			visualize.writeTagTd(f, "" + bin_p, "tg-s268");
			visualize.writeTagTd(f, "" + bin_types, "tg-s268");
			visualize.writeTagTd(f, "" + bin_classes, "tg-s268");
			visualize.writeTag(f, "/tr", "");
		}
		visualize.writeTag(f, "/table", "");
		visualize.writeTag(f, "/body", "");
		visualize.writeTag(f, "/html", "");
		f.close();
	}
	
	public void printResults() {
		System.out.println("Done!");
	}
	
	public void slove() throws FileNotFoundException {
//		readData("MinMaxTypeMultiKnapsackInput-1000.json");
		readData("C:\\Users\\ironman\\Desktop\\git\\CourseCPCBLS\\OpenCBLS\\src\\khmtk60\\miniprojects\\multiknapsackminmaxtypeconstraints\\200.json");
		
		stateModel();
		search(1000);
//		visualizeHTML();
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		MinMaxTypeMultiKnapsackSolution2 solution = new MinMaxTypeMultiKnapsackSolution2();
		solution.slove();
	}

}
