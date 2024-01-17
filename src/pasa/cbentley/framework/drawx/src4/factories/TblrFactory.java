/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesBOC;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesDrw;
import pasa.cbentley.core.src4.interfaces.C;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.utils.BitUtils;
import pasa.cbentley.framework.drawx.src4.ctx.BOModuleDrawx;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOTblr;
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
public class TblrFactory extends AbstractDrwFactory implements IBOTblr {

   public TblrFactory(DrwCtx drc) {
      super(drc);
   }

   /**
    * Get method for TBLR values given a position.
    * <br>
    * The ViewContext is the w and h
    * <br>
    * @param tblr
    * @param pos {@link C#POS_0_TOP} ...
    * @return
    */
   public int getTBLRValue(ByteObject tblr, int pos) {
      int flags = tblr.get1(TBLR_OFFSET_01_FLAG);
      if (BitUtils.hasFlag(flags, TBLR_FLAG_4_SAME_VALUE)) {
         return tblr.get4(IBOTblr.TBLR_OFFSET_03_DATA4);
      } else {
         //how to read the value
         if (BitUtils.hasFlag(flags, TBLR_FLAG_1_USING_ARRAY)) {
            ByteObject intarray = tblr.getSubFirst(IBOTypesBOC.TYPE_007_LIT_ARRAY_INT);
            return boc.getLitteralIntOperator().getLitteralArrayValueAt(intarray, pos);
         }
         if (pos == C.POS_0_TOP) {
            if (BitUtils.hasFlag(flags, TBLR_FLAG_5_DEF_TOP)) {
               return tblr.get1(TBLR_OFFSET_03_DATA4);
            } else {
               return 0;
            }
         } else if (pos == C.POS_1_BOT) {
            if (BitUtils.hasFlag(flags, TBLR_FLAG_6_DEF_BOT)) {
               return tblr.get1(TBLR_OFFSET_03_DATA4 + 1);
            } else {
               return 0;
            }
         } else if (pos == C.POS_2_LEFT) {
            if (BitUtils.hasFlag(flags, TBLR_FLAG_7_DEF_LEFT)) {
               return tblr.get1(TBLR_OFFSET_03_DATA4 + 2);
            } else {
               return 0;
            }
         } else if (pos == C.POS_3_RIGHT) {
            if (BitUtils.hasFlag(flags, TBLR_FLAG_8_DEF_RIGHT)) {
               return tblr.get1(TBLR_OFFSET_03_DATA4 + 3);
            } else {
               return 0;
            }
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   /**
    * Called by {@link BOModuleDrawx#merge(ByteObject, ByteObject)}
    * 
    * @param root
    * @param merge
    * @return
    */
   public ByteObject mergeTBLR(ByteObject root, ByteObject merge) {
      int top = -1;
      if (merge.hasFlag(IBOTblr.TBLR_OFFSET_01_FLAG, IBOTblr.TBLR_FLAG_5_DEF_TOP)) {
         top = getTBLRValue(merge, C.POS_0_TOP);
      }
      int bot = -1;
      if (merge.hasFlag(IBOTblr.TBLR_OFFSET_01_FLAG, IBOTblr.TBLR_FLAG_6_DEF_BOT)) {
         bot = getTBLRValue(merge, C.POS_1_BOT);
      }
      int left = -1;
      if (merge.hasFlag(IBOTblr.TBLR_OFFSET_01_FLAG, IBOTblr.TBLR_FLAG_7_DEF_LEFT)) {
         left = getTBLRValue(merge, C.POS_2_LEFT);
      }
      int right = -1;
      if (merge.hasFlag(IBOTblr.TBLR_OFFSET_01_FLAG, IBOTblr.TBLR_FLAG_8_DEF_RIGHT)) {
         right = getTBLRValue(merge, C.POS_3_RIGHT);
      }
      ByteObject nt = getTblrFactory().getTBLR(top, bot, left, right);
      return nt;
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
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_060_TBLR, IBOTblr.TBLR_BASIC_SIZE);
      p.set4(TBLR_OFFSET_03_DATA4, samevalue);
      p.setFlag(IBOTblr.TBLR_OFFSET_01_FLAG, IBOTblr.TBLR_FLAG_4_SAME_VALUE, true);
      return p;
   }

   /**
    * Looks up the pooling
    * Immutable TBLR value
    * @param top -1 or negative for undefined
    * @param bot
    * @param left
    * @param right
    * @return
    */
   public ByteObject getTBLR(int top, int bot, int left, int right) {
      if (top == bot && top == left && top == right) {
         return getTBLR(top);
      } else {
         //
         ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_060_TBLR, IBOTblr.TBLR_BASIC_SIZE);
         int flag = 0;
         boolean useArray = false;
         boolean isDefTop = true;
         boolean isDefBot = true;
         boolean isDefLeft = true;
         boolean isDefRight = true;
         if (top > 255) {
            useArray = true;
            if (top < 0) {
               isDefTop = false;
            }
         }
         if (bot > 255) {
            useArray = true;
            if (top < 0) {
               isDefBot = false;
            }
         }
         if (left > 255) {
            useArray = true;
            if (top < 0) {
               isDefLeft = false;
            }
         }
         if (right > 255) {
            useArray = true;
            if (top < 0) {
               isDefRight = false;
            }
         }
         flag = BitUtils.setFlag(flag, IBOTblr.TBLR_FLAG_5_DEF_TOP, isDefTop);
         flag = BitUtils.setFlag(flag, IBOTblr.TBLR_FLAG_6_DEF_BOT, isDefBot);
         flag = BitUtils.setFlag(flag, IBOTblr.TBLR_FLAG_7_DEF_LEFT, isDefLeft);
         flag = BitUtils.setFlag(flag, IBOTblr.TBLR_FLAG_8_DEF_RIGHT, isDefRight);
         if (useArray) {
            int[] ar = new int[] { top, bot, left, right };
            ByteObject arrayBo = boc.getLitteralIntFactory().getLitteralArray(ar);
            p.addSub(arrayBo);
            flag = BitUtils.setFlag(flag, IBOTblr.TBLR_FLAG_1_USING_ARRAY, true);
         } else {
            int values = 0;
            values = BitUtils.setByte1(values, top);
            values = BitUtils.setByte2(values, bot);
            values = BitUtils.setByte3(values, left);
            values = BitUtils.setByte4(values, right);
            p.set4(TBLR_OFFSET_03_DATA4, values);
         }
         p.set1(TBLR_OFFSET_01_FLAG, flag);
         return p;
      }
   }

   public ByteObject getTBLR(ByteObject sizerTop, ByteObject sizerBot, ByteObject sizerLeft, ByteObject sizerRight) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_060_TBLR, IBOTblr.TBLR_BASIC_SIZE);

