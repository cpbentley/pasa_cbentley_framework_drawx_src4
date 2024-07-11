/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.logging.IStringable;
import pasa.cbentley.core.src4.utils.CharUtils;
import pasa.cbentley.core.src4.utils.IntUtils;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IConfigDrawX;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;
import pasa.cbentley.framework.drawx.src4.string.interfaces.ITechStringDrw;
import pasa.cbentley.framework.drawx.src4.string.interfaces.ITechStringer;
import pasa.cbentley.framework.drawx.src4.utils.AnchorUtils;

/**
 * A {@link StringMetrics} instance belongs to a single {@link Stringer}. It computes and keeps track of the sizes and positions of each character for the {@link Stringer}.
 * <br>
 * <br>
 * The Stringer asks for  trims or breaks the String of the {@link Stringer} by computing computes pw and ph using the {@link StringFx}
 * <br>
 * <br>
 * @author Charles-Philip Bentley
 * @see Stringer
 */
public class StringMetrics extends ObjectDrw implements IStringable, ITechStringer {

   /**
    * Tracks word positions. A Word is a consecutive serie of alphanumerical characters.
    * <br>
    * <br>
    * <b>Array Structure : Triplets </b> <br>
    * <li>index[0] = last valid index (6). Number of lines is this value divided by 3
    * <li>index[1] = start index of 1st word
    * <li>index[2] = number of visible chars for that word
    * <li>index[3] = number of pixels consumed by visible characters that word
    * <br>
    * <br>
    * Relative to {@link Stringer#offsetChars}
    */
   int[]                  breaksWord;

   /**
    *  Textbreak constants set in constructor from the {@link IConfigDrawX#getLineBreakChars()}
    *  
    *  <p>
    *   How do deal with line breaks in a language such as arabic ?
    *  </p>
    */
   private final char[]   C_TEXTBREAKS;

   private int            charBiggestW;

   /**
    * Tell us about the char states.. if computed with is valid etc
    */
   int[]                  charFlags = new int[10];

   private int            charWidthMono;

   /**
    * Characters widths, computed during breaking.
    * <br>
    * Includes modifications made by {@link StringFx}.
    * <br>
    * TODO When using a Monospace font, this is not needed. unless we want to compute
    * <br>
    * What happens with special char Fx.. that might change their size individually
    * <br>
    * Special cases when some characters have zero width size like new lines ?
    */
   //int[]                  charWidths;

   /**
    * Char x positions relative to first char at 0, computed during breaking
    * <br>
    * Those values are used for caret positioning. All {@link StringFx} scoped to {@link ITechStringer#FX_SCOPE_1_CHAR}  artifacts use it.
    * <br>
    * Values depends on {@link StringerDraw} anchoring {@link Anchor}
    * <br>
    * For basic {@link Stringer} type, those values are computed on demand.
    * 
    */
   int[]                  charXs    = new int[10];

   /**
    * Char y positions relative to first char at 0.
    * <br>
    * An rotation animation will modify those values
    */
   int[]                  charYs    = new int[10];

   private int            lineBiggestH;

   private int            lineBiggestW;

   private LineStringer[] lines;

   /**
    * Relative x positions of lines. null when no line breaking.
    * <br>
    * <br>
    * Animation may work on those values.
    * <br>
    * <br>
    * 
    */
   int[]                  lineXs;

   /**
    * 
    */
   int[]                  lineYs;

   /**
    * Height of the {@link Stringer}
    * Used by {@link StringMetrics#getPrefHeight()}
    */
   private int            ph        = -1;

   /**
    * Width of the {@link Stringer}.
    * Used by {@link StringMetrics#getPrefWidth()}
    */
   private int            pw        = -1;

   private final Stringer stringer;

   /**
    * Initialize the {@link StringMetrics} with the controlling {@link Stringer}.
    * <br>
    * <br>
    * 
    * @param stringer
    */
   public StringMetrics(DrwCtx drc, Stringer stringer) {
      super(drc);
      this.stringer = stringer;
      C_TEXTBREAKS = drc.getConfigDrawX().getLineBreakChars();
   }

