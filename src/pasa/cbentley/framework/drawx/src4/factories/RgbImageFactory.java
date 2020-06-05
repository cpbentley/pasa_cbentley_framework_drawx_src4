/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.framework.coredraw.src4.interfaces.IGraphics;
import pasa.cbentley.framework.coredraw.src4.interfaces.IImage;
import pasa.cbentley.framework.coredraw.src4.interfaces.IMFont;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;
import pasa.cbentley.framework.drawx.src4.tech.ITechBox;

public class RgbImageFactory extends AbstractDrwFactory {

   public RgbImageFactory(DrwCtx drc) {
      super(drc);
   }


   public RgbImage getCharImage(int color, char c, IMFont f, int bgColor) {
      int wi = f.charWidth(c);
      int hi = f.getHeight();
      RgbImage figImg = drc.getCache().createPrimitiveRgb(wi, hi, bgColor);
      GraphicsX figGraphics = figImg.getGraphicsX();
      figGraphics.setColor(0);
      figGraphics.setFont(f);
      figGraphics.drawChar(c, 0, 0, ITechBox.ANCHOR);
      figImg.disposeGraphics();
      return figImg;
   }

   public IImage[] getCharImages(String str, int color, IMFont f) {
      IImage[] imgs = new IImage[str.length()];
      int h = f.getHeight();
      for (int i = 0; i < imgs.length; i++) {
         char c = str.charAt(i);
         int w = f.charWidth(c);
         IImage ic = drc.getImageFactory().createImage(w, h);
         IGraphics g = ic.getGraphics();
         g.setColor(color);
         g.setFont(f);
         g.drawChar(c, 0, 0, IGraphics.TOP | IGraphics.LEFT);
         imgs[i] = ic;
      }
      return imgs;
   }
}
