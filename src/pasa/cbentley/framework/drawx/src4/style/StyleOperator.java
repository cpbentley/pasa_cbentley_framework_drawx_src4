/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.style;

import pasa.cbentley.byteobjects.src4.core.BOAbstractOperator;
import pasa.cbentley.byteobjects.src4.core.BOModulesManager;
import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesBOC;
import pasa.cbentley.byteobjects.src4.ctx.IToStringFlagsBO;
import pasa.cbentley.byteobjects.src4.objects.pointer.IBOPointer;
import pasa.cbentley.core.src4.interfaces.C;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.utils.BitUtils;
import pasa.cbentley.core.src4.utils.interfaces.IColors;
import pasa.cbentley.framework.coredraw.src4.interfaces.IMFont;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrawX;
import pasa.cbentley.framework.drawx.src4.ctx.IToStringFlagsDrw;
import pasa.cbentley.framework.drawx.src4.ctx.ToStringStaticDrawx;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.factories.FigureOperator;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigure;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;
import pasa.cbentley.framework.drawx.src4.tech.ITechStyle;
import pasa.cbentley.layouter.src4.ctx.IBOTypesLayout;
import pasa.cbentley.layouter.src4.engine.LayoutOperator;
import pasa.cbentley.layouter.src4.engine.LayoutableRect;
import pasa.cbentley.layouter.src4.interfaces.ILayoutable;
import pasa.cbentley.layouter.src4.tech.IBOTblr;
import pasa.cbentley.layouter.src4.tech.ITechLayout;

public class StyleOperator extends BOAbstractOperator implements IBOStyle, ITechFigure, IBOTypesDrawX, IBOTblr, ITechStyleCache {

   protected final DrwCtx dc;

   public StyleOperator(DrwCtx dc) {
      super(dc.getBOC());
      this.dc = dc;
   }

   /**
    * Can be null, then ignores.
    * @param style
    */
   public void checkStyleIncomplete(ByteObject style) {
      if (style != null) {

         boolean isIncomplete = style.hasFlag(STYLE_OFFSET_5_FLAG_X, STYLE_FLAG_X_1_INCOMPLETE);
         if (!isIncomplete) {
            throw new IllegalArgumentException();
         }
      }
   }

   /**
    * Optimize often used style areas.
    * @param x
    * @param y
    * @param w
    * @param h
    * @param style
    * @return
    * 0-3 = border (see {@link ByteObject#STYLE_ANC_0_BORDER}
    * 4-7 = border (see {@link ByteObject#STYLE_ANC_1_MARGIN}
    * 8-11 = border (see {@link ByteObject#STYLE_ANC_2_CONTENT}
    * 12-15 = border (see {@link ByteObject#STYLE_ANC_3_PADDING}
    * 
    */
   public int[] computeNewStyleAreas(int x, int y, int w, int h, ByteObject style) {
      LayoutableRect r = new LayoutableRect(dc.getLAC(), w, h);
      r.set(x, y, w, h);
      int[] areas = computeNewStyleAreas(x, y, w, h, style, r);
      return areas;
   }

   public int[] computeNewStyleAreas(int x, int y, int w, int h, ByteObject style, ILayoutable c) {
      int[] areas = new int[16];
      areas[4] = x;
      areas[5] = y;
      areas[6] = w;
      areas[7] = h;
      if (style.hasFlag(STYLE_OFFSET_1_FLAG_A, STYLE_FLAG_A_5_MARGIN)) {
         int ml = getStyleMargin(style, C.POS_2_LEFT, c);
         int mt = getStyleMargin(style, C.POS_0_TOP, c);
         x += ml;
         y += mt;
         w = w - ml - getStyleMargin(style, C.POS_3_RIGHT, c);
         h = h - mt - getStyleMargin(style, C.POS_1_BOT, c);
      }
      areas[0] = x;
      areas[1] = y;
      areas[2] = w;
      areas[3] = h;
      if (style.hasFlag(STYLE_OFFSET_1_FLAG_A, STYLE_FLAG_A_4_BORDER)) {
         int ml = getStyleBorder(style, C.POS_2_LEFT, c);
         int mt = getStyleBorder(style, C.POS_0_TOP, c);
         x += ml;
         y += mt;
         w = w - ml - getStyleBorder(style, C.POS_3_RIGHT, c);
         h = h - mt - getStyleBorder(style, C.POS_1_BOT, c);
      }
      areas[12] = x;
      areas[13] = y;
      areas[14] = w;
      areas[15] = h;
      if (style.hasFlag(STYLE_OFFSET_1_FLAG_A, STYLE_FLAG_A_3_PADDING)) {
         int ml = getStylePadding(style, C.POS_2_LEFT, c);
         int mt = getStylePadding(style, C.POS_0_TOP, c);
         x += ml;
         y += mt;
         w = w - ml - getStylePadding(style, C.POS_3_RIGHT, c);
         h = h - mt - getStylePadding(style, C.POS_1_BOT, c);
      }
      areas[8] = x;
      areas[9] = y;
      areas[10] = w;
      areas[11] = h;
      return areas;
   }

   /**
    * Draw style decoration (background + border + figure + foreground) 
    * on the given area. Sizers and Pozers are all relative to xy,wh
    * @param g
    * @param x coordinate
    * @param y
    * @param w
    * @param h
    * @param style
    */
   public void drawStyle(GraphicsX g, int x, int y, int w, int h, ByteObject style) {
      int[] areas = computeNewStyleAreas(x, y, w, h, style);
      drawStyleBg(style, g, x, y, w, h, areas);
      drawStyleFg(style, g, x, y, w, h, areas);
   }

   /**
    * Called by Drawable for drawing its background using the drawing param of its style
    * @param styleKey
    * @param g
    * @param x
    * @param y
    * @param w
    * @param h
    */
   public void drawStyleBg(ByteObject style, GraphicsX g, int x, int y, int w, int h, int[] areas) {
      drawStyleFigure(style, g, x, y, w, h, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_1_BG, areas, 1);
      drawStyleFigure(style, g, x, y, w, h, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_2_BG, areas, 3);
      drawStyleFigure(style, g, x, y, w, h, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_3_BG, areas, 5);
      drawStyleFigure(style, g, x, y, w, h, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_4_BG, areas, 7);
   }

   public void drawStyleBg(ByteObject style, GraphicsX g, int[] areas) {
      drawStyleFigure(style, g, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_1_BG, areas, 1);
      drawStyleFigure(style, g, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_2_BG, areas, 3);
      drawStyleFigure(style, g, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_3_BG, areas, 5);
      drawStyleFigure(style, g, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_4_BG, areas, 7);
   }

   public void drawStyleBg(ByteObject style, GraphicsX g, int[] areas, int x, int y) {
      drawStyleFigure(style, g, x, y, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_1_BG, areas, 1);
      drawStyleFigure(style, g, x, y, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_2_BG, areas, 3);
      drawStyleFigure(style, g, x, y, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_3_BG, areas, 5);
      drawStyleFigure(style, g, x, y, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_4_BG, areas, 7);
   }

   public void drawStyleFg(ByteObject style, GraphicsX g, int x, int y, int w, int h, int[] areas) {
      drawStyleFigure(style, g, x, y, w, h, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_5_FG, areas, 1);
      drawStyleFigure(style, g, x, y, w, h, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_6_FG, areas, 3);
      drawStyleFigure(style, g, x, y, w, h, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_7_FG, areas, 5);
      drawStyleFigure(style, g, x, y, w, h, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_8_FG, areas, 7);
   }

   public void drawStyleFg(ByteObject style, GraphicsX g, int[] areas) {
      drawStyleFigure(style, g, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_5_FG, areas, 1);
      drawStyleFigure(style, g, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_6_FG, areas, 3);
      drawStyleFigure(style, g, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_7_FG, areas, 5);
      drawStyleFigure(style, g, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_8_FG, areas, 7);
   }

   public void drawStyleFg(ByteObject style, GraphicsX g, int[] areas, int x, int y) {
      drawStyleFigure(style, g, x, y, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_5_FG, areas, 1);
      drawStyleFigure(style, g, x, y, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_6_FG, areas, 3);
      drawStyleFigure(style, g, x, y, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_7_FG, areas, 5);
      drawStyleFigure(style, g, x, y, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_8_FG, areas, 7);
   }

   /**
    * Draw style figure
    * @param style
    * @param g
    * @param x
    * @param y
    * @param w
    * @param h
    * @param pointer pointer of flags
    * @param flag flag of figure layer
    * @param areas computed area for figure, at margin,border,content or padding
    * @param anc
    */
   public void drawStyleFigure(ByteObject style, GraphicsX g, int x, int y, int w, int h, int pointer, int flag, int[] areas, int anc) {
      ByteObject bg = getStyleDrw(style, pointer, flag);
      if (bg != null) {
         int val = style.get1(STYLE_OFFSET_7_BG_POINTS1);
         if (flag > 8) {
            //we have a figure for FG layers
            val = style.get1(STYLE_OFFSET_8_FG_POINTS1);
         }
         int p = (BitUtils.getBit(anc + 1, val) << 1) + BitUtils.getBit(anc, val);
         //System.out.print(debugStyleAnchor(p));
         //when p is 0, it is border, when p is 1 it is margin
         //#debug
         toDLog().pNull("StyleGAnchor=+" + ToStringStaticDrawx.toStringStyleAnchor(p) + " anc=" + anc, null, StyleOperator.class, "drawStyleFigure", LVL_05_FINE, true);
         p = p * 4;
         int dx = areas[p];
         int dy = areas[p + 1];
         int dw = areas[p + 2];
         int dh = areas[p + 3];
         FigureOperator figureOperator = dc.getFigureOperator();
         figureOperator.paintFigure(g, dx, dy, dw, dh, bg);
      }
   }

