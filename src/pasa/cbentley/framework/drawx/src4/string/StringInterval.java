package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.structs.IntBuffer;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;

/**
 * A {@link StringInterval} has a {@link StringFx} overlay applied to all its {@link StringLeaf}.
 * <br>
 * 
 * Q: Is <b>public</b> in <code>public class ... </code> a StringInterval?<br>
 * A: It belongs to a {@link Stringer}
 * <br>
 * <br>
 * 
 * Q: What happens when you select characters from 2 different {@link StringInterval}? <br>
 * A: You create new intervals and the select style is applied
 * <br>
 * <br>
 * 
 * A word in bold with have its own {@link StringInterval}
 * 
 * {@link StringInterval} is the user model, used to model 
 * {@link StringLeaf} is the code model used to draw
 * 
 * When a {@link StringInterval} is changed, it modifies all the {@link StringLeaf} 
 * @author Charles Bentley
 *
 */
public class StringInterval extends ObjectDrw implements IBOFxStr {

   ByteObject               fx;

   private int              len;

   private int              offset;

   /**
    * The {@link StringInterval} in which
    * this.offset < parent.offset
    * this.len < parent.len
    */
   private StringInterval   parent;

   protected final Stringer st;

   public StringInterval(DrwCtx drc, Stringer st) {
      super(drc);
      this.st = st;

   }

   /**
    * The {@link StringFx} for this interval of text
    * @return
    */
   public ByteObject getFx() {
      return fx;
   }

   public int getLen() {
      return len;
   }
   
   public void setFx(ByteObject fx) {
      this.fx=fx;
   }

   /**
    * The offset in {@link Stringer} at which this {@link StringInterval} starts.
    * @return
    */
   public int getOffset() {
      return offset;
   }

   public int getOffsetEnd() {
      return offset + len;
   }

   public StringInterval getParent() {
      return parent;
   }

   public void setLen(int len) {
      this.len = len;
   }

   public void setOffset(int offset) {
      this.offset = offset;
   }

   /**
    * True when index is inside the {@link StringInterval}
    * @param index
    * @return
    */
   public boolean isInside(int index) {
      return offset <= index && (offset + len) < index;
   }

   /**
    * 
    * 10:8 and offset=15 return 18-15 = 3
    * 10:8 and offset=10 return 18-10 = 8 = len
    * @param offset
    * @return
    */
   public int getLeftCenter(int offset) {
      return this.offset + this.len - offset;
   }

   public void setParent(StringInterval parent) {
      this.parent = parent;
   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, StringInterval.class, "@line5");
      toStringPrivate(dc);
      super.toString(dc.sup());
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, StringInterval.class);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   private void toStringPrivate(Dctx dc) {

   }

   //#enddebug

}
