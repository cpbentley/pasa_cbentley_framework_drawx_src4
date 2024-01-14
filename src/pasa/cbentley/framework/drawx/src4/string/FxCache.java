package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.utils.ByteObjectUtilz;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;

/**
 * 
 * @author Charles Bentley
 *
 */
public class FxCache extends ObjectDrw {

   public FxCache(DrwCtx drc) {
      super(drc);
      cacheTop = new ByteObject[4];
      cacheBot = new ByteObject[4];
      cacheMerge = new ByteObject[4];
   }

   private int          cacheSize;

   private ByteObject[] cacheTop;

   private ByteObject[] cacheBot;

   private ByteObject[] cacheMerge;

   public ByteObject getFxCacheMerge(ByteObject boFxBelow, ByteObject boFxTop) {
      for (int i = 0; i < cacheTop.length; i++) {
         if (cacheTop[i] == boFxTop && cacheBot[i] == boFxBelow) {
            if (cacheMerge[i] == null) {
               cacheMerge[i] = drc.getFxStringOperator().mergeTxtEffects(boFxBelow, boFxTop);
            }
            return cacheMerge[i];
         }
      }
      ByteObject boFxMerged = drc.getFxStringOperator().mergeTxtEffects(boFxBelow, boFxTop);
      //not in cache add it
      if (cacheSize + 1 >= cacheTop.length) {
         ByteObjectUtilz bou = drc.getBOC().getBOU();
         cacheTop = bou.increaseCapacity(cacheTop, 3);
         cacheBot = bou.increaseCapacity(cacheBot, 3);
         cacheMerge = bou.increaseCapacity(cacheMerge, 3);
      }
      int newIndex = cacheSize;
      cacheTop[newIndex] = boFxTop;
      cacheBot[newIndex] = boFxBelow;
      cacheMerge[newIndex] = boFxMerged;
      return boFxMerged;
   }
}
