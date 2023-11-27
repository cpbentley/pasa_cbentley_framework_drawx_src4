/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.functions.Function;
import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.logging.IStringable;
import pasa.cbentley.core.src4.logging.ITechLvl;
import pasa.cbentley.core.src4.utils.ColorUtils;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;
import pasa.cbentley.framework.drawx.src4.tech.ITechBox;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;
import pasa.cbentley.framework.drawx.src4.tech.ITechMask;
import pasa.cbentley.framework.drawx.src4.utils.AnchorUtils;

/**
 * Follow the drawing process of a String  figure.
 * <br>
 * Draw {@link StringInterval}s
 * <br>
 * Encapsulates the referential to which characters are drawn.
 * <br>
 * <br>
 * 
 * @author Charles-Philip Bentley
 *
 */
public class StringDraw implements IStringable, ITechFigure, IBOTypesDrw, ITechMask, ITechBox, ITechStringer {

   /**
    * Tracks current char x coordinate
    */
   int              cxTracker;

   /**
    * Tracks current char y coordinate
    */
   int              cyTracker;

   private Stringer st;

   private DrwCtx   drc;

   public StringDraw(DrwCtx drc, Stringer stringer) {
      this.drc = drc;
      this.st = stringer;
   }

   /**
    * Align line  from within the string area.
    * <br>
    * <br>
    * Ask Stringer to know if align applies
    * <br>
    * When needed (char position are needed) compute a dxAlign and dyAlign for that line.
    * <br>
    * <br>
    * TODO use and store string width computation
    * <br>
    * <br>
    * @param anchor
    * @param cs
    * @param offset
    * @param len
    */
   void align(char[] cs, int offset, int len) {
   }

   /**
    * 
    * @param g
    * @param chars
    * @param offset
    * @param len
    * @param breaks
    */
   public void drawBlock(GraphicsX g, char[] chars, int offset, int len, int[] breaks) {
      int numLines = breaks[0];
      int end = Math.min(numLines, 0);
      for (int i = 0; i < end; i++) {
         int index = 1 + (i * 2);
         int startOffset = breaks[index];
         int charNum = breaks[index + 1];
         drawLine(g, startOffset, charNum, i);
      }
   }

   public void drawChar(GraphicsX g, char c, int indexRelative) {
      StringFx fx = st.getCharFx(indexRelative);
      StringMetrics sm = st.getMetrics();
      int cx = cxTracker + sm.getCharX(indexRelative);
      int cy = cyTracker + sm.getCharY(indexRelative);
      g.setFont(fx.f);
      g.setColor(fx.color);

      //#debug
      g.toDLog().pDraw("Single Char " + c + " at [" + cx + "," + cy + "]", this, StringDraw.class, "drawChar", ITechLvl.LVL_05_FINE, true);

      g.drawChar(c, cx, cy, ITechBox.ANCHOR);
   }

   /**
    * Draw char in {@link GraphicsX} and increase the cx tracker.
    * <br>
    * Uses char fx is any.
    * <br>
    * When special line structure effect applies, the cy tracker is also applied
    * <br>
    * <br>
    * @param g
    * @param c
    * @param indexRelative relative index
    */
   void drawCharFx(GraphicsX g, char c, int indexRelative) {
      StringFx fx = st.getCharFx(indexRelative);
      StringMetrics sm = st.getMetrics();
      int cx = cxTracker + sm.getCharX(indexRelative);
      int cy = cyTracker + sm.getCharY(indexRelative);
      if (fx.bgFigure != null) {
         int cw = sm.getCharWidth(indexRelative);
         int ch = sm.getCharHeight(indexRelative);
         drc.getFigureOperator().paintFigure(g, cx, cy, cw, ch, fx.bgFigure);
      }
      if (fx.maskChar != null) {
         drc.getMaskOperator().drawMask(g, cx, cy, fx.maskChar, c, fx.f);
      } else {
         g.setFont(fx.f);
         g.setColor(fx.color);
         g.drawChar(c, cx, cy, ANCHOR);
      }
      endOfChar();
   }

