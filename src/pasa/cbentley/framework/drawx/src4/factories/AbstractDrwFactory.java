/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.BOAbstractFactory;
import pasa.cbentley.core.src4.logging.IDLog;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;

public class AbstractDrwFactory extends BOAbstractFactory {

   protected final DrwCtx drc;

   public AbstractDrwFactory(DrwCtx drc) {
      super(drc.getBOC());
      this.drc = drc;
   }

   public IDLog toDLog() {
      return drc.toDLog();
   }

   public TblrFactory getTblrFactory() {
      return drc.getTblrFactory();
   }

  
}
