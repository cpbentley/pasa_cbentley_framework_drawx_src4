/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.BOAbstractFactory;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.layouter.src4.engine.TblrFactory;

public class AbstractDrwFactory extends BOAbstractFactory {

   protected final DrwCtx drc;

   public AbstractDrwFactory(DrwCtx drc) {
      super(drc.getBOC());
      this.drc = drc;
   }

   public TblrFactory getTblrFactory() {
      return drc.getTblrFactory();
   }
   
   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, AbstractDrwFactory.class, 27);
      toStringPrivate(dc);
      super.toString(dc.sup());
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, AbstractDrwFactory.class, 27);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   private void toStringPrivate(Dctx dc) {
      
   }
   //#enddebug
   


}
