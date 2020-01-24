package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.utils.ColorUtils;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;
import pasa.cbentley.framework.drawx.src4.tech.ITechSkew;
import pasa.cbentley.framework.drawx.src4.tech.ITechSkewer;

public class SkewImplementationJava implements ITechSkewer {

   public int         edgeAction    = ITechSkew.SKEW_EDGE_0_ZERO;

   public int         interpolation = ITechSkew.SKEW_TYPE_1_BILINEAR;

   /**
    * Minimum 1
    */
   public int         fuzzyBorder   = 1;

   protected int[]    transformedSpace;

   protected int[]    originalSpace;

   /**
    * Read from {@link ITechSkewer#SKEWER_OFFSET_08_COLOR_MOD4}
    */
   public int         colorPixel;

   private float      x0, y0, x1, y1, x2, y2, x3, y3;

   private float      dx1, dy1, dx2, dy2, dx3, dy3;

   private float      A, B, C, D, E, F, G, H, I;

   private RgbImage   src;

   private RgbImage   dst;

   private DrwCtx drc;

   public SkewImplementationJava(DrwCtx drc, RgbImage src) {

      this.drc = drc;
      this.src = src;

   }

   public SkewImplementationJava(DrwCtx drc, RgbImage src, ByteObject tech) {
      this.drc = drc;
      this.src = src;
      interpolation = tech.get1(SKEWER_OFFSET_03_INTERPOLATION_TYPE1);
      edgeAction = tech.get1(SKEWER_OFFSET_02_EDGE_TYPE1);
      fuzzyBorder = tech.get1(SKEWER_OFFSET_03_INTERPOLATION_TYPE1);
      colorPixel = tech.get4(SKEWER_OFFSET_08_COLOR_MOD4);
      if (fuzzyBorder < 1) {
         fuzzyBorder = 1;
      }

   }

   /**
    *  // keep the upper left corner as it is
            0,0, // UL
   
            // push the upper right corner more to the bottom
            image.getWidth(),20, // UR
   
            // push the lower right corner more to the left
            image.getWidth()-45,image.getHeight(), // LR
   
            // push the lower left corner more to the right
            55,image.getHeight()); // LL
    * @param x0
    * @param y0
    * @param x1
    * @param y1
    * @param x2
    * @param y2
    * @param x3
    * @param y3
    * @return
    */
   public RgbImage setCorners(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
      this.x0 = x0;
      this.y0 = y0;
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
      this.x3 = x3;
      this.y3 = y3;

      dx1 = x1 - x2;
      dy1 = y1 - y2;
      dx2 = x3 - x2;
      dy2 = y3 - y2;
      dx3 = x0 - x1 + x2 - x3;
      dy3 = y0 - y1 + y2 - y3;

      float a11, a12, a13, a21, a22, a23, a31, a32;

      if (dx3 == 0 && dy3 == 0) {
         a11 = x1 - x0;
         a21 = x2 - x1;
         a31 = x0;
         a12 = y1 - y0;
         a22 = y2 - y1;
         a32 = y0;
         a13 = a23 = 0;
      } else {
         a13 = (dx3 * dy2 - dx2 * dy3) / (dx1 * dy2 - dy1 * dx2);
         a23 = (dx1 * dy3 - dy1 * dx3) / (dx1 * dy2 - dy1 * dx2);
         a11 = x1 - x0 + a13 * x1;
         a21 = x3 - x0 + a23 * x3;
         a31 = x0;
         a12 = y1 - y0 + a13 * y1;
         a22 = y3 - y0 + a23 * y3;
         a32 = y0;
      }

      A = a22 - a32 * a23;
      B = a31 * a23 - a21;
      C = a21 * a32 - a31 * a22;
      D = a32 * a13 - a12;
      E = a11 - a31 * a13;
      F = a31 * a12 - a11 * a32;
      G = a12 * a23 - a22 * a13;
      H = a21 * a13 - a11 * a23;
      I = a11 * a22 - a21 * a12;

      return filter(src);
   }

