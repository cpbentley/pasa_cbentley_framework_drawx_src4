package pasa.cbentley.framework.drawx.src4.string;

import java.util.Enumeration;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.structs.BufferObject;
import pasa.cbentley.core.src4.structs.IntInterval;
import pasa.cbentley.core.src4.structs.IntIntervals;
import pasa.cbentley.core.src4.utils.StringUtils;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigString;
import pasa.cbentley.framework.drawx.src4.string.interfaces.ITechStringer;
import pasa.cbentley.framework.drawx.src4.utils.AnchorUtils;

public class LineAlgo {
   private boolean      allSameFont;

   private boolean      allSameWidth;

   private int          biggestLineH;

   private int          biggestLineW;

   private int          charBiggestWidth       = 0;

   private int          dy;

   BufferObject         finalLines;

   private int          fontHeight;

   Enumeration          intervals;

   private boolean      isIgnoreNewLines;

   private boolean      isTestWidth;

   private boolean      isTestHeight;

   private int          maxLinesNum;

   private int          maxLineWidth;

   private LineStringer newLine;

   /**
    * 
    */
   private int          niceBreakOffset;

   private boolean      niceDiffFontHeights;

   /**
    * Records the line length at the last "nice" position. ie last space character
    */
   private int          niceLineLength;

   /**
    * Records the line width at the last "nice" position. ie last space character
    */
   private int          niceLineWidth;

   /**
    * Records the maximum line height at the last "nice" position. ie last space character
    */
   private int          niceMaxLineHeight;

   private int          lineNumofSpaces;

   /**
    * Flag telling us, that even if we have not a flagged font as monospace and/or different styles
    * 
    * all characters have indeed the same width. The {@link LineAlgo} will try to invalidate
    * this flag.
    */
   private boolean      sameCharWidthFact      = true;

   private int          sameCharWidthFactValue = 0;

   private Stringer     stringer;

   StringFx             style;

   IntInterval          styleInterval;

   private boolean      tempDiffFontHeights;

   private int          tempLineLength;

   /**
    * Last space width. Everytime the style changes, this value is updated
    */
   private int          spaceWidthLastStyle;

   private int          tempLineStartOffsetRelative;

   private int          tempLineWidth;

   private int          tempMaxLineHeight;

   private boolean      isShowHiddenChars;

   private int          linesTotalH;

   private boolean      isTrimArtifact;

   private boolean      isStop;

   private int          maxHeight;

   private LineStringer previousLine;

   public LineAlgo(Stringer stringer) {
      this.stringer = stringer;

   }

   private boolean isMaxLinesReached() {
      return (maxLinesNum != 0 && finalLines.getSize() >= maxLinesNum);
   }

   private void createNewLineANiceWordLastLine() {
      if (isMaxLinesReached()) {
         return;
      }
      //do the inverse because this is the last line.. so no wrap
      niceDiffFontHeights = tempDiffFontHeights;
      niceLineLength = tempLineLength;
      niceLineWidth = tempLineWidth;
      niceMaxLineHeight = tempMaxLineHeight;

      int lineStartingOffset = newLine.getOffset();

      lineStartingOffset = trimSpacesLeading(lineStartingOffset);
      trimSpacesTrailing(lineStartingOffset);

      newLine.setLen(niceLineLength);
      newLine.setPixelsH(niceMaxLineHeight);
      newLine.setPixelsW(niceLineWidth); //
      newLine.setHasDifferentFonts(niceDiffFontHeights);
      newLine.build();

      if (niceMaxLineHeight > biggestLineH) {
         biggestLineH = tempMaxLineHeight;
      }
      if (niceLineWidth > biggestLineW) {
         biggestLineW = tempLineWidth;
      }

      int dax = AnchorUtils.getXAlign(stringer.anchor, 0, stringer.areaW, niceLineWidth);
      newLine.setX(dax);

      createNewLineC();
   }

