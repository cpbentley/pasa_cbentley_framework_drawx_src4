/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.style;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesDrw;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.utils.BitUtils;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;
import pasa.cbentley.layouter.src4.interfaces.ILayoutable;
import pasa.cbentley.layouter.src4.tech.IBOTblr;

/**
 * The context for sizing style values is the {@link ILayoutable}
 * 
 * @author Charles Bentley
 *
 */
public class StyleCache extends ObjectDrw implements IBOStyle, IBOTblr, IBOTypesDrw, ITechStyleCache {

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
      if(ctx == null) {
         throw new NullPointerException();
      }
      this.ctx = ctx;
      if(style == null) {
         throw new NullPointerException();
      }
      this.style = style;
      this.styleOp = dc.getStyleOperator();
   }

   public ByteObject getStyle() {
      return style;
   }
   private void appendCache(Dctx dc, String name, int val, int flag) {
      dc.appendVarWithSpace(name, val);
      dc.appendBracketedWithSpace(isValueValid(flag));
   }

   public void computeStyleDimensions(int w, int h) {
      areas = createStyleArea(0, 0, w, h);
   }


   /**
    * Compute x,y,w,h values for the 4 style structures.
    * 
    * <p>
    * Optimize often used style areas. Those values will be used by style layers to draw themselves.
    * </p>
    * 
    * <p>
    * The returned array has the following data :
    * <li>0-3 = at margin rectangle
    * <li>4-7 = at border rectangle
    * <li>8-11 = at padding rectangle
    * <li>12-15 = at content rectangle
    * 
    * </p>
    * @param x
    * @param y
    * @param w
    * @param h
    * @param style
    * @return
    * 
    */
   public int[] createStyleArea(int x, int y, int w, int h) {
      int[] areas = new int[16];
      areas[4] = x;
      areas[5] = y;
      areas[6] = w;
      areas[7] = h;
      if (style.hasFlag(STYLE_OFFSET_1_FLAGA, STYLE_FLAGA_5_MARGIN)) {
         int marginLeft = getStyleWMarginLeft();
         int marginTop = getStyleHMarginTop();
         x += marginLeft;
         y += marginTop;
         w = w - marginLeft - getStyleWMarginRite();
         h = h - marginTop - getStyleHMarginBot();
      }
      areas[0] = x;
      areas[1] = y;
      areas[2] = w;
      areas[3] = h;
      if (style.hasFlag(STYLE_OFFSET_1_FLAGA, STYLE_FLAGA_4_BORDER)) {
         int borderLeft = getStyleWBorderLeft();
         int borderTop = getStyleHBorderTop();
         x += borderLeft;
         y += borderTop;
         w = w - borderLeft - getStyleWBorderRite();
         h = h - borderTop - getStyleHBorderBot();
      }
      areas[12] = x;
      areas[13] = y;
      areas[14] = w;
      areas[15] = h;
      if (style.hasFlag(STYLE_OFFSET_1_FLAGA, STYLE_FLAGA_3_PADDING)) {
         int paddingLeft = getStyleWPaddingLeft();
         int paddingTop = getStyleHPaddingTop();
         x += paddingLeft;
         y += paddingTop;
         w = w - paddingLeft - getStyleWPaddingRite();
         h = h - paddingTop - getStyleHPaddingBot();
      }
      areas[8] = x;
      areas[9] = y;
      areas[10] = w;
      areas[11] = h;
      return areas;
   }

   /**
    * Direct reference.
    * Cannot be null. Must be called after compute.
    * 
    * provides x y diff values and w,h for margin,border,pad content areas
    * 
    * That 4*4 values = 16
    * @return
    */
   public int[] getStyleAreas() {
      if (areas == null) {
         areas = getStyleAreas(ctx.getPozeX(), ctx.getPozeY(), ctx.getSizeDrawnWidth(), ctx.getSizeDrawnHeight());
      }
      return areas;
   }

   public int[] getStyleAreas(int x, int y, int w, int h) {
      //#debug
      toDLog().pFlow("x=" + x + " y=" + y + " w=" + w + " h=" + h, this, StyleCache.class, "getStyleAreas", LVL_05_FINE, true);
      
      if (areas == null || isValueInvalid(SC_FLAG_30_AREAS)) {
         areas = styleOp.getStyleAreas(x, y, w, h, style, ctx);
         
         int[] areas = createStyleArea(x, y, w, h);
         this.areas = areas;
      }
      if (areas == null) {

         //#debug
         toDLog().pNull("x=" + x + " y=" + y + " w=" + w + " h=" + h, this, StyleCache.class, "getStyleAreas");
         throw new IllegalStateException("Areas must not be null");
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
      BitUtils.setFlag(flagsValidity, flag, true);
   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, StyleCache.class, 368);
      toStringPrivate(dc);
      super.toString(dc.sup());

      dc.nlLvl("styleAreas", areas, 4);

      dc.nlLvl(style, "Style");
      dc.nlLvl(ctx, ILayoutable.class);

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

   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, StyleCache.class);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   private void toStringPrivate(Dctx dc) {
      dc.appendVarWithSpace("isValid", isValidStyleAreas);

   }

   //#enddebug

}