   protected void transformSpace(int[] rect) {
      rect[0] = (int) Math.min(Math.min(x0, x1), Math.min(x2, x3));
      rect[1] = (int) Math.min(Math.min(y0, y1), Math.min(y2, y3));
      rect[2] = (int) Math.max(Math.max(x0, x1), Math.max(x2, x3)) - rect[0];
      rect[3] = (int) Math.max(Math.max(y0, y1), Math.max(y2, y3)) - rect[1];
   }

   public float getOriginX() {
      return x0 - (int) Math.min(Math.min(x0, x1), Math.min(x2, x3));
   }

   public float getOriginY() {
      return y0 - (int) Math.min(Math.min(y0, y1), Math.min(y2, y3));
   }

   private RgbImage filter(RgbImage src) {
      //#debug
      //src.getRgbCache().toLog().printBusiness("#Skew#filter for " + Skew.toStringEdge(edgeAction) + " : " + toStringInterpol(interpolation), Skew.class);
      int width = src.getWidth();
      int height = src.getHeight();
      //int type = src.getType();
      //WritableRaster srcRaster = src.getRaster();

      originalSpace = new int[] { 0, 0, width, height };
      transformedSpace = new int[] { 0, 0, width, height };
      transformSpace(transformedSpace);
      //WritableRaster dstRaster = dst.getRaster();

      int[] inPixels = src.getRgbData();

      if (interpolation == ITechSkew.SKEW_TYPE_0_NEAREST_NEIGHBOUR) {
         return filterPixelsNN(width, height, inPixels, transformedSpace);
      } else if (interpolation == ITechSkew.SKEW_TYPE_2_BILINEAR_4SPLIT) {
         return filterHalf(src, width, height, inPixels);
      } else {
         return filterBilinear(src, width, height, inPixels);
      }

   }

   protected RgbImage filterBilinear(RgbImage src, int width, int height, int[] inPixels) {
      int srcWidth = width;
      int srcHeight = height;
      int srcWidth1 = width - fuzzyBorder;
      int srcHeight1 = height - fuzzyBorder;
      int outWidth = transformedSpace[2];
      int outHeight = transformedSpace[3];
      int outX, outY;
      //int index = 0;
      int[] outPixels = new int[outWidth];
      this.dst = src.getRgbCache().createNonNull(outWidth, outHeight);
      outX = transformedSpace[0];
      outY = transformedSpace[1];
      float[] out = new float[2];

      //#debug
      //StringBuilder sb = StringBuilder.getBigMain();
      for (int y = 0; y < outHeight; y++) {
         for (int x = 0; x < outWidth; x++) {
            transformInverse(outX + x, outY + y, out);
            int srcX = (int) Math.floor(out[0]);
            int srcY = (int) Math.floor(out[1]);
            float xWeight = out[0] - srcX;
            float yWeight = out[1] - srcY;
            int nw, ne, sw, se;
            boolean border = false;
            if (srcX >= 0 && srcX < srcWidth1 && srcY >= 0 && srcY < srcHeight1) {
               // Easy case, all corners are in the image
               int i = srcWidth * srcY + srcX;
               nw = inPixels[i];
               ne = inPixels[i + 1];
               sw = inPixels[i + srcWidth];
               se = inPixels[i + srcWidth + 1];
            } else {
               //System.out.println("srcX=" + srcX + " srcWidth1=" + srcWidth1 + " srcY=" + srcY + " srcHeight1=" + srcHeight1);
               // Some of the corners are off the image
               border = true;
               nw = getEdgePixel(inPixels, srcX, srcY, srcWidth, srcHeight);
               ne = getEdgePixel(inPixels, srcX + 1, srcY, srcWidth, srcHeight);
               sw = getEdgePixel(inPixels, srcX, srcY + 1, srcWidth, srcHeight);
               se = getEdgePixel(inPixels, srcX + 1, srcY + 1, srcWidth, srcHeight);
            }
            //System.out.println("nw=" + nw + " ne=" + ne + " sw=" + sw + " se=" + se);
            outPixels[x] = bilinearInterpolate(xWeight, yWeight, nw, ne, sw, se);
            if (border) {
               //sb.append(ColorConstants.debugColor(outPixels[x]));
               //sb.append("\n");
            }
         }
         //System.out.println(sb.toString());
         //System.out.println("y" + y + " outWidth=" + outWidth + " srcWidth=" + srcWidth);
         dst.setRGB(0, y, outWidth, 1, outPixels);
      }
      return dst;
   }

