/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories.drawer;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesBOC;
import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.logging.IStringable;
import pasa.cbentley.core.src4.structs.IntBuffer;
import pasa.cbentley.core.src4.utils.IntUtils;
import pasa.cbentley.core.src4.utils.interfaces.IColors;
import pasa.cbentley.framework.coredraw.src4.interfaces.IGraphics;
import pasa.cbentley.framework.coredraw.src4.interfaces.IImage;
import pasa.cbentley.framework.coredraw.src4.interfaces.IMFont;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechGraphics;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrawX;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigChar;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigure;
import pasa.cbentley.framework.drawx.src4.string.StringDrawUtils;
import pasa.cbentley.framework.drawx.src4.string.Stringer;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOFigString;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;

/**
 * Draw String figures.
 * <br>
 * <br>
 * Tracks the Stringer
 * <br>
 * <br>
 * How do you load a Stringer extension into the framework?
 * <br>
 * It must be linked manually
 * <br>
 * <br>
 * 
 * @author Charles-Philip Bentley
 *
 */
public class DrawerString implements IStringable, IBOTypesDrawX, ITechFigure {

   /** New line constant */
   public static final char   NEWLINE    = '\n';

   /** Textbreak constants */
   public static final char[] TEXTBREAKS = { ' ', '?', ';', ',', '.', '!', ':', '-', '=', '(', ')', '[', ']' };

   protected final DrwCtx     drc;

   private Stringer stringerChar;

   public DrawerString(DrwCtx drc) {
      this.drc = drc;
     
      
   }
   
   public Stringer getStringerChar() {
      if(stringerChar == null) {
         stringerChar = new Stringer(drc);
         stringerChar.setString(new char[1], 0, 1);
      }
      return stringerChar;
   }

   /**
    * Returns a cropped char drawn in a fully transparent background
    * with connexed pixels
    * @param c
    * @param f
    * @return
    */
   public IImage cropChar(char c, IMFont f) {
      int w = f.charWidth(c);
      int h = f.getHeight();
      int[] imgData = new int[w * h];
      IImage img = drc.getImageFactory().createImage(w, h, IColors.FULLY_TRANSPARENT_BLACK);
      IGraphics g = img.getGraphics();
      g.setColor(IColors.FULLY_OPAQUE_WHITE);
      g.drawChar(c, 0, 0, ITechGraphics.TOP | ITechGraphics.LEFT);
      img.getRGB(imgData, 0, w, 0, 0, w, h);
      int start = 0;
      int end = h;
      int index = 0;
      int countLines = 0;
      boolean started = false;
      for (int k = 0; k < h; k++) {
         if (imgData[index] != 0) {
            //we start with a background pixels
         } else {
            //we start with a font pixel
            started = true;
         }
         //check if the first row if pixels have the same num
         if (imgData[index] != 0 && IntUtils.isOnly(imgData, index, w, imgData[index])) {
            start++;
         } else {
            if (started) {
               started = true;
            }
            countLines++;
         }
      }
      int newX = 0;
      int newY = 0;
      int newW = w;
      int newH = countLines;
      int[] newData = new int[newW * newH];

      return drc.getImageFactory().createRGBImage(newData, newW, newH, true);
   }


