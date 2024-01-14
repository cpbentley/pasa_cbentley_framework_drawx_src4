/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesDrw;
import pasa.cbentley.byteobjects.src4.objects.color.ITechGradient;
import pasa.cbentley.core.src4.helpers.StringBBuilder;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.utils.interfaces.IColors;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.ToStringStaticDrawx;
import pasa.cbentley.framework.drawx.src4.tech.ITechMask;

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
    * @param filter can be null
    * @param figure can be null
    * @return
    */
   public ByteObject getMaskPreset(int type, ByteObject filter, ByteObject figure) {
      switch (type) {
         case MASK_PRESET_0HAL0:
            return getMask(-1, 0, 0, 0, 255, 0, 0, 0, filter, figure);
         case MASK_PRESET_1HAL0:
            return getMask(-1, 0, 0, 255, 0, 0, 0, 0, filter, figure);
         case MASK_PRESET_2HAL0:
            return getMask(-1, 0, 0, 0, 255, 1, 0, 0, filter, figure);
         case MASK_PRESET_3HAL0:
            return getMask(-1, 0, 0, 0, 255, 0, 1, 1, filter, figure);
         default:
            throw new IllegalArgumentException();
      }
   }

   /**
    * Gets a {@link IBOTypesDrw#TYPE_058_MASK}
    * <br>
    * <br>
    * @param colorBg {@link ITechMask#MASK_OFFSET_2_COLOR_BG4}
    * @param colorMid {@link ITechMask#MASK_OFFSET_3_COLOR_MID4}
    * @param colorShape {@link ITechMask#MASK_OFFSET_4_COLOR_SHAPE4}
    * @param alphaBg {@link ITechMask#MASK_OFFSET_5_BLEND_BG1}
    * @param alphaShape
    * @param blendBg
    * @param blendMid
    * @param blendShape
    * @param filter can be null
    * @param bgFigure can be null
    * @return
    */
   public ByteObject getMask(int colorBg, int colorMid, int colorShape, int alphaBg, int alphaShape, int blendBg, int blendMid, int blendShape, ByteObject filter, ByteObject bgFigure) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_058_MASK, MASK_BASIC_SIZE);
      p.setValue(MASK_OFFSET_2_COLOR_BG4, colorBg, 4);
      p.setValue(MASK_OFFSET_3_COLOR_MID4, colorShape, 4);
      p.setValue(MASK_OFFSET_4_COLOR_SHAPE4, colorShape, 4);
      p.setValue(MASK_OFFSET_5_BLEND_BG1, blendBg, 1);
      p.setValue(MASK_OFFSET_6_BLEND_MID1, blendMid, 1);
      p.setValue(MASK_OFFSET_7_BLEND_SHAPE1, blendShape, 1);
      p.setValue(MASK_OFFSET_8_ALPHA_BG1, alphaBg, 1);
      p.setValue(MASK_OFFSET_9_ALPHA_SHAPE1, alphaShape, 1);
      if (filter != null) {
         //#debug
         filter.checkType(IBOTypesDrw.TYPE_056_COLOR_FILTER);
         p.setFlag(MASK_OFFSET_1_FLAG1, MASK_FLAG_1_MASK_FILTER, true);
         p.addSub(filter);
      }
      if (bgFigure != null) {
         //#debug
         bgFigure.checkType(IBOTypesDrw.TYPE_050_FIGURE);
         p.setFlag(MASK_OFFSET_1_FLAG1, MASK_FLAG_2_BG_FIGURE, true);
         p.addSub(bgFigure);
      }
      return p;
   }

   /**
    * A mask whose "color" is a gradient rectangle to be applied to a regular black and white shape
    * @param color
    * @param secColor
    * @return
    */
   public ByteObject getMaskGradient(int color, int secColor) {
      ByteObject grad = drc.getGradientFactory().getGradient(secColor, 50, ITechGradient.GRADIENT_TYPE_RECT_01_HORIZ);
      ByteObject figure = drc.getFigureFactory().getFigRect(color, grad);
      ByteObject maskFilter = null;
      int colorBg = IColors.FULLY_OPAQUE_BLACK;
      int colorShape = IColors.FULLY_TRANSPARENT_BLACK;
      int colorMid = IColors.FULLY_TRANSPARENT_BLACK;
      int alphaBg = 0;
      int alphaShape = 255;
      ByteObject p = getMask(colorBg, colorMid, colorShape, alphaBg, alphaShape, maskFilter, figure);
      return p;
   }

   public ByteObject getMask(int colorBg, int colorMid, int colorShape, int alphaBg, int alphaShape, ByteObject maskFilter, ByteObject bgFigure) {
      return getMask(colorBg, colorMid, colorShape, alphaBg, alphaShape, MASK_BLEND_0, MASK_BLEND_0, MASK_BLEND_0, maskFilter, bgFigure);
   }

   public ByteObject getMask(int colorBg, int colorShape, int alphaBg, int alphaShape, ByteObject maskFilter, ByteObject bgFigure) {
      return getMask(colorBg, 0, colorShape, alphaBg, alphaShape, 0, 0, 0, maskFilter, bgFigure);
   }

   public void toStringMask(ByteObject bo, Dctx dc) {
      dc.rootN(bo, "Mask", MaskFactory.class, 110);
      dc.nl();
      StringBBuilder tru = new StringBBuilder(drc.getUCtx());
      StringBBuilder fal = new StringBBuilder(drc.getUCtx());
      ToStringStaticDrawx.debugFlagTrueOrFalse(bo, MASK_OFFSET_1_FLAG1, MASK_FLAG_6_SIZE_MASK, "hasSizeMask", tru, fal);
      dc.append("TrueFlags:");
      dc.append(tru.toString());
      dc.append("FalseFlags:");
      dc.append(fal.toString());
      dc.nl();
      dc.append("colorBg=" + ToStringStaticDrawx.toStringColor(bo.get4(MASK_OFFSET_2_COLOR_BG4)));
      dc.append(" colorMid=" + ToStringStaticDrawx.toStringColor(bo.get4(MASK_OFFSET_2_COLOR_BG4)));
      dc.append(" colorShape=" + ToStringStaticDrawx.toStringColor(bo.get4(MASK_OFFSET_4_COLOR_SHAPE4)));
      dc.nl();
      dc.append(" blendBg=" + bo.get1(MASK_OFFSET_8_ALPHA_BG1));

      dc.nl();
      dc.append(" alphaBg=" + bo.get1(MASK_OFFSET_8_ALPHA_BG1));
      dc.append(" alphaShape=" + bo.get1(MASK_OFFSET_9_ALPHA_SHAPE1));
   }
}