   /**
    * Tweak on the bilinear interpolation to provide a balanced skew.
    * <br>
    * 
    * @param src
    * @param width
    * @param height
    * @param inPixels
    * @return
    */
   private RgbImage filterHalf(RgbImage src, int width, int height, int[] inPixels) {

      int srcWidth = width;
      int srcHeight = height;
      int srcWidth1 = width - fuzzyBorder;
      int srcHeight1 = height - fuzzyBorder;
      int outWidth = transformedSpace[2];
      int outHeight = transformedSpace[3];
      int outX, outY;
      //int index = 0;
      int[] outPixels = new int[outWidth];
      this.dst = src.getRgbCache().createNonNull(outWidth, outHeight);
      outX = transformedSpace[0];
      outY = transformedSpace[1];
      float[] out = new float[2];

      int outHeight2 = outHeight / 2;
      int outWidth2 = outWidth / 2;
      int buffOutWidthRight = outWidth - outWidth2;
      int buffOutWidthLeft = outWidth2;
      for (int y = 0; y < outHeight2; y++) {
         for (int x = 0; x < outWidth2; x++) {
            //let's map the pixel in the skewed rectangle to a pixel in the src image
            transformInverse(outX + x, outY + y, out);
            int srcX = (int) Math.floor(out[0]);
            int srcY = (int) Math.floor(out[1]);
            float xWeight = out[0] - srcX; //weight is the disance 
            float yWeight = out[1] - srcY;
            //debug
            //src.toLog().printDraw("y=" + y + " srcY=" + srcY + " \t x=" + x + " srcX=" + srcX + " yWeight="+yWeight + " xWeight="+xWeight, Skew.class);
            int nw, ne, sw, se;
            if (srcX >= 0 && srcX < srcWidth1 && srcY >= 0 && srcY < srcHeight1) {
               // Easy case, all corners are in the image
               int i = srcWidth * srcY + srcX;
               nw = inPixels[i];
               ne = inPixels[i + 1];
               sw = inPixels[i + srcWidth];
               se = inPixels[i + srcWidth + 1];
            } else {
               nw = getEdgePixel(inPixels, srcX, srcY, srcWidth, srcHeight);
               ne = getEdgePixel(inPixels, srcX + 1, srcY, srcWidth, srcHeight);
               sw = getEdgePixel(inPixels, srcX, srcY + 1, srcWidth, srcHeight);
               se = getEdgePixel(inPixels, srcX + 1, srcY + 1, srcWidth, srcHeight);
            }
            outPixels[x] = bilinearInterpolate(xWeight, yWeight, nw, ne, sw, se);
         }
         dst.setRGB(0, y, buffOutWidthLeft, 1, outPixels);
      }
      //
      for (int y = 0; y < outHeight2; y++) {
         for (int x = outWidth2; x < outWidth; x++) {
            transformInverse(outX + x, outY + y, out);
            int srcX = (int) Math.floor(out[0]);
            int srcY = (int) Math.floor(out[1]);
            float xWeight = 1 - (out[0] - srcX);
            float yWeight = out[1] - srcY;
            //debug
            //src.toLog().printDraw("y=" + y + " srcY=" + srcY + " \t x=" + x + " srcX=" + srcX + "\t yWeight="+yWeight + " xWeight="+xWeight, Skew.class);
            int nw, ne, sw, se;
            if (srcX >= 1 && srcX < srcWidth1 && srcY >= 0 && srcY < srcHeight1) {
               // Easy case, all corners are in the image
               int i = srcWidth * srcY + srcX;
               nw = inPixels[i];
               ne = inPixels[i - 1];
               sw = inPixels[i + srcWidth];
               se = inPixels[i + srcWidth - 1];
            } else {
               nw = getEdgePixel(inPixels, srcX, srcY, srcWidth, srcHeight);
               ne = getEdgePixel(inPixels, srcX - 1, srcY, srcWidth, srcHeight);
               sw = getEdgePixel(inPixels, srcX, srcY + 1, srcWidth, srcHeight);
               se = getEdgePixel(inPixels, srcX - 1, srcY + 1, srcWidth, srcHeight);
            }
            outPixels[x - outWidth2] = bilinearInterpolate(xWeight, yWeight, nw, ne, sw, se);
         }
         dst.setRGB(outWidth2, y, buffOutWidthRight, 1, outPixels);
      }
      for (int y = outHeight2; y < outHeight; y++) {
         for (int x = 0; x < outWidth2; x++) {
            transformInverse(outX + x, outY + y, out);
            int srcX = (int) Math.floor(out[0]);
            int srcY = (int) Math.floor(out[1]);
            float xWeight = out[0] - srcX;
            float yWeight = 1 - (out[1] - srcY);
            int nw, ne, sw, se;
            if (srcX >= 0 && srcX < srcWidth1 && srcY >= 1 && srcY < srcHeight1) {
               int i = srcWidth * srcY + srcX;
               nw = inPixels[i];
               ne = inPixels[i + 1];
               sw = inPixels[i - srcWidth];
               se = inPixels[i - srcWidth + 1];
            } else {
               nw = getEdgePixel(inPixels, srcX, srcY, srcWidth, srcHeight);
               ne = getEdgePixel(inPixels, srcX + 1, srcY, srcWidth, srcHeight);
               sw = getEdgePixel(inPixels, srcX, srcY - 1, srcWidth, srcHeight);
               se = getEdgePixel(inPixels, srcX + 1, srcY - 1, srcWidth, srcHeight);
            }
            outPixels[x] = bilinearInterpolate(xWeight, yWeight, nw, ne, sw, se);
         }
         dst.setRGB(0, y, buffOutWidthLeft, 1, outPixels);
      }
      for (int y = outHeight2; y < outHeight; y++) {
         for (int x = outWidth2; x < outWidth; x++) {
            transformInverse(outX + x, outY + y, out);
            int srcX = (int) Math.floor(out[0]);
            int srcY = (int) Math.floor(out[1]);
            float xWeight = 1 - (out[0] - srcX);
            float yWeight = 1 - (out[1] - srcY);
            int nw, ne, sw, se;
            if (srcX >= 1 && srcX < srcWidth1 && srcY >= 1 && srcY < srcHeight1) {
               int i = srcWidth * srcY + srcX;
               nw = inPixels[i];
               ne = inPixels[i - 1];
               sw = inPixels[i - srcWidth];
               se = inPixels[i - srcWidth - 1];
            } else {
               nw = getEdgePixel(inPixels, srcX, srcY, srcWidth, srcHeight);
               ne = getEdgePixel(inPixels, srcX - 1, srcY, srcWidth, srcHeight);
               sw = getEdgePixel(inPixels, srcX, srcY - 1, srcWidth, srcHeight);
               se = getEdgePixel(inPixels, srcX - 1, srcY - 1, srcWidth, srcHeight);
            }
            outPixels[x - outWidth2] = bilinearInterpolate(xWeight, yWeight, nw, ne, sw, se);
         }
         dst.setRGB(outWidth2, y, buffOutWidthRight, 1, outPixels);
      }
      return dst;
   }