   /**
    * Called when y stays the same
    * @param g
    * @param c
    * @param charWidth
    * @param index
    * @param charXPositions
    * @param y
    */
   private void drawCharFx(GraphicsX g, char c, int charWidth, int index, int[] charXPositions, int y) {

   }

   private void drawCharFx(GraphicsX g, char c, int index, int[] charXPositions, int[] yPos) {
      //use this only when several different fx
      StringFx fx = st.getCharFx(index);
      int cx = charXPositions[index];
      int cy = yPos[index];
      if (fx.maskChar != null) {
         drc.getMaskOperator().drawMask(g, cx, cy, fx.maskChar, c, fx.f);
      } else {
         g.setFont(fx.f);
         g.setColor(fx.color);
         g.drawChar(c, cx, cy, ITechBox.ANCHOR);
      }
      endOfChar();

   }

   public void drawLine(char[] chars, int offset, int len) {

   }

   /**
    * Draws [offset,len] as a Line. 
    * <br>
    * The starting offset and length are relative to {@link Stringer} offset!.
    * <br>
    * The method assumes the caller knows that [offset,len] is a line.
    * <br>
    * <br>
    * It applies the given strategy. Reads the CharFx of the first character.
    * If the StringFx has a line fx with graphical artifacts, they will be drawn according to
    * {@link StringMetrics} which gives the line position and properties.
    * <br>
    * <br>
    * 
    * Checks if within clip x.
    * <br>
    * 
    * Position the line horizontally
    * <br>
    * <br>
    * TODO in some case, when there is no charFx, line can be drawn in one go.
    * <br>
    * @param g
    * @param offset relative to offset
    * @param len additional char offset. caller makes sure value are not fail
    * @param wNum
    * <br>
    * <br>
    * 
    */
   void drawLine(GraphicsX g, int offset, int len, int lineIndex) {
      StringFx fxLine = st.getLineFx(lineIndex);
      lineStart(fxLine);
      switch (st.drawLineType) {
         case ITechStringer.TYPE_0_SINGLE_LINE:
            drawLineSingle(g, offset, len);
            break;
         case ITechStringer.TYPE_1_SINGLE_LINE_FX:
            //what StringFX to use? unless there is a specific StringFX. uses the default one
            drawLineSingleFX(g, offset, len);

            break;
         case ITechStringer.TYPE_2_BREAKS:
            drawLineInBreak(g, offset, len, lineIndex);
            break;
         case ITechStringer.TYPE_7_LINE_BREAKS_WORD_BREAKS_FX:
            int[] bs = st.getMetrics().breaksWord; //offsets are relative (0 -based)
            drawLineWords(g, lineIndex, bs);
            break;
         default:
            break;
      }
      lineEnd(fxLine);
   }

   /**
    * Draws a simple line (no fx) in a break configuration. The starting position of the line is provided by {@link StringMetrics}.
    * which compute the alignment.
    * <br>
    * <br>
    * The position of the first character
    * 
    * @param g
    * @param offset
    * @param len
    */
   void drawLineInBreak(GraphicsX g, int offset, int len, int lineIndex) {
      //alignement is done? The StringMetric is not used for this setting.
      int cx = cxTracker + st.getMetrics().getCharX(offset - st.offsetChars);
      int cy = cyTracker + st.getMetrics().getCharY(offset - st.offsetChars);
      g.setFont(st.stringFx.f);
      g.setColor(st.stringFx.color);
      g.drawChars(st.chars, offset, len, cx, cy, st.stringFx.anchor);
   }

