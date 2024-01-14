package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;

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

   /**
    * 
    * @param data
    * @param fx
    */
   public void appendLine(String data, ByteObject fx) {
      if (stringer.chars == null) {
         stringer.chars = new char[0];
         stringer.getStyleLayers();
      }
      int len = data.length();
      int offset = stringer.lengthChars;
      char[] array = getUC().getMem().increaseCapacity(stringer.chars, len + 1);
      System.arraycopy(array, offset, data.toCharArray(), 0, len);
      stringer.chars = array;
      stringer.offsetChars = 0;
      stringer.chars[offset + len] = '\n';
      stringer.lengthChars = offset + len + 1;
      stringer.addInterval(offset, len + 1, 0, fx);
   }
}
