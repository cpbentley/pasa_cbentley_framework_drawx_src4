package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.structs.BufferObject;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;

public class BuildLineData extends ObjectDrw {

   private BufferObject buf;

   private int          lastSpaceIndex = -2;

   public BuildLineData(DrwCtx drc) {
      super(drc);
      buf = new BufferObject(drc.getUC());
   }

   public void addChar(char c, int w, int offsetLine) {
      CharAlgo ca = new CharAlgo(drc);
      ca.setC(c);
      ca.setWidth(w);
      ca.setOffsetStringer(offsetLine);
      buf.add(ca);
   }

   public void addChar(CharAlgo ca) {
      buf.add(ca);
   }

   public void addSpace(char c, int w, int offsetLine) {
      addChar(c, w, offsetLine);
      lastSpaceIndex = offsetLine;
   }

   public BufferObject getChars() {
      return buf;
   }

   public CharAlgo getLastChar() {
      return (CharAlgo) buf.getLast();
   }

   /**
    * offsetline of the last space character
    * -1 if no spaces
    * @return
    */
   public int getLastSpaceIndex() {
      if (lastSpaceIndex == -2) {
         //must be computed
         lastSpaceIndex = getOffsetLineLastChar(' ');
      }
      return lastSpaceIndex;
   }

   /**
    * 
    * @param c
    * @return
    */
   public int getOffsetLineLastChar(char c) {
      int size = buf.getSize();
      for (int i = size - 1; i >= 0; i--) {
         CharAlgo ca = (CharAlgo) buf.get(i);
         char ccc = ca.getC();
         if (ccc == c) {
            return ca.getOffsetLine();
         }
      }
      return -1;
   }

   public BufferObject removeChars(int offsetLine) {
      BufferObject rb = buf.removeAllForIndex(offsetLine);
      //stats are not valid anymore
      return rb;
   }

   public CharAlgo removeLastChar() {
      return (CharAlgo) buf.removeLast();
   }

   public void setChars(BufferObject bo) {
      this.buf = bo;
   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, BuildLineData.class, 40);
      toStringPrivate(dc);
      super.toString(dc.sup());

      dc.nlLvl(buf, "buf");
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, BuildLineData.class);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   private void toStringPrivate(Dctx dc) {

   }
   //#enddebug

}
