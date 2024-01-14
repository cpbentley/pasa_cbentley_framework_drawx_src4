package pasa.cbentley.framework.drawx.src4.string;

import java.util.Enumeration;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesDrw;
import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.structs.IntInterval;
import pasa.cbentley.core.src4.structs.IntIntervals;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;

/**
 * A group of {@link StringInterval} for a Stringer.
 * 
 * <li> base set of {@link StringInterval} for complex styled document
 * <li> selection overlay
 * <li> highlight overlay for search
 * <li> highlight overlay for other styles
 * <li> highlight style overlay current line
 * 
 * From several {@link StringStyleLayer}, the {@link StringFxLeaf} are created.
 * 
 * <br>
 * <br>
 * 
 * A {@link StringStyleLayer} may have a single {@link ByteObject} fx for all its
 * 
 * @author Charles Bentley
 *
 */
public class StringStyleLayer extends ObjectDrw {

   private String           name;

   private boolean          isActive;

   private int              id;

   /**
    * Tracks the intervals for the Dynamic Styles.  {@link IBOFxStr#FX_FLAGZ_2_DYNAMIC}
    * <br>
    * {@link IDrwTypes#TYPE_070_TEXT_EFFECTS} already affecting an index will merge with the dynamic style.
    * <br>
    * <br>
    * By Default Dynamic text effect merges over, which means it replaces static style definitions
    * <br>
    * Created on demand. So by default extra fxs are not loaded since most String items will use only the default
    * styling until the user makes an action to modify.
    * <br>
    * 
    * Sorted by construction
    */

   private IntIntervals     intervals;

   protected final Stringer st;

   public StringStyleLayer(DrwCtx drc, Stringer st, int id) {
      super(drc);
      this.st = st;
      this.id = id;
      intervals = new IntIntervals(drc.getUCtx());
      intervals.setPayLoadCheck(true);
   }

   public boolean isActive() {
      return isActive;
   }

   /**
    * 
    * @return {@link Enumeration} of {@link IntInterval}
    */
   public Enumeration getIntervals() {
      return intervals.getIntervalEnumeration();
   }

   public int getSize() {
      return intervals.getSize();
   }

   /**
    * Clears the given interval
    * @param offset
    * @param len
    */
   public void removeInterval(int offset, int len) {
      intervals.removeInterval(offset, len);
   }

   /**
    * Returns the {@link IBOTypesDrw#TYPE_070_TEXT_EFFECTS} for the given interval at offset
    * @param offset
    * @return null if interval defined at index
    */
   public ByteObject getStyle(int offset) {
      IntInterval inter = intervals.getIntervalIntersect(offset);
      if(inter == null) {
         return null;
      } else  {
         return (ByteObject) inter.getPayload();
      }
   }
   /**
    * Remove only if found the exact offset-len combo
    * @param val
    * @return true if found. false otherwise
    */
   public boolean removeInterval(IntInterval val) {
      return intervals.removeIntervalExact(val);
   }

   /**
    * Creates a new {@link StringInterval} overlaying current style with the given styleID
    * 
    * This creates new intervals based on the {@link ByteObject} definition
    * @param offset
    * @param len
    * @param styleID
    * @return
    */
   public IntInterval addInterval(int offset, int len, ByteObject fx) {
      //#debug
      fx.checkType(IBOTypesDrw.TYPE_070_TEXT_EFFECTS);

      IntInterval interval =  new IntInterval(getUC(), offset, len, fx);
      //merge listener .. merge only when same style
      intervals.addInterval(interval);
      //two possibilities. either everything is computed and we invalidates
      st.setState(ITechStringer.STATE_17_COMPUTED_FX, false);
      return interval;
   }

   public void setActive(boolean isActive) {
      this.isActive = isActive;
   }

   public void removeAllIntervals() {
      intervals.clear();
   }

   /**
    * Removes all and add this interval on this layer.
    * 
    * @param offset
    * @param len
    * @param fx
    * @return
    */
   public IntInterval removeAllAndSetInterval(int offset, int len, ByteObject fx) {
      intervals.clear();
      IntInterval ii = intervals.addInterval(offset, len);
      ii.setPayload(fx);
      return ii;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int getId() {
      return id;
   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, StringStyleLayer.class, "@line5");
      toStringPrivate(dc);
      super.toString(dc.sup());
      dc.nlLvl(intervals, "intervals");
      dc.nlLvl(st, "Stringer");
   }

   private void toStringPrivate(Dctx dc) {
      dc.appendVarWithSpace("name",name);
      dc.appendVarWithSpace("id",id);
      dc.appendVarWithSpace("isActive",isActive);
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, StringStyleLayer.class);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   //#enddebug
   

}
