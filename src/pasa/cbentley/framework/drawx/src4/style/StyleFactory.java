/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.style;

import pasa.cbentley.byteobjects.src4.core.BOAbstractFactory;
import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesBOC;
import pasa.cbentley.core.src4.utils.BitUtils;
import pasa.cbentley.framework.coredraw.src4.interfaces.IMFont;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrawX;
import pasa.cbentley.framework.drawx.src4.factories.FigureFactory;
import pasa.cbentley.framework.drawx.src4.tech.ITechMergeFigure;
import pasa.cbentley.framework.drawx.src4.tech.ITechMergeFigureString;
import pasa.cbentley.layouter.src4.ctx.IBOTypesLayout;
import pasa.cbentley.layouter.src4.engine.TblrFactory;

public class StyleFactory extends BOAbstractFactory implements IBOStyle, IBOTypesDrawX {

   protected final DrwCtx gc;

   public StyleFactory(DrwCtx gc) {
      super(gc.getBOC());
      this.gc = gc;
   }

   /**
    * A Style with a single figure
    * @param fig
    * @return
    */
   public ByteObject getStyle(ByteObject fig) {
      return getStyle(new ByteObject[] { fig });
   }

   public ByteObject getStyle(ByteObject bg, ByteObject txt, ByteObject anchor) {
      return getStyle(bg, txt, anchor, null, null, null, null);
   }

   public ByteObject getStyle(ByteObject bg, ByteObject content, ByteObject anchor, ByteObject pad, ByteObject br, ByteObject margin, ByteObject fg) {
      return getStyle(new ByteObject[] { bg }, content, anchor, pad, br, margin, new ByteObject[] { fg });
   }

   /**
    * Create style mask for font string and color
    * <br>
    * Create a String figure.
    * <br>
    * Uses {@link IMergeMask}
    * <br>
    * <br>
    * Every font has a fontID to identify it. When Font cannot be found,
    * it falls back on font {@link IMFont#FACE_SYSTEM}
    * <br>
    * <br>
    * 
    * @param string
    * @param fontColor
    * @return
    */
   public ByteObject createStyleContentMask(String string, int fontColor) {

      int fontID = gc.getFontFactory().getFontFaceID(string);
      //in the case of figString, we need a 

      FigureFactory figureFactory = gc.getFigureFactory();

      ByteObject text = figureFactory.getFigStringT_FaceColor(fontID, fontColor);

      ByteObject content = text;
      return gc.getStyleFactory().getStyle(content, STYLE_OFFSET_1_FLAG_A, STYLE_FLAG_A_1_CONTENT);
   }

   /**
    * Transparent style defining only anchors
    * <br>
    * Valid alignment values are
    * <li> {@link ByteObject#ALIGN_3_LEFT} 
    * <li> {@link ByteObject#ALIGN_6_CENTER}
    * <li> {@link ByteObject#ALIGN_4_RIGHT}
    * <li> {@link ByteObject#ALIGN_1_TOP}
    * <li> {@link ByteObject#ALIGN_2_BOTTOM}
    * @param ha
    * @param va
    * @return
    */
   public ByteObject getStyleContentAnchor(int ha, int va) {
      ByteObject anchor = gc.getAnchorFactory().getAnchor(ha, va);
      ByteObject style = getStyle(null, null, anchor);
      style.setFlag(STYLE_OFFSET_5_FLAG_X, STYLE_FLAG_X_1_INCOMPLETE, true);
      return style;
   }

   /**
    * A Style with just one component identified by its flag and flag pointer.
    * <br>
    * <br>
    * Style is incomplete i.e. 
    * <br>
    * <br>
    * Use {@link MStyle#styleSet(ByteObject, ByteObject, int, int)} to add more layers
    * <br>
    * <br>
    * @param c
    * @param flag
    * @return
    */
   public ByteObject getStyle(ByteObject c, int pointer, int flag) {
      ByteObject style = createStyle();
      style.setFlag(pointer, flag, true);
      style.addByteObject(c);
      style.setFlag(STYLE_OFFSET_5_FLAG_X, STYLE_FLAG_X_1_INCOMPLETE, true);
      return style;
   }

