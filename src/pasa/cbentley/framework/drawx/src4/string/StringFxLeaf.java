package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.logging.ITechLvl;
import pasa.cbentley.core.src4.structs.IntInterval;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrawX;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOBox;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOMask;
import pasa.cbentley.framework.drawx.src4.string.interfaces.ITechStringer;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;

/**
 * A {@link StringFxLeaf} has a consistent {@link StringFx} applied to all its characters.
 * 
 * It is the sum of FXs of the layers {@link StringStyleLayer} 
 * 
 * The {@link Stringer} simply draw all {@link StringFxLeaf} serially at the {@link IntInterval}
 * 
 * <p>
 * What about line breaks which change lines and words potentially ?
 * 
 * without line breaks ? If you have line fxs.. yes
 * 
 * if no line fx, A StringLeaf can encompasses line breaks / word breaks/ sentence breaks
 * </p>
 * 
 * A {@link StringFxLeaf} can encompass words and lines
 * If a background is to be drawn over a non rectangular shape ? Reason it must be full lines or not.
 * You cannot have a leaf over 1.5 line. You will need a leaf over 1 and another over 0.5 ?
 * 
 * <p>
 * What happens when you select characters from 2 different {@link StringFxLeaf}? It creates a new {@link IntInterval}
 * on the selection layer {@link StringStyleLayer}. This invalidates fx states and a rebuild is done.
 * The building phase creates 2 new intervals each with a new {@link StringFxLeaf}, adjacent leaves are updated.
 * Same when an interval is removed, adjacent leaves offsets and lengths are updated or merged together
 * </p>
 * 
 * You create new intervals and the select style is applied 
 * 
 * <li> The sizes inside a {@link StringFxLeaf}
 * <li> {@link StringerDraw}
 * 
 * @author Charles Bentley
 *
 */
public class StringFxLeaf extends ObjectDrw implements ITechFigure, IBOTypesDrawX, IBOMask, IBOBox, ITechStringer {

   /**
    * The style to be applied to all the characters in this interval
    */
   private StringFx         fx;

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
    * The {@link StringFxLeaf} in which
    * <li>this.offset < parent.offset
    * <li>this.len < parent.len
    */
   private StringFxLeaf     parent;

   protected final Stringer st;

   private int              states;

   private int              type;

   public StringFxLeaf(DrwCtx drc, Stringer st) {
      super(drc);
      this.st = st;
   }

   /**
    * 
    * @param drc
    * @param st
    * @param fx cannot be null
    */
   public StringFxLeaf(DrwCtx drc, Stringer st, StringFx fx) {
      super(drc);
      this.st = st;
      if (fx == null) {
         throw new NullPointerException();
      }
      this.fx = fx;
   }

   public boolean contains(int index) {
      return index >= offset && index < offset + len;
   }



   void drawStringLeaf_MaskChar(GraphicsX g, int x, int y, StringFx fx, ByteObject mask) {
      char[] data = st.getCharsRef();
      StringFxLeaf leaf = this;
      if (fx.getTypeStruct() == FX_STRUCT_TYPE_0_BASIC_HORIZONTAL) {
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
    * TODO when underlined red wiggle. its an overlay artifact leaf style
    * does not change structure, it belongs to interval
    * ->background figure is also a underlay artifact leaf
    * 
    * TODO leading space area before the first character, tabs or space
    * 
    * TODO trail space area after the last character or newline \n.. empty but pixels can be decorated
    * on the line fx ?
    *
    * Document model? when word breaks
    * TODO test line breaking and word breaking independantly
    * Called in the context of a "line"
    * 
    * TODO text justification on lines https://stackoverflow.com/questions/8524979/justify-text-in-java
    * 
    * @param g
    * @param x left coordinate
    * @param y top coordinate
    * @param leaf
    * @param fx
    */
   void drawStringLeaf_MaskNone(GraphicsX g, int x, int y, StringFx fx) {
      StringFxLeaf leaf = this;
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
      if (fx.getTypeStruct() == FX_STRUCT_TYPE_0_BASIC_HORIZONTAL) {
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

            g.drawChar(c, cx, cy, IBOBox.ANCHOR);

            //#debug
            g.toDLog().pDraw("Single Char " + c + " at [" + cx + "," + cy + "]", this, StringerDraw.class, "drawChar", ITechLvl.LVL_05_FINE, true);
            count += 1;
         }
      }
   }

   void drawStringLeaf_MaskWord(GraphicsX g, int x, int y, StringFx fx, ByteObject mask) {

   }

   /**
    * The {@link StringFx} for this interval of text. Computed  by ?
    * 
    * @return Null if none is defined yet for this interval
    */
   public StringFx getFx() {
      return fx;
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
    * The offset in {@link Stringer} at which this {@link StringFxLeaf} starts.
    * Attention this offset is relative to the {@link Stringer} base offset
    * @return
    */
   public int getOffset() {
      return offset;
   }

   public StringFxLeaf getParent() {
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

   public void setByteObjectFx(StringFx fx) {
      this.fx = fx;
   }

   public void setLen(int len) {
      this.len = len;
   }

   public void setOffset(int offset) {
      this.offset = offset;
   }

   public void setParent(StringFxLeaf parent) {
      this.parent = parent;
   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, StringFxLeaf.class, "@line5");
      toStringPrivate(dc);
      super.toString(dc.sup());
      dc.nlLvl(parent, "parentStringFxLeaf");
      dc.nlLvl(fx, "StringFx");
      dc.nlLvl(st, "parentStringer");
   }

   private void toStringPrivate(Dctx dc) {
      dc.appendVarWithSpace("hashcode", this.hashCode());
      dc.appendVarWithSpace("offset", offset);
      dc.appendVarWithSpace("len", len);
      dc.appendVarWithSpace("type", type);
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, StringFxLeaf.class);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   //#enddebug
   

}
