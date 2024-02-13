/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesDrw;
import pasa.cbentley.byteobjects.src4.objects.pointer.IBOMergeMask;
import pasa.cbentley.framework.coredraw.src4.interfaces.IMFont;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.factories.AbstractDrwFactory;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigString;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigure;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOFxApplicator;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOFxStr;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOFxStrChar;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOFxStrLine;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOFxStrPara;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOFxStrWord;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;

/**
 * Creator of {@link IBOFxStr} templates.
 * <br>
 * <br>
 * 
 * @author Charles-Philip Bentley
 *
 */
public class FxStringFactory extends AbstractDrwFactory implements IBOFxStr, IBOTypesDrw, IBOFxApplicator, IBOFxStrChar, IBOFxStrLine, IBOFxStrPara, IBOFxStrWord {

   public FxStringFactory(DrwCtx drc) {
      super(drc);
   }

   /**
    * Add the figure FX.. what happens if there is already one ?
    * @param fx
    * @param fig
    */
   public void addFxFigure(ByteObject fx, ByteObject fig) {
      //#debug
      fig.checkType(TYPE_050_FIGURE);
      
      fx.addByteObject(fig);
      fx.setFlag(FX_OFFSET_03_FLAGY, FX_FLAGY_2_FIGURE, true);
   }

   public void addFxApp(ByteObject p, ByteObject app) {
      //#debug
      app.checkType(TYPE_010_POINTER);
      
      p.setFlag(FX_OFFSET_03_FLAGY, FX_FLAGY_8_APPLICATOR, true);
      p.addByteObject(app);
   }

   public void addFxMask(ByteObject p, ByteObject mask) {
      p.setFlag(FX_OFFSET_03_FLAGY, FX_FLAGY_3_MASK, true);
      p.addByteObject(mask);
   }

   /**
    * Scope is whole text
    * 
    * Ignores the {@link IBOMergeMask}
    * 
    * @param textFigure
    * @return
    */
   public ByteObject createFxFromFigure(ByteObject textFigure) {
      ByteObject fx = getBOFactory().createByteObject(TYPE_070_TEXT_EFFECTS, FX_BASIC_SIZE_TEXT);
      fx.setValue(FX_OFFSET_05_SCOPE_FX1, FX_SCOPE_0_TEXT, 1);

      this.setColor(fx, textFigure.get4(IBOFigure.FIG__OFFSET_06_COLOR4));
      this.setFace(fx, textFigure.get1(IBOFigString.FIG_STRING_OFFSET_03_FACE1));
      this.setFontStyle(fx, textFigure.get1(IBOFigString.FIG_STRING_OFFSET_04_STYLE1));
      this.setFontSize(fx, textFigure.get1(IBOFigString.FIG_STRING_OFFSET_05_SIZE1));

      if(textFigure.hasFlag(IBOFigString.FIG_STRING_OFFSET_02_FLAGX, IBOFigString.FIG_STRING_FLAGX_2_DEFINED_FX)) {
         ByteObject fxFigStr = textFigure.getSubFirst(TYPE_070_TEXT_EFFECTS);
         if(fxFigStr != null) {
            int scope = fxFigStr.get1(FX_OFFSET_05_SCOPE_FX1);
            fx.set1(FX_OFFSET_05_SCOPE_FX1, scope);
            ByteObject mask = fxFigStr.getSubFirst(TYPE_058_MASK);
            if(mask != null) {
               setMaskToFx(fx, mask);
            }
         }
      }
      return fx;
   }

   public ByteObject getFxApplicator(int index) {
      ByteObject p = getBOFactory().createByteObject(TYPE_072_FX_APPLICATOR, FXA_BASIC_SIZE);

      p.set2(FXA_OFFSET_02_INDEX2, index);
      return p;
   }

   /**
    * A Text effect applied on the char
    * @param mask
    * @return
    */
   public ByteObject getFxChar(ByteObject mask) {
      ByteObject p = getBOFactory().createByteObject(TYPE_070_TEXT_EFFECTS, FXCHAR_BASIC_SIZE);
      p.setValue(FX_OFFSET_05_SCOPE_FX1, FX_SCOPE_1_CHAR, 1);
      return p;
   }

   /**
    * A character effect that applies to the <code>charIndex</code>
    * @param mask
    * @param charIndex Pointer to identify the characters to which to apply the effect
    * @return
    */
   public ByteObject getFxChar(ByteObject mask, int charIndex) {
      ByteObject fx = getFxEffect(FX_SCOPE_1_CHAR);
      ByteObject pointer = getFxApplicator(charIndex);
      addFxApp(fx, pointer);
      return fx;
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
      p.setValue(FX_OFFSET_05_SCOPE_FX1, FX_SCOPE_1_CHAR, 1);
      p.addByteObject(fxs);
      return p;
   }