   private void createNewLineANiceWord() {
      //irrespective of the space trimming, we must compute the next line first before
      //modifying them
      int newTempLineLength = tempLineLength - niceLineLength;
      int newTempLineWidth = tempLineWidth - niceLineWidth;
      //only value that is known to be true right now
      int lineStartingOffset = newLine.getOffset();
      //remove leading actual spaces by shifting the line starting offset
      lineStartingOffset = trimSpacesLeading(lineStartingOffset);

      if (finalLines.getSize() + 1 == maxLinesNum) {
         //we are on the last line.. add
         if (isTrimArtifact) {
            int lineLastCharIndex = lineStartingOffset + niceLineLength - 1;
            newLine.charMapTo(lineLastCharIndex, '.');
            newLine.charMapTo(lineLastCharIndex - 1, '.');
            stringer.setState(ITechStringer.STATE_04_TRIMMED, true);
         }
      } else {
         //do not trim spaces on a trimmed line
         trimSpacesTrailing(lineStartingOffset);
      }

      newLine.setLen(niceLineLength);
      newLine.setPixelsH(niceMaxLineHeight);
      newLine.setPixelsW(niceLineWidth); //
      newLine.setHasDifferentFonts(niceDiffFontHeights);
      newLine.build();

      if (niceMaxLineHeight > biggestLineH) {
         biggestLineH = tempMaxLineHeight;
      }
      if (niceLineWidth > biggestLineW) {
         biggestLineW = tempLineWidth;
      }

      justify();

      int dax = AnchorUtils.getXAlign(stringer.anchor, 0, stringer.areaW, niceLineWidth);
      newLine.setX(dax);

      createNewLineC();

      resetLineStateNiceWord(newTempLineLength, newTempLineWidth);

   }

   private void resetLineStateNiceWord(int newTempLineLength, int newTempLineWidth) {
      lineNumofSpaces = 0;
      tempLineStartOffsetRelative = niceBreakOffset;
      tempLineLength = newTempLineLength;
      tempLineWidth = newTempLineWidth;
      tempDiffFontHeights = false;
      previousLine = newLine;
      newLine = new LineStringer(stringer);
      newLine.setOffset(tempLineStartOffsetRelative);
   }

   private void justify() {
      if (stringer.getSpaceTrimManager() == ITechStringer.SPACETRIM_2_JUSTIFIED) {
         //amount of pixels that have to be redistributed on this lines spaces
         int widthDifference = maxLineWidth - niceLineWidth;
         if (widthDifference != 0 && lineNumofSpaces != 0) {
            int pixelsToAdd = widthDifference / lineNumofSpaces;
            int leftover = widthDifference % lineNumofSpaces;
            newLine.enableCharWidths();
            newLine.setJustified(true);
            int len = newLine.getLen();
            //careful. we have to work on the visible chars of the lines.. so might use a char mapper
            for (int i = 0; i < len; i++) {
               char c = newLine.getChar(i);
               if (c == ' ') {
                  int add = pixelsToAdd;
                  if (leftover != 0) {
                     add++;
                     leftover--;
                  }
                  newLine.incrementCharWidth(i, add);
               }
            }
            //x must be zero because the line takes all the available space
            //mono is easier

            //first remove external space.. each word ends with a space.. last word starts with a space
            //if one word. do nothing
            //if 2+ word, take last one and add a space in front, then first one add a space trailing
            //then 3rd word trailing space, etc until the quote of space is exhausted
         }
      }
   }

   private void trimSpacesTrailing(int lineStartingOffset) {
      //now that we know the line width and length remove trailing spaces
      if (isRemoveSpaceFromLineExtremities()) {
         if (niceLineLength != 0) {
            int lineLastCharIndex = lineStartingOffset + niceLineLength - 1;
            char last = stringer.getCharAtRelative(lineLastCharIndex);
            int count = 0;
            while (last == ' ' && count < tempLineLength) {
               count++;
               int offsetRelative = lineLastCharIndex - count;
               niceLineWidth -= stringer.getMetrics().getCharWidth(offsetRelative);
               last = stringer.getCharAtRelative(offsetRelative);
            }
            //simply reducing the length is enough for the trailing line spaces
            niceLineLength -= count;
            lineNumofSpaces -= count;
         }
      }
   }