      p.setFlag(IBOTblr.TBLR_OFFSET_01_FLAG, IBOTblr.TBLR_FLAG_5_DEF_TOP, sizerTop != null);
      p.setFlag(IBOTblr.TBLR_OFFSET_01_FLAG, IBOTblr.TBLR_FLAG_6_DEF_BOT, sizerBot != null);
      p.setFlag(IBOTblr.TBLR_OFFSET_01_FLAG, IBOTblr.TBLR_FLAG_7_DEF_LEFT, sizerLeft != null);
      p.setFlag(IBOTblr.TBLR_OFFSET_01_FLAG, IBOTblr.TBLR_FLAG_8_DEF_RIGHT, sizerRight != null);

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
      if (!tblr.hasFlag(IBOTblr.TBLR_OFFSET_01_FLAG, IBOTblr.TBLR_FLAG_4_SAME_VALUE)) {
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
    * @param dc
    */
   public void toStringTBLR(ByteObject bo, Dctx dc) {
      dc.rootN(this, "TBLR", TblrFactory.class, 274);
      dc.appendVarWithSpace("hasArray", bo.hasFlag(TBLR_OFFSET_01_FLAG, TBLR_FLAG_1_USING_ARRAY));
      dc.appendVarWithSpace("SameValue", bo.hasFlag(TBLR_OFFSET_01_FLAG, TBLR_FLAG_4_SAME_VALUE));
      if (bo.hasFlag(IBOTblr.TBLR_OFFSET_01_FLAG, IBOTblr.TBLR_FLAG_4_SAME_VALUE)) {
         dc.append(" Value = " + getTBLRValue(bo, C.POS_0_TOP));
      } else {
         dc.appendVarWithSpace("top", bo.hasFlag(TBLR_OFFSET_01_FLAG, TBLR_FLAG_5_DEF_TOP));
         dc.appendVarWithSpace("bot", bo.hasFlag(TBLR_OFFSET_01_FLAG, TBLR_FLAG_6_DEF_BOT));
         dc.appendVarWithSpace("left", bo.hasFlag(TBLR_OFFSET_01_FLAG, TBLR_FLAG_7_DEF_LEFT));
         dc.appendVarWithSpace("right", bo.hasFlag(TBLR_OFFSET_01_FLAG, TBLR_FLAG_8_DEF_RIGHT));
         dc.nl();
         dc.append(" Top=" + getTBLRValue(bo, C.POS_0_TOP));
         dc.append(" Bot=" + getTBLRValue(bo, C.POS_1_BOT));
         dc.append(" Left=" + getTBLRValue(bo, C.POS_2_LEFT));
         dc.append(" Right=" + getTBLRValue(bo, C.POS_3_RIGHT));
      }
   }

}