   public ByteObject getStyle(ByteObject[] bg) {
      return getStyle(bg, null, null, null, null);
   }

   /**
    * 
    * @param mod
    * @param bg
    * @param content
    * @param anchor
    * @param pad
    * @param border
    * @return
    */
   public ByteObject getStyle(ByteObject[] bg, ByteObject content, ByteObject anchor, ByteObject pad, ByteObject border) {
      return getStyle(bg, content, anchor, pad, border, null, null, null, null);
   }

   public ByteObject getStyle(ByteObject[] bg, ByteObject content, ByteObject anchor, ByteObject pad, ByteObject border, ByteObject margin) {
      return getStyle(bg, content, anchor, pad, border, margin, null, null, null);
   }

   /**
    * All non nulls are opaque, 
    * 
    * Return style is T
    * @param bg
    * @param content
    * @param anchor
    * @param pad
    * @param border
    * @param margin
    * @return
    */
   public ByteObject getStyleT(ByteObject[] bg, ByteObject content, ByteObject anchor, ByteObject pad, ByteObject border, ByteObject margin) {
      return getStyle(bg, content, anchor, pad, border, margin, null, null, null, false);
   }

   public ByteObject getStyle(ByteObject[] bg, ByteObject content, ByteObject anchor, ByteObject pad, ByteObject border, ByteObject margin, ByteObject fg) {
      return getStyle(bg, content, anchor, pad, border, margin, new ByteObject[] { fg }, null, null);
   }

   public ByteObject getStyle(ByteObject[] bg, ByteObject content, ByteObject anchor, ByteObject pad, ByteObject border, ByteObject margin, ByteObject[] fg) {
      return getStyle(bg, content, anchor, pad, border, margin, fg, null, null);
   }

   public ByteObject getStyle(ByteObject[] bg, ByteObject content, ByteObject anchor, ByteObject pad, ByteObject border, ByteObject margin, ByteObject[] fg, ByteObject[] filters) {
      return getStyle(bg, content, anchor, pad, border, margin, fg, filters, null);
   }

   /**
    * Style is flagged as complete
    * 
    * First anim in array is used for 
    * <li> {@link IBOStyle#STYLE_FLAG_C_6_ANIM_ENTRY}
    * 
    * @param bg up to 4 figures
    * @param content figure for the content. usually an implicit text effect
    * @param anchor box model's anchoring
    * @param pad box model's padding TBLR
    * @param border box model's border TBLR 
    * @param margin box model's margin TBLR
    * @param fg up to 4 figures
    * @param filters up to 5 filters
    * @param anims array of size up to 3 animations
    * @return
    */
   public ByteObject getStyle(ByteObject[] bg, ByteObject content, ByteObject anchor, ByteObject pad, ByteObject border, ByteObject margin, ByteObject[] fg, ByteObject[] filters, ByteObject[] anims) {
      return getStyle(bg, content, anchor, pad, border, margin, fg, filters, anims, true);
   }

