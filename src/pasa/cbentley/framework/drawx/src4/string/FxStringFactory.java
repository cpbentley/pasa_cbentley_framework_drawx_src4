/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.framework.coredraw.src4.interfaces.IMFont;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.drawx.src4.factories.AbstractDrwFactory;

/**
 * Creator of {@link IBOFxStr} templates.
 * <br>
 * <br>
 * 
 * @author Charles-Philip Bentley
 *
 */
public class FxStringFactory extends AbstractDrwFactory implements IBOFxStr, IBOTypesDrw, IBOFxStrChar, IBOFxStrLine, IBOFxStrPara, IBOFxStrWord {

   public FxStringFactory(DrwCtx drc) {
      super(drc);
   }

   /**
    * Line text effect.
    * <br>
    * @param xf xf modifier from normal char width (signed byte)
    * @param yf yf modifier from normal font height (signed byte)
    * @return
    */
   public ByteObject getFxLine(int xf, int yf) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_070_TEXT_EFFECTS, FXLINE_BASIC_SIZE);
      p.setValue(FX_OFFSET_04_TYPE_SCOPE1, FX_SCOPE_2_LINE, 1);
      p.setFlag(FXLINE_OFFSET_02_CHAR_X_OFFSET1, FXLINE_FLAG_5_DEFINED_XF, true);
      p.setFlag(FXLINE_OFFSET_03_CHAR_Y_OFFSET1, FXLINE_FLAG_6_DEFINED_YF, true);
      p.setValue(FXLINE_OFFSET_02_CHAR_X_OFFSET1, xf, 1);
      p.setValue(FXLINE_OFFSET_03_CHAR_Y_OFFSET1, yf, 1);
      return p;
   }

   /**
    * Sets specific index for that style
    * <br>
    * <br>
    * @param index
    * @param style
    * @return
    */
   public ByteObject getTextEffectChar(int index, ByteObject style) {
      ByteObject p = getBOFactory().createByteObject(TYPE_070_TEXT_EFFECTS, FX_BASIC_SIZE);
      p.setValue(FX_OFFSET_04_TYPE_SCOPE1, FX_SCOPE_0_CHAR, 1);
      p.setValue(FX_OFFSET_04_INDEX2, index, 2);
      p.setValue(FX_OFFSET_05_INDEX_PATTERN1, index, 1);

      p.addByteObject(style);
      return p;
   }

   /**
    * Figures to be drawn below
    * @param bgFigure
    * @return
    */
   public ByteObject getFxChar(ByteObject[] bgFigure, int[] indexes) {
      ByteObject p = getBOFactory().createByteObject(TYPE_070_TEXT_EFFECTS, FXCHAR_BASIC_SIZE);
      p.setValue(FX_OFFSET_04_TYPE_SCOPE1, FX_SCOPE_0_CHAR, 1);
      return p;
   }

   public ByteObject getFxChar(int type) {
      ByteObject p = getBOFactory().createByteObject(TYPE_070_TEXT_EFFECTS, FXCHAR_BASIC_SIZE);
      p.setValue(FX_OFFSET_04_TYPE_SCOPE1, FX_SCOPE_0_CHAR, 1);
      return p;
   }

   /**
    * Draws the figure first on the area of the text interval
    * @param fxLine
    * @return
    */
   public ByteObject getFxFigureBg(ByteObject figure) {
      //#debug
      figure.checkType(TYPE_050_FIGURE);
      ByteObject fx = getFx(FX_SCOPE_4_TEXT);

      setFxFigureBg(fx, figure);
      return fx;
   }

   public void setColor(ByteObject fx, int color) {
      fx.setFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_5_DEFINED_COLOR, true);
      fx.set4(FX_OFFSET_09_COLOR4, color);
   }

   /**
    * Draws the figure first on the area of the text interval and override
    * the text color with the given one.
    * @param figure
    * @param textColor
    * @return
    */
   public ByteObject getFxFigureBg(ByteObject figure, int textColor) {
      //#debug
      figure.checkType(TYPE_050_FIGURE);
      ByteObject fx = getFx(FX_SCOPE_4_TEXT);
      setColor(fx, textColor);
      setFxFigureBg(fx, figure);
      return fx;
   }

   /**
    * Create a Mask FX with the given scope
    * @param mask
    * @param scope
    * @return
    */
   public ByteObject getFxMask(ByteObject mask, int scope) {
      ByteObject p = getFx(scope);
      p.setFlag(FX_OFFSET_03_FLAGZ, FX_FLAGZ_3_MASK, true);
      p.addByteObject(mask);
      return p;
   }

   /**
    * Alternating char level fx for all characters.
    * <br>
    * <br>
    * Each char has a different color/font/fx
    * <br>
    * <br>
    * @param fxs
    * @return
    */
   public ByteObject getFxChar(ByteObject[] fxs) {
      ByteObject p = getBOFactory().createByteObject(TYPE_070_TEXT_EFFECTS, FXCHAR_BASIC_SIZE);
      p.setValue(FX_OFFSET_04_TYPE_SCOPE1, FX_SCOPE_0_CHAR, 1);
      p.addByteObject(fxs);
      return p;
   }

   public ByteObject getFxChar(ByteObject[] masks, IMFont[] fonts, int[] indexes) {
      ByteObject p = getBOFactory().createByteObject(TYPE_070_TEXT_EFFECTS, FXCHAR_BASIC_SIZE);
      p.setValue(FX_OFFSET_04_TYPE_SCOPE1, FX_SCOPE_0_CHAR, 1);
      return p;
   }

   public ByteObject getFxChar(ByteObject[] masks, IMFont[] fonts) {
      ByteObject p = getBOFactory().createByteObject(TYPE_070_TEXT_EFFECTS, FXCHAR_BASIC_SIZE);
      p.setValue(FX_OFFSET_04_TYPE_SCOPE1, FX_SCOPE_0_CHAR, 1);
      return p;
   }

   public void setFxMask(ByteObject fx, ByteObject mask) {
      //#debug
      mask.checkType(TYPE_058_MASK);
      fx.addByteObject(mask);
      fx.setFlag(FX_OFFSET_03_FLAGZ, FX_FLAGZ_3_MASK, true);
   }

   public void setFxPointer(ByteObject fx, ByteObject pointer) {
      //#debug
      pointer.checkType(TYPE_010_POINTER);
      fx.addByteObject(pointer);
      fx.setFlag(FX_OFFSET_03_FLAGZ, FX_FLAGZ_8_POINTER, true);
   }

   public void setFxFigureBg(ByteObject fx, ByteObject figure) {
      //#debug
      figure.checkType(TYPE_058_MASK);
      fx.addByteObject(figure);
      fx.setFlag(FX_OFFSET_03_FLAGZ, FX_FLAGZ_2_FIGURE, true);
   }

   /**
    * 
    * @param mask
    * @return
    */
   public ByteObject getFxChar(ByteObject mask) {
      ByteObject p = getBOFactory().createByteObject(TYPE_070_TEXT_EFFECTS, FXCHAR_BASIC_SIZE);
      p.setValue(FX_OFFSET_04_TYPE_SCOPE1, FX_SCOPE_0_CHAR, 1);
      return p;
   }

   /**
    * A character effect that applies to the <code>charIndex</code>
    * @param mask
    * @param charIndex Pointer to identify the characters to which to apply the effect
    * @return
    */
   public ByteObject getFxChar(ByteObject mask, int charIndex) {
      ByteObject fx = getFx(FX_SCOPE_0_CHAR);
      ByteObject pointer = boc.getPointerFactory().getPointer(charIndex, 1);
      setFxPointer(fx, pointer);
      return fx;
   }

   /**
    * 
    * @param mask
    * @param charIndex
    * @return
    */
   public ByteObject getFxWord(ByteObject mask, int charIndex) {
      ByteObject fx = getFx(FX_SCOPE_1_WORD);
      ByteObject pointer = boc.getPointerFactory().getPointer(charIndex, 1);
      setFxPointer(fx, pointer);
      return fx;
   }

   /**
    * A character effect that applies to the first character of the parent scope.
    * 
    * <li> When applied to words, every first characters of word
    * <li> When applied to lines, every first characters of a line.
    * <li> When applied to sentences, every first characters of a sentence.
    * <br>
    * <br>
    * What is the parent scope?
    * @param mask
    * @return
    */
   public ByteObject getFxCharFirst(ByteObject mask) {
      ByteObject fx= getFxChar(mask, 0);
      fx.setFlag(FX_OFFSET_01_FLAG, FX_FLAGX_7_INCOMPLETE, true);
      fx.setFlag(FX_OFFSET_01_FLAG, FX_FLAGX_6_DEFINED_INDEX, true);

      fx.setFlag(FX_OFFSET_01_FLAG, FX_FLAGX_4_DEFINED_FONT, false);
      fx.setFlag(FX_OFFSET_01_FLAG, FX_FLAGX_5_DEFINED_COLOR, false);
      return fx;
   }

   public ByteObject getFx(int scope) {
      int size = FX_BASIC_SIZE;
      switch (scope) {
         case FX_SCOPE_0_CHAR:
            size = FXCHAR_BASIC_SIZE;
            break;
         case FX_SCOPE_1_WORD:
            size = FXWORD_BASIC_SIZE;
            break;
         case FX_SCOPE_2_LINE:
            size = FXLINE_BASIC_SIZE;
            break;
         case FX_SCOPE_3_PARA:
            size = FXPARA_BASIC_SIZE;
            break;
         case FX_SCOPE_4_TEXT:
            size = FXTEXT_BASIC_SIZE;
            break;
         case FX_SCOPE_5_FRAZ:
            size = FXFRAZ_BASIC_SIZE;
            break;
         default:
            break;
      }
      ByteObject p = getBOFactory().createByteObject(TYPE_070_TEXT_EFFECTS, size);
      p.set1(FX_OFFSET_04_TYPE_SCOPE1, scope);
      return p;
   }

}
