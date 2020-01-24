package pasa.cbentley.framework.drawx.src4.param;
//package mordan.draw.param;
//
//import mordan.draw.interfaces.IDrw;
//import mordan.draw.interfaces.IDrwTypes;
//import mordan.draw.interfaces.IGradient;
//import mordan.memory.BOModule;
//import mordan.memory.interfaces.IObject;
//import pasa.cbentley.byteobjects.core.ByteObject;
//
//public class FigRectC {
//
//   public static ByteObject getFigBorder(BOModule mod, ByteObject tblr, ByteObject rect) {
//      return getFigBorder(mod, tblr, rect, false);
//   }
//
//   /**
//    * Border is just a rectangle
//    * @param tblr
//    * @param rect
//    * @param outer
//    * @return
//    */
//   public static ByteObject getFigBorder(BOModule mod, ByteObject tblr, ByteObject rect, boolean outer) {
//      ByteObject p = ByteObject.createByteObject(mod, IDrwTypes.TYPE_050_FIGURE, IDrw.FIG_BORDER_BASIC_SIZE);
//      p.setValue(IDrw.FIG__OFFSET_01_TYPE1, IDrw.FIG_TYPE_02_BORDER, 1);
//      p.setFlag(IDrw.FIG__OFFSET_03_FLAGP, IDrw.FIG_FLAGP_2EXTRA_BOUNDARY, outer);
//      p.setFlag(IDrw.FIG_BORDER_OFFSET_1FLAG, IDrw.FIG_BORDER_FLAG_1OUTER, outer);
//      p.setFlag(IDrw.FIG_BORDER_OFFSET_1FLAG, IDrw.FIG_BORDER_FLAG_8FIGURES, false);
//      p.setFlag(IDrw.FIG_BORDER_OFFSET_1FLAG, IDrw.FIG_BORDER_FLAG_5FIGURE, true);
//      if (tblr == null) {
//         p.setFlag(IDrw.FIG_BORDER_OFFSET_1FLAG, IDrw.FIG_BORDER_FLAG_4COIN, false);
//         p.setFlag(IObject.A_OBJECT_OFFSET_2_FLAG, IObject.A_OBJECT_FLAG_1_INCOMPLETE, true);
//      }
//      p.setByteObjects(new ByteObject[] { rect, tblr });
//      return p;
//   }
//
//   /**
//    * Creates a border with 4 coin figures and 4 rectangle figures
//    * <br>
//    * 
//    * @param tblr TBLR pixe sizes
//    * @param tblrRects 4 TBLR figures. Maybe null.
//    * @param coins 4 TL/TR/BL/BR figures. Maybe null.
//    * @param outer True when drawn outside the boundary.
//    * @return
//    */
//   public static ByteObject getFigBorder(BOModule mod, ByteObject tblr, ByteObject tblrRects, ByteObject coins, boolean outer) {
//      ByteObject[] figs = new ByteObject[] { tblrRects, tblrRects, tblrRects, tblrRects, coins, coins, coins, coins };
//      ByteObject p = getFigBorder(mod, tblr, figs, outer);
//      if (coins != null) {
//         p.setFlag(IDrw.FIG_BORDER_OFFSET_1FLAG, IDrw.FIG_BORDER_FLAG_4COIN, true);
//      }
//      return p;
//   }
//
//   public static ByteObject getFigBorder(BOModule mod, ByteObject tblr, ByteObject coinFig, ByteObject topFig, ByteObject botFig, ByteObject leftFig, ByteObject rightFig, boolean outer) {
//      ByteObject[] figures = new ByteObject[] { topFig, botFig, leftFig, rightFig, coinFig, coinFig, coinFig, coinFig };
//      return getFigBorder(mod, tblr, figures, outer);
//   }
//
//   /**
//    * A border figure with TBLR sizes and up to 8 figures.
//    * For partial definitions, set it to null. 
//    * @param tblr The TBLR used  by the Style Border Box model
//    * @param figures 8 figures for the 8 spots 
//    * 0 = top rect
//    * 1 = bot rect
//    * 2 = left rect
//    * 3 = right rect
//    * 4 = TL coin
//    * 5 = TR coin
//    * 6 = BL coin
//    * 7 = BR coin
//    * @param outer
//    * @return
//    */
//   public static ByteObject getFigBorder(BOModule mod, ByteObject tblr, ByteObject[] figures, boolean outer) {
//      ByteObject p = ByteObject.createByteObject(mod, IDrwTypes.TYPE_050_FIGURE, IDrw.FIG_BORDER_BASIC_SIZE);
//      p.setValue(IDrw.FIG__OFFSET_01_TYPE1, IDrw.FIG_TYPE_02_BORDER, 1);
//      p.setFlag(IDrw.FIG__OFFSET_03_FLAGP, IDrw.FIG_FLAGP_2EXTRA_BOUNDARY, outer);
//      p.setFlag(IDrw.FIG_BORDER_OFFSET_1FLAG, IDrw.FIG_BORDER_FLAG_1OUTER, outer);
//      p.setFlag(IDrw.FIG_BORDER_OFFSET_1FLAG, IDrw.FIG_BORDER_FLAG_8FIGURES, true);
//      p.setFlag(IDrw.FIG_BORDER_OFFSET_1FLAG, IDrw.FIG_BORDER_FLAG_4COIN, true);
//      if (tblr == null) {
//         p.setFlag(IDrw.FIG_BORDER_OFFSET_1FLAG, IDrw.FIG_BORDER_FLAG_4COIN, false);
//         p.setFlag(IObject.A_OBJECT_OFFSET_2_FLAG, IObject.A_OBJECT_FLAG_1_INCOMPLETE, true);
//      }
//      if (figures.length != 8) {
//         throw new IllegalArgumentException();
//      }
//      p.setByteObjects(figures);
//      p.addByteObject(tblr);
//      return p;
//   }
//
//   public static ByteObject getFigBorder(BOModule mod, ByteObject tblr, int color) {
//      return getFigBorder(mod, tblr, color, false);
//   }
//
//   /**
//    * 
//    * @param tblr
//    * @param color just a color for the border
//    * @return
//    */
//   public static ByteObject getFigBorder(BOModule mod, ByteObject tblr, int color, boolean outer) {
//      ByteObject rect = getFigRect(mod, color);
//      return getFigBorder(mod, tblr, rect, outer);
//   }
//
//   public static ByteObject getFigBorder(BOModule mod, ByteObject tblr, int arcw, int arch, int color, ByteObject grad) {
//      ByteObject rect = getFigRect(mod, color, arcw, arch, grad, null, null, null);
//      return getFigBorder(mod, tblr, rect);
//   }
//
//   /**
//    * Border Figure 
//    * @param size
//    * @param rect
//    * @return
//    */
//   public static ByteObject getFigBorder(BOModule mod, int size, ByteObject rect) {
//      return getFigBorder(mod, size, rect, false);
//   }
//
//   public static ByteObject getFigBorder(BOModule mod, int size, ByteObject rect, boolean outer) {
//      ByteObject tblr = TblrC.getTBLR(mod, size);
//      return getFigBorder(mod, tblr, rect, outer);
//   }
//
//   public static ByteObject getFigBorder(BOModule mod, int size, int color) {
//      return getFigBorder(mod, size, color, false);
//   }
//
//   public static ByteObject getFigBorder(BOModule mod, int size, int color, boolean outer) {
//      ByteObject rect = getFigRect(mod, color);
//      return getFigBorder(mod, size, rect, outer);
//   }
//
//   public static ByteObject getFigBorder(BOModule mod, int size, int color, ByteObject grad) {
//      //fill size so that the Opaque Flag is not set
//      ByteObject rect = getFigRect(mod, color, 0, 0, size, grad, null, null, null);
//      return getFigBorder(mod, size, rect);
//   }
//
//   public static ByteObject getFigBorder(BOModule mod, int size, int pcolor, int scolor, int sec) {
//      return getFigBorder(mod, size, 0, 0, pcolor, scolor, sec);
//   }
//
//   public static ByteObject getFigBorder(BOModule mod, int size, int arcw, int arch, int color, ByteObject grad) {
//      ByteObject rect = getFigRect(mod, color, arcw, arch, grad, null, null, null);
//      return getFigBorder(mod, size, rect);
//   }
//
//   /**
//    * Simple border
//    * @param outer
//    * @param size
//    * @param arcw
//    * @param arch
//    * @param color
//    * @param scolor
//    * @return
//    */
//   public static ByteObject getFigBorder(BOModule mod, int size, int arcw, int arch, int color, int scolor, int sec) {
//      ByteObject grad = GradientC.getGradient(mod, scolor, sec, IGradient.GRADIENT_TYPE_RECT_00_SQUARE);
//      return getFigBorder(mod, size, arcw, arch, color, grad);
//   }
//
//   /**
//    * 
//    * @param fillVert should the gradient be vertical?
//    * @param pcolor
//    * @param scolor
//    * @param maxSec
//    * @return
//    */
//   public static ByteObject getFigRect(BOModule mod, boolean fillVert, int pcolor, int scolor, int maxSec) {
//      int type = IGradient.GRADIENT_TYPE_RECT_01_HORIZ;
//      if (fillVert)
//         type = IGradient.GRADIENT_TYPE_RECT_02_VERT;
//      ByteObject grad = GradientC.getGradient(mod, scolor, maxSec, type);
//      return getFigRect(mod, pcolor, grad, null);
//   }
//
//   public static ByteObject getFigRect(BOModule mod, int color, boolean doAlpha) {
//      ByteObject p = getFigRect(mod, color);
//      setDoAlplay(p, doAlpha);
//      return p;
//   }
//
//   public static ByteObject getFigRect(BOModule mod, int color, ByteObject grad) {
//      return getFigRect(mod, color, grad, null, null, null);
//   }
//
//   public static ByteObject getFigRect(BOModule mod, int color, ByteObject grad, ByteObject anchor) {
//      return getFigRect(mod, color, grad, anchor, null, null);
//   }
//
//   public static ByteObject getFigRect(BOModule mod, int color, ByteObject grad, ByteObject anchor, ByteObject filter, ByteObject[] sub) {
//      return getFigRect(mod, color, -1, -1, grad, anchor, filter, sub);
//   }
//
//   public static ByteObject getFigRect(BOModule mod, int color, int arcw, int arch, ByteObject grad) {
//      return getFigRect(mod, color, arcw, arch, 0, grad, null, null, null);
//   }
//
//   /**
//    * Filled Rectangle
//    * @param color
//    * @param arcw
//    * @param arch
//    * @param grad
//    * @param anchor
//    * @param filter
//    * @param sub
//    * @return
//    */
//   public static ByteObject getFigRect(BOModule mod, int color, int arcw, int arch, ByteObject grad, ByteObject anchor, ByteObject filter, ByteObject[] sub) {
//      return getFigRect(mod, color, arcw, arch, 0, grad, anchor, filter, sub);
//   }
//
//   public static ByteObject getFigRect(BOModule mod, int pcolor, int scolor, int maxSec, int gradType) {
//      ByteObject grad = GradientC.getGradient(mod, scolor, maxSec, gradType);
//      return getFigRect(mod, pcolor, grad, null, null, null);
//   }
//
//   /**
//    * 
//    * @param color
//    * @param arcw
//    * @param arch
//    * @param fillSize <= 0 means a filled rectangle. A value of 1 means the figure drawing method will
//    * call the drawRectangle primitive once. 5 means a 5 pixels rectangle 
//    * @param grad
//    * @param anchor
//    * @param filter
//    * @param sub
//    * @return
//    */
//   public static ByteObject getFigRect(BOModule mod, int color, int arcw, int arch, int fillSize, ByteObject grad, ByteObject anchor, ByteObject filter, ByteObject[] sub) {
//      ByteObject p = ByteObject.createByteObject(mod, IDrwTypes.TYPE_050_FIGURE, IDrw.FIG_RECTANGLE_BASIC_SIZE);
//      p.setValue(IDrw.FIG__OFFSET_01_TYPE1, IDrw.FIG_TYPE_01_RECTANGLE, 1);
//      p.setValue(IDrw.FIG__OFFSET_06_COLOR4, color, 4);
//      if (fillSize < 0)
//         fillSize = 0;
//      p.setValue(IDrw.FIG_RECTANGLE_OFFSET_4SIZEF1, fillSize, 1);
//
//      if (arcw != -1 || arch != -1) {
//         p.setFlag(IDrw.FIG_RECTANGLE_OFFSET_1FLAG, IDrw.FIG_RECTANGLE_FLAG_1ROUND, true);
//      }
//      if (arcw != -1) {
//         p.setFlag(IDrw.FIG_RECTANGLE_OFFSET_1FLAG, IDrw.FIG_RECTANGLE_FLAG_7ARCW1, true);
//         p.setValue(IDrw.FIG_RECTANGLE_OFFSET_2ARCW1, arcw, 1);
//      }
//      if (arch != -1) {
//         p.setFlag(IDrw.FIG_RECTANGLE_OFFSET_1FLAG, IDrw.FIG_RECTANGLE_FLAG_8ARCH1, true);
//         p.setValue(IDrw.FIG_RECTANGLE_OFFSET_3ARCH1, arch, 1);
//      }
//      //TODO merge with genetic flag
//      p.setFlag(IDrw.FIG__OFFSET_03_FLAGP, IDrw.FIG_FLAGP_5IGNORE_ALPHA, true);
//      if (arch <= 0 && arcw <= 0) {
//         //when rounded edges, figure is not shape opaque.
//         DrwParamFig.setFigPerfFlag(color, grad, p);
//      }
//      DrwParamFig.setFigLinks(p, grad, anchor, filter, sub);
//      return p;
//
//   }
//
//   /**
//    * TODO
//    * Rectangle figure whose borders are chopped away.
//    * <br>
//    * When all triangles of same size, implemented with a clipped losange.
//    * @param color
//    * @param grad
//    * @return
//    */
//   public static ByteObject getFigRectChopped(BOModule mod, int color, ByteObject grad) {
//      ByteObject p = ByteObject.createByteObject(mod, IDrwTypes.TYPE_050_FIGURE, IDrw.FIG_RECTANGLE_BASIC_SIZE);
//
//      return p;
//   }
//
//   public static ByteObject getFigRectOpaque(BOModule mod, int color) {
//      return getFigRect(mod, (color & 0xFFFFFF) + (255 << 24));
//   }
//
//   public static ByteObject getFigRectOpaque(BOModule mod, int color, ByteObject grad) {
//      return getFigRect(mod, (color & 0xFFFFFF) + (255 << 24), grad);
//   }
//
//   public static void setDoAlplay(ByteObject p, boolean doAlpha) {
//      if (doAlpha) {
//         p.setFlag(IDrw.FIG__OFFSET_02_FLAG, IDrw.FIG_FLAGP_5IGNORE_ALPHA, false);
//         p.setFlag(IDrw.FIG__OFFSET_03_FLAGP, IDrw.FIG_FLAGP_3OPAQUE, false);
//      } else {
//         p.setFlag(IDrw.FIG__OFFSET_02_FLAG, IDrw.FIG_FLAGP_5IGNORE_ALPHA, true);
//         p.setFlag(IDrw.FIG__OFFSET_03_FLAGP, IDrw.FIG_FLAGP_3OPAQUE, true);
//      }
//   }
//
//}
