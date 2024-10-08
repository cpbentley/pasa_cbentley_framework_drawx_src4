/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesBOC;
import pasa.cbentley.core.src4.utils.RgbUtils;
import pasa.cbentley.framework.coredraw.src4.interfaces.IFontFactory;
import pasa.cbentley.framework.coredraw.src4.interfaces.IMFont;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrawX;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;
import pasa.cbentley.framework.drawx.src4.factories.AbstractDrwOperator;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOBox;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigure;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOFigString;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOStrAux;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOStrAuxFx;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOStrAuxFxApplicator;
import pasa.cbentley.framework.drawx.src4.string.interfaces.ITechStringer;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;

/**
 * 
 * @author Charles Bentley
 *
 */
public class StringAuxOperator extends AbstractDrwOperator implements ITechFigure, IBOTypesDrawX, IBOStrAuxFx, IBOStrAuxFxApplicator {

   public StringAuxOperator(DrwCtx drc) {
      super(drc);
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
         g.drawChar(c, x, y, IBOBox.ANCHOR);
         xy[0] += fw;
      } else {
         //mask
         if (charFx.hasFlag(FX_OFFSET_03_FLAGY, FX_FLAGY_3_MASK)) {
            ByteObject mask = charFx.getSubFirst(TYPE_DRWX_06_MASK);
            drc.getMaskOperator().drawMask(g, x, y, mask, String.valueOf(c), g.getFont());
         }
      }
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
      gi.drawChar(c, ix + 1 + hspacing, iy, IBOBox.ANCHOR);

