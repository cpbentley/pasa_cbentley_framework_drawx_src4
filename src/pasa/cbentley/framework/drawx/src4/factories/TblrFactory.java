package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.interfaces.C;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.drawx.src4.tech.ITechTblr;
import pasa.cbentley.layouter.src4.interfaces.ISizable;
import pasa.cbentley.layouter.src4.tech.ITechLayout;

/**
 * 3 ways to encode a TBLR
 * <li> 1 raw value
 * <li> 1 sizer
 * <li> 4 sizers
 * 
 * @author Charles Bentley
 *
 */
public class TblrFactory extends AbstractDrwFactory implements ITechTblr {

   public TblrFactory(DrwCtx drc) {
      super(drc);
   }

   /**
    * Get method for TBLR values given a position.
    * <br>
    * The ViewContext is the w and h
    * <br>
    * @param tblr
    * @param pos
    * @return
    */
   public int getTBLRValue(ByteObject tblr, int pos) {
      return tblr.get4(ITechTblr.TBLR_OFFSET_03_DATA4);
   }

   /**
    * Returns 0 if failure to compute
    * <br>
    * Logs a Dev BIP.
    * <br>
    * @param tblr
    * @param pos
    * @param sizer
    * @param sc
    * @return
    */
   public int getTBLRValue(ByteObject tblr, int pos, ISizable sizer, int w, int h) {
      int type = tblr.get1(TBLR_OFFSET_02_TYPE1);
      if (type == TYPE_0_ONE) {
         return tblr.get4(TBLR_OFFSET_03_DATA4);
      } else if (type == TYPE_1_SIZER) {
         ByteObject siz = tblr.getSubAtIndexNull(0);
         if (siz != null) {
            int ctx = ITechLayout.CTX_1_WIDTH;
            if (pos == C.POS_0_TOP || pos == C.POS_1_BOT) {
               ctx = ITechLayout.CTX_2_HEIGHT;
            }
            return sizer.getPixelSize(siz, w, h, ctx);
         }
      }
      //log DEV warning TODO
      //error case we return 0.
      return 0;
   }

   /**
    * TBLR is typically reused many times over. Immutable 
    * A reference consumed 4 bytes.
    * A same value
    * @param samevalue
    * @return
    */
   public ByteObject getTBLR(int samevalue) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_060_TBLR, ITechTblr.TBLR_BASIC_SIZE);
      p.set4(TBLR_OFFSET_03_DATA4, samevalue);
      p.setFlag(ITechTblr.TBLR_OFFSET_01_FLAG, ITechTblr.TBLR_FLAG_4_SAME_VALUE, true);
      return p;
   }

   /**
    * Looks up the pooling
    * Immutable TBLR value
    * @param top -1 for undefined
    * @param bot
    * @param left
    * @param right
    * @return
    */
   public ByteObject getTBLR(int top, int bot, int left, int right) {
      if (top == bot && top == left && top == right) {
         return getTBLR(top);
      } else {
         ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_060_TBLR, ITechTblr.TBLR_BASIC_SIZE);
         p.setFlag(ITechTblr.TBLR_OFFSET_01_FLAG, ITechTblr.TBLR_FLAG_5_DEF_TOP, top != TBLR_UNDEF);
         p.setFlag(ITechTblr.TBLR_OFFSET_01_FLAG, ITechTblr.TBLR_FLAG_6_DEF_BOT, bot != TBLR_UNDEF);
         p.setFlag(ITechTblr.TBLR_OFFSET_01_FLAG, ITechTblr.TBLR_FLAG_7_DEF_LEFT, left != TBLR_UNDEF);
         p.setFlag(ITechTblr.TBLR_OFFSET_01_FLAG, ITechTblr.TBLR_FLAG_8_DEF_RIGHT, right != TBLR_UNDEF);
         int[] ar = new int[] { top, bot, left, right };
         ByteObject arrayBo = boc.getLitteralIntFactory().getLitteralArray(ar);
         p.addSub(arrayBo);
         return p;
      }
   }

   public ByteObject getTBLR(ByteObject sizerTop, ByteObject sizerBot, ByteObject sizerLeft, ByteObject sizerRight) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_060_TBLR, ITechTblr.TBLR_BASIC_SIZE);

      p.setFlag(ITechTblr.TBLR_OFFSET_01_FLAG, ITechTblr.TBLR_FLAG_5_DEF_TOP, sizerTop != null);
      p.setFlag(ITechTblr.TBLR_OFFSET_01_FLAG, ITechTblr.TBLR_FLAG_6_DEF_BOT, sizerBot != null);
      p.setFlag(ITechTblr.TBLR_OFFSET_01_FLAG, ITechTblr.TBLR_FLAG_7_DEF_LEFT, sizerLeft != null);
      p.setFlag(ITechTblr.TBLR_OFFSET_01_FLAG, ITechTblr.TBLR_FLAG_8_DEF_RIGHT, sizerRight != null);

      p.addByteObjectNull(sizerTop);
      p.addByteObjectNull(sizerBot);
      p.addByteObjectNull(sizerLeft);
      p.addByteObjectNull(sizerRight);

      return p;
   }

   /**
    * a TBLR with a unique sizer for the elements.
    * <br>
    * 
    * @param mod
    * @param sizer defines the unit. when null. sero
    * @return
    */
   public ByteObject getTBLR(ByteObject sizer) {
      ByteObject bo = getTBLR(0, 0, 0, 0);
      bo.set1(TBLR_OFFSET_02_TYPE1, TYPE_1_SIZER);
      bo.addSub(sizer);
      return bo;
   }

   /**
    * May return null if no sizers
    * @param tblr
    * @param pos
    * @return
    */
   public ByteObject getTBLRSizer(ByteObject tblr, int pos) {
      int mod = 0;
      if (!tblr.hasFlag(ITechTblr.TBLR_OFFSET_01_FLAG, ITechTblr.TBLR_FLAG_4_SAME_VALUE)) {
         if (pos == C.POS_1_BOT)
            mod = 1;
         else if (pos == C.POS_2_LEFT)
            mod = 2;
         else if (pos == C.POS_3_RIGHT)
            mod = 3;
      }
      return tblr.getSubAtIndexNull(mod);
   }
   
   /**
    * Descriptive. no context needed.
    * @param bo
    * @param sb
    */
   public void toStringTBLR(ByteObject bo, Dctx sb) {
      sb.append("#TBLR");
      if (bo.hasFlag(ITechTblr.TBLR_OFFSET_01_FLAG, ITechTblr.TBLR_FLAG_4_SAME_VALUE)) {
         sb.append(" Value = " + getTBLRValue(bo, C.POS_0_TOP));
      } else {
         sb.append(" Top=" + getTBLRValue(bo, C.POS_0_TOP));
         sb.append(" Bot=" + getTBLRValue(bo, C.POS_1_BOT));
         sb.append(" Left=" + getTBLRValue(bo, C.POS_2_LEFT));
         sb.append(" Right=" + getTBLRValue(bo, C.POS_3_RIGHT));
      }
   }

}
