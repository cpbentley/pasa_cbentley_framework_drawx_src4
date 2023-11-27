package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.logging.ITechLvl;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.tech.ITechBox;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;
import pasa.cbentley.framework.drawx.src4.tech.ITechMask;

/**
 * A {@link StringLeaf} has a consistent {@link StringFx} applied to all its characters 
 * without line breaks ? If you have line fxs.. yes
 * 
 * if no line fx, A StringLeaf can encompasses line breaks / word breaks/ sentence breaks
 * 
 * What happens when you select characters from 2 different {@link StringLeaf}?
 * 
 * You create new intervals and the select style is applied 
 * 
 * <li> The sizes inside a {@link StringLeaf}
 * <li> {@link StringDraw}
 * 
 * @author Charles Bentley
 *
 */
public class StringLeaf extends ObjectDrw implements ITechFigure, IBOTypesDrw, ITechMask, ITechBox, ITechStringer {

   /**
    * The style to be applied to all the characters in this interval
    */
   private StringFx         fx;

   private ByteObject       boFx;

   /**
    * 
    */
   private int              len;

   /**
    * 0 is on the Stringer offset
    * 
    * offset of first char in the Stringer
    */
   private int              offset;

   /**
    * The {@link StringLeaf} in which
    * this.offset < parent.offset
    * this.len < parent.len
    */
   private StringLeaf       parent;

   protected final Stringer st;

   private int              states;

   private int              type;

   public StringLeaf(DrwCtx drc, Stringer st, ByteObject boFx) {
      super(drc);
      this.st = st;
      this.boFx = boFx;

   }

   /**
    * The {@link StringFx} for this interval of text.
    * Null if none defined for this interval
    * @return
    */
   public StringFx getFx() {
      return fx;
   }

   /**
    * Draws the {@link StringLeaf} on referential position x,y
    * @param g
    * @param x left coordinate
    * @param y top coordinate
    * @param leaf
    */
   public void drawStringLeaf(GraphicsX g, int x, int y) {
      StringLeaf leaf = this;
      StringFx fx = leaf.getFxDraw();
      //#debug
      drc.toStringCheckNull(fx);

      switch (fx.getMaskType()) {
         case FX_MASKDRAW_TYPE_0_NONE:
            drawStringLeaf_MaskNone(g, x, y, fx);
            break;
         case FX_MASKDRAW_TYPE_1_CHAR:
            drawStringLeaf_MaskChar(g, x, y, fx, fx.maskChar);
            break;
         case FX_MASKDRAW_TYPE_2_WORD:
            drawStringLeaf_MaskWord(g, x, y, fx, fx.maskWord);
            break;
         default:
            break;
      }
   }

