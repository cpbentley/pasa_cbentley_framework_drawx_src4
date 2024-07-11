/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.style;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.utils.BitUtils;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrawX;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;
import pasa.cbentley.layouter.src4.interfaces.ILayoutable;
import pasa.cbentley.layouter.src4.tech.IBOTblr;

/**
 * The context for sizing style values is the {@link ILayoutable}
 * 
 * @author Charles Bentley
 *
 */
public class StyleCache extends ObjectDrw implements IBOStyle, IBOTblr, IBOTypesDrawX, ITechStyleCache {

   private int[]         areas;

   private ILayoutable   ctx;

   private int           flagsValidity;

   protected boolean     isValidStyleAreas;

   private ByteObject    style;

   private int           styleHAll;

   private int           styleHAllBot;

   private int           styleHAllTop;

   private int           styleHBorder;

   private int           styleHBorderBot;

   private int           styleHBorderTop;

   private int           styleHMargin;

   private int           styleHMarginBot;

   private int           styleHMarginTop;

   private int           styleHPadding;

   private int           styleHPaddingBot;

   private int           styleHPaddingTop;

   private StyleOperator styleOp;

   private int           styleWAll;

   private int           styleWAllLeft;

   private int           styleWAllRite;

   private int           styleWBorder;

   private int           styleWBorderLeft;

   private int           styleWBorderRite;

   private int           styleWMargin;

   private int           styleWMarginLeft;

   private int           styleWMarginRite;

   private int           styleWPadding;

   private int           styleWPaddingLeft;

   private int           styleWPaddingRite;

   /**
    * 
    * @param gc
    * @param d
    * @param ctx
    */
   public StyleCache(DrwCtx dc, ILayoutable ctx, ByteObject style) {
      super(dc);
      if (ctx == null) {
         throw new NullPointerException();
      }
      this.ctx = ctx;
      if (style == null) {
         throw new NullPointerException();
      }
      this.style = style;
      this.styleOp = dc.getStyleOperator();
   }

   private void appendCache(Dctx dc, String name, int val, int flag) {
      dc.appendVarWithSpace(name, val);
      dc.appendBracketedWithSpace(isValueValid(flag));
   }

   public void computeStyleDimensions(int w, int h) {
      int r = ITechStyleCache.RELATIVE_TYPE_0_MARGIN;
      this.computeStyleDimensions(w, h, r, r, r, r);
   }

   public void computeStyleDimensions(int w, int h, int typeRelativeW, int typeRelativeH, int typeRelativeX, int typeRelativeY) {
      int x = 0;
      int y = 0;
      areas = styleOp.getStyleAreas(x, y, w, h, style, ctx, this, typeRelativeW, typeRelativeH, typeRelativeX, typeRelativeY);
   }

   public ILayoutable getLayoutable() {
      return ctx;
   }

   public ByteObject getStyle() {
      return style;
   }

   /**
    * Returns a direct reference to the array of {@link StyleCache}.
    * Must be called after compute.
    * 
    * provides x y diff values and w,h for margin,border,pad content areas
    * 
    * Directly related to {@link IBOStyle#STYLE_OFFSET_5_BG_POINTS1}
    * 
    * That 4*4 values = 16
    * 
    * <li>0-3 = border rectangle -> {@link IBOStyle#STYLE_ANC_0_BORDER}
    * <li>4-7 = margin rectanble -> {@link IBOStyle#STYLE_ANC_1_MARGIN}
    * <li>8-11 = content rectangle -> {@link IBOStyle#STYLE_ANC_2_CONTENT}
    * <li>12-15 = padding rectangle -> {@link IBOStyle#STYLE_ANC_3_PADDING}
    * 
    * @return cannot be null.
    */
   public int[] getStyleAreas() {
      if (areas == null) {
         //#debug
         toDLog().pNull("", this, StyleCache.class, "getStyleAreas@142", LVL_05_FINE, false);
         throw new NullPointerException("computeStyleDimensions has not been called");
      }
      return areas;
   }

   public int getStyleHAll() {
      if (isValueInvalid(SC_FLAG_02_HEIGHT_ALL)) {
         styleHAll = getStyleHBotAll() + getStyleHTopAll();
         setValueValid(SC_FLAG_02_HEIGHT_ALL);
      }
      return styleHAll;
   }