   public StringMetrics(DrwCtx drc, Stringer stringer, char newLine, char[] breaks) {
      super(drc);
      this.stringer = stringer;
      C_TEXTBREAKS = breaks;
   }


   private void checkStateLine() {
      if (lines == null) {
         meterString();
      }
   }

   /**
    * Once the string has been broken, the number of lines is known and character positions can be computed.
    * <br>
    * Postion depends on
    * <li> alignment TODO String locale when aligment is locale dependant.
    * <li> extra line spaces between and around
    * <li> function
    * 
    * <br>
    * <br>
    * By default computes for a TOP LEFT anchoring
    * TODO define in String figure
    * IDrw.FIG_TYPE_10_STRING
    * Define type of locale impact
    * IDrw StringF
    */
   private void computeCharPositions() {

      charXs = drc.getMem().ensureCapacity(charXs, stringer.lengthChars);
      charYs = drc.getMem().ensureCapacity(charYs, stringer.lengthChars);
      int dx = stringer.stringFx.fxLineExtraW; //relative to Stringer x coordinate
      int dy = 0;
      //align with respect to local
      //TODO anchor locale and opposite 

      int day = AnchorUtils.getYAlign(stringer.anchor, 0, stringer.areaH, getPrefHeight());
      int dax = AnchorUtils.getXAlign(stringer.anchor, 0, stringer.areaW, getPrefWidth());
      //single line
      for (int i = 0; i < stringer.lengthChars; i++) {
         charXs[i] = dax + dx;
         charYs[i] = day + dy;
         dx = getFXDxOffset(0, dx, i);
      }
      stringer.setFlagState(ITechStringer.STATE_06_CHAR_POSITIONS, true);
   }

   public int getCharHeight(int indexRelative) {
      int value = 0;
      StringFx fx = stringer.getCharFx(indexRelative);
      value = fx.font.getHeight();
      return value;
   }

   /**
    * Computes and stores the character pixel width at the given step, using Font and Fx. 
    * <br>
    * If already computed, return it.
    * <br>
    * {@link StringFx} may have special rules for given character index.
    * <br>
    * 
    * @param indexRelative relative to the {@link Stringer} offset
    * @return
    */
   public int getCharWidth(int indexRelative) {
      //we delegate this job to the line
      LineStringer line = this.getLineFromCharIndex(indexRelative);
      int offsetLine = line.getOffsetLineFromStringerOffset(indexRelative);
      
      int cw = line.getCharWidth(offsetLine);
      
      return cw;
   }
   
   public boolean isAllCharsSameWidth() {
      if(stringer.getSpaceTrimManager() == ITechStringer.SPACETRIM_2_JUSTIFIED) {
         return false;
      }
      //add here any other options that destroy the invariant of same widths for all non zero characters.
      return stringer.hasFlagState(ITechStringer.STATE_18_FULL_MONOSPACE);
   }

   /**
    * Compute char width as if it is displayed alone.
    * <br>
    * This method is key when computing string metrics. 
    * <p>
    * <b>PRE</b>: {@link StringFx} has been assigned.
    * </p>
    * <br>
    * 
    * <p>
    * Line Based {@link StringFx}:<br>
    * In case of width based string breaking, line based styles cannot be assigned until text is broken
    * The scope {@link ITechStringer#FX_SCOPE_4_LINE} is used
    * </p>
    * 
    * 
    * The <code>indexRelative</code> parameter selects the {@link StringFx} to be used.
    * <br>
    * <br>
    * When {@link Stringer} has {@link ITechStringer#STATE_01_CHAR_EFFECTS}
    * <br>
    * <br>
    * <p>
    *
    * In MonoSpace fonts, the character width never changes. So for a given layer of style, the same value
    * is computed once for all characters of this style group
    * 
    * </p>
    * @param step
    * @return
    */
   public int getCharWidthCompute(char c, int indexRelative) {
      int value = 0;
      StringFx fx = stringer.getCharFx(indexRelative);
      value = fx.getCharWidth(c);
      return value;
   }

   /**
    * Biggest char
    * @return
    */
   public int getCharWidthEtalon() {
      return charBiggestW;
   }