   public void drawFigChar(GraphicsX g, int x, int y, int w, int h, ByteObject fig) {
      Stringer st = getStringerChar();
      st.setAreaXYWH(x, y, w, h);
      st.setFigureChar(fig);
      st.buildFxAndMeter();
      st.draw(g);
   }

   
   /**
    * The figure must have some raw text.
    * <br>
    * <br>
    * 
    * @param g
    * @param x
    * @param y
    * @param w
    * @param h
    * @param fig
    */
   public void drawFigString(GraphicsX g, int x, int y, int w, int h, ByteObject fig) {
      ByteObject rawText = fig.getSubFirst(IBOTypesBOC.TYPE_003_LIT_STRING);
      String str = null;
      if (rawText != null) {
         str = drc.getBOC().getLitteralStringOperator().getLitteralString(rawText);
         if (str != null) {
            drawFigString(g, x, y, w, h, fig, str.toCharArray(), 0, str.length());
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   /**
    * Send figure to Stringer 
    * @param g
    * @param x
    * @param y
    * @param w
    * @param h
    * @param fig
    * @param chars
    * @param offset
    * @param len
    */
   public void drawFigString(GraphicsX g, int x, int y, int w, int h, ByteObject fig, char[] chars, int offset, int len) {
      Stringer st = new Stringer(drc);
      st.setAreaXYWH(x, y, w, h);
      st.setString(chars, offset, len);
      st.buildForDisplayWith(fig);
      st.draw(g);
   }

   /**
    * 
    * @param g
    * @param x
    * @param y
    * @param s
    * @param strFigure
    */
   public void drawString(GraphicsX g, int x, int y, String[] s, ByteObject strFigure) {
      ByteObject sc = strFigure.getSubFirst(IBOTypesDrawX.TYPE_DRWX_05_SCALE);
      ByteObject anchor = strFigure.getSubFirst(IBOTypesDrawX.TYPE_DRWX_03_BOX);
      IMFont f = getStringFont(strFigure);
      int color = getStringColor(strFigure);
      g.setColor(color);
      g.setFont(f);
      for (int i = 0; i < s.length; i++) {
         String str = s[i];
         g.drawString(str, x, y, GraphicsX.ANCHOR);
         y += f.getHeight();
      }
   }

   /**
    * m and k will return different height pixel values
    * m width > k width
    * k height > m height
    * Some uses need finer grained control than just Font.getHeight()
    * 
    */
   public int[] getCharPixelSize(char c, IMFont f) {
      int[] wh = new int[2];
      int add = 6;
      int w = f.charWidth(c);
      //Because we cannot be sure of the implemention char dimension values we must add
      IImage img = drc.getImageFactory().createImage(w + add, f.getHeight() + add);
      int imgH = img.getHeight();
      int imgW = img.getWidth();
      IGraphics g = img.getGraphics();
      g.setColor(0);
      g.drawChar(c, 3, 3, ITechGraphics.TOP | ITechGraphics.LEFT);
      int[] rgb = new int[imgW * imgH];
      img.getRGB(rgb, 0, imgW, 0, 0, imgW, imgH);
      wh[1] = StringDrawUtils.countLinesWithBlack(imgH, imgW, rgb);

      return wh;
   }

   /**
    * Offsets where there is a '\n' character
    * @param text
    * @return
    */
   public int[] getNewLineBreaks(String text) {
      IntBuffer ib = new IntBuffer(drc.getUC(), 5);
      for (int i = 0; i < text.length(); i++) {
         if (text.charAt(i) == NEWLINE) {
            ib.addInt(i);
         }
      }
      if (ib.getSize() == 0)
         ib.addInt(text.length());
      return ib.getIntsClonedTrimmed();
   }

   /**
    * 
    * @param text
    * @return if undefined will return 0
    */
   public int getStringColor(ByteObject strFig) {
      if (strFig == null) {
         return 0;
      }
      return strFig.get4(IBOFigure.FIG__OFFSET_06_COLOR4);
   }

   /**
    * If null, return def font from the {@link DeviceDriver}.
    * <br>
    * <br>
    * @param strFig 
    * @return
    */
   public IMFont getStringFont(ByteObject strFig) {
      if (strFig == null) {
         return drc.getFontFactory().getDefaultFont();
      }
      int face = strFig.get1(IBOFigString.FIG_STRING_OFFSET_03_FACE1);
      int style = strFig.get1(IBOFigString.FIG_STRING_OFFSET_04_STYLE1);
      int size = strFig.get1(IBOFigString.FIG_STRING_OFFSET_05_SIZE1);

      IMFont f = drc.getFontFactory().getFont(face, style, size);
      return f;
   }

   //#mdebug
   public String toString() {
      return Dctx.toString(this);
   }

   public void toString(Dctx dc) {
      dc.root(this, "StringDrawer");
   }

   public String toString1Line() {
      return Dctx.toString1Line(this);
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, "StringDrawer");
   }

   public UCtx toStringGetUCtx() {
      return drc.getUC();
   }
   //#enddebug
}
