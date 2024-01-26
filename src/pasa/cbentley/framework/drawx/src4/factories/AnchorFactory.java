/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.core.interfaces.IByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesDrw;
import pasa.cbentley.core.src4.interfaces.C;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.ToStringStaticDrawx;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOAnchor;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOBox;
import pasa.cbentley.framework.drawx.src4.tech.ITechAnchor;
import pasa.cbentley.layouter.src4.engine.LayoutOperator;
import pasa.cbentley.layouter.src4.tech.ITechLayout;
import pasa.cbentley.layouter.src4.tech.IBOPozer;
import pasa.cbentley.layouter.src4.tech.IBOSizer;

/**
 * 
 * {@link ITechAnchor}
 * 
 * @author Charles Bentley
 *
 */
public class AnchorFactory extends AbstractDrwFactory implements ITechAnchor {

   public AnchorFactory(DrwCtx drc) {
      super(drc);
   }

   public ByteObject getCenterCenter() {
      return getAnchor(ALIGN_6_CENTER, ALIGN_6_CENTER);
   }

   public ByteObject getLeftCenter() {
      return getAnchor(ALIGN_3_LEFT, ALIGN_6_CENTER);
   }

   public ByteObject getRightCenter() {
      return getAnchor(ALIGN_4_RIGHT, ALIGN_6_CENTER);
   }

   public ByteObject getLeftBottom() {
      return getAnchor(ALIGN_3_LEFT, ALIGN_2_BOTTOM);
   }

   public ByteObject getLeftTop() {
      return getAnchor(ALIGN_3_LEFT, ALIGN_1_TOP);
   }

   public ByteObject getRightBottom() {
      return getAnchor(ALIGN_4_RIGHT, ALIGN_2_BOTTOM);
   }

   public ByteObject getRightTop() {
      return getAnchor(ALIGN_4_RIGHT, ALIGN_1_TOP);
   }

   public ByteObject getCenterBottom() {
      return getAnchor(ALIGN_6_CENTER, ALIGN_2_BOTTOM);
   }

   public ByteObject getCenterTop() {
      return getAnchor(ALIGN_6_CENTER, ALIGN_1_TOP);
   }

   /**
    * 
    * @param root
    * @param merge
    * @return
    */
   public ByteObject mergeAnchor(ByteObject root, ByteObject merge) {
      int rootHa = root.get1(IBOAnchor.ANCHOR_OFFSET_02_HORIZ_ALIGN1);
      int rootVa = root.get1(IBOAnchor.ANCHOR_OFFSET_03_VERTICAL_ALIGN1);
      if (merge.hasFlag(IBOAnchor.ANCHOR_OFFSET_01_FLAG, IBOAnchor.ANCHOR_FLAG_7_DEF_HORIZ_ALIGN)) {
         rootHa = merge.get1(IBOAnchor.ANCHOR_OFFSET_02_HORIZ_ALIGN1);
      }
      if (merge.hasFlag(IBOAnchor.ANCHOR_OFFSET_01_FLAG, IBOAnchor.ANCHOR_FLAG_8_DEF_VERT_ALIGN)) {
         rootVa = merge.get1(IBOAnchor.ANCHOR_OFFSET_03_VERTICAL_ALIGN1);
      }
      ByteObject newAnchor = getAnchor(rootHa, rootVa);
      return newAnchor;
   }

   /**
    * For all 4 int parameters, -1 means undefined. Thus any -1 will set object as {@link IBOAnchor#ANCHOR_FLAG_1_INCOMPLETE}
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
    * Type : {@link IBOTypesDrw#TYPE_051_BOX}
    * <br>
    * <br>
    * 
    * @param ha horizontal alignment
    * @param va vertical alignment see 
    * @param w width of the box
    * @param h
    * @param perc true if sizeW and sizeH are percetages. False when they are pixel values
    * @return
    */
   public ByteObject getAnchor(int ha, int va) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_069_ANCHOR, IBOAnchor.ANCHOR_BASIC_SIZE);
      boolean incomplete = false;
      if (ha != ALIGN_0_UNDEFINED) {
         p.setFlag(IBOAnchor.ANCHOR_OFFSET_01_FLAG, IBOAnchor.ANCHOR_FLAG_7_DEF_HORIZ_ALIGN, true);
      } else {
         incomplete = true;
         ha = C.LOGIC_1_TOP_LEFT;
      }
      if (va != ALIGN_0_UNDEFINED) {
         p.setFlag(IBOAnchor.ANCHOR_OFFSET_01_FLAG, IBOAnchor.ANCHOR_FLAG_8_DEF_VERT_ALIGN, true);
      } else {
         incomplete = true;
         va = C.LOGIC_1_TOP_LEFT;
      }
      p.set1(IBOAnchor.ANCHOR_OFFSET_03_VERTICAL_ALIGN1, va);
      p.set1(IBOAnchor.ANCHOR_OFFSET_02_HORIZ_ALIGN1, ha);
      p.setFlag(IBOAnchor.ANCHOR_OFFSET_01_FLAG, IBOAnchor.ANCHOR_FLAG_1_INCOMPLETE, incomplete);
      return p;
   }

   public void toStringAnchor(ByteObject bo, Dctx sb) {
      sb.append("#Anchor ");
      sb.append("[H V]=[");
      sb.append(ToStringStaticDrawx.toStringAlign(bo.get1(IBOAnchor.ANCHOR_OFFSET_02_HORIZ_ALIGN1)));
      if (bo.hasFlag(IBOAnchor.ANCHOR_OFFSET_01_FLAG, IBOAnchor.ANCHOR_FLAG_7_DEF_HORIZ_ALIGN)) {

      } else {
         sb.append(" undef");
      }
      sb.append(' ');
      sb.append(ToStringStaticDrawx.toStringAlign(bo.get1(IBOAnchor.ANCHOR_OFFSET_03_VERTICAL_ALIGN1)));
      if (bo.hasFlag(IBOAnchor.ANCHOR_OFFSET_01_FLAG, IBOAnchor.ANCHOR_FLAG_8_DEF_VERT_ALIGN)) {

      } else {
         sb.append(" undef");
      }
      sb.append(']');
   }
}
