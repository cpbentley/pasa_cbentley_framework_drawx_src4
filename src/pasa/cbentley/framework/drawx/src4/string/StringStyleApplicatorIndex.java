package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;

/**
 * Index application.
 * <li>Fx for the 1st word of each sentence. 
 * <li>Fx for the 1st character of each word. 
 * 
 * 
 * X letter in the {@link Stringer}
 * 
 * @author Charles Bentley
 *
 */
public class StringStyleApplicatorIndex extends StringStyleApplicator {

   private int index;

   private int scope;

   private boolean isLast;
   
   public StringStyleApplicatorIndex(DrwCtx drc) {
      super(drc);
   }

   public int getIndex() {
      return index;
   }

   public String getName() {
      return "index:" + getIndex();
   }

   public void setIndex(int index) {
      this.index = index;
   }

   public int getScope() {
      return scope;
   }

   /**
    * Scope of the index value
    * <li> 
    * @param scope
    */
   public void setScope(int scope) {
      this.scope = scope;
   }

   public boolean isLast() {
      return isLast;
   }

   /**
    * When true, index is computed from last to start
    * @param isLast
    */
   public void setLast(boolean isLast) {
      this.isLast = isLast;
   }
}