   /**
    * X offset for the start of the character at index relative to 0.
    * <br>
    * i.e. not counting the offset in the char array
    * <br>
    * <br>
    * @param indexRelative
    * @return integer relative to {@link Stringer#areaX}
    */
   public int getCharX(int indexRelative) {
      
      LineStringer line = getLineFromCharIndex(indexRelative);
      int offsetLine  = line.getOffsetLineFromStringerOffset(indexRelative);
      int x = line.getCharX(offsetLine);
      return x;
   }



   public int getCharY(int indexRelative) {
      LineStringer line = getLineFromCharIndex(indexRelative);
      return line.getY();
   }

   public int[] getCharYs() {
      if (!stringer.hasFlagState(ITechStringer.STATE_06_CHAR_POSITIONS)) {
         //ask to compute all
         computeCharPositions();
      }
      return charYs;
   }

   /**
    * @param lineCount
    * @param dx
    * @param startIndex
    * @return
    */
   private int getFXDxOffset(int lineCount, int dx, int startIndex) {
      return dx + getCharWidth(startIndex);
   }

   public IntUtils getIntUtils() {
      return drc.getUC().getIU();
   }

   public LineStringer getLine(int lineIndex) {
      //#debug
      checkStateLine();
      return lines[lineIndex];
   }

   public char[] getLineBreakChars() {
      return C_TEXTBREAKS;
   }

   /**
    * Returns the biggest line height of all the lines
    * 
    * @return
    */
   public int getLineHeight() {
      return lineBiggestH;
   }

   /**
    * What is the line height when different fxs are on the same line?
    * <br>
    * Anwser: the biggest line height of the fxs.
    * <br>
    * This value is used to draw background figures for the line.
    * <br>
    * @return
    */
   public int getLineHeight(int lineIndex) {
      //#debug
      checkStateLine();
      return lines[lineIndex].getPixelsH();
   }

   public LineStringer[] getLinesFromOffsetLen(int offset, int len) {
      return getLinesFromOffsets(offset, offset + len - 1);
   }

   public LineStringer[] getLinesFromOffsets(int offsetStart, int offsetEnd) {
      int lineFirst = getLineIndexFromCharIndex(offsetStart);
      int lineLast = getLineIndexFromCharIndex(offsetEnd);
      int numLines = lineLast - lineFirst + 1;
      LineStringer[] ar = new LineStringer[numLines];
      for (int i = 0; i < numLines; i++) {
         ar[i] = getLine(lineFirst + i);
      }
      return ar;
   }

   /**
    * 
    * @param indexRelative
    * @return -1 if index is not inside any lines
    */
   public int getLineIndexFromCharIndex(int indexRelative) {
      //#debug
      checkStateLine();
      for (int i = 0; i < lines.length; i++) {
         if (lines[i].isInside(indexRelative)) {
            return i;
         }
      }
      return -1;
   }

   public LineStringer getLineFromCharIndex(int indexRelative) {
      //#debug
      checkStateLine();
      for (int i = 0; i < lines.length; i++) {
         if (lines[i].isInside(indexRelative)) {
            lines[i].setLineID(i);
            return lines[i];
         }
      }
      return null;
   }

   public String getLineString(int lineIndex) {
      LineStringer line = lines[lineIndex];
      return line.getLineString();
   }

   /**
    * It is slightly different than {@link StringMetrics#getPrefWidthLine(int) }
    * 
    * @param caretIndex
    * @return
    */
   public int getLineWidth(int lineIndex) {
      //#debug
      checkStateLine();
      return lines[lineIndex].getPixelsW();
   }

   /**
    * The X position of the first line
    * @param lineIndex
    * @return
    * @throws NullPointerException when not computed
    * @throws ArrayIndexOutOfBoundsException
    */
   public int getLineX(int lineIndex) {
      //#debug
      checkStateLine();
      return lines[lineIndex].getX();
   }

   /**
    * The Y coordinate for the given line index.
    * @param indexLine
    * @return
    */
   public int getLineY(int lineIndex) {
      //#debug
      checkStateLine();
      return lines[lineIndex].getY();
   }

