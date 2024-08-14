/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.interfaces.C;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrawX;
import pasa.cbentley.framework.drawx.src4.ctx.ToStringStaticDrawx;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOBox;
import pasa.cbentley.framework.drawx.src4.tech.ITechAnchor;
import pasa.cbentley.layouter.src4.engine.LayoutOperator;
import pasa.cbentley.layouter.src4.tech.IBOPozer;
import pasa.cbentley.layouter.src4.tech.IBOSizer;
import pasa.cbentley.layouter.src4.tech.ITechLayout;

/**
 * FactoryEngine for {@link IBOBox}
 * <br>
 * Boxes constrain Figures to specific areas of the drawing rectangle
 * Creates a lightweight layout in a drawing rectangle. No concept of GUI. Just drawing
 * Simplified {@link IBOSizer} and {@link IBOPozer} in a single ByteObject of 16 bytes 
 * Move to Layouter
 * @author Charles Bentley
 *
 */
public class BoxFactory extends AbstractDrwFactory implements ITechLayout, IBOBox, ITechAnchor {

   private LayoutOperator sizable;

   public BoxFactory(DrwCtx drc) {
      super(drc);
      this.sizable = drc.getSizer();
   }

   public int computeSizeH(ByteObject box, int w, int h) {
      return sizable.codedSizeDecode(box, BOX_OFFSET_05_HEIGHT4, w, h, CTX_2_HEIGHT);
   }

   public int computeSizeW(ByteObject box, int w, int h) {
      return sizable.codedSizeDecode(box, BOX_OFFSET_04_WIDTH4, w, h, CTX_1_WIDTH);
   }

   public int computeX(int fw, ByteObject anchor, int x, int y, int w, int h) {
      return sizable.getPosAlign(BOX_OFFSET_02_HORIZ_ALIGN4, anchor, x, w, fw, w, h, CTX_1_WIDTH);
   }

   public int computeY(int fh, ByteObject anchor, int x, int y, int w, int h) {
      return sizable.getPosAlign(BOX_OFFSET_03_VERTICAL_ALIGN4, anchor, y, h, fh, w, h, CTX_2_HEIGHT);
   }

   private ByteObject createBox() {
      return getBOFactory().createByteObject(IBOTypesDrawX.TYPE_DRWX_03_BOX, BOX_BASIC_SIZE);
   }

   /**
    * For all 4 int parameters, -1 means undefined. Thus any -1 will set object as {@link IBOBox#BOX_FLAG_1_INCOMPLETE}
    * For alignments the values are : <br>
    * <li>{@link ITechAnchor#ALIGN_1_TOP}
    * <li>{@link ITechAnchor#ALIGN_2_BOTTOM}
    * <li>{@link ITechAnchor#ALIGN_3_LEFT}
    * <li>{@link ITechAnchor#ALIGN_4_RIGHT}
    * <li>{@link ITechAnchor#ALIGN_6_CENTER}
    * <li>{@link ITechAnchor#ALIGN_5_FILL}
    * <br>
    * <br>
    * 
    * {@link IBOBox}
    * 
    * Type : {@link IBOTypesDrawX#TYPE_DRWX_03_BOX}
    * <br>
    * <br>
    * 
    * @param ha horizontal alignment
    * @param va vertical alignment see 
    * @param w width of the box
    * @param h height of the box
    * @param perc true if sizeW and sizeH are percetages. False when they are pixel values
    * @return
    */
   public ByteObject createBox(int ha, int va, int w, int h, boolean perc) {
      ByteObject p = createBox();
      boolean incomplete = false;
      if (ha != BOX_UNDEFINED) {
         p.setFlag(BOX_OFFSET_01_FLAG, BOX_FLAG_7_DEF_HORIZ_ALIGN, true);
      } else {
         incomplete = true;
         ha = C.LOGIC_1_TOP_LEFT;
      }
      if (va != BOX_UNDEFINED) {
         p.setFlag(BOX_OFFSET_01_FLAG, BOX_FLAG_8_DEF_VERT_ALIGN, true);
      } else {
         incomplete = true;
         va = C.LOGIC_1_TOP_LEFT;
      }
      if (w != BOX_UNDEFINED) {
         p.setFlag(BOX_OFFSET_01_FLAG, BOX_FLAG_5_DEF_WIDTH, true);
         if (perc) {
            //create sizers
            w = sizable.codedSizeEncode(MODE_2_FUNCTION, w, 0, 0, 0);
         }
      } else {
         incomplete = true;
         w = 0;
      }
      if (h != BOX_UNDEFINED) {
         p.setFlag(BOX_OFFSET_01_FLAG, BOX_FLAG_6_DEF_HEIGHT, true);
         if (perc) {
            h = sizable.codedSizeEncode(MODE_2_FUNCTION, h, 0, 0, 0);
         }
      } else {
         incomplete = true;
         h = 0;
      }

      p.set4(BOX_OFFSET_03_VERTICAL_ALIGN4, va);
      p.set4(BOX_OFFSET_02_HORIZ_ALIGN4, ha);
      p.set4(BOX_OFFSET_04_WIDTH4, w);
      p.set4(BOX_OFFSET_05_HEIGHT4, h);

      p.setFlag(BOX_OFFSET_01_FLAG, BOX_FLAG_1_INCOMPLETE, incomplete);
      return p;
   }

