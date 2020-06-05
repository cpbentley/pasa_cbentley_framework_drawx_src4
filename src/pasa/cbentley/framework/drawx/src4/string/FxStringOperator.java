/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.interfaces.C;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.framework.coredraw.src4.interfaces.IFontFactory;
import pasa.cbentley.framework.coredraw.src4.interfaces.IMFont;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;
import pasa.cbentley.framework.drawx.src4.factories.AbstractDrwOperator;
import pasa.cbentley.framework.drawx.src4.tech.ITechBox;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigureString;
import pasa.cbentley.framework.drawx.src4.utils.DrawUtilz;

public class FxStringOperator extends AbstractDrwOperator implements ITechFigure, ITechFigureString, IBOTypesDrw, ITechStrFx {

   public FxStringOperator(DrwCtx drc) {
      super(drc);
   }

   public void drawString(ByteObject style, GraphicsX g, String str, int offset, int len, int x, int y, int cw, int ch, ByteObject fx) {

   }

   /**
    * Simple draw of String. No breaks. No Anchoring. No Box
    * Coordinate x,y is the TOP LEFT.  
    * <br>
    * <br>
    * @param g
    * @param s
    * @param x
    * @param y
    * @param strFigure
    */
   public void drawString(GraphicsX g, int x, int y, String s, ByteObject strFigure) {
      drawString(g, x, y, new String[] { s }, strFigure);
   }