   public ByteObject getFxChar(ByteObject[] masks, IMFont[] fonts) {
      ByteObject p = getBOFactory().createByteObject(TYPE_070_TEXT_EFFECTS, FXCHAR_BASIC_SIZE);
      p.setValue(FX_OFFSET_05_SCOPE_FX1, FX_SCOPE_1_CHAR, 1);
      return p;
   }

   public ByteObject getFxChar(ByteObject[] masks, IMFont[] fonts, int[] indexes) {
      ByteObject p = getBOFactory().createByteObject(TYPE_070_TEXT_EFFECTS, FXCHAR_BASIC_SIZE);
      p.setValue(FX_OFFSET_05_SCOPE_FX1, FX_SCOPE_1_CHAR, 1);
      return p;
   }

   /**
    * Figures to be drawn below
    * @param bgFigure
    * @return
    */
   public ByteObject getFxChar(ByteObject[] bgFigure, int[] indexes) {
      ByteObject p = getBOFactory().createByteObject(TYPE_070_TEXT_EFFECTS, FXCHAR_BASIC_SIZE);
      p.setValue(FX_OFFSET_05_SCOPE_FX1, FX_SCOPE_1_CHAR, 1);
      return p;
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
      ByteObject p = getFxEffect(FX_SCOPE_1_CHAR);
      addFxMask(p, mask);
      int indexApplicator = 0; //first
      ByteObject fxa = getFxApplicator(indexApplicator);
      addFxApp(p, fxa);
      return p;
   }

   public ByteObject getFxFont(int face, int style, int size, int color) {
      ByteObject p = getBOFactory().createByteObject(TYPE_070_TEXT_EFFECTS, FX_BASIC_SIZE);
      p.set1(FX_OFFSET_06_FACE1, face);
      p.set1(FX_OFFSET_07_STYLE1, style);
      p.set1(FX_OFFSET_08_SIZE1, size);
      p.set4(FX_OFFSET_09_COLOR4, color);
      return p;
   }
   /**
    * Returns {@link ByteObject} of type {@link IBOTypesDrw#TYPE_070_TEXT_EFFECTS} with scope
    * <li> {@link IBOFxStr#FX_SCOPE_1_CHAR}
    * <li> {@link IBOFxStr#FX_SCOPE_2_WORD}
    * <li> {@link IBOFxStr#FX_SCOPE_2_LINE}
    * <li> {@link IBOFxStr#FX_SCOPE_3_PARA}
    * <li> {@link IBOFxStr#FX_SCOPE_0_TEXT}
    * <li> {@link IBOFxStr#FX_SCOPE_5_FRAZ}
    * <li> {@link IBOFxStr#FX_SCOPE_6_SEPARATORS}
    * @param scope
    * @return
    */
   public ByteObject getFxEffect(int scope) {
      int size = FX_BASIC_SIZE;
      ByteObject p = getBOFactory().createByteObject(TYPE_070_TEXT_EFFECTS, size);
      p.set1(FX_OFFSET_05_SCOPE_FX1, scope);
      return p;
   }

   /**
    * Returns a Fx with just the color defined, even scope is transparent
    * @param scope
    * @return
    */
   public ByteObject getFxEffectColor(int color) {
      ByteObject p = getBOFactory().createByteObject(TYPE_070_TEXT_EFFECTS, FX_BASIC_SIZE);
      p.set4(FX_OFFSET_09_COLOR4, color);
      setFontTransparent(p);
      p.setFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_5_UNDEFINED_SCOPE, true);
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
      ByteObject fx = getFxEffect(FX_SCOPE_0_TEXT);

