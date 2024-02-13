package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.helpers.StringBBuilder;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.strings.CharMapper;
import pasa.cbentley.core.src4.structs.IntIntervals;
import pasa.cbentley.core.src4.utils.StringUtils;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;
import pasa.cbentley.framework.drawx.src4.string.interfaces.ITechStringer;

/**
 * {@link CharMap} manages the trim replace with 2 dots ..  for lack of space
 * Example of a trimmed line because it is too big for the {@link Stringer} area and word wrap is set to
 * {@link ITechStringer#WORDWRAP_0_NONE}
 * 
 * this sentence is trimmed because not enou..
 * 
 * @author Charles Bentley
 *
 */
public class LineStringer extends ObjectDrw {

   /**
    * Mapping of the char Width
    */
   int[]              charsWidth;

   private boolean    hasDifferentFonts;

   private int        index;

   /**
    * True when empty line for form feed page break
    */
   private boolean    isFictiveLine;

   private boolean    isJustified;

   private boolean    isMonospaced;

   /**
    * True when this line was created because of \n
    */
   private boolean    isRealModelLine;

   private int        len;

   private LineFx     lineFx;

   private CharMapper map;

   private int        offset;

   private int        pixelsH;

   private int        pixelsW;

   private Stringer   stringer;

   private int        widthMono;

   private int[]      wordBreaks;

   private int        x;

   private int        y;

   public LineStringer(Stringer stringer) {
      super(stringer.getDRC());
      this.stringer = stringer;
   }

   /**
    * Called once the length is known and all values set.
    * 
    * This method builds the charmap if any
    */
   public void build() {
      if (map != null) {
         int offset = stringer.offsetChars + this.offset;
         map.setSource(stringer.chars, offset, this.len);
         map.build();
      }
   }

   /**
    * At offset, replace existing character by c
    * @param offset stringer srcChar relative
    * @param c char to replace one at offset
    */
   public void charMapTo(int offset, char c) {
      int lineMappedOffset = stringer.offsetChars + offset;
      getCharMapper().opReplaceChar(lineMappedOffset, c);
   }

   /**
    * 
    * @param index
    * @param len
    */
   public void debugExIsInside(int index, int len) {
      boolean isOffsetOk = 0 <= index && index < this.len;
      boolean isLenOk = len > 0 && (index + len) <= this.offset + this.len;
      if (!isOffsetOk) {
         //#debug
         toDLog().pAlways("msg", this, LineStringer.class, "debugExIsInside", LVL_05_FINE, false);
         throw new IllegalArgumentException("offset " + index + " is not inside Line");
      }
      if (!isLenOk) {
         throw new IllegalArgumentException("length " + len + " is not inside Line (offset=" + index + ")");
      }
   }

   public void enableCharWidths() {
      if (charsWidth == null) {
         charsWidth = new int[len];
      }
   }

   /**
    * 
    * @param offsetLine relative to line offset
    * @return
    */
   public char getChar(int offsetLine) {
      if (map == null) {
         return stringer.getCharAtRelative(this.offset + offsetLine);
      } else {
         return map.getCharsMapped()[offsetLine];
      }
   }

   /**
    * The char array reference to be used for getting chars
    * @return
    */
   public char[] getCharArrayRef() {
      if (map == null) {
         return stringer.chars;
      } else {
         return map.getCharsMapped();
      }
   }

   /**
    * The offset of the first character of this line in the array returned by
    * {@link LineStringer#getCharArrayRef()}
    * 
    * @return
    */
   public int getCharArrayRefOffset() {
      if (map == null) {
         return stringer.offsetChars + offset;
      } else {
         return 0;
      }
   }

   public CharMapper getCharMapper() {
      if (map == null) {
         map = new CharMapper(this.getUC());
      }
      return map;
   }

   /**
    * Which character index corresponds to the pixel width
    * <p>
    * Allows to find at which offset to start drawing
    * </p>
    * @param pixelW
    * @return
    */
   public int getCharOffsetForPixle(int pixelW) {
      throw new RuntimeException();
   }

   public int getCharsWidth(int indexRelative, int len) {
      int sum = 0;
      for (int i = 0; i < len; i++) {
         sum += charsWidth[indexRelative + i];
      }
      return sum;
   }

   /**
    * 
    * Returns the width consumed by those characters.
    * <br>
    * <br>
    * 
    * @param indexRelative 0 based index.
    * @param len
    * @return
    * @throws IllegalArgumentException when values are out of bounds
    */
   public int getCharsWidthConsumed(int indexRelative, int len) {
      debugExIsInside(indexRelative, len);
      if (map == null) {
         return stringer.getMetrics().getWidthConsumed(indexRelative, len);
      } else {
         if (isMonospaced) {
            return len * widthMono;
         } else {
            if (charsWidth == null) {
               return stringer.getMetrics().getWidthConsumed(indexRelative, len);
            } else {
               return getCharsWidth(indexRelative, len);
            }
         }
      }
   }

   /**
    * Offset relative to line offset
    * @param offsetRel
    * @return
    */
   public int getCharWidth(int offsetRel) {
      if (charsWidth == null) {
         return stringer.getMetrics().getCharWidth(offset + offsetRel);
      } else {
         return charsWidth[offsetRel];
      }
   }

   public ByteObject getFigureBG() {
      if (lineFx != null) {
         return lineFx.getFigureBG();
      }
      return null;
   }

   public int getIndex() {
      return index;
   }

   public int getLen() {
      return len;
   }

