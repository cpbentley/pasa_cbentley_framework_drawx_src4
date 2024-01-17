/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.style;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesDrw;
import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.logging.IDLog;
import pasa.cbentley.core.src4.utils.BitUtils;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOTblr;
import pasa.cbentley.layouter.src4.interfaces.ILayoutable;

/**
 * The context for sizing style values is the {@link ILayoutable}
 * 
 * @author Charles Bentley
 *
 */
public class StyleCache extends ObjectDrw implements IBOStyle, IBOTblr, IBOTypesDrw, ITechStyleCache {

   private int[]         areas;

   private ILayoutable   ctx;

   private DrwCtx        dc;

   private int           flagsValidity;

   protected boolean     isValid;

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
      this.ctx = ctx;
      this.style = style;
      this.styleOp = dc.getStyleOperator();
   }

   /**
    * Direct reference which may be null.
    * @return
    */
   public int[] getStyleAreas() {
      return areas;
   }

   public int[] getStyleAreas(int x, int y, int w, int h) {

      //#debug
      toDLog().pFlow("x=" + x + " y=" + y + " w=" + w + " h=" + h, this, StyleCache.class, "getStyleAreas", LVL_05_FINE, true);

      if (areas == null || isValueInvalid(SC_FLAG_30_AREAS)) {
         areas = styleOp.getStyleAreas(x, y, w, h, style, ctx);
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
      }
      return styleHAll;
   }

   public int getStyleHBorder() {
      if (isValueInvalid(SC_FLAG_25_BORDER_HEIGHT)) {
         styleHBorder = getStyleHBorderBot() + getStyleHBorderTop();
      }
      return styleHBorder;
   }

   public int getStyleHBorderBot() {
      if (isValueInvalid(SC_FLAG_15_BORDER_BOT)) {
         styleHBorderBot = styleOp.getStyleBorderBot(style, ctx);
      }
      return styleHBorderBot;
   }

   public int getStyleHBorderTop() {
      if (isValueInvalid(SC_FLAG_14_BORDER_TOP)) {
         styleHBorderTop = styleOp.getStyleBorderTop(style, ctx);
      }
      return styleHBorderTop;
   }

   public int getStyleHBotAll() {
      if (isValueInvalid(SC_FLAG_06_HEIGHT_BOT)) {
         styleHAllBot = getStyleHBorderBot() + getStyleHPaddingBot() + getStyleHMarginBot();
      }
      return styleHAllBot;
   }

   public int getStyleHMargin() {
      if (isValueInvalid(SC_FLAG_27_MARGIN_HEIGHT)) {
         styleHMargin = getStyleHMarginBot() + getStyleHMarginTop();
      }
      return styleHMargin;
   }

   public int getStyleHMarginBot() {
      if (isValueInvalid(SC_FLAG_19_MARGIN_BOT)) {
         styleHMarginBot = styleOp.getStyleMarginBot(style, ctx);
      }
      return styleHMarginBot;
   }

   public int getStyleHMarginTop() {
      if (isValueInvalid(SC_FLAG_18_MARGIN_TOP)) {
         styleHMarginTop = styleOp.getStyleMarginTop(style, ctx);
      }
      return styleHMarginTop;
   }

   public int getStyleHPadding() {
      if (isValueInvalid(SC_FLAG_23_PADDING_HEIGHT)) {
         styleHPadding = getStyleHPaddingBot() + getStyleHPaddingTop();
      }
      return styleHPadding;
   }

   public int getStyleHPaddingBot() {
      if (isValueInvalid(SC_FLAG_11_PADDING_BOT)) {
         styleHPaddingBot = styleOp.getStylePaddingBot(style, ctx);
      }
      return styleHPaddingBot;
   }

   public int getStyleHPaddingTop() {
      if (isValueInvalid(SC_FLAG_10_PADDING_TOP)) {
         styleHPaddingTop = styleOp.getStylePaddingTop(style, ctx);
      }
      return styleHPaddingTop;
   }

   public int getStyleHTopAll() {
      if (isValueInvalid(SC_FLAG_05_HEIGHT_TOP)) {
         styleHAllTop = getStyleHMarginTop() + getStyleHBorderTop() + getStyleHPaddingTop();
      }
      return styleHAllTop;
   }

   public int getStyleWAll() {
      if (isValueInvalid(SC_FLAG_01_WIDTH_ALL)) {
         styleWAll = getStyleWLeftAll() + getStyleWRightAll();
      }
      return styleWAll;
   }

   public int getStyleWBorder() {
      if (isValueInvalid(SC_FLAG_24_BORDER_WIDTH)) {
         styleWBorder = getStyleWBorderLeft() + getStyleWBorderRite();
      }
      return styleWBorder;
   }

   public int getStyleWBorderLeft() {
      if (isValueInvalid(SC_FLAG_16_BORDER_LEFT)) {
         styleWBorderLeft = styleOp.getStyleBorderLeft(style, ctx);
      }
      return styleWBorderLeft;
   }

   public int getStyleWBorderRite() {
      if (isValueInvalid(SC_FLAG_17_BORDER_RITE)) {
         styleWBorderRite = styleOp.getStyleBorderRite(style, ctx);
      }
      return styleWBorderRite;
   }

   public int getStyleWLeftAll() {
      if (isValueInvalid(SC_FLAG_03_WIDTH_LEFT)) {
         styleWAllLeft = getStyleWBorderLeft() + getStyleWMarginLeft() + getStyleWPaddingLeft();
      }
      return styleWAllLeft;
   }

   public int getStyleWMargin() {
      if (isValueInvalid(SC_FLAG_26_MARGIN_WIDTH)) {
         styleWMargin = getStyleWMarginLeft() + getStyleWMarginRite();
      }
      return styleWMargin;
   }

   public int getStyleWMarginLeft() {
      if (isValueInvalid(SC_FLAG_20_MARGIN_LEFT)) {
         styleWMarginLeft = styleOp.getStyleMarginLeft(style, ctx);
      }
      return styleWMarginLeft;
   }

   public int getStyleWMarginRite() {
      if (isValueInvalid(SC_FLAG_21_MARGIN_RITE)) {
         styleWMarginRite = styleOp.getStyleMarginRite(style, ctx);
      }
      return styleWMarginRite;
   }

   public int getStyleWPadding() {
      if (isValueInvalid(SC_FLAG_22_PADDING_WIDTH)) {
         styleWPadding = getStyleWPaddingLeft() + getStyleWPaddingRite();
      }
      return styleWPadding;
   }

   public int getStyleWPaddingLeft() {
      if (isValueInvalid(SC_FLAG_12_PADDING_LEFT)) {
         styleWPaddingLeft = styleOp.getStylePaddingLeft(style, ctx);
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
      }
      return styleWPaddingRite;
   }

   public int getStyleWRightAll() {
      if (isValueInvalid(SC_FLAG_04_WIDTH_RITE)) {
         styleWAllRite = getStyleWBorderRite() + getStyleWMarginRite() + getStyleWPaddingRite();
      }
      return styleWAllRite;
   }

   /**
    * Called when style is invalidated and when {@link ILayoutable} dimensions are changed
    */
   public void invalidateValues() {
      flagsValidity = 0;
   }

   public boolean isValid() {
      return isValid;
   }

   public boolean isValueInvalid(int flag) {
      //global disable of cache
      return BitUtils.hasFlag(flagsValidity, flag);
   }

   public void setNewStyle(ByteObject style) {
      isValid = false;
      this.style = style;
   }

   public void setValid() {
      isValid = true;
   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, StyleCache.class, "@line5");
      toStringPrivate(dc);
      super.toString(dc.sup());
      
      dc.nlLvl(style, "Style");
      dc.nlLvl(ctx, ILayoutable.class);
   }

   private void toStringPrivate(Dctx dc) {
      
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, StyleCache.class);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   //#enddebug
   


}