   final private int getEdgePixel(int[] pixels, int x, int y, int width, int height) {
      if (x < 0 || x >= width || y < 0 || y >= height) {
         return getEdge(pixels, x, y, width, height);
      }
      return pixels[y * width + x];
   }

   protected int getEdge(int[] pixels, int x, int y, int width, int height) {
      switch (edgeAction) {
         case ITechSkew.SKEW_EDGE_0_ZERO:
            return 0;
         case ITechSkew.SKEW_EDGE_2_WRAP:
            return pixels[(mod(y, height) * width) + mod(x, width)];
         case ITechSkew.SKEW_EDGE_1_CLAMP:
            //clamp value
            return pixels[(clamp(y, 0, height - 1) * width) + clamp(x, 0, width - 1)];
         case ITechSkew.SKEW_EDGE_3_FULLY_TRANS_PIXEL:
            int p = pixels[(clamp(y, 0, height - 1) * width) + clamp(x, 0, width - 1)];
            return p & 0x00FFFFFF;
         case ITechSkew.SKEW_EDGE_4_PIXEL:
            return colorPixel;
         case ITechSkew.SKEW_EDGE_5_WHITE:
            return ColorUtils.FULLY_TRANSPARENT_WHITE;
         default:
            return 0;
      }
   }

   protected RgbImage filterPixelsNN(int width, int height, int[] inPixels, int[] transformedSpace) {
      int srcWidth = width;
      int srcHeight = height;
      int outWidth = transformedSpace[2];
      int outHeight = transformedSpace[3];
      int outX, outY, srcX, srcY;
      int[] outPixels = new int[outWidth];
      this.dst = src.getRgbCache().createNonNull(outWidth, outHeight);
      outX = transformedSpace[0];
      outY = transformedSpace[1];
      int[] rgb = new int[4];
      float[] out = new float[2];

      for (int y = 0; y < outHeight; y++) {
         for (int x = 0; x < outWidth; x++) {
            transformInverse(outX + x, outY + y, out);
            srcX = (int) out[0];
            srcY = (int) out[1];
            // int casting rounds towards zero, so we check out[0] < 0, not srcX < 0
            if (out[0] < 0 || srcX >= srcWidth || out[1] < 0 || srcY >= srcHeight) {
               int p = getEdge(inPixels, srcX, srcY, srcWidth, srcHeight);
               //               int p;
               //               switch (edgeAction) {
               //                  case SKEW_EDGE_0_ZERO:
               //                  default:
               //                     p = 0;
               //                     break;
               //                  case SKEW_EDGE_2_WRAP:
               //                     p = inPixels[(mod(srcY, srcHeight) * srcWidth) + mod(srcX, srcWidth)];
               //                     break;
               //                  case SKEW_EDGE_1_CLAMP:
               //                     p = inPixels[(clamp(srcY, 0, srcHeight - 1) * srcWidth) + clamp(srcX, 0, srcWidth - 1)];
               //                     break;
               //               }
               outPixels[x] = p;
            } else {
               int i = srcWidth * srcY + srcX;
               rgb[0] = inPixels[i];
               outPixels[x] = inPixels[i];
            }
         }
         dst.setRGB(0, y, outWidth, 1, outPixels);
      }
      return dst;
   }

