package tbmmmm;

import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.LessThan;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class BACP {
    int n,p,K,lambda,gamma,alpha,beta;
    int[] c,I,J;

    LocalSearchManager mgr;
    VarIntLS[] x;
    ConstraintSystem CS;
    IFunction[] credits; //credits[j]: So tin chi cac mon phan vao hoc ky j
    IFunction[] courses; //courses[j]: So mon phan vao hoc ky j

    public void readData(String filename) {
        try {
            Scanner in = new Scanner(new File(filename));
            n = in.nextInt();
            p = in.nextInt();
            lambda = in.nextInt();
            gamma = in.nextInt();
            alpha = in.nextInt();
            beta = in.nextInt();
            c = new int[n];
            for (int i = 0; i < n; i++) {
                c[i] = in.nextInt();
            }

            K = in.nextInt();
            I = new int[K];
            J = new int[K];
            for (int i = 0; i < K; i++) {
                I[i] = in.nextInt()-1;
                J[i] = in.nextInt()-1;
            }

            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    void stateModel() {
        mgr = new LocalSearchManager();
        x = new VarIntLS[n];
        for (int i = 0; i < n; i++)
            x[i] = new VarIntLS(mgr, 0, p-1);

        CS = new ConstraintSystem(mgr);

        credits = new IFunction[p];
        courses = new IFunction[p];

        for (int i = 0; i < p; i++) {
            credits[i] = new ConditionalSum(x, c, i);
            courses[i] = new ConditionalSum(x, i);

            CS.post(new LessOrEqual(alpha,courses[i]));
            CS.post(new LessOrEqual(courses[i], beta));

            CS.post(new LessOrEqual(lambda,credits[i]));
            CS.post(new LessOrEqual(credits[i], gamma));
        }

        for (int i = 0; i < K; i++) {
            CS.post(new LessThan(x[I[i]], x[J[i]]));
        }

        mgr.close();
    }

    void search() {
        HillClimbing se = new HillClimbing(CS);
        se.search(10000, 10000);

        if (CS.violations() == 0) {
            System.out.println("Solution found:");
            for (int i = 1; i <= p; i++) {
                System.out.print("Day "+i+" : ");
                for (int j = 0; j < n; j++) {
                    if (x[j].getValue() == i) {
                        System.out.print(j + " ");
                    }
                }
                System.out.println("");
            }
        }
    }

    public static void main(String[] args) {
        BACP app = new BACP();
        app.readData("./data/BACP/bacp.in01");
        app.stateModel();
        app.search();
    }
}
