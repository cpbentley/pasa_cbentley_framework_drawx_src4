package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;

public class TabColumnStringer  extends ObjectDrw {

   private Stringer stringer;

   private int numCharacters;
   
   private int offset;
   
   public TabColumnStringer(Stringer stringer) {
      super(stringer.getDRC());
      this.stringer = stringer;
   }

   public int getNumCharacters() {
      return numCharacters;
   }

   public void setNumCharacters(int numCharacters) {
      this.numCharacters = numCharacters;
   }

   /**
    * Where does the column starts in the line
    * @return
    */
   public int getOffset() {
      return offset;
   }

   /**
    * 
    * @param offset
    */
   public void setOffset(int offset) {
      this.offset = offset;
   }
  
}
