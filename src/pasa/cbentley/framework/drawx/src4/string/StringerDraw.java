/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.objects.function.Function;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.logging.IStringable;
import pasa.cbentley.core.src4.logging.ITechLvl;
import pasa.cbentley.core.src4.structs.IntInterval;
import pasa.cbentley.core.src4.structs.IntIntervals;
import pasa.cbentley.core.src4.text.StringInterval;
import pasa.cbentley.framework.coredraw.src4.interfaces.IMFont;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrawX;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOBox;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOMask;
import pasa.cbentley.framework.drawx.src4.string.interfaces.ITechStringer;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;

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
public class StringerDraw extends ObjectDrw implements IStringable, ITechFigure, IBOTypesDrawX, IBOMask, IBOBox, ITechStringer {

   private int            currentLineOffset;

   /**
    * Tracks current char x coordinate
    */
   int                    cxTracker;

   /**
    * Tracks current char y coordinate
    */
   int                    cyTracker;

   private int            firstCharOffsetRelLine;

   /**
    * usually 0, but when drawing a scrolled text horizontally, each line starts at this offset.
    * This value is never modified
    */
   private int            firstCharOffsetRequested;

   private int            firstLineOffsetRequested;

   private StringFx       fx;

   private StringFxLeaf   fxLeaf;

   private IntInterval    interval;

   private int            intervalIndex;

   private boolean        isAbsoluteXY = true;

   private boolean        isCheckWidth;

   private int            lastNumDrawnChars;

   private LineStringer   line;

   int                    lineOffsetRelTracker;

   private int            numberOfCharDrawnOnCurrentLine;

   private int            numberOfCharsRequested;

   private int            numberOfLines;

   private int            numberOfLinesActual;

   private int            numberOfLinesRequested;

   private int            numCharsInThisStyleInterval;

   private int            numCharsToBeDrawnNext;

   private int            numCharsUndrawnLeftInTheLine;

   private int            pixelsHeightDrawn;

   private int            pixelsHeightRequest;

   private int            pixelsWidthDrawnOnCurrentLine;

   private int            pixelsWidthRequest;

   private final Stringer st;

   private int            xLineTracker;

   private int            yLineTracker;

   private ByteObject     maskChar;

   public StringerDraw(DrwCtx drc, Stringer stringer) {
      super(drc);
      this.st = stringer;
   }

   /**
    * Return the number of characters drawn
    * @param g
    * @param line
    * @param wOffset
    * @param len
    * @param wSize, available pixel size.. we do not use character count because prop font can cram a lot
    * of small characters, thus displaying a maximum of characters
    * @return
    */
   void drawALine(GraphicsX g) {
      //#debug
      line.debugOffsetLineIsInside(firstCharOffsetRequested, 1);

      /////////////////////////////////////
      //line background figure
      int dx = line.getX();
      int dy = line.getY(); //line Y have been computed 
      if (isAbsoluteXY) {
         xLineTracker = cxTracker + dx;
         yLineTracker = cyTracker + dy;
      } else {
         xLineTracker = cxTracker;
         yLineTracker = cyTracker;
      }
      //TODO.. anchoring inside a line. when font have different size. biggest
      // H alignement of fonts smaller than the line Height ?
      //TODO parameters telling which lines H to use for all lines
      // selecting a Char makes them bigger. so we want metrics to use that style

      //area covering the whole line
      if (line.getFigureBG() != null) {
         int h = line.getPixelsH();
         int w = line.getStringer().getAreaW();
         if (pixelsWidthRequest > 0) {
            w = Math.min(pixelsWidthRequest, w);
         }
         drc.getFigureOperator().paintFigure(g, xLineTracker, yLineTracker, w, h, line.getFigureBG());
      }
      //////////////////////////////////////

      //its not always text will disappear 
      isCheckWidth = st.getWordwrap() == WORDWRAP_0_NONE; //when wordwrap is none, string will be drawm

      pixelsWidthDrawnOnCurrentLine = 0;
      numberOfCharDrawnOnCurrentLine = 0;

      firstCharOffsetRelLine = firstCharOffsetRequested;
      IntIntervals intervalsOfLeaves = st.getIntervalsOfLeaves();
      //the interval that contains the first character to be drawn here
      int offsetRelStringer = line.getOffset() + firstCharOffsetRelLine;
      intervalIndex = intervalsOfLeaves.getIntervalIntersectIndex(offsetRelStringer);
      interval = intervalsOfLeaves.getInterval(intervalIndex);
      lineOffsetRelTracker = firstCharOffsetRelLine;
      //now we have the offset, we must compute the number of chars from that interval to draw
      //depends on the pixelsW if not zero
      numCharsInThisStyleInterval = interval.getDistanceToEnd(offsetRelStringer);
      numCharsUndrawnLeftInTheLine = getNumCharactersInLine() - firstCharOffsetRelLine;
      numCharsToBeDrawnNext = Math.min(numCharsInThisStyleInterval, numCharsUndrawnLeftInTheLine);
      if (pixelsWidthRequest != 0) {
         //reduce number of char based on 
      }
      if (numberOfCharsRequested != 0) {
         numCharsToBeDrawnNext = Math.min(numberOfCharsRequested, numCharsToBeDrawnNext);
      }
      do {

         fxLeaf = (StringFxLeaf) interval.getPayload();
         fx = fxLeaf.getFx();

         //we know the distance already
         drawBgFigureText(g, xLineTracker, yLineTracker);
         drawChoice(g);

         numCharsUndrawnLeftInTheLine -= lastNumDrawnChars;
         lineOffsetRelTracker += lastNumDrawnChars;
         //learn how many chars we can draw on this interval and the size
         numberOfCharDrawnOnCurrentLine += lastNumDrawnChars;

      } while (isContinueOnCurrentLine());
   }

