package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.helpers.StringBBuilder;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.strings.CharMapper;
import pasa.cbentley.core.src4.structs.BufferObject;
import pasa.cbentley.core.src4.structs.IntIntervals;
import pasa.cbentley.core.src4.utils.StringUtils;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;
import pasa.cbentley.framework.drawx.src4.string.interfaces.ITechStringer;

/**
 * <p>
 * {@link CharMap} tracks computations of a single line for the {@link Stringer} class.
 * </p>
 * 
 * <li> {@link CharMapper} when some characters are hidden.  
 * <li> Is the line with different fonts
 * <li> width and height of the line
 * 
 * <p>
 * 
 * {@link CharMap} manages the trim replace with 2 dots ..  for lack of space.
 * 
 * Example of a trimmed line because it is too big for the {@link Stringer} area and word wrap is set to
 * {@link ITechStringer#WORDWRAP_0_NONE}
 * </p>
 * 
 * <p>
 * this sentence is trimmed because not enou..
 * </p>
 * 
 * @author Charles Bentley
 *
 */
public class LineStringer extends ObjectDrw {

   /**
    * Null when all chars have the same height
    */
   int[]                 charsHeight;

   /**
    * When null, all chars have the same width on this line which is
    * 
    * {@link LineStringer#widthMono}
    * 
    * When isJustified 
    * 
    */
   int[]                 charsWidth;

   private boolean       hasDifferentFonts;

   private int           index;

   /**
    * True when empty line for form feed page break
    */
   private boolean       isFictiveLine;

   private boolean       isJustified;

   private boolean       isMonospaced;

   /**
    * True when this line was created because of \n.
    * 
    * <li>{@link LineStringer#isRealModelLine()} 
    * <li>{@link LineStringer#setRealModelLine(boolean)} 
    */
   private boolean       isRealModelLine;

   private boolean       isSpaceOut;

   private int           len;

   private LineFx        lineFx;

   /**
    * Unknown by default
    */
   private int           lineID = -1;

   private CharMapper    map;

   /**
    * Kept if word breaks are required.
    * CharAlgo buffer
    * 
    * When null.. line is built
    * when not null line is being built
    */
   private BuildLineData model;

   private int           numOfSpaces;

   private int           offset;

   private int           pixelsH;

   private int           pixelsW;

   /**
    * building
    * built
    */
   private int           state;

   private Stringer      stringer;

   private int           widthMono;

   private int[]         wordBreaks;

   private int           x;

   private int           y;

   public LineStringer(Stringer stringer) {
      super(stringer.getDRC());
      this.stringer = stringer;
      model = new BuildLineData(drc);
   }

   public void addChar(CharAlgo c) {
      BuildLineData builder = getBuilder();
      builder.addChar(c);

      c.setOffsetLine(len);
      int width = c.getWidth();

      if (len == 0) {
         widthMono = width;
         this.offset = c.getOffsetStringer();
      } else {
         if (widthMono != -1 && width != widthMono) {
            widthMono = -1;
         }
      }

      pixelsW += width;
      int h = c.getHeight();
      if (h > pixelsH) {
         pixelsH = h;
      }
      len++;
   }

   public void addCharEther(CharAlgo ca) {
      ca.setEther();
      this.addChar(ca);
   }

   public void addCharFictive(int offset, char c) {
      getCharMapper().opAddChar(offset, c);
   }

   /**
    * Called when having to show invisible chars
    * @param c
    */
   public void addCharHiddenSpace(CharAlgo c) {
      this.charRemove(c.getOffsetStringer());
      this.incrementLen();
   }

   public void addCharIgnore() {
      CharAlgo lastc = model.removeLastChar();
      int wlast = lastc.getWidth();
      this.pixelsW -= wlast;
      this.charRemove(lastc.getOffsetStringer());
   }

   public CharAlgo addCharJavaEscaped(String str, StringFx style) {
      CharAlgo lastc = model.getLastChar();
      this.charMapTo(lastc.getOffsetStringer(), str);

      int wlast = lastc.getWidth();
      this.pixelsW -= wlast;

      int w = 0;
      for (int i = 0; i < str.length(); i++) {
         char c = str.charAt(i);
         int cw = style.getCharWidth(c);
         w += cw;
      }
      lastc.setWidth(w);
      this.pixelsW += w;
      return lastc;
   }

   public void addCharReplace(CharAlgo ca) {
      this.charMapTo(ca.getOffsetStringer(), StringUtils.LINE_BREAK_RETURN);
      this.incrementLen();
   }

