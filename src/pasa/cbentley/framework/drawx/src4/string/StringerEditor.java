package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.helpers.StringBBuilder;
import pasa.cbentley.core.src4.utils.CharUtils;
import pasa.cbentley.core.src4.utils.StringUtils;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOFigString;
import pasa.cbentley.framework.drawx.src4.string.interfaces.ITechStringer;

/**
 * Edition always resets the Stringer offset to zero by creating a new array
 * 
 * @author Charles Bentley
 *
 */
public class StringerEditor extends ObjectDrw {

   private int              bufferExpansion = 10;

   private int              caretIndex;

   protected final Stringer stringer;

   public StringerEditor(Stringer stringer) {
      super(stringer.getDRC());
      this.stringer = stringer;
   }

   /**
    * Called by client when he wants to modify the string
    * <br>
    * 
    * TODO depending on style applicators.. update fx intervals
    * 
    * @param cs update char array
    * @param index
    * @param c
    */
   public void addChar(char[] cs, int index, char c) {
      stringer.chars = cs;
      stringer.stringMetrics.addChar(index, c);
      stringer.lengthChars++;
   }

   public void insertAreaChar(AreaChar ac, int index) {

   }

   /**
    * Returns the visual text [offsetStart,offsetEnd].
    * 
    * Maps hidden characters, {@link IBOFigString#FIG_STRING_FLAG_2_SHOW_HIDDEN_CHARS}
    * What do we copy ?
    * @param offsetStart
    * @param offsetEnd
    * @return
    */
   public String copy(int offsetStart, int offsetEnd) {
      StringBBuilder sb = new StringBBuilder(getUC());
      copyAppend(sb, offsetStart, offsetEnd);
      return sb.toString();
   }

   public void copyAppend(StringBBuilder sb, int offsetStart, int offsetEnd) {
      LineStringer[] lines = stringer.getMetrics().getLinesFromOffsets(offsetStart, offsetEnd);
      if (lines.length == 0) {

      } else if (lines.length == 1) {
         lines[0].appendCharFromOffsets(sb, offsetStart, offsetEnd);
      } else if (lines.length == 2) {
         int lineEndOffset = lines[0].getOffsetLast();
         lines[0].appendCharFromOffsets(sb, offsetStart, lineEndOffset);
         //copy the model stuff between current line and next line
         //if fictive line etc ?
         lines[1].appendCharFromOffsets(sb, 0, offsetEnd);
      } else {
         lines[0].appendCharFromOffsets(sb, offsetStart, lines[0].getOffsetLast());
         for (int i = 1; i < lines.length - 1; i++) {
            lines[i].appendCharFromOffsets(sb, 0, lines[i].getOffsetLast());
         }
         lines[lines.length-1].appendCharFromOffsets(sb, 0, offsetEnd);
      }

   }

   /**
    * If source char is protected, creates a new one and remove protected flag
    * {@link ITechStringer#FX_FLAG_06_PROTECTED_CHARS}
    * 
    * @param str
    * @param fx
    */
   public void append(String str, ByteObject fx) {
      int len = str.length();
      editionStartChecks(len);
      int offset = stringer.offsetChars + stringer.lengthChars;
      char[] charArray = str.toCharArray();
      System.arraycopy(charArray, 0, stringer.chars, offset, len);
      stringer.lengthChars += len;
      int layerID = 0;
      if (fx != null) {
         stringer.addInterval(offset, len, layerID, fx);
      }
   }

   /**
    * Appends Characters at the end of the buffer using the current style
    * 
    * We can know if a newline/break occurs.
    * if not, no need to compute breask again.
    * @param c
    */
   public void appendChar(char c) {
      editionStartChecks(1);
      int index = stringer.offsetChars + stringer.lengthChars;
      stringer.chars[index] = c;
      stringer.lengthChars++;
   }

   /**
    * Creates a new line
    * @param data
    * @param fx
    */
   public void appendLine(String data, ByteObject fx) {
      int len = data.length();
      editionStartChecks(len + 1); //+2 if windows new lines
      int offset = stringer.offsetChars;
      int destOffset = stringer.offsetChars + stringer.lengthChars;
      int offsetNewLine = destOffset;
      stringer.chars[offsetNewLine] = StringUtils.NEW_LINE;
      char[] charArray = data.toCharArray();

      System.arraycopy(charArray, 0, stringer.chars, offsetNewLine + 1, len);
      int totalLen = len + 1;
      stringer.lengthChars += totalLen;

      //start the interval after the newline
      int offsetInterval = destOffset;
      int layerID = 0;
      if (fx != null) {
         stringer.addInterval(offsetNewLine, totalLen, layerID, fx);
      }
   }

   public void deleteChar(int index) {
      editionStartChecks(1);
      CharUtils.shiftCharDown(stringer.chars, 1, index, stringer.lengthChars);
      stringer.lengthChars -= 1;
   }

   /**
    * Make sure the array is not protected anymore and that it can process additional
    * 
    */
   public void editionStartChecks(int sizeIncrease) {
      if (stringer.chars == null) {
         stringer.chars = new char[0];
         stringer.getStyleLayers();
      }
      if (stringer.isProtected() || stringer.offsetChars != 0) {
         //copy to new
         char[] newArray = new char[stringer.lengthChars + bufferExpansion];
         System.arraycopy(stringer.chars, stringer.offsetChars, newArray, 0, stringer.lengthChars);
         stringer.chars = newArray;
         stringer.offsetChars = 0;
         stringer.setProtected(false);
      }
      if (!hasSpaceForIncrease(sizeIncrease)) {
         int lenIncrease = sizeIncrease + bufferExpansion;
         stringer.chars = getUC().getMem().increaseCapacity(stringer.chars, lenIncrease);
      }
      stringer.resetFigure();
   }

   public int getCaretIndex() {
      return caretIndex;
   }

   public boolean hasSpaceForIncrease(int value) {
      if (stringer.lengthChars + value >= stringer.chars.length) {
         return false;
      }
      return true;
   }

   /**
    * Insert c at index
    * @param c
    * @param index
    */
   public void insertChar(char c, int index) {
      editionStartChecks(1);
      CharUtils.shiftCharUp(stringer.chars, 1, index, stringer.lengthChars);
      stringer.chars[index] = c;
      stringer.lengthChars += 1;

   }

   public void insertChar(char[] cs, int offset, int len, int index) {
      editionStartChecks(len);
      CharUtils.shiftCharUp(stringer.chars, len, index, stringer.lengthChars);
      System.arraycopy(cs, offset, stringer.chars, index, len);
      stringer.lengthChars += len;

   }

   public void replaceChar(char c, int index) {
      editionStartChecks(1);
      stringer.chars[index] = c;
      if (stringer.hasState(ITechStringer.STATE_18_FULL_MONOSPACE)) {
         //no need to reset if mono

         //TODO justified text is not mono
      } else {
         //check if old characters was same side
         stringer.resetFigure();
      }
   }

   public void setCaretIndex(int caretIndex) {
      this.caretIndex = caretIndex;
   }
}
