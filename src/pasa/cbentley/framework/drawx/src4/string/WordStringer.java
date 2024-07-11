package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.structs.BufferObject;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;

/**
 * Continuous characters without space tab, newlines
 * @author Charles Bentley
 *
 */
public class WordStringer extends ObjectDrw {

   private int len;

   private int offset;
   
   /**
    * 
    */
   private BufferObject charAlgo;

   public WordStringer(DrwCtx drc) {
      super(drc);
   }

   public int getLen() {
      return len;
   }

   public int getOffset() {
      return offset;
   }

   public void setLen(int len) {
      this.len = len;
   }

   public void setOffset(int offset) {
      this.offset = offset;
   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, WordStringer.class, 60);
      toStringPrivate(dc);
      super.toString(dc.sup());
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, WordStringer.class);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   private void toStringPrivate(Dctx dc) {

   }
   //#enddebug

}
