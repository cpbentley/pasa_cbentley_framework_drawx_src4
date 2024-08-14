package pasa.cbentley.framework.drawx.src4.anim;

import pasa.cbentley.byteobjects.src4.core.BOAbstractFactory;
import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.BOCtx;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesBOC;
import pasa.cbentley.byteobjects.src4.objects.anim.IBOAnim;
import pasa.cbentley.byteobjects.src4.objects.anim.ITechAnim;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;

public class AnimRgbImageFactory extends BOAbstractFactory implements IBOAnim, ITechAnim, ITechShiftLine {

   protected DrwCtx drc;

   public AnimRgbImageFactory(DrwCtx drc) {
      super(drc.getBOC());
      this.drc = drc;
   }

   private ByteObject createAnimAlpha() {
      return createAnim(ANIM_TYPE_06_ALPHA);
   }

   private ByteObject createAnimPixelate() {
      return createAnim(ANIM_TYPE_04_PIXELATE);
   }

   private ByteObject createAnimShiftLines() {
      return createAnim(ANIM_TYPE_05_LINE_SHIFT);
   }

   private ByteObject createAnim(int type) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesBOC.TYPE_030_ANIM, ANIM_BASIC_SIZE);
      p.set1(ANIM_OFFSET_01_TYPE1, type);
      return p;
   }

   /**
    * 
    * @param timing
    * @return
    */
   public ByteObject getAnimShiftLines(int timing) {
      ByteObject p = createAnimShiftLines();
      return p;
   }

   /**
    * 
    * @param in
    * @param leftToRight
    * @param clip
    * @return
    */
   public ByteObject getAnimShiftLine(boolean in, boolean leftToRight, boolean clip, int shiftBaseSize) {
      ByteObject t = createAnimShiftLines();
      t.setFlag(TECH_OFFSET_1_FLAG, TECH_FLAG_5CLIP, in);
      t.setFlag(TECH_OFFSET_1_FLAG, TECH_FLAG_2REVERSE, leftToRight);
      t.setFlag(TECH_OFFSET_1_FLAG, TECH_FLAG_4MULTIPLY, true);
      t.setValue(TECH_OFFSET_3_SHIFT_SIZE2, shiftBaseSize, 2);
      return t;
   }

   /**
    * Pixelize the area out or in
    * @param type according to type, pixelate in or out?
    * @param b
    * @param object
    * @return
    */
   public ByteObject getAnimPixel(boolean isOut) {
      ByteObject p = createAnimPixelate();
      p.setFlag(ANIM_OFFSET_02_FLAG, ANIM_FLAG_8_CUSTOM, isOut);
      return p;
   }
}