   public void drawStyleFigure(ByteObject style, GraphicsX g, int x, int y, int pointer, int flag, int[] areas, int anc) {
      ByteObject bg = getStyleDrw(style, pointer, flag);
      if (bg != null) {
         int val = style.get1(STYLE_OFFSET_7_BG_POINTS1);
         if (flag > 8) {
            //we have a figure for FG layers
            val = style.get1(STYLE_OFFSET_8_FG_POINTS1);
         }
         int p = (BitUtils.getBit(anc + 1, val) << 1) + BitUtils.getBit(anc, val);
         //System.out.print(debugStyleAnchor(p));
         p = p * 4;
         int dx = x + areas[p];
         int dy = y + areas[p + 1];
         int dw = areas[p + 2];
         int dh = areas[p + 3];
         FigureOperator figureOperator = dc.getFigureOperator();
         figureOperator.paintFigure(g, dx, dy, dw, dh, bg);
      }
   }

   /**
    * 
    * @param style the style object from which to extract the figure
    * @param g
    * @param pointer
    * @param flag
    * <li> {@link IBOStyle#STYLE_FLAG_B_1_BG}
    * <li> {@link IBOStyle#STYLE_FLAG_B_2_BG}
    * <li> {@link IBOStyle#STYLE_FLAG_B_3_BG}
    * <li> {@link IBOStyle#STYLE_FLAG_B_4_BG}
    * <li> {@link IBOStyle#STYLE_FLAG_B_5_FG}
    * <li> {@link IBOStyle#STYLE_FLAG_B_6_FG}
    * 
    * @param areas
    * @param areaOffset position of bit flag in {@link IBOStyle#STYLE_OFFSET_7_BG_POINTS1}
    */
   public void drawStyleFigure(ByteObject style, GraphicsX g, int pointer, int flag, int[] areas, int areaOffset) {
      ByteObject bg = getStyleDrw(style, pointer, flag);
      if (bg != null) {
         int val = style.get1(STYLE_OFFSET_7_BG_POINTS1);
         if (flag > 8) {
            //we have a figure for FG layers
            val = style.get1(STYLE_OFFSET_8_FG_POINTS1);
         }
         int p = (BitUtils.getBit(areaOffset + 1, val) << 1) + BitUtils.getBit(areaOffset, val);
         //System.out.print(debugStyleAnchor(p));
         p = p * 4;
         int dx = areas[p];
         int dy = areas[p + 1];
         int dw = areas[p + 2];
         int dh = areas[p + 3];
         FigureOperator figureOperator = dc.getFigureOperator();
         figureOperator.paintFigure(g, dx, dy, dw, dh, bg);
      }
   }

   /**
    * Return the style element linked to {@link IBOStyle#STYLE_FLAG_A_1_CONTENT}
    * <br>
    * When not load, returns default
    * <br>
    * <br>
    * @param style 
    * @return non null {@link ByteObject}
    */
   public ByteObject getContentStyle(ByteObject style) {
      return getStyleElement(style, STYLE_FLAG_A_1_CONTENT);
   }

   public int getMargin(ByteObject style, int pos) {
      return getStyleMargin(style, pos);
   }

   public int getPadH(ByteObject style) {
      return getStylePadding(style, C.POS_0_TOP) + getStylePadding(style, C.POS_1_BOT);
   }

   public int getPadLeft(ByteObject style) {
      return getStylePadding(style, C.POS_2_LEFT);
   }

   public int getPadTop(ByteObject style) {
      return getStylePadding(style, C.POS_0_TOP);
   }

   public int getPadW(ByteObject style) {
      return getStylePadding(style, C.POS_2_LEFT) + getStylePadding(style, C.POS_3_RIGHT);
   }

   /**
    * How much Height will x lines take?
    * Straightforward for horizontal text effect
    * Sans object for vertical and diagonal because cannot be
    * computed with just the number of lines
    * 
    * @param styleKey
    * @param numLines
    * @return
    */
   public int getPH(ByteObject styleKey, int numLines) {
      IMFont f = getStyleFont(styleKey);
      return numLines * f.getHeight();
   }

   /**
    * Preferred Height for displaying those broken lines
    * @param styleKey
    * @param breakText
    * @return 0 if breakText is null
    */
   public int getPH(ByteObject styleKey, int[][] breakText) {
      if (breakText == null) {
         return 0;
      }
      IMFont f = getStyleFont(styleKey);
      return breakText.length * f.getHeight();
   }

   private int getPixelSizeTBLRCoded(int codedsizer, int pos, ILayoutable c) {
      int ctxType = ITechLayout.CTX_1_WIDTH;
      if (pos == C.POS_0_TOP || pos == C.POS_1_BOT) {
         ctxType = ITechLayout.CTX_2_HEIGHT;
      }
      return dc.getLAC().getLayoutOperator().codedSizeDecode(codedsizer, c, ctxType);
   }

   /**
    * Gets a Style TBLR pixel size.
    * <br>
    * <br>
    * Sizer describes how to compute it.
    * <br>
    * {@link ISizer#SIZER_OFFSET_06_ET_FUN1}
    * <br>
    * What matters is the type of etalon to use
    * @param sizer
    * @param pos
    * @return
    */
   private int getPixelSizeTBLRSizer(ByteObject sizer, int pos, ILayoutable c) {
      int ctxType = ITechLayout.CTX_1_WIDTH;
      if (pos == C.POS_0_TOP || pos == C.POS_1_BOT) {
         ctxType = ITechLayout.CTX_2_HEIGHT;
      }
      LayoutOperator layoutOperator = dc.getLAC().getLayoutOperator();
      return layoutOperator.getPixelSize(sizer, c, ctxType);
   }

   /**
    * String Width
    * @param style
    * @param str
    * @return
    */
   public int getPW(ByteObject style, String str) {
      IMFont f = getStyleFont(style);
      return f.substringWidth(str, 0, str.length());
   }

   public ByteObject getStyleAnchor(ByteObject style) {
      return style.getSubFirst(IBOTypesDrawX.TYPE_DRWX_03_BOX);
   }

