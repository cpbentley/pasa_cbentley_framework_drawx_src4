/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesBOC;
import pasa.cbentley.byteobjects.src4.objects.pointer.IBOMerge;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrawX;
import pasa.cbentley.framework.drawx.src4.factories.AbstractDrwFactory;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigure;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOFigString;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOStrAuxFx;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOStrAuxFxApplicator;
import pasa.cbentley.framework.drawx.src4.string.interfaces.ITechStringer;

/**
 * 
 * @author Charles-Philip Bentley
 *
 */
public class StringAuxFxFactory extends AbstractDrwFactory implements IBOStrAuxFx, IBOTypesDrawX, IBOStrAuxFxApplicator {

   public StringAuxFxFactory(DrwCtx drc) {
      super(drc);
   }

   public void addFxApplicator(ByteObject p, ByteObject app) {
      //#debug
      app.checkType(TYPE_DRWX_07_STRING_AUX);

      p.setFlag(FX_OFFSET_03_FLAGY, FX_FLAGY_8_APPLICATOR, true);
      p.addByteObject(app);
   }

   /**
    * Add the figure FX.. what happens if there is already one ?
    * @param fx
    * @param fig
    */
   public void addFxFigure(ByteObject fx, ByteObject fig) {
      //#debug
      fig.checkType(TYPE_DRWX_00_FIGURE);

      fx.addByteObject(fig);
      fx.setFlag(FX_OFFSET_03_FLAGY, FX_FLAGY_2_FIGURE_BG, true);
   }

   public void addFxMask(ByteObject p, ByteObject mask) {
      p.setFlag(FX_OFFSET_03_FLAGY, FX_FLAGY_3_MASK, true);
      p.addByteObject(mask);
   }

   /**
    * Scope is whole text
    * 
    * Ignores the {@link IBOMerge}
    * 
    * @param textFigure
    * @return
    */
   public ByteObject createFxFromFigure(ByteObject textFigure) {
      ByteObject fx = createStrAuxFx();
      fx.setValue(FX_OFFSET_05_SCOPE_FX1, ITechStringer.FX_SCOPE_0_TEXT, 1);

      this.setColor(fx, textFigure.get4(IBOFigure.FIG__OFFSET_06_COLOR4));
      this.setFace(fx, textFigure.get1(IBOFigString.FIG_STRING_OFFSET_03_FACE1));
      this.setFontStyle(fx, textFigure.get1(IBOFigString.FIG_STRING_OFFSET_04_STYLE1));
      this.setFontSize(fx, textFigure.get1(IBOFigString.FIG_STRING_OFFSET_05_SIZE1));

      if (textFigure.hasFlag(IBOFigString.FIG_STRING_OFFSET_02_FLAGX, IBOFigString.FIG_STRING_FLAGX_2_DEFINED_FX)) {
         ByteObject fxFigStr = drc.getStrAuxOperator().getSub(textFigure, TYPE_DRWX_07_STRING_AUX_4_FX);
         if (fxFigStr != null) {
            int scope = fxFigStr.get1(FX_OFFSET_05_SCOPE_FX1);
            fx.set1(FX_OFFSET_05_SCOPE_FX1, scope);
            ByteObject mask = fxFigStr.getSubFirst(TYPE_DRWX_06_MASK);
            if (mask != null) {
               setMaskToFx(fx, mask);
            }
         }
      }
      return fx;
   }

   public ByteObject createStrAuxFx() {
      ByteObject p = drc.getStringAuxFactory().createStrAuxFx();
      return p;
   }

   public ByteObject getFxApplicator(int index) {
      ByteObject p = drc.getStringAuxFactory().createStrAuxApplicator();
      p.set2(FXA_OFFSET_02_INDEX2, index);
      return p;
   }

   /**
    * A character effect that applies to the <code>charIndex</code>
    * @param mask
    * @param charIndex Pointer to identify the characters to which to apply the effect
    * @return
    */
   public ByteObject getFxChar(ByteObject mask, int charIndex) {
      ByteObject fx = getFxEffect(ITechStringer.FX_SCOPE_1_CHAR);
      ByteObject fxApplicator = getFxApplicator(charIndex);
      addFxApplicator(fx, fxApplicator);
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
      ByteObject p = getFxEffect(ITechStringer.FX_SCOPE_1_CHAR);
      addFxMask(p, mask);
      int indexApplicator = 0; //first
      ByteObject fxa = getFxApplicator(indexApplicator);
      addFxApplicator(p, fxa);
      return p;
   }

   /**
    * Returns {@link ByteObject} of type {@link IBOTypesDrawX#TYPE_DRWX_11_TEXT_EFFECTS} with scope
    * <li> {@link ITechStringer#FX_SCOPE_1_CHAR}
    * <li> {@link ITechStringer#FX_SCOPE_2_WORD}
    * <li> {@link ITechStringer#FX_SCOPE_4_LINE}
    * <li> {@link ITechStringer#FX_SCOPE_3_PARA}
    * <li> {@link ITechStringer#FX_SCOPE_0_TEXT}
    * <li> {@link ITechStringer#FX_SCOPE_5_FRAZ}
    * <li> {@link ITechStringer#FX_SCOPE_6_SEPARATORS}
    * @param scope
    * @return
    */
   public ByteObject getFxEffect(int scope) {
      ByteObject p = createStrAuxFx();
      p.set1(FX_OFFSET_05_SCOPE_FX1, scope);
      return p;
   }