   /**
    * Draw basic for an intervla
    * @param g
    * @param fx
    * @param line
    * @param firstCharOffsetRelLine
    */
   private void drawBasic(GraphicsX g) {
      //now only deals with color variance
      IMFont font = fx.getFont();
      g.setFont(font);
      int x = xLineTracker;
      int y = yLineTracker;
      char[] data = line.getCharArrayRef();
      int offsetData = line.getCharArrayRefOffset() + lineOffsetRelTracker;
      int lengthInData = numCharsToBeDrawnNext;

      getUC().getStrU().checkValid(data, offsetData, lengthInData);

      if (maskChar != null) {
         int dx = x;
         for (int i = 0; i < lengthInData; i++) {
            int cw = line.getCharWidth(i);
            char c = data[offsetData + i];
            drc.getMaskOperator().drawMask(g, dx, y, maskChar, c, font);
            dx += cw;
         }
      } else if (fx.isColorStable()) {
         if (line.isJustified()) {
            g.setColor(fx.getColor());
            int dx = x;
            for (int i = 0; i < lengthInData; i++) {
               int cw = line.getCharWidth(i);
               char c = data[offsetData + i];
               g.drawChar(c, dx, y, ANCHOR);
               dx += cw;
            }
         } else {
            g.setColor(fx.getColor());
            g.drawChars(data, offsetData, lengthInData, x, y, ANCHOR);
         }
      } else {
         int count = 0;
         int dx = x;
         while (count < lengthInData) {
            int numChars = fx.setColor(g, count);
            if (numChars < 0) {
               numChars = 1;
            } else if (numChars > lengthInData) {
               numChars = lengthInData;
            }
            g.drawChars(data, offsetData + count, numChars, dx, y, ANCHOR);
            for (int i = 0; i < numChars; i++) {
               int cw = line.getCharWidth(count + i);
               dx += cw;
            }
            count += numChars;
         }
      }
      lastNumDrawnChars = lengthInData;
      int widthDrawn = line.getCharsWidthConsumed(lineOffsetRelTracker, lengthInData);
      this.pixelsWidthDrawnOnCurrentLine += widthDrawn;
      this.xLineTracker += widthDrawn;
   }

   private void drawBasicCharsFx(GraphicsX g, ByteObject mask, StringFxLeaf fxLeaf, StringFx fx, IntInterval interval, LineStringer line, int firstCharOffset, ByteObject bg) {
      //now only deals with color variance

      char[] data = line.getCharArrayRef();
      int offset = line.getCharArrayRefOffset() + firstCharOffset;
      int numCharsInThisStyle = interval.getDistanceToEnd(firstCharOffset);
      int len = numCharsInThisStyle;

      int ch = line.getPixelsH();

      int cx = xLineTracker;
      int cy = yLineTracker;
      IMFont font = fx.getFont();
      g.setColor(fx.getColor());
      g.setFont(font);

      for (int i = 0; i < len; i++) {
         char c = data[offset + i];
         int intervalOffset = firstCharOffset + i;
         int cw = line.getCharWidth(intervalOffset);
         if (bg != null) {
            drc.getFigureOperator().paintFigure(g, cx, cy, cw, ch, bg);
         }
         if (mask == null) {
            if (!fx.isColorStable()) {
               fx.setColor(g, intervalOffset); //index of the interval
            }
            g.drawChar(c, cx, cy, ANCHOR);
         } else {
            drc.getMaskOperator().drawMask(g, cx, cy, mask, c, font);
         }
         pixelsWidthDrawnOnCurrentLine += cw;
         cx += cw;
      }
   }

