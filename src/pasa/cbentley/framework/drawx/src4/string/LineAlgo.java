package pasa.cbentley.framework.drawx.src4.string;

import java.util.Enumeration;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.structs.BufferObject;
import pasa.cbentley.core.src4.structs.IntInterval;
import pasa.cbentley.core.src4.structs.IntIntervals;
import pasa.cbentley.core.src4.utils.StringUtils;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;
import pasa.cbentley.framework.drawx.src4.string.interfaces.ITechStringer;
import pasa.cbentley.framework.drawx.src4.utils.AnchorUtils;

/**
 * LineAlgo tracks computations for {@link Stringer} when formatting text.
 * 
 * @author Charles Bentley
 *
 */
public class LineAlgo extends ObjectDrw {

   private CharAlgo            charCurrent;

   private BufferObject        charsForNextLine;

   private int                 dy;

   BufferObject                finalLines;

   private int                 fontHeight;

   Enumeration                 intervals;

   private boolean             isShowHiddenChars;

   private boolean             isStop;

   private boolean             isTestWidth;

   private boolean             isTrimArtifact;

   private char                lastChar;

   private int                 lastTabOffsetEnd;

   private LineStringer        lineCurrent;

   private LineStringer        linePrevious;

   private char                markupEndChar   = '〗';

   /**
    * An end markup has to be on the same line
    */
   private char                markupStartChar = '〖';

   private int                 maxHeight;

   private int                 maxLinesNum;

   private int                 maxLineWidth;

   private int                 numTabsInLine;

   private TextStats           stats;

   private Stringer            stringer;

   /**
    * Style of current interval in algo
    */
   StringFx                    style;

   IntInterval                 styleInterval;

   private TabColumnStringer[] tabCols;

   private int                 tabLineCounter  = 0;

   public LineAlgo(Stringer stringer) {
      super(stringer.getDRC());
      this.stringer = stringer;

   }

   private void buildLineNormal() {
      int lineMaxH = stats.getLineMaxH();
      boolean hasDiffFontHeights = stats.hasLineDiffFontHeights();
      lineCurrent.setPixelsH(lineMaxH);
      lineCurrent.setHasDifferentFonts(hasDiffFontHeights);

      justify();

      stats.processLine(lineCurrent);

      //algin on x coordiante
      int walign = lineCurrent.getPixelsW();
      ByteObject anchor = stringer.anchor;
      int dax = AnchorUtils.getXAlign(anchor, 0, stringer.areaW, walign);
      lineCurrent.setX(dax);
      //do y coordinate
      lineCurrent.setY(dy);
      dy += lineMaxH;

      if (stats.isTrimmedH()) {
         //our line exceed.. so we trim previous line and do not add current to collection.
         isStop = true;
         doTrimHPreviousLine();
      } else {
         doTrimMaxLine();
         lineCurrent.setLineID(finalLines.getSize());
         finalLines.add(lineCurrent);

      }

      lineCurrent.build();

      //
      linePrevious = lineCurrent;

      LineStringer nextLine = new LineStringer(stringer);
      if (charsForNextLine != null && !charsForNextLine.isEmpty()) {
         CharAlgo ca = (CharAlgo) charsForNextLine.removeFirst();
         boolean isSpace = ca.getC() == StringUtils.ENGLISH_SPACE;
         if (isSpace && stringer.getFormatWordWrap() == ITechStringer.WORDWRAP_2_NICE_WORD) {

         } else {
            nextLine.addChar(ca);
            if (isSpace) {
               nextLine.incrementNumOfSpaces(1);
            }
         }

         //nl.setOffset(offset);
         while ((ca = (CharAlgo) charsForNextLine.removeFirst()) != null) {
            nextLine.addChar(ca);
         }
      }

      stats.resetLineStats(style.getFontHeight());
      charsForNextLine = null;
      lineCurrent = nextLine;
      lineLengthAtPrevioustab = 0;

   }

