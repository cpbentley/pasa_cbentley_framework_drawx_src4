package pasa.cbentley.framework.drawx.src4.utils;

import java.util.Random;

import pasa.cbentley.core.src4.ctx.ObjectU;
import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.structs.BufferObject;

public class RectangleGen extends ObjectU {

   private int          minArea = 10;

   private BufferObject rectangles;

   private Random       random;

   public RectangleGen(UCtx uc) {
      super(uc);
      random = uc.getRandom();
      rectangles = new BufferObject(uc);
   }

   public void makeRects(int x, int y, int w, int h) {
      int area = w * h;
      if (isShouldDivideArea(area)) {
         int rs = random.nextInt(w + h);
         if (rs < w) {
            //split vert
            int nextW = random.nextInt(w);
            makeRects(x, y, nextW, h);
            makeRects(x + nextW, y, w - nextW, h);
         } else {
            int nextH = random.nextInt(h);
            makeRects(x, y, w, nextH);
            makeRects(x, y + nextH, w, h - nextH);
         }
      } else {
         rectangles.add(new Rect(x, y, w, h));
      }
   }

   public boolean isShouldDivideArea(int area) {
      if (area <= minArea) {
         return false;
      }
      return true;
   }

}
