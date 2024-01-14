package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;

public class TabColumnStringer  extends ObjectDrw {

   private Stringer stringer;

   public TabColumnStringer(Stringer stringer) {
      super(stringer.getDRC());
      this.stringer = stringer;
   }

}