   private void buildLineTooMuchWidth() {

      int wordwrap = stringer.getWordwrap();
      if (wordwrap == ITechStringer.WORDWRAP_0_NONE) {
         //trim too much width
         doWrapLineTrim();

         if (stringer.getDirectiveNewLine() == ITechStringer.SPECIALS_NEWLINE_0_IGNORED) {
            //this is the end.. finish algo
            isStop = true;
         } else {
            //TODO continue until maybe we find a newline to actually start the next line 
            isStop = true;
         }
      } else if (wordwrap == ITechStringer.WORDWRAP_1_ANYWHERE) {
         doWrapLineAnywhere();
      } else if (wordwrap == ITechStringer.WORDWRAP_2_NICE_WORD) {
         doWrapLineNiceWord();
      } else if (wordwrap == ITechStringer.WORDWRAP_3_NICE_HYPHENATION) {
         doWrapLineHyphen();
      } else {
         throw new IllegalArgumentException();
      }

      buildLineNormal();
   }

   /**
    * 
    */
   void createNewLineEmptyFictive() {
      LineStringer line = new LineStringer(stringer);
      int offset = 0;
      if (linePrevious != null) {
         offset = linePrevious.getOffsetStringerLastChar() + 1;
      }
      line.setOffset(offset);
      line.setFictiveLine(true);

      int fh = stringer.stringFx.getFontHeight();
      line.setPixelsH(fh);

      stats.processLine(line);
      finalLines.add(line);
   }

   private void doSpecialIgnore() {
      lineCurrent.addCharIgnore();
   }

   private void doSpecialJava(String str) {
      //remove current newline and 

      lineCurrent.addCharJavaEscaped(str, style);

   }

   private void doSpecialSpaceFor(char c) {
      char rc = ' ';
      if (isShowHiddenChars) {
         rc = c;
      }
      int charWidth = style.getCharWidth(' ');
      lineCurrent.addCharSpaceSpecial(rc, charWidth);
      
      regular(charCurrent);
   }

   private void doTrimHPreviousLine() {
      if (isTrimArtifact) {
         //if (!stringer.hasState(ITechStringer.STATE_04_TRIMMED)) {
         if (linePrevious != null) {
            //take previous and add trim cue
            int lineLastCharIndex = linePrevious.getOffsetStringerLastChar();
            linePrevious.charMapTo(lineLastCharIndex, '.');
            linePrevious.charMapTo(lineLastCharIndex - 1, '.');
            //!!! we need to rebuild the line
            linePrevious.buildMap();
            stringer.setFlagState(ITechStringer.STATE_04_TRIMMED, true);
         }
         // }
      }
   }

   private void doTrimMaxLine() {
      if (finalLines.getSize() + 1 == maxLinesNum) {
         //we are on the last line.. add
         if (isTrimArtifact) {
            int lineLastCharIndex = lineCurrent.getOffsetStringerLastChar();
            lineCurrent.charMapTo(lineLastCharIndex, '.');
            lineCurrent.charMapTo(lineLastCharIndex - 1, '.');
            stringer.setFlagState(ITechStringer.STATE_04_TRIMMED, true);
         }
         isStop = true;
      }
   }

   private void doWrapLineAnywhere() {
      //simply removes last char
      CharAlgo ca = lineCurrent.removeLastChar();
      charsForNextLine = new BufferObject(getUC());
      charsForNextLine.add(ca);
      if (ca.getC() == StringUtils.ENGLISH_SPACE) {
         lineCurrent.incrementNumOfSpaces(-1);
      }
   }

   private void doWrapLineHyphen() {
      //remove and add a new char '-'
      int ol = lineCurrent.getOffsetStringerLastChar();
      charsForNextLine = lineCurrent.deleteCharsFrom(ol);
      //add a fictive dash character

      lineCurrent.addCharFictive(ol, '-');
   }

   private void doWrapLineNiceWord() {

      //in this mode by construction, there is no.. do we want to remove leading spaces on the first line ?
      BuildLineData builder = lineCurrent.getBuilder();

      int sp = builder.getLastSpaceIndex();

      if (sp > 0) {
         charsForNextLine = lineCurrent.deleteCharsFrom(sp);

         //we remove one space
         lineCurrent.incrementNumOfSpaces(-1);
         lineCurrent.setSpaceOut();

         if (isShowHiddenChars) {
            //insert a new char that is etheral. it does not impact line breaking
            CharAlgo ca = (CharAlgo) charsForNextLine.getFirst();
            lineCurrent.addCharEther(ca);
         }
      }

   }

