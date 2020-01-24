package pasa.cbentley.framework.drawx.src4.string;


import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.logging.IStringable;
import pasa.cbentley.core.src4.structs.IntBuffer;
import pasa.cbentley.core.src4.structs.IntToInts;
import pasa.cbentley.core.src4.utils.IntUtils;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.utils.AnchorUtils;

/**
 * Controls the sizes and position of each characters of a {@link Stringer}.
 * <br>
 * <br>
 * It trims or breaks the String of the {@link Stringer} by computing computes pw and ph using the {@link StringFx}
 * <br>
 * 
 * {@link StringMetrics} are updated when the system flag tells us font sizes have changed q
 * <br>
 * @author Charles-Philip Bentley
 *
 */
public class StringMetrics implements IStringable {

   /**
    * <b>Array Structure : Triplets </b> <br>
    * <li>index[0] = last valid index (6). Number of lines is this value divided by 3
    * <li>index[1] = start index of 1st line
    * <li>index[2] = number of visible chars for that line
    * <li>index[3] = number of pixels consumed by visible characters that line
    * <li>index[4] = start index of 2nd line
    * <li>index[5] = number of chars for the 2nd line
    * <li>index[6] = number pixels consumed by the 2nd line
    * <li>index[7] = Flags
    * <br>
    * <br>
    * In case of Trim, the last two letters will be replaced with .. or . if not enough space for 2 or 1 letters
    */
   public static final int[]  BREAK_LINE_SPEC = null;

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
   public static final int[]  BREAK_WORD_SPEC = null;

   /** 
    * New line constant 
    */
   public static final char   C_NEWLINE       = '\n';

   /**
    *  Textbreak constants 
    *  TODO what about arabic ?
    */
   public static final char[] C_TEXTBREAKS    = { ' ', '?', ';', ',', '.', '!', ':', '-', '=', '(', ')', '[', ']' };

   /**
    * 
    * @param c
    * @return
    */
   public static boolean isCharBreak(char c) {
      for (int i = C_TEXTBREAKS.length - 1; i >= 0; i--) {
         if (c == C_TEXTBREAKS[i]) {
            return true;
         }
      }
      return false;
   }

   /**
    * Stored value
    */
   int                breakHeight;

   /**
    * <b>Array Structure : Triplets </b> <br>
    * <li>index[0] = last valid index (6). Number of lines is this value divided by 3
    * <li>index[1] = start index of 1st line
    * <li>index[2] = number of visible chars for that line
    * <li>index[3] = number of pixels consumed by visible characters that line
    * <li>index[4] = start index of 2nd line
    * <li>index[5] = number of chars for the 2nd line
    * <li>index[6] = number pixels consumed by the 2nd line
    * <li>index[7] = Flags
    * <br>
    * <br>
    * Format specified by {@link StringMetrics#BREAK_LINE_SPEC}
    * 
    * In case of Trim, the last two letters will be replaced with .. or . if not enough space for 2 or 1 letters
    */
   int[]              breaks;

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
   int[]              breaksWord;

   /**
    * Break type used to break the string.
    */
   int                breakType;

   /**
    * Stored value
    */
   int                breakWidth;

   /**
    * Characters widths, computed during breaking.
    * <br>
    * Includes modifications made by {@link StringFx}.
    * <br>
    * TODO When using a Monospace font, this is not needed.
    * <br>
    * <br>
    * 
    */
   private int[]      charWidths = new int[10];

   /**
    * Char x positions relative to first char at 0, computed during breaking
    * <br>
    * Those values are used for caret positioning. All {@link StringFx} scoped to {@link IFxStr#FX_SCOPE_0_CHAR}  artifacts use it.
    * <br>
    * Values depends on {@link StringDraw} anchoring {@link Anchor}
    * <br>
    * For basic {@link Stringer} type, those values are computed on demand.
    * <br>
    * For {@link IStr#BREAK_1_WIDTH}, values are computed during the breaking process.
    * <br>
    * <br>
    * 
    */
   int[]              charXs     = new int[10];

   /**
    * Char y positions relative to first char at 0.
    * <br>
    * An rotation animation will modify those values
    */
   int[]              charYs     = new int[10];

   private DrwCtx drc;

   /**
    * Not null when different lines have different heights due to different Fxs.
    */
   private int[]      lineHeights;

   /**
    * The pixel size width for each line.
    * <br>
    * <br>
    * TODO When settings allows it. redistribute extra space to inbetween characters for that line
    * <br>
    * <br>
    * When only one line, this value is null and pw is returned.
    */
   int[]              lineWidths;

   /**
    * word breaks relative to the start of a line
    */
   int[][]            lineWordBreaks;

   /**
    * Relative x positions of lines. null when no line breaking.
    * <br>
    * <br>
    * Animation may work on those values.
    * <br>
    * <br>
    * 
    */
   int[]              lineXs;

   /**
    * 
    */
   int[]              lineYs;