   /**
    * Gets a box with w and h undefined.
    * <br>
    * Meaning it will take the context w and h or preferred size. (String figure)
    * @param ha
    * @param va
    * @return
    */
   public ByteObject getBox(int ha, int va) {
      return createBox(ha, va, BOX_UNDEFINED, BOX_UNDEFINED, false);
   }

   /**
    * 
    * @param ha
    * @param va
    * @param sizeW
    * @param sizeH
    * @return
    */
   public ByteObject getBox(int ha, int va, int sizeW, int sizeH) {
      return createBox(ha, va, sizeW, sizeH, false);
   }

   public ByteObject getBoxCenterBotT_WH() {
      return createBox(ALIGN_6_CENTER, ALIGN_2_BOTTOM, BOX_UNDEFINED, BOX_UNDEFINED, false);
   }

   public ByteObject getBoxCenterCenter(int w, int h) {
      return createBox(ALIGN_6_CENTER, ALIGN_6_CENTER, w, h, false);
   }

   public ByteObject getBoxCenterCenterT_WH() {
      return createBox(ALIGN_6_CENTER, ALIGN_6_CENTER, BOX_UNDEFINED, BOX_UNDEFINED, false);
   }

   public ByteObject getBoxCenterLeft(int w, int h) {
      return createBox(ALIGN_3_LEFT, ALIGN_6_CENTER, w, h, false);
   }

   public ByteObject getBoxCenterRight(int w, int h) {
      return createBox(ALIGN_4_RIGHT, ALIGN_6_CENTER, w, h, false);
   }

   public ByteObject getBoxCenterTop() {
      return createBox(ALIGN_6_CENTER, ALIGN_1_TOP, BOX_UNDEFINED, BOX_UNDEFINED, false);
   }

   /**
   * Incomplete box with just H defined
   * Fill Anchor with an explicit pixel Height given. <br>
   * Width is unknown and set to zero. 
   * Will be explicity given for the drawing.
   * <br>
   * <br>
   * @param h
   * @return {@link ByteObject} of type {@link IBOTypesDrawX#TYPE_DRWX_03_BOX}.
   */
   public ByteObject getBoxH(int h) {
      return createBox(BOX_UNDEFINED, BOX_UNDEFINED, BOX_UNDEFINED, h, false);
   }

   public ByteObject getBoxLeftBot(int w, int h) {
      return createBox(ALIGN_3_LEFT, ALIGN_2_BOTTOM, w, h, false);
   }

   public ByteObject getBoxLeftCenter() {
      return createBox(ALIGN_3_LEFT, ALIGN_6_CENTER, BOX_UNDEFINED, BOX_UNDEFINED, false);
   }

   public ByteObject getBoxLeftCenter(int w, int h) {
      return createBox(ALIGN_3_LEFT, ALIGN_6_CENTER, w, h, false);
   }

   public ByteObject getBoxLeftTop(int w, int h) {
      return createBox(ALIGN_3_LEFT, ALIGN_1_TOP, w, h, false);
   }

   public ByteObject getBoxRightBot(int w, int h) {
      return createBox(ALIGN_4_RIGHT, ALIGN_2_BOTTOM, w, h, false);
   }