   /**
    * Draws the line of characters as a shape
    * <br>
    * <br>
    * Returns the glyph to use for drawing the line mask. graphical context must be drawn before.
    * <br>
    * <br>
    * 
    * @param mask
    */
   public void drawLineMask(GraphicsX g, ByteObject mask, char[] chars, int offset, int len) {
      StringMetrics sm = st.getMetrics();
      int w = sm.getWidthConsumed(offset, len); //the width needed to draw those chars. sum which is given StringMetrics
      int h = st.getMetrics().getLineHeight();
      int bgColor = -1;
      RgbImage maskImg = drc.getCache().createPrimitiveRgb(w, h, bgColor);
      GraphicsX figGraphics = maskImg.getGraphicsX(GraphicsX.MODE_1_IMAGE);
      figGraphics.setColor(ColorUtils.FULLY_OPAQUE_BLACK);
      //do we shift?
      drawShapeString(figGraphics, chars, offset, len, 0, 0);
      figGraphics.drawChars(chars, offset, len, 0, 0, ANCHOR);

      if (mask.hasFlag(MASK_OFFSET_1_FLAG1, MASK_FLAG_1_MASK_FILTER)) {
         ByteObject maskColorFilter = mask.getSubFirst(TYPE_056_COLOR_FILTER);
         drc.getFilterOperator().applyColorFilter(maskColorFilter, maskImg);
         //image will be switch to RGB mode.
      }
      RgbImage mak = drc.getMaskOperator().createMaskedFigure(mask, maskImg);
      //get position of first character
      int finalX = cxTracker + sm.getCharX(offset - st.offsetChars);
      int finalY = cyTracker + sm.getCharY(offset - st.offsetChars);
      //you still have to draw the image in the context FigDrawable or just bg image
      g.drawImage(mak, finalX, finalY, ANCHOR);
   }

   /**
    * Draw the string on {@link GraphicsX} 
    * @param g
    * @param offset
    * @param len
    */
   public void drawLineSingle(GraphicsX g, int offset, int len) {
      //alignement is done? The StringMetric is not used for this setting.
      int day = AnchorUtils.getYAlign(st.anchor, 0, st.areaH, st.stringMetrics.getPrefHeight());
      int dax = AnchorUtils.getXAlign(st.anchor, 0, st.areaW, st.stringMetrics.getPrefWidth());
      int cx = cxTracker + dax;
      int cy = cyTracker + day;
      g.setFont(st.stringFx.f);
      g.setColor(st.stringFx.color);
      //
      int clen = st.chars.length;

      if (offset < 0 || offset >= clen || offset + len > clen) {
         //#debug
         g.toDLog().pNull("Error -> chars clen=" + clen + " offset=" + offset + " len=" + len, this, StringDraw.class, "drawLineSingle", ITechLvl.LVL_05_FINE, true);
      } else {
         g.drawChars(st.chars, offset, len, cx, cy, st.stringFx.anchor);
      }
   }

   /**
    * Single Line drawing without word breaks
    * <br>
    * <br>
    * The line may have the following
    * <li> specific char {@link StringFx}.
    * <li> line mask
    * @param g
    * @param offset
    * @param len
    */
   public void drawLineSingleFX(GraphicsX g, int offset, int len) {
      StringFx fx = st.getCharFx(offset);

      boolean maskDrawn = false;
      if (fx.maskLine != null) {
         drawLineMask(g, fx.maskLine, st.chars, offset, len);
         maskDrawn = true;
      }

      if (st.hasState(ITechStringer.STATE_01_CHAR_EFFECTS)) {
         //as soon as a char has a LineFX it becomes active?
         //draw each char as itself
         for (int i = 0; i < len; i++) {
            int index = st.offsetChars + offset + i;
            drawCharFx(g, st.chars[index], i);
         }
      }

   }

   /**
    * Called when words have to be drawn separately.
    * <br>
    * <br>
    * 
    * @param g
    * @param lineIndex
    * @param wb array of word breaking specified by 
    */
   public void drawLineWords(GraphicsX g, int lineIndex, int[] wb) {
      int numWords = (wb[0] - ITechStringer.BREAK_EXTRA_SIZE + 1) / ITechStringer.BREAK_WINDOW_SIZE;
      for (int i = 0; i < numWords; i++) {
         int index = ITechStringer.BREAK_HEADER_SIZE + (i * ITechStringer.BREAK_WINDOW_SIZE);
         int numChars = wb[index + 1];
         int woffset = wb[index];

         drawWordSingle(g, woffset, numChars);
      }
   }