      setFxFigure(fx, figure);
      return fx;
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
      ByteObject fx = getFxEffect(FX_SCOPE_0_TEXT);
      setColor(fx, textColor);
      setFxFigure(fx, figure);
      return fx;
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
      p.setValue(FX_OFFSET_05_SCOPE_FX1, FX_SCOPE_2_LINE, 1);
      p.setFlag(FXLINE_OFFSET_02_CHAR_X_OFFSET1, FXLINE_FLAG_5_DEFINED_XF, true);
      p.setFlag(FXLINE_OFFSET_03_CHAR_Y_OFFSET1, FXLINE_FLAG_6_DEFINED_YF, true);
      p.setValue(FXLINE_OFFSET_02_CHAR_X_OFFSET1, xf, 1);
      p.setValue(FXLINE_OFFSET_03_CHAR_Y_OFFSET1, yf, 1);
      return p;
   }

   /**
    * Create a Mask FX with the given scope
    * @param mask
    * @param scope
    * @return
    */
   public ByteObject getFxMask(ByteObject mask, int scope) {
      ByteObject p = getFxEffect(scope);
      p.setFlag(FX_OFFSET_03_FLAGY, FX_FLAGY_3_MASK, true);
      p.addByteObject(mask);
      return p;
   }

   /**
    * Every word will be masked individually
    * @param mask
    * @return
    */
   public ByteObject getFxWord(ByteObject mask) {
      ByteObject fx = getFxEffect(FX_SCOPE_2_WORD);
      setFxMask(fx, mask);
      return fx;
   }

   /**
    * 
    * @param mask
    * @param charIndex
    * @return
    */
   public ByteObject getFxWord(ByteObject mask, int charIndex) {
      ByteObject fx = getFxEffect(FX_SCOPE_2_WORD);
      ByteObject pointer = getFxApplicator(charIndex);
      addFxApp(fx, pointer);
      return fx;
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
      p.setValue(FX_OFFSET_05_SCOPE_FX1, FX_SCOPE_1_CHAR, 1);

      ByteObject app = getFxApplicator(index);
      addFxApp(p, app);

      p.addByteObject(style);
      return p;
   }

   public void setColor(ByteObject fx, int color) {
      fx.set4(FX_OFFSET_09_COLOR4, color);
      fx.setFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_4_UNDEFINED_COLOR, false);
   }

   public void setFace(ByteObject fx, int face) {
      fx.set1(FX_OFFSET_06_FACE1, face);
      fx.setFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_1_UNDEFINED_FONT_FACE, false);
      
   }

   public void setFigureToFx(ByteObject fx, ByteObject fig) {
      fx.addByteObjectUniqueType(fig);
      fx.setFlag(FX_OFFSET_03_FLAGY, FX_FLAGY_2_FIGURE, true);
   }

   public void setFontSize(ByteObject fx, int size) {
      fx.set1(FX_OFFSET_08_SIZE1, size);
      fx.setFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_3_UNDEFINED_FONT_SIZE, false);
   }

   public void setFontStyle(ByteObject fx, int style) {
      fx.set1(FX_OFFSET_07_STYLE1, style);
      fx.setFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_2_UNDEFINED_FONT_STYLE, false);
   }

   /**
    * Set flags so that the font face/style/size values are transparent for the purpose of merging
    * 
    * @param fx
    */
   public void setFontTransparent(ByteObject fx) {
      setFontTransparent(fx, true, true, true, false);
   }

   public void setFontTransparent(ByteObject fx, boolean isFaceTrans, boolean isStyleTrans, boolean isSizeTrans, boolean isColorTrans) {
      fx.setFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_1_UNDEFINED_FONT_FACE, isFaceTrans);
      fx.setFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_2_UNDEFINED_FONT_STYLE, isStyleTrans);
      fx.setFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_3_UNDEFINED_FONT_SIZE, isSizeTrans);
      fx.setFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_4_UNDEFINED_COLOR, isColorTrans);
      boolean isIncomplete = isFaceTrans | isStyleTrans | isSizeTrans | isColorTrans;
      fx.setFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_8_INCOMPLETE, isIncomplete);
   }

   public void setFxFigure(ByteObject fx, ByteObject figure) {
      //#debug
      figure.checkType(TYPE_050_FIGURE);
      fx.addByteObjectUniqueType(figure);
      fx.setFlag(FX_OFFSET_03_FLAGY, FX_FLAGY_2_FIGURE, true);
   }

   public void setFxMask(ByteObject fx, ByteObject mask) {
      addFxMask(fx, mask);
   }

   public void setFxApplicator(ByteObject fx, ByteObject app) {
      //#debug
      app.checkType(TYPE_010_POINTER);
      fx.addByteObject(app);
      fx.setFlag(FX_OFFSET_03_FLAGY, FX_FLAGY_8_APPLICATOR, true);
   }

   /**
    * Replace first existing Mask object if any
    * @param fx
    * @param mask
    */
   public void setMaskToFx(ByteObject fx, ByteObject mask) {
      //#debug
      mask.checkType(TYPE_058_MASK);
      fx.addByteObjectUniqueType(mask);
      fx.setFlag(FX_OFFSET_03_FLAGY, FX_FLAGY_3_MASK, true);
   }

}
