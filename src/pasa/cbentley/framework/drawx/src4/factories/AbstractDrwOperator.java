/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.BOAbstractOperator;
import pasa.cbentley.byteobjects.src4.core.ByteObjectFactory;
import pasa.cbentley.byteobjects.src4.ctx.BOCtx;
import pasa.cbentley.byteobjects.src4.objects.color.FilterFactory;
import pasa.cbentley.byteobjects.src4.objects.pointer.IBOMergeMask;
import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.logging.IDLog;
import pasa.cbentley.core.src4.logging.IStringable;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.layouter.src4.engine.LayoutOperator;
import pasa.cbentley.layouter.src4.engine.TblrFactory;

public abstract class AbstractDrwOperator extends BOAbstractOperator implements IStringable, IBOMergeMask {

   protected final DrwCtx drc;

   protected final BOCtx  boc;

   public AbstractDrwOperator(DrwCtx drc) {
      super(drc.getBOC());
      this.drc = drc;
      this.boc = drc.getBOC();
   }

   public ByteObjectFactory getBOFactory() {
      return drc.getBOC().getByteObjectFactory();
   }

   public FilterFactory getFilterFactory() {
      return drc.getFilterFactory();
   }

   public TblrFactory getTblrFactory() {
      return drc.getTblrFactory();
   }

   public LayoutOperator getLayoutOperator() {
      return drc.getLayoutOperator();
   }

   //#mdebug
   public String toString() {
      return Dctx.toString(this);
   }

   public IDLog toDLog() {
      return drc.toDLog();
   }

   public void toString(Dctx dc) {
      dc.root(this, "AbstractEng");
      toStringPrivate(dc);
   }

   public String toString1Line() {
      return Dctx.toString1Line(this);
   }

   private void toStringPrivate(Dctx dc) {

   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, "AbstractEng");
      toStringPrivate(dc);
   }

   public UCtx toStringGetUCtx() {
      return drc.getUC();
   }

   //#enddebug

}