   /**
    * Height of the {@link Stringer}
    * <br>
    * <br>
    * Used by {@link StringMetrics#getPrefHeight()}
    */
   private int        ph         = -1;

   /**
    * Width of the {@link Stringer}
    * <li>When {@link IStr#BREAK_1_WIDTH}, this is the value given during the break/format process
    * <li>otherwise, it will be the computed length
    * <br>
    * <br>
    * Used by {@link StringMetrics#getPrefWidth()}
    */
   private int        pw         = -1;

   private Stringer   stringer   = null;

   /**
    * Initialize the {@link StringMetrics} with the controlling {@link Stringer}.
    * <br>
    * <br>
    * 
    * @param stringer
    */
   public StringMetrics(DrwCtx drc, Stringer stringer) {
      this.drc = drc;
      this.stringer = stringer;
   }

   /**
    * Adds a char in a horizontally displayed String.
    * <br>
    * <br>
    * Update the pw. Redo a break if necessary 
    * <br>
    * TODO Flag that breaks are invalid!
    * <br>
    * @param indexRelative
    * @param c
    */
   public void addChar(int indexRelative, char c) {
      charWidths = drc.getMem().ensureCapacity(charWidths, stringer.lengthChars + 1);
      int cw = getCharWidthCompute(c, indexRelative);
      for (int i = stringer.lengthChars - 1; i >= indexRelative; i--) {
         charWidths[i + 1] = charWidths[i];
      }
      charWidths[indexRelative] = cw;

      //now update the breaks. update char on a line. if it goes too far, update following lines
      if (stringer.hasState(Stringer.STATE_06_CHAR_POSITIONS)) {
         for (int i = indexRelative + 1; i < charXs.length; i++) {
            charXs[i] += cw;
         }
      }
      pw += cw;
   }

   /**
    * When breaking at a newline or at a space, the character will never be drawn.
    * <br>
    * <br>
    * @param text
    * @param offset from which method starts reading characters.
    * @param len How many characters to read
    * @param width      Width given for the break
    * @param options when equal to 1
    * @param buffer holder for startoffset, numchar and pixel length of line
    * @return relative offset of next break or length of text if no more breaks
    */
   private int breakFindNext(char[] text, int offset, int len, int width, int options, IntBuffer buffer) {
      int breakOffset = offset;
      int textW = 0;
      int niceBreak = -1;
      int niceBreakWidth = 0;
      int lineWidth = 0;
      boolean isBigger = false;
      char c;
      int cw;
      //tries finding characters such as '!' '.' or ' ' It is a good position to create a new line.
      while (breakOffset < offset + len) {
         if (breakOffset == offset + len) {
            c = C_TEXTBREAKS[0]; // last character + 1, fake break char
         } else {
            c = text[breakOffset];
         }
         if (options != 1 && c == C_NEWLINE) {
            // got a nice break here, new line
            niceBreak = breakOffset;
            break;
         }

         //save char computation
         cw = getCharWidth(breakOffset);

         textW += cw;

         // Try finding break charachters
         if (isCharBreak(c)) {
            niceBreak = breakOffset;
            niceBreakWidth = textW;
            if (c == ' ' || c == '\n') {
               niceBreakWidth -= cw;
               //textW -= cw;
            }
         }

         if (textW > width) {
            textW -= cw;
            isBigger = true;
            break;
         }

         //check if offset position is at the last character.
         //         if (breakOffset == offset + len - 1) {
         //            // Special case, skip the last character
         //            niceBreak = breakOffset + 1;
         //         }

         breakOffset++; //go to next character.
      }
      lineWidth = niceBreakWidth;
      int finalOffset = 0;
      //assert that nicebreak is the index of the last
      //when none was found, breakoffset is the last
      if (isBigger && niceBreak > offset && niceBreak < offset + len - 2 && (text[niceBreak + 1] == ' ')) {
         finalOffset = niceBreak + 2; // case: special case to get rid of extra spaces
      } else if (isBigger && niceBreak > offset && niceBreak < offset + len) {
         finalOffset = niceBreak + 1; // case: found a nice break, use this
      } else if (breakOffset > offset + len) {
         finalOffset = breakOffset - 1; // case: broke due to text width too big
      } else if (breakOffset == offset) {
         finalOffset = breakOffset + 1; // case: broken on first char, step one more
      } else {
         finalOffset = breakOffset; // case: default
      }

      int numChars = finalOffset - offset;
      buffer.addInt(offset);
      buffer.addInt(numChars);
      buffer.addInt(lineWidth);

      return finalOffset;
   }