   public int getStyleHBorder() {
      if (isValueInvalid(SC_FLAG_25_BORDER_HEIGHT)) {
         styleHBorder = getStyleHBorderBot() + getStyleHBorderTop();
         setValueValid(SC_FLAG_25_BORDER_HEIGHT);
      }
      return styleHBorder;
   }

   public int getStyleHBorderBot() {
      if (isValueInvalid(SC_FLAG_15_BORDER_BOT)) {
         styleHBorderBot = styleOp.getStyleBorderBot(style, ctx);
         setValueValid(SC_FLAG_15_BORDER_BOT);
      }
      return styleHBorderBot;
   }

   public int getStyleHBorderMargin() {
      return getStyleHBorder() + getStyleHMargin();
   }

   public int getStyleHBorderTop() {
      if (isValueInvalid(SC_FLAG_14_BORDER_TOP)) {
         styleHBorderTop = styleOp.getStyleBorderTop(style, ctx);
         setValueValid(SC_FLAG_14_BORDER_TOP);
      }
      return styleHBorderTop;
   }

   public int getStyleHBotAll() {
      if (isValueInvalid(SC_FLAG_06_HEIGHT_BOT)) {
         styleHAllBot = getStyleHBorderBot() + getStyleHPaddingBot() + getStyleHMarginBot();
         setValueValid(SC_FLAG_06_HEIGHT_BOT);
      }
      return styleHAllBot;
   }

   public int getStyleHMargin() {
      if (isValueInvalid(SC_FLAG_27_MARGIN_HEIGHT)) {
         styleHMargin = getStyleHMarginBot() + getStyleHMarginTop();
         setValueValid(SC_FLAG_27_MARGIN_HEIGHT);
      }
      return styleHMargin;
   }

   public int getStyleHMarginBot() {
      if (isValueInvalid(SC_FLAG_19_MARGIN_BOT)) {
         styleHMarginBot = styleOp.getStyleMarginBot(style, ctx);
         setValueValid(SC_FLAG_19_MARGIN_BOT);
      }
      return styleHMarginBot;
   }

   public int getStyleHMarginTop() {
      if (isValueInvalid(SC_FLAG_18_MARGIN_TOP)) {
         styleHMarginTop = styleOp.getStyleMarginTop(style, ctx);
         setValueValid(SC_FLAG_18_MARGIN_TOP);
      }
      return styleHMarginTop;
   }

   public int getStyleHPadding() {
      if (isValueInvalid(SC_FLAG_23_PADDING_HEIGHT)) {
         styleHPadding = getStyleHPaddingBot() + getStyleHPaddingTop();
         setValueValid(SC_FLAG_23_PADDING_HEIGHT);
      }
      return styleHPadding;
   }

   public int getStyleHPaddingBorder() {
      return getStyleHPadding() + getStyleHBorder();
   }

   public int getStyleHPaddingBorderMargin() {
      return getStyleHPadding() + getStyleHBorder() + getStyleHMargin();
   }

   public int getStyleHPaddingBot() {
      if (isValueInvalid(SC_FLAG_11_PADDING_BOT)) {
         styleHPaddingBot = styleOp.getStylePaddingBot(style, ctx);
         setValueValid(SC_FLAG_11_PADDING_BOT);
      }
      return styleHPaddingBot;
   }

   public int getStyleHPaddingTop() {
      if (isValueInvalid(SC_FLAG_10_PADDING_TOP)) {
         styleHPaddingTop = styleOp.getStylePaddingTop(style, ctx);
         setValueValid(SC_FLAG_10_PADDING_TOP);
      }
      return styleHPaddingTop;
   }

   public int getStyleHTopAll() {
      if (isValueInvalid(SC_FLAG_05_HEIGHT_TOP)) {
         styleHAllTop = getStyleHMarginTop() + getStyleHBorderTop() + getStyleHPaddingTop();
         setValueValid(SC_FLAG_05_HEIGHT_TOP);
      }
      return styleHAllTop;
   }

   public int getStyleWAll() {
      if (isValueInvalid(SC_FLAG_01_WIDTH_ALL)) {
         styleWAll = getStyleWLeftAll() + getStyleWRightAll();
         setValueValid(SC_FLAG_01_WIDTH_ALL);
      }
      return styleWAll;
   }