   /**
    * @param x coordinate of the content area
    * @param y coordinate of the content area
    * @param w  width of the content area
    * @param h height of the content area
    * @param style
    * @param c
    * @return
    */
   public int[] getStyleAreas(int x, int y, int w, int h, ByteObject style, ILayoutable c, StyleCache cache, int typeRelativeW, int typeRelativeH, int typeRelativeX, int typeRelativeY) {
      LayoutOperator layOp = dc.getLayoutOperator();
      int mTop = 0;
      int mBot = 0;
      int mLeft = 0;
      int mRite = 0;

      int bTop = 0;
      int bBot = 0;
      int bLeft = 0;
      int bRite = 0;

      int pTop = 0;
      int pBot = 0;
      int pLeft = 0;
      int pRite = 0;
      ByteObject tblr = null;
      if ((tblr = getStyleElement(style, STYLE_FLAG_A_3_PADDING)) != null) {
         pTop = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_0_TOP, c) : cache.getStyleHPaddingTop();
         pBot = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_1_BOT, c) : cache.getStyleHPaddingBot();
         pLeft = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_2_LEFT, c) : cache.getStyleWPaddingLeft();
         pRite = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_3_RIGHT, c) : cache.getStyleWPaddingRite();
      }
      if ((tblr = getStyleElement(style, STYLE_FLAG_A_4_BORDER)) != null) {
         bTop = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_0_TOP, c) : cache.getStyleHBorderTop();
         bBot = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_1_BOT, c) : cache.getStyleHBorderBot();
         bLeft = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_2_LEFT, c) : cache.getStyleWBorderLeft();
         bRite = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_3_RIGHT, c) : cache.getStyleWBorderRite();
      }

      if ((tblr = getStyleElement(style, STYLE_FLAG_A_5_MARGIN)) != null) {
         mTop = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_0_TOP, c) : cache.getStyleHMarginBot();
         mBot = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_1_BOT, c) : cache.getStyleHMarginBot();
         mLeft = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_2_LEFT, c) : cache.getStyleWMarginLeft();
         mRite = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_3_RIGHT, c) : cache.getStyleWMarginRite();
      }
      //default caawe of
      int marginX = 0;
      int borderX = 0;
      int paddingX = 0;
      int contentX = 0;
      if (typeRelativeX == RELATIVE_TYPE_0_MARGIN) {
         marginX = x;
         borderX = x + mLeft;
         paddingX = x + mLeft + bLeft;
         contentX = x + mLeft + bLeft + pLeft;
      } else if (typeRelativeX == RELATIVE_TYPE_1_BORDER) {
         marginX = x - mLeft;
         borderX = x;
         paddingX = x + bLeft;
         contentX = x + bLeft + pLeft;
      } else if (typeRelativeX == RELATIVE_TYPE_2_PADDING) {
         marginX = x - bLeft - mLeft;
         borderX = x - bLeft;
         paddingX = x;
         contentX = x + pLeft;
      } else if (typeRelativeX == RELATIVE_TYPE_3_CONTENT) {
         marginX = x - pLeft - bLeft - mLeft;
         borderX = x - pLeft - bLeft;
         paddingX = x - pLeft;
         contentX = x;
      }

      int marginY = 0;
      int borderY = 0;
      int paddingY = 0;
      int contentY = 0;
      if (typeRelativeY == RELATIVE_TYPE_0_MARGIN) {
         marginY = y;
         borderY = y + mTop;
         paddingY = y + mTop + bTop;
         contentY = y + mTop + bTop + pTop;
      } else if (typeRelativeY == RELATIVE_TYPE_1_BORDER) {
         marginY = y - mTop;
         borderY = y;
         paddingY = y + bTop;
         contentY = y + bTop + pTop;
      } else if (typeRelativeY == RELATIVE_TYPE_2_PADDING) {
         marginY = y - bTop - mTop;
         borderY = y - bTop;
         paddingY = y;
         contentY = y + pTop;
      } else if (typeRelativeY == RELATIVE_TYPE_3_CONTENT) {
         marginY = y - pTop - bTop - mTop;
         borderY = y - pTop - bTop;
         paddingY = y - pTop;
         contentY = y;
      }

      int marginW = 0;
      int borderW = 0;
      int paddingW = 0;
      int contentW = 0;
      if (typeRelativeW == RELATIVE_TYPE_0_MARGIN) {
         marginW = w;
         borderW = w - mLeft - mRite;
         paddingW = w - mLeft - bLeft - mRite - bRite;
         contentW = w - mLeft - bLeft - pLeft - mRite - bRite - pRite;
      } else if (typeRelativeW == RELATIVE_TYPE_1_BORDER) {
         marginW = w + mLeft + mRite;
         borderW = w;
         paddingW = w - bLeft - bRite;
         contentW = w - bLeft - pLeft - bRite - pRite;
      } else if (typeRelativeW == RELATIVE_TYPE_2_PADDING) {
         marginW = w + bLeft + mLeft + bRite + mRite;
         borderW = w + bLeft + bRite;
         paddingW = w;
         contentW = w - pLeft - pRite;
      } else if (typeRelativeW == RELATIVE_TYPE_3_CONTENT) {
         marginW = w + pLeft + bLeft + mLeft + pRite + bRite + mRite;
         borderW = w + pLeft + bLeft + pRite + bRite;
         paddingW = w + pLeft + pRite;
         contentW = w;
      }

      int marginH = 0;
      int borderH = 0;
      int paddingH = 0;
      int contentH = 0;
      if (typeRelativeH == RELATIVE_TYPE_0_MARGIN) {
         marginH = h;
         borderH = h - mTop - mBot;
         paddingH = h - mTop - bTop - mBot - bBot;
         contentH = h - mTop - bTop - pTop - mBot - bBot - pBot;
      } else if (typeRelativeH == RELATIVE_TYPE_1_BORDER) {
         marginH = h + mTop + mBot;
         borderH = h;
         paddingH = h - bTop - bBot;
         contentH = h - bTop - pTop - bBot - pBot;
      } else if (typeRelativeH == RELATIVE_TYPE_2_PADDING) {
         marginH = h + bTop + mTop + bBot + mBot;
         borderH = h + bTop + bBot;
         paddingH = h;
         contentH = h - pTop - pBot;
      } else if (typeRelativeH == RELATIVE_TYPE_3_CONTENT) {
         marginH = h + pTop + bTop + mTop + pBot + bBot + mBot;
         borderH = h + pTop + bTop + pBot + bBot;
         paddingH = h + pTop + pBot;
         contentH = h;
      }

      int[] areas = new int[16];
      areas[ITechStyleCache.OFFSET_CONTENT_X] = contentX; //content values are located third
      areas[ITechStyleCache.OFFSET_CONTENT_Y] = contentY;
      areas[ITechStyleCache.OFFSET_CONTENT_W] = contentW;
      areas[ITechStyleCache.OFFSET_CONTENT_H] = contentH;

      areas[ITechStyleCache.OFFSET_PADDING_X] = paddingX;
      areas[ITechStyleCache.OFFSET_PADDING_Y] = paddingY;
      areas[ITechStyleCache.OFFSET_PADDING_W] = paddingW;
      areas[ITechStyleCache.OFFSET_PADDING_H] = paddingH;

      areas[ITechStyleCache.OFFSET_BORDER_X_0] = borderX;
      areas[ITechStyleCache.OFFSET_BORDER_Y_1] = borderY;
      areas[ITechStyleCache.OFFSET_BORDER_W_2] = borderW;
      areas[ITechStyleCache.OFFSET_BORDER_H_3] = borderH;

      areas[ITechStyleCache.OFFSET_MARGIN_X] = marginX; //margin values are located second
      areas[ITechStyleCache.OFFSET_MARGIN_Y] = marginY;
      areas[ITechStyleCache.OFFSET_MARGIN_W] = marginW;
      areas[ITechStyleCache.OFFSET_MARGIN_H] = marginH;
      return areas;
   }

   public int[] getStyleAreasContent(int x, int y, int w, int h, ByteObject style, ILayoutable c) {
      return getStyleAreasContent(x, y, w, h, style, c, null);
   }

   /**
    * @param x coordinate of the content area
    * @param y coordinate of the content area
    * @param w  width of the content area
    * @param h height of the content area
    * @param style
    * @param c
    * @return
    */
   public int[] getStyleAreasContent(int x, int y, int w, int h, ByteObject style, ILayoutable c, StyleCache cache) {
      LayoutOperator layOp = dc.getLayoutOperator();
      int[] areas = new int[16];
      areas[8] = x; //content values are located third
      areas[9] = y;
      areas[10] = w;
      areas[11] = h;
      ByteObject tblr = null;
      if ((tblr = getStyleElement(style, STYLE_FLAG_A_3_PADDING)) != null) {
         int mt = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_0_TOP, c) : cache.getStyleHPaddingTop();
         int mb = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_1_BOT, c) : cache.getStyleHPaddingBot();
         int ml = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_2_LEFT, c) : cache.getStyleWPaddingLeft();
         int mr = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_3_RIGHT, c) : cache.getStyleWPaddingRite();
         x -= ml;
         y -= mt;
         w = w + ml + mr;
         h = h + mt + mb;
      }
      areas[12] = x; //padding values are located last
      areas[13] = y;
      areas[14] = w;
      areas[15] = h;
      if ((tblr = getStyleElement(style, STYLE_FLAG_A_4_BORDER)) != null) {
         int mt = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_0_TOP, c) : cache.getStyleHBorderTop();
         int mb = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_1_BOT, c) : cache.getStyleHBorderBot();
         int ml = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_2_LEFT, c) : cache.getStyleWBorderLeft();
         int mr = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_3_RIGHT, c) : cache.getStyleWBorderRite();
         x -= ml;
         y -= mt;
         w = w + ml + mr;
         h = h + mt + mb;
      }
      areas[0] = x; //border values are located first
      areas[1] = y;
      areas[2] = w;
      areas[3] = h;
      if ((tblr = getStyleElement(style, STYLE_FLAG_A_5_MARGIN)) != null) {
         int mt = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_0_TOP, c) : cache.getStyleHMarginBot();
         int mb = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_1_BOT, c) : cache.getStyleHMarginBot();
         int ml = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_2_LEFT, c) : cache.getStyleWMarginLeft();
         int mr = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_3_RIGHT, c) : cache.getStyleWMarginRite();
         x -= ml;
         y -= mt;
         w = w + ml + mr;
         h = h + mt + mb;
      }
      areas[4] = x; //margin values are located second
      areas[5] = y;
      areas[6] = w;
      areas[7] = h;
      return areas;
   }

   public void getStyleAreasContentH(int x, int y, int w, int h, ByteObject style, ILayoutable c, int[] areas, StyleCache cache) {
      LayoutOperator layOp = dc.getLayoutOperator();
      //content values are located third
      areas[9] = y;
      areas[11] = h;
      ByteObject tblr = null;
      if ((tblr = getStyleElement(style, STYLE_FLAG_A_3_PADDING)) != null) {
         int mt = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_0_TOP, c) : cache.getStyleHPaddingTop();
         int mb = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_1_BOT, c) : cache.getStyleHPaddingBot();
         y -= mt;
         h = h + mt + mb;
      }
      //padding values are located last
      areas[13] = y;
      areas[15] = h;
      if ((tblr = getStyleElement(style, STYLE_FLAG_A_4_BORDER)) != null) {
         int mt = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_0_TOP, c) : cache.getStyleHBorderTop();
         int mb = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_1_BOT, c) : cache.getStyleHBorderBot();
         y -= mt;
         h = h + mt + mb;
      }
      //border values are located first
      areas[1] = y;
      areas[3] = h;
      if ((tblr = getStyleElement(style, STYLE_FLAG_A_5_MARGIN)) != null) {
         int mt = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_0_TOP, c) : cache.getStyleHMarginTop();
         int mb = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_1_BOT, c) : cache.getStyleHMarginBot();
         y -= mt;
         h = h + mt + mb;
      }
      //margin values are located second
      areas[5] = y;
      areas[7] = h;
   }

   public void getStyleAreasContentW(int x, int y, int w, int h, ByteObject style, ILayoutable c, int[] areas, StyleCache cache) {
      LayoutOperator layOp = dc.getLayoutOperator();
      areas[8] = x; //content values are located third
      areas[10] = w;
      ByteObject tblr = null;
      if ((tblr = getStyleElement(style, STYLE_FLAG_A_3_PADDING)) != null) {
         int ml = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_2_LEFT, c) : cache.getStyleWPaddingLeft();
         int mr = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_3_RIGHT, c) : cache.getStyleWPaddingRite();
         x -= ml;
         w = w + ml + mr;
      }
      areas[12] = x; //padding values are located last
      areas[14] = w;
      if ((tblr = getStyleElement(style, STYLE_FLAG_A_4_BORDER)) != null) {
         int ml = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_2_LEFT, c) : cache.getStyleWBorderLeft();
         int mr = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_3_RIGHT, c) : cache.getStyleWBorderRite();
         x -= ml;
         w = w + ml + mr;
      }
      areas[0] = x; //border values are located first
      areas[2] = w;
      if ((tblr = getStyleElement(style, STYLE_FLAG_A_5_MARGIN)) != null) {
         int ml = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_2_LEFT, c) : cache.getStyleWMarginLeft();
         int mr = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_3_RIGHT, c) : cache.getStyleWMarginRite();
         x -= ml;
         w = w + ml + mr;
      }
      areas[4] = x; //margin values are located second
      areas[6] = w;
   }

   public int[] getStyleAreasContentWFullH(int x, int y, int w, int h, ByteObject style, ILayoutable c, StyleCache cache) {
      int[] areas = new int[16];
      getStyleAreasContentW(x, y, w, h, style, c, areas, cache);
      getStyleAreasFullH(x, y, w, h, style, c, areas, cache);
      return areas;
   }

   public int[] getStyleAreasFull(int x, int y, int w, int h, ByteObject style, ILayoutable c) {
      return getStyleAreasFull(x, y, w, h, style, c, null);
   }

   /**
    * Optimize often used style areas.
    * Compute x,y,w,h values for the 4 style structures
    * Those values will be used by style layers to draw themselves.
    * @param x coordinate of the whole area
    * @param y coordinate of the whole area
    * @param w width of the whole area
    * @param h height of the whole area
    * @param style
    * @return
    * 
    * <li>0-3 = border rectangle -> {@link ITechStyle#STYLE_ANC_0_BORDER}
    * <li>4-7 = margin rectanble -> {@link ITechStyle#STYLE_ANC_1_MARGIN}
    * <li>8-11 = content rectangle -> {@link ITechStyle#STYLE_ANC_2_CONTENT}
    * <li>12-15 = padding rectangle -> {@link ITechStyle#STYLE_ANC_3_PADDING}
    * 
    */
   public int[] getStyleAreasFull(int x, int y, int w, int h, ByteObject style, ILayoutable c, StyleCache cache) {
      LayoutOperator layOp = dc.getLayoutOperator();
      int[] areas = new int[16];
      areas[4] = x; //margin values are located second
      areas[5] = y;
      areas[6] = w;
      areas[7] = h;
      ByteObject tblr = null;
      if ((tblr = getStyleElement(style, STYLE_FLAG_A_5_MARGIN)) != null) {
         int mt = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_0_TOP, c) : cache.getStyleHMarginTop();
         int mb = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_1_BOT, c) : cache.getStyleHMarginBot();
         int ml = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_2_LEFT, c) : cache.getStyleWMarginLeft();
         int mr = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_3_RIGHT, c) : cache.getStyleWMarginRite();
         x += ml;
         y += mt;
         w = w - ml - mr;
         h = h - mt - mb;
      }
      areas[0] = x; //border values are located first
      areas[1] = y;
      areas[2] = w;
      areas[3] = h;
      if ((tblr = getStyleElement(style, STYLE_FLAG_A_4_BORDER)) != null) {
         int mt = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_0_TOP, c) : cache.getStyleHBorderTop();
         int mb = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_1_BOT, c) : cache.getStyleHBorderBot();
         int ml = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_2_LEFT, c) : cache.getStyleWBorderLeft();
         int mr = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_3_RIGHT, c) : cache.getStyleWBorderRite();
         x += ml;
         y += mt;
         w = w - ml - mr;
         h = h - mt - mb;
      }
      areas[12] = x; //padding values are located last
      areas[13] = y;
      areas[14] = w;
      areas[15] = h;
      if ((tblr = getStyleElement(style, STYLE_FLAG_A_3_PADDING)) != null) {
         int mt = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_0_TOP, c) : cache.getStyleHPaddingTop();
         int mb = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_1_BOT, c) : cache.getStyleHPaddingBot();
         int ml = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_2_LEFT, c) : cache.getStyleWPaddingLeft();
         int mr = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_3_RIGHT, c) : cache.getStyleWPaddingRite();
         x += ml;
         y += mt;
         w = w - ml - mr;
         h = h - mt - mb;
      }
      areas[8] = x; //content values are located third
      areas[9] = y;
      areas[10] = w;
      areas[11] = h;
      return areas;
   }

   public int[] getStyleAreasFull(int x, int y, int w, int h, StyleCache cache) {
      if (cache == null) {
         throw new NullPointerException();
      }
      return getStyleAreasFull(x, y, w, h, cache.getStyle(), cache.getLayoutable(), cache);
   }

   public void getStyleAreasFullH(int x, int y, int w, int h, ByteObject style, ILayoutable c, int[] areas, StyleCache cache) {
      LayoutOperator layOp = dc.getLayoutOperator();
      //margin values are located second
      areas[5] = y;
      areas[7] = h;
      ByteObject tblr = null;
      if ((tblr = getStyleElement(style, STYLE_FLAG_A_5_MARGIN)) != null) {
         int mt = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_0_TOP, c) : cache.getStyleHMarginTop();
         int mb = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_1_BOT, c) : cache.getStyleHMarginBot();
         y += mt;
         h = h - mt - mb;
      }
      //border values are located first
      areas[1] = y;
      areas[3] = h;
      if ((tblr = getStyleElement(style, STYLE_FLAG_A_4_BORDER)) != null) {
         int mt = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_0_TOP, c) : cache.getStyleHBorderTop();
         int mb = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_1_BOT, c) : cache.getStyleHBorderBot();
         y += mt;
         h = h - mt - mb;
      }
      //padding values are located last
      areas[13] = y;
      areas[15] = h;
      if ((tblr = getStyleElement(style, STYLE_FLAG_A_3_PADDING)) != null) {
         int mt = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_0_TOP, c) : cache.getStyleHPaddingTop();
         int mb = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_1_BOT, c) : cache.getStyleHPaddingBot();
         y += mt;
         h = h - mt - mb;
      }
      //content values are located third
      areas[9] = y;
      areas[11] = h;
   }

   /**
    * @param x coordinate of the whole area
    * @param y coordinate of the content area
    * @param w  width of the whole area
    * @param h height of the content area
    * @param style
    * @param c
    * @return
    */
   public void getStyleAreasFullW(int x, int y, int w, int h, ByteObject style, ILayoutable c, int[] areas, StyleCache cache) {
      LayoutOperator layOp = dc.getLayoutOperator();
      //margin values are located second
      areas[4] = x;
      areas[6] = w;
      ByteObject tblr = null;
      if ((tblr = getStyleElement(style, STYLE_FLAG_A_5_MARGIN)) != null) {
         int ml = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_2_LEFT, c) : cache.getStyleWMarginLeft();
         int mr = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_3_RIGHT, c) : cache.getStyleWMarginRite();
         x += ml;
         w = w - ml - mr;
      }
      //border values are located first
      areas[0] = x;
      areas[2] = w;
      if ((tblr = getStyleElement(style, STYLE_FLAG_A_4_BORDER)) != null) {
         int ml = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_2_LEFT, c) : cache.getStyleWBorderLeft();
         int mr = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_3_RIGHT, c) : cache.getStyleWBorderRite();
         x += ml;
         w = w - ml - mr;
      }
      //padding values are located last
      areas[12] = x;
      areas[14] = w;
      if ((tblr = getStyleElement(style, STYLE_FLAG_A_3_PADDING)) != null) {
         int ml = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_2_LEFT, c) : cache.getStyleWPaddingLeft();
         int mr = (cache == null) ? layOp.getTBLRValue(tblr, C.POS_3_RIGHT, c) : cache.getStyleWPaddingRite();
         x += ml;
         w = w - ml - mr;
      }
      //content values are located third
      areas[8] = x;
      areas[10] = w;
   }

   public int[] getStyleAreasFullWContentH(int x, int y, int w, int h, ByteObject style, ILayoutable c, StyleCache cache) {
      int[] areas = new int[16];
      getStyleAreasContentH(x, y, w, h, style, c, areas, cache);
      getStyleAreasFullW(x, y, w, h, style, c, areas, cache);
      return areas;
   }

   /**
    * Returns an Array of the style's 4 BG layers
    * @param style
    * @return 4 sized array or null if none
    */
   public ByteObject[] getStyleBgs(ByteObject style) {
      int flag = style.get1(STYLE_OFFSET_2_FLAG_B);
      if ((flag & 0x00001111b) == 0) {
         return null;
      } else {
         int bgIndex = 0;
         ByteObject[] bgs = new ByteObject[4];
         bgIndex = styleLayerPut(style, bgs, 0, flag, STYLE_FLAG_B_1_BG, bgIndex);
         bgIndex = styleLayerPut(style, bgs, 1, flag, STYLE_FLAG_B_2_BG, bgIndex);
         bgIndex = styleLayerPut(style, bgs, 2, flag, STYLE_FLAG_B_3_BG, bgIndex);
         bgIndex = styleLayerPut(style, bgs, 3, flag, STYLE_FLAG_B_4_BG, bgIndex);
         return bgs;
      }
   }

   public int getStyleBorder(ByteObject p, int pos) {
      ByteObject tblr = getStyleElement(p, STYLE_FLAG_A_4_BORDER);
      if (tblr != null) {
         return dc.getLayoutOperator().getTBLRValue(tblr, pos);
      }
      return 0;
   }

   /**
    * The border pixel value for the style and tblr position and drawable
    * @param style
    * @param pos
    * @param c
    * @return
    */
   public int getStyleBorder(ByteObject style, int pos, ILayoutable c) {
      ByteObject tblr = getStyleElement(style, STYLE_FLAG_A_4_BORDER);
      if (tblr != null) {
         return dc.getLayoutOperator().getTBLRValue(tblr, pos, c);
      }
      return 0;
   }

   public int getStyleBorderBot(ByteObject style, ILayoutable c) {
      return getStyleBorder(style, C.POS_1_BOT, c);
   }

   public int getStyleBorderLeft(ByteObject style, ILayoutable c) {
      return getStyleBorder(style, C.POS_2_LEFT, c);
   }

   public int getStyleBorderRite(ByteObject style, ILayoutable c) {
      return getStyleBorder(style, C.POS_3_RIGHT, c);
   }

   public int getStyleBorderTop(ByteObject style, ILayoutable c) {
      return getStyleBorder(style, C.POS_0_TOP, c);
   }

   public int getStyleBotHConsumed(ByteObject p) {
      return getStyleSizeConsumed(p, C.POS_1_BOT);
   }

   public int getStyleBotHConsumed(ByteObject p, ILayoutable c) {
      return getStyleSizeConsumed(p, C.POS_1_BOT, c);
   }

   /**
    * Look up figure in style.
    * <li> 0 for content
    * <li> 1 for Bg1
    * <li> 8 for fg8
    * 
    * etc.
    * @param figure
    * @return -1 if figure is not a DLayer of this style
    */
   public int getStyleDLayerPosition(ByteObject style, ByteObject figure) {
      int val = -1;
      if (getStyleDrw(style, STYLE_OFFSET_1_FLAG_A, STYLE_FLAG_A_1_CONTENT) == figure) {
         val = 0;
      } else {
         if (getStyleDrw(style, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_1_BG) == figure) {
            val = 1;
         } else if (getStyleDrw(style, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_2_BG) == figure) {
            val = 2;
         } else if (getStyleDrw(style, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_3_BG) == figure) {
            val = 3;
         } else if (getStyleDrw(style, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_4_BG) == figure) {
            val = 4;
         } else if (getStyleDrw(style, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_5_FG) == figure) {
            val = 5;
         } else if (getStyleDrw(style, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_6_FG) == figure) {
            val = 6;
         } else if (getStyleDrw(style, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_7_FG) == figure) {
            val = 7;
         } else if (getStyleDrw(style, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_8_FG) == figure) {
            val = 8;
         }
      }
      return val;
   }

   /**
    * Gets the style component for that style, flag offset and flag.
    * <br>
    * <br>
    * @see IBOPointer#POINTER_FLAG_8_FLAG_ORDERING
    * @param flagtype the offset of the flag byte
    * @param flag the flag bit 
    * @return null if no param for that flag
    * @throws IllegalStateException if the style flag does not have
    * @throws ArrayIndexOutOfBoundsException ByteObject does not have it
    */
   public ByteObject getStyleDrw(ByteObject style, int flagtype, int flag) {
      if (style.hasFlag(flagtype, flag)) {
         int sx = getStyleElementPosition(style, flagtype, flag);
         if (sx != -1) {
            return style.getSubAtIndex(sx);
         } else {
            throw new IllegalStateException("Style flag tells us we must have a element");
         }
      }
      return null;
   }

   /**
    * Get the member position at flag for elements at {@link IBOStyle#STYLE_OFFSET_1_FLAG_A}
    * <br>
    * <br>
    * @param style
    * @param flag
    * @return
    */
   public ByteObject getStyleElement(ByteObject style, int flag) {
      if (style.hasFlag(STYLE_OFFSET_1_FLAG_A, flag)) {
         int sx = style.getFlagCount(STYLE_OFFSET_1_FLAG_A, flag);
         if (sx != -1) {
            try {
               return style.getSubAtIndex(sx);
            } catch (ArrayIndexOutOfBoundsException e) {
               //#mdebug
               String msg = "Index sx " + sx + " invalid for STYLE_OFFSET_1_FLAGV " + flag;
               //we want to debug byteObject raw only
               Dctx dc = new Dctx(boc.getUC());
               //override any configured flags on boc
               dc.setFlagToString(boc, IToStringFlagsBO.TOSTRING_FLAG_3_IGNORE_CONTENT, true);
               dc.append(msg);
               dc.nl();
               style.toString(dc);
               //#debug
               toDLog().pNull(dc.toString(), null, StyleOperator.class, "getStyleElement", LVL_05_FINE, true);
               //#enddebug
               throw e;
            }
         } else {
            throw new IllegalStateException();
         }
      }
      return null;
   }

   /**
    * Get the 0-based position in the array of {@link ByteObject} for the element of style defined by that flag and pointer.
    * <br>
    * <br>
    * @param flag
    * @return
    */
   public int getStyleElementPosition(ByteObject style, int pointer, int flag) {
      int val = style.getFlagCount(pointer, flag);
      if (pointer == STYLE_OFFSET_1_FLAG_A) {
         return style.getFlagCount(pointer, flag);
      } else if (pointer == STYLE_OFFSET_2_FLAG_B) {
         int flags = style.get1(STYLE_OFFSET_1_FLAG_A);
         val += BitUtils.countBits(flags);
      } else if (pointer == STYLE_OFFSET_3_FLAG_C) {
         int flags = style.get1(STYLE_OFFSET_1_FLAG_A);
         val += BitUtils.countBits(flags);
         flags = style.get1(STYLE_OFFSET_2_FLAG_B);
         val += BitUtils.countBits(flags);
      } else if (pointer == STYLE_OFFSET_4_FLAG_F) {
         int flags = style.get1(STYLE_OFFSET_1_FLAG_A);
         val += BitUtils.countBits(flags);
         flags = style.get1(STYLE_OFFSET_2_FLAG_B);
         val += BitUtils.countBits(flags);
         flags = style.get1(STYLE_OFFSET_3_FLAG_C);
         val += BitUtils.countBits(flags);
      }
      return val;
   }

   /**
    * 
    * @param style
    * @return
    */
   public IMFont getStyleFont(ByteObject style) {
      ByteObject txt = getContentStyle(style);
      if (txt != null) {
         //System.out.println("Presentation#getFont " + txt);
         return dc.getStrAuxOperator().getStringFont(txt);
      }
      return dc.getCoreDrawCtx().getFontFactory().getDefaultFont();
   }

   public int getStyleFontColor(ByteObject style) {
      ByteObject txt = getContentStyle(style);
      if (txt != null) {
         return dc.getStrAuxOperator().getStringColor(txt);
      }
      return IColors.FULLY_OPAQUE_GREY;
   }

   public int getStyleHConsumed(ByteObject style) {
      return getStyleTopHConsumed(style) + getStyleBotHConsumed(style);
   }

   public int getStyleHConsumed(ByteObject style, ILayoutable c) {
      return getStyleTopHConsumed(style, c) + getStyleBotHConsumed(style, c);
   }

   /**
    * The anchor of the layer which was set using 
    * @param style
    * @param flag {@link ByteObject#STYLE_FLAG_B_1_BG} - {@link ByteObject#STYLE_FLAG_B_8_FG} 
    * @param anc value depending on flag
    * 1 3 4 7
    * @return
    */
   public int getStyleLayerAnchor(ByteObject style, int flag, int anc) {
      int val = style.get1(STYLE_OFFSET_7_BG_POINTS1);
      if (flag > 8) {
         val = style.get1(STYLE_OFFSET_8_FG_POINTS1);
      }
      int p = (BitUtils.getBit(anc + 1, val) << 1) + BitUtils.getBit(anc, val);
      return p;
   }

   public int getStyleLeftWConsumed(ByteObject p) {
      return getStyleSizeConsumed(p, C.POS_2_LEFT);
   }

   public int getStyleLeftWConsumed(ByteObject p, ILayoutable c) {
      return getStyleSizeConsumed(p, C.POS_2_LEFT, c);
   }

   public int getStyleMargin(ByteObject p, int pos) {
      ByteObject tblr = getStyleElement(p, STYLE_FLAG_A_5_MARGIN);
      if (tblr != null) {
         return dc.getLayoutOperator().getTBLRValue(tblr, pos);
      }
      return 0;
   }

   public int getStyleMargin(ByteObject style, int pos, ILayoutable c) {
      ByteObject tblr = getStyleElement(style, STYLE_FLAG_A_5_MARGIN);
      if (tblr != null) {
         return dc.getLayoutOperator().getTBLRValue(tblr, pos, c);
      }
      return 0;
   }

   public int getStyleMarginBot(ByteObject style, ILayoutable c) {
      return getStyleMargin(style, C.POS_1_BOT, c);
   }

   public int getStyleMarginLeft(ByteObject style, ILayoutable c) {
      return getStyleMargin(style, C.POS_2_LEFT, c);
   }

   public int getStyleMarginRite(ByteObject style, ILayoutable c) {
      return getStyleMargin(style, C.POS_3_RIGHT, c);
   }

   public int getStyleMarginTop(ByteObject style, ILayoutable c) {
      return getStyleMargin(style, C.POS_0_TOP, c);
   }

   /**
    * When no padding is defined, returns zero
    * <br>
    * {@link ByteObject} style must have
    * @param style
    * @param pos {@link C#POS_0_TOP} .. {@link C#POS_3_RIGHT}
    * @param size the context for sizing the padding
    * @return
    */
   public int getStylePadding(ByteObject style, int pos) {
      //fetch the padding param of this layer and draws it
      ByteObject tblr = getStyleElement(style, STYLE_FLAG_A_3_PADDING);
      if (tblr != null) {
         return getStyleValue(tblr, pos);
      }
      return 0;
   }

   /**
    * When no padding is defined, returns zero
    * <br>
    * {@link ByteObject} style must have
    * @param style
    * @param pos {@link C#POS_0_TOP} .. {@link C#POS_3_RIGHT}
    * @param size the context for sizing the padding
    * @return
    */
   public int getStylePadding(ByteObject style, int pos, ILayoutable c) {
      //fetch the border param of this layer and draws it
      ByteObject tblr = getStyleElement(style, STYLE_FLAG_A_3_PADDING);
      if (tblr != null) {
         return dc.getLayoutOperator().getTBLRValue(tblr, pos, c);
      }
      return 0;
   }

   public int getStylePaddingBot(ByteObject style, ILayoutable c) {
      return getStylePadding(style, C.POS_1_BOT, c);
   }

   public int getStylePaddingLeft(ByteObject style, ILayoutable c) {
      return getStylePadding(style, C.POS_2_LEFT, c);
   }

   public int getStylePaddingRite(ByteObject style, ILayoutable c) {
      return getStylePadding(style, C.POS_3_RIGHT, c);
   }

   public int getStylePaddingTop(ByteObject style, ILayoutable c) {
      return getStylePadding(style, C.POS_0_TOP, c);
   }

   /**
    * Read flag and read value
    * @param ar
    * @param index
    * @param posColumns
    * @return
    */

   public int getStyleRightWConsumed(ByteObject p) {
      return getStyleSizeConsumed(p, C.POS_3_RIGHT);
   }

   public int getStyleRightWConsumed(ByteObject p, ILayoutable c) {
      return getStyleSizeConsumed(p, C.POS_3_RIGHT, c);
   }

   /**
    * TODO what if contextual of w and h?
    * @param p
    * @param dir
    * @return
    */
   public int getStyleSizeConsumed(ByteObject p, int dir) {
      return getStylePadding(p, dir) + getStyleMargin(p, dir) + getStyleBorder(p, dir);
   }

   public int getStyleSizeConsumed(ByteObject p, int dir, ILayoutable c) {
      return getStylePadding(p, dir, c) + getStyleMargin(p, dir, c) + getStyleBorder(p, dir, c);
   }

   /**
    * Adds all top values for padding, border and margin.
    * <br>
    * @param p
    * @return
    */
   public int getStyleTopHConsumed(ByteObject p) {
      return getStyleSizeConsumed(p, C.POS_0_TOP);
   }

   public int getStyleTopHConsumed(ByteObject p, ILayoutable c) {
      return getStyleSizeConsumed(p, C.POS_0_TOP, c);
   }

   private int getStyleValue(ByteObject tblr, int pos) {
      if (tblr != null) {
         int type = tblr.getType();
         if (type == IBOTypesLayout.FTYPE_2_TBLR) {
            int val = dc.getLayoutOperator().getTBLRValue(tblr, pos);
            return val;
         }
      }
      return 0;
   }

   public int getStyleWConsumed(ByteObject style) {
      return getStyleLeftWConsumed(style) + getStyleRightWConsumed(style);
   }

   public int getStyleWConsumed(ByteObject style, ILayoutable c) {
      return getStyleLeftWConsumed(style, c) + getStyleRightWConsumed(style, c);
   }

   public boolean isOpaqueBgLayersStyle(ByteObject style) {
      int[] areas = computeNewStyleAreas(0, 0, 10, 10, style);
      return isOpaqueBgLayersStyle(style, areas);
   }

   /**
    * A Drawable with a margin,border or padding and no bg layers is not opaque.
    * <br>
    * If Drawable is modified structurally
    * <br>
    * Performance wise, it is important to know when caching if an opaque bg layer is drawn over the whole area.
    * <br>
    * This color becomes the bgcolor of the {@link IDrawable}.
    * @param d
    * @return
    */
   public boolean isOpaqueBgLayersStyle(ByteObject style, int[] areas) {
      boolean bg1 = isStyleFigureOpaque(style, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_1_BG, areas, 1);
      if (bg1) {
         return true;
      }
      boolean bg2 = isStyleFigureOpaque(style, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_2_BG, areas, 3);
      if (bg2) {
         return true;
      }
      boolean bg3 = isStyleFigureOpaque(style, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_3_BG, areas, 5);
      if (bg3) {
         return true;
      }
      boolean bg4 = isStyleFigureOpaque(style, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_4_BG, areas, 7);
      if (bg4) {
         return true;
      }
      return false;
   }

   /**
    * Check if Style figure is an opaque rectangle drawn at margin
    * @param style
    * @param g
    * @param x
    * @param y
    * @param w
    * @param h
    * @param pointer
    * @param flag
    * @param areas
    * @param anc
    * @return
    */
   public boolean isStyleFigureOpaque(ByteObject style, int pointer, int flag, int[] areas, int anc) {
      ByteObject bg = getStyleDrw(style, pointer, flag);
      if (bg != null) {
         final int type = bg.getValue(IBOFigure.FIG__OFFSET_01_TYPE1, 1);
         if (type == FIG_TYPE_01_RECTANGLE) {
            if (bg.hasFlag(IBOFigure.FIG__OFFSET_03_FLAGP, IBOFigure.FIG_FLAGP_3_OPAQUE)) {
               return true;
            }
         }
      }
      return false;
   }

   private void mergeAnchorPoints(ByteObject root, ByteObject merge, ByteObject styleResult) {
      int anchorRoot = root.get1(STYLE_OFFSET_7_BG_POINTS1);
      //merges layer anchoring
      int a1 = 0;
      if (merge.hasFlag(STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_1_BG)) {
         a1 = getStyleLayerAnchor(merge, STYLE_FLAG_B_1_BG, 1);
         if (a1 != 0) {
            anchorRoot = (anchorRoot & 0x03) + a1 << 0;
         }
      }
      if (merge.hasFlag(STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_2_BG)) {
         a1 = getStyleLayerAnchor(merge, STYLE_FLAG_B_2_BG, 3);
         if (a1 != 0) {
            anchorRoot = (anchorRoot & 0x0C) + a1 << 2;
         }
      }
      if (merge.hasFlag(STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_3_BG)) {
         a1 = getStyleLayerAnchor(merge, STYLE_FLAG_B_3_BG, 5);
         if (a1 != 0) {
            anchorRoot = (anchorRoot & 0x30) + a1 << 4;
         }
      }
      if (merge.hasFlag(STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_4_BG)) {
         a1 = getStyleLayerAnchor(merge, STYLE_FLAG_B_4_BG, 7);
         if (a1 != 0) {
            anchorRoot = (anchorRoot & 0xC0) + a1 << 6;
         }
      }
      styleResult.setValue(STYLE_OFFSET_7_BG_POINTS1, anchorRoot, 1);
      a1 = 0;

      anchorRoot = root.get1(STYLE_OFFSET_8_FG_POINTS1);
      if (merge.hasFlag(STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_5_FG)) {
         a1 = getStyleLayerAnchor(merge, STYLE_FLAG_B_5_FG, 1);
         if (a1 != 0) {
            anchorRoot = (anchorRoot & 0x03) + a1 << 0;
         }
      }
      if (merge.hasFlag(STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_6_FG)) {
         a1 = getStyleLayerAnchor(merge, STYLE_FLAG_B_6_FG, 3);
         if (a1 != 0) {
            anchorRoot = (anchorRoot & 0x0C) + a1 << 2;
         }
      }
      if (merge.hasFlag(STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_7_FG)) {
         a1 = getStyleLayerAnchor(merge, STYLE_FLAG_B_7_FG, 5);
         if (a1 != 0) {
            anchorRoot = (anchorRoot & 0x30) + a1 << 4;
         }
      }
      if (merge.hasFlag(STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_8_FG)) {
         a1 = getStyleLayerAnchor(merge, STYLE_FLAG_B_8_FG, 7);
         if (a1 != 0) {
            anchorRoot = (anchorRoot & 0xC0) + a1 << 6;
         }
      }
      styleResult.set1(STYLE_OFFSET_8_FG_POINTS1, anchorRoot);
   }

   /**
    * Helper method of mergeStyle procedure
    * @param style result of the merge
    * @param root
    * @param merge
    * @param pointer
    * @param flag
    * @param ar
    * @param count
    * @return
    */
   private void mergeSet(ByteObject style, ByteObject root, ByteObject merge, int pointer, int flag, ByteObject[] ar, int[] count) {
      ByteObject rootElement = getStyleDrw(root, pointer, flag);
      ByteObject mergeElement = getStyleDrw(merge, pointer, flag);
      if (mergeElement != null) {
         count[1]++;
      }
      BOModulesManager boModuleManager = dc.getBOC().getBOModuleManager();
      ByteObject me = boModuleManager.mergeByteObject(rootElement, mergeElement);
      if (me != null) {
         int index = count[0]++;
         ar[index] = me;
         style.setFlag(pointer, flag, true);
      }
   }

   /**
    * Return a new style with flag {@link IBOStyle#STYLE_FLAG_X_2_MERGED}
    * <p> 
    * Merges Layer Anchors 
    * </p>
    * @param root bottom style whose members will be erased/merged
    * @param merge top style that is merging over the bottom style
    * @return
    */
   public ByteObject mergeStyle(ByteObject root, ByteObject merge) {
      //force the pooling
      ByteObject styleResult = dc.getStyleFactory().createStyle();

      styleResult.setFlag(STYLE_OFFSET_5_FLAG_X, STYLE_FLAG_X_2_MERGED, true);

      //#debug
      root.checkType(TYPE_DRWX_08_STYLE);

      //#mdebug
      if (merge != null) {
         merge.checkType(TYPE_DRWX_08_STYLE);
      }
      //#enddebug

      //build the merged array of sub byteobjects
      ByteObject[] ar = new ByteObject[24];

      //this enables the debugger to print the object while debugging
      //#debug
      styleResult.setByteObjects(ar);

      int[] counters = new int[2];
      mergeSet(styleResult, root, merge, STYLE_OFFSET_1_FLAG_A, STYLE_FLAG_A_1_CONTENT, ar, counters);
      mergeSet(styleResult, root, merge, STYLE_OFFSET_1_FLAG_A, STYLE_FLAG_A_2_ANCHOR, ar, counters);

      int z = counters[1];
      mergeSet(styleResult, root, merge, STYLE_OFFSET_1_FLAG_A, STYLE_FLAG_A_3_PADDING, ar, counters);
      mergeSet(styleResult, root, merge, STYLE_OFFSET_1_FLAG_A, STYLE_FLAG_A_4_BORDER, ar, counters);
      mergeSet(styleResult, root, merge, STYLE_OFFSET_1_FLAG_A, STYLE_FLAG_A_5_MARGIN, ar, counters);
      if (z != counters[1]) {
         styleResult.setFlag(STYLE_OFFSET_5_FLAG_X, STYLE_FLAG_X_3_MERGED_STRUCT, true);
      }

      mergeSet(styleResult, root, merge, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_1_BG, ar, counters);
      mergeSet(styleResult, root, merge, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_2_BG, ar, counters);
      mergeSet(styleResult, root, merge, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_3_BG, ar, counters);
      mergeSet(styleResult, root, merge, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_4_BG, ar, counters);
      mergeSet(styleResult, root, merge, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_5_FG, ar, counters);
      mergeSet(styleResult, root, merge, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_6_FG, ar, counters);
      mergeSet(styleResult, root, merge, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_7_FG, ar, counters);
      mergeSet(styleResult, root, merge, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_8_FG, ar, counters);

      z = counters[1];
      mergeSet(styleResult, root, merge, STYLE_OFFSET_3_FLAG_C, STYLE_FLAG_C_6_ANIM_ENTRY, ar, counters);
      mergeSet(styleResult, root, merge, STYLE_OFFSET_3_FLAG_C, STYLE_FLAG_C_7_ANIM_MAIN, ar, counters);
      mergeSet(styleResult, root, merge, STYLE_OFFSET_3_FLAG_C, STYLE_FLAG_C_8_ANIM_EXIT, ar, counters);
      if (z != counters[1]) {
         styleResult.setFlag(STYLE_OFFSET_5_FLAG_X, STYLE_FLAG_X_4_MERGED_ANIM, true);
      }

      mergeSet(styleResult, root, merge, STYLE_OFFSET_4_FLAG_F, STYLE_FLAG_F_1_FILTER_BG, ar, counters);
      mergeSet(styleResult, root, merge, STYLE_OFFSET_4_FLAG_F, STYLE_FLAG_F_2_FILTER_CONTENT, ar, counters);
      mergeSet(styleResult, root, merge, STYLE_OFFSET_4_FLAG_F, STYLE_FLAG_F_3_FILTER_FG, ar, counters);
      mergeSet(styleResult, root, merge, STYLE_OFFSET_4_FLAG_F, STYLE_FLAG_F_4_FILTER_BG_CONTENT, ar, counters);
      mergeSet(styleResult, root, merge, STYLE_OFFSET_4_FLAG_F, STYLE_FLAG_F_5_FILTER_ALL, ar, counters);

      mergeAnchorPoints(root, merge, styleResult);

      styleResult.setByteObjectsTrimmed(ar);
      //first do text for both bot and up
      return styleResult;

   }

   /**
    * When the {@link ByteObject} is not null, sets the flag in root
    * 
    * This method provides a safety check.. {@link ByteObject#getType()} must be the same as Type
    * otherwise an {@link IllegalArgumentException} is thrown.
    * <br>
    * Special case with {@link IBOTypesBOC#TYPE_025_ACTION}
    * <br>
    * @param field
    * @param root
    * @param flag
    * @param type
    * @return
    */
   public int setFlagNotNullStyleFieldFlag(ByteObject field, int root, int flag, int type) {
      if (field != null) {
         //a style element can always be an ACTION
         if (field.getType() != IBOTypesBOC.TYPE_025_ACTION) {
            field.checkType(type);
         }
         root |= flag;
      }
      return root;

   }

   /**
    * When the {@link ByteObject} is not null, sets the flag in root
    * @param field
    * @param root
    * @param flag
    * @return
    */
   public int setFlagWhenNotNull(ByteObject field, int root, int flag) {
      if (field != null) {
         root |= flag;
      }
      return root;
   }

   /**
    * <p>
    * flag is
    * <li> {@link IBOStyle#STYLE_FLAG_B_1_BG}
    * <li> {@link IBOStyle#STYLE_FLAG_B_2_BG}
    * <li> {@link IBOStyle#STYLE_FLAG_B_3_BG}
    * <li> {@link IBOStyle#STYLE_FLAG_B_4_BG}
    * </p>
    * 
    * anc is
    * <li> {@link ITechStyle#STYLE_ANC_0_BORDER}
    * <li> {@link ITechStyle#STYLE_ANC_1_MARGIN}
    * <li> {@link ITechStyle#STYLE_ANC_2_CONTENT}
    * <li> {@link ITechStyle#STYLE_ANC_3_PADDING}
    * @param style
    * @param flag
    * @param anc
    */
   public void setGAnchors(ByteObject style, int flag, int anc) {
      int off = STYLE_OFFSET_7_BG_POINTS1;
      int n = 1;
      if (flag > 8) {
         off = STYLE_OFFSET_8_FG_POINTS1;
         if (flag == STYLE_FLAG_B_6_FG)
            n = 3;
         if (flag == STYLE_FLAG_B_7_FG)
            n = 5;
         if (flag == STYLE_FLAG_B_8_FG)
            n = 7;
      } else {
         //1 = 1, 2 = 3, 4 = 5, 8 = 7
         if (flag == STYLE_FLAG_B_2_BG)
            n = 3;
         if (flag == STYLE_FLAG_B_3_BG)
            n = 5;
         if (flag == STYLE_FLAG_B_4_BG)
            n = 7;
      }

      int val = style.get1(off);
      val = BitUtils.setBit(val, n, BitUtils.getBit(1, anc));
      val = BitUtils.setBit(val, n + 1, BitUtils.getBit(2, anc));
      style.setValue(off, val, 1);
   }

   public void setGAnchors2Content(ByteObject style) {
      this.setGAnchors(style, STYLE_FLAG_B_2_BG, ITechStyle.STYLE_ANC_2_CONTENT);
   }

   public void setGAnchorsBG1_Margin(ByteObject style) {
      this.setGAnchors(style, STYLE_FLAG_B_1_BG, ITechStyle.STYLE_ANC_1_MARGIN);
   }

   public void setGAnchorsBG2_Margin(ByteObject style) {
      this.setGAnchors(style, STYLE_FLAG_B_2_BG, ITechStyle.STYLE_ANC_1_MARGIN);
   }

   public void setGAnchorsBG3_Margin(ByteObject style) {
      this.setGAnchors(style, STYLE_FLAG_B_3_BG, ITechStyle.STYLE_ANC_1_MARGIN);
   }

   public void setGAnchorsFG1_Content(ByteObject style) {
      this.setGAnchors(style, STYLE_FLAG_B_5_FG, ITechStyle.STYLE_ANC_2_CONTENT);
   }

   public void setGAnchorsFG1_Margin(ByteObject style) {
      this.setGAnchors(style, STYLE_FLAG_B_5_FG, ITechStyle.STYLE_ANC_1_MARGIN);
   }

   public void setStyleIncomplete(ByteObject style) {
      style.setFlag(STYLE_OFFSET_5_FLAG_X, STYLE_FLAG_X_1_INCOMPLETE, true);
   }

   private int setWhenNotNullStyle(ByteObject style, ByteObject field, int count) {
      if (field != null) {
         style.setSub(field, count);
         return count + 1;
      }
      return count;
   }

   public int styleCreationLastFill(ByteObject sty, ByteObject field, int count) {
      if (field != null) {
         sty.setSub(field, count);
         return count + 1;
      }
      return count;
   }

   public int styleCreationLastFill(ByteObject sty, ByteObject[] fields, int count) {
      if (fields != null) {
         for (int i = 0; i < fields.length; i++) {
            if (fields[i] != null) {
               sty.setSub(fields[i], count);
               count++;
            }
         }
         return count;
      }
      return count;
   }

   /**
    * 
    * @param rootPart
    * @param partOver
    * @return
    */
   public ByteObject styleGetPart(ByteObject rootPart, ByteObject partOver) {
      if (partOver == null) {
         return rootPart;
      } else {
         if (partOver.getType() == TYPE_025_ACTION) {
            return dc.getBOC().getActionOp().doActionFunctorClone(partOver, rootPart);
         } else {
            //each part is checked for existence
            return partOver;
         }
      }
   }

   public int styleLayerPut(ByteObject style, ByteObject[] dest, int destoffset, int flags, int flag, int bgIndex) {
      if ((flags & flag) == flag) {
         dest[destoffset] = style.getSubAtIndex(bgIndex);
         bgIndex++;
      }
      return bgIndex;
   }

   public ByteObject styleMergeOver(ByteObject root, ByteObject styleO) {
      root.checkType(TYPE_DRWX_08_STYLE);
      styleO.checkType(TYPE_DRWX_08_STYLE);

      //create a new possible
      ByteObject content = styleMergePartOver(root, STYLE_OFFSET_1_FLAG_A, STYLE_FLAG_A_1_CONTENT, styleO);
      ByteObject anchor = styleMergePartOver(root, STYLE_OFFSET_1_FLAG_A, STYLE_FLAG_A_2_ANCHOR, styleO);
      ByteObject pad = styleMergePartOver(root, STYLE_OFFSET_1_FLAG_A, STYLE_FLAG_A_3_PADDING, styleO);
      ByteObject border = styleMergePartOver(root, STYLE_OFFSET_1_FLAG_A, STYLE_FLAG_A_4_BORDER, styleO);
      ByteObject margin = styleMergePartOver(root, STYLE_OFFSET_1_FLAG_A, STYLE_FLAG_A_5_MARGIN, styleO);

      ByteObject[] bg = null;
      if (root.hasFlag(STYLE_OFFSET_6_FLAG_PERF, STYLE_FLAG_PERF_1_BG)) {
         bg = new ByteObject[4];
         bg[0] = styleMergePartOver(root, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_1_BG, styleO);
         bg[1] = styleMergePartOver(root, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_2_BG, styleO);
         bg[2] = styleMergePartOver(root, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_3_BG, styleO);
         bg[3] = styleMergePartOver(root, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_4_BG, styleO);
      }
      ByteObject[] fg = null;
      if (root.hasFlag(STYLE_OFFSET_6_FLAG_PERF, STYLE_FLAG_PERF_2_FG)) {
         fg = new ByteObject[4];
         fg[0] = styleMergePartOver(root, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_5_FG, styleO);
         fg[1] = styleMergePartOver(root, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_6_FG, styleO);
         fg[2] = styleMergePartOver(root, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_7_FG, styleO);
         fg[3] = styleMergePartOver(root, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_8_FG, styleO);
      }
      ByteObject[] filters = null;
      if (root.hasFlag(STYLE_OFFSET_6_FLAG_PERF, STYLE_FLAG_PERF_3_FILTERS)) {
         filters = new ByteObject[5];
         filters[0] = styleMergePartOver(root, STYLE_OFFSET_4_FLAG_F, STYLE_FLAG_F_1_FILTER_BG, styleO);
         filters[1] = styleMergePartOver(root, STYLE_OFFSET_4_FLAG_F, STYLE_FLAG_F_2_FILTER_CONTENT, styleO);
         filters[2] = styleMergePartOver(root, STYLE_OFFSET_4_FLAG_F, STYLE_FLAG_F_3_FILTER_FG, styleO);
         filters[3] = styleMergePartOver(root, STYLE_OFFSET_4_FLAG_F, STYLE_FLAG_F_4_FILTER_BG_CONTENT, styleO);
         filters[4] = styleMergePartOver(root, STYLE_OFFSET_4_FLAG_F, STYLE_FLAG_F_5_FILTER_ALL, styleO);
      }
      ByteObject[] anims = null;
      if (root.hasFlag(STYLE_OFFSET_6_FLAG_PERF, STYLE_FLAG_PERF_4_ANIMS)) {
         anims = new ByteObject[3];
         anims[0] = styleMergePartOver(root, STYLE_OFFSET_3_FLAG_C, STYLE_FLAG_C_6_ANIM_ENTRY, styleO);
         anims[1] = styleMergePartOver(root, STYLE_OFFSET_3_FLAG_C, STYLE_FLAG_C_7_ANIM_MAIN, styleO);
         anims[2] = styleMergePartOver(root, STYLE_OFFSET_3_FLAG_C, STYLE_FLAG_C_8_ANIM_EXIT, styleO);
      }

      ByteObject newStyle = dc.getStyleFactory().getStyle(bg, content, anchor, pad, border, margin, fg, filters, anims);
      return newStyle;
   }

   public ByteObject styleMergePartOver(ByteObject root, int offset, int flag, ByteObject over) {
      ByteObject rootPart = getStyleDrw(root, offset, flag);
      ByteObject overPart = getStyleDrw(over, offset, flag);
      return styleGetPart(rootPart, overPart);
   }

   /**
    * Replace or add item to style.
    * <br>
    * <br>
    * @param style
    * @param item
    * @param pointer
    * @param flag
    */
   public void styleSet(ByteObject style, ByteObject item, int pointer, int flag) {
      if (style.hasFlag(pointer, flag)) {
         //replace
         int pos = getStyleElementPosition(style, pointer, flag);
         style.setSub(item, pos);
      } else {
         //add
         int flagsValues = style.get1(pointer);
         flagsValues = setFlagNotNullStyleFieldFlag(item, flagsValues, flag, item.getType());
         style.setValue(pointer, flagsValues, 1);
         int pos = getStyleElementPosition(style, pointer, flag);
         style.insertByteObject(item, pos);
      }
   }

   //#mdebug
   public void toString1LineStyle(ByteObject bo, Dctx sb) {
      sb.append("#Style " + bo.getMyHashCode());
      sb.append("\t");
      sb.append("ConsumeTBLR[");
      //only possible to compute this ally if no context
      sb.append(getStyleTopHConsumed(bo));
      sb.append('-');
      sb.append(getStyleBotHConsumed(bo));
      sb.append(' ');
      sb.append(getStyleLeftWConsumed(bo));
      sb.append('-');
      sb.append(getStyleRightWConsumed(bo));

   }

   public void toStringFigFlag(Dctx sb, ByteObject p, int offset, int flag, String str) {
      if (p.hasFlag(offset, flag)) {
         sb.append(str);
      }
   }

   public void toStringStyle(ByteObject bo, Dctx db) {
      db.append("#Style " + bo.getMyHashCode());
      db.append("\t");
      db.append("ConsumeTBLR[");
      db.append(getStyleTopHConsumed(bo));
      db.append('-');
      db.append(getStyleBotHConsumed(bo));
      db.append(' ');
      db.append(getStyleLeftWConsumed(bo));
      db.append('-');
      db.append(getStyleRightWConsumed(bo));
      db.append(']');
      //TODO how to decide what to show here. by default we may not want to overload with too much data
      //so that when you select an object in debugger, you gets more data
      if (db.hasFlagToString(dc, IToStringFlagsDrw.D_FLAG_01_STYLE)) {
         db.appendWithNewLine("IFlagsToStringDrw.D_FLAG_01_STYLE is true. StyleData is Shown below.");
         if (bo.get1(STYLE_OFFSET_1_FLAG_A) != 0) {
            db.nl();
            db.append("FlagV:");
            toStringFigFlag(db, bo, STYLE_OFFSET_1_FLAG_A, STYLE_FLAG_A_1_CONTENT, " Content");
            toStringFigFlag(db, bo, STYLE_OFFSET_1_FLAG_A, STYLE_FLAG_A_2_ANCHOR, " Anchor");
            toStringFigFlag(db, bo, STYLE_OFFSET_1_FLAG_A, STYLE_FLAG_A_3_PADDING, " Padding");
            toStringFigFlag(db, bo, STYLE_OFFSET_1_FLAG_A, STYLE_FLAG_A_4_BORDER, " Border");
            toStringFigFlag(db, bo, STYLE_OFFSET_1_FLAG_A, STYLE_FLAG_A_5_MARGIN, " Margin");
         }
         if (bo.get1(STYLE_OFFSET_2_FLAG_B) != 0) {
            db.nl();
            db.append("FlagG:");
            toStringStyleGLayer(db, " Bg1", bo, STYLE_FLAG_B_1_BG, 1);
            toStringStyleGLayer(db, " Bg2", bo, STYLE_FLAG_B_2_BG, 3);
            toStringStyleGLayer(db, " Bg3", bo, STYLE_FLAG_B_3_BG, 5);
            toStringStyleGLayer(db, " Bg4", bo, STYLE_FLAG_B_4_BG, 7);
            toStringStyleGLayer(db, " Fg1", bo, STYLE_FLAG_B_5_FG, 1);
            toStringStyleGLayer(db, " Fg2", bo, STYLE_FLAG_B_6_FG, 3);
            toStringStyleGLayer(db, " Fg3", bo, STYLE_FLAG_B_7_FG, 5);
            toStringStyleGLayer(db, " Fg4", bo, STYLE_FLAG_B_8_FG, 7);
         }
         if (bo.get1(STYLE_OFFSET_3_FLAG_C) != 0) {
            db.nl();
            db.append("FlagC:");

            toStringFigFlag(db, bo, STYLE_OFFSET_3_FLAG_C, STYLE_FLAG_C_6_ANIM_ENTRY, " AnimEntry");
            toStringFigFlag(db, bo, STYLE_OFFSET_3_FLAG_C, STYLE_FLAG_C_7_ANIM_MAIN, " AnimMain");
            toStringFigFlag(db, bo, STYLE_OFFSET_3_FLAG_C, STYLE_FLAG_C_8_ANIM_EXIT, " AnimExit");
         }
         if (bo.get1(STYLE_OFFSET_4_FLAG_F) != 0) {
            db.nl();
            db.append("FlagF:");
            toStringFigFlag(db, bo, STYLE_OFFSET_4_FLAG_F, STYLE_FLAG_F_1_FILTER_BG, " FilterBg");
            toStringFigFlag(db, bo, STYLE_OFFSET_4_FLAG_F, STYLE_FLAG_F_2_FILTER_CONTENT, " FilterContent");
            toStringFigFlag(db, bo, STYLE_OFFSET_4_FLAG_F, STYLE_FLAG_F_3_FILTER_FG, " FilterFg");
            toStringFigFlag(db, bo, STYLE_OFFSET_4_FLAG_F, STYLE_FLAG_F_4_FILTER_BG_CONTENT, " FilterBgContent");
            toStringFigFlag(db, bo, STYLE_OFFSET_4_FLAG_F, STYLE_FLAG_F_5_FILTER_ALL, " FilterAll");
         }
      } else {
         db.appendWithNewLine("IFlagsToStringDrw.D_FLAG_01_STYLE is false. StyleData is Hidden.");
      }
   }

   public void toStringStyleGLayer(Dctx sb, String str, ByteObject style, int flag, int anc) {
      if (style.hasFlag(STYLE_OFFSET_2_FLAG_B, flag)) {
         sb.append(str);
         int val = style.get1(STYLE_OFFSET_7_BG_POINTS1);
         if (flag > 8) {
            val = style.get1(STYLE_OFFSET_8_FG_POINTS1);
         }
         int p = (BitUtils.getBit(anc + 1, val) << 1) + BitUtils.getBit(anc, val);
         sb.append("(");
         sb.append(ToStringStaticDrawx.toStringStyleAnchor(p));
         sb.append(")");

      }
   }
   //#enddebug
}