   /**
    * Draws the line as a shape using
    * <br>
    * <br>
    * @param g
    * @param chars
    * @param offset
    * @param len
    * @param breaks the word breaks if not null means different fxs at the word level
    * @param baseX
    * @param baseY
    */
   public void drawShapeLine(GraphicsX g, char[] chars, int offset, int len, int[] breaks, int baseX, int baseY) {
      if (breaks == null) {
         drawShapeString(g, chars, offset, len, baseX, baseY);
      } else {
         //broken into words
         int numWords = (breaks[0] - ITechStringer.BREAK_EXTRA_SIZE + 1) / ITechStringer.BREAK_WINDOW_SIZE;
         for (int i = 0; i < numWords; i++) {
            int index = ITechStringer.BREAK_HEADER_SIZE + (i + numWords) * ITechStringer.BREAK_WINDOW_SIZE;
            int startOffset = breaks[index];
            int charNum = breaks[index + 1];

            //wOffset decides what to draw
            drawShapeWord(g, chars, startOffset, charNum, baseX, baseY);
            //draw non word characters

            drawShapeString(g, chars, startOffset, len, baseX, baseY);
         }
      }
   }

   /**
    * Method used to draw shape of word/line for masks.
    * <br>
    * <br>
    * 
    * @param g
    * @param chars
    * @param offset absolute index into char array
    * @param len
    */
   public void drawShapeString(GraphicsX g, char[] chars, int offset, int len, int baseX, int baseY) {
      StringMetrics sm = st.getMetrics();
      if (st.hasState(ITechStringer.STATE_14_BASIC_POSITIONING)) {
         g.drawChars(chars, offset, len, baseX, baseY, ANCHOR);
      } else if (st.hasState(ITechStringer.STATE_11_DIFFERENT_FONTS)) {
         drawShapeStringChars(g, chars, offset, len, baseX, baseY, sm);
      }
   }

   public void drawShapeStringChars(GraphicsX g, char[] chars, int offset, int len, int baseX, int baseY, StringMetrics sm) {
      for (int i = 0; i < len; i++) {
         int index = offset + i;
         StringFx fx = st.getCharFx(index);
         char c = chars[index];
         int cx = baseX + sm.getCharX(index);
         int cy = baseY + sm.getCharY(index);
         g.setFont(fx.f);
         int anchor = fx.anchor;
         g.drawChar(c, cx, cy, anchor);
      }
   }

   /**
    * Draws the shape of the whole text using current {@link GraphicsX} color.
    * <br>
    * <br>
    * 
    * @param g
    */
   public void drawShapeText(GraphicsX g, char[] chars, int offset, int len, int[] breaks) {
      if (breaks == null) {
         drawShapeString(g, chars, offset, len, 0, 0);
      } else {
         int numLines = st.getNumOfLines();
         for (int i = 0; i < numLines; i++) {
            int index = 1 + (i + numLines) * ITechStringer.BREAK_WINDOW_SIZE;
            int startOffset = breaks[index];
            int charNum = breaks[index + 1];
            //wOffset decides what to draw
            drawShapeLine(g, chars, startOffset, charNum, st.getWordBreaks(i), 0, 0);
         }
      }
   }

  
   /**
    * Maybe the first words of each line have a special font?
    * <br>
    * <br>
    * 
    * @param g
    * @param chars
    * @param offset
    * @param len
    * @param baseX
    * @param baseY
    */
   public void drawShapeWord(GraphicsX g, char[] chars, int offset, int len, int baseX, int baseY) {
      if (st.hasState(ITechStringer.STATE_09_WORD_FX)) {

      }
      drawShapeString(g, chars, offset, len, baseX, baseY);
   }

   /**
    * Draws all the {@link Stringer} characters
    * @param g
    */
   public void drawText(GraphicsX g) {

   }