   /**
    * Returns next break when breaking a string starting at offset. Look at max len characters.
    * <br>
    * <br>
    * TODO option of breaking = ignore /n
    * @param text   The chars to calculate on
    * @param offset From what offset to read in chars
    * @param len    How many characters to read
    * @param w      Width given for the break
    * @return relative offset of next break or length of text if no more breaks
    */
   private int breakFindNextOld(char[] text, int offset, int len, int w) {
      int breakOffset = offset;
      int textW = 0;
      int niceBreak = -1;
      char c;
      //loop until width is bigger and until end of string
      while (breakOffset < offset + len && textW < w) {
         if (breakOffset == offset + len) {
            c = C_TEXTBREAKS[0]; // last character + 1, fake break char
         } else {
            c = text[breakOffset];
         }
         //TODO option to ignore this
         if (c == C_NEWLINE) {
            // got a nice break here, new line
            niceBreak = breakOffset;
            break;
         }

         // Try finding break charachters
         if (isCharBreak(c)) {
            niceBreak = breakOffset;
         }

         //save char computation
         textW += getCharWidth(breakOffset);

         if (breakOffset == offset + len - 1) {
            // Special case, skip the last character
            niceBreak = breakOffset + 1;
         }
         breakOffset++;
      }
      //assert that nicebreak is the index of the last
      //when none was found, breakoffset is the last
      if (niceBreak > offset && niceBreak < offset + len - 2 && (text[niceBreak + 1] == ' '))
         return niceBreak + 2; // case: special case to get rid of extra spaces
      else if (niceBreak > offset && niceBreak < offset + len)
         return niceBreak + 1; // case: found a nice break, use this
      else if (breakOffset > offset + len)
         return breakOffset - 1; // case: broke due to text width too big
      else if (breakOffset == offset)
         return breakOffset + 1; // case: broken on first char, step one more
      else
         return breakOffset; // case: default

   }

   /**
    * Break {@link Stringer} string offset/len according to the break type method.
    * <br>
    * <br>
    * <b>Array Structure : Triplets </b> <br>
    * <li>index[0] = last valid index (6). Number of lines is this value divided by 3<br>
    * <li>index[1] = start index of 1st line<br>
    * <li>index[2] = number of visible chars for that line excluding new line characters<br>
    * <li>index[3] = number of pixels consumed by visible characters in that line<br>
    * <li>index[4] = start index of 2nd line<br>
    * <li>index[5] = number of chars for the 2nd line<br>
    * <li>index[6] = number pixels consumed by the 2nd line<br>
    * <li>index[7] = trailer : construction flags. 1 means word fully fits the area. <br>
    * 
    * <br>
    * <br>
    * Break Types
    * <li> {@link IStr#BREAK_0_NONE}
    * <li> {@link IStr#BREAK_1_WIDTH}
    * <li> {@link IStr#BREAK_2_NATURAL}
    * <li> {@link IStr#BREAK_3_ONE_LINE}
    * <li> {@link IStr#BREAK_4_TRIM_SINGLE_LINE}
    * <li> {@link IStr#BREAK_6_WORD_LINE}
    * <br>
    * <br>
    * When trimmed, the index.len is valid.
    * <br>
    * <br>
    * 
    * @param breakType
    * @param maxLines
    * @param breakWidth
    * @param breakHeight
    * @param stringer The breaking is done relative to {@link Stringer#offsetChars}
    * @return maybe return null if no breaking was made.
    */
   private int[] breakString(int breakType, int maxLines, int breakWidth, int breakHeight, Stringer stringer) {
      int[] breaks = null;
      switch (breakType) {
         case IStr.BREAK_0_NONE:
            break;
         case IStr.BREAK_1_WIDTH:
            //break at cw. unlimited lines
            breaks = stringer.getMetrics().breakStringLine2(breakWidth, -1, stringer);
            break;
         case IStr.BREAK_2_NATURAL:
            //natural break
            //System.out.println(new String(chars, offset, len));
            breaks = drc.getUCtx().getStrU().getBreaksLineNatural(stringer.chars, stringer.offsetChars, stringer.lengthChars);
            //MUtils.debug(breaks);
            break;
         case IStr.BREAK_3_ONE_LINE:
            //remove all new lines characters
            break;
         case IStr.BREAK_4_TRIM_SINGLE_LINE:
            //trim at cw (only 1 line). if not enough room for a single character?
            breaks = getTrimSingleLine(breakWidth, stringer);
            break;
         case IStr.BREAK_5_TRIM_FIT_HEIGHT:
            //trim at cw and ch
            int numLines = breakHeight / stringer.getMetrics().getLineHeight();
            breaks = getTrimFormat(breakWidth, numLines, stringer);
            break;
         default:
            break;
      }
      return breaks;
   }

