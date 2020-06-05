/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.helpers.StringBBuilder;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.drawx.src4.tech.ITechMask;
import pasa.cbentley.framework.drawx.src4.utils.ToStringStaticDraw;

public class MaskFactory extends AbstractDrwFactory implements ITechMask {

   public MaskFactory(DrwCtx drc) {
      super(drc);
   }

   /**
    * 
    * @param figure
    * @param filter
    * @return
    */
   public ByteObject getMask(ByteObject figure, ByteObject filter) {
      return getMaskPreset(0, filter, figure);
   }

   /**
    * Mask presets the colors, blending and alpha value to give a preset effect.
    * @param type
    * @return
    */
   public ByteObject getMaskPreset(int type, ByteObject filter, ByteObject figure) {
      switch (type) {
         case ITechMask.MASK_PRESET_0HAL0:
            return getMask(-1, 0, 0, 0, 255, 0, 0, 0, filter, figure);
         case ITechMask.MASK_PRESET_1HAL0:
            return getMask(-1, 0, 0, 255, 0, 0, 0, 0, filter, figure);
         case ITechMask.MASK_PRESET_2HAL0:
            return getMask(-1, 0, 0, 0, 255, 1, 0, 0, filter, figure);
         case ITechMask.MASK_PRESET_3HAL0:
            return getMask(-1, 0, 0, 0, 255, 0, 1, 1, filter, figure);
         default:
            throw new IllegalArgumentException();
      }
   }

   /**
    * Gets a {@link IBOTypesDrw#TYPE_058_MASK}
    * <br>
    * <br>
    * @param colorBg {@link ITechMask#MASK_OFFSET_2COLOR_BG4}
    * @param colorMid {@link ITechMask#MASK_OFFSET_3COLOR_MID4}
    * @param colorShape {@link ITechMask#MASK_OFFSET_4COLOR_SHAPE4}
    * @param alphaBg {@link ITechMask#MASK_OFFSET_5BLEND_BG1}
    * @param alphaShape
    * @param blendBg
    * @param blendMid
    * @param blendShape
    * @param filter
    * @param bgFigure
    * @return
    */
   public ByteObject getMask(int colorBg, int colorMid, int colorShape, int alphaBg, int alphaShape, int blendBg, int blendMid, int blendShape, ByteObject filter, ByteObject bgFigure) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_058_MASK, ITechMask.MASK_BASIC_SIZE);
      p.setValue(ITechMask.MASK_OFFSET_2COLOR_BG4, colorBg, 4);
      p.setValue(ITechMask.MASK_OFFSET_3COLOR_MID4, colorShape, 4);
      p.setValue(ITechMask.MASK_OFFSET_4COLOR_SHAPE4, colorShape, 4);
      p.setValue(ITechMask.MASK_OFFSET_5BLEND_BG1, blendBg, 1);
      p.setValue(ITechMask.MASK_OFFSET_6BLEND_MID1, blendMid, 1);
      p.setValue(ITechMask.MASK_OFFSET_7BLEND_SHAPE1, blendShape, 1);
      p.setValue(ITechMask.MASK_OFFSET_8ALPHA_BG1, alphaBg, 1);
      p.setValue(ITechMask.MASK_OFFSET_9ALPHA_SHAPE1, alphaShape, 1);
      if (filter != null) {
         p.setFlag(ITechMask.MASK_OFFSET_1FLAG1, ITechMask.MASK_FLAG_1MASK_FILTER, true);
         p.addSub(filter);
      }
      if (bgFigure != null) {
         p.setFlag(ITechMask.MASK_OFFSET_1FLAG1, ITechMask.MASK_FLAG_2BG_FIGURE, true);
         p.addSub(bgFigure);
      }
      return p;
   }

   public ByteObject getMaskGradient(int color, int secColor) {
      ByteObject grad = drc.getGradientFactory().getGradient(secColor, 50);
      ByteObject figure = drc.getFigureFactory().getFigRect(color, grad);
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_058_MASK, ITechMask.MASK_BASIC_SIZE);
      return p;
   }

   public ByteObject getMask(int colorBg, int colorShape, int alphaBg, int alphaShape, ByteObject maskFilter, ByteObject bgFigure) {
      return getMask(colorBg, 0, colorShape, alphaBg, alphaShape, 0, 0, 0, maskFilter, bgFigure);
   }

   public void toStringMask(ByteObject bo, Dctx sb) {
      sb.append("#Mask ");
      sb.nl();
      StringBBuilder tru = new StringBBuilder(drc.getUCtx());
      StringBBuilder fal = new StringBBuilder(drc.getUCtx());
      ToStringStaticDraw.debugFlagTrueOrFalse(bo, ITechMask.MASK_OFFSET_1FLAG1, ITechMask.MASK_FLAG_6SIZE_MASK, "hasSizeMask", tru, fal);
      sb.append("TrueFlags:");
      sb.append(tru.toString());
      sb.append("FalseFlags:");
      sb.append(fal.toString());
      sb.nl();
      sb.append("colorBg=" + ToStringStaticDraw.toStringColor(bo.get4(ITechMask.MASK_OFFSET_2COLOR_BG4)));
      sb.append(" colorMid=" + ToStringStaticDraw.toStringColor(bo.get4(ITechMask.MASK_OFFSET_2COLOR_BG4)));
      sb.append(" colorShape=" + ToStringStaticDraw.toStringColor(bo.get4(ITechMask.MASK_OFFSET_4COLOR_SHAPE4)));
      sb.nl();
      sb.append(" blendBg=" + bo.get1(ITechMask.MASK_OFFSET_8ALPHA_BG1));

      sb.nl();
      sb.append(" alphaBg=" + bo.get1(ITechMask.MASK_OFFSET_8ALPHA_BG1));
      sb.append(" alphaShape=" + bo.get1(ITechMask.MASK_OFFSET_9ALPHA_SHAPE1));
   }
}