   private void doWrapLineTrim() {
      //start a new line

      if (isTrimArtifact) {
         int lineStartingOffset = lineCurrent.getOffset();
         int lineLastCharIndex = lineStartingOffset + lineCurrent.getLengthInStringer() - 1;
         lineCurrent.charMapTo(lineLastCharIndex, '.');
         lineCurrent.charMapTo(lineLastCharIndex - 1, '.');
         stringer.setFlagState(ITechStringer.STATE_04_TRIMMED, true);
      }

   }

   public LineStringer[] getLines() {
      LineStringer[] lines = new LineStringer[finalLines.getSize()];
      finalLines.appendBufferToArrayAt(lines, 0);
      return lines;
   }

   public TextStats getStats() {
      return stats;
   }

   /**
    * Based on Stringer state, init the intervals and stuff.
    * 
    * If Stringer is empty or without style
    */
   public void init() {

      stats = new TextStats(drc);

      IntIntervals leaves = stringer.getIntervalsOfLeaves();
      this.intervals = leaves.getIntervalEnumeration();

      finalLines = new BufferObject(stringer.getUC());

      maxLineWidth = stringer.getBreakW();
      if (maxLineWidth <= 0) {
         maxLineWidth = Integer.MAX_VALUE; //ignore
         isTestWidth = false;
      } else {
         int wordwrap = stringer.getFormatWordWrap();
         if (wordwrap == ITechStringer.WORDWRAP_0_NONE) {
            isTestWidth = false;
         } else {
            isTestWidth = true;
         }
      }

      boolean isTestHeight = false;
      maxHeight = stringer.getBreakH();
      if (maxHeight <= 0) {
         maxHeight = Integer.MAX_VALUE; //ignore
         isTestHeight = false;
      } else {
         int linewrap = stringer.getFormatLineWrap();
         if (linewrap == ITechStringer.LINEWRAP_0_NONE) {
            isTestHeight = false;
         } else {
            isTestHeight = true;
         }
      }
      stats.setTestHeight(isTestHeight);
      stats.setMaxHeight(maxHeight);

      //      isIgnoreNewLines = stringer.getNewLineManager() == ITechStringer.SPECIALS_NEWLINE_0_IGNORE;
      //      if (stringer.getWordwrap() >= ITechStringer.WORDWRAP_2_NICE_WORD) {
      //         isIgnoreNewLines = false;
      //      }
      maxLinesNum = stringer.getBreakMaxLines();
      isTrimArtifact = stringer.isTrimArtifacts();

      isShowHiddenChars = stringer.isShowHiddenChars();
      lineCurrent = new LineStringer(stringer);
   }

   private boolean isJustifiedText() {
      return stringer.getSpaceTrimManager() == ITechStringer.SPACETRIM_2_JUSTIFIED && maxLineWidth != Integer.MAX_VALUE;
   }

   private boolean isRemoveSpaceFromLineExtremities() {
      return stringer.getSpaceTrimManager() != ITechStringer.SPACETRIM_0_NONE;
   }

