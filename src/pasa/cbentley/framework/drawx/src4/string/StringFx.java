/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesBOC;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.structs.IntBuffer;
import pasa.cbentley.core.src4.utils.BitUtils;
import pasa.cbentley.core.src4.utils.StringUtils;
import pasa.cbentley.framework.coredraw.src4.interfaces.IMFont;
import pasa.cbentley.framework.drawx.src4.color.ColorFunction;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;
import pasa.cbentley.framework.drawx.src4.ctx.ToStringStaticDrawx;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.tech.ITechBox;

/**
 * Manages a FX group which is the merge of several {@link IDrwTypes#TYPE_070_TEXT_EFFECTS} definitions.
 * <br>
 * <br>
 * Unwraps the values stored in a text effect definition {@link IBOFxStr}.
 * <br>
 * <br>
 * A {@link StringFx} tracks information of style for an index class. Several FX definition may apply to such a class.
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
 * String selection is implemented with an {@link StringInterval} of a {@link StringLayerStyle} which is rendered using an additional Text Fx.
 * <br>
 * The {@link Stringer} engine breaks down consistent style units into {@link StringLeaf}
 * 
 * Upon interval selection, the FX definition is merged over the given StringFX to produce new {@link StringFx}s.
 * 
 * Indeed several {@link StringFx} will be needed if the interval encompasses more than one static {@link StringFx}.
 * <br>
 * </p>
 * <br>
 * <br>
 * When {@link IBOFxStr#FX_FLAGZ_7_STYLE} is used
 * <br>
 * A {@link StringFx} is font stable. One Font
 * <br>
 * @author Charles-Philip Bentley
 *
 */
public class StringFx extends ObjectDrw implements IBOFxStr, ITechStringer, IBOTypesDrw {

   public int       anchor = ITechBox.ANCHOR;

   ByteObject       bgFigure;

   /**
    * How should this figure be drawn
    * {@link IBOFxStr#FX_SCOPE_0_CHAR}
    * {@link IBOFxStr#FX_SCOPE_1_WORD}
    * {@link IBOFxStr#FX_SCOPE_2_LINE}
    * {@link IBOFxStr#FX_SCOPE_3_PARA}
    * {@link IBOFxStr#FX_SCOPE_5_FRAZ}
    */
   int              bgFigureScope;

   /**
    * Main RGB color used by {@link Stringer}.
    * <br>
    * <br>
    * THis value is influenced by the following
    * <li> {@link IDrw#FIG__OFFSET_06_COLOR4}
    * <li> {@link IBOFxStr#FX_OFFSET_09_COLOR4}
    * <li> {@link IBOFxStr#FXLINE_FLAG_2_GRADIENT}
    * <br>
    * <br>
    * 
    */
   int              color;

   IntBuffer        dynIDs = null;

   /**
    * Main Font used by {@link Stringer}.
    * Prevent nulls by setting default
    */
   IMFont           f      = null;

   /**
    * Figure to be drawn before on the area of the line width
    */
   ByteObject       figureLineWidth;

   private int      flags;

   /**
    * By default a fxBlock force a full reapint whenever a character is modified on a given fxed block.
    */
   ByteObject       fxBlock;

   /**
    * Fx effect for every characters of this interval. Merged of all the {@link IBOFxStr#FX_SCOPE_0_CHAR}
    * definitions.
    */
   ByteObject       fxChar;

   /**
    * null when {@link StringFx} is wrapping a simple {@link IDrwTypes#TYPE_050_FIGURE}.
    * <br>
    * <br>
    * 
    */
   ByteObject       fxDefinition;

   /**
    * Special effects defined at the line.
    * <br>
    * {@link IBOFxStr#FXLINE_OFFSET_02_CHAR_X_OFFSET1}
    * {@link IBOFxStr#FXLINE_OFFSET_03_CHAR_Y_OFFSET1}
    * <br>
    * <br>
    * By default, a fxLine effect force a full repaint when any character is modified on the line.
    */
   ByteObject       fxLine;