   /**
    * Merge definitions of text effects.
    * <br>
    * <br>
    * Must be of the same scope otherwise root is returned.
    * <br>
    * <br>
    * @param root
    * @param merge
    * @return
    */
   public ByteObject mergeTxtEffects(ByteObject root, ByteObject merge) {
      int scopeRoot = root.get1(ITechStrFx.FX_OFFSET_03_SCOPE1);
      if (scopeRoot != merge.get1(FX_OFFSET_03_SCOPE1)) {
         return root;
      }
      //merge the base
      ByteObject ntx = root.cloneCopyHeadRefParams();
      ntx.set1(FX_OFFSET_03_SCOPE1, scopeRoot);
      if (merge.hasFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_4_DEFINED_FONT)) {
         ntx.setFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_4_DEFINED_FONT, true);
         ntx.set1(FX_OFFSET_06_FACE1, merge.get1(FX_OFFSET_06_FACE1));
         ntx.set1(FX_OFFSET_07_STYLE1, merge.get1(FX_OFFSET_07_STYLE1));
         ntx.set1(FX_OFFSET_08_SIZE1, merge.get1(FX_OFFSET_08_SIZE1));

      }
      if (merge.hasFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_5_DEFINED_COLOR)) {
         ntx.set4(FX_OFFSET_09_COLOR4, merge.get4(FX_OFFSET_09_COLOR4));
      }

      if (merge.hasFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_6_DEFINED_INDEX)) {
         ntx.set1(FX_OFFSET_05_INDEX_PATTERN1, merge.get2(FX_OFFSET_05_INDEX_PATTERN1));
         ntx.set2(FX_OFFSET_04_INDEX2, merge.get2(FX_OFFSET_04_INDEX2));
      }
      if (merge.hasFlag(FX_OFFSET_10_FLAGZ, FX_FLAGZ_2_FIGURE)) {
         ntx.addByteObject(merge.getSubAtIndex(TYPE_050_FIGURE));
         ntx.setFlag(FX_OFFSET_10_FLAGZ, FX_FLAGZ_2_FIGURE, true);

      }
      if (merge.hasFlag(FX_OFFSET_10_FLAGZ, FX_FLAGZ_3_MASK)) {
         ntx.addByteObject(merge.getSubAtIndex(TYPE_058_MASK));
         ntx.setFlag(FX_OFFSET_10_FLAGZ, FX_FLAGZ_3_MASK, true);
      }
      return ntx;
   }

   /**
    * 
    * @param g
    * @param x
    * @param y
    * @param s
    * @param strFigure
    */
   public void drawString(GraphicsX g, int x, int y, String[] s, ByteObject strFigure) {
      //SystemLog
      IMFont f = getStringFont(strFigure);
      int color = getStringColor(strFigure);
      g.setFont(f);
      g.setColor(color);
      ByteObject block = null; //fx for block
      ByteObject line = null; //fx for line
      ByteObject ch = null;
      //by construction, only 1 mask is present at a 
      if (strFigure.hasFlag(FIG_STRING_OFFSET_01_FLAG, FIG_STRING_FLAG_5_EFFECT)) {

         ByteObject effect = strFigure.getSubFirst(TYPE_070_TEXT_EFFECTS);
         if (effect.hasFlag(ITechStrFx.FX_OFFSET_01_FLAG, ITechStrFx.FX_FLAG_3_VERTICAL)) {

         }
         line = drc.getFxStringFactory().getSubFxEffect(effect, ITechStrFx.FX_SCOPE_2_LINE, ITechStrFx.FX_FLAG_7_LINE);
         ch = drc.getFxStringFactory().getSubFxEffect(effect, ITechStrFx.FX_SCOPE_0_CHAR, ITechStrFx.FX_FLAG_8_CHAR);
         GraphicsX sg = g;
         int lineShiftX = 0;
         int lineShiftY = 0;
         //only one mask operation is applied.
         if (block != null) {

         }
         int ex = 0;
         int ey = 0;
         if (line != null) {
            ex = line.get1(ITechStrFx.FXLINE_OFFSET_02_CHAR_X_OFFSET1);
            ey = line.get1(ITechStrFx.FXLINE_OFFSET_03_CHAR_Y_OFFSET1);
         }
         for (int k = 0; k < s.length; k++) {
            String str = s[k];
            int len = str.length();
            int dx = x;
            int dy = y;

            int[] xy = new int[] { dx, dy };
            for (int i = 0; i < len; i++) {
               drawChar(sg, xy, str.charAt(i), ch);
               xy[0] += ex;
               xy[1] += ey;
            }
         }
      } else {
         for (int i = 0; i < s.length; i++) {
            String str = s[i];
            g.drawString(str, x, y, GraphicsX.ANCHOR);
            y += f.getHeight();
         }
      }

   }

   public void drawStringChar(GraphicsX g, int count, char c, int x, int y, ByteObject fx) {
      if (fx != null) {
         int index = fx.get2(ITechStrFx.FX_OFFSET_04_INDEX2);
         if (count == index) {

         }
      } else {
         g.drawChar(c, x, y, ITechBox.ANCHOR);
      }
   }

   /**
    * Draws several lines of text using the ByteObject text effects
    * @param txt
    * @param breaks
    * @param str
    * @return
    */
   public void drawStrings(GraphicsX g, int x, int y, ByteObject txt, int[][] breaks, int boff, int blen, String str) {

   }

   /**
    * 
    * @param text
    * @return if undefined will return 0
    */
   public int getStringColor(ByteObject strFig) {
      if (strFig == null) {
         return 0;
      }
      return strFig.get4(FIG__OFFSET_06_COLOR4);
   }

   /**
    * If null, return def IMFont
    * <br>
    * <br>
    * @param strFig 
    * @return
    */
   public IMFont getStringFont(ByteObject strFig) {
      IFontFactory fontFactory = drc.getFontFactory();
      if (strFig == null) {
         return fontFactory.getDefaultFont();
      }
      int face = strFig.getValue(FIG_STRING_OFFSET_02_FACE1, 1);
      int style = strFig.getValue(FIG_STRING_OFFSET_03_STYLE1, 1);

      int size = strFig.getValue(FIG_STRING_OFFSET_04_SIZE1, 1);
      IMFont f = fontFactory.getFont(face, style, size);
      return f;
   }

   public int getStringLineHeight(ByteObject txt, String s) {
      //if horizontal . easiest. just send 
      return 0;
   }

   /**
    * Pixel width consumed by this string rendered using the given text effects.
    * <br>
    * <br>
    * @param txt
    * @param str
    * @param offset
    * @param len
    * @return
    */
   public int getStringSubStringW(ByteObject txt, String str, int offset, int len) {
      IMFont f = getStringFont(txt);
      int extraLeft = 0;
      int extraRight = 0;
      if (txt.hasFlag(ITechStrFx.FX_OFFSET_01_FLAG, ITechStrFx.FX_FLAG_4_EXTRA_SPACE_TBLR)) {
         ByteObject tblr = txt.getSubFirst(TYPE_060_TBLR);
         extraLeft = drc.getTblrFactory().getTBLRValue(tblr, C.POS_2_LEFT);
         extraRight = drc.getTblrFactory().getTBLRValue(tblr, C.POS_3_RIGHT);
      }
      return f.substringWidth(str, offset, len) + extraLeft + extraRight;
   }

   public int getStringSubStringWidth(ByteObject style, String str, int offset, int len) {
      ByteObject text = style.getSubFirst(TYPE_070_TEXT_EFFECTS);
      IMFont f = drc.getFontFactory().getDefaultFont();
      if (text != null) {
         f = getStringFont(text);
      }
      return f.substringWidth(str, offset, len);
   }

   /**
    * Extra Number of pixels used left by the text effect décoration
    * For example a halo applied at the LINE level will consume several pixels
    * TopBottomLeftRight
    * @param txt
    * @return
    */
   public int getStringTxtEffectExtraLeft(ByteObject txt) {
      return 0;

   }

   /**
    * Similar to getTxtEffectWidth
    * How many pixels will drawing a line of text using this text effect consume?
    * @param txt
    * @param str the string used for diagonal and vertical text
    * @return
    */
   public int getStringTxtEffectHeight(ByteObject txt, String str) {
      return drc.getFontFactory().getDefaultFont().getHeight();
   }

   /**
    * Width consumed by the string using text effect definition
    * @param txt
    * @param str
    * @return
    */
   public int getStringTxtEffectWidth(ByteObject txt, String str) {
      return drc.getFontFactory().getDefaultFont().stringWidth(str);
   }

   public void drawChar(GraphicsX g, int x, int y, char c, ByteObject charFx) {
      drawChar(g, new int[] { x, y }, c, charFx);
   }

   /**
    * Draws transparently to the GraphicsX
    * @param g
    * @param xy
    * @param c
    * @param charFx
    *    //decoration not implemented yet
    *    A cache maybe implemented
    */
   public void drawChar(GraphicsX g, int[] xy, char c, ByteObject charFx) {
      int fw = g.getFont().charWidth(c);
      int x = xy[0];
      int y = xy[1];
      if (charFx == null) {
         g.drawChar(c, x, y, ITechBox.ANCHOR);
         xy[0] += fw;
      } else {
         //mask
         if (charFx.hasFlag(ITechStrFx.FX_OFFSET_10_FLAGZ, ITechStrFx.FX_FLAGZ_3_MASK)) {
            ByteObject mask = charFx.getSubFirst(TYPE_058_MASK);
            drc.getMaskOperator().drawMask(g, x, y, mask, String.valueOf(c), g.getFont());
         }
      }
   }

   public int[][] breakString(char[] text, int width, ByteObject txt) {
      return null;
   }

   public int[][] breakString(String str, int width, ByteObject txteffect) {
      return breakString(str.toCharArray(), width, txteffect);
   }

   /**
    * Returns a new string whose width fits the size and the style
    */
   public String fitString(String s, int width, ByteObject style) {
      ByteObject txt = style.getSubFirst(TYPE_070_TEXT_EFFECTS);
      IMFont dataFont = getStringFont(txt);
      int size = dataFont.stringWidth("..");
      int strWidth = dataFont.stringWidth(s);
      if (width < strWidth) {
         //trim and add ...
         int target = width - size;
         //at least
         int sindex = 0;
         for (int i = 1; i < s.length(); i++) {
            if (target < dataFont.substringWidth(s, 0, i)) {
               sindex = i - 1;
               //System.out.println(s.substring(0, sindex) );
               break;
            }
         }
         return s.substring(0, sindex) + "..";
      }
      return s;
   }

   /**
    * 
    * @param g
    * @param c
    * @param x
    * @param y
    * @param f
    * @param ix
    * @param iy
    * @param hspacing
    * @param fontColor
    * @param lineColor
    * @param bgColor
    * @param trans
    * @return
    */
   public int drawCharWithLineEffect(GraphicsX g, char c, int x, int y, IMFont f, int ix, int iy, int hspacing, int fontColor, int lineColor, int bgColor, boolean trans) {
      int charW = f.charWidth(c);
      int cw = f.charWidth(c);
      cw += (2 * (ix + hspacing));
      int fh = f.getHeight();
      int ch = f.getHeight();
      ch += (2 * iy);
      cw++;
      RgbImage i = drc.getCache().createImage(cw, ch);
      GraphicsX gi = i.getGraphicsX();
      //since bg color may be used 
      gi.setColor(bgColor);
      gi.fillRect(0, 0, cw, ch);
      gi.setColor(lineColor);
      gi.drawLine(cw / 2, 0, cw / 2, iy);
      gi.drawLine(cw / 2, iy + fh, cw / 2, ch);

      gi.drawLine(0, ch / 2, ix, ch / 2);
      gi.drawLine(ix + (2 * hspacing) + charW, ch / 2, cw, ch / 2);

      gi.setFont(f);
      gi.setColor(fontColor);
      gi.drawChar(c, ix + 1 + hspacing, iy, ITechBox.ANCHOR);

      if (trans) {
         int[] rgb = i.getRgbData();
         bgColor = (gi.getDisplayColor(bgColor) & 0xFFFFFF);
         DrawUtilz.setAlphaToColorRGB(rgb, bgColor, 0);
         g.drawRGB(rgb, 0, cw, x, y, cw, ch, true);
      } else {
         i.draw(g, x, y);
      }
      return cw;
   }

   public void toStringTxtEffect(ByteObject bo, Dctx sb) {
      sb.append("#Text Effect ");
      sb.append(" scope " + StringFx.toStringFxScope(bo.get1(ITechStrFx.FX_OFFSET_03_SCOPE1)));

      sb.append(" index " + bo.get2(ITechStrFx.FX_OFFSET_04_INDEX2));
      sb.append(" pattern " + bo.get2(ITechStrFx.FX_OFFSET_05_INDEX_PATTERN1));
      sb.append("Defined Font " + bo.hasFlag(ITechStrFx.FX_OFFSET_01_FLAG, ITechStrFx.FX_FLAGX_4_DEFINED_FONT));
      sb.append(" face " + bo.get1(ITechStrFx.FX_OFFSET_06_FACE1));
      sb.append(" style " + bo.get1(ITechStrFx.FX_OFFSET_07_STYLE1));
      sb.append(" size " + bo.get1(ITechStrFx.FX_OFFSET_08_SIZE1));

   }
}