   public void drawBasicLineMask(GraphicsX g, ByteObject mask) {
      //char[] chars = line.getCharArrayRef();
      //int numCharsInThisStyle = intervalFirst.getDistanceToEnd(firstCharOffset);

      char[] chars = line.getCharArrayRef();
      int offset = line.getCharArrayRefOffset() + lineOffsetRelTracker;
      int len = numCharsToBeDrawnNext;

      StringMetrics sm = st.getMetrics();
      int widthDrawn = line.getCharsWidthConsumed(lineOffsetRelTracker, len);
      int w = widthDrawn;
      int h = line.getPixelsH();

      IMFont f = fx.getFont();
      drc.getMaskOperator().drawMask(g, xLineTracker, yLineTracker, mask, chars, offset, len, f, w, h);

      lastNumDrawnChars = len;
      this.pixelsWidthDrawnOnCurrentLine += widthDrawn;
      this.xLineTracker += widthDrawn;
   }

   private void drawBgFigureText(GraphicsX g, int x, int y) {
      if (fx.getFigureBG() != null) {
         int scope = fx.getScope();
         if (scope == FX_SCOPE_0_TEXT) {
            int h = line.getPixelsH();
            int w = line.getCharsWidthConsumed(lineOffsetRelTracker, numCharsToBeDrawnNext);
            if (pixelsWidthRequest > 0) {
               w = Math.min(pixelsWidthRequest, w);
            }
            drc.getFigureOperator().paintFigure(g, x, y, w, h, fx.getFigureBG());
         } else if (scope == FX_SCOPE_2_WORD) {
            int[] words = line.getWordBreaks();
         } else {
            //char scope
         }
      }
   }