   int              fxLineExtraBetween;

   int              fxLineExtraH;

   int              fxLineExtraW;

   int              fxLineOffsetX;

   int              fxLineOffsetY;

   /**
    * The different FX definition. starts by the root.
    * <br>
    * <br>
    * Last one is the most significant and is merged last.
    * <br>
    * <br>
    * 
    */
   ByteObject[]     fxRoots;

   /**
    * Fx for every word as broken by {@link StringUtils#getBreaksWord(char[], int, int, boolean)}
    * <br>
    * <br>
    * 
    */
   ByteObject       fxWord;

   ByteObject       maskBlock;

   /**
    * Mask object to use on characters using this Fx.
    */
   ByteObject       maskChar;

   /**
    * Mask object to use on lines
    */
   ByteObject       maskLine;

   ByteObject       maskWord;

   ByteObject       scale;

   private Stringer stringer;

   public StringFx(DrwCtx drc, Stringer st) {
      super(drc);
      stringer = st;
   }

   /**
    * Adds 
    * @param stringFx
    * @return
    */
   public StringFx add(StringFx fx) {
      fxRoots = drc.getBOC().getBOU().addByteObject(fxRoots, fx.fxDefinition);
      maskChar = fx.maskChar;
      maskLine = fx.maskLine;
      maskWord = fx.maskWord;
      bgFigure = fx.bgFigure;
      int scope = fx.fxDefinition.get1(FX_OFFSET_04_TYPE_SCOPE1);
      if (scope == FX_SCOPE_0_CHAR) {
         fxChar = drc.getBOC().getModule().merge(fxChar, fx.fxDefinition);
      } else if (scope == FX_SCOPE_1_WORD) {
         fxWord = drc.getBOC().getModule().merge(fxWord, fx.fxDefinition);
      } else if (scope == FX_SCOPE_2_LINE) {
         fxLine = drc.getBOC().getModule().merge(fxLine, fx.fxDefinition);
      }
      return this;
   }

   /**
    * 
    * @param fxLine
    */
   public void addFxLine(ByteObject fxLine) {
      if (fxLine == null) {
         this.fxLine = fxLine;
      }
   }

   public StringFx cloneMerge(StringFx stringFx) {
      return this;
   }