   private int trimSpacesLeading(int lineStartingOffset) {
      if (isRemoveSpaceFromLineExtremities()) {
         int lineFirstCharIndex = lineStartingOffset;
         char first = stringer.getCharAtRelative(lineFirstCharIndex);
         int count = 0;
         while (first == ' ' && count < tempLineLength) {
            count++;
            first = stringer.getCharAtRelative(lineFirstCharIndex + count);
            niceLineWidth -= stringer.getMetrics().getCharWidth(lineFirstCharIndex + count);
         }
         lineStartingOffset = lineFirstCharIndex + count;
         newLine.setOffset(lineStartingOffset);
         niceLineLength -= count;
         lineNumofSpaces -= count;
      }
      return lineStartingOffset;
   }

   private void createNewLineB() {

      //start a new line
      newLine.setLen(tempLineLength);
      newLine.setPixelsH(tempMaxLineHeight);
      newLine.setPixelsW(tempLineWidth);
      newLine.setHasDifferentFonts(tempDiffFontHeights);
      newLine.build();
      if (tempMaxLineHeight > biggestLineH) {
         biggestLineH = tempMaxLineHeight;
      }
      if (tempLineWidth > biggestLineW) {
         biggestLineW = tempLineWidth;
      }
      justify();
      int dax = AnchorUtils.getXAlign(stringer.anchor, 0, stringer.areaW, tempLineWidth);
      newLine.setX(dax);
   }

   private void createNewLineTrimmed() {
      //start a new line
      newLine.setLen(tempLineLength);
      newLine.setPixelsH(tempMaxLineHeight);
      newLine.setPixelsW(tempLineWidth);
      newLine.setHasDifferentFonts(tempDiffFontHeights);

      if (isTrimArtifact) {
         int lineStartingOffset = newLine.getOffset();
         int lineLastCharIndex = lineStartingOffset + tempLineLength - 1;
         newLine.charMapTo(lineLastCharIndex, '.');
         newLine.charMapTo(lineLastCharIndex - 1, '.');
         stringer.setState(ITechStringer.STATE_04_TRIMMED, true);
      }

      newLine.build();
      if (tempMaxLineHeight > biggestLineH) {
         biggestLineH = tempMaxLineHeight;
      }
      if (tempLineWidth > biggestLineW) {
         biggestLineW = tempLineWidth;
      }
      int dax = AnchorUtils.getXAlign(stringer.anchor, 0, stringer.areaW, tempLineWidth);
      newLine.setX(dax);

   }

   private void createNewLineC() {
      newLine.setY(dy);
      dy += tempMaxLineHeight;
      linesTotalH += tempMaxLineHeight;
      if (isTestHeight && linesTotalH > maxHeight) {
         isStop = true;
         if (isTrimArtifact) {
            if (!stringer.hasState(ITechStringer.STATE_04_TRIMMED)) {
               //take previous and add trim cue
               int lineLastCharIndex = previousLine.getOffsetLast();
               previousLine.charMapTo(lineLastCharIndex, '.');
               previousLine.charMapTo(lineLastCharIndex - 1, '.');
               //!!! we need to rebuild the line
               previousLine.build();
               stringer.setState(ITechStringer.STATE_04_TRIMMED, true);
            }
         }
      } else {
         finalLines.add(newLine);
      }
   }

   public void createNewLineChar(int tentativeNewLineOffset) {
      createNewLineB();
      createNewLineC();
      resetLineStats(tentativeNewLineOffset);
   }

   public void createNewLineLast() {
      if (isMaxLinesReached()) {
         return;
      }
      int wordwrap = stringer.getWordwrap();
      if (wordwrap == ITechStringer.WORDWRAP_0_NONE) {
         createNewLineB();
         createNewLineC();
      } else if (wordwrap == ITechStringer.WORDWRAP_2_NICE_WORD || wordwrap == ITechStringer.WORDWRAP_3_NICE_HYPHENATION) {
         createNewLineANiceWordLastLine();
      } else if (wordwrap == ITechStringer.WORDWRAP_1_ANYWHERE) {
         createNewLineB();
         createNewLineC();
      } else {
         throw new IllegalArgumentException();
      }
   }

