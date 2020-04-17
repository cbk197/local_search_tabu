package khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.constraints.basic.OR;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.TabuSearch;

class AssignMove{
	int x;
	int v;
	public AssignMove(int a, int b) {
		x = a;
		v = b;
	}
}




class Hset{
	HashSet<Integer> Bins;
	public Hset() {
		Bins = new HashSet<Integer>();
	}
}
public class Test {
	int _F = 1000;
	LocalSearchManager mgr;
	ConstraintSystem S;
	VarIntLS[] X;
	VarIntLS[][] Y;
	VarIntLS[][] Z;
	
	MinMaxTypeMultiKnapsackInput Input;
	MinMaxTypeMultiKnapsackInputItem[] Items;
	MinMaxTypeMultiKnapsackInputBin[] Bins;
	Hset _domain[];
	int _w[];
	int _p[];
	int _r[];
	int _t[];
	int _W[];//suc chua 1 cua bin 
	int _P[];//suc chua 2 cua bin
	int _mW[];// min load
	int _R[];
	int _T[];
	Set<Integer> Rset = new HashSet<Integer>();
	Set<Integer> Tset = new HashSet<Integer>();
	Map<Integer, Integer> Rmap = new HashMap<>();
	Map<Integer, Integer> Tmap = new HashMap<>();
	public Test() {
		MinMaxTypeMultiKnapsackInput InputTmp = new MinMaxTypeMultiKnapsackInput();
		Input = InputTmp.loadFromFile(
				"C:\\Users\\ironman\\Desktop\\git\\CourseCPCBLS\\OpenCBLS\\src\\khmtk60\\miniprojects\\multiknapsackminmaxtypeconstraints\\MinMaxTypeMultiKnapsackInput-3000.json");
		Items = Input.getItems();
		Bins = Input.getBins();
		_domain = new Hset[Items.length];
		_w = new int[Items.length];
		_p = new int[Items.length];
		_t = new int[Items.length];
		_r = new int[Items.length];
		for(int i = 0 ; i< Items.length; i++) {
			
			_domain[i] = new Hset();
			_w[i] = (int)(Items[i].getW()*_F);
			_p[i] = (int)(Items[i].getP()*_F);
			_t[i] = Items[i].getT();
			if(!Tset.contains(_t[i])) {
				Tset.add(_t[i]);
			}
			_r[i] = Items[i].getR();
			if(!Rset.contains(_r[i])) {
				Rset.add(_r[i]);
			}
			for(int j = 0; j<Items[i].getBinIndices().length; j++) {
				_domain[i].Bins.add(Items[i].getBinIndices()[j]);
			}
		}
		int tmp = 0;
		for(int i : Rset) {
			Rmap.put(i, tmp);
			tmp++;
		};
		tmp = 0; 
		for(int i : Tset) {
			Tmap.put(i, tmp);
			tmp++;
		}
		_W = new int[Bins.length];
		_P = new int[Bins.length];
		_mW = new int[Bins.length];
		_T = new int[Bins.length];
		_R = new int[Bins.length];
		for(int i = 0 ; i< Bins.length; i++) {
			_W[i] = (int)(Bins[i].getCapacity()*_F);
//			System.out.print("    "+ _W[i]+ "  ==  "+Bins[i].getCapacity());
			_P[i] = (int)(Bins[i].getP()*_F);
			_mW[i] = (int)(Bins[i].getMinLoad()*_F);
			_T[i] = Bins[i].getT();
			_R[i] = Bins[i].getR();
		}
		System.out.println();
		System.out.println("bins : " + Bins.length +"  Items : " + Items.length);
	}

