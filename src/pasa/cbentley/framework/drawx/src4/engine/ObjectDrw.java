/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.engine;

import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.logging.IDLog;
import pasa.cbentley.core.src4.logging.IStringable;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;

public abstract class ObjectDrw implements IStringable {
   protected final DrwCtx drc;

   protected final DrwCtx dc;

   public ObjectDrw(DrwCtx drc) {
      this.drc = drc;
      this.dc = drc;
   }

   //#mdebug
   public IDLog toDLog() {
      return toStringGetUCtx().toDLog();
   }

   public String toString() {
      return Dctx.toString(this);
   }

   public void toString(Dctx dc) {
      dc.root(this, ObjectDrw.class, "@line5");
      toStringPrivate(dc);
   }

   public String toString1Line() {
      return Dctx.toString1Line(this);
   }

   private void toStringPrivate(Dctx dc) {

   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, ObjectDrw.class);
      toStringPrivate(dc);
   }

   public UCtx toStringGetUCtx() {
      return dc.getUCtx();
   }

   //#enddebug

}
