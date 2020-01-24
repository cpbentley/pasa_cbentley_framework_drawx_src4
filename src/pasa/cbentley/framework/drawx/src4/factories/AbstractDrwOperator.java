package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.BOAbstractOperator;
import pasa.cbentley.byteobjects.src4.core.ByteObjectFactory;
import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.logging.IDLog;
import pasa.cbentley.core.src4.logging.IStringable;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;

public abstract class AbstractDrwOperator extends BOAbstractOperator implements IStringable {
  
   protected final DrwCtx drc;

   public AbstractDrwOperator(DrwCtx drc) {
      super(drc.getBOC());
      this.drc = drc;
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
      return drc.getUCtx();
   }

   //#enddebug

}
