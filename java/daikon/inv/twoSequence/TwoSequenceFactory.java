package daikon.inv.twoSequence;

import daikon.*;
import daikon.inv.*;

import java.lang.reflect.*;

import utilMDE.*;

public class TwoSequenceFactory {

  // Adds the appropriate new Invariant objects to the specified Invariants
  // collection.
  public static void instantiate(PptSlice ppt, int pass) {
    // Not really the right place for these tests
    VarInfo var1 = ppt.var_infos[0];
    VarInfo var2 = ppt.var_infos[1];

    Assert.assert(var1.rep_type.equals(ProglangType.INT_ARRAY)
                  && var2.rep_type.equals(ProglangType.INT_ARRAY));

    VarInfo super1 = var1.isDerivedSubSequenceOf();
    if (super1 == null)
      super1 = var1;
    VarInfo super2 = var2.isDerivedSubSequenceOf();
    if (super2 == null)
      super2 = var2;

    if (Daikon.check_program_types
        && (! var1.type.comparable(var2.type)))
      return;

    if (pass == 1) {
      if (super1 != super2) {
        SeqComparison.instantiate(ppt);
      }
    } else if (pass == 2) {
      Reverse.instantiate(ppt);
      if (super1 != super2) {
        // NonEqual.instantiate(ppt);
        SubSequence.instantiate(ppt);
        SuperSequence.instantiate(ppt);

        PairwiseIntComparison.instantiate(ppt);
        PairwiseLinearBinary.instantiate(ppt);
        for (int i=0; i<2; i++) {
          boolean invert = (i==1);
          VarInfo arg = (invert ? var1 : var2);
          // Don't bother to check arg.isConstant():  we really want to
          // know whether the elements of arg are constant
          PairwiseFunctionUnary.instantiate(ppt, Functions.Math_abs, invert);
          PairwiseFunctionUnary.instantiate(ppt, Functions.MathMDE_negate, invert);
          PairwiseFunctionUnary.instantiate(ppt, Functions.MathMDE_bitwiseComplement, invert);
        }
      }
    }

  }

  private TwoSequenceFactory() {
  }

}
