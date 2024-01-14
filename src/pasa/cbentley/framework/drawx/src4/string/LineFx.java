package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;

/**
 * Specifics for LineFxs
 * @author Charles Bentley
 *
 */
public class LineFx extends ObjectDrw {

   private LineStringer line;

   public LineFx(DrwCtx drc, LineStringer line) {
      super(drc);
      this.line = line;
   }

   private ByteObject figureBG;

   private ByteObject lineTBLR;

   private ByteObject lineFigTop;

   private ByteObject lineFigBot;

   private ByteObject lineFigLeft;

   private ByteObject lineFigRight;

   private ByteObject figureFG;
   
   /**
    * Line BG that depends on the index of the line
    * @return
    */
   public ByteObject getFigureBG() {
      return figureBG;
   }
}