   /**
    * Replace
    * @param c
    * @param isShowHiddenChars
    */
   public void addCharSpaceSpecial(char newC, int newCharWidth) {
      CharAlgo lastc = model.getLastChar();
      int wlast = lastc.getWidth();
      this.pixelsW -= wlast;
      this.pixelsW += newCharWidth;
      lastc.setWidth(newCharWidth);
      lastc.setC(newC);
      lastc.setMapReplaceChar(newC);
      //this.charMapTo(offsetStringer, newC);
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

   /**
    * Called once the length is known and all values set.
    * 
    * This method builds the charmap if any
    */
   public void build() {
      if (model == null) {
         throw new IllegalStateException("Cannot build a line with nothing in it");
      }

      BufferObject chars = model.getChars();
      int size = chars.getSize();
      for (int i = 0; i < size; i++) {
         CharAlgo ca = (CharAlgo) chars.get(i);
         map = ca.addToMap(map);
      }

      buildMap();

      //fill up charWidths for the visible chars
      if (charsWidth == null) {
         int num = getNumCharVisible();
         charsWidth = new int[num];
      }

      //chars that were from model
      chars = model.getChars();
      CharAlgo ca = null;
      int count = 0;
      while ((ca = (CharAlgo) chars.removeFirst()) != null) {
         charsWidth[count] += ca.getWidth();
         count++;
      }
      model = null;

   }

   public void buildMap() {
      if (map != null) {
         int lineOffset = getOffset();
         int srcLength = this.getLengthInStringer();
         int srcOffset = stringer.offsetChars + lineOffset;
         map.setOffsetExtra(lineOffset);
         map.setSource(stringer.chars, srcOffset, srcLength);
         map.build();

      }
   }

   /**
    * At offset, replace existing character by c
    * @param offset stringer srcChar relative
    * @param c char to replace one at offset
    */
   public void charMapTo(int offsetStringer, char c) {
      debugOffsetStringerIsInside(offsetStringer);
      //offset is srcChar relative.. but it has to be lineOffset relative
      CharMapper charMapper = getCharMapper();
      charMapper.opReplaceChar(offsetStringer, c);
   }

   public void charMapTo(int offsetStringer, String str) {
      debugOffsetStringerIsInside(offsetStringer);
      CharMapper charMapper = getCharMapper();
      charMapper.opReplaceWith(offsetStringer, str);
   }

   public void charRemove(int offsetStringer) {
      //offset is srcChar relative.. but it has to be lineOffset relative
      CharMapper charMapper = getCharMapper();
      charMapper.opRemove(offsetStringer);
   }

   /**
    * 
    * @param offsetLine
    * @param len
    */
   public void debugOffsetLineIsInside(int offsetLine, int len) {
      boolean isOffsetOk = 0 <= offsetLine && offsetLine < this.len;
      boolean isLenOk = len > 0 && (offsetLine + len) <= this.offset + this.len;
      if (!isOffsetOk) {
         //#debug
         toDLog().pAlways("msg", this, LineStringer.class, "debugExIsInside", LVL_05_FINE, false);
         throw new IllegalArgumentException("offsetLine " + offsetLine + " is not inside Line");
      }
      if (!isLenOk) {
         throw new IllegalArgumentException("length " + len + " is not inside Line (offset=" + offsetLine + ")");
      }
   }

   public void debugOffsetStringerIsInside(int offsetStringer) {
      if (!this.isInside(offsetStringer)) {
         //#debug
         toDLog().pAlways("msg", this, LineStringer.class, "debugExIsInside", LVL_05_FINE, false);
         throw new IllegalArgumentException("offsetStringer " + offsetStringer + " is not inside Line");
      }
   }

   public BufferObject deleteCharsFrom(int offsetLine) {
      BufferObject removedChars = model.removeChars(offsetLine);
      int size = removedChars.getSize();
      len -= size;
      for (int i = 0; i < size; i++) {
         CharAlgo ca = (CharAlgo) removedChars.get(i);
         this.pixelsW -= ca.getWidth();
      }
      return removedChars;
   }

   public void editorCharReplace(int offsetStringer, char c) {
      char[] ar = stringer.chars;
      int offsetline = this.getOffsetLineFromStringerOffset(offsetStringer);
      int offset = stringer.offsetChars + offsetStringer;
      ar[offset] = c;
   }

   public void enableCharWidths() {
      if (charsWidth == null) {
         charsWidth = new int[len];
      }
   }

   public BuildLineData getBuilder() {
      if (model == null) {
         throw new IllegalStateException("cannot call once build method set model to null");
      }
      return model;
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
      debugOffsetLineIsInside(indexRelative, len);
      if (charsWidth == null) {
         return len * widthMono;
      } else {
         int sum = 0;
         for (int i = 0; i < len; i++) {
            sum += charsWidth[indexRelative + i];
         }
         return sum;
      }
   }

   /**
    * 
    * @param offsetLineVisible relative to line offset
    * @return
    */
   public char getCharVisible(int offsetLineVisible) {
      if (map == null) {
         return stringer.getCharSourceAtRelative(this.offset + offsetLineVisible);
      } else {
         char[] charsMapped = map.getCharsMapped();
         return charsMapped[offsetLineVisible];
      }
   }

   public int getCharWidth(int indexLine) {
      if (charsWidth == null) {
         return widthMono;
      } else {
         return charsWidth[indexLine];
      }
   }

   /**
    * Returns the number of pixels consumed by all letters until index not included.
    * 
    * Therefore,
    * <li> when index is 0, returns 0
    * <li> when index is 1, returns the width of the first characters
    * @param indexLine
    * @return
    */
   public int getCharWidthConsumedUntil(int indexLine) {
      int len = indexLine;
      int startIndex = 0;
      int sum = getCharsWidthConsumed(startIndex, len);
      return sum;
   }

   public int getCharX(int offsetLine) {
      int m = 0;
      if (charsWidth == null) {
         m = offsetLine * widthMono;
      } else {
         for (int i = 0; i < offsetLine; i++) {
            int w = charsWidth[i];
            m += w;
         }
      }
      return x + m;
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

   /**
    * The length the line runs for into the Stringer buffer, this value included
    * hidden characters and does not include mapped strings that are bigger than 1.
    * 
    * <p>
    * It is the length in the source array
    * </p>
    * @return
    */
   public int getLengthInStringer() {
      return len;
   }

   public int getLineID() {
      return lineID;
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
    * Returns the number of char
    * 
    * @return
    */
   public int getNumCharVisible() {
      if (map == null) {
         return len;
      } else {
         return len + map.getSizeDiff();
      }
   }

   public int getNumOfSpaces() {
      return numOfSpaces;
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

   /**
    * Returns the offset relative to line.
    * 
    * 
    * @param offsetStringer
    * @return
    */
   public int getOffsetLineFromStringerOffset(int offsetStringer) {
      return offsetStringer - this.offset;
   }

   /**
    * 
    * @return
    */
   public int getOffsetLineLastChar() {
      return len - 1;
   }

   /**
    * 
    * @return
    */
   public int getOffsetStringerLastChar() {
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

   public int getWidthMono() {
      return widthMono;
   }

   /**
    * Return and compute the word intervals on this line
    * @return
    */
   public int[] getWordBreaks() {
      if (wordBreaks == null) {
         StringUtils strU = drc.getUC().getStrU();
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
    * 
    * <p>
    * charsWidth are enabled
    * </p>
    * @param offsetRel
    */
   public void incrementCharWidth(int offsetRel, int val) {
      charsWidth[offsetRel] += val;
      pixelsW += val;
   }

   public void incrementLen() {
      len++;
   }

   public void incrementLen(int incr) {
      len += incr;
   }

   public void incrementNumOfSpaces(int incr) {
      numOfSpaces += incr;
   }

   public boolean isFictiveLine() {
      return isFictiveLine;
   }

   public boolean isHasDifferentFonts() {
      return hasDifferentFonts;
   }

   /**
    * True if index relative to the {@link Stringer#getOffsetChar()} is inside this line.
    * @param indexStringer
    * @return
    */
   public boolean isInside(int indexStringer) {
      return offset <= indexStringer && (offset + len) > indexStringer;
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

   public boolean isSpaceOut() {
      return isSpaceOut;
   }

   /**
    * What if this char was mapped ?
    * @return
    */
   public CharAlgo removeLastChar() {
      CharAlgo lastc = model.removeLastChar();
      int wlast = lastc.getWidth();
      this.pixelsW -= wlast;
      len -= 1;
      return lastc;
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

   public void setLineID(int id) {
      this.lineID = id;
   }

   public void setNumOfSpaces(int numOfSpaces) {
      this.numOfSpaces = numOfSpaces;
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

   public void setSpaceOut() {
      isSpaceOut = true;
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

      dc.appendVarWithNewLine("x", x);
      dc.appendVarWithSpace("y", y);
      dc.appendVarWithSpace("pixelsW", pixelsW);
      dc.appendVarWithSpace("pixelsH", pixelsH);
      dc.appendVarWithSpace("widthMono", widthMono);
      dc.appendVarWithSpace("numOfSpaces", numOfSpaces);

      dc.appendVarWithNewLine("isMonospaced", isMonospaced);
      dc.appendVarWithSpace("isJustified", isJustified);
      dc.appendVarWithSpace("hasDifferentFonts", hasDifferentFonts);
      dc.appendVarWithSpace("isFictiveLine", isFictiveLine);
      dc.appendVarWithSpace("isRealModelLine", isRealModelLine);

      String val = new String(stringer.chars, stringer.offsetChars + offset, len);
      dc.appendVarWithNewLine("String", val);

      dc.nlLvl(model, "model");
      dc.nlLvl("charsWidth", charsWidth, 20);
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
      dc.append(getOffsetStringerLastChar());
      dc.append(']');

      dc.appendVarWithSpace("lineID", lineID);

   }

}
