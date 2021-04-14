package tbmmmm;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.selectors.MinMaxSelector;

public class QueenCBLS {
    int n;
    LocalSearchManager mgr;
    VarIntLS[] x;
    ConstraintSystem S;

    public QueenCBLS(int n) {
        this.n = n;
    }

    private void stateModel() {
        mgr = new LocalSearchManager();
        S = new ConstraintSystem(mgr);

        x = new VarIntLS[n];
        for (int i = 0; i < n; i++) x[i] = new VarIntLS(mgr,0,n-1);
        S.post(new AllDifferent(x));

        IFunction[] f1 = new IFunction[n];
        for (int i = 0; i < n; i++) f1[i] = new FuncPlus(x[i], i);
        S.post(new AllDifferent(f1));

        IFunction[] f2 = new IFunction[n];
        for (int i = 0; i < n; i++) f2[i] = new FuncPlus(x[i], -i);
        S.post(new AllDifferent(f2));

        mgr.close();
    }

    private void localSearch() {
        MinMaxSelector mms = new MinMaxSelector(S);
        for(int i = 1; i <= 10000; i++) {
            VarIntLS selectX = mms.selectMostViolatingVariable();
            int v = mms.selectMostPromissingValue(selectX);
            selectX.setValuePropagate(v);

            System.out.println("Step "+i+" violations = "+S.violations());

            if(S.violations() == 0) {
                break;
            }
        }
    }

    public void solve() {
        stateModel();
        //localSearch();

        HillClimbing searcher = new HillClimbing(S);
        searcher.search(10000, 100000);
        for(int i = 0; i< n; i ++) {
            System.out.println("x["+i+"] = "+x[i].getValue());
        }
    }

    public static void main(String[] args) {
        QueenCBLS app = new QueenCBLS(20);
        app.solve();
    }

}