   public ByteObject getStyle(ByteObject[] bg, ByteObject content, ByteObject anchor, ByteObject pad, ByteObject border, ByteObject margin, ByteObject[] fg, ByteObject[] filters, ByteObject[] anims, boolean isComplete) {
      ByteObject p = createStyle();
      int flagA = 0;
      StyleOperator styleOperator = gc.getStyleOperator();
      flagA = styleOperator.setFlagNotNullStyleFieldFlag(content, flagA, STYLE_FLAG_A_1_CONTENT, TYPE_DRWX_00_FIGURE);
      flagA = styleOperator.setFlagNotNullStyleFieldFlag(anchor, flagA, STYLE_FLAG_A_2_ANCHOR, TYPE_DRWX_10_ANCHOR);
      flagA = styleOperator.setFlagNotNullStyleFieldFlag(pad, flagA, STYLE_FLAG_A_3_PADDING, IBOTypesLayout.FTYPE_2_TBLR);
      flagA = styleOperator.setFlagNotNullStyleFieldFlag(border, flagA, STYLE_FLAG_A_4_BORDER, IBOTypesLayout.FTYPE_2_TBLR);
      flagA = styleOperator.setFlagNotNullStyleFieldFlag(margin, flagA, STYLE_FLAG_A_5_MARGIN, IBOTypesLayout.FTYPE_2_TBLR);
      int flagB = 0;
      if (bg != null && bg.length != 0) {
         if (bg.length > 4) {
            throw new IllegalArgumentException("Max 4 backgrounds");
         }
         int start = STYLE_FLAG_B_1_BG;
         for (int i = 0; i < bg.length; i++) {
            flagB = styleOperator.setFlagNotNullStyleFieldFlag(bg[i], flagB, start, TYPE_DRWX_00_FIGURE);
            start = start << 1;
         }
         p.setFlag(STYLE_OFFSET_6_FLAG_PERF, STYLE_FLAG_PERF_1_BG, true);
      }
      if (fg != null && fg.length != 0) {
         if (fg.length > 4) {
            throw new IllegalArgumentException();
         }
         int start = STYLE_FLAG_B_5_FG;
         for (int i = 0; i < fg.length; i++) {
            flagB = styleOperator.setFlagNotNullStyleFieldFlag(fg[i], flagB, start, TYPE_DRWX_00_FIGURE);
            start = start << 1;
         }
         p.setFlag(STYLE_OFFSET_6_FLAG_PERF, STYLE_FLAG_PERF_2_FG, true);
      }
      int flagF = 0;
      if (filters != null && filters.length != 0) {
         if (filters.length > 5) {
            throw new IllegalArgumentException();
         }
         int start = STYLE_FLAG_F_1_FILTER_BG;
         for (int i = 0; i < filters.length; i++) {
            flagF = styleOperator.setFlagNotNullStyleFieldFlag(filters[i], flagF, start, IBOTypesBOC.TYPE_040_COLOR_FILTER);
            start = start << 1;
         }
         p.setFlag(STYLE_OFFSET_6_FLAG_PERF, STYLE_FLAG_PERF_3_FILTERS, true);
      }
      int flagC = 0;
      if (anims != null && anims.length != 0) {
         if (anims.length > 3) {
            throw new IllegalArgumentException();
         }
         int start = STYLE_FLAG_C_6_ANIM_ENTRY;
         for (int i = 0; i < anims.length; i++) {
            flagC = styleOperator.setFlagWhenNotNull(anims[i], flagC, start);
            start = start << 1;
         }
         p.setFlag(STYLE_OFFSET_6_FLAG_PERF, STYLE_FLAG_PERF_4_ANIMS, true);
      }

      int total = BitUtils.countBits(flagA) + BitUtils.countBits(flagB) + BitUtils.countBits(flagC);

      p.setByteObjects(new ByteObject[total]);

      p.set1(STYLE_OFFSET_1_FLAG_A, flagA);
      p.set1(STYLE_OFFSET_2_FLAG_B, flagB);
      p.set1(STYLE_OFFSET_3_FLAG_C, flagC);
      p.set1(STYLE_OFFSET_4_FLAG_F, flagF);

      int c = 0;
      c = styleOperator.styleCreationLastFill(p, content, c);
      c = styleOperator.styleCreationLastFill(p, anchor, c);
      c = styleOperator.styleCreationLastFill(p, pad, c);
      c = styleOperator.styleCreationLastFill(p, border, c);
      c = styleOperator.styleCreationLastFill(p, margin, c);
      c = styleOperator.styleCreationLastFill(p, bg, c);
      c = styleOperator.styleCreationLastFill(p, fg, c);
      c = styleOperator.styleCreationLastFill(p, filters, c);
      c = styleOperator.styleCreationLastFill(p, anims, c);

      p.setFlag(IBOStyle.STYLE_OFFSET_5_FLAG_X, IBOStyle.STYLE_FLAG_X_1_INCOMPLETE, !isComplete);

      return p;
   }