   /**
    * Main entry point for formatting a {@link Stringer}. Must be called after {@link StringFx} have been set up.
    * <br>
    * <br>
    * POST: String is broken
    * <br>
    * <br>
    * Called by {@link Stringer#meterString(int, int, int, int)}
    * <br>
    * <br>
    * Depending on the breakType value, the method creates an int[] containing lines information.
    * <br>
    * <br>
    * 
    * @param breakType {@link IStr#BREAK_0_NONE} to {@link IStr#BREAK_6_WORD_LINE}.
    * @param maxLinesth
    * @param breakWidth
    * @param breakHeight
    */
   void breakStringEntry(int breakType, int maxLines, int breakWidth, int breakHeight) {
      this.breakType = breakType;
      if (stringer.hasState(Stringer.STATE_09_WORD_FX)) {
         //we must index the words
         breaksWord = breakWords(stringer);

      } else {
         breaksWord = null;
      }
      int[] breaks = breakString(breakType, maxLines, breakWidth, breakHeight, stringer);

      if (breaks != null) {
         int lastBreaksIndex = breaks[0];
         int breakFlag = breaks[lastBreaksIndex];

         //check if breaks are useless: only if 1 line AND full fit
         if (breaks[0] == 1) {
            //only 1 line assume there is no breaks then
            breaks = null;
         } else if ((breaks[0] == Stringer.BREAK_HEADER_SIZE + Stringer.BREAK_WINDOW_SIZE + Stringer.BREAK_TRAILER_SIZE - 1)) {
            //1 line
            if (breakFlag == 1) {
               breaks = null;
            }
         }
      }
      this.breaks = breaks;
      if (breaks != null) {
         //compute line positions
         computeLinePositions();
         //managed the trim issue.
         switch (breakType) {
            case IStr.BREAK_4_TRIM_SINGLE_LINE:
            case IStr.BREAK_5_TRIM_FIT_HEIGHT:
               pw = breakWidth; //shorten the Stringer's width to breakWidth
               //only trim if needed : check last line if trim is needed
               int lastBreaksIndex = breaks[0];
               int breakFlag = breaks[lastBreaksIndex];
               if (breakFlag == 0) {
                  int indexNumberLastLine = lastBreaksIndex - 2;
                  int indexFirstIndex = lastBreaksIndex - 3;
                  //last index of char is 
                  int numberOfVisibleCharsOnLastLine = breaks[indexNumberLastLine];
                  int lastIndex = breaks[indexFirstIndex] + numberOfVisibleCharsOnLastLine;
                  stringer.trim(lastIndex);
               } else {
                  //no trim cue to install
               }
               break;
            default:
               break;
         }
      }
      stringer.setState(Stringer.STATE_07_BROKEN, true);
   }

   /**
    * Called when breaking {@link IStr#BREAK_1_WIDTH}
    * <br>
    * <br>
    * Compute breaks.
    * <br>
    * Not CharXs, and charY because those values are rarely needed.
    * <br> 
    * @param width
    * @param maxLines
    * @param stringer
    * @return
    */
   private int[] breakStringLine(int width, int maxLines, Stringer stringer) {
      char[] text = stringer.chars;
      // Count text lines
      int startOffsetAbs = stringer.offsetChars;
      int finalOffsetAbs = stringer.offsetChars + stringer.lengthChars;
      int lenLeft = stringer.lengthChars;
      int numLines = 0;
      int lineOffset = 0;
      IntToInts ib = new IntToInts(drc.getUCtx(), IntToInts.TYPE_0NON_ORDERED);
      IntBuffer intBuffer = new IntBuffer(drc.getUCtx());
      //count the number of lines
      while (startOffsetAbs < finalOffsetAbs) {
         //absolute offset.
         int breakAbsOffset = breakFindNextOld(text, startOffsetAbs, lenLeft, width);
         int numChars = breakAbsOffset - startOffsetAbs;
         lenLeft -= numChars;
         startOffsetAbs = breakAbsOffset;
         ib.add(lineOffset, numChars);

         lineOffset += numChars;
         numLines++;
         if (numLines == maxLines) {
            break;
         }
      }
      return ib.getArrayReference();
   }

   /**
    * Break string into lines. 
    * <br>
    * Returns as soon as we have maxLines of data.
    * <br>
    * Creates at least one line
    * <br>
    * <br>
    * <br>
    * Format is specified by {@link StringMetrics#BREAK_LINE_SPEC}
    * <br>
    * <br>
    * @param width
    * @param maxLines -1 if never stops early.
    * @param stringer TODO
    * @return array whose format specified by {@link StringMetrics#BREAK_LINE_SPEC}
    */
   private int[] breakStringLine2(int width, int maxLines, Stringer stringer) {
      char[] text = stringer.chars;
      // Count text lines
      int startOffsetAbs = stringer.offsetChars;
      int finalOffsetAbs = stringer.offsetChars + stringer.lengthChars;
      int lenLeft = stringer.lengthChars;
      int numLines = 0;
      IntBuffer intBuffer = new IntBuffer(drc.getUCtx());
      int options = 0;
      //count the number of lines
      while (startOffsetAbs < finalOffsetAbs) {
         //absolute offset.
         int breakOffset = breakFindNext(text, startOffsetAbs, lenLeft, width, options, intBuffer);
         int numChars = breakOffset - startOffsetAbs;
         lenLeft -= numChars;
         startOffsetAbs = breakOffset;
         numLines++;
         if (numLines == maxLines) {
            break;
         }
      }
      //make all offsets relative to start offset
      for (int i = 0; i < numLines; i++) {

      }
      intBuffer.addInt(0);
      return intBuffer.getIntsRef();
   }