   /**
    * User visible string for this line
    * @return
    */
   public String getLineString() {
      if (map == null) {
         int offset = stringer.offsetChars + this.offset;
         return new String(stringer.chars, offset, len);
      } else {
         return map.getStringMapped();
      }
   }

   /**
    * Offset relative to Stringer.
    * 
    * The first offset relative to Line is always zero because there are no
    * offsetLine. Its always zero by construction.
    * @return
    */
   public int getOffset() {
      return offset;
   }

   public int getOffsetLast() {
      return offset + len - 1;
   }

   public int getPixelsH() {
      return pixelsH;
   }

   public int getPixelsW() {
      return pixelsW;
   }

   public Stringer getStringer() {
      return stringer;
   }

   /**
    * Return and compute the word intervals on this line
    * @return
    */
   public int[] getWordBreaks() {
      if (wordBreaks == null) {
         StringUtils strU = drc.getUCtx().getStrU();
         int offsetStartLine = stringer.offsetChars + offset;
         return strU.getBreaksWord(stringer.chars, offsetStartLine, len);
      }
      return wordBreaks;
   }

   public IntIntervals getWords() {
      stringer.buildIntervalWords();
      return null;
   }

   /**
    * Offset to X
    * @return
    */
   public int getX() {
      return x;
   }

   /**
    * Offset to y
    * @return
    */
   public int getY() {
      return y;
   }

   /**
    * Specifically increase the size of chars
    * <p>
    * charsWidth are enabled
    * </p>
    * @param offsetRel
    */
   public void incrementCharWidth(int offsetRel, int val) {
      charsWidth[offsetRel] += val;
      pixelsW += val;
   }

   public boolean isFictiveLine() {
      return isFictiveLine;
   }

   public boolean isHasDifferentFonts() {
      return hasDifferentFonts;
   }

   public boolean isInside(int index) {
      return offset <= index && (offset + len) > index;
   }

   public boolean isJustified() {
      return isJustified;
   }

   /**
    * True if every character in the line have the same width
    * @return
    */
   public boolean isMonospaced() {
      if (isJustified) {
         return false;
      }
      return isMonospaced;
   }

   public boolean isRealModelLine() {
      return isRealModelLine;
   }

   public void setFictiveLine(boolean isFictiveLine) {
      this.isFictiveLine = isFictiveLine;
   }

   public void setHasDifferentFonts(boolean hasDifferentFonts) {
      this.hasDifferentFonts = hasDifferentFonts;
   }

   public void setIndex(int index) {
      this.index = index;
   }

   public void setJustified(boolean b) {
      isJustified = b;
   }

   public void setLen(int len) {
      this.len = len;
   }

   public void setOffset(int offset) {
      this.offset = offset;
   }

   public void setPixelsH(int pixelsH) {
      this.pixelsH = pixelsH;
   }

   public void setPixelsW(int pixelsW) {
      this.pixelsW = pixelsW;
   }

   public void setRealModelLine(boolean isRealModelLine) {
      this.isRealModelLine = isRealModelLine;
   }

   public void setX(int x) {
      this.x = x;
   }

   public void setY(int y) {
      this.y = y;
   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, LineStringer.class, "@line5");
      toStringPrivate(dc);
      super.toString(dc.sup());

      dc.nl();
      dc.appendVar("x", x);
      dc.appendVarWithSpace("y", y);
      dc.appendVarWithSpace("pixelsW", pixelsW);
      dc.appendVarWithSpace("pixelsH", pixelsH);
      dc.appendVarWithSpace("widthMono", widthMono);

      dc.appendVarWithNewLine("isFictiveLine", isMonospaced);
      dc.appendVarWithSpace("hasDifferentFonts", hasDifferentFonts);
      dc.appendVarWithSpace("isFictiveLine", isFictiveLine);
      dc.appendVarWithSpace("isRealModelLine", isRealModelLine);

      String val = new String(stringer.chars, stringer.offsetChars + offset, len);
      dc.appendVarWithNewLine("String", val);

      dc.nlLvl("wordBreaks", wordBreaks, 2);
      dc.nlLvl(map, "CharMapper");
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, LineStringer.class);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   private void toStringPrivate(Dctx dc) {
      dc.appendVarWithSpace("index", getIndex());
      dc.appendVarWithSpace("offset", offset);
      dc.appendVarWithSpace("len", len);
      dc.append('[');
      dc.append(getOffset());
      dc.append(',');
      dc.append(getOffsetLast());
      dc.append(']');
   }

   /**
    * Append Visible Text
    * @param sb
    * @param offsetStart line relative offset.. so first char is 0
    * @param offsetEnd line relative offset
    */
   public void appendCharFromOffsets(StringBBuilder sb, int offsetStart, int offsetEnd) {
      int diff = offsetEnd - offsetStart;
      int count = 0;
      char[] array = getCharArrayRef();
      int arraOffset = getCharArrayRefOffset();
      while (count < diff) {
         char c = array[arraOffset + count];
         sb.append(c);
         count++;
      }
   }

   /**
    * Append the model text
    * @param sb
    * @param offsetStart
    * @param offsetEnd
    */
   public void appendCharFromOffsetsModel(StringBBuilder sb, int offsetStart, int offsetEnd) {
      if (map == null) {
         //model equals visible
         appendCharFromOffsets(sb, offsetStart, offsetEnd);
      } else {
         map.appendStringSrc(sb, offsetStart, offsetEnd);
      }
   }

   //#enddebug

}
