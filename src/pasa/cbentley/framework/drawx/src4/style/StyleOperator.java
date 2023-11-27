/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.style;

import pasa.cbentley.byteobjects.src4.core.BOAbstractOperator;
import pasa.cbentley.byteobjects.src4.core.BOModulesManager;
import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesBOC;
import pasa.cbentley.byteobjects.src4.ctx.IFlagsToStringBO;
import pasa.cbentley.core.src4.interfaces.C;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.utils.BitUtils;
import pasa.cbentley.core.src4.utils.interfaces.IColors;
import pasa.cbentley.framework.coredraw.src4.interfaces.IMFont;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.drawx.src4.ctx.IFlagsToStringDrw;
import pasa.cbentley.framework.drawx.src4.ctx.ToStringStaticDrawx;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.factories.TblrFactory;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;
import pasa.cbentley.framework.drawx.src4.tech.ITechTblr;
import pasa.cbentley.layouter.src4.interfaces.ILayoutable;
import pasa.cbentley.layouter.src4.interfaces.ISizeCtx;
import pasa.cbentley.layouter.src4.tech.ITechLayout;

public class StyleOperator extends BOAbstractOperator implements ITechStyle, ITechFigure, IBOTypesDrw, ITechTblr {

   protected final DrwCtx dc;

   public StyleOperator(DrwCtx dc) {
      super(dc.getBOC());
      this.dc = dc;
   }

   public void debugFigFlag(Dctx sb, ByteObject p, int offset, int flag, String str) {
      if (p.hasFlag(offset, flag)) {
         sb.append(str);
      }
   }

   public void debugStyleGLayer(Dctx sb, String str, ByteObject style, int flag, int anc) {
      if (style.hasFlag(STYLE_OFFSET_2_FLAGB, flag)) {
         sb.append(str);
         int val = style.get1(STYLE_OFFSET_5_BG_POINTS1);
         if (flag > 8) {
            val = style.get1(STYLE_OFFSET_6_FG_POINTS1);
         }
         int p = (BitUtils.getBit(anc + 1, val) << 1) + BitUtils.getBit(anc, val);
         sb.append("(");
         sb.append(ToStringStaticDrawx.styleAnchor(p));
         sb.append(")");

      }
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
      boolean bg1 = isStyleFigureOpaque(style, ITechStyle.STYLE_OFFSET_2_FLAGB, ITechStyle.STYLE_FLAGB_1_BG, areas, 1);
      if (bg1) {
         return true;
      }
      boolean bg2 = isStyleFigureOpaque(style, ITechStyle.STYLE_OFFSET_2_FLAGB, ITechStyle.STYLE_FLAGB_2_BG, areas, 3);
      if (bg2) {
         return true;
      }
      boolean bg3 = isStyleFigureOpaque(style, ITechStyle.STYLE_OFFSET_2_FLAGB, ITechStyle.STYLE_FLAGB_3_BG, areas, 5);
      if (bg3) {
         return true;
      }
      boolean bg4 = isStyleFigureOpaque(style, ITechStyle.STYLE_OFFSET_2_FLAGB, ITechStyle.STYLE_FLAGB_4_BG, areas, 7);
      if (bg4) {
         return true;
      }
      return false;
   }

   public boolean isOpaqueBgLayersStyle(ByteObject style) {
      int[] areas = getStyleAreas(0, 0, 10, 10, style);
      return isOpaqueBgLayersStyle(style, areas);
   }

   /**
    * Draw style decoration (background + border + figure + foreground)
    * @param g
    * @param x coordinate
    * @param y
    * @param w
    * @param h
    * @param style
    */
   public void drawStyle(GraphicsX g, int x, int y, int w, int h, ByteObject style) {
      int[] areas = getStyleAreas(x, y, w, h, style);
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
      drawStyleFigure(style, g, x, y, w, h, STYLE_OFFSET_2_FLAGB, STYLE_FLAGB_1_BG, areas, 1);
      drawStyleFigure(style, g, x, y, w, h, STYLE_OFFSET_2_FLAGB, STYLE_FLAGB_2_BG, areas, 3);
      drawStyleFigure(style, g, x, y, w, h, STYLE_OFFSET_2_FLAGB, STYLE_FLAGB_3_BG, areas, 5);
      drawStyleFigure(style, g, x, y, w, h, STYLE_OFFSET_2_FLAGB, STYLE_FLAGB_4_BG, areas, 7);
   }

   public void drawStyleBg(GraphicsX g, ByteObject style, int x, int y, int w, int h) {
      int[] areas = getStyleAreas(x, y, w, h, style);
      drawStyleBg(style, g, x, y, w, h, areas);
   }

   public void drawStyleFg(ByteObject style, GraphicsX g, int x, int y, int w, int h, int[] areas) {
      drawStyleFigure(style, g, x, y, w, h, STYLE_OFFSET_2_FLAGB, STYLE_FLAGB_5_FG, areas, 1);
      drawStyleFigure(style, g, x, y, w, h, STYLE_OFFSET_2_FLAGB, STYLE_FLAGB_6_FG, areas, 3);
   }