   /**
    * Int array specified by {@link StringMetrics#BREAK_WORD_SPEC}
    * <br>
    * <br>
    * @param stre
    * @return
    */
   private int[] breakWords(Stringer stre) {
      return drc.getUCtx().getStrU().getBreaksWord(stre.chars, stre.offsetChars, stre.lengthChars);
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
      int dx = stringer.fx.fxLineExtraW; //relative to Stringer x coordinate
      int dy = 0;
      //align with respect to local
      //TODO anchor locale and opposite 

      int day = AnchorUtils.getYAlign(stringer.anchor, 0, stringer.areaH, stringer.metrics.getPrefHeight());
      if (breaks != null) {
         //iterate over each line
         int lineCount = 0;
         int numLines = getNumOfLines();
         for (int i = 0; i < numLines; i++) {
            int index = Stringer.BREAK_HEADER_SIZE + (i * Stringer.BREAK_WINDOW_SIZE);
            //0 based line index
            int startIndex = breaks[index];
            int numChars = breaks[index + 1];
            //you need to align after the line widths have been computed 
            int dax = AnchorUtils.getXAlign(stringer.anchor, 0, stringer.areaW, getPrefWidthLine(lineCount));
            //int sizeLen = breaks[i + 2];
            for (int j = 0; j < numChars; j++) {
               int off = startIndex + j;
               charXs[off] = dax + dx;
               charYs[off] = day + dy;
               dx = getFXDxOffset(lineCount, dx, off);
            }
            dy = getFXDyOffset(lineCount, dy);
            dx = 0;
            lineCount++;
         }
      } else {
         int dax = AnchorUtils.getXAlign(stringer.anchor, 0, stringer.areaW, getPrefWidth());
         //single line
         for (int i = 0; i < stringer.lengthChars; i++) {
            charXs[i] = dax + dx;
            charYs[i] = day + dy;
            dx = getFXDxOffset(0, dx, i);
         }
      }
      stringer.setState(Stringer.STATE_06_CHAR_POSITIONS, true);
   }

   /**
    * Lines positions are actually the position of the first char
    */
   public void computeLinePositions() {
      int numLines = getNumOfLines();
      lineXs = new int[numLines];
      lineYs = new int[numLines];
      int dy = 0;
      //figure anchoring
      for (int i = 0; i < numLines; i++) {
         int index = Stringer.BREAK_HEADER_SIZE + (i * Stringer.BREAK_WINDOW_SIZE);
         int linePixelSize = breaks[index + 2];
         int dax = AnchorUtils.getXAlign(stringer.anchor, 0, stringer.areaW, linePixelSize);
         lineXs[i] = dax;
         lineYs[i] = dy;
         dy += getLineHeight(i);
      }
   }

   /**
    * When {@link StringFx} have index based Fx, a full break must be done again.
    * <br>
    * <br>
    * @param indexRelative
    */
   public void deleteCharAt(int indexRelative) {
      int cw = charWidths[indexRelative];
      pw -= cw;
      boolean doPositions = stringer.hasState(Stringer.STATE_06_CHAR_POSITIONS);
      for (int i = indexRelative; i < stringer.lengthChars - 1; i++) {
         charWidths[i] = charWidths[i + 1];
         if (doPositions) {
            charYs[i] = charYs[i + 1];
            charXs[i] -= cw;
         }
      }
      if (breaks != null) {
         //update the breaks

      }
   }

   /**
    * The breaking value for the character array.
    * <br>
    * <br>
    * 
    * @return
    */
   public int[] getBreaks() {
      return breaks;
   }

   public int getCharHeight(int indexRelative) {
      int value = 0;
      StringFx fx = stringer.getCharFx(indexRelative);
      value = fx.f.getHeight();
      return value;
   }

   /**
    * Compute the character pixel width at the given step, using Font and Fx. 
    * <br>
    * If alreay computed, return it.
    * <br>
    * {@link StringFx} may have special rules for given character index.
    * <br>
    * 
    * @param indexRelative
    * @return
    */
   public int getCharWidth(int indexRelative) {
      //System.out.println("#StringMetrics getCharWidth relativeIndex=" + indexRelative + " charWidths.length=" + charWidths.length + " for " + stringer.offset + ":" + stringer.len);
      if (stringer.hasState(Stringer.STATE_02_CHAR_WIDTHS)) {
         return charWidths[indexRelative];
      } else {
         //compute it
         char c = stringer.chars[stringer.offsetChars + indexRelative];
         int cw = getCharWidthCompute(c, indexRelative);
         charWidths[indexRelative] = cw;
         return cw;
      }
   }