   private void drawChoice(GraphicsX g) {
      if (fx.isStableFont()) {

      }
      int type = fx.getTypeStruct();

      //TODO special case.. justify with text mask.

      ByteObject mask = fx.getMask();
      if (fx.isMasked()) {
         int scope = fx.getScope();
         //can be masked on char,text, word
         if (scope == FX_SCOPE_0_TEXT) {
            //beware we can have scoped width
            drawBasicLineMask(g, mask);
         } else if (scope == FX_SCOPE_1_CHAR) {
            maskChar = mask;
            drawBasic(g);
         }
      } else {
         if (fx.getFigureBG() != null) {
            int scope = fx.getScope();
            if (scope == FX_SCOPE_0_TEXT) {
               if (type == FX_STRUCT_TYPE_0_BASIC_HORIZONTAL) {
                  drawBasic(g);
               } else if (type == FX_STRUCT_TYPE_1_METRICS_X) {

               }
            } else if (scope == FX_SCOPE_1_CHAR) {
               drawBasicCharsFx(g, mask, fxLeaf, fx, interval, line, firstCharOffsetRelLine, fx.getFigureBG());
            }
         } else {
            //no bg, no mask
            if (type == FX_STRUCT_TYPE_0_BASIC_HORIZONTAL) {
               drawBasic(g);
            } else if (type == FX_STRUCT_TYPE_1_METRICS_X) {

            }
         }
      }
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
    * @param offset relative to charOffset
    * @param len additional char offset. caller makes sure value are not fail
    * @param wNum
    * <br>
    * <br>
    * 
    */
   void drawLine(GraphicsX g, int offset, int len, int lineIndex) {
      StringFx fxLine = st.getLineFx(lineIndex);
      lineStart(fxLine);
      lineEnd(fxLine);
   }

   public void drawOffsetsLines(GraphicsX g) {
      StringMetrics sm = st.stringMetrics;
      //      if (firstCharOffsetRequested != 0 || firstLineOffsetRequested != 0) {
      //         if (firstCharOffsetRequested > 0) {
      //            cxTracker -= sm.getCharX(firstCharOffsetRequested);
      //         }
      //         if (firstLineOffsetRequested > 0) {
      //            cyTracker -= sm.getLineY(firstLineOffsetRequested);
      //         }
      //      }
      pixelsHeightDrawn = 0;
      currentLineOffset = firstLineOffsetRequested;
      //draw at least 1 line
      do {
         line = sm.getLine(currentLineOffset);
         if (getNumCharactersInLine() == 0) {
            //TODO draw line artifacts
         } else {
            drawALine(g);
         }
         pixelsHeightDrawn += line.getPixelsH();
         if (!isAbsoluteXY) {
            //we do not use line getY. so we must increment for next line
            cyTracker += line.getPixelsH();
         }
      } while (isContinueH());
   }

   private int getNumCharactersInLine() {
      return line.getNumCharVisible();
   }

   public void drawUniqueCharBasic(GraphicsX g, char c, int indexRelative) {
      StringFx fx = st.getCharFx(indexRelative);
      StringMetrics sm = st.getMetrics();
      int cx = cxTracker + sm.getCharX(indexRelative);
      int cy = cyTracker + sm.getCharY(indexRelative);
      g.setFont(fx.font);
      g.setColor(fx.color);

      //#debug
      g.toDLog().pDraw("Single Char " + c + " at [" + cx + "," + cy + "]", this, StringerDraw.class, "drawChar", ITechLvl.LVL_05_FINE, true);

      g.drawChar(c, cx, cy, IBOBox.ANCHOR);
   }

   /**
    * Draw char in {@link GraphicsX} as if it is drawn alone.
    * Uses char fx is any.
    * <br>
    * When special line structure effect applies, the cy tracker is also applied
    * <br>
    * <br>
    * drawUnique are used when {@link Stringer} needs to redraw a single char.
    * @param g
    * @param c
    * @param indexRelative relative index
    */
   void drawUniqueCharFx(GraphicsX g, char c, int indexRelative) {
      StringFx fx = st.getCharFx(indexRelative);
      StringMetrics sm = st.getMetrics();
      int cx = cxTracker + sm.getCharX(indexRelative);
      int cy = cyTracker + sm.getCharY(indexRelative);
      int lineIndex = st.getLineIndexFromCharIndex(indexRelative);
      if (fx.bgFigure != null) {
         int cw = sm.getCharWidth(indexRelative);
         int ch = sm.getCharHeight(indexRelative);
         drc.getFigureOperator().paintFigure(g, cx, cy, cw, ch, fx.bgFigure);
      }
      if (fx.getMask() != null) {
         drc.getMaskOperator().drawMask(g, cx, cy, fx.getMask(), c, fx.font);
      } else {
         g.setFont(fx.font);
         fx.setColor(g, indexRelative);
         g.drawChar(c, cx, cy, ANCHOR);
      }
      endOfChar();
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

   public int getLastNumDrawnChars() {
      return lastNumDrawnChars;
   }

   private void initOffsets() {
      //

   }

   /**
    * Opti to stop drawing early when pixels W and H are exhausted
    * @param pixelsWidth
    * @param pixelsHeight
    */
   public void initRequestArea(int pixelsWidth, int pixelsHeight) {
      this.pixelsWidthRequest = pixelsWidth;
      this.pixelsHeightRequest = pixelsHeight;
   }

   /**
    * 
    * @param offsetStart 
    * @param lengthChars number of chars.. algo will cap it to available chars
    * so no need to check if it is overflowing
    */
   public void initRequestChars(int offsetStart, int lengthChars) {
      this.firstCharOffsetRequested = offsetStart;
      this.numberOfCharsRequested = lengthChars;
   }

   public void initRequestLines(int firstLineOffset, int numOfLines) {
      this.firstLineOffsetRequested = firstLineOffset;
      this.numberOfLinesRequested = numOfLines;
      numberOfLines = st.getNumOfLines();
      numberOfLinesActual = Math.min(numberOfLines, numberOfLinesRequested);
   }

   public void initTrackerXY(int x, int y) {
      cxTracker = x;
      cyTracker = y;
   }

   public boolean isAbsoluteXY() {
      return isAbsoluteXY;
   }

   private boolean isContinueH() {
      if (pixelsHeightRequest != 0) {
         boolean isHeightDone = pixelsHeightDrawn >= pixelsHeightRequest;
         if (isHeightDone) {
            return false;
         }
      }
      currentLineOffset++;
      if (currentLineOffset >= firstLineOffsetRequested + numberOfLinesActual) {
         return false;
      }
      return true;
   }

   private boolean isContinueOnCurrentLine() {
      intervalIndex++;
      IntIntervals intervalsOfLeaves = st.getIntervalsOfLeaves();
      if (intervalIndex >= intervalsOfLeaves.getSize()) {
         //TODO normally this should never occur if lenght is correct
         return false;
      } else {
         interval = intervalsOfLeaves.getInterval(intervalIndex);
         numCharsInThisStyleInterval = interval.getLen();
         numCharsToBeDrawnNext = Math.min(numCharsInThisStyleInterval, numCharsUndrawnLeftInTheLine);
         if (numCharsUndrawnLeftInTheLine <= 0) {
            return false;
         }
      }

      if (pixelsWidthRequest != 0) {
         boolean isWidthDone = pixelsWidthDrawnOnCurrentLine >= pixelsWidthRequest;
         if (isWidthDone) {
            return false;
         }
      }
      int lenLine = getNumCharactersInLine();
      boolean isLengthDone = numberOfCharDrawnOnCurrentLine >= lenLine;
      if (isLengthDone) {
         return false;
      }
      return true;
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

   public void setAbsoluteXY(boolean isAbsoluteXY) {
      this.isAbsoluteXY = isAbsoluteXY;
   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, StringerDraw.class, "@line5");
      toStringPrivate(dc);
      super.toString(dc.sup());
      dc.appendVarWithSpace("currentLineOffset", currentLineOffset);
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, StringerDraw.class);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());

   }

   private void toStringPrivate(Dctx dc) {

   }

   //#enddebug

}