   public ByteObject getBoxRightCenter() {
      return createBox(ALIGN_4_RIGHT, ALIGN_6_CENTER, BOX_UNDEFINED, BOX_UNDEFINED, false);
   }

   public ByteObject getBoxRightCenter(int w, int h) {
      return createBox(ALIGN_4_RIGHT, ALIGN_6_CENTER, w, h, false);
   }

   public ByteObject getBoxRightTop(int w, int h) {
      return createBox(ALIGN_4_RIGHT,ALIGN_1_TOP, w, h, false);
   }

   public ByteObject getBoxTopCenter(int w, int h) {
      return createBox(ALIGN_6_CENTER, ALIGN_1_TOP, w, h, false);
   }

   public ByteObject getBoxW(int w) {
      return createBox(BOX_UNDEFINED, BOX_UNDEFINED, w, BOX_UNDEFINED, false);
   }

   /**
    * 
    * @param root
    * @param merge
    * @return
    */
   public ByteObject mergeBox(ByteObject root, ByteObject merge) {
      int rootHa = root.get4(BOX_OFFSET_02_HORIZ_ALIGN4);
      int rootVa = root.get4(BOX_OFFSET_03_VERTICAL_ALIGN4);
      int rootW = root.get4(BOX_OFFSET_04_WIDTH4);
      int rootH = root.get4(BOX_OFFSET_05_HEIGHT4);
      if (merge.hasFlag(BOX_OFFSET_01_FLAG, BOX_FLAG_7_DEF_HORIZ_ALIGN)) {
         rootHa = merge.get4(BOX_OFFSET_02_HORIZ_ALIGN4);
      }
      if (merge.hasFlag(BOX_OFFSET_01_FLAG, BOX_FLAG_8_DEF_VERT_ALIGN)) {
         rootVa = merge.get4(BOX_OFFSET_03_VERTICAL_ALIGN4);
      }
      if (merge.hasFlag(BOX_OFFSET_01_FLAG, BOX_FLAG_5_DEF_WIDTH)) {
         rootW = merge.get4(BOX_OFFSET_04_WIDTH4);
      }
      if (merge.hasFlag(BOX_OFFSET_01_FLAG, BOX_FLAG_6_DEF_HEIGHT)) {
         rootH = merge.get4(BOX_OFFSET_05_HEIGHT4);
      }
      ByteObject newAnchor = drc.getBoxFactory().getBox(rootHa, rootVa, rootW, rootH);
      return newAnchor;
   }

   //#mdebug
   public void toStringBox(ByteObject bo, Dctx sb) {
      sb.append("#Box ");
      sb.append("[H V]=[");
      sb.append(ToStringStaticDrawx.toStringAlign(bo.get4(BOX_OFFSET_02_HORIZ_ALIGN4)));
      if (bo.hasFlag(BOX_OFFSET_01_FLAG, BOX_FLAG_7_DEF_HORIZ_ALIGN)) {

      } else {
         sb.append(" undef");
      }
      sb.append(' ');
      sb.append(ToStringStaticDrawx.toStringAlign(bo.get4(BOX_OFFSET_03_VERTICAL_ALIGN4)));
      if (bo.hasFlag(BOX_OFFSET_01_FLAG, BOX_FLAG_8_DEF_VERT_ALIGN)) {

      } else {
         sb.append(" undef");
      }
      sb.append(']');
      int w = bo.get4(BOX_OFFSET_04_WIDTH4);
      sb.append(" [w,h]=");
      if (bo.hasFlag(BOX_OFFSET_01_FLAG, BOX_FLAG_5_DEF_WIDTH)) {
         //ask sizer factory for a String
         sb.append(drc.getLAC().getLayoutFactory().toString1Line(w, bo));
      } else {
         sb.append(" undef");
      }
      int h = bo.get4(BOX_OFFSET_05_HEIGHT4);
      sb.append(',');
      sb.append(h);
      if (bo.hasFlag(BOX_OFFSET_01_FLAG, BOX_FLAG_6_DEF_HEIGHT)) {
         sb.append(drc.getLAC().getLayoutFactory().toString1Line(h, bo));
      } else {
         sb.append(" undef");
      }
   }
   //#enddebug
}
