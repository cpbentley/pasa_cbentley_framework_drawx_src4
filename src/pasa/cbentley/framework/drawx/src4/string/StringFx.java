/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.objects.color.ColorFunction;
import pasa.cbentley.byteobjects.src4.objects.function.IBOFunction;
import pasa.cbentley.core.src4.interfaces.C;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.text.StringInterval;
import pasa.cbentley.core.src4.utils.BitUtils;
import pasa.cbentley.framework.coredraw.src4.interfaces.IMFont;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrawX;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;
import pasa.cbentley.framework.drawx.src4.ctx.ToStringStaticDrawx;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOBox;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOStrAux;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOStrAuxFx;
import pasa.cbentley.framework.drawx.src4.string.interfaces.ITechStringer;
import pasa.cbentley.layouter.src4.ctx.IBOTypesLayout;

/**
 * Java object wrapper of the ByteObject {@link IBOTypesDrawX#TYPE_DRWX_11_TEXT_EFFECTS} or {@link IBOTypesDrawX#TYPE_DRWX_00_FIGURE}  whose responsabilities are :
 * 
 * <ul>
 * <li> Manages a FX group which is the merge of several {@link IBOTypesDrawX#TYPE_DRWX_07_STRING_AUX_4_FX} definitions.
 * <li> Unwraps the values stored in a text effect definition {@link IBOStrAuxFx}.
 * <ul>
 *      <li>Font face, size and color
 *      <li>asdsa
 * </ul>
 * <li> Tracks the styles definition layers that apply to it.
 *      <ul>
 *      <li>{@link StringFx#srcFigure} is therefore never null. provide the root color and font
 *      <li>{@link StringFx#srcFigure}
 *      </ul>
 * <li>
 * </ul>
 * 
 * <p>
 * A {@link StringFx} tracks information of style for an index class. Several FX definition may apply to such a class.
 * </p>
 * 
 * <br>
 * <li><b>B</b>onjour with the B in bold will have 2 {@link StringFx} . A base {@link StringFx} defining the font and color and the {@link StringFx} specific to the B index.
 * <br>
 * All characters of a line have the base line fx.
 * <br>
 * Q: What if a Char FX is used instead of a Line FX above? A: It is merged.
 * <br>
 * 
 * <p>
 * <b>Masks</b> 
 * A {@link StringFx} may define a {@link ByteObject} mask.
 * <li> a Char mask is applied to a single character one at a time.
 * <br>
 * <br>
 * Only one mask is used.
 * </p>
 * <br>
 * <p>
 * <b>String selection</b>
 * <br>
 * <br>
 * String selection is implemented with an {@link StringInterval} of a {@link StringStyleLayer} which is rendered using an additional Text Fx.
 * <br>
 * The {@link Stringer} engine breaks down consistent style units into {@link StringFxLeaf}
 * 
 * Upon interval selection, the FX definition is merged over the given StringFX to produce new {@link StringFx}s.
 * 
 * Indeed several {@link StringFx} will be needed if the interval encompasses more than one static {@link StringFx}.
 * <br>
 * </p>
 * <br>
 * <br>
 * When {@link IBOFxStr#FX_FLAGY_7_STYLE} is used
 * <br>
 * A {@link StringFx} is font stable. One Font
 * <br>
 * @author Charles-Philip Bentley
 *
 */
public class StringFx extends ObjectDrw implements IBOStrAuxFx, ITechStringer, IBOTypesDrawX {

   public int         anchor = IBOBox.ANCHOR;

   ByteObject         bgFigure;

   /**
    * How should this figure be drawn
    * {@link ITechStringer#FX_SCOPE_1_CHAR}
    * {@link ITechStringer#FX_SCOPE_2_WORD}
    * {@link ITechStringer#FX_SCOPE_4_LINE}
    * {@link ITechStringer#FX_SCOPE_3_PARA}
    * {@link ITechStringer#FX_SCOPE_5_FRAZ}
    */
   int                bgFigureScope;

   ColorFunction      cf;

   /**
    * Main RGB color used by {@link Stringer}.
    * <br>
    * <br>
    * THis value is influenced by the following
    * <li> {@link IDrw#FIG__OFFSET_06_COLOR4}
    * <li> {@link IBOStrAuxFx#FX_OFFSET_09_COLOR4}
    * 
    */
   int                color;

   /**
    * Figure to be drawn before on the area of the line width
    */
   ByteObject         figureLineWidth;

   private int        flags;

   /**
    * Main Font used by {@link Stringer}.
    * Prevent nulls by setting default
    */
   IMFont             font   = null;