   public int getStyleWBorder() {
      if (isValueInvalid(SC_FLAG_24_BORDER_WIDTH)) {
         styleWBorder = getStyleWBorderLeft() + getStyleWBorderRite();
         setValueValid(SC_FLAG_24_BORDER_WIDTH);
      }
      return styleWBorder;
   }

   public int getStyleWBorderLeft() {
      if (isValueInvalid(SC_FLAG_16_BORDER_LEFT)) {
         styleWBorderLeft = styleOp.getStyleBorderLeft(style, ctx);
         setValueValid(SC_FLAG_16_BORDER_LEFT);
      }
      return styleWBorderLeft;
   }

   public int getStyleWBorderMargin() {
      return getStyleWBorder() + getStyleWMargin();
   }

   public int getStyleWBorderRite() {
      if (isValueInvalid(SC_FLAG_17_BORDER_RITE)) {
         styleWBorderRite = styleOp.getStyleBorderRite(style, ctx);
         setValueValid(SC_FLAG_17_BORDER_RITE);
      }
      return styleWBorderRite;
   }

   public int getStyleWLeftAll() {
      if (isValueInvalid(SC_FLAG_03_WIDTH_LEFT)) {
         styleWAllLeft = getStyleWBorderLeft() + getStyleWMarginLeft() + getStyleWPaddingLeft();
         setValueValid(SC_FLAG_03_WIDTH_LEFT);
      }
      return styleWAllLeft;
   }

   public int getStyleWMargin() {
      if (isValueInvalid(SC_FLAG_26_MARGIN_WIDTH)) {
         styleWMargin = getStyleWMarginLeft() + getStyleWMarginRite();
         setValueValid(SC_FLAG_26_MARGIN_WIDTH);
      }
      return styleWMargin;
   }

   public int getStyleWMarginLeft() {
      if (isValueInvalid(SC_FLAG_20_MARGIN_LEFT)) {
         styleWMarginLeft = styleOp.getStyleMarginLeft(style, ctx);
         setValueValid(SC_FLAG_20_MARGIN_LEFT);
      }
      return styleWMarginLeft;
   }

   public int getStyleWMarginRite() {
      if (isValueInvalid(SC_FLAG_21_MARGIN_RITE)) {
         styleWMarginRite = styleOp.getStyleMarginRite(style, ctx);
         setValueValid(SC_FLAG_21_MARGIN_RITE);
      }
      return styleWMarginRite;
   }

   public int getStyleWPadding() {
      if (isValueInvalid(SC_FLAG_22_PADDING_WIDTH)) {
         styleWPadding = getStyleWPaddingLeft() + getStyleWPaddingRite();
         setValueValid(SC_FLAG_22_PADDING_WIDTH);
      }
      return styleWPadding;
   }

   public int getStyleWPaddingBorder() {
      return getStyleWPadding() + getStyleWBorder();
   }

   public int getStyleWPaddingBorderMargin() {
      return getStyleWPadding() + getStyleWBorder() + getStyleWMargin();
   }

   public int getStyleWPaddingLeft() {
      if (isValueInvalid(SC_FLAG_12_PADDING_LEFT)) {
         styleWPaddingLeft = styleOp.getStylePaddingLeft(style, ctx);
         setValueValid(SC_FLAG_12_PADDING_LEFT);
      }
      return styleWPaddingLeft;
   }

   /**
    * 
    * @return
    */
   public int getStyleWPaddingRite() {
      if (isValueInvalid(SC_FLAG_13_PADDING_RITE)) {
         styleWPaddingRite = styleOp.getStylePaddingRite(style, ctx);
         setValueValid(SC_FLAG_13_PADDING_RITE);
      }
      return styleWPaddingRite;
   }

   public int getStyleWRightAll() {
      if (isValueInvalid(SC_FLAG_04_WIDTH_RITE)) {
         styleWAllRite = getStyleWBorderRite() + getStyleWMarginRite() + getStyleWPaddingRite();
         setValueValid(SC_FLAG_04_WIDTH_RITE);
      }
      return styleWAllRite;
   }

   /**
    * Called when style is invalidated and when {@link ILayoutable} dimensions are changed
    */
   public void invalidateValues() {
      flagsValidity = 0;
      isValidStyleAreas = false;
   }

