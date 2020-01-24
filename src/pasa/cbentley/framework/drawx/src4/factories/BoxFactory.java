package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.tech.ITechByteObject;
import pasa.cbentley.core.src4.interfaces.C;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.drawx.src4.tech.ITechBox;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;
import pasa.cbentley.framework.drawx.src4.utils.ToStringStaticDraw;
import pasa.cbentley.layouter.src4.engine.LayoutOperator;
import pasa.cbentley.layouter.src4.tech.ITechLayout;
import pasa.cbentley.layouter.src4.tech.ITechPozer;
import pasa.cbentley.layouter.src4.tech.ITechSizer;

/**
 * FactoryEngine for {@link ITechBox}
 * <br>
 * Boxes constrain Figures to specific areas of the drawing rectangle
 * Creates a lightweight layout in a drawing rectangle. No concept of GUI. Just drawing
 * Simplified {@link ITechSizer} and {@link ITechPozer} in a single ByteObject of 16 bytes 
 * Complies with {@link ITechByteObject#A_OBJECT_FLAG_1_INCOMPLETE} framework.
 * Move to Layouter
 * @author Charles Bentley
 *
 */
public class BoxFactory extends AbstractDrwFactory implements ITechLayout, ITechBox {

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

   /**
    * For all 4 int parameters, -1 means undefined. Thus any -1 will set object as {@link IObject#A_OBJECT_FLAG_1_INCOMPLETE}
    * For alignments the values are : <br>
    * <li>{@link ITechFigure#ALIGN_TOP}
    * <li>{@link ITechFigure#ALIGN_BOTTOM}
    * <li>{@link ITechFigure#ALIGN_LEFT}
    * <li>{@link ITechFigure#ALIGN_RIGHT}
    * <li>{@link ITechFigure#ALIGN_CENTER}
    * <li>{@link ITechFigure#ALIGN_FILL}
    * <br>
    * <br>
    * @param ha horizontal alignment
    * @param va vertical alignment see 
    * @param w width of the box
    * @param h
    * @param perc true if sizeW and sizeH are percetages. False when they are pixel values
    * @return
    */
   public ByteObject createBox(int ha, int va, int w, int h, boolean perc) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_051_BOX, ITechBox.BOX_BASIC_SIZE);
      boolean incomplete = false;
      if (ha != BOX_UNDEFINED) {
         p.setFlag(ITechBox.BOX_OFFSET_01_FLAG, ITechBox.BOX_FLAG_7_DEF_HORIZ_ALIGN, true);
      } else {
         incomplete = true;
         ha = C.LOGIC_1_TOP_LEFT;
      }
      if (va != BOX_UNDEFINED) {
         p.setFlag(ITechBox.BOX_OFFSET_01_FLAG, ITechBox.BOX_FLAG_8_DEF_VERT_ALIGN, true);
      } else {
         incomplete = true;
         va = C.LOGIC_1_TOP_LEFT;
      }
      if (w != BOX_UNDEFINED) {
         p.setFlag(ITechBox.BOX_OFFSET_01_FLAG, ITechBox.BOX_FLAG_5_DEF_WIDTH, true);
         if (perc) {
            //create sizers
            w = sizable.codedSizeEncode(MODE_2_RATIO, w, 0, 0, 0);
         }
      } else {
         incomplete = true;
         w = 0;
      }
      if (h != BOX_UNDEFINED) {
         p.setFlag(ITechBox.BOX_OFFSET_01_FLAG, ITechBox.BOX_FLAG_6_DEF_HEIGHT, true);
         if (perc) {
            h = sizable.codedSizeEncode(MODE_2_RATIO, h, 0, 0, 0);
         }
      } else {
         incomplete = true;
         h = 0;
      }

      p.set4(ITechBox.BOX_OFFSET_03_VERTICAL_ALIGN4, va);
      p.set4(ITechBox.BOX_OFFSET_02_HORIZ_ALIGN4, ha);
      p.set4(ITechBox.BOX_OFFSET_04_WIDTH4, w);
      p.set4(ITechBox.BOX_OFFSET_05_HEIGHT4, h);
      p.setFlag(A_OBJECT_OFFSET_2_FLAG, A_OBJECT_FLAG_1_INCOMPLETE, incomplete);
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
   public ByteObject getAnchor(int ha, int va) {
      return createBox(ha, va, BOX_UNDEFINED, BOX_UNDEFINED, false);
   }

   public ByteObject getAnchor(int ha, int va, int sizeW, int sizeH) {
      return createBox(ha, va, sizeW, sizeH, false);
   }

   /**
    * Incomplete box with just H defined
   * Fill Anchor with an explicit pixel Height given. <br>
   * Width is unknown and set to zero. 
   * Will be explicity given for the drawing.
   * <br>
   * <br>
   * @param h
   * @return {@link ByteObject} of type {@link IBOTypesDrw#TYPE_051_BOX}.
   */
   public ByteObject getAnchorH(int h) {
      return createBox(-1, -1, -1, h, false);
   }

   public ByteObject getAnchorW(int w) {
      return createBox(-1, -1, w, -1, false);
   }

   public ByteObject getBoxCenter() {
      return createBox(C.LOGIC_2_CENTER, C.LOGIC_2_CENTER, BOX_UNDEFINED, BOX_UNDEFINED, false);
   }

   public ByteObject getBoxLeftCenter() {
      return createBox(C.LOGIC_1_TOP_LEFT, C.LOGIC_2_CENTER, BOX_UNDEFINED, BOX_UNDEFINED, false);
   }

   public ByteObject getBoxRightCenter() {
      return createBox(C.LOGIC_3_BOTTOM_RIGHT, C.LOGIC_2_CENTER, BOX_UNDEFINED, BOX_UNDEFINED, false);
   }

   public ByteObject getCenter(int w, int h) {
      return createBox(C.LOGIC_2_CENTER, C.LOGIC_2_CENTER, w, h, false);
   }

   public ByteObject getCenterBot() {
      return createBox(C.LOGIC_2_CENTER, C.LOGIC_3_BOTTOM_RIGHT, BOX_UNDEFINED, BOX_UNDEFINED, false);
   }

   public ByteObject getCenterTop() {
      return createBox(C.LOGIC_2_CENTER, C.LOGIC_1_TOP_LEFT, BOX_UNDEFINED, BOX_UNDEFINED, false);
   }

   public ByteObject getLeftBot(int w, int h) {
      return createBox(C.LOGIC_1_TOP_LEFT, C.LOGIC_3_BOTTOM_RIGHT, w, h, false);
   }

   public ByteObject getLeftTop(int w, int h) {
      return createBox(C.LOGIC_1_TOP_LEFT, C.LOGIC_1_TOP_LEFT, w, h, false);
   }

   public ByteObject getRightBot(int w, int h) {
      return createBox(C.LOGIC_3_BOTTOM_RIGHT, C.LOGIC_3_BOTTOM_RIGHT, w, h, false);
   }

   public ByteObject getRightTop(int w, int h) {
      return createBox(C.LOGIC_3_BOTTOM_RIGHT, C.LOGIC_1_TOP_LEFT, w, h, false);
   }

   /**
    * 
    * @param bo
    * @param sb
    * @param nl
    */
   public void toStringBox(ByteObject bo, Dctx sb) {
      sb.append("#Box ");
      sb.append("[H V]=[");
      sb.append(ToStringStaticDraw.debugAlign(bo.getValue(ITechBox.BOX_OFFSET_02_HORIZ_ALIGN4, 1)));
      if (bo.hasFlag(ITechBox.BOX_OFFSET_01_FLAG, ITechBox.BOX_FLAG_7_DEF_HORIZ_ALIGN)) {

      } else {
         sb.append(" undef");
      }
      sb.append(' ');
      sb.append(ToStringStaticDraw.debugAlign(bo.getValue(ITechBox.BOX_OFFSET_03_VERTICAL_ALIGN4, 1)));
      if (bo.hasFlag(ITechBox.BOX_OFFSET_01_FLAG, ITechBox.BOX_FLAG_8_DEF_VERT_ALIGN)) {

      } else {
         sb.append(" undef");
      }
      sb.append(']');
      int w = bo.get4(ITechBox.BOX_OFFSET_04_WIDTH4);
      sb.append(" [w,h]=");
      if (bo.hasFlag(ITechBox.BOX_OFFSET_01_FLAG, ITechBox.BOX_FLAG_5_DEF_WIDTH)) {
         //ask sizer factory for a String
         sb.append(drc.getLAC().getLayoutFactory().toString1Line(w, bo));
      } else {
         sb.append(" undef");
      }
      int h = bo.get4(ITechBox.BOX_OFFSET_05_HEIGHT4);
      sb.append(',');
      sb.append(h);
      if (bo.hasFlag(ITechBox.BOX_OFFSET_01_FLAG, ITechBox.BOX_FLAG_6_DEF_HEIGHT)) {
         sb.append(drc.getLAC().getLayoutFactory().toString1Line(h, bo));
      } else {
         sb.append(" undef");
      }

   }
}
