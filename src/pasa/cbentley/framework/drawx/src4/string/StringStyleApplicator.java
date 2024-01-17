package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.structs.IntInterval;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IStyleApplicator;

/**
 * 
 * Finds textual elements in the {@link Stringer}, creates a {@link StringStyleLayer} on which
 * its generates  for which the {@link StringFx} 
 * 
 * Every text modification, the Applicator must update its text intervals
 * Sub classes implements
 * @author Charles Bentley
 *
 */
public abstract class StringStyleApplicator extends ObjectDrw implements IStyleApplicator {

   protected Stringer       stringer;

   private ByteObject     fxSrc;

   /**
    * 
    */
   private int              layerID;

   private StringStyleLayer layer;

   public void reset() {
      if (layer != null) {
         layer.removeAllIntervals();
      }
   }

   /**
    * When not null, the applicator only works on the interval applied on the Stringer offset/len
    */
   protected IntInterval interval;

   public StringStyleApplicator(DrwCtx drc) {
      super(drc);
   }

   /**
    * optimization when text is modified.
    * applicator will react differently based on their purpose.
    * @param value
    */
   public void shiftIntervals(int value) {

   }

   public void recheckAround(int offset) {
      //called when a char has been added
   }

   public Stringer getStringer() {
      return stringer;
   }

   public void setStringer(Stringer stringer) {
      this.stringer = stringer;
   }

   public void init() {
      if (stringer == null) {
         throw new NullPointerException();
      }

      String name = getName();

      setLayer(stringer.createLayer(name));

   }

   public abstract String getName();

   public StringStyleLayer getLayer() {
      return layer;
   }

   public void setLayer(StringStyleLayer layer) {
      this.layer = layer;
   }

   public ByteObject getFxSrc() {
      return fxSrc;
   }

   public void setFxSrc(ByteObject fxSrc) {
      this.fxSrc = fxSrc;
   }

}