   protected void transformInverse(int x, int y, float[] out) {
      out[0] = originalSpace[2] * (A * x + B * y + C) / (G * x + H * y + I);
      out[1] = originalSpace[3] * (D * x + E * y + F) / (G * x + H * y + I);
   }

   /*
      public Rectangle2D getBounds2D( BufferedImage src ) {
          return new Rectangle(0, 0, src.getWidth(), src.getHeight());
      }
   
      public Point2D getPoint2D( Point2D srcPt, Point2D dstPt ) {
          if ( dstPt == null )
              dstPt = new Point2D.Double();
          dstPt.setLocation( srcPt.getX(), srcPt.getY() );
          return dstPt;
      }
   */

   /**
    * Clamp a value to an interval.
    * @param a the lower clamp threshold
    * @param b the upper clamp threshold
    * @param x the input parameter
    * @return the clamped value
    */
   private float clamp(float x, float a, float b) {
      return (x < a) ? a : (x > b) ? b : x;
   }

   /**
    * Clamp a value to an interval.
    * <br>
    * Returns a or b if x is outside the interval.
    * Returns x if x is inside the interval
    * @param a the lower clamp threshold
    * @param b the upper clamp threshold
    * @param x the input parameter
    * @return the clamped value
    */
   private int clamp(int x, int a, int b) {
      return (x < a) ? a : (x > b) ? b : x;
   }