   public boolean isValid() {
      return isValidStyleAreas;
   }

   private boolean isValueInvalid(int flag) {
      return !BitUtils.hasFlag(flagsValidity, flag);
   }

   public boolean isValueValid(int flag) {
      return BitUtils.hasFlag(flagsValidity, flag);
   }

   public void setNewStyle(ByteObject style) {
      isValidStyleAreas = false;
      this.style = style;
   }

   public void setValid() {
      isValidStyleAreas = true;
   }

   private void setValueValid(int flag) {
      flagsValidity = BitUtils.setFlag(flagsValidity, flag, true);
   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, StyleCache.class, 368);
      toStringPrivate(dc);
      super.toString(dc.sup());

      dc.nl();
      appendCache(dc, "styleHAll", styleHAll, SC_FLAG_02_HEIGHT_ALL);
      appendCache(dc, "styleHAllTop", styleHAllTop, SC_FLAG_05_HEIGHT_TOP);
      appendCache(dc, "styleHAllBot", styleHAllBot, SC_FLAG_06_HEIGHT_BOT);

      dc.nl();
      appendCache(dc, "styleHPadding", styleHPadding, SC_FLAG_23_PADDING_HEIGHT);
      appendCache(dc, "styleHPaddingTop", styleHPaddingTop, SC_FLAG_10_PADDING_TOP);
      appendCache(dc, "styleHPaddingBot", styleHPaddingBot, SC_FLAG_11_PADDING_BOT);

      dc.nl();
      appendCache(dc, "styleHBorder", styleHBorder, SC_FLAG_25_BORDER_HEIGHT);
      appendCache(dc, "styleHBorderTop", styleHBorderTop, SC_FLAG_14_BORDER_TOP);
      appendCache(dc, "styleHBorderBot", styleHBorderBot, SC_FLAG_15_BORDER_BOT);

      dc.nl();
      appendCache(dc, "styleHMargin", styleHMargin, SC_FLAG_27_MARGIN_HEIGHT);
      appendCache(dc, "styleHMarginTop", styleHMarginTop, SC_FLAG_18_MARGIN_TOP);
      appendCache(dc, "styleHMarginBot", styleHMarginBot, SC_FLAG_19_MARGIN_BOT);

      dc.nl();
      appendCache(dc, "styleWAll", styleWAll, SC_FLAG_01_WIDTH_ALL);
      appendCache(dc, "styleWAllLeft", styleWAllLeft, SC_FLAG_03_WIDTH_LEFT);
      appendCache(dc, "styleWAllRite", styleWAllRite, SC_FLAG_04_WIDTH_RITE);

      dc.nl();
      appendCache(dc, "styleHPadding", styleWPadding, SC_FLAG_22_PADDING_WIDTH);
      appendCache(dc, "styleHPaddingTop", styleWPaddingLeft, SC_FLAG_12_PADDING_LEFT);
      appendCache(dc, "styleHPaddingBot", styleWPaddingRite, SC_FLAG_13_PADDING_RITE);

      dc.nl();
      appendCache(dc, "styleWBorder", styleWBorder, SC_FLAG_24_BORDER_WIDTH);
      appendCache(dc, "styleWBorderLeft", styleWBorderLeft, SC_FLAG_16_BORDER_LEFT);
      appendCache(dc, "styleWBorderRite", styleWBorderRite, SC_FLAG_17_BORDER_RITE);

      dc.nl();
      appendCache(dc, "styleHMargin", styleWMargin, SC_FLAG_26_MARGIN_WIDTH);
      appendCache(dc, "styleWMarginLeft", styleWMarginLeft, SC_FLAG_20_MARGIN_LEFT);
      appendCache(dc, "styleWMarginRite", styleWMarginRite, SC_FLAG_21_MARGIN_RITE);

      dc.nlLvl("styleAreas", areas, 4);
      
      dc.nlLvl(style, "Style");
      dc.nlLvl(ctx, ILayoutable.class);
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, StyleCache.class);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   public int[] toStringGetAreas() {
      return areas;
   }

   private void toStringPrivate(Dctx dc) {
      dc.appendVarWithSpace("isValid", isValidStyleAreas);

   }

   //#enddebug

}