   int                fxLineExtraBetween;

   int                fxLineExtraH;

   int                fxLineExtraW;

   int                fxLineOffsetX;

   int                fxLineOffsetY;

   private ByteObject mask;

   /**
    * <li> {@link ITechStringer#FX_SCOPE_1_CHAR}
    * <li> {@link ITechStringer#FX_SCOPE_2_WORD}
    * <li> {@link ITechStringer#FX_SCOPE_4_LINE}
    */
   int                maskScope;

   /**
    * The very first Fx bo from the constructor. cannot be null
    * <br>
    * <br>
    * Its never incomplete. The merge must be done before consturction
    */
   ByteObject         srcFx;

   private Stringer   stringer;

   /**
    * 
    */
   ByteObject         tblr;

   private int        typeStruct;

   public StringFx(DrwCtx drc, Stringer st, ByteObject src) {
      super(drc);
      stringer = st;
      if (src == null) {
         throw new NullPointerException();
      }
      this.initFx(src);
   }

   /**
    * Compute the width for the character according to FX parameters
    * @param c
    * @return
    */
   public int getCharWidth(char c) {
      return font.charWidth(c) + getExtraCharWLeft() + getExtraCharWRight();
   }

   /**
    * The base color of this effect
    * @return
    */
   public int getColor() {
      return color;
   }

   public int getExtraCharHBot() {
      if (tblr == null) {
         return 0;
      } else {
         return stringer.getTBLRValueStringerArea(tblr, C.POS_1_BOT);
      }
   }

   public int getExtraCharHTop() {
      if (tblr == null) {
         return 0;
      } else {
         return stringer.getTBLRValueStringerArea(tblr, C.POS_0_TOP);
      }
   }

   /**
    * TBLR effect on each character 
    * @return
    */
   public int getExtraCharWLeft() {
      if (tblr == null) {
         return 0;
      } else {
         return stringer.getTBLRValueStringerArea(tblr, C.POS_2_LEFT);
      }
   }

   public int getExtraCharWRight() {
      if (tblr == null) {
         return 0;
      } else {
         return stringer.getTBLRValueStringerArea(tblr, C.POS_3_RIGHT);
      }
   }

   /**
    * The figure to drawn as bg over the whole interval. Specifically scoped to it.
    * @return
    */
   public ByteObject getFigureBG() {
      return bgFigure;
   }

   /**
    * not null once method
    * @return
    */
   public IMFont getFont() {
      return font;
   }

   /**
    * 
    * @return -1 if font is not defined
    */
   public int getFontHeight() {
      if (font == null) {
         return -1;
      }
      return font.getHeight();
   }

   public ByteObject getMask() {
      return mask;
   }

   /**
    * The scope of this {@link StringFx}
    * <li> {@link ITechStringer#FX_SCOPE_0_TEXT}
    * <li> {@link ITechStringer#FX_SCOPE_1_CHAR}
    * <li> {@link ITechStringer#FX_SCOPE_2_WORD}
    * @return
    */
   public int getScope() {
      return srcFx.get1(FX_OFFSET_05_SCOPE_FX1);
   }

   /**
    * The definition of this effect
    * @return
    */
   public ByteObject getSrcFx() {
      return srcFx;
   }

   /**
    * Not null when  {@link IBOStrAuxFx#FX_FLAG_4_EXTRA_SPACE_TBLR} is true
    * @return
    */
   public ByteObject getTBLR() {
      return tblr;
   }

   /**
    * Returns a value indicating the style XY structure for drawing purposes.
    * 
    * <li> {@link ITechStringer#FX_STRUCT_TYPE_0_BASIC_HORIZONTAL}
    * <li> {@link ITechStringer#FX_STRUCT_TYPE_1_METRICS_X}
    * <li> {@link ITechStringer#FX_STRUCT_TYPE_2_METRICS_XY}
    * @return
    */
   public int getTypeStruct() {
      return typeStruct;
   }

   /**
    * <li> {@link ITechStringer#FX_FLAG_01_SAME_HEIGHTS}
    * <li> {@link ITechStringer#FX_FLAG_02_HAS_LINE_VISUAL_ARTIFACTS}
    * <li> {@link ITechStringer#FX_FLAG_03_HAS_WORD_VISUALS}
    * <li> {@link ITechStringer#FX_FLAG_04_HAS_CHAR_VISUALS}
    * <li> {@link ITechStringer#FX_FLAG_05_UNSTABLE_COLOR}
    * <li> {@link ITechStringer#FX_FLAG_06_PROTECTED_CHARS}
    * @param state
    * @return
    */
   boolean hasState(int state) {
      return BitUtils.hasFlag(flags, state);
   }

