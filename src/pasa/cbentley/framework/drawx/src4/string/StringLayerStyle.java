package pasa.cbentley.framework.drawx.src4.string;

import java.util.Enumeration;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.structs.IntInterval;
import pasa.cbentley.core.src4.structs.IntIntervals;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrw;
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
 * From several {@link StringLayerStyle}, the {@link StringLeaf} are created.
 * 
 * <br>
 * <br>
 * 
 * A {@link StringLayerStyle} may have a single {@link ByteObject} fx for all its
 * 
 * @author Charles Bentley
 *
 */
public class StringLayerStyle extends ObjectDrw {

   private String           name;

   private boolean          isActive;

   /**
    * Tracks the intervals for the Dynamic Styles.
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

   public StringLayerStyle(DrwCtx drc, Stringer st) {
      super(drc);
      this.st = st;
      intervals = new IntIntervals(drc.getUCtx());
   }

   public boolean isActive() {
      return isActive;
   }

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

      //merge listener .. merge only when same style
      IntInterval ii = intervals.addInterval(offset, len);
      ii.setPayload(fx);
      return ii;
   }

   public void setActive(boolean isActive) {
      this.isActive = isActive;
   }

   public void removeAllIntervals() {
      intervals.clear();
   }

   /**
    * Removes all and add this interval
    * @param offset
    * @param len
    * @param fx
    * @return
    */
   public IntInterval setInterval(int offset, int len, ByteObject fx) {
      intervals.clear();
      IntInterval ii = intervals.addInterval(offset, len);
      ii.setPayload(fx);
      return ii;
   }

}
