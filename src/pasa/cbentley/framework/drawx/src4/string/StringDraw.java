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
 * <br>
 * Encapsulates the referential to which characters are drawn.
 * <br>
 * <br>
 * 
 * @author Charles-Philip Bentley
 *
 */
public class StringDraw implements IStringable, ITechFigure, IBOTypesDrw, ITechMask, ITechBox {

   /**
    * Tracks current char x coordinate
    */
   int                cxTracker;

   /**
    * Tracks current char y coordinate
    */
   int                cyTracker;

   private Stringer   stringer;

   private DrwCtx drc;

   public StringDraw(DrwCtx drc, Stringer stringer) {
      this.drc = drc;
      this.stringer = stringer;
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
      StringFx fx = stringer.getCharFx(indexRelative);
      StringMetrics sm = stringer.getMetrics();
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
      StringFx fx = stringer.getCharFx(indexRelative);
      StringMetrics sm = stringer.getMetrics();
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
      StringFx fx = stringer.getCharFx(index);
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
   public void drawLine(GraphicsX g, int offset, int len, int lineIndex) {
      StringFx fxLine = stringer.getLineFx(lineIndex);
      lineStart(fxLine);
      switch (stringer.drawLineType) {
         case Stringer.TYPE_0_SINGLE_LINE:
            drawLineSingle(g, offset, len);
            break;
         case Stringer.TYPE_1_SINGLE_LINE_FX:
            //what StringFX to use? unless there is a specific StringFX. uses the default one
            drawLineSingleFX(g, offset, len);

            break;
         case Stringer.TYPE_2_BREAKS:
            drawLineInBreak(g, offset, len, lineIndex);
            break;
         case Stringer.TYPE_7_LINE_BREAKS_WORD_BREAKS_FX:
            int[] bs = stringer.getMetrics().breaksWord; //offsets are relative (0 -based)
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
   public void drawLineInBreak(GraphicsX g, int offset, int len, int lineIndex) {
      //alignement is done? The StringMetric is not used for this setting.
      int cx = cxTracker + stringer.getMetrics().getCharX(offset - stringer.offsetChars);
      int cy = cyTracker + stringer.getMetrics().getCharY(offset - stringer.offsetChars);
      g.setFont(stringer.fx.f);
      g.setColor(stringer.fx.color);
      g.drawChars(stringer.chars, offset, len, cx, cy, stringer.fx.anchor);
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
      StringMetrics sm = stringer.getMetrics();
      int w = sm.getWidthConsumed(offset, len); //the width needed to draw those chars. sum which is given StringMetrics
      int h = stringer.getMetrics().getLineHeight();
      int bgColor = -1;
      RgbImage maskImg = drc.getCache().createPrimitiveRgb(w, h, bgColor);
      GraphicsX figGraphics = maskImg.getGraphicsX(GraphicsX.MODE_1_IMAGE);
      figGraphics.setColor(ColorUtils.FULLY_OPAQUE_BLACK);
      //do we shift?
      drawShapeString(figGraphics, chars, offset, len, 0, 0);
      figGraphics.drawChars(chars, offset, len, 0, 0, ANCHOR);

      if (mask.hasFlag(MASK_OFFSET_1FLAG1, MASK_FLAG_1MASK_FILTER)) {
         ByteObject maskColorFilter = mask.getSubFirst(TYPE_056_COLOR_FILTER);
         drc.getFilterOperator().applyColorFilter(maskColorFilter, maskImg);
         //image will be switch to RGB mode.
      }
      RgbImage mak = drc.getMaskOperator().createMaskedFigure(g, mask, maskImg);
      //get position of first character
      int finalX = cxTracker + sm.getCharX(offset - stringer.offsetChars);
      int finalY = cyTracker + sm.getCharY(offset - stringer.offsetChars);
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
      int day = AnchorUtils.getYAlign(stringer.anchor, 0, stringer.areaH, stringer.metrics.getPrefHeight());
      int dax = AnchorUtils.getXAlign(stringer.anchor, 0, stringer.areaW, stringer.metrics.getPrefWidth());
      int cx = cxTracker + dax;
      int cy = cyTracker + day;
      g.setFont(stringer.fx.f);
      g.setColor(stringer.fx.color);
      //
      int clen = stringer.chars.length;
      
      if (offset < 0 || offset >= clen || offset + len > clen) {
         //#debug
         g.toDLog().pDraw("chars clen=" + clen + " offset=" + offset + " len=" + len, this, StringDraw.class, "drawLineSingle", ITechLvl.LVL_05_FINE, true);
      } else {
         g.drawChars(stringer.chars, offset, len, cx, cy, stringer.fx.anchor);
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
      StringFx fx = stringer.getCharFx(offset);

      boolean maskDrawn = false;
      if (fx.maskLine != null) {
         drawLineMask(g, fx.maskLine, stringer.chars, offset, len);
         maskDrawn = true;
      }

      if (stringer.hasState(Stringer.STATE_01_CHAR_EFFECTS)) {
         //as soon as a char has a LineFX it becomes active?
         //draw each char as itself
         for (int i = 0; i < len; i++) {
            int index = stringer.offsetChars + offset + i;
            drawCharFx(g, stringer.chars[index], i);
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
      int numWords = (wb[0] - Stringer.BREAK_EXTRA_SIZE + 1) / Stringer.BREAK_WINDOW_SIZE;
      for (int i = 0; i < numWords; i++) {
         int index = Stringer.BREAK_HEADER_SIZE + (i * Stringer.BREAK_WINDOW_SIZE);
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
         int numWords = (breaks[0] - Stringer.BREAK_EXTRA_SIZE + 1) / Stringer.BREAK_WINDOW_SIZE;
         for (int i = 0; i < numWords; i++) {
            int index = Stringer.BREAK_HEADER_SIZE + (i + numWords) * Stringer.BREAK_WINDOW_SIZE;
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
      StringMetrics sm = stringer.getMetrics();
      if (stringer.hasState(Stringer.STATE_14_BASIC_POSITIONING)) {
         g.drawChars(chars, offset, len, baseX, baseY, ANCHOR);
      } else if (stringer.hasState(Stringer.STATE_11_DIFFERENT_FONTS)) {
         drawShapeStringChars(g, chars, offset, len, baseX, baseY, sm);
      }
   }

   public void drawShapeStringChars(GraphicsX g, char[] chars, int offset, int len, int baseX, int baseY, StringMetrics sm) {
      for (int i = 0; i < len; i++) {
         int index = offset + i;
         StringFx fx = stringer.getCharFx(index);
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
         int numLines = stringer.getNumOfLines();
         for (int i = 0; i < numLines; i++) {
            int index = 1 + (i + numLines) * Stringer.BREAK_WINDOW_SIZE;
            int startOffset = breaks[index];
            int charNum = breaks[index + 1];
            //wOffset decides what to draw
            drawShapeLine(g, chars, startOffset, charNum, stringer.getWordBreaks(i), 0, 0);
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
      if (stringer.hasState(Stringer.STATE_09_WORD_FX)) {

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
      if (mask.hasFlag(MASK_OFFSET_1FLAG1, MASK_FLAG_1MASK_FILTER)) {
         ByteObject maskColorFilter = mask.getSubFirst(TYPE_056_COLOR_FILTER);
         drc.getFilterOperator().applyColorFilter(maskColorFilter, maskImg);
         //image will be switch to RGB mode.
      }
      drc.getMaskOperator().createMaskedFigure(g, mask, maskImg);
   }

   /**
    * Draw the given character interval as a word.
    * <br>
    * <br>
    * This is only called when there is a {@link StringFx} scoped to {@link IFxStr#FX_SCOPE_1_WORD}.
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
      StringFx fx = stringer.getCharFx(offset);

      if (fx.maskWord != null) {
         int[] bs = stringer.getMetrics().breaksWord; //offsets are relative (0 -based)
         int relOffset = offset - stringer.offsetChars;
         int numWords = (bs[0] - Stringer.BREAK_EXTRA_SIZE + 1) / Stringer.BREAK_WINDOW_SIZE;
         for (int i = 0; i < numWords; i++) {
            int index = Stringer.BREAK_HEADER_SIZE + (i * Stringer.BREAK_WINDOW_SIZE);
            int woffset = bs[index];
            int wlen = bs[index + 1];
            int firstChar = stringer.offsetChars + woffset;
            int lastChar = stringer.offsetChars + woffset + wlen;
            if (lastChar > offset && firstChar < offset + len) {

               drawLineMask(g, fx.maskWord, stringer.chars, woffset, wlen);
            }
         }
         if (stringer.hasState(Stringer.STATE_09_WORD_FX)) {
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
    * For {@link Stringer#STATE_04_TRIMMED} ?
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
      dc.root(this, "StringDraw");
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