   /**
    * Compute char width as if it is displayed alone.
    * <br>
    * <br>
    * The index relative parameter selects the {@link StringFx} to be used.
    * <br>
    * <br>
    * When {@link Stringer} has {@link Stringer#STATE_01_CHAR_EFFECTS}
    * <br>
    * <br>
    * @param step
    * @return
    */
   public int getCharWidthCompute(char c, int indexRelative) {
      int value = 0;
      StringFx fx = stringer.getCharFx(indexRelative);
      value = fx.f.charWidth(c) + fx.getExtraCharWidth();
      return value;
   }

   public int getCharWidthEtalon() {
      return charWidths[0];
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
      if (!stringer.hasState(Stringer.STATE_06_CHAR_POSITIONS)) {
         //ask to compute all
         computeCharPositions();
      }
      return charXs[indexRelative];
   }

   /**
    * Array of  integer values relative to {@link Stringer#areaX}.
    * <br>
    * <br>
    * Values are computed when flag {@link Stringer#STATE_06_CHAR_POSITIONS} is not set.
    * @return
    */
   public int[] getCharXs() {
      if (!stringer.hasState(Stringer.STATE_06_CHAR_POSITIONS)) {
         //ask to compute all
         computeCharPositions();
      }
      return charXs;
   }

   public int getCharY(int indexRelative) {
      return charYs[indexRelative];
   }

   public int[] getCharYs() {
      if (!stringer.hasState(Stringer.STATE_06_CHAR_POSITIONS)) {
         //ask to compute all
         computeCharPositions();
      }
      return charYs;
   }

   /**
    * TODO {@link IFxStr#FXLINE_OFFSET_02_CHAR_X_OFFSET1} function to modify x coordinates.
    * <br>
    * <br>
    * 
    * @param lineCount
    * @param dx
    * @param startIndex
    * @return
    */
   private int getFXDxOffset(int lineCount, int dx, int startIndex) {
      return dx + getCharWidth(startIndex);
   }

   private int getFXDyOffset(int lineCount, int dy) {
      return dy + stringer.fx.f.getHeight();
   }

   /**
    * What is the line height when different fxs are on the same line?
    * <br>
    * Anwser: the biggest line height of the fxs.
    * <br>
    * <br>
    * 
    * @return
    */
   public int getLineHeight() {
      if (stringer.fx.fxLine == null) {
         return stringer.fx.f.getHeight();
      } else {
         return stringer.fx.f.getHeight();
      }
   }

   /**
    * Compute the line pixel height at the given line count, using {@link Font} and {@link IFxStr}.
    * <br>
    * When use of several Fonts, height is the biggest size.
    * <br>
    * <br>
    * @return
    */
   public int getLineHeight(int lineIndex) {
      if (lineHeights != null) {
         return lineHeights[lineIndex];
      }
      return stringer.fx.f.getHeight();
   }

   /**
    * 
    * @param caretIndex
    * @return
    */
   public int getLineWidth(int caretIndex) {
      return 0;
   }

   public int getLineX(int lineIndex) {
      return lineXs[lineIndex];

   }

   public int getLineY(int lineIndex) {
      return lineYs[lineIndex];
   }

   /**
    * The number of lines
    * @return
    */
   public int getNumOfLines() {
      if (breaks != null) {
         return (breaks[0] - Stringer.BREAK_EXTRA_SIZE + 1) / 3;
      }
      return 1;
   }

   /**
    * Returns the default fx line height
    * <br>
    * <br>
    * 
    * @return
    */
   public int getPrefCharHeight() {
      return stringer.fx.f.getHeight();
   }

   /**
    * Returns the default fx 'm' width
    * @return
    */
   public int getPrefCharWidth() {
      return stringer.fx.f.getWidthWeigh();
   }

   /**
    * Compute the preferred height for the whole set of line(s).
    * <br>
    * <br>
    * 
    * @return
    */
   public int getPrefHeight() {
      if (ph == -1) {
         //compute it
         switch (stringer.drawLineType) {
            case Stringer.TYPE_0_SINGLE_LINE:
            case Stringer.TYPE_1_SINGLE_LINE_FX:
               ph = stringer.fx.getFontHeight();
               break;
            case Stringer.TYPE_2_BREAKS:
            case Stringer.TYPE_3_BREAKS_FX:
               int max = stringer.fx.getFontHeight();
               max += stringer.fx.fxLineExtraH;
               if (breaks != null) {
                  int numLines = getNumOfLines();
                  int val = numLines * (max) + ((numLines - 1) * stringer.fx.fxLineExtraBetween);
                  //System.out.println("#StringMetrics#getPrefHeight numLines=" + numLines + " val=" + val);
                  ph = val;
               } else {
                  ph = max;
               }
               break;
            default:
               throw new IllegalArgumentException();
         }
      }
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
      if (pw == -1) {
         switch (stringer.drawLineType) {
            case Stringer.TYPE_0_SINGLE_LINE:
            case Stringer.TYPE_1_SINGLE_LINE_FX: //TODO compute fx
               pw = stringer.fx.f.charsWidth(stringer.chars, stringer.offsetChars, stringer.lengthChars);
               break;
            case Stringer.TYPE_2_BREAKS:
            case Stringer.TYPE_3_BREAKS_FX:
               int max = 0;
               int numLines = getNumOfLines();
               for (int i = 0; i < numLines; i++) {
                  int index = Stringer.BREAK_HEADER_SIZE + (i * Stringer.BREAK_WINDOW_SIZE);
                  int offset = stringer.offsetChars + breaks[index];
                  int len = breaks[index + 1];
                  int v = stringer.fx.f.charsWidth(stringer.chars, offset, len);
                  if (v > max) {
                     max = v;
                  }
               }
               max += stringer.fx.fxLineExtraW;
               pw = max;
               break;
            default:
               throw new IllegalArgumentException("unknown inner type " + stringer.drawLineType);
         }
      }
      return pw;
   }

