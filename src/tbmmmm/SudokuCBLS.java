package tbmmmm;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

import java.util.ArrayList;
import java.util.Random;

public class SudokuCBLS {
    ConstraintSystem CS;
    LocalSearchManager mgr;
    VarIntLS[][] x;

    class Move {
        int i,j1,j2;
        public  Move(int i, int j1, int j2) {
            this.i = i; this.j1 = j1; this.j2 = j2;
        }
    }

    void exploreNeighborhood(ArrayList<Move> cand) {
        int minDelta = Integer.MAX_VALUE;
        cand.clear();
        for (int i = 0; i < 9; i++) {
            for (int j1 = 0; j1 < 8; j1++) {
                for (int j2 = j1+1; j2 < 9; j2++) {
                    int d = CS.getSwapDelta(x[i][j1], x[i][j2]);
                    if (d < minDelta) {
                        cand.clear();
                        cand.add(new Move(i,j1,j2));
                        minDelta = d;
                    } else if (d == minDelta) {
                        cand.add(new Move(i,j1,j2));
                    }
                }
            }
        }
    }

    public void stateModel() {
        mgr = new LocalSearchManager();
        x = new VarIntLS[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                x[i][j] = new VarIntLS(mgr,1,9);
                x[i][j].setValue(j+1);
            }
        }
        CS = new ConstraintSystem(mgr);

//        for (int i = 0; i < 9; i++) {
//            VarIntLS[] y = new VarIntLS[9];
//            for (int j = 0; j < 9; j++) {
//                y[j] = x[i][j];
//            }
//            CS.post(new AllDifferent(y));
//        }

        for (int i = 0; i < 9; i++) {
            VarIntLS[] y  = new VarIntLS[9];
            for (int j = 0; j < 9; j++) {
                y[j] = x[j][i];
            }
            CS.post(new AllDifferent(y));
        }

        for (int I = 0; I <= 2; I++) {
            for (int J = 0; J <= 2; J++) {
                VarIntLS[] y = new VarIntLS[9];
                int idx = -1;
                for (int i = 0; i <= 2; i++) {
                    for (int j = 0; j <= 2; j++) {
                        idx++;
                        y[idx] = x[3*I+i][3*J+j];
                    }
                }
                CS.post(new AllDifferent(y));
            }
        }

        mgr.close();
    }

    public void search() {
//        HillClimbing se = new HillClimbing(CS);
//        se.search(10000, 10000);

        Random R = new Random();
        ArrayList<Move> cand = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            exploreNeighborhood(cand);
            int idx = R.nextInt(cand.size());

            Move m = cand.get(idx);
            x[m.i][m.j1].swapValuePropagate(x[m.i][m.j2]);

            System.out.println("Step "+i+" violations = "+CS.violations());

            if (CS.violations() == 0) {
                break;
            }
        }

        if (CS.violations() == 0) {
            System.out.println("Solution found:");
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    System.out.print(x[i][j].getValue()+ " ");
                }
                System.out.println("");
            }
        } else System.out.println("No solution");
    }

    public static void main(String[] args) {
        SudokuCBLS app = new SudokuCBLS();
        app.stateModel();
        app.search();
    }

}