	public void statemodel() {
		Random R = new Random();
		mgr = new LocalSearchManager();
		S  = new ConstraintSystem(mgr);
		X = new VarIntLS[Items.length];
		Y = new VarIntLS[Bins.length][Rmap.size()];
		Z = new VarIntLS[Bins.length][Tmap.size()];
//		_Btn = new VarIntLS[Items.length][Bins.length];
		for(int i = 0 ; i< Bins.length; i++) {
			for(int j = 0 ; j<Rmap.size(); j++) {
				Y[i][j] = new VarIntLS(mgr, 0,1);
				
			}
		}
		for(int i = 0 ; i< Bins.length; i++) {
			for(int j = 0 ; j<Tmap.size(); j++) {
				Z[i][j] = new VarIntLS(mgr, 0,1);
				
			}
		}
		for ( int i = 0 ; i < Items.length; i++) {
			
			
			X[i] = new VarIntLS(mgr, _domain[i].Bins);
			Iterator<Integer> Cons =	_domain[i].Bins.iterator();
			int tmp = R.nextInt(_domain[i].Bins.size());
			int tmp0 = 0;
			while(tmp0 < tmp) {
				Cons.next();
				tmp0++;
			}
			X[i].setValue(Cons.next());
			Y[X[i].getValue()][Rmap.get(_r[i])].setValue(1);
			Z[X[i].getValue()][Tmap.get(_t[i])].setValue(1);
//			for(int j = 0 ; j< Bins.length; j++) {
//				_Btn[i][j] = new VarIntLS(mgr, 0,1);
//			}
		}
		
		
		for(int i = 0 ; i< Items.length; i++) {
			S.post(new IsEqual(Y[X[i].getValue()][Rmap.get(_r[i])], 1 ));
			S.post(new IsEqual(Z[X[i].getValue()][Tmap.get(_t[i])], 1 ));
		}
		for(int i=0; i<Bins.length;i++) {
//			S.post(new IsEqual(Y[i], _r[X[i].getValue()]));
//			for(int j = 0 ; j< Items.length; j++) {
//				
//				S.post(new Implicate(new NotEqual(X[j], i), new IsEqual(_Btn[j][i], 0)));
//				S.post(new Implicate(new IsEqual(X[j], i), new IsEqual(_Btn[j][i], 1)));
//				
//			}
			
			IFunction F1 = new ConditionalSum(X, _w, i);
			S.post(new LessOrEqual(F1, _W[i]));
			S.post(new OR(new LessOrEqual(_mW[i],F1), new IsEqual(F1,0)));
			S.post(new LessOrEqual(new ConditionalSum(X, _p, i), _P[i]));
			S.post(new LessOrEqual( new Sum(Y[i]), _R[i]));
			S.post(new LessOrEqual( new Sum(Z[i]), _T[i]));
			
		}
		
		
		
		
//		S.close();
		mgr.close();
		
		
	}

	
	public void hillclimbing(IConstraint c, int maxIter) {
		VarIntLS[] y = c.getVariables();
		ArrayList<AssignMove> cand = new ArrayList<AssignMove>();
		Random R = new Random();
		int it = 0;
		while (it < maxIter && c.violations() != 0) {
			cand.clear();
			int minDelta = Integer.MAX_VALUE;
			for (int i = 0; i < y.length; i++) {
				for (int v = y[i].getMinValue(); v <= y[i].getMaxValue(); v++) {
					int d = c.getAssignDelta(y[i], v);
					if (d < minDelta) {
						cand.clear();
						cand.add(new AssignMove(i, v));
						minDelta = d;
					} else if (d == minDelta) {
						cand.add(new AssignMove(i, v));
					}
				}
			}
			int idx = R.nextInt(cand.size());
			AssignMove m = cand.get(idx);
			y[m.x].setValuePropagate(m.v);
			System.out.println("Step " + it + " ,violation " + c.violations());
			it++;
			
		}
	}
	
	public void printItem() {
		for(int i = 0 ; i< Items.length; i++) {
			System.out.println("items "+i+"    W : "+Items[i].getW() + " T : "+ Items[i].getT() +"    P : " + Items[i].getP() + " R: " + Items[i].getR());
			for(int j = 0 ; j<Items[i].getBinIndices().length; j++) {
				System.out.print("  "+Items[i].getBinIndices()[j]);
			}
			System.out.println();
		}
	}
	public void printBin() {
		for(int i = 0 ; i< Bins.length; i++) {
			System.out.print("\nbin " + i +" W = " + Bins[i].getCapacity() + " P= " + Bins[i].getP()+" minload : "+ Bins[i].getMinLoad()+ "  T"+ Bins[i].getT()+"  R" + Bins[i].getR());
		}
	}
	public void printResult() {
		double W ,P;
		for(int i = 0; i < Bins.length; i++) {
			W = 0;
			P = 0;
			System.out.print("bin "+i+" : ");
			for(int j =0 ; j<Items.length; j++) {
				if(X[j].getValue() == i) {
					System.out.print("  "+j);
					W += Items[j].getW();
					P += Items[j].getP();
				}
			}
			System.out.print("    P = " +P+"   W= "+W);
			System.out.println();
		}
	}

	public static void main(String[] args) {
		Test C = new Test();
		C.statemodel();
//		TabuSearch ts = new TabuSearch();
//		ts.search(C.S, 50,1000, 1000, 100);
//		C.printItem();
//		C.printBin();
		C.hillclimbing(C.S, 200);
		C.printResult();
		System.out.println(" \nviolation : "+C.S.violations());
		
		
	}
}
