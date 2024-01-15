package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.utils.StringUtils;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;

/**
 * Edition always resets the Stringer offset to zero by creating a new array
 * 
 * @author Charles Bentley
 *
 */
public class StringerEditor extends ObjectDrw {

   protected final Stringer stringer;

   public StringerEditor(Stringer stringer) {
      super(stringer.getDRC());
      this.stringer = stringer;
   }

   /**
    * Appends Characters at the end of the buffer using the current style
    * 
    * We can know if a newline/break occurs.
    * if not, no need to compute breask again.
    * @param c
    */
   public void appendChar(char c) {
      stringer.chars = drc.getUCtx().getMem().increaseCapacity(stringer.chars, 3);
      int index = stringer.offsetChars + stringer.lengthChars;
      stringer.chars[index] = c;
      stringer.stringMetrics.addChar(index, c);
      stringer.lengthChars++;
   }

   /**
    * Insert c at index
    * @param c
    * @param index
    */
   public void addChar(char c, int index) {

   }

   /**
    * Called by client when he wants to modify the string
    * <br>
    * Updates the breaks
    * <br>
    * To initialize Stringer with a string,
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

   /**
    * Only works if figure has its own text.
    * @param textFigure
    */
   public void append(ByteObject textFigure) {
      this.append(textFigure, null);
   }

   /**
    * Appends the textFigure with the given fx
    * @param textFigure if null nothing is appended.
    * @param fx if null use textFigure only
    */
   public void append(ByteObject textFigure, ByteObject fx) {
      // TODO Auto-generated method stub

   }

   /**
    * If source char is protected, creates a new one and remove protected flag
    * {@link ITechStringer#FX_FLAG_06_PROTECTED_CHARS}
    * 
    * @param str
    * @param fx
    */
   public void append(String str, ByteObject fx) {
      // TODO Auto-generated method stub

   }

   private int bufferExpansion = 10;

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
   }

   public boolean hasSpaceForIncrease(int value) {
      if (stringer.lengthChars + value >= stringer.chars.length) {
         return false;
      }
      return true;
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
      stringer.addInterval(offsetNewLine, totalLen, layerID, fx);
   }
}