      if (trans) {
         int[] rgb = i.getRgbData();
         bgColor = (gi.getDisplayColor(bgColor) & 0xFFFFFF);
         RgbUtils.setAlphaToColorRGB(rgb, bgColor, 0);
         g.drawRGB(rgb, 0, cw, x, y, cw, ch, true);
      } else {
         i.draw(g, x, y);
      }
      return cw;
   }

   public void drawString(ByteObject style, GraphicsX g, String str, int offset, int len, int x, int y, int cw, int ch, ByteObject fx) {

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
    * Returns a new string whose width fits the size and the style
    */
   public String fitString(String s, int width, IMFont dataFont) {
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

   public IMFont getFont(int face, int style, int size) {
      IFontFactory fontFactory = drc.getFontFactory();
      IMFont f = fontFactory.getFont(face, style, size);
      return f;
   }

   public int getLineExtraBetween(ByteObject fxLine) {
      if (fxLine != null) {

      }
      return 0;
   }

   public int getLineExtraH(ByteObject fxLine) {
      if (fxLine != null) {

      }
      return 0;
   }

   public int getLineExtraW(ByteObject fxLine) {
      if (fxLine != null) {

      }
      return 0;
   }

   public int getNumStringFx(ByteObject textFigure) {
      int subtype = IBOTypesDrawX.TYPE_DRWX_07_STRING_AUX_4_FX;
      int subTypeOffset = IBOStrAux.STR_AUX_OFFSET_1_EXT_TYPE1;
      int num = textFigure.getSubSubNum(IBOTypesDrawX.TYPE_DRWX_07_STRING_AUX, subTypeOffset, 1, subtype);
      return num;
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
      return strFig.get4(IBOFigure.FIG__OFFSET_06_COLOR4);
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
      int face = strFig.getValue(IBOFigString.FIG_STRING_OFFSET_03_FACE1, 1);
      int style = strFig.getValue(IBOFigString.FIG_STRING_OFFSET_04_STYLE1, 1);

      int size = strFig.getValue(IBOFigString.FIG_STRING_OFFSET_05_SIZE1, 1);
      IMFont f = fontFactory.getFont(face, style, size);
      return f;
   }

   public int getStringLineHeight(ByteObject txt, String s) {
      //if horizontal . easiest. just send 
      return 0;
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

   public ByteObject getSub(ByteObject bo, int auxType) {
      int typeMain = IBOTypesDrawX.TYPE_DRWX_07_STRING_AUX;
      int index = IBOStrAux.STR_AUX_OFFSET_1_EXT_TYPE1;
      int size = 1;
      ByteObject aux = bo.getSubSubFirst(typeMain, auxType, index, size);
      return aux;
   }

   public ByteObject getSubSpecial(ByteObject bo) {
      return getSub(bo, IBOTypesDrawX.TYPE_DRWX_07_STRING_AUX_2_SPECIALS_C);
   }

   public ByteObject getSubFormat(ByteObject bo) {
      return getSub(bo, IBOTypesDrawX.TYPE_DRWX_07_STRING_AUX_1_FORMAT);
   }

   public ByteObject getSubCharFx(ByteObject fx) {
      return getSubFxEffect(fx, ITechStringer.FX_SCOPE_1_CHAR, FX_FLAG_8_CHAR);
   }

   /**
    * From main, get block, line or char effect
    * <br>
    * <br>
    * @param txt
    * @param type
    * @param flag
    * @return null if no such text effects
    */
   public ByteObject getSubFxEffect(ByteObject txt, int type, int flag) {
      if (txt.hasFlag(FX_OFFSET_01_FLAG, flag)) {
         ByteObject[] param = txt.getSubs();
         for (int i = 0; i < param.length; i++) {
            ByteObject p = param[i];
            if (p != null) {
               if (p.get1(FX_OFFSET_05_SCOPE_FX1) == type)
                  return p;
            }
         }
      }
      return null;
   }

   /**
    * 
    * @param fx
    * @return
    */
   public ByteObject getSubLineFx(ByteObject fx) {
      return getSubFxEffect(fx, ITechStringer.FX_SCOPE_4_LINE, FX_FLAG_7_LINE);
   }

   /**
    * Gets the effect Char Lvl, Line Level
    * <br>
    * <li> {@link ByteObject#TXT_LVL_CHAR}
    * <li> {@link ByteObject#TXT_LVL_LINE}
    * 
    * @param style
    * @param flagtype
    * @param flag
    * @return
    */
   public ByteObject getTxtEffectDrw(ByteObject fx, int flag, int scope) {
      return null;
   }

   private ByteObject mergeFormat(ByteObject root, ByteObject merge) {
      // TODO Auto-generated method stub
      return null;
   }

   private ByteObject mergeFxStruct(ByteObject root, ByteObject merge) {
      // TODO Auto-generated method stub
      return null;
   }

   private ByteObject mergeSpecials(ByteObject root, ByteObject merge) {
      // TODO Auto-generated method stub
      return null;
   }

   public ByteObject mergeStrAux(ByteObject root, ByteObject merge) {
      final int subTypeRoot = root.get1(IBOStrAux.STR_AUX_OFFSET_1_EXT_TYPE1);
      final int subTypeMerge = root.get1(IBOStrAux.STR_AUX_OFFSET_1_EXT_TYPE1);
      if (subTypeMerge != subTypeRoot) {
         throw new IllegalArgumentException("aux sub types not equal merge:" + subTypeMerge + " root:" + subTypeRoot);
      }
      switch (subTypeRoot) {
         case IBOTypesDrawX.TYPE_DRWX_07_STRING_AUX_4_FX:
            return mergeTxtEffects(root, merge);
         case IBOTypesDrawX.TYPE_DRWX_07_STRING_AUX_1_FORMAT:
            return mergeFormat(root, merge);
         case IBOTypesDrawX.TYPE_DRWX_07_STRING_AUX_2_SPECIALS_C:
            return mergeSpecials(root, merge);
         case IBOTypesDrawX.TYPE_DRWX_07_STRING_AUX_3_APPLICATOR:
            return mergeStringApplicator(root, merge);
         case IBOTypesDrawX.TYPE_DRWX_07_STRING_AUX_5_FX_STRUCT:
            return mergeFxStruct(root, merge);
         default:
            //subtype is not known by this module
            return null;
      }
   }

   private ByteObject mergeStringApplicator(ByteObject root, ByteObject merge) {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * Merge definitions of text effects.
    * <br>
    * <br>
    * The scope of the merged object is the root scope.
    * <br>
    * <br>
    * @param root
    * @param merge Fx on top, merged into root
    * @return a new {@link ByteObject}, root and merge are not modified in any ways.
    */
   public ByteObject mergeTxtEffects(ByteObject root, ByteObject merge) {
      //merge the base
      ByteObject ntx = root.cloneCopyHeadRefParams();

      boolean isIncomplete = false;
      if (merge.hasFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_1_UNDEFINED_FONT_FACE)) {
         boolean isRootFace = root.hasFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_1_UNDEFINED_FONT_FACE);
         ntx.setFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_1_UNDEFINED_FONT_FACE, isRootFace);
         isIncomplete |= isRootFace;
      } else {
         ntx.set1(FX_OFFSET_06_FACE1, merge.get1(FX_OFFSET_06_FACE1));
      }
      if (merge.hasFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_2_UNDEFINED_FONT_STYLE)) {
         boolean isRootStyle = root.hasFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_2_UNDEFINED_FONT_STYLE);
         ntx.setFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_2_UNDEFINED_FONT_STYLE, isRootStyle);
         isIncomplete |= isRootStyle;
      } else {
         ntx.set1(FX_OFFSET_07_STYLE1, merge.get1(FX_OFFSET_07_STYLE1));
      }

      if (merge.hasFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_3_UNDEFINED_FONT_SIZE)) {
         boolean isRootSize = root.hasFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_3_UNDEFINED_FONT_SIZE);
         ntx.setFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_3_UNDEFINED_FONT_SIZE, isRootSize);
         isIncomplete |= isRootSize;
      } else {
         ntx.set1(FX_OFFSET_08_SIZE1, merge.get1(FX_OFFSET_08_SIZE1));
      }

      if (merge.hasFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_4_UNDEFINED_COLOR)) {
         boolean isRootColor = root.hasFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_4_UNDEFINED_COLOR);
         ntx.setFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_4_UNDEFINED_COLOR, isRootColor);
         isIncomplete |= isRootColor;
      } else {
         ntx.set4(FX_OFFSET_09_COLOR4, merge.get4(FX_OFFSET_09_COLOR4));
      }

      if (merge.hasFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_5_UNDEFINED_SCOPE)) {
         boolean isRootScope = root.hasFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_5_UNDEFINED_SCOPE);
         ntx.setFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_5_UNDEFINED_SCOPE, isRootScope);
         isIncomplete |= isRootScope;
      } else {
         ntx.set1(FX_OFFSET_05_SCOPE_FX1, merge.get1(FX_OFFSET_05_SCOPE_FX1));
      }
      if (merge.hasFlag(FX_OFFSET_03_FLAGY, FX_FLAGY_2_FIGURE_BG)) {
         ByteObject fig = merge.getSubFirst(TYPE_DRWX_00_FIGURE);
         drc.getFxStringFactory().setFxFigure(ntx, fig);
      }

      if (merge.hasFlag(FX_OFFSET_03_FLAGY, FX_FLAGY_3_MASK)) {
         ByteObject mask = merge.getSubFirst(TYPE_DRWX_06_MASK);
         drc.getFxStringFactory().setMaskToFx(ntx, mask);
      }
      if (merge.hasFlag(FX_OFFSET_04_FLAGZ, FX_FLAGZ_8_FUNCTION)) {
         ByteObject function = merge.getSubFirst(IBOTypesBOC.TYPE_021_FUNCTION);
         drc.getFxStringFactory().setFunctionToFx(ntx, function);
      }
      //merged cannot be incomplete
      ntx.setFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_8_INCOMPLETE, isIncomplete);
      return ntx;
   }
}
