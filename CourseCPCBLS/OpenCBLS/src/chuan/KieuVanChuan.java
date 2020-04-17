package chuan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInput;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInputBin;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInputItem;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.multiknapsack.MultiKnapsack;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.AbstractInvariant;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.TabuSearch;

class AssignMove {
	int x;
	int v;

	public AssignMove(int x, int v) {
		this.x = x;
		this.v = v;
	}
}


class Rlist {
	int R;
	int Count;

	public Rlist(int r, int c) {
		R = r;
		Count = c;
	}
}

class Hset{
	HashSet<Integer> Bins;
	public Hset() {
		Bins = new HashSet<Integer>();
	}
}
public class KieuVanChuan {
	int _F = 1000000;
	LocalSearchManager mgr;
	ConstraintSystem S;
	VarIntLS[] X;
	VarIntLS[] Y;// chua so lop trong bin 
	VarIntLS[] Z;// chua so loai trong bin
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

	public KieuVanChuan() {
		MinMaxTypeMultiKnapsackInput InputTmp = new MinMaxTypeMultiKnapsackInput();
		Input = InputTmp.loadFromFile(
				"C:\\Users\\ironman\\Desktop\\git\\CourseCPCBLS\\OpenCBLS\\src\\khmtk60\\miniprojects\\multiknapsackminmaxtypeconstraints\\MinMaxTypeMultiKnapsackInput.json");
		Items = Input.getItems();
		Bins = Input.getBins();
		_domain = new Hset[Items.length];
		_w = new int[Items.length];
		_p = new int[Items.length];
		_t = new int[Items.length];
		_r = new int[Items.length];
		for(int i = 0 ; i< Items.length; i++) {
			_domain[i] = new Hset();
			_w[i] = (int)Items[i].getW()*_F;
			_p[i] = (int)Items[i].getP()*_F;
			_t[i] = Items[i].getT();
			_r[i] = Items[i].getR();
			for(int j = 0; j<Items[i].getBinIndices().length; j++) {
				_domain[i].Bins.add(Items[i].getBinIndices()[j]);
			}
		}
		_W = new int[Bins.length];
		_P = new int[Bins.length];
		_mW = new int[Bins.length];
		_T = new int[Bins.length];
		_R = new int[Bins.length];
		for(int i = 0 ; i< Bins.length; i++) {
			_W[i] = (int)Bins[i].getCapacity()*_F;
			_P[i] = (int)Bins[i].getP()*_F;
			_mW[i] = (int)Bins[i].getMinLoad()*_F;
			_T[i] = Bins[i].getT();
			_R[i] = Bins[i].getR();
		}
	}

	public void statemodel() {
		Random R = new Random();
		int _tmp;
		mgr = new LocalSearchManager();
		S  = new ConstraintSystem(mgr);
		X = new VarIntLS[Items.length];
		Y = new VarIntLS[Bins.length];
		for ( int i = 0 ; i < Items.length; i++) {
			Y[i] = new VarIntLS(mgr, 0,1);
			X[i] = new VarIntLS(mgr, _domain[i].Bins);
			Iterator<Integer> Cons =	_domain[i].Bins.iterator();
			int tmp = R.nextInt(_domain[i].Bins.size());
			int tmp0 = 0;
			while(tmp0 < tmp) {
				Cons.next();
				tmp0++;
			}
			X[i].setValue(Cons.next());
		}
		
		for(int i=0; i<Bins.length;i++) {
			
			
			S.post(new LessOrEqual(new ConditionalSum(X, _w, i), _W[i]));
			S.post(new LessOrEqual(_mW[i],new ConditionalSum(X, _w, i)));
			S.post(new LessOrEqual(new ConditionalSum(X, _p, i), _P[i]));
		}
		
//		S.close();
		mgr.close();
		System.out.println("violation "+ S.violations());
		
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
			printResult();
		}
	}
	
	public void printItem() {
		for(int i = 0 ; i< Items.length; i++) {
			System.out.println("items "+i+"    W : "+Items[i].getW() + " T : "+ Items[i].getT() +"    P : " + Items[i].getP());
			for(int j = 0 ; j<Items[i].getBinIndices().length; j++) {
				System.out.print("  "+Items[i].getBinIndices()[j]);
			}
			System.out.println();
		}
	}
	public void printBin() {
		for(int i = 0 ; i< Bins.length; i++) {
			System.out.print("\nbin " + i +" W = " + Bins[i].getCapacity() + " P= " + Bins[i].getP());
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
		KieuVanChuan C = new KieuVanChuan();
		C.statemodel();
		TabuSearch ts = new TabuSearch();
//		ts.search(C.S, 10000, 300, 1000, 10000);
		C.printItem();
		C.printBin();
		C.hillclimbing(C.S, 20);
		C.printResult();
	}
}