   /**
    * Take into account the size of spaces which is removed
    * <br>
    * <br>
    * 
    * @param indexLine 0 based index
    * @return
    */
   public int getPrefWidthLine(int indexLine) {
      if (breaks == null) {
         return pw;
      } else {
         int i = Stringer.BREAK_HEADER_SIZE + (indexLine * Stringer.BREAK_WINDOW_SIZE);
         return breaks[i + 2];
      }
   }

   /**
    * Gets the for that width and number of lines. Uses stringer for string data
    *<br>
    *<br>
    * @param width
    * @param maxLines
    * @param str
    * @return
    */
   public int[] getTrim(int width, int maxLines, Stringer str) {
      int widthPixelCount = 0;
      int lineCount = 0;
      int counter = 1;
      IntToInts breaks = new IntToInts(drc.getUCtx());
      IntBuffer intBuffer = new IntBuffer(drc.getUCtx());
      int numCharOnLine = 0;
      StringMetrics sm = str.getMetrics();
      int charw = 0;
      int stepStart = 0;
      int stepEnd = str.lengthChars;
      for (int step = stepStart; step < stepEnd; step++) {
         charw = sm.getCharWidth(step);
         widthPixelCount += charw;
         if (widthPixelCount > width) {
            if (lineCount == maxLines) {
               //we reached the end of available lines. do a trim.
               if (numCharOnLine > 1) {
                  numCharOnLine -= 2;
               }
               step -= 2;
               breaks.addUnoDuo(step, numCharOnLine);
               return breaks.getArrayReference();
            } else {
               if (numCharOnLine == 0) {
                  breaks.addUnoDuo(stepStart, 1);
                  lineCount++;
                  widthPixelCount = 0;
                  numCharOnLine = 0;
                  stepStart = step + 1;
               } else {
                  //finish current line
                  breaks.addUnoDuo(stepStart, numCharOnLine);
                  stepStart = step;
                  lineCount++;
                  widthPixelCount = charw;
                  numCharOnLine = 1;
               }
               counter += 2;
            }
         } else {
            numCharOnLine += 1;
         }
      }
      if (lineCount == 0) {
         return null;
      } else {
         //finalize line.
         breaks.addUnoDuo(stepStart, numCharOnLine);
         return breaks.getArrayReference();
      }
   }

   /**
    * Format String to fit into the <code>width</code> parameter given the {@link Font}.
    * <br>
    * <br>
    * Does not modify the state of the {@link Stringer}.
    * <br>
    * <br>
    * 
    * When maxLines is 1, trim as soon as width is consumed.
    * <br>
    * When maxLines is 2, trim on the second line after a line break.
    * <br>
    * <br>
    * <b>Structure of Integer Array</b> <br>
    * index[0] = control value<br>
    * index[1] = start index of 1st line<br>
    * index[2] = number of characters on 1st line<br>
    * index[3] = start index of 2nd line<br>
    * index[4] = number of characters on 2nd line<br>
    * <br>
    * This structure allows to skip newline entirely. It allows to draw tabs \t.
    * 
    * <li>array's length is the number of lines * 2 + 1
    * <li>The first value is a control value.
    * <li>A line may be empty.
    * <br>
    * <br>
    * TODO Write a switch to stop the format for really big strings. The maxline is a first security.                 
    * <br>
    * <br>
    * @param width width given for formatting the string. If width not big enough for one character. Method fits at least one letter by line.
    * @param maxLines the number of lines after -1 if infinity of lines. Automatically sets the TRIM state to the 
    * @return integer array 
    */
   public int[] getTrimFormat(int width, int maxLines, Stringer stringer) {
      int lineWidth = 0;
      int lineCount = 1;
      int numCharOnLine = 0;
      IntBuffer data = new IntBuffer(drc.getUCtx());
      StringMetrics sm = stringer.getMetrics();
      int charw = 0;
      int lineStartOffset = 0;
      boolean isTrimNeeded = false;
      for (int step = 0; step < stringer.lengthChars; step++) {
         charw = sm.getCharWidth(step);
         lineWidth += charw;
         if (lineWidth > width) {
            if (lineCount == maxLines) {
               //we reached the end of available lines. trim the last 2 characters to replace them with the trim cue.
               lineWidth -= charw;
               isTrimNeeded = true;
               break;//end the algo
            } else {
               //special case where not even one character can fit the space. only one dot will be drawn.
               if (numCharOnLine == 0) {
                  numCharOnLine = 1;
                  lineWidth = 1;
                  isTrimNeeded = true;
                  break;
               } else {
                  //finish current line
                  data.addInt(lineStartOffset);
                  data.addInt(numCharOnLine);
                  data.addInt(lineWidth - charw);
                  lineStartOffset = step;
                  lineCount++;
                  lineWidth = charw;
                  numCharOnLine = 1;
               }
            }
         } else {
            //increment the number of characters on this line
            numCharOnLine += 1;
         }
      }
      //finalize line if there is enough space
      data.addInt(lineStartOffset);
      data.addInt(numCharOnLine);
      data.addInt(lineWidth);
      int flag = 0;
      if (!isTrimNeeded) {
         flag = 1;
      }
      data.addInt(flag);
      return data.getIntsRef();
   }