   /**
    * Return a mod b. This differs from the % operator with respect to negative numbers.
    * @param a the dividend
    * @param b the divisor
    * @return a mod b
    */
   private double mod(double a, double b) {
      int n = (int) (a / b);

      a -= n * b;
      if (a < 0)
         return a + b;
      return a;
   }

   /**
    * Return a mod b. This differs from the % operator with respect to negative numbers.
    * @param a the dividend
    * @param b the divisor
    * @return a mod b
    */
   private float mod(float a, float b) {
      int n = (int) (a / b);

      a -= n * b;
      if (a < 0)
         return a + b;
      return a;
   }

   /**
    * Return a mod b. This differs from the % operator with respect to negative numbers.
    * @param a the dividend
    * @param b the divisor
    * @return a mod b
    */
   private int mod(int a, int b) {
      int n = a / b;

      a -= n * b;
      if (a < 0)
         return a + b;
      return a;
   }

   /**
    * Bilinear interpolation of ARGB values.
    * @param x the X interpolation parameter 0..1
    * @param y the y interpolation parameter 0..1
    * @param rgb array of four ARGB values in the order NW, NE, SW, SE
    * @return the interpolated value
    */
   private int bilinearInterpolate(float x, float y, int nw, int ne, int sw, int se) {
      float m0, m1;
      int a0 = (nw >> 24) & 0xff;
      int r0 = (nw >> 16) & 0xff;
      int g0 = (nw >> 8) & 0xff;
      int b0 = nw & 0xff;
      int a1 = (ne >> 24) & 0xff;
      int r1 = (ne >> 16) & 0xff;
      int g1 = (ne >> 8) & 0xff;
      int b1 = ne & 0xff;
      int a2 = (sw >> 24) & 0xff;
      int r2 = (sw >> 16) & 0xff;
      int g2 = (sw >> 8) & 0xff;
      int b2 = sw & 0xff;
      int a3 = (se >> 24) & 0xff;
      int r3 = (se >> 16) & 0xff;
      int g3 = (se >> 8) & 0xff;
      int b3 = se & 0xff;

      float cx = 1.0f - x;
      float cy = 1.0f - y;

      m0 = cx * a0 + x * a1;
      m1 = cx * a2 + x * a3;
      int a = (int) (cy * m0 + y * m1);

      m0 = cx * r0 + x * r1;
      m1 = cx * r2 + x * r3;
      int r = (int) (cy * m0 + y * m1);

      m0 = cx * g0 + x * g1;
      m1 = cx * g2 + x * g3;
      int g = (int) (cy * m0 + y * m1);

      m0 = cx * b0 + x * b1;
      m1 = cx * b2 + x * b3;
      int b = (int) (cy * m0 + y * m1);

      return (a << 24) | (r << 16) | (g << 8) | b;
   }

   //#mdebug
   public String toString() {
      return Dctx.toString(this);
   }

   public void toString(Dctx dc) {
      dc.root(this, "Skew");
      toStringPrivate(dc);
   }

   public String toString1Line() {
      return Dctx.toString1Line(this);
   }

   private void toStringPrivate(Dctx dc) {

   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, "Skew");
      toStringPrivate(dc);
   }

   public UCtx toStringGetUCtx() {
      return drc.getUCtx();
   }

   //#enddebug

}