   /**
    * The number of lines.
    * 
    * If the {@link StringMetrics} has not been computed, assume number of lines is 1.
    * 
    * @return
    */
   public int getNumOfLines() {
      checkStateLine();
      return lines.length;
   }

   /**
    * Returns the default fx line height
    * <br>
    * <br>
    * 
    * @return
    */
   public int getPrefCharHeight() {
      return stringer.stringFx.font.getHeight();
   }

   /**
    * Returns the default fx 'm' width
    * @return
    */
   public int getPrefCharWidth() {
      return stringer.stringFx.font.getWidthWeigh();
   }

   /**
    * Compute the preferred height for the whole set of line(s).
    * <br>
    * <br>
    * 
    * @return
    */
   public int getPrefHeight() {
      //#debug
      checkStateLine();
      return ph;
   }

   /**
    * Compute preferred width or returned the cached value.
    * <br>
    * <br>
    * When several lines, return the maximum value
    * <br>
    * @return
    */
   public int getPrefWidth() {
      //#debug
      checkStateLine();
      return pw;
   }

   /**
    * 
    * @param offsetLeaf
    * @return
    */
   public int getShiftX(int offsetLeaf) {
      // TODO Auto-generated method stub
      return 0;
   }

   public int getShiftY(int offsetLeaf) {
      // TODO Auto-generated method stub
      return 0;
   }

   /**
    * Pixels consumed by letter since the start of the line
    * <br>
    * @param index
    * @return
    */
   public int getWidthConsumed(int index) {
      LineStringer line = getLineFromCharIndex(index);
      if(line == null) {
         throw new IllegalArgumentException();
      }
      int lineIndex = line.getOffsetLineFromStringerOffset(index);
      return line.getCharWidthConsumedUntil(lineIndex);
   }

   /**
    * 
    * @param c
    * @return
    */
   public boolean isCharBreak(char c) {
      return CharUtils.contains(C_TEXTBREAKS, c);
   }

   /**
    * what 
    */
   void meterString() {

      //check style
      if (!stringer.hasFlagState(STATE_19_FX_SETUP)) {
         throw new IllegalStateException("FxSetup was not called");
      }

      if (stringer.chars == null) {
         stringer.chars = new char[0];
      }

      LineAlgo lineAlgo = new LineAlgo(stringer);
      lineAlgo.init();
      lineAlgo.start();

      lines = lineAlgo.getLines();
      //we have all ours lines
      for (int i = 0; i < lines.length; i++) {
         lines[i].setIndex(i);
      }
      TextStats stats = lineAlgo.getStats();
      lineBiggestH = stats.getBiggestLineH();
      lineBiggestW = stats.getBiggestLineW();
      pw = lineBiggestW;
      ph = stats.getLinesTotalH();
      charWidthMono = stats.getSameCharWidthFactValue();
      charBiggestW = stats.getCharBiggestWidth();

      //align y coordinates
      if (stringer.anchor != null) {
         int dy = AnchorUtils.getYAlign(stringer.anchor, 0, stringer.areaH, ph);
         for (int i = 0; i < lines.length; i++) {
            LineStringer line = lines[i];
            int ny = line.getY() + dy;
            line.setY(ny);
         }
      }
   }

   public void reset() {
      pw = -1;
      ph = -1;
      charXs = drc.getMem().ensureCapacity(charXs, stringer.lengthChars);
      charYs = drc.getMem().ensureCapacity(charYs, stringer.lengthChars);
   }



   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, StringMetrics.class, 1161);
      dc.append(" pw=" + pw + " ph=" + ph);
      dc.appendVarWithNewLine("charXs", charXs, ",", true);
      dc.appendVarWithNewLine("charYs", charYs, ",", true);

      dc.nlLvlArray("lines", lines);
      //complete call might generate exception
      dc.appendVarWithSpace("pw", pw);
      dc.appendVarWithSpace("ph", ph);

      dc.nlLvl(stringer, "stringer");
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, "StringMetrics");
      dc.append(" pw=" + pw + " ph=" + ph);
   }
   //#enddebug

}