   public ByteObject createStyle() {
      ByteObject p = getBOFactory().createByteObject(TYPE_DRWX_08_STYLE, STYLE_BASIC_SIZE);
      return p;
   }

   public ByteObject getStyle(ByteObject[] bg, ByteObject content, ByteObject anchor, int pad, int border) {
      TblrFactory tblrFactory = gc.getTblrFactory();
      return getStyle(bg, content, anchor, tblrFactory.getTBLRCoded(pad), tblrFactory.getTBLRCoded(border));
   }

   public ByteObject getStyle(ByteObject[] bg, int pad, int border) {
      return getStyle(bg, null, null, pad, border);
   }

   /**
    * A style with just a border figure whose TBLR will be used by the Box Model
    * @param borderFig
    * @return
    */
   public ByteObject getStyleBorder(ByteObject borderFig) {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * A Drawable has a style with  colors defined. A mechanism is needed to change colors all used styles. 
    * <br>
    * <br>
    * TODO A Style that only defines a series of colors for drawing content
    * Drawable will draw with those colors. index 0 being the main color
    * TODO See Color Themes
    * <br>
    * <br>
    * @param colors
    * @return
    */
   public ByteObject getStyleContentColors(int[] colors) {
      return null;
   }

   /**
    * Single BG Figure style
    * @param fig
    * @return
    */
   public ByteObject getStyleFig(ByteObject fig) {
      return getStyle(fig, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_1_BG);
   }

   public ByteObject getStyleFigT(ByteObject fig) {
      ByteObject bo = getStyle(fig, STYLE_OFFSET_2_FLAG_B, STYLE_FLAG_B_1_BG);
      gc.getStyleOperator().setStyleIncomplete(bo);
      return bo;
   }

   /**
    * Complete style.
    * @param fig1
    * @param fig2
    * @return
    */
   public ByteObject getStyleFig(ByteObject fig1, ByteObject fig2) {
      return getStyle(new ByteObject[] { fig1, fig2 });
   }

   /**
    * 
    * @param bgFig1
    * @param bgFig2
    * @param borderTBLR border TBLR object
    * @return
    */
   public ByteObject getStyleFiguresWithBorder(ByteObject bgFig1, ByteObject bgFig2, ByteObject borderTBLR) {
      return getStyle(new ByteObject[] { bgFig1, bgFig2 }, null, null, null, borderTBLR, null);

   }

   public ByteObject getStyleNull() {
      ByteObject style = createStyle();
      style.setFlag(STYLE_OFFSET_5_FLAG_X, STYLE_FLAG_X_1_INCOMPLETE, true);
      return style;
   }

   /**
    * 
    * @param bg single layer for background
    * @param content descriptive of content
    * @param anchor box model positioning ANCHOR
    * @param pad box model padding TBLR
    * @param br box model border TBLR
    * @return
    */
   public ByteObject getStyleS(ByteObject bg, ByteObject content, ByteObject anchor, ByteObject pad, ByteObject br) {
      return getStyle(new ByteObject[] { bg }, content, anchor, pad, br, null, null, null, null);

   }

   public ByteObject getTStyleContentColor(int color) {
      ByteObject content = gc.getFigureFactory().getFigStringT_Color(color);
      ByteObject style = getStyle(null, content, null);
      style.setFlag(STYLE_OFFSET_5_FLAG_X, STYLE_FLAG_X_1_INCOMPLETE, true);
      return style;
   }

   public void setTransparent(ByteObject style) {
      style.setFlag(STYLE_OFFSET_5_FLAG_X, STYLE_FLAG_X_1_INCOMPLETE, true);
   }

}
