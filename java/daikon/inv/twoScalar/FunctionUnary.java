package daikon.inv.twoScalar;

import daikon.*;
import java.lang.reflect.*;


class FunctionUnary extends TwoScalar {

  FunctionUnaryCore core;

  protected FunctionUnary(PptSlice ppt, String methodname, Method function, boolean inverse) {
    super(ppt);
    core = new FunctionUnaryCore(this, methodname, function, inverse);
  }

  public static FunctionUnary instantiate(PptSlice ppt, String methodname, Method function, boolean inverse) {
    return new FunctionUnary(ppt, methodname, function, inverse);
  }

  public String repr() {
    double probability = getProbability();
    return "FunctionUnary" + varNames() + ": "
      + "probability = " + probability
      + "; " + core.repr();
  }

  public String format() {
    if (justified()) {
      return core.format(var1().name, var2().name);
    } else {
      return null;
    }
  }


  public void add_modified(int x_int, int y_int, int count) {
    core.add_modified(x_int, y_int, count);
  }


  protected double computeProbability() {
    return core.computeProbability();
  }

  public boolean isExact() {
    return true;
  }

}