   /**
    * TODO when underlined red wiggle. its an overlay artifact leaf style
    * does not change structure, it belongs to interval
    * ->background figure is also a underlay artifact leaf
    * TODO leading space area before the first character, tabs or space
    * TODO trail space area after the last character or newline \n.. empty but pixels can be decorated
    * Document model? when word breaks
    * TODO test line breaking and word breaking independantly
    * Called in the context of a "line"
    * TODO text justification on lines https://stackoverflow.com/questions/8524979/justify-text-in-java
    * 
    * @param g
    * @param x left coordinate
    * @param y top coordinate
    * @param leaf
    * @param fx
    */
   void drawStringLeaf_MaskNone(GraphicsX g, int x, int y, StringFx fx) {
      StringLeaf leaf = this;
      StringMetrics sm = st.getMetrics();
      //what kind of bg figure do we have? char,word,sentence,line, block
      //at this level, we care only about char,word and sentence
      int indexRelative = this.offset;
      if (fx.bgFigure != null) {
         int cw = sm.getCharWidth(indexRelative);
         int ch = sm.getCharHeight(indexRelative);
         drc.getFigureOperator().paintFigure(g, x, y, cw, ch, fx.bgFigure);
      }

      char[] data = st.getCharsRef();
      if (fx.getTypeStruct() == FX_STRUCT_TYPE_0_BASIC) {
         //now only deals with color variance
         g.setFont(fx.getFont());
         int offset = st.getCharsStart() + leaf.getOffset();
         if (fx.isColorStable()) {
            g.setColor(fx.getColor());
            g.drawChars(data, offset, leaf.getLen(), x, y, ANCHOR);
         } else {
            int count = 0;
            int len = leaf.getLen();
            while (count < len) {
               int numChars = fx.setColor(g, count);
               if (numChars < 0) {
                  numChars = 1;
               } else if (numChars > len) {
                  numChars = len;
               }
               g.drawChars(data, offset + count, numChars, x, y, ANCHOR);
               count += numChars;
            }
         }
      } else if (fx.getTypeStruct() == FX_STRUCT_TYPE_1_METRICS_X) {
         int count = 0;
         int offset = leaf.getOffset();
         int len = leaf.getLen();
         int cx = x;
         int cy = y;
         while (count < len) {
            int offsetLeaf = offset + count;
            int offsetData = st.getCharsStart() + count;
            char c = data[offsetData];

            cx += sm.getShiftX(offsetLeaf);
            cy += sm.getShiftY(offsetLeaf);

            fx.setColor(g, count);
            fx.setFont(g, count);

            g.drawChar(c, cx, cy, ITechBox.ANCHOR);

            //#debug
            g.toDLog().pDraw("Single Char " + c + " at [" + cx + "," + cy + "]", this, StringDraw.class, "drawChar", ITechLvl.LVL_05_FINE, true);
            count += 1;
         }
      }
   }

   void drawStringLeaf_MaskWord(GraphicsX g, int x, int y, StringFx fx, ByteObject mask) {

   }

   void drawStringLeaf_MaskChar(GraphicsX g, int x, int y, StringFx fx, ByteObject mask) {
      char[] data = st.getCharsRef();
      StringLeaf leaf = this;
      if (fx.getTypeStruct() == FX_STRUCT_TYPE_0_BASIC) {
         //now only deals with color variance
         g.setFont(fx.getFont());

         int offset = st.getCharsStart() + leaf.getOffset();
         if (fx.isColorStable()) {
            g.setColor(fx.getColor());
            g.drawChars(data, offset, leaf.getLen(), x, y, ANCHOR);
         } else {
            int count = 0;
            int len = leaf.getLen();
            while (count < len) {
               int numChars = fx.setColor(g, count);
               if (numChars < 0) {
                  numChars = 1;
               } else if (numChars > len) {
                  numChars = len;
               }
               g.drawChars(data, offset + count, numChars, x, y, ANCHOR);
               count += numChars;
            }
         }
      } else if (fx.getTypeStruct() == FX_STRUCT_TYPE_1_METRICS_X) {
         StringMetrics sm = st.getMetrics();

      }
   }

   /**
    * 
    * @return
    */
   public StringFx getFxDraw() {
      if (fx == null) {
         return st.getFx();
      }
      return fx;
   }

   public int getLen() {
      return len;
   }

   /**
    * The offset in {@link Stringer} at which this {@link StringLeaf} starts.
    * relative to the {@link Stringer} base offset
    * @return
    */
   public int getOffset() {
      return offset;
   }

   public StringLeaf getParent() {
      return parent;
   }

   /**
    * char,word,line
    * 
    * Is this leaf
    * @return
    */
   public int getType() {
      return type;
   }

   public void setLen(int len) {
      this.len = len;
   }

   public void setOffset(int offset) {
      this.offset = offset;
   }

   public void setParent(StringLeaf parent) {
      this.parent = parent;
   }

   public void setByteObjectFx(StringFx fx) {
      this.fx = fx;
   }

   public boolean contains(int index) {
      return index >= offset && index < offset + len;
   }

   public ByteObject getBoFx() {
      return boFx;
   }

   public void setBoFx(ByteObject boFx) {
      this.boFx = boFx;
   }
}
