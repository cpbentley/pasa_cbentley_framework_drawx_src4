/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.ctx;

import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.logging.IDLog;
import pasa.cbentley.core.src4.logging.IStringable;

/**
 * 
 * @author Charles Bentley
 *
 */
public abstract class ObjectDrw implements IStringable {
   protected final DrwCtx drc;

   //#debug
   private String         toStringName;

   public ObjectDrw(DrwCtx drc) {
      this.drc = drc;
   }

   public DrwCtx getDRC() {
      return drc;
   }

   public UCtx getUC() {
      return drc.getUCtx();
   }

   //#mdebug
   public IDLog toDLog() {
      return toStringGetUCtx().toDLog();
   }

   public void toStringSetName(String name) {
      if (toStringName == null) {
         toStringName = name;
      } else {
         toStringName = toStringName + " - " + name;
      }
   }

   public String toString() {
      return Dctx.toString(this);
   }

   public void toString(Dctx dc) {
      dc.root(this, ObjectDrw.class, 50);
      toStringPrivate(dc);
   }

   public String toString1Line() {
      return Dctx.toString1Line(this);
   }

   private void toStringPrivate(Dctx dc) {
      if(toStringName != null) {
         dc.appendWithSpace(toStringName);
      }
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, ObjectDrw.class);
      toStringPrivate(dc);
   }

   public UCtx toStringGetUCtx() {
      return drc.getUCtx();
   }

   //#enddebug

}
