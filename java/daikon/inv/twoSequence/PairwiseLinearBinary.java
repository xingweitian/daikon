package daikon.inv.twoSequence;

import daikon.*;
import daikon.inv.twoScalar.*;

class PairwiseLinearBinary extends TwoSequence {

  LinearBinaryCore core;

  protected PairwiseLinearBinary(PptSlice ppt) {
    super(ppt);
    core = new LinearBinaryCore(this);
  }

  public static PairwiseLinearBinary instantiate(PptSlice ppt) {
    return new PairwiseLinearBinary(ppt);
  }

  public String repr() {
    double probability = getProbability();
    return "PairwiseLinearBinary" + varNames() + ": "
      + "no_invariant=" + no_invariant
      + ",probability = " + probability
      + "; " + core.repr();
  }

  public String format() {
    if (no_invariant || ! justified()) {
      return null;
    }
    return core.format(var1().name, var2().name);
  }

  public void add_modified(int[] x_arr, int[] y_arr, int count) {
    if (x_arr.length != y_arr.length) {
      destroy();
      return;
    }
    int len = x_arr.length;
    // int len = Math.min(x_arr.length, y_arr.length);

    for (int i=0; i<len; i++) {
      int x = x_arr[i];
      int y = y_arr[i];

      core.add_modified(x, y, count);
      if (no_invariant) {
        // destroy() must have already been called
        return;
      }
    }
  }

  protected double computeProbability() {
    return core.computeProbability();
  }

}