   /**
    * When Trimmed, len of chars is kept -2.
    * <br>
    * <br>
    * What is the trim cue for word breaking? it is nothing.
    * <br>
    * <br>
    * 
    */
   public void drawTrimCue(GraphicsX g, int step, int index) {
      drawCharFx(g, '.', index);
      drawCharFx(g, '.', index + 1);
   }

   public void drawWordMask(GraphicsX g, ByteObject mask) {
      RgbImage maskImg = null;
      if (mask.hasFlag(MASK_OFFSET_1_FLAG1, MASK_FLAG_1_MASK_FILTER)) {
         ByteObject maskColorFilter = mask.getSubFirst(TYPE_056_COLOR_FILTER);
         drc.getFilterOperator().applyColorFilter(maskColorFilter, maskImg);
         //image will be switch to RGB mode.
      }
      drc.getMaskOperator().createMaskedFigure(mask, maskImg);
   }

   /**
    * Draw the given character interval as a word.
    * <br>
    * <br>
    * This is only called when there is a {@link StringFx} scoped to {@link IBOFxStr#FX_SCOPE_1_WORD}.
    * <br>
    * <br>
    * 
    * @param g
    * @param offset
    * @param len
    */
   public void drawWordSingle(GraphicsX g, int offset, int len) {

   }

   /**
    * Draws the word [offset,len]
    * @param g
    * @param offset
    * @param len
    */
   public void drawWordSingleFX(GraphicsX g, int offset, int len) {
      StringFx fx = st.getCharFx(offset);

      if (fx.maskWord != null) {
         int[] bs = st.getMetrics().breaksWord; //offsets are relative (0 -based)
         int relOffset = offset - st.offsetChars;
         int numWords = (bs[0] - ITechStringer.BREAK_EXTRA_SIZE + 1) / ITechStringer.BREAK_WINDOW_SIZE;
         for (int i = 0; i < numWords; i++) {
            int index = ITechStringer.BREAK_HEADER_SIZE + (i * ITechStringer.BREAK_WINDOW_SIZE);
            int woffset = bs[index];
            int wlen = bs[index + 1];
            int firstChar = st.offsetChars + woffset;
            int lastChar = st.offsetChars + woffset + wlen;
            if (lastChar > offset && firstChar < offset + len) {

               drawLineMask(g, fx.maskWord, st.chars, woffset, wlen);
            }
         }
         if (st.hasState(ITechStringer.STATE_09_WORD_FX)) {
            //line must be written word by word. and each word may have to be written char by char
            drawLineWords(g, 0, bs);
         }
      }
   }

   /**
    * Decides where the next character on the line will be.
    * <br>
    * <br>
    * A {@link Function}
    * <br>
    * <br>
    * What about the rotation? we want the characters to follow the curve.
    * <br>
    * Draw image and rotate it with {@link RgbImage} and {@link RotateFloatingPoint}.
    * <br>
    * <br>
    * By default, 
    */
   private void endOfChar() {

   }

   public void init(int x, int y) {
      cxTracker = x;
      cyTracker = y;
   }

   /**
    * Work to do at the end of a line.
    * <br>
    * <br>
    * For {@link ITechStringer#STATE_04_TRIMMED} ?
    * <br>
    * <br>
    * Line {@link StringFx} decides where the next line be located 
    * relative to the previous line.
    */
   private void lineEnd(StringFx fx) {
   }

   /**
    * Sets specific line Font and Fx.
    * <br>
    * <br>
    * 
    */
   private void lineStart(StringFx fx) {

   }

   //#mdebug
   public String toString() {
      return Dctx.toString(this);
   }

   public void toString(Dctx dc) {
      dc.root(this, StringDraw.class, 591);
      dc.append(" Tracker=[" + cxTracker + "," + cyTracker + "]");
   }

   public String toString1Line() {
      return Dctx.toString1Line(this);
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, "StringDraw");
      dc.append(" Tracker=[" + cxTracker + "," + cyTracker + "]");
   }
   //#enddebug

   public UCtx toStringGetUCtx() {
      return drc.getUCtx();
   }

}
