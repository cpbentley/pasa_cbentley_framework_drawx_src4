/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.style;

import pasa.cbentley.byteobjects.src4.core.BOAbstractFactory;
import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesDrw;
import pasa.cbentley.core.src4.utils.BitUtils;
import pasa.cbentley.framework.coredraw.src4.interfaces.IMFont;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.tech.ITechMergeMaskFigure;
import pasa.cbentley.framework.drawx.src4.tech.ITechMergeMaskFigureString;
import pasa.cbentley.layouter.src4.ctx.IBOTypesLayout;
import pasa.cbentley.layouter.src4.engine.TblrFactory;

public class StyleFactory extends BOAbstractFactory implements IBOStyle, IBOTypesDrw {

   protected final DrwCtx gc;

   public StyleFactory(DrwCtx gc) {
      super(gc.getBOC());
      this.gc = gc;
   }

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

      ByteObject text = gc.getFigureFactory().getFigString(fontID, 0, 0, fontColor);
      
      ByteObject mergeMaskFigure = gc.getFigureFactory().getMergeMaskFigure();
      
      mergeMaskFigure.setFlag(ITechMergeMaskFigureString.FIG_STR_MM_02_FACE_INDEX, ITechMergeMaskFigureString.FIG_STR_MM_02_FACE_FLAG, true);
      mergeMaskFigure.setFlag(ITechMergeMaskFigure.MM_INDEX_VALUES5_FLAG_2_COLOR, ITechMergeMaskFigure.MM_VALUES5_FLAG_2_COLOR, true);
      gc.getMergeMaskFactory().setMergeMask(text, mergeMaskFigure);

      ByteObject content = text;
      return gc.getStyleFactory().getStyle(content, STYLE_OFFSET_1_FLAGA, STYLE_FLAGA_1_CONTENT);
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
      style.setFlag(STYLE_OFFSET_4_FLAG_PERF, STYLE_FLAG_PERF_7_INCOMPLETE, true);
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
      ByteObject style = getBOFactory().createByteObject(TYPE_071_STYLE, STYLE_BASIC_SIZE);
      style.setFlag(pointer, flag, true);
      style.addByteObject(c);
      style.setFlag(STYLE_OFFSET_4_FLAG_PERF, STYLE_FLAG_PERF_7_INCOMPLETE, true);
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
    * @param bg up to 4 figures
    * @param content figure for the content. usually an implicit text effect
    * @param anchor box model's anchoring
    * @param pad box model's padding TBLR
    * @param border box model's border TBLR 
    * @param margin box model's margin TBLR
    * @param fg up to 4 figures
    * @param filters up to 5 filters
    * @param anims up to 3 animations
    * @return
    */
   public ByteObject getStyle(ByteObject[] bg, ByteObject content, ByteObject anchor, ByteObject pad, ByteObject border, ByteObject margin, ByteObject[] fg, ByteObject[] filters, ByteObject[] anims) {
      ByteObject p = getBOFactory().createByteObject(TYPE_071_STYLE, STYLE_BASIC_SIZE);
      int flagv = 0;
      StyleOperator styleOperator = gc.getStyleOperator();
      flagv = styleOperator.setFlagNotNullStyleFieldFlag(content, flagv, STYLE_FLAGA_1_CONTENT, TYPE_050_FIGURE);
      flagv = styleOperator.setFlagNotNullStyleFieldFlag(anchor, flagv, STYLE_FLAGA_2_ANCHOR, TYPE_069_ANCHOR);
      flagv = styleOperator.setFlagNotNullStyleFieldFlag(pad, flagv, STYLE_FLAGA_3_PADDING, IBOTypesLayout.FTYPE_2_TBLR);
      flagv = styleOperator.setFlagNotNullStyleFieldFlag(border, flagv, STYLE_FLAGA_4_BORDER, IBOTypesLayout.FTYPE_2_TBLR);
      flagv = styleOperator.setFlagNotNullStyleFieldFlag(margin, flagv, STYLE_FLAGA_5_MARGIN, IBOTypesLayout.FTYPE_2_TBLR);
      int flagg = 0;
      if (bg != null && bg.length != 0) {
         if (bg.length > 4) {
            throw new IllegalArgumentException("Max 4 backgrounds");
         }
         int start = STYLE_FLAGB_1_BG;
         for (int i = 0; i < bg.length; i++) {
            flagg = styleOperator.setFlagNotNullStyleFieldFlag(bg[i], flagg, start, TYPE_050_FIGURE);
            start = start << 1;
         }
         p.setFlag(STYLE_OFFSET_4_FLAG_PERF, STYLE_FLAG_PERF_1_BG, true);
      }
      if (fg != null && fg.length != 0) {
         if (fg.length > 4) {
            throw new IllegalArgumentException();
         }
         int start = STYLE_FLAGB_5_FG;
         for (int i = 0; i < fg.length; i++) {
            flagg = styleOperator.setFlagNotNullStyleFieldFlag(fg[i], flagg, start, TYPE_050_FIGURE);
            start = start << 1;
         }
         p.setFlag(STYLE_OFFSET_4_FLAG_PERF, STYLE_FLAG_PERF_2_FG, true);
      }
      int fflag = 0;
      if (filters != null && filters.length != 0) {
         if (filters.length > 5) {
            throw new IllegalArgumentException();
         }
         int start = STYLE_FLAGC_1_FILTER_BG;
         for (int i = 0; i < filters.length; i++) {
            fflag = styleOperator.setFlagNotNullStyleFieldFlag(filters[i], fflag, start, TYPE_056_COLOR_FILTER);
            start = start << 1;
         }
         p.setFlag(STYLE_OFFSET_4_FLAG_PERF, STYLE_FLAG_PERF_3_FILTERS, true);
      }
      if (anims != null && anims.length != 0) {
         if (anims.length > 3) {
            throw new IllegalArgumentException();
         }
         int start = STYLE_FLAGC_6_ANIM_ENTRY;
         for (int i = 0; i < anims.length; i++) {
            fflag = styleOperator.setFlagWhenNotNull(anims[i], fflag, start);
            start = start << 1;
         }
         p.setFlag(STYLE_OFFSET_4_FLAG_PERF, STYLE_FLAG_PERF_4_ANIMS, true);
      }

      int total = BitUtils.countBits(flagv) + BitUtils.countBits(flagg) + BitUtils.countBits(fflag);

      p.setByteObjects(new ByteObject[total]);

      p.setValue(STYLE_OFFSET_1_FLAGA, flagv, 1);
      p.setValue(STYLE_OFFSET_2_FLAGB, flagg, 1);
      p.setValue(STYLE_OFFSET_3_FLAGC, fflag, 1);

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
      return getStyle(fig, STYLE_OFFSET_2_FLAGB, STYLE_FLAGB_1_BG);
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
      ByteObject style = getBOFactory().createByteObject(TYPE_071_STYLE, STYLE_BASIC_SIZE);
      style.setFlag(STYLE_OFFSET_4_FLAG_PERF, STYLE_FLAG_PERF_7_INCOMPLETE, true);
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
      ByteObject content = gc.getFigureFactory().getFigStringTColor(color);
      ByteObject style = getStyle(null, content, null);
      style.setFlag(STYLE_OFFSET_4_FLAG_PERF, STYLE_FLAG_PERF_7_INCOMPLETE, true);
      return style;
   }

   public void setTransparent(ByteObject style) {
      style.setFlag(STYLE_OFFSET_4_FLAG_PERF, STYLE_FLAG_PERF_7_INCOMPLETE, true);
   }

}
