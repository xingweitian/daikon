package daikon.split.griesLisp;

import daikon.*;
import daikon.split.*;

public class y_gt_x extends Splitter {
  public String condition() { return "y > x"; }
  VarInfo y_varinfo;
  VarInfo x_varinfo;
  public y_gt_x() { }
  public y_gt_x(Ppt ppt) {
    y_varinfo = ppt.findVar("Y");
    x_varinfo = ppt.findVar("X");
  }
  public Splitter instantiate(Ppt ppt) { return new y_gt_x(ppt); }
  public boolean valid() { return (x_varinfo != null) && (y_varinfo != null); }
  public boolean test(ValueTuple vt) {
    return (y_varinfo.getIntValue(vt) > x_varinfo.getIntValue(vt));
  }
}