   /**
    * Initializae the {@link StringFx} instance with the {@link IDrwTypes#TYPE_DRWX_11_TEXT_EFFECTS} object.
    * or a {@link IDrwTypes#TYPE_DRWX_00_FIGURE} 
    * <br>
    * <br>
    * 
    * @param fx
    */
   public void initFx(ByteObject fx) {
      //#debug
      fx.checkTypeSubType(TYPE_DRWX_07_STRING_AUX, IBOStrAux.STR_AUX_OFFSET_1_EXT_TYPE1, TYPE_DRWX_07_STRING_AUX_4_FX);
      srcFx = fx;

      mask = srcFx.getSubFirst(TYPE_DRWX_06_MASK);
      bgFigure = srcFx.getSubFirst(TYPE_DRWX_00_FIGURE);
      color = srcFx.get4(IBOStrAuxFx.FX_OFFSET_09_COLOR4);

      int offsetSubType = IBOFunction.FUN_OFFSET_09_EXTENSION_TYPE2;
      ByteObject colorFunctionDef = srcFx.getSubFirst(TYPE_021_FUNCTION);
      if (colorFunctionDef != null) {
         cf = drc.getColorFunctionFactory().createColorFunction(colorFunctionDef);
         setState(ITechStringer.FX_FLAG_05_UNSTABLE_COLOR, true);
      }
      int face = srcFx.get1(IBOStrAuxFx.FX_OFFSET_06_FACE1);
      int style = srcFx.get1(IBOStrAuxFx.FX_OFFSET_07_STYLE1);
      int size = srcFx.get1(IBOStrAuxFx.FX_OFFSET_08_SIZE1);
      font = drc.getStrAuxOperator().getFont(face, style, size);

      if (srcFx.hasFlag(FX_OFFSET_01_FLAG, FX_FLAG_4_EXTRA_SPACE_TBLR)) {
         tblr = srcFx.getSubFirst(IBOTypesLayout.FTYPE_2_TBLR);
      }
      typeStruct = FX_STRUCT_TYPE_0_BASIC_HORIZONTAL;
      bgFigureScope = srcFx.get1(FX_OFFSET_05_SCOPE_FX1);
   }

   /**
    * True when the color of this {@link StringFx} is the same for all characters
    * {@link ITechStringer}
    * @return
    */
   public boolean isColorStable() {
      return !hasState(ITechStringer.FX_FLAG_05_UNSTABLE_COLOR);
   }

   public boolean isFontMonospace() {
      return font.isMonospace();
   }

   /**
    * True is there is some masking done
    * @return
    */
   public boolean isMasked() {
      return mask != null;
   }

   public boolean isStableFont() {
      return !hasState(ITechStringer.FX_FLAG_05_UNSTABLE_COLOR);
   }

   /**
    * Returns the number of characters to be drawn
    * @param g
    * @param index the index relative to the interval of style
    * @return the number of characters impact
    */
   public int setColor(GraphicsX g, int index) {
      //color provider delegate or fixed array
      //color function
      //word color/ sentence color
      if (cf == null) {
         g.setColor(color);
         return Integer.MAX_VALUE;
      } else {
         int color = cf.fx(index);
         g.setColor(color);
         return 1;
      }
   }

   public void setFont(GraphicsX g, int count) {
      g.setFont(this.font);
   }

   void setState(int state, boolean v) {
      flags = BitUtils.setFlag(flags, state, v);
   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, StringFx.class, 412);
      dc.nl();
      dc.appendVarWithSpace("color", ToStringStaticDrawx.toStringColor(color));
      dc.appendVarWithSpace("font", ToStringStaticDrawx.toStringFontBrackets(font));

      dc.appendVarWithSpace("fxLineExtraBetween", fxLineExtraBetween);
      dc.appendVarWithSpace("fxLineExtraW", fxLineExtraW);
      dc.appendVarWithSpace("fxLineExtraH", fxLineExtraH);
      dc.appendVarWithSpace("fxLineOffsetX", fxLineOffsetX);
      dc.appendVarWithSpace("fxLineOffsetY", fxLineOffsetY);
      dc.nlLvl(bgFigure, "bgFigure");
      dc.nlLvl(srcFx, "fxDefinition");
      dc.appendVarWithSpace("getExtraCharWidth()", getExtraCharWLeft());

   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, "StringFx");
   }
   //#enddebug
}
