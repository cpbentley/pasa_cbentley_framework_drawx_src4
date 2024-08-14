package pasa.cbentley.framework.drawx.src4.engine;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.utils.interfaces.IColors;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOPassBridge;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.ITechPass;

public class PassBridge extends ObjectDrw implements IBOPassBridge {

   private int extraH;

   private int extraW;

   private ByteObject gpass;

   private int h;

   private boolean isExtraSponge = false;

   protected PassBridge parent;

   private int w;

   public PassBridge(DrwCtx drc) {
      super(drc);
   }

   public void computeExtra(PassBridge pass) {
      int type = pass.getPassType();
      switch (type) {
         case ITechPass.PASS_1_MOSAIC:
            break;
         case ITechPass.PASS_2_SKEW:
            if (pass.isExtraSponge) {
               //eat them
               pass.extraW = 0;
               pass.extraH = 0;
            } else {
               //resize 
               pass.removeExtra();
            }
            break;
         case ITechPass.PASS_3_SCALE:
            if (pass.isExtraSponge) {
               //eat them
               pass.extraH = 0;
               pass.extraW = 0;
            }
            break;
         case ITechPass.PASS_4_ROTATE:
            if (pass.isExtraSponge) {
               //eat them
               pass.extraH = 0;
               pass.extraW = 0;
            }
            break;
         default:
            break;
      }
   }

   public int getPassBgColor() {
      int bgColor = IColors.FULLY_TRANSPARENT_BLACK;
      if (hasGPassFlag(PASS_FLAG_2_POST_FILTER)) {
         bgColor = IColors.FULLY_OPAQUE_BLACK;
      }
      if (hasGPassFlag(PASS_FLAG_1_FULLY_TRANS_WHITE)) {
         bgColor = IColors.FULLY_TRANSPARENT_WHITE;
      }
      return bgColor;
   }

   public int getPassType() {
      return 0;
   }

   private boolean hasGPassFlag(int flag) {
      return gpass.hasFlag(PBRIDGE_OFFSET_01_FLAG1, flag);
   }

   public boolean isInChain(PassBridge gp) {
      if (this == gp) {
         return true;
      } else {
         if (parent != null) {
            return parent.isInChain(gp);
         }
      }
      return false;
   }

   public void processExtra(int wt, int ht) {
      while (extraW - wt >= 0) {
         extraW -= wt;
      }
      while (extraH - ht >= 0) {
         extraH -= ht;
      }

   }

   private void removeExtra() {
      w -= extraW;
      h -= extraH;
   }

   public void setArea(int w, int h) {
      this.w = w;
      this.h = h;
   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, PassBridge.class, 30);
      toStringPrivate(dc);
      super.toString(dc.sup());
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, PassBridge.class, 30);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   private void toStringPrivate(Dctx dc) {

   }
   //#enddebug

}