   private void createNewLineTooMuchWidth(int tentativeNewLineOffset, int lineWidthNew) {

      int wordwrap = stringer.getWordwrap();
      if (wordwrap == ITechStringer.WORDWRAP_0_NONE) {
         //trim too much width
         createNewLineTrimmed();
         //stop on this line
         createNewLineC();

         if (stringer.getNewLineManager() == ITechStringer.NEWLINE_MANAGER_0_IGNORE) {
            //this is the end.. finish algo
            isStop = true;
         } else {
            //continue until maybe we find a newline to actually start the next line 
            isStop = true;
         }
      } else if (wordwrap == ITechStringer.WORDWRAP_2_NICE_WORD || wordwrap == ITechStringer.WORDWRAP_3_NICE_HYPHENATION) {
         createNewLineANiceWord();
      } else if (wordwrap == ITechStringer.WORDWRAP_1_ANYWHERE) {
         createNewLineB();
         createNewLineC();
         resetLineStats(tentativeNewLineOffset);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int getBiggestLineH() {
      return biggestLineH;
   }

   public int getBiggestLineW() {
      return biggestLineW;
   }

   public int getLinesTotalH() {
      return linesTotalH;
   }

   public int getCharBiggestWidth() {
      return charBiggestWidth;
   }

   public LineStringer[] getLines() {
      LineStringer[] lines = new LineStringer[finalLines.getSize()];
      finalLines.appendBufferToArrayAt(lines, 0);
      return lines;
   }

   public int getSameCharWidthFactValue() {
      return sameCharWidthFactValue;
   }

   /**
    * Based on Stringer state, init the intervals and stuff.
    * 
    * If Stringer is empty or without style
    */
   public void init() {

      ByteObject textFigure = stringer.getTextFigure();

      IntIntervals leaves = stringer.getIntervalsOfLeaves();
      this.intervals = leaves.getIntervalEnumeration();

      finalLines = new BufferObject(stringer.getUC());

      maxLineWidth = stringer.getBreakW();
      if (maxLineWidth <= 0) {
         maxLineWidth = Integer.MAX_VALUE; //ignore
         isTestWidth = false;
      } else {
         int wordwrap = textFigure.get1(IBOFigString.FIG_STRING_OFFSET_07_WRAP_WIDTH1);
         if(wordwrap == ITechStringer.WORDWRAP_0_NONE) {
            isTestWidth = false;
         } else {
            isTestWidth = true;
         }
      }
      maxHeight = stringer.getBreakH();
      if (maxHeight <= 0) {
         maxHeight = Integer.MAX_VALUE; //ignore
         isTestHeight = false;
      } else {
         int linewrap = textFigure.get1(IBOFigString.FIG_STRING_OFFSET_08_WRAP_HEIGHT1);
         if(linewrap == ITechStringer.LINEWRAP_0_NONE) {
            isTestHeight = false;
         } else {
            isTestHeight = true;
         }
      }

      isIgnoreNewLines = stringer.getNewLineManager() == ITechStringer.NEWLINE_MANAGER_0_IGNORE;
      if (stringer.getWordwrap() >= ITechStringer.WORDWRAP_2_NICE_WORD) {
         isIgnoreNewLines = false;
      }
      maxLinesNum = stringer.getBreakMaxLines();
      isTrimArtifact = textFigure.hasFlag(IBOFigString.FIG_STRING_OFFSET_01_FLAG, IBOFigString.FIG_STRING_FLAG_3_TRIM_ARTIFACT);
      tempMaxLineHeight = -1;
      tempDiffFontHeights = false;
      tempLineWidth = 0;
      allSameWidth = stringer.hasState(ITechStringer.STATE_18_FULL_MONOSPACE);
      allSameFont = !stringer.hasState(ITechStringer.STATE_11_DIFFERENT_FONTS);

      isShowHiddenChars = stringer.isShowHiddenChars();
      tempLineStartOffsetRelative = 0;
      tempLineLength = 0;
      newLine = new LineStringer(stringer);

   }

   public void lineCheck() {
      fontHeight = style.getFontHeight();
      if (tempMaxLineHeight == -1) {
         tempMaxLineHeight = fontHeight;
      } else {
         if (fontHeight != tempMaxLineHeight) {
            tempDiffFontHeights = true;
         }
         if (tempMaxLineHeight < fontHeight) {
            tempMaxLineHeight = fontHeight;
         }
      }
   }

   /**
    * Fast.. does not verify break width
    * 
    * <li> {@link ITechStringer#WORDWRAP_0_NONE}
    * <li> {@link ITechStringer#NEWLINE_MANAGER_1_WORK}
    */
   private void loopFastCheckNewLinesHiddenChars() {
      int offset = styleInterval.getOffset();
      int len = styleInterval.getLen();
      for (int i = offset; i < len; i++) {
         //are we still in the current line
         char c = stringer.getCharAtRelative(i);
         if (c == StringUtils.NEW_LINE) {
            regularCharNewLine(i);
         } else if (c == StringUtils.NEW_LINE_CARRIAGE_RETURN) {
            regularCharCarriage(i);
         } else if (c == StringUtils.FORM_FEED) {
            c = regularCharFormFeed(i, c);
         } else if (c == StringUtils.TAB) {
            c = regularCharTab(i, c);
         } else if (c == StringUtils.ENGLISH_SPACE) {
            c = regularCharSpaceNoW(offset, i, c);
         } else {
         }
         regularNoWidthCheck(i, c);
      }
      createNewLineLast();
   }

   private void loopFast() {
      int offset = styleInterval.getOffset();
      int len = styleInterval.getLen();
      for (int i = offset; i < offset + len; i++) {
         //are we still in the current line
         char c = stringer.getCharAtRelative(i);
         if (isShowHiddenChars) {
            switch (c) {
               case StringUtils.NEW_LINE:
                  c = StringUtils.LINE_BREAK_RETURN;
                  newLine.charMapTo(i, c);
                  break;
               case StringUtils.NEW_LINE_CARRIAGE_RETURN:
                  c = StringUtils.SYMBOL_RETURN;
                  newLine.charMapTo(i, c);
                  break;
               case StringUtils.TAB:
                  c = StringUtils.ARROW_RIGHT;
                  newLine.charMapTo(i, c);
                  break;
               case StringUtils.FORM_FEED:
                  c = StringUtils.GENDER_FEMALE;
                  newLine.charMapTo(i, c);
                  break;
               case StringUtils.ENGLISH_SPACE:
                  c = StringUtils.INTER_PUNCT;
                  newLine.charMapTo(i, c);
                  break;
               default:
                  break;
            }
         }
         regular(i, c);
      }
   }

   private int                 tabMaxNum       = 0;

   private int                 tabLineCounter  = 0;

   private int                 tabCurrentTab   = -1;

   /**
    * An end markup has to be on the same line
    */
   private char                markupStartChar = '〖';

   private char                markupEndChar   = '〗';

   private TabColumnStringer[] tabCols;

   private char                lastChar;

   /**
    * TODO how to deal with hidden characters.. when selecting a visible char.. index visible is not equal to
    * index of chars
    * 
    * {@link CharMap} in {@link LineStringer}
    */
   private void loopFull() {
      //this offset is stringer srcChar relative
      int offset = styleInterval.getOffset();
      int len = styleInterval.getLen();
      for (int i = offset; i < offset + len; i++) {
         //are we still in the current line
         lastChar = stringer.getCharAtRelative(i);
         //check only newline chars if requested.. can be turned off for perf reasons
         if (lastChar == markupStartChar) {
            //check markup
         } else if (lastChar == StringUtils.NEW_LINE) {
            //TODO. is the index included in the newLine?
            regularCharNewLine(i);
         } else if (lastChar == StringUtils.NEW_LINE_CARRIAGE_RETURN) {
            regularCharCarriage(i);
         } else if (lastChar == StringUtils.FORM_FEED) {
            lastChar = regularCharFormFeed(i, lastChar);
         } else {
            regularNoNewLinesChar(offset, i, lastChar);
         }
         if (isMaxLinesReached()) {
            return;
         }
         if (isStop) {
            return;
         }
      }
   }

   private char regularCharFormFeed(int i, char c) {
      if (isShowHiddenChars) {
         newLine.charMapTo(i, StringUtils.GENDER_FEMALE);
         c = StringUtils.GENDER_FEMALE;
      }
      createNewLineChar(i + 1);
      //create as many new lines for a page break
      int numLinesPerPage = stringer.getNumLinesPerPage();
      int addPages = numLinesPerPage - (finalLines.getSize() % numLinesPerPage);
      for (int j = 0; j < addPages; j++) {
         //non existing newline characters.. 
         //each line has a zero length
         createNewLineChar(i + 1);
      }
      return c;
   }

   private void regularCharCarriage(int i) {
      if (isShowHiddenChars) {
         newLine.charMapTo(i, StringUtils.SYMBOL_RETURN);
         newLine.charMapTo(i + 1, StringUtils.SYMBOL_RETURN);
      }
      createNewLineChar(i + 2);
      newLine.setRealModelLine(true);
      //TODO check if well formed with \n following
   }

   private void regularCharNewLine(int i) {
      if (isShowHiddenChars) {
         newLine.charMapTo(i, StringUtils.LINE_BREAK_RETURN);
      }
      createNewLineChar(i + 1);
      //next line is indeed created by a 
      newLine.setRealModelLine(true);
   }

   /**
    * Deal with c knowing it is not a characters generating any new line business
    * 
    * PRE: c is not a new line char
    * @param offsetStartInterval
    * @param srcOffsetRel
    * @param c
    */
   private void regularNoNewLinesChar(int offsetStartInterval, int srcOffsetRel, char c) {
      if (c == StringUtils.TAB) {
         c = regularCharTab(srcOffsetRel, c);
      }
      if (c == StringUtils.ENGLISH_SPACE) {
         c = regularCharSpace(offsetStartInterval, srcOffsetRel, c);
      }

      regular(srcOffsetRel, c);
   }

   private char regularCharSpaceNoW(int offsetStartInterval, int srcOffsetRel, char c) {
      if (isShowHiddenChars) {
         c = StringUtils.INTER_PUNCT;
         newLine.charMapTo(srcOffsetRel, StringUtils.INTER_PUNCT);
      }
      lineNumofSpaces++;
      return c;
   }

   private char regularCharSpace(int offsetStartInterval, int srcOffsetRel, char c) {
      if (isShowHiddenChars) {
         c = StringUtils.INTER_PUNCT;
         newLine.charMapTo(srcOffsetRel, StringUtils.INTER_PUNCT);
      }
      lineNumofSpaces++;
      //
      niceBreakOffset = srcOffsetRel;
      niceLineWidth = tempLineWidth;
      niceLineLength = tempLineLength;
      niceDiffFontHeights = tempDiffFontHeights;
      niceMaxLineHeight = tempMaxLineHeight;
      return c;
   }

   private boolean isRemoveSpaceFromLineExtremities() {
      return stringer.getSpaceTrimManager() != ITechStringer.SPACETRIM_0_NONE;
   }

   /**
    * Tab
    * {@link ITechStringer#TAB_MANAGER_0_SINGLE_SPACE}
    * 
    * @param srcOffsetRel
    * @param c
    * @return
    */
   private char regularCharTab(int srcOffsetRel, char c) {
      if (isShowHiddenChars) {
         c = StringUtils.ARROW_RIGHT;
         newLine.charMapTo(srcOffsetRel, StringUtils.ARROW_RIGHT);
      }
      int tabManager = stringer.getTabManager();
      if (tabManager == ITechStringer.TAB_MANAGER_2_COLUMN) {
         if (tabCols == null) {
            tabCols = new TabColumnStringer[5];
            tabCols[0] = new TabColumnStringer(stringer);
            tabLineCounter++;
         }
         if (tabCols[tabLineCounter] == null) {
            tabCols[tabLineCounter] = new TabColumnStringer(stringer);
         }

      } else if (tabManager == ITechStringer.TAB_MANAGER_0_SINGLE_SPACE) {
         c = ' ';
         //enable charmap and replace on this line
         newLine.charMapTo(srcOffsetRel, c);
      } else {
         //ignored
         newLine.charMapTo(srcOffsetRel, StringUtils.NULL_CHAR);
      }
      //option 2 replace it with X num of spaces
      return c;
   }

   /**
    * Iterate over current styling
    */
   public void loopStart() {

      if (isIgnoreNewLines) {
         loopFast();
      } else {
         loopFull();
      }

   }

   private void regularNoWidthCheck(int i, char c) {
      //manage the width business
      int cw = style.getCharWidth(c);
      if (cw == 0) {
         stringer.setState(ITechStringer.STATE_21_ZERO_WIDTH_CHARS, true);
         sameCharWidthFact = false;
      }
      if (allSameWidth) {
         //we know that all characters have the same width. set it as a global variable.. 
         sameCharWidthFactValue = cw;
         charBiggestWidth = cw;
      } else {
         //each char have different width so use the array
         stringer.getMetrics().charWidths[i] = cw;
         //check if they are the same width anyways in case
         if (cw > getCharBiggestWidth()) {
            charBiggestWidth = cw;
         }
         if (sameCharWidthFact) {
            if (getSameCharWidthFactValue() == 0) {
               sameCharWidthFactValue = cw;
            } else {
               if (cw != getSameCharWidthFactValue()) {
                  sameCharWidthFact = false;
               }
            }
         }
      }
      //keep working on the same line
      tempLineWidth += cw;
      tempLineLength++;
   }

   private void regular(int i, char c) {
      //manage the width business
      int cw = style.getCharWidth(c);
      if (cw == 0) {
         stringer.setState(ITechStringer.STATE_21_ZERO_WIDTH_CHARS, true);
         sameCharWidthFact = false;
      }
      if (allSameWidth) {
         //we know that all characters have the same width. set it as a global variable.. 
         sameCharWidthFactValue = cw;
         charBiggestWidth = cw;
      } else {
         //each char have different width so use the array
         stringer.getMetrics().charWidths[i] = cw;
         //check if they are the same width anyways in case
         if (cw > getCharBiggestWidth()) {
            charBiggestWidth = cw;
         }
         if (sameCharWidthFact) {
            if (getSameCharWidthFactValue() == 0) {
               sameCharWidthFactValue = cw;
            } else {
               if (cw != getSameCharWidthFactValue()) {
                  sameCharWidthFact = false;
               }
            }
         }
      }
      int lineWidthNew = tempLineWidth + cw;

      if (isTestWidth && lineWidthNew > maxLineWidth) {
         //put char on a new line
         createNewLineTooMuchWidth(i, lineWidthNew);
      }
      //keep working on the same line
      tempLineWidth += cw;
      tempLineLength++;
   }

   private void resetLineStats(int index) {
      lineNumofSpaces = 0;
      tempLineStartOffsetRelative = index;
      tempLineLength = 0;
      tempDiffFontHeights = false;
      tempLineWidth = 0;
      newLine = new LineStringer(stringer);
      newLine.setOffset(tempLineStartOffsetRelative);
   }

   public void start() {

      if (stringer.lengthChars == 0) {
      } else {
         while (intervals.hasMoreElements()) {
            stepNext();
            lineCheck();
            loopStart();
         }
      }

      //we have our lines

      //case 1 when no chars. we have a single empty line
      //case 2 when it ends with \n. create the same scneario with an "fictive" line 
      if (stringer.lengthChars == 0 || lastChar == StringUtils.NEW_LINE) {
         createNewLineEmptyFictive();
      } else {
         //finish algo by making sure at least 1 line is created
         createNewLineLast();
      }
   }

   void createNewLineEmptyFictive() {
      LineStringer line = new LineStringer(stringer);
      line.setOffset(tempLineStartOffsetRelative);
      line.setLen(0);
      line.setFictiveLine(true);
      int fh = stringer.stringFx.getFontHeight();
      biggestLineH = fh;
      linesTotalH = fh;
      line.setPixelsH(fh);
      finalLines.add(line);
   }

   public void stepNext() {
      styleInterval = (IntInterval) intervals.nextElement();
      style = ((StringFxLeaf) styleInterval.getPayload()).getFx();
   }
}
