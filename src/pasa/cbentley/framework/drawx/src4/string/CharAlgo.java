package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.strings.CharMapper;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;

public class CharAlgo extends ObjectDrw {

   private char    c;

   private int     height;

   private boolean isEther;

   private int     offsetLine;

   private int     offsetStringer;

   private int     width;

   private int     mapOp;

   private String  mapData;

   private char    mapChar;

   public CharAlgo(DrwCtx drc) {
      super(drc);
   }

   public char getC() {
      return c;
   }

   public int getHeight() {
      return height;
   }

   public int getOffsetLine() {
      return offsetLine;
   }

   public void setMapRemove() {
      this.mapOp = CharMapper.OP_2_REMOVE;
   }

   public void setMapReplaceChar(char c) {
      this.mapOp = CharMapper.OP_1_REPLACE_ONE;
      this.mapChar = c;
   }

   /**
    * 
    * @param op
    * @param offsetStringer
    * @param data
    */
   public void setMapData(int op, int offsetStringer, String data) {
      this.mapOp = op;
      this.offsetStringer = offsetStringer;
      this.mapData = data;
   }

   public int getOffsetStringer() {
      return offsetStringer;
   }

   public int getWidth() {
      return width;
   }

   public void setC(char c) {
      this.c = c;
   }

   public void setEther() {
      isEther = true;
   }

   public void setHeight(int height) {
      this.height = height;
   }

   public void setOffsetLine(int offsetLine) {
      this.offsetLine = offsetLine;
   }

   public void setOffsetStringer(int offsetStringer) {
      this.offsetStringer = offsetStringer;
   }

   public void setWidth(int width) {
      this.width = width;
   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, CharAlgo.class, 16);
      toStringPrivate(dc);
      super.toString(dc.sup());
      dc.appendVarWithNewLine("height", height);

      dc.appendVarWithNewLine("offsetStringer", offsetStringer);
      dc.appendVarWithSpace("offsetLine", offsetLine);

      dc.appendVarWithNewLine("isEther", isEther);
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, CharAlgo.class);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   private void toStringPrivate(Dctx dc) {
      dc.appendVarWithSpace("char", String.valueOf(c));
      dc.appendVarWithSpace("width", width);
   }
   //#enddebug

   public CharMapper addToMap(CharMapper map) {
      if (map != null) {
         addToMap1(map);
      } else {
         if (mapOp != 0) {
            map = new CharMapper(getUC());
            addToMap1(map);
         }
      }
      return map;

   }

   public void addToMap1(CharMapper map) {
      if (mapOp != 0) {
         if (mapOp == CharMapper.OP_1_REPLACE_ONE) {
            map.opReplaceOne(offsetStringer, mapChar);
         } else if (mapOp == CharMapper.OP_2_REMOVE) {
            map.opRemove(offsetStringer);
         } else if (mapOp == CharMapper.OP_3_REPLACE_SEVERAL) {
            map.opReplaceWith(offsetStringer, mapData);
         } else if (mapOp == CharMapper.OP_4_ADD_ONE) {
            map.opAddChar(offsetStringer, mapChar);
         } else if (mapOp == CharMapper.OP_5_ADD_SEVERAL) {
            map.opAddChars(offsetStringer, mapData);
         }
      }

   }

}
