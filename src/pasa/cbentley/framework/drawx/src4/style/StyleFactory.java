package pasa.cbentley.framework.drawx.src4.style;

import pasa.cbentley.byteobjects.src4.core.BOAbstractFactory;
import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.utils.BitUtils;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.drawx.src4.factories.TblrFactory;

public class StyleFactory extends BOAbstractFactory implements IStyle, IBOTypesDrw {

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
    * Transparent style defining only anchors
    * <br>
    * Valid alignment values are
    * <li> {@link ByteObject#ALIGN_LEFT} 
    * <li> {@link ByteObject#ALIGN_CENTER}
    * <li> {@link ByteObject#ALIGN_RIGHT}
    * <li> {@link ByteObject#ALIGN_TOP}
    * <li> {@link ByteObject#ALIGN_BOTTOM}
    * @param ha
    * @param va
    * @return
    */
   public ByteObject getStyleContentAnchor(int ha, int va) {
      ByteObject anchor = gc.getBoxEng().getAnchor(ha, va);
      ByteObject style = getStyle(null, null, anchor);
      style.setFlag(A_OBJECT_OFFSET_2_FLAG, A_OBJECT_FLAG_1_INCOMPLETE, true);
      return style;
   }
   /**
    * A Style with just one component identified by its flag and flag pointer.
    * <br>
    * <br>
    * Style is incomplete i.e. 
    * {@link IObject#A_OBJECT_FLAG_1_INCOMPLETE}
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
      ByteObject p = getBOFactory().createByteObject(TYPE_123_STYLE, STYLE_BASIC_SIZE);
      p.setFlag(pointer, flag, true);
      p.addByteObject(c);
      p.setFlag(A_OBJECT_OFFSET_2_FLAG, A_OBJECT_FLAG_1_INCOMPLETE, true);
      return p;
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
      ByteObject p = getBOFactory().createByteObject(TYPE_123_STYLE, STYLE_BASIC_SIZE);
      int flagv = 0;
      StyleOperator styleOperator = gc.getStyleOperator();
      flagv = styleOperator.updateStyleFieldFlag(content, flagv, STYLE_FLAGV_1CONTENT, TYPE_050_FIGURE);
      flagv = styleOperator.updateStyleFieldFlag(anchor, flagv, STYLE_FLAGV_2ANCHOR, TYPE_051_BOX);
      flagv = styleOperator.updateStyleFieldFlag(pad, flagv, STYLE_FLAGV_3PADDING, TYPE_060_TBLR);
      flagv = styleOperator.updateStyleFieldFlag(border, flagv, STYLE_FLAGV_4BORDER, TYPE_060_TBLR);
      flagv = styleOperator.updateStyleFieldFlag(margin, flagv, STYLE_FLAGV_5MARGIN, TYPE_060_TBLR);
      int flagg = 0;
      if (bg != null && bg.length != 0) {
         if (bg.length > 4) {
            throw new IllegalArgumentException("Max 4 backgrounds");
         }
         int start = STYLE_FLAGG_1BG;
         for (int i = 0; i < bg.length; i++) {
            flagg = styleOperator.updateStyleFieldFlag(bg[i], flagg, start, TYPE_050_FIGURE);
            start = start << 1;
         }
         p.setFlag(STYLE_OFFSET_4FLAG_PERF, STYLE_FLAG_PERF_1BG, true);
      }
      if (fg != null && fg.length != 0) {
         if (fg.length > 4) {
            throw new IllegalArgumentException();
         }
         int start = STYLE_FLAGG_5FG;
         for (int i = 0; i < fg.length; i++) {
            flagg = styleOperator.updateStyleFieldFlag(fg[i], flagg, start, TYPE_050_FIGURE);
            start = start << 1;
         }
         p.setFlag(STYLE_OFFSET_4FLAG_PERF, STYLE_FLAG_PERF_2FG, true);
      }
      int fflag = 0;
      if (filters != null && filters.length != 0) {
         if (filters.length > 5) {
            throw new IllegalArgumentException();
         }
         int start = STYLE_FLAGF_1FILTER_BG;
         for (int i = 0; i < filters.length; i++) {
            fflag = styleOperator.updateStyleFieldFlag(filters[i], fflag, start, TYPE_056_COLOR_FILTER);
            start = start << 1;
         }
         p.setFlag(STYLE_OFFSET_4FLAG_PERF, STYLE_FLAG_PERF_3FILTERS, true);
      }
      if (anims != null && anims.length != 0) {
         if (anims.length > 3) {
            throw new IllegalArgumentException();
         }
         int start = STYLE_FLAGF_6ANIM_ENTRY;
         for (int i = 0; i < anims.length; i++) {
            fflag = styleOperator.updateStyleFieldFlag(anims[i], fflag, start);
            start = start << 1;
         }
         p.setFlag(STYLE_OFFSET_4FLAG_PERF, STYLE_FLAG_PERF_4ANIMS, true);
      }

      int total = BitUtils.countBits(flagv) + BitUtils.countBits(flagg) + BitUtils.countBits(fflag);

      p.setByteObjects(new ByteObject[total]);

      p.setValue(STYLE_OFFSET_1FLAGV, flagv, 1);
      p.setValue(STYLE_OFFSET_2FLAGG, flagg, 1);
      p.setValue(STYLE_OFFSET_3FLAGF, fflag, 1);

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
      return getStyle(bg, content, anchor, tblrFactory.getTBLR(pad), tblrFactory.getTBLR(border));
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
      return getStyle(fig, STYLE_OFFSET_2FLAGG, STYLE_FLAGG_1BG);
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
      ByteObject p = getBOFactory().createByteObject(TYPE_123_STYLE, STYLE_BASIC_SIZE);
      p.setFlag(A_OBJECT_OFFSET_2_FLAG, A_OBJECT_FLAG_1_INCOMPLETE, true);
      return p;
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
      style.setFlag(A_OBJECT_OFFSET_2_FLAG, A_OBJECT_FLAG_1_INCOMPLETE, true);
      return style;
   }

   public void setT(ByteObject style) {
      style.setFlag(A_OBJECT_OFFSET_2_FLAG, A_OBJECT_FLAG_1_INCOMPLETE, true);
   }
}
