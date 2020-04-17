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

public class Solution1 {
	int _F = 1000;
	LocalSearchManager mgr;
	ConstraintSystem S;
	VarIntLS[][] X;
	VarIntLS[][] Y;
	VarIntLS[][] Z;
	VarIntLS[][] X1;
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
	int _BinIn[][];
	Set<Integer> Rset = new HashSet<Integer>();
	Set<Integer> Tset = new HashSet<Integer>();
	Map<Integer, Integer> Rmap = new HashMap<>();
	Map<Integer, Integer> Tmap = new HashMap<>();
	public Solution1() {
		MinMaxTypeMultiKnapsackInput InputTmp = new MinMaxTypeMultiKnapsackInput();
		Input = InputTmp.loadFromFile(
				"C:\\Users\\ironman\\Desktop\\git\\CourseCPCBLS\\OpenCBLS\\src\\khmtk60\\miniprojects\\multiknapsackminmaxtypeconstraints\\200.json");
		Items = Input.getItems();
		Bins = Input.getBins();
		_BinIn = new int[Bins.length][Items.length];
		for(int i = 0 ; i< Bins.length; i++) {
			for(int j = 0 ; j<Items.length; j++) {
				_BinIn[i][j] = 0 ; 
			}
		}
		_domain = new Hset[Items.length];
		_w = new int[Items.length];
		_p = new int[Items.length];
		_t = new int[Items.length];
		_r = new int[Items.length];
		for(int i = 0 ; i< Items.length; i++) {
			
//			_domain[i] = new Hset();
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
//				_domain[i].Bins.add(Items[i].getBinIndices()[j]);
				_BinIn[Items[i].getBinIndices()[j]][i] = 1;
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
	
	public void stateModel() {
		Random R = new Random();
		mgr = new LocalSearchManager();
		S  = new ConstraintSystem(mgr);
		X  = new VarIntLS[Bins.length][Items.length];
		X1 = new VarIntLS[Items.length][Bins.length];
		Y = new VarIntLS[Bins.length][Rmap.size()];
		Z = new VarIntLS[Bins.length][Tmap.size()];
		for(int i = 0; i<Bins.length; i++) {
			for(int j = 0 ; j< Items.length; j++) {
				X[i][j] = new VarIntLS(mgr, 0,1);
				X1[j][i] = new VarIntLS(mgr, 0,1);
			}
			
			for(int j = 0 ; j<Rmap.size(); j++) {
				Y[i][j] = new VarIntLS(mgr, 0,1);
				
			}
			for(int j = 0 ; j<Tmap.size(); j++) {
				Z[i][j] = new VarIntLS(mgr, 0,1);
				
			}
		}
		
		for(int i = 0; i<Items.length;i++) {
			for(int j = 0 ; j<Bins.length;j++) {
				if(_BinIn[j][i] == 1) {
					X[j][i].setValue(1);
					break;
				}
			}
		}
		for(int i = 0 ; i<Bins.length; i++) {
			for(int j = 0; j<Items.length; j++) {
				S.post(new LessOrEqual(X[i][j], _BinIn[i][j]));
				S.post(new Implicate(new IsEqual(X[i][j], 1), new IsEqual(Y[i][Rmap.get(_r[j])], 1)));
				S.post(new Implicate(new IsEqual(X[i][j], 1), new IsEqual(Z[i][Tmap.get(_t[j])], 1)));
				
			}
			IFunction Fw = new ConditionalSum(X[i],_w,1);
			S.post(new LessOrEqual(Fw,_W[i]));
			S.post(new LessOrEqual(new ConditionalSum(X[i], _p,1), _P[i]));
			S.post(new OR(new LessOrEqual(_mW[i], Fw), new IsEqual(Fw, 0)));
			S.post(new LessOrEqual( new Sum(Y[i]), _R[i]));
			S.post(new LessOrEqual( new Sum(Z[i]), _T[i]));
		}
		for(int i = 0 ; i< Items.length; i++) {
			VarIntLS[] X2 = new VarIntLS[Bins.length];
			for(int j = 0 ; j<Bins.length;j++) {
				X2[j] = X[j][i];
			}
			S.post(new IsEqual(new Sum(X2), 1));
		}
		
		mgr.close();
	
		
		
	}
	public static void main(String[] args) {
		Solution1 C = new Solution1();
		TabuSearch t = new TabuSearch();
		C.stateModel();
		t.search(C.S, 30, 5000, 10000, 30);
	}

}
