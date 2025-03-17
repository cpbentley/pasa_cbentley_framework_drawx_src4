package pasa.cbentley.framework.drawx.src4.engine;

import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;

public class GraphicsXFactory extends ObjectDrw {

   public GraphicsXFactory(DrwCtx drc) {
      super(drc);
   }

   public GraphicsX createGraphicsX() {
      return new GraphicsX(drc);
   }

   public GraphicsX createGraphicsX(RgbImage rgbImg, boolean isNull) {
      return new GraphicsX(drc, rgbImg, isNull);
   }

   public GraphicsX createGraphicsX(RgbImage rgbImg, int paintMode) {
      return new GraphicsX(drc, rgbImg, paintMode);
   }

   public GraphicsX createGraphicsX(RgbImage rgbImg, int paintMode, int x, int y, int w, int h) {
      return new GraphicsX(drc, rgbImg, paintMode, x, y, w, h);
   }
}
