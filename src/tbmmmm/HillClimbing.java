package tbmmmm;

import localsearch.model.IConstraint;
import localsearch.model.VarIntLS;

import java.util.ArrayList;
import java.util.Random;

public class HillClimbing {
    IConstraint c;
    VarIntLS[] x;
    Random R = new Random();

    class Move {
        int i;
        int val;

        Move(int i, int val) {
            this.i = i;
            this.val = val;
        }
    }

    public HillClimbing(IConstraint c) {
        this.c = c;
    }

    private void exploreNeighborhood(ArrayList<Move> cand) {
        //Explore all neighbor sol, collect all best neighbor into cand
        int minDelta = Integer.MAX_VALUE;

        cand.clear();

        for (int i = 0; i < x.length; i++) {
            for (int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++) {
                int d = c.getAssignDelta(x[i], v);

                if (d < minDelta) {
                    cand.clear();
                    cand.add(new Move(i, v));
                    minDelta = d;
                } else if (d == minDelta) {
                    cand.add(new Move(i, v));
                }
            }
        }
    }

    private Move select(ArrayList<Move> cand) {
        int id = R.nextInt(cand.size());
        return cand.get(id);
    }

    public void search(int maxIters, int maxTime) {
        ArrayList<Move> cand = new ArrayList<Move>();
        x = c.getVariables();
        double t0 = System.currentTimeMillis();
        for (int i = 1; i <= maxIters; i++) {
            exploreNeighborhood(cand);
            Move m = select(cand);

            x[m.i].setValuePropagate(m.val);
            System.out.println("x[" + m.i + "] = " + m.val + " violations = " + c.violations());
            if (c.violations() == 0) break;

            double t = System.currentTimeMillis() - t0;
            if (t > maxTime) {
                System.out.println("Time limit exceeded");
                break;
            }
        }
    }

}