   public void drawStyleFg(GraphicsX g, ByteObject style, int x, int y, int w, int h) {
      int[] areas = getStyleAreas(x, y, w, h, style);
      drawStyleFg(style, g, x, y, w, h, areas);
   }

   /**
    * Draws style figure in style area.
    * @param style
    * @param g
    * @param x
    * @param y
    * @param w
    * @param h
    * @param pointer pointer for the figure
    * @param flag flag identifiying the figure in the 8 possibilities
    */
   public void drawStyleFigure(ByteObject style, GraphicsX g, int x, int y, int w, int h, int pointer, int flag) {
      ByteObject bg = getStyleDrw(style, pointer, flag);
      if (bg != null) {
         int val = style.get1(STYLE_OFFSET_5_BG_POINTS1);
         int anchor = (BitUtils.getBit(2, val) << 1) + BitUtils.getBit(1, val);
         if (anchor == 0 && style.hasFlag(STYLE_OFFSET_1_FLAGA, STYLE_FLAGA_5_MARGIN)) {
            int ml = getStyleMargin(style, C.POS_2_LEFT);
            int mt = getStyleMargin(style, C.POS_0_TOP);
            x += ml;
            y += mt;
            w = w - ml - getStyleMargin(style, C.POS_3_RIGHT);
            h = h - mt - getStyleMargin(style, C.POS_1_BOT);
         } else if (anchor == 1) {
            //at content
            int ml = getStyleLeftWConsumed(style);
            int mt = getStyleTopHConsumed(style);
            x += ml;
            y += mt;
            w = w - ml - getStyleRightWConsumed(style);
            h = h - mt - getStyleBotHConsumed(style);
         } else if (anchor == 2) {
            //at padding
            if (style.hasFlag(STYLE_OFFSET_1_FLAGA, STYLE_FLAGA_5_MARGIN)) {
               int ml = getStyleMargin(style, C.POS_2_LEFT);
               int mt = getStyleMargin(style, C.POS_0_TOP);
               x += ml;
               y += mt;
               w = w - ml - getStyleMargin(style, C.POS_3_RIGHT);
               h = h - mt - getStyleMargin(style, C.POS_1_BOT);
            }
            if (style.hasFlag(STYLE_OFFSET_1_FLAGA, STYLE_FLAGA_4_BORDER)) {
               int ml = getStyleBorder(style, C.POS_2_LEFT);
               int mt = getStyleBorder(style, C.POS_0_TOP);
               x += ml;
               y += mt;
               w = w - ml - getStyleBorder(style, C.POS_3_RIGHT);
               h = h - mt - getStyleBorder(style, C.POS_1_BOT);
            }
         } else {
            //at margin don't change area
         }

         dc.getFigureOperator().paintFigure(g, x, y, w, h, bg);
      }
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
         int val = style.get1(STYLE_OFFSET_5_BG_POINTS1);
         if (flag > 8) {
            //we have a figure for FG layers
            val = style.get1(STYLE_OFFSET_6_FG_POINTS1);
         }
         int p = (BitUtils.getBit(anc + 1, val) << 1) + BitUtils.getBit(anc, val);
         //System.out.print(debugStyleAnchor(p));
         p = p * 4;
         int dx = areas[p];
         int dy = areas[p + 1];
         int dw = areas[p + 2];
         int dh = areas[p + 3];
         dc.getFigureOperator().paintFigure(g, dx, dy, dw, dh, bg);
      }
   }

   /**
    * Return the style element linked to {@link ITechStyle#STYLE_FLAGA_1_CONTENT}
    * <br>
    * When not load, returns default
    * <br>
    * <br>
    * @param style 
    * @return non null {@link ByteObject}
    */
   public ByteObject getContentStyle(ByteObject style) {
      return getStyleElement(style, ITechStyle.STYLE_FLAGA_1_CONTENT);
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

   /**
    * Gets a Style TBLR pixel size.
    * <br>
    * <br>
    * Sizer describes how to compute it.
    * <br>
    * {@link ISizer#SIZER_OFFSET_04_FUNCTION1}
    * <br>
    * What matters is the type of etalon to use
    * @param sizer
    * @param pos
    * @return
    */
   private int getPixelSizeTBLR(ByteObject sizer, int pos, ILayoutable c) {
      int ctxType = ITechLayout.CTX_1_WIDTH;
      if (pos == C.POS_0_TOP || pos == C.POS_1_BOT) {
         ctxType = ITechLayout.CTX_2_HEIGHT;
      }
      return dc.getLAC().getLayoutOperator().getPixelSize(sizer, c, ctxType);
   }

   private int getPixelSizeTBLR(int codedsizer, int pos, ILayoutable c) {
      int ctxType = ITechLayout.CTX_1_WIDTH;
      if (pos == C.POS_0_TOP || pos == C.POS_1_BOT) {
         ctxType = ITechLayout.CTX_2_HEIGHT;
      }
      return dc.getLAC().getLayoutOperator().codedSizeDecode(codedsizer, c, ctxType);
   }

   /**
    * Optimize often used style areas.
    * Compute x,y,w,h values for the 4 style structures
    * <br>
    * Those values will be used by style layers to draw themselves.
    * <br>
    * @param x
    * @param y
    * @param w
    * @param h
    * @param style
    * @return
    * 0-3 = at margin rectangle
    * 4-7 = at border rectangle
    * 8-11 = at padding rectangle
    * 12-15 = at content rectangle
    * 
    */
   public int[] getStyleAreas(int x, int y, int w, int h, ByteObject style, ILayoutable c) {
      int[] areas = new int[16];
      areas[4] = x;
      areas[5] = y;
      areas[6] = w;
      areas[7] = h;
      if (style.hasFlag(STYLE_OFFSET_1_FLAGA, STYLE_FLAGA_5_MARGIN)) {
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
      if (style.hasFlag(STYLE_OFFSET_1_FLAGA, STYLE_FLAGA_4_BORDER)) {
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
      if (style.hasFlag(STYLE_OFFSET_1_FLAGA, STYLE_FLAGA_3_PADDING)) {
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
      return style.getSubFirst(IBOTypesDrw.TYPE_051_BOX);
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
   public int[] getStyleAreas(int x, int y, int w, int h, ByteObject style) {
      int[] areas = new int[16];
      areas[4] = x;
      areas[5] = y;
      areas[6] = w;
      areas[7] = h;
      if (style.hasFlag(STYLE_OFFSET_1_FLAGA, STYLE_FLAGA_5_MARGIN)) {
         int ml = getStyleMargin(style, C.POS_2_LEFT);
         int mt = getStyleMargin(style, C.POS_0_TOP);
         x += ml;
         y += mt;
         w = w - ml - getStyleMargin(style, C.POS_3_RIGHT);
         h = h - mt - getStyleMargin(style, C.POS_1_BOT);
      }
      areas[0] = x;
      areas[1] = y;
      areas[2] = w;
      areas[3] = h;
      if (style.hasFlag(STYLE_OFFSET_1_FLAGA, STYLE_FLAGA_4_BORDER)) {
         int ml = getStyleBorder(style, C.POS_2_LEFT);
         int mt = getStyleBorder(style, C.POS_0_TOP);
         x += ml;
         y += mt;
         w = w - ml - getStyleBorder(style, C.POS_3_RIGHT);
         h = h - mt - getStyleBorder(style, C.POS_1_BOT);
      }
      areas[12] = x;
      areas[13] = y;
      areas[14] = w;
      areas[15] = h;
      if (style.hasFlag(STYLE_OFFSET_1_FLAGA, STYLE_FLAGA_3_PADDING)) {
         int ml = getStylePadding(style, C.POS_2_LEFT);
         int mt = getStylePadding(style, C.POS_0_TOP);
         x += ml;
         y += mt;
         w = w - ml - getStylePadding(style, C.POS_3_RIGHT);
         h = h - mt - getStylePadding(style, C.POS_1_BOT);
      }
      areas[8] = x;
      areas[9] = y;
      areas[10] = w;
      areas[11] = h;
      return areas;
   }

   /**
    * Returns an Array of the style's 4 BG layers
    * @param style
    * @return 4 sized array or null if none
    */
   public ByteObject[] getStyleBgs(ByteObject style) {
      int flag = style.get1(STYLE_OFFSET_2_FLAGB);
      if ((flag & 0x00001111b) == 0) {
         return null;
      } else {
         int bgIndex = 0;
         ByteObject[] bgs = new ByteObject[4];
         bgIndex = styleLayerPut(style, bgs, 0, flag, STYLE_FLAGB_1_BG, bgIndex);
         bgIndex = styleLayerPut(style, bgs, 1, flag, STYLE_FLAGB_2_BG, bgIndex);
         bgIndex = styleLayerPut(style, bgs, 2, flag, STYLE_FLAGB_3_BG, bgIndex);
         bgIndex = styleLayerPut(style, bgs, 3, flag, STYLE_FLAGB_4_BG, bgIndex);
         return bgs;
      }
   }

   public int getStyleBorder(ByteObject p, int pos) {
      ByteObject tblr = getStyleElement(p, STYLE_FLAGA_4_BORDER);
      if (tblr != null) {
         return dc.getTblrFactory().getTBLRValue(tblr, pos);
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
      ByteObject tblr = getStyleElement(style, STYLE_FLAGA_4_BORDER);
      return getStyleValue(tblr, pos, c);
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
      if (getStyleDrw(style, ITechStyle.STYLE_OFFSET_1_FLAGA, ITechStyle.STYLE_FLAGA_1_CONTENT) == figure) {
         val = 0;
      } else {
         if (getStyleDrw(style, ITechStyle.STYLE_OFFSET_2_FLAGB, ITechStyle.STYLE_FLAGB_1_BG) == figure) {
            val = 1;
         } else if (getStyleDrw(style, ITechStyle.STYLE_OFFSET_2_FLAGB, ITechStyle.STYLE_FLAGB_2_BG) == figure) {
            val = 2;
         } else if (getStyleDrw(style, ITechStyle.STYLE_OFFSET_2_FLAGB, ITechStyle.STYLE_FLAGB_3_BG) == figure) {
            val = 3;
         } else if (getStyleDrw(style, ITechStyle.STYLE_OFFSET_2_FLAGB, ITechStyle.STYLE_FLAGB_4_BG) == figure) {
            val = 4;
         } else if (getStyleDrw(style, ITechStyle.STYLE_OFFSET_2_FLAGB, ITechStyle.STYLE_FLAGB_5_FG) == figure) {
            val = 5;
         } else if (getStyleDrw(style, ITechStyle.STYLE_OFFSET_2_FLAGB, ITechStyle.STYLE_FLAGB_6_FG) == figure) {
            val = 6;
         } else if (getStyleDrw(style, ITechStyle.STYLE_OFFSET_2_FLAGB, ITechStyle.STYLE_FLAGB_7_FG) == figure) {
            val = 7;
         } else if (getStyleDrw(style, ITechStyle.STYLE_OFFSET_2_FLAGB, ITechStyle.STYLE_FLAGB_8_FG) == figure) {
            val = 8;
         }
      }
      return val;
   }

   /**
    * Gets the style component for that style, flag offset and flag.
    * <br>
    * <br>
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
    * Get the member position at flag for elements at {@link ITechStyle#STYLE_OFFSET_1_FLAGA}
    * <br>
    * <br>
    * @param style
    * @param flag
    * @return
    */
   public ByteObject getStyleElement(ByteObject style, int flag) {
      if (style.hasFlag(STYLE_OFFSET_1_FLAGA, flag)) {
         int sx = style.getFlagCount(STYLE_OFFSET_1_FLAGA, flag);
         if (sx != -1) {
            try {
               return style.getSubAtIndex(sx);
            } catch (ArrayIndexOutOfBoundsException e) {
               //#debug
               String msg = "Index sx " + sx + " invalid for STYLE_OFFSET_1_FLAGV " + flag;
               //we want to debug byteObject raw only
               Dctx dc = new Dctx(boc.getUCtx());
               //override any configured flags on boc
               dc.setFlagData(boc,IFlagsToStringBO.TOSTRING_FLAG_3_IGNORE_CONTENT,true);
               dc.append(msg);
               dc.nl();
               style.toString(dc);
               //#debug
               toDLog().pNull(dc.toString(), null, StyleOperator.class, "getStyleElement", LVL_05_FINE, true);
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
      if (pointer == STYLE_OFFSET_1_FLAGA) {
         return style.getFlagCount(pointer, flag);
      } else if (pointer == STYLE_OFFSET_2_FLAGB) {
         int flags = style.get1(STYLE_OFFSET_1_FLAGA);
         val += BitUtils.countBits(flags);
      } else if (pointer == STYLE_OFFSET_3_FLAGC) {
         int flags = style.get1(STYLE_OFFSET_1_FLAGA);
         val += BitUtils.countBits(flags);
         flags = style.get1(STYLE_OFFSET_2_FLAGB);
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
         return dc.getFxStringOperator().getStringFont(txt);
      }
      return dc.getCoreDrawCtx().getFontFactory().getDefaultFont();
   }

   public int getStyleFontColor(ByteObject style) {
      ByteObject txt = getContentStyle(style);
      if (txt != null) {
         return dc.getFxStringOperator().getStringColor(txt);
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
    * 
    * @param style
    * @param flag {@link ByteObject#STYLE_FLAGB_1_BG} - {@link ByteObject#STYLE_FLAGB_8_FG} 
    * @param anc value depending on flag
    * 1 3 4 7
    * @return
    */
   public int getStyleLayerAnchor(ByteObject style, int flag, int anc) {
      int val = style.get1(STYLE_OFFSET_5_BG_POINTS1);
      if (flag > 8) {
         val = style.get1(STYLE_OFFSET_6_FG_POINTS1);
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
      ByteObject tblr = getStyleElement(p, STYLE_FLAGA_5_MARGIN);
      if (tblr != null) {
         return dc.getTblrFactory().getTBLRValue(tblr, pos);
      }
      return 0;
   }

   public int getStyleMargin(ByteObject style, int pos, ILayoutable c) {
      ByteObject tblr = getStyleElement(style, STYLE_FLAGA_5_MARGIN);
      return getStyleValue(tblr, pos, c);
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

   public int getStylePaddingBot(ByteObject style, ILayoutable c) {
      return getStylePadding(style, C.POS_1_BOT, c);
   }

   public int getStyleBorderTop(ByteObject style, ILayoutable c) {
      return getStyleBorder(style, C.POS_0_TOP, c);
   }

   public int getStyleBorderLeft(ByteObject style, ILayoutable c) {
      return getStyleBorder(style, C.POS_2_LEFT, c);
   }

   public int getStyleBorderRite(ByteObject style, ILayoutable c) {
      return getStyleBorder(style, C.POS_3_RIGHT, c);
   }

   public int getStyleBorderBot(ByteObject style, ILayoutable c) {
      return getStyleBorder(style, C.POS_1_BOT, c);
   }

   public int getStyleMarginBot(ByteObject style, ILayoutable c) {
      return getStyleMargin(style, C.POS_1_BOT, c);
   }

   public int getStyleMarginTop(ByteObject style, ILayoutable c) {
      return getStyleMargin(style, C.POS_0_TOP, c);
   }

   public int getStyleMarginLeft(ByteObject style, ILayoutable c) {
      return getStyleMargin(style, C.POS_2_LEFT, c);
   }

   public int getStyleMarginRite(ByteObject style, ILayoutable c) {
      return getStyleMargin(style, C.POS_3_RIGHT, c);
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
      ByteObject tblr = getStyleElement(style, STYLE_FLAGA_3_PADDING);
      return getStyleValue(tblr, pos);
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
      ByteObject tblr = getStyleElement(style, STYLE_FLAGA_3_PADDING);
      return getStyleValue(tblr, pos, c);
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
         if (type == TYPE_060_TBLR) {
            int val = dc.getTblrFactory().getTBLRValue(tblr, pos);
            return val;
         }
      }
      return 0;
   }

   /**
    * Deals with the {@link TblrC} definition.
    * <br>
    * 
    * @param tblr an Object of type TYPE_060_TBLR
    * @param pos the position Top,Bottom, Left or Right to read in the TBLR def.
    * @return
    */
   private int getStyleValue(ByteObject tblr, int pos, ILayoutable c) {
      if (tblr != null) {
         TblrFactory tblrFactory = dc.getTblrFactory();
         //#debug
         tblr.checkType(TYPE_060_TBLR);
         //get TBLR type
         int type = tblr.get1(TBLR_OFFSET_02_TYPE1);
         if (type == ITechTblr.TYPE_0_ONE) {
            int val = tblrFactory.getTBLRValue(tblr, pos);
            //TBLR is defined as a value.
            return getPixelSizeTBLR(val, pos, c);
         } else if (type == TYPE_1_SIZER) {
            ByteObject sizer = tblrFactory.getTBLRSizer(tblr, pos);
            if (sizer != null) {
               //we must feed the type. Top Bottom will depends on width of drawable
               //right left will depend on height
               return getPixelSizeTBLR(sizer, pos, c);
            }
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
         final int type = bg.getValue(FIG__OFFSET_01_TYPE1, 1);
         if (type == FIG_TYPE_01_RECTANGLE) {
            if (bg.hasFlag(FIG__OFFSET_03_FLAGP, FIG_FLAGP_3OPAQUE)) {
               return true;
            }
         }
      }
      return false;
   }

   /**
    * Helper method of mergeStyle procedure
    * @param style
    * @param root
    * @param merge
    * @param pointer
    * @param flag
    * @param ar
    * @param count
    * @return
    */
   private int mergeSet(ByteObject style, ByteObject root, ByteObject merge, int pointer, int flag, ByteObject[] ar, int count) {
      ByteObject rootElement = getStyleDrw(root, pointer, flag);
      ByteObject mergeElement = getStyleDrw(merge, pointer, flag);
      //nothing to merge
      BOModulesManager boModuleManager = dc.getBOC().getBOModuleManager();
      ByteObject me = boModuleManager.mergeByteObject(rootElement, mergeElement);
      if (me != null) {
         ar[count] = me;
         style.setFlag(pointer, flag, true);
         return count + 1;
      }
      return count;
   }

   /**
    * Return a new style
    * <p> Merges Layer Anchors 
    * </p>
    * @param root bottom style whose members will be erased/merged
    * @param merge top style that is merging over the bottom style
    * @return
    */
   public ByteObject mergeStyle(ByteObject root, ByteObject merge) {
      //force the pooling
      ByteObject styleResult = dc.getBOC().getByteObjectFactory().createByteObject(TYPE_071_STYLE, STYLE_BASIC_SIZE);

      //#debug
      root.checkType(TYPE_071_STYLE);
      //#debug
      if (merge != null) {
         merge.checkType(TYPE_071_STYLE);
      }

      //build the merged array of sub byteobjects
      ByteObject[] ar = new ByteObject[24];
      //this enables the debugger to print the object while debugging
      styleResult.setByteObjects(ar);
      
      int count = 0;
      count = mergeSet(styleResult, root, merge, STYLE_OFFSET_1_FLAGA, STYLE_FLAGA_1_CONTENT, ar, count);
      count = mergeSet(styleResult, root, merge, STYLE_OFFSET_1_FLAGA, STYLE_FLAGA_2_ANCHOR, ar, count);
      count = mergeSet(styleResult, root, merge, STYLE_OFFSET_1_FLAGA, STYLE_FLAGA_3_PADDING, ar, count);
      count = mergeSet(styleResult, root, merge, STYLE_OFFSET_1_FLAGA, STYLE_FLAGA_4_BORDER, ar, count);
      count = mergeSet(styleResult, root, merge, STYLE_OFFSET_1_FLAGA, STYLE_FLAGA_5_MARGIN, ar, count);
      count = mergeSet(styleResult, root, merge, STYLE_OFFSET_1_FLAGA, STYLE_FLAGA_6_ANIMATIONS, ar, count);

      count = mergeSet(styleResult, root, merge, STYLE_OFFSET_2_FLAGB, STYLE_FLAGB_1_BG, ar, count);
      count = mergeSet(styleResult, root, merge, STYLE_OFFSET_2_FLAGB, STYLE_FLAGB_2_BG, ar, count);
      count = mergeSet(styleResult, root, merge, STYLE_OFFSET_2_FLAGB, STYLE_FLAGB_3_BG, ar, count);
      count = mergeSet(styleResult, root, merge, STYLE_OFFSET_2_FLAGB, STYLE_FLAGB_4_BG, ar, count);
      count = mergeSet(styleResult, root, merge, STYLE_OFFSET_2_FLAGB, STYLE_FLAGB_5_FG, ar, count);
      count = mergeSet(styleResult, root, merge, STYLE_OFFSET_2_FLAGB, STYLE_FLAGB_6_FG, ar, count);
      count = mergeSet(styleResult, root, merge, STYLE_OFFSET_2_FLAGB, STYLE_FLAGB_7_FG, ar, count);
      count = mergeSet(styleResult, root, merge, STYLE_OFFSET_2_FLAGB, STYLE_FLAGB_8_FG, ar, count);

      count = mergeSet(styleResult, root, merge, STYLE_OFFSET_3_FLAGC, STYLE_FLAGC_1_FILTER_BG, ar, count);
      count = mergeSet(styleResult, root, merge, STYLE_OFFSET_3_FLAGC, STYLE_FLAGC_2_FILTER_CONTENT, ar, count);
      count = mergeSet(styleResult, root, merge, STYLE_OFFSET_3_FLAGC, STYLE_FLAGC_3_FILTER_FG, ar, count);
      count = mergeSet(styleResult, root, merge, STYLE_OFFSET_3_FLAGC, STYLE_FLAGC_4_FILTER_BG_CONTENT, ar, count);
      count = mergeSet(styleResult, root, merge, STYLE_OFFSET_3_FLAGC, STYLE_FLAGC_5_FILTER_ALL, ar, count);
      count = mergeSet(styleResult, root, merge, STYLE_OFFSET_3_FLAGC, STYLE_FLAGC_6_ANIM_ENTRY, ar, count);
      count = mergeSet(styleResult, root, merge, STYLE_OFFSET_3_FLAGC, STYLE_FLAGC_7_ANIM_MAIN, ar, count);
      count = mergeSet(styleResult, root, merge, STYLE_OFFSET_3_FLAGC, STYLE_FLAGC_8_ANIM_EXIT, ar, count);

      //TODO clean this
      int anc1 = root.get1(STYLE_OFFSET_5_BG_POINTS1);
      //merges layer anchoring
      int a1 = 0;
      if (merge.hasFlag(STYLE_OFFSET_2_FLAGB, STYLE_FLAGB_1_BG)) {
         a1 = getStyleLayerAnchor(merge, STYLE_FLAGB_1_BG, 1);
         if (a1 != 0) {
            anc1 = (anc1 & 0x03) + a1 << 0;
         }
      }
      if (merge.hasFlag(STYLE_OFFSET_2_FLAGB, STYLE_FLAGB_2_BG)) {
         a1 = getStyleLayerAnchor(merge, STYLE_FLAGB_2_BG, 3);
         if (a1 != 0) {
            anc1 = (anc1 & 0x0C) + a1 << 2;
         }
      }
      if (merge.hasFlag(STYLE_OFFSET_2_FLAGB, STYLE_FLAGB_3_BG)) {
         a1 = getStyleLayerAnchor(merge, STYLE_FLAGB_3_BG, 5);
         if (a1 != 0) {
            anc1 = (anc1 & 0x30) + a1 << 4;
         }
      }
      if (merge.hasFlag(STYLE_OFFSET_2_FLAGB, STYLE_FLAGB_4_BG)) {
         a1 = getStyleLayerAnchor(merge, STYLE_FLAGB_4_BG, 7);
         if (a1 != 0) {
            anc1 = (anc1 & 0xC0) + a1 << 6;
         }
      }
      styleResult.setValue(STYLE_OFFSET_5_BG_POINTS1, anc1, 1);
      a1 = 0;
      
      if (merge.hasFlag(STYLE_OFFSET_2_FLAGB, STYLE_FLAGB_5_FG)) {
         a1 = getStyleLayerAnchor(merge, STYLE_FLAGB_5_FG, 1);
         if (a1 != 0) {
            anc1 = (anc1 & 0x03) + a1 << 0;
         }
      }
      styleResult.setValue(STYLE_OFFSET_6_FG_POINTS1, anc1, 1);

      ByteObject[] trimmedAr = dc.getBOC().getBOU().getTrim(ar);
      styleResult.setByteObjects(trimmedAr);
      //first do text for both bot and up
      return styleResult;

   }

   public void setGAnchors(ByteObject style, int flag, int anc) {
      int off = STYLE_OFFSET_5_BG_POINTS1;
      if (flag > 8) {
         off = STYLE_OFFSET_6_FG_POINTS1;
         flag = flag >> 4;
      }
      //1 = 1, 2 = 3, 4 = 5, 8 = 7
      int n = 1;
      if (flag == 2)
         n = 3;
      if (flag == 4)
         n = 5;
      if (flag == 8)
         n = 7;
      int val = style.get1(off);
      val = BitUtils.setBit(val, n, BitUtils.getBit(1, anc));
      val = BitUtils.setBit(val, n + 1, BitUtils.getBit(2, anc));
      style.setValue(off, val, 1);
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
      root.checkType(TYPE_071_STYLE);
      styleO.checkType(TYPE_071_STYLE);

      //create a new possible
      ByteObject content = styleMergePartOver(root, STYLE_OFFSET_1_FLAGA, STYLE_FLAGA_1_CONTENT, styleO);
      ByteObject anchor = styleMergePartOver(root, STYLE_OFFSET_1_FLAGA, STYLE_FLAGA_2_ANCHOR, styleO);
      ByteObject pad = styleMergePartOver(root, STYLE_OFFSET_1_FLAGA, STYLE_FLAGA_3_PADDING, styleO);
      ByteObject border = styleMergePartOver(root, STYLE_OFFSET_1_FLAGA, STYLE_FLAGA_4_BORDER, styleO);
      ByteObject margin = styleMergePartOver(root, STYLE_OFFSET_1_FLAGA, STYLE_FLAGA_5_MARGIN, styleO);

      ByteObject[] bg = null;
      if (root.hasFlag(STYLE_OFFSET_4_FLAG_PERF, STYLE_FLAG_PERF_1_BG)) {
         bg = new ByteObject[4];
         bg[0] = styleMergePartOver(root, STYLE_OFFSET_2_FLAGB, STYLE_FLAGB_1_BG, styleO);
         bg[1] = styleMergePartOver(root, STYLE_OFFSET_2_FLAGB, STYLE_FLAGB_2_BG, styleO);
         bg[2] = styleMergePartOver(root, STYLE_OFFSET_2_FLAGB, STYLE_FLAGB_3_BG, styleO);
         bg[3] = styleMergePartOver(root, STYLE_OFFSET_2_FLAGB, STYLE_FLAGB_4_BG, styleO);
      }
      ByteObject[] fg = null;
      if (root.hasFlag(STYLE_OFFSET_4_FLAG_PERF, STYLE_FLAG_PERF_2_FG)) {
         fg = new ByteObject[4];
         fg[0] = styleMergePartOver(root, STYLE_OFFSET_2_FLAGB, STYLE_FLAGB_5_FG, styleO);
         fg[1] = styleMergePartOver(root, STYLE_OFFSET_2_FLAGB, STYLE_FLAGB_6_FG, styleO);
         fg[2] = styleMergePartOver(root, STYLE_OFFSET_2_FLAGB, STYLE_FLAGB_7_FG, styleO);
         fg[3] = styleMergePartOver(root, STYLE_OFFSET_2_FLAGB, STYLE_FLAGB_8_FG, styleO);
      }
      ByteObject[] filters = null;
      if (root.hasFlag(STYLE_OFFSET_4_FLAG_PERF, STYLE_FLAG_PERF_3_FILTERS)) {
         filters = new ByteObject[5];
         filters[0] = styleMergePartOver(root, STYLE_OFFSET_3_FLAGC, STYLE_FLAGC_1_FILTER_BG, styleO);
         filters[1] = styleMergePartOver(root, STYLE_OFFSET_3_FLAGC, STYLE_FLAGC_2_FILTER_CONTENT, styleO);
         filters[2] = styleMergePartOver(root, STYLE_OFFSET_3_FLAGC, STYLE_FLAGC_3_FILTER_FG, styleO);
         filters[3] = styleMergePartOver(root, STYLE_OFFSET_3_FLAGC, STYLE_FLAGC_4_FILTER_BG_CONTENT, styleO);
         filters[4] = styleMergePartOver(root, STYLE_OFFSET_3_FLAGC, STYLE_FLAGC_5_FILTER_ALL, styleO);
      }
      ByteObject[] anims = null;
      if (root.hasFlag(STYLE_OFFSET_4_FLAG_PERF, STYLE_FLAG_PERF_4_ANIMS)) {
         anims = new ByteObject[3];
         anims[0] = styleMergePartOver(root, STYLE_OFFSET_3_FLAGC, STYLE_FLAGC_6_ANIM_ENTRY, styleO);
         anims[1] = styleMergePartOver(root, STYLE_OFFSET_3_FLAGC, STYLE_FLAGC_7_ANIM_MAIN, styleO);
         anims[2] = styleMergePartOver(root, STYLE_OFFSET_3_FLAGC, STYLE_FLAGC_8_ANIM_EXIT, styleO);
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

   public void toStringStyle(ByteObject bo, Dctx sb) {
      sb.append("#Style " + bo.getMyHashCode());
      sb.append("\t");
      sb.append("ConsumeTBLR[");
      sb.append(getStyleTopHConsumed(bo));
      sb.append('-');
      sb.append(getStyleBotHConsumed(bo));
      sb.append(' ');
      sb.append(getStyleLeftWConsumed(bo));
      sb.append('-');
      sb.append(getStyleRightWConsumed(bo));
      sb.append(']');
      if (sb.hasFlagData(dc, IFlagsToStringDrw.D_FLAG_01_STYLE)) {
         if (bo.get1(STYLE_OFFSET_1_FLAGA) != 0) {
            sb.nl();
            sb.append("FlagV:");
            debugFigFlag(sb, bo, STYLE_OFFSET_1_FLAGA, STYLE_FLAGA_1_CONTENT, " Content");
            debugFigFlag(sb, bo, STYLE_OFFSET_1_FLAGA, STYLE_FLAGA_2_ANCHOR, " Anchor");
            debugFigFlag(sb, bo, STYLE_OFFSET_1_FLAGA, STYLE_FLAGA_3_PADDING, " Padding");
            debugFigFlag(sb, bo, STYLE_OFFSET_1_FLAGA, STYLE_FLAGA_4_BORDER, " Border");
            debugFigFlag(sb, bo, STYLE_OFFSET_1_FLAGA, STYLE_FLAGA_5_MARGIN, " Margin");
         }
         if (bo.get1(STYLE_OFFSET_2_FLAGB) != 0) {
            sb.nl();
            sb.append("FlagG:");
            debugStyleGLayer(sb, " Bg1", bo, STYLE_FLAGB_1_BG, 1);
            debugStyleGLayer(sb, " Bg2", bo, STYLE_FLAGB_2_BG, 3);
            debugStyleGLayer(sb, " Bg3", bo, STYLE_FLAGB_3_BG, 5);
            debugStyleGLayer(sb, " Bg4", bo, STYLE_FLAGB_4_BG, 7);
            debugStyleGLayer(sb, " Fg1", bo, STYLE_FLAGB_5_FG, 1);
            debugStyleGLayer(sb, " Fg2", bo, STYLE_FLAGB_6_FG, 3);
         }
         if (bo.get1(STYLE_OFFSET_3_FLAGC) != 0) {
            sb.nl();
            sb.append("FlagF:");
            debugFigFlag(sb, bo, STYLE_OFFSET_3_FLAGC, STYLE_FLAGC_1_FILTER_BG, " FilterBg");
            debugFigFlag(sb, bo, STYLE_OFFSET_3_FLAGC, STYLE_FLAGC_2_FILTER_CONTENT, " FilterContent");
            debugFigFlag(sb, bo, STYLE_OFFSET_3_FLAGC, STYLE_FLAGC_3_FILTER_FG, " FilterFg");
            debugFigFlag(sb, bo, STYLE_OFFSET_3_FLAGC, STYLE_FLAGC_4_FILTER_BG_CONTENT, " FilterBgContent");
            debugFigFlag(sb, bo, STYLE_OFFSET_3_FLAGC, STYLE_FLAGC_5_FILTER_ALL, " FilterAll");
            debugFigFlag(sb, bo, STYLE_OFFSET_3_FLAGC, STYLE_FLAGC_6_ANIM_ENTRY, " AnimEntry");
            debugFigFlag(sb, bo, STYLE_OFFSET_3_FLAGC, STYLE_FLAGC_7_ANIM_MAIN, " AnimMain");
            debugFigFlag(sb, bo, STYLE_OFFSET_3_FLAGC, STYLE_FLAGC_8_ANIM_EXIT, " AnimExit");
         }
      }
   }

   private int setWhenNotNullStyle(ByteObject style, ByteObject field, int count) {
      if (field != null) {
         style.setSub(field, count);
         return count + 1;
      }
      return count;
   }

   /**
    * Make sure the ByteObject field is already in the repository of DrwParams
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
}