   public ByteObject getFxColorFunction(int scope, ByteObject cf) {
      ByteObject p = createStrAuxFx();
      p.set1(FX_OFFSET_05_SCOPE_FX1, scope);
      p.addByteObject(cf);
      p.setFlag(FX_OFFSET_04_FLAGZ, FX_FLAGZ_8_FUNCTION, true);
      this.setFontAllTransparent(p);
      return p;
   }

   public ByteObject getFxColor(int scope, int color) {
      ByteObject p = createStrAuxFx();
      p.set1(FX_OFFSET_05_SCOPE_FX1, scope);
      p.set4(FX_OFFSET_09_COLOR4, color);
      this.setFontTransparent(p);
      return p;
   }

   /**
    * Returns an {@link IBOStrAuxFx} with just the color defined, even scope is transparent.
    * 
    * @param scope
    * @return
    */
   public ByteObject getFxEffectColor(int color) {
      ByteObject p = createStrAuxFx();
      p.set4(FX_OFFSET_09_COLOR4, color);
      setFontTransparent(p);
      p.setFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_5_UNDEFINED_SCOPE, true);
      return p;
   }

   public ByteObject getFxEffectFontFace(int face) {
      ByteObject p = createStrAuxFx();
      p.set1(FX_OFFSET_06_FACE1, face);
      setFontTransparent(p, false, true, true, true);
      p.setFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_5_UNDEFINED_SCOPE, true);
      return p;
   }

   public ByteObject getFxEffectFontFaceStyle(int face, int style) {
      ByteObject p = createStrAuxFx();
      p.set1(FX_OFFSET_06_FACE1, face);
      p.set1(FX_OFFSET_07_STYLE1, style);
      setFontTransparent(p, false, false, true, true);
      p.setFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_5_UNDEFINED_SCOPE, true);
      return p;
   }

   /**
    * Draws the figure first on the area of the text interval
    * @param fxLine
    * @return
    */
   public ByteObject getFxFigureBg(ByteObject figure) {
      return this.getFxFigureBg(ITechStringer.FX_SCOPE_0_TEXT, figure);
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
      figure.checkType(TYPE_DRWX_00_FIGURE);
      ByteObject fx = getFxEffect(ITechStringer.FX_SCOPE_0_TEXT);
      setColor(fx, textColor);
      setFxFigure(fx, figure);
      return fx;
   }

   /**
    * Font color are transparent
    */
   public ByteObject getFxFigureBg(int scope, ByteObject figure) {
      //#debug
      figure.checkType(TYPE_DRWX_00_FIGURE);
      ByteObject fx = getFxEffect(scope);

      setFxFigure(fx, figure);
      this.setFontAllTransparent(fx);
      return fx;
   }

   public ByteObject getFxFont(int face, int style, int size, int color) {
      ByteObject p = createStrAuxFx();
      p.set1(FX_OFFSET_06_FACE1, face);
      p.set1(FX_OFFSET_07_STYLE1, style);
      p.set1(FX_OFFSET_08_SIZE1, size);
      p.set4(FX_OFFSET_09_COLOR4, color);
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
      ByteObject fx = getFxEffect(ITechStringer.FX_SCOPE_2_WORD);
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
      ByteObject fx = getFxEffect(ITechStringer.FX_SCOPE_2_WORD);
      ByteObject pointer = getFxApplicator(charIndex);
      addFxApplicator(fx, pointer);
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
      ByteObject p = createStrAuxFx();

      p.setValue(FX_OFFSET_05_SCOPE_FX1, ITechStringer.FX_SCOPE_1_CHAR, 1);

      ByteObject app = getFxApplicator(index);
      addFxApplicator(p, app);

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
      fx.setFlag(FX_OFFSET_03_FLAGY, FX_FLAGY_2_FIGURE_BG, true);
   }

   public void setFontAllTransparent(ByteObject fx) {
      setFontTransparent(fx, true, true, true, true);
   }

   /**
    * Helper method that sets the {@link IBOStrAux#}
    * @param fx
    * @param size
    */
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

   public void setFxApplicator(ByteObject fx, ByteObject app) {
      //#debug
      app.checkType(TYPE_010_POINTER);
      fx.addByteObject(app);
      fx.setFlag(FX_OFFSET_03_FLAGY, FX_FLAGY_8_APPLICATOR, true);
   }

   public void setFxFigure(ByteObject fx, ByteObject figure) {
      //#debug
      figure.checkType(TYPE_DRWX_00_FIGURE);
      fx.addByteObjectUniqueType(figure);
      fx.setFlag(FX_OFFSET_03_FLAGY, FX_FLAGY_2_FIGURE_BG, true);
   }

   public void setFxMask(ByteObject fx, ByteObject mask) {
      addFxMask(fx, mask);
   }

   /**
    * Replace first existing Mask object if any
    * @param fx
    * @param mask
    */
   public void setMaskToFx(ByteObject fx, ByteObject mask) {
      //#debug
      mask.checkType(TYPE_DRWX_06_MASK);
      fx.addByteObjectUniqueType(mask);
      fx.setFlag(FX_OFFSET_03_FLAGY, FX_FLAGY_3_MASK, true);
   }
   public void setFunctionToFx(ByteObject fx, ByteObject function) {
      //#debug
      function.checkType(IBOTypesBOC.TYPE_021_FUNCTION);
      fx.addByteObjectUniqueType(function);
      fx.setFlag(FX_OFFSET_04_FLAGZ, FX_FLAGZ_8_FUNCTION, true);
   }
}