   /**
    * Method creates a trim cue with {@link Stringer} and the given width.
    * <br>
    * <br>
    * 
    * POST: the state of {@link Stringer} is not modified.
    * <br>
    * <br>
    * @param str
    * @return null if trimming is not needed. For structure semantics see {@link StringMetrics#breaks}
    */
   public int[] getTrimSingleLine(int width, Stringer str) {
      StringMetrics sm = str.getMetrics();
      int widthPixelCount = 0;
      boolean isTrimmed = false;
      IntBuffer breaks = new IntBuffer(drc.getUCtx());
      int numCharOnLine = 0;
      int charw = 0;
      int stepStart = 0;
      int stepEnd = str.lengthChars;
      for (int step = stepStart; step < stepEnd; step++) {
         charw = sm.getCharWidth(step);
         widthPixelCount += charw;
         if (widthPixelCount <= width) {
            numCharOnLine++;
         } else {
            widthPixelCount -= charw;
            isTrimmed = true;
            break;
         }
      }
      if (isTrimmed) {
         //finalize line.
         breaks.addInt(stepStart);
         breaks.addInt(numCharOnLine);
         breaks.addInt(widthPixelCount);
         breaks.addInt(0);
         return breaks.getIntsRef();
      } else {
         return null;
      }
   }

   /**
    * Pixels consumed by letter since the start of the line
    * <br>
    * @param index
    * @return
    */
   public int getWidthConsumed(int index) {
      int cx = getCharX(index);
      return cx + charWidths[index];
   }

   /**
    * Returns the width consumed by those characters.
    * <br>
    * <br>
    * 
    * @param indexRelative 0 based index.
    * @param len
    * @return
    */
   public int getWidthConsumed(int indexRelative, int len) {
      // TODO Auto-generated method stub
      return 0;
   }

   public void init() {
      pw = -1;
      ph = -1;
      breaks = null;
      lineHeights = null;
      charWidths = drc.getMem().ensureCapacity(charWidths, stringer.lengthChars);
      charXs = drc.getMem().ensureCapacity(charXs, stringer.lengthChars);
      charYs = drc.getMem().ensureCapacity(charYs, stringer.lengthChars);

   }

   public IntUtils getIntUtils() {
      return drc.getUCtx().getIU();
   }
   
   //#mdebug
   public String toString() {
      return Dctx.toString(this);
   }

   public void toString(Dctx sb) {
      sb.root(this, "StringMetrics");
      sb.append(" pw=" + pw + " ph=" + ph);
      sb.append(" Breaks=[" + getIntUtils().debugString(breaks) + "]");
      sb.nl();
      sb.append("charWidths " + getIntUtils().debugString(charWidths));
      sb.nl();
      sb.append("charXs \t" + getIntUtils().debugString(charXs));
      sb.nl();
      sb.append("charYs \t" + getIntUtils().debugString(charYs));
      sb.nl();
      sb.append("lineHeights " + getIntUtils().debugString(lineHeights));
   }

   public String toString1Line() {
      return Dctx.toString1Line(this);
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, "StringMetrics");
      dc.append(" pw=" + pw + " ph=" + ph);
   }

   //#enddebug

   public UCtx toStringGetUCtx() {
      return drc.getUCtx();
   }

   /**
    * 
    * @param index
    * @param c
    */
   public void updateCharAt(int index, char c) {
      int oldw = charWidths[index];
      int cw = getCharWidthCompute(c, index);
      charWidths[index] = cw;
      if (oldw != cw) {
         int diff = cw - oldw;
         pw += diff;
         if (stringer.hasState(Stringer.STATE_06_CHAR_POSITIONS)) {
            for (int i = index + 1; i < charXs.length; i++) {
               charXs[i] += diff;
            }
         }
      }
   }

}