   /**
    * Do we justify after the build ?
    */
   private void justify() {
      if (isJustifiedText()) {
         //amount of pixels that have to be redistributed on this lines spaces
         int widthDifference = maxLineWidth - lineCurrent.getPixelsW();
         int lineNumOfSpaces = lineCurrent.getNumOfSpaces();
         if (widthDifference != 0 && lineNumOfSpaces != 0) {
            int pixelsToAdd = widthDifference / lineNumOfSpaces;
            int leftover = widthDifference % lineNumOfSpaces;
            lineCurrent.enableCharWidths();
            lineCurrent.setJustified(true);
            int len = lineCurrent.getLengthInStringer();
            //careful. we have to work on the visible chars of the lines.. so might use a char mapper
            for (int i = 0; i < len; i++) {
               char c = lineCurrent.getCharVisible(i);
               if (c == ' ') {
                  int add = pixelsToAdd;
                  if (leftover != 0) {
                     add++;
                     leftover--;
                  }
                  lineCurrent.incrementCharWidth(i, add);
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

   //   public void lineCheckHeight() {
   //      fontHeight = style.getFontHeight();
   //      if (tempMaxLineHeight == -1) {
   //         tempMaxLineHeight = fontHeight;
   //      } else {
   //         if (fontHeight != tempMaxLineHeight) {
   //            tempDiffFontHeights = true;
   //         }
   //         if (tempMaxLineHeight < fontHeight) {
   //            tempMaxLineHeight = fontHeight;
   //         }
   //      }
   //   }

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
      for (int srcOffsetRelative = offset; srcOffsetRelative < offset + len; srcOffsetRelative++) {
         //are we still in the current line
         char cc = stringer.getCharSourceAtRelative(srcOffsetRelative);

         loopWorkOnCharacter(srcOffsetRelative, cc);

         if (isStop) {
            return;
         }
      }
   }

   /**
    * Iterate over current styling
    */
   public void loopStartCurrentInterval() {
      loopFull();
   }

   private void loopWorkOnCharacter(int srcOffsetRelative, char cc) {
      this.lastChar = cc;
      charCurrent = new CharAlgo(drc);
      charCurrent.setC(cc);

      //in all cases. we need its width
      int cw = style.getCharWidth(cc);
      charCurrent.setOffsetStringer(srcOffsetRelative);
      charCurrent.setWidth(cw);
      charCurrent.setHeight(fontHeight);

      stats.processChar(charCurrent);

      //invariant. we add the char to the line.
      //it will be removed if necessary
      lineCurrent.addChar(charCurrent);

      if (cc == StringUtils.NEW_LINE) {
         regularCharNewLine();
      } else if (cc == StringUtils.NEW_LINE_CARRIAGE_RETURN) {
         regularCharCarriage();
      } else if (cc == StringUtils.FORM_FEED) {
         regularCharFormFeed();
      } else if (cc == StringUtils.TAB) {
         regularCharTab();
      } else if (cc == markupStartChar) {
         //check markup
      } else if (cc == StringUtils.ENGLISH_SPACE) {
         if (isShowHiddenChars) {
            lineCurrent.charMapTo(srcOffsetRelative, StringUtils.INTER_PUNCT);
         }
         lineCurrent.incrementNumOfSpaces(1);
         regular(charCurrent);
      } else {
         regular(charCurrent);
      }
   }

   private void regular(CharAlgo ca) {
      int lineWidthNew = lineCurrent.getPixelsW();
      if (isTestWidth && lineWidthNew > maxLineWidth) {
         buildLineTooMuchWidth();
      }
   }

   /**
    * Uses the same directive as newline
    * @param i
    */
   private void regularCharCarriage() {
      int directiveNewLine = stringer.getDirectiveNewLine();
      if (directiveNewLine == ITechStringer.SPECIALS_NEWLINE_0_IGNORED) {
         doSpecialIgnore();
      } else if (directiveNewLine == ITechStringer.SPECIALS_NEWLINE_1_SPACE_SPECIAL) {
         doSpecialSpaceFor(StringUtils.SYMBOL_RETURN);
      } else if (directiveNewLine == ITechStringer.SPECIALS_NEWLINE_2_JAVA_ESCAPED) {
         doSpecialJava("\\r");
      } else if (directiveNewLine == ITechStringer.SPECIALS_NEWLINE_3_WORK) {
         if (isShowHiddenChars) {
            charCurrent.setEther();
            doSpecialSpaceFor(StringUtils.LINE_BREAK_RETURN);
         } else {
            //remove
            lineCurrent.removeLastChar();
         }

         //does nothing.. but check if \n follows
      } else {
         throw new IllegalArgumentException();
      }

   }

   private void regularCharFormFeed() {
      int directiveNewLine = stringer.getDirectiveFormFeed();
      if (directiveNewLine == ITechStringer.SPECIALS_NEWLINE_0_IGNORED) {
         doSpecialIgnore();
      } else if (directiveNewLine == ITechStringer.SPECIALS_FORMFEED_1_SPACE_SPECIAL) {
         doSpecialSpaceFor(StringUtils.GENDER_FEMALE);
      } else if (directiveNewLine == ITechStringer.SPECIALS_FORMFEED_2_JAVA_ESCAPED) {
         doSpecialJava("\\f");
      } else if (directiveNewLine == ITechStringer.SPECIALS_FORMFEED_3_NEW_PAGE) {

         if (isShowHiddenChars) {
            charCurrent.setEther();
            doSpecialSpaceFor(StringUtils.GENDER_FEMALE);
         } else {
            //remove
            lineCurrent.removeLastChar();
         }

         buildLineNormal();
         //create as many new lines for a page break
         int numLinesPerPage = stringer.getNumLinesPerPage();
         int addPages = numLinesPerPage - (finalLines.getSize() % numLinesPerPage);
         for (int j = 0; j < addPages; j++) {
            //non existing newline characters.. 
            //each line has a zero length
            buildLineNormal();
         }
      }
   }

   private void regularCharNewLine() {
      int directiveNewLine = stringer.getDirectiveNewLine();
      if (directiveNewLine == ITechStringer.SPECIALS_NEWLINE_0_IGNORED) {
         doSpecialIgnore();
      } else if (directiveNewLine == ITechStringer.SPECIALS_NEWLINE_1_SPACE_SPECIAL) {
         doSpecialSpaceFor(StringUtils.LINE_BREAK_RETURN);
      } else if (directiveNewLine == ITechStringer.SPECIALS_NEWLINE_2_JAVA_ESCAPED) {
         doSpecialJava("\\n");
      } else if (directiveNewLine == ITechStringer.SPECIALS_NEWLINE_3_WORK) {
         if (isShowHiddenChars) {
            charCurrent.setEther();
            doSpecialSpaceFor(StringUtils.LINE_BREAK_RETURN);
         } else {
            //remove
            lineCurrent.removeLastChar();
         }

         buildLineNormal();
         //next line is indeed created by a 
         lineCurrent.setRealModelLine(true);
      } else if (directiveNewLine == ITechStringer.SPECIALS_NEWLINE_4_WORK_SHOW) {

      }
   }

   /**
    * Tab
    * {@link ITechStringer#SPECIALS_TAB_0_SINGLE_SPACE}
    * 
    * @param srcOffsetRel
    * @param c
    * @return
    */
   private void regularCharTab() {
      int tabManager = stringer.getTabManager();
      if (tabManager == ITechStringer.SPECIALS_TAB_0_IGNORED) {
         doSpecialIgnore();
      } else if (tabManager == ITechStringer.SPECIALS_TAB_1_SPACE_SPECIAL) {
         doSpecialSpaceFor(StringUtils.ARROW_RIGHT);
      } else if (tabManager == ITechStringer.SPECIALS_TAB_2_JAVA_ESCAPED) {
         doSpecialJava("\\t");
      } else if (tabManager == ITechStringer.SPECIALS_TAB_4_ECLIPSE) {
         tabEclipse();
      } else if (tabManager == ITechStringer.SPECIALS_TAB_5_COLUMN) {
         tabColumn();
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void start() {

      if (stringer.lengthChars == 0) {
         //do nothing
      } else {
         while (intervals.hasMoreElements()) {
            styleInterval = (IntInterval) intervals.nextElement();
            style = ((StringFxLeaf) styleInterval.getPayload()).getFx();

            int fontH = style.getFontHeight();
            stats.lineCheckHeight(fontH);
            loopStartCurrentInterval();
         }
      }

      //we have our lines

      //case 1 when no chars. we have a single empty line
      //case 2 when it ends with \n. create the same scneario with an "fictive" line 
      if (stringer.lengthChars == 0 || lastChar == StringUtils.NEW_LINE) {
         createNewLineEmptyFictive();
      } else {
         if (!isStop) {
            //finish algo by making sure at least 1 line is created
            buildLineNormal();
         }
      }

   }

   private LineStringer lineFirstWithTabs;

   private int          lastTabOffsetStringer;

   private int          lineLengthAtPrevioustab;

   /**
    * First line with tabs decides the table.
    * table is invalidated with a line without a single tab
    * @param srcOffsetRel
    */
   private void tabColumn() {
      int srcOffsetRel = charCurrent.getOffsetStringer();
      int tabID = stats.getTabLineCountAndIncrement();
      if (tabCols == null) {
         tabCols = new TabColumnStringer[5];
      }
      boolean isFirstLineWithTabs = false;
      if (lineFirstWithTabs == null) {
         isFirstLineWithTabs = true;
         lineFirstWithTabs = lineCurrent;
      } else {
         if (lineFirstWithTabs == lineCurrent) {
            isFirstLineWithTabs = true;
         }
      }

      if (isFirstLineWithTabs) {
         TabColumnStringer tabCol = new TabColumnStringer(stringer);
         int offsetOfTabColumn = lastTabOffsetEnd;
         int sizeOfTabColumn = lineCurrent.getLengthInStringer() - offsetOfTabColumn;

         tabCol.setOffset(offsetOfTabColumn);
         tabCol.setNumCharacters(sizeOfTabColumn);

         lastTabOffsetEnd = lastTabOffsetEnd + sizeOfTabColumn;
         tabCols[tabID] = tabCol;

         //we replace tab with a space or an arrow
         doSpecialSpaceFor(StringUtils.ARROW_RIGHT);

      } else {
         //count the number of tabs in the line

         TabColumnStringer tabCol = tabCols[tabID];
         int sizeOfTabColumn = tabCol.getNumCharacters(); //num of chars

         int lineLength = lineCurrent.getLengthInStringer();
         int numCharsSinceLastTab = lineLength - lineLengthAtPrevioustab;
         //reset to 0 on a new line
         lineLengthAtPrevioustab = lineLength;
         //when positive, we need to add characters to reach the tab column size
         int diffChars = sizeOfTabColumn - numCharsSinceLastTab;

         //we will replace the tab char with a certain number of spaces/special chars
         char c = StringUtils.ARROW_RIGHT;
         char rc = ' ';
         if (isShowHiddenChars) {
            rc = c;
         }
         String str = String.valueOf(rc);
         if (diffChars == 0) {
            lineCurrent.charMapTo(srcOffsetRel, rc); //do not show anything
         } else if (diffChars > 0) {
            int numOfChars = diffChars + 1; // +1 because tabchar is already counted in line length
            //and it is replaced
            String strTab = stringer.getUC().getStrU().getString(str, numOfChars);
            lineCurrent.charMapTo(srcOffsetRel, strTab);
         } else {
            lineCurrent.charMapTo(srcOffsetRel, rc); //do not show anything
            //but remove
            int v = Math.abs(diffChars);
            //when too big, hides the characters 
            for (int i = 0; i < v; i++) {
               int offset = srcOffsetRel - i - 1;
               lineCurrent.charRemove(offset);
            }
         }

      }
   }

   private void tabEclipse() {

      int tabSize = stringer.getDirectiveTabAux();
      int lineSize = lineCurrent.getNumCharVisible();
      //\t was already added. so we need to remove one
      lineSize--;
      int base = (lineSize % tabSize);
      int numSpacesForTab = tabSize - base;
      char cs = isShowHiddenChars ? StringUtils.ARROW_RIGHT : StringUtils.ENGLISH_SPACE;
      String str = String.valueOf(cs);

      //lineCurrent.incrementNumOfSpaces(numSpacesForTab);

      String spaces = stringer.getUC().getStrU().getString(str, numSpacesForTab);
      lineCurrent.addCharJavaEscaped(spaces, style);

   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, LineAlgo.class, 1009);
      toStringPrivate(dc);
      super.toString(dc.sup());

      dc.appendVarWithSpace("isShowHiddenChars", isShowHiddenChars);

      dc.appendVarWithNewLine("lastChar", lastChar);
      dc.appendVarWithSpace("maxLinesNum", maxLinesNum);
      dc.appendVarWithSpace("maxLineWidth", maxLineWidth);

      dc.nlLvl(lineCurrent, "newLine");
      dc.nlLvl(linePrevious, "previousLine");
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, LineAlgo.class);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   private void toStringPrivate(Dctx dc) {

   }
   //#enddebug

}