   /**
    * Clone this {@link StringFx} by adding this definition.
    * <br>
    * <br>
    * How does merging remove any Mask? By defining a color. A Mask is a way to drawing a color.
    * <br>
    * <br>
    * The Background figure can also be used as a bg figure when {@link IDrw#MASK_OFFSET_8_ALPHA_BG1} is not zero.
    * <br>
    * <br>
    * @param dynDef
    * @return {@link StringFx} different from this
    */
   public StringFx cloneMergeTop(ByteObject dynDef, int id) {
      StringFx fx = new StringFx(drc, stringer);
      if (dynDef.hasFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_7_INCOMPLETE)) {
         int scope = dynDef.get1(FX_OFFSET_04_TYPE_SCOPE1);
         ByteObject bo = dynDef.getSubFirst(IBOTypesBOC.TYPE_011_MERGE_MASK);
         if (bo != null) {
            ByteObject mask = dynDef.getSubFirst(TYPE_058_MASK);
            fx.maskChar = mask;
         }
      } else {
         fx.init(dynDef);
      }
      if (id != 0) {
         dynIDs.addInt(id);
      }
      return fx;
   }

   public int getColor() {
      return color;
   }

   /**
    * Effect may draw extra stuff around each characters
    * @return
    */
   public int getExtraCharWidth() {
      return 0;
   }

   public IMFont getFont() {
      return f;
   }

   public int getFontHeight() {
      if (f == null) {

      }
      return f.getHeight();
   }

   /**
    * <li>{@link ITechStringer#FX_MASKDRAW_TYPE_0_NONE}
    * <li>{@link ITechStringer#FX_MASKDRAW_TYPE_1_CHAR}
    * <li>{@link ITechStringer#FX_MASKDRAW_TYPE_2_WORD}
    * @return
    */
   public int getMaskType() {
      int type = FX_MASKDRAW_TYPE_0_NONE;
      if (maskChar != null) {
         type = FX_MASKDRAW_TYPE_1_CHAR;
      }
      if (maskWord != null) {
         type = FX_MASKDRAW_TYPE_2_WORD;
      }
      if (maskLine != null) {
         type = FX_MASKDRAW_TYPE_3_LINE;
      }
      return type;
   }

   public int getTypeStruct() {
      int type = FX_STRUCT_TYPE_0_BASIC;

      return type;
   }

   /**
    * 
    * @param id
    * @return
    */
   public boolean hasDynamicID(int id) {
      boolean has = false;
      if (dynIDs != null) {
         has = dynIDs.contains(id);
      }
      return has;
   }

   /**
    * 
    * @param state
    * @return
    */
   boolean hasState(int state) {
      return BitUtils.hasFlag(flags, state);
   }

   /**
    * Initializae the {@link StringFx} instance with the {@link IDrwTypes#TYPE_070_TEXT_EFFECTS} object.
    * or a {@link IDrwTypes#TYPE_050_FIGURE} 
    * <br>
    * <br>
    * 
    * @param text
    */
   public void init(ByteObject text) {
      //System.out.println(text);
      scale = text.getSubFirst(TYPE_055_SCALE);
      bgFigure = text.getSubFirst(TYPE_050_FIGURE);
      int type = text.getType();
      if (type == TYPE_050_FIGURE) {
         f = drc.getFxStringOperator().getStringFont(text);
         color = drc.getFxStringOperator().getStringColor(text);
      } else if (type == TYPE_070_TEXT_EFFECTS) {
         initTextEffect(text);
      } else {
         throw new IllegalArgumentException("StringFx incorrect type " + type);
      }

      //font cannot be null after this
   }

   public void initFigure(ByteObject text) {
      //#debug
      text.checkType(TYPE_050_FIGURE);
      scale = text.getSubFirst(TYPE_055_SCALE);
      bgFigure = text.getSubFirst(TYPE_050_FIGURE);
      f = drc.getFxStringOperator().getStringFont(text);
      color = drc.getFxStringOperator().getStringColor(text);
   }

   public void initFxChar() {
      //check the type of char fx. if a different fx for all chars.
      if (fxDefinition.hasFlag(IBOFxStr.FX_OFFSET_03_FLAGZ, IBOFxStr.FX_FLAGZ_3_MASK)) {
         maskChar = fxDefinition.getSubFirst(TYPE_058_MASK);
         setState(FX_FLAG_02_HAS_LINE_VISUAL_ARTIFACTS, true);
         setState(FX_FLAG_04_HAS_CHAR_VISUALS, true);
      }
      fxChar = fxDefinition;
      stringer.setState(ITechStringer.STATE_01_CHAR_EFFECTS, true);
      if (fxDefinition.hasFlag(IBOFxStr.FX_OFFSET_02_FLAGX, IBOFxStr.FX_FLAGX_6_DEFINED_INDEX)) {
         int indexs = fxDefinition.get2(FX_OFFSET_04_INDEX2);

      } else {

      }
      if (fxDefinition.hasFlag(FX_OFFSET_01_FLAG, FX_FLAG_4_EXTRA_SPACE_TBLR)) {
         ByteObject tblr = fxDefinition.getSubFirst(TYPE_060_TBLR);
      }
   }

   /**
    * 
    */
   public void initFxLine() {
      //check the type of char fx. if a different fx for all chars.
      if (fxDefinition.hasFlag(IBOFxStr.FX_OFFSET_03_FLAGZ, IBOFxStr.FX_FLAGZ_3_MASK)) {
         maskLine = fxDefinition.getSubFirst(TYPE_058_MASK);
         setState(StringFx.FX_FLAG_02_HAS_LINE_VISUAL_ARTIFACTS, true);
      }
      fxLine = fxDefinition;
      int indexs = fxDefinition.get2(FX_OFFSET_04_INDEX2);
   }

   public void initFxWord() {
      //check the type of char fx. if a different fx for all chars.
      if (fxDefinition.hasFlag(IBOFxStr.FX_OFFSET_03_FLAGZ, IBOFxStr.FX_FLAGZ_3_MASK)) {
         maskWord = fxDefinition.getSubFirst(TYPE_058_MASK);
         setState(StringFx.FX_FLAG_03_HAS_WORD_VISUALS, true);
      }
      fxWord = fxDefinition;
      int indexs = fxDefinition.get2(FX_OFFSET_04_INDEX2);
   }

   /**
    * 
    * @param fx {@link IDrwTypes#TYPE_070_TEXT_EFFECTS}
    */
   private void initTextEffect(ByteObject fx) {
      //how do you distinguish between bgFigure and fx styles?
      fxDefinition = fx;
      int scope = fxDefinition.get1(FX_OFFSET_04_TYPE_SCOPE1);
      if (fxDefinition != null) {
         fxLineExtraBetween = drc.getFxStringOperator().getLineExtraBetween(fxLine);
      } else {
         //reset all fxFields
         reset();
      }
      if (scope == FX_SCOPE_0_CHAR) {
         initFxChar();
      } else if (scope == FX_SCOPE_1_WORD) {
         initFxWord();
      } else if (scope == FX_SCOPE_2_LINE) {
         initFxLine();
      }
   }

   /**
    * True when the color of this {@link StringFx} is the same for all characters
    * {@link ITechStringer}
    * @return
    */
   public boolean isColorStable() {
      return !hasState(ITechStringer.FX_FLAG_05_UNSTABLE_COLOR);
   }

   public boolean isStableFont() {
      return !hasState(ITechStringer.FX_FLAG_05_UNSTABLE_COLOR);
   }

   private void reset() {
      fxBlock = null;
      fxLine = null;
      fxChar = null;
      fxLine = null;
      fxLineExtraBetween = 0;
   }

   //#enddebug

   ColorFunction cf;

   int           wordColorType;

   /**
    * Returns the number of characters to be drawn
    * @param g
    * @param index
    * @return
    */
   public int setColor(GraphicsX g, int index) {
      //color provider delegate or fixed array
      //color function
      //word color/ sentence color
      if (index == 0) {
         g.setColor(color);
      } else {

      }
      // TODO Auto-generated method stub
      int color = cf.fx(index);
      return 0;
   }

   void setState(int state, boolean v) {
      flags = BitUtils.setFlag(flags, state, v);
   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, StringFx.class, 412);
      dc.nl();
      dc.appendVarWithSpace("color", ToStringStaticDrawx.toStringColor(color));
      dc.appendVarWithSpace("font", ToStringStaticDrawx.debugFontBrackets(f));

      dc.appendVarWithSpace("fxLineExtraBetween", fxLineExtraBetween);
      dc.appendVarWithSpace("fxLineExtraW", fxLineExtraW);
      dc.appendVarWithSpace("fxLineExtraH", fxLineExtraH);
      dc.appendVarWithSpace("fxLineOffsetX", fxLineOffsetX);
      dc.appendVarWithSpace("fxLineOffsetY", fxLineOffsetY);
      dc.nlLvl(bgFigure, "bgFigure");
      dc.nlLvl(fxDefinition, "fxDefinition");
      dc.appendVarWithSpace("getExtraCharWidth()", getExtraCharWidth());

      dc.nlLvl(maskBlock, "maskBlock");
      dc.nlLvl(maskLine, "maskLine");
      dc.nlLvl(maskWord, "maskWord");
      dc.nlLvl(maskChar, "maskChar");

      dc.nlLvl(scale, "Scaler");
      dc.nlLvl(fxBlock, "fxBlock");
      dc.nlLvl(fxLine, "fxLine");
      dc.nlLvl(fxWord, "fxWord");
      dc.nlLvl(fxChar, "fxChar");
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, "StringFx");
   }

   public void setFont(GraphicsX g, int count) {
      // TODO Auto-generated method stub

   }

}
