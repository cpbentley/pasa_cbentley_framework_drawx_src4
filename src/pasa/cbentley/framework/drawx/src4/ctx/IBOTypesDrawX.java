/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.ctx;

import pasa.cbentley.byteobjects.src4.ctx.IBOTypesBOC;
import pasa.cbentley.byteobjects.src4.objects.color.BlendOp;
import pasa.cbentley.framework.coredraw.src4.ctx.IBOTypesCoreDraw;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOAnchor;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOArtifact;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOBox;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigure;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOPassMosaic;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOPixelStar;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOPassSkewer;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOStructFigSub;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOStrAux;
import pasa.cbentley.framework.drawx.src4.style.IBOStyle;
import pasa.cbentley.framework.drawx.src4.tech.ITechAnchor;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;
import pasa.cbentley.layouter.src4.ctx.IBOTypesLayout;

/**
 * <li> {@link IBOTypesBOC}
 * <li> {@link IBOTypesLayout}
 * <li> {@link IBOTypesCoreDraw}
 * 
 * @author Charles-Philip Bentley
 *
 */
public interface IBOTypesDrawX extends IBOTypesBOC {

   public static final int A_SID_DRWTYPE_A                      = 161;

   /**
    * When Length is not sufficient anymore, you have to create a new module
    * or sub module
    */
   public static final int A_SID_DRWTYPE_Z                      = 180;

   /**
    * 1 or 2 bytes defines a figure
    * Some Figures are TBLR directional.
    */
   public static final int TYPE_DRWX_00_FIGURE                  = A_SID_DRWTYPE_A + 0;

   /**
    * {@link IBOStructFigSub}
    * 
    * Struct that hosts a reference to a {@link IBOFigure} and a {@link IBOBox}
    * and ids of the numbers of sub.
    * 
    * There is only 1 {@link IBOTypesDrawX#TYPE_DRWX_01_FIG_SUB_STRUCT} per figure maximum
    */
   public static final int TYPE_DRWX_01_FIG_SUB_STRUCT          = A_SID_DRWTYPE_A + 1;

   /**
    * Micro figure used as dirt/salt to micro change appearance of a gradient
    * {@link IBOArtifact}
    */
   public static final int TYPE_DRWX_02_FIG_ARTIFACT            = A_SID_DRWTYPE_A + 2;

   /**
    * Defines a Rectangle Box and its position  a figure in a 2D rectangular area. <br>
    * <br>
    * Used to define a box in which to draw a {@link IBOTypesDrawX#TYPE_DRWX_00_FIGURE}.
    * Gives a size<br>
    * Used to define a position and an alignment and a size<br>
    * 1 byte = type<br>
    * 2 byte = flag<br>
    * Most compact version is<br>
    * 2 bits for horizontal alignment<br>
    * 2 bits for vertical alignment<br>
    * 4 bits for size (max size is 16)<br>
    * <br>
    * Most complete is<br>
    * x offset relative to origin of coordinate<br>
    * y offset relative to origin of coordinate<br>
    * <br>
    * 
    * Several Types of Anchor encoding exists.<br>
    */
   public static final int TYPE_DRWX_03_BOX                     = A_SID_DRWTYPE_A + 3;

   public static final int TYPE_DRWX_04                         = A_SID_DRWTYPE_A + 4;

   /**
    * Parameters for scaling an image, including a post scaling RGB filter to smooth the scaling result
    */
   public static final int TYPE_DRWX_05_SCALE                   = A_SID_DRWTYPE_A + 5;

   /**
    * Defines an interaction between 2 layers of RGB data. 
    * <br>
    * <br>
    * One layer is the bottom layer (destination of merge) and the other is the top aka mask layer (source of merge). 
    * <br>
    * Each layer may have a color filter. 
    * <br>
    * Finally a <font color=#CC1111> Merge </font> operation is done with a {@link BlendOp} where <br>
    * <br>
    * pixBot = f(pixBot,pixTop) <br>
    * <br>
    * The final pixel to be drawn is a function of itself and the corresponding pixel of the top layer. <br>
    * <br>
    * <b>Classic</b> : top layer pixels decide the alpha value of bottom layer pixels. Only the bottom
    * layer is drawn to the hardware Graphics.
    * <br>
    * When applied to Figures, {@link ITechFigure#FIG_FLAG_4_MASK} is set. The figure is drawn in black over a white background. 
    * The mask figure is drawn on the bottom layer.
    * <br>
    * <font color=#CC1111>Merge</font> : a top white pixel at x,y makes the bottom pixel x,y fully transparent. 
    * <br>
    * The mask may have a bg figure and a shape figure
    * A color filter may be applied on the mask layer.
    * <br>
    * <b>Halo Effect</b>: 
    * <br>
    * A Touch color filter is applied on the top Black and White layer. Pixels touching white are given an alpha value.
    * <br>
    * <font color=#CC1111>Merge</font> : a top opaque pixel at x,y makes the bottom pixel x,y fully transparent. 
    */
   public static final int TYPE_DRWX_06_MASK                    = A_SID_DRWTYPE_A + 6;

   /**
    * {@link IBOStrAux}
    */
   public static final int TYPE_DRWX_07_STRING_AUX              = A_SID_DRWTYPE_A + 7;

   public static final int TYPE_DRWX_07_STRING_AUX_1_FORMAT     = 1;

   public static final int TYPE_DRWX_07_STRING_AUX_2_SPECIALS_C = 2;

   public static final int TYPE_DRWX_07_STRING_AUX_3_APPLICATOR = 3;

   public static final int TYPE_DRWX_07_STRING_AUX_4_FX         = 4;

   public static final int TYPE_DRWX_07_STRING_AUX_5_FX_STRUCT  = 5;

   public static final int TYPE_DRWX_07_STRING_AUX_XXX          = 6;

   /**
    * {@link IBOStyle}
    */
   public static final int TYPE_DRWX_08_STYLE                   = A_SID_DRWTYPE_A + 8;

   /**
    * Defines an area around a pixel on which to apply a filter.
    * 
    * {@link IBOPixelStar}
    */
   public static final int TYPE_DRWX_09_PIX_STAR                = A_SID_DRWTYPE_A + 9;

   /**
    * {@link IBOAnchor}
    */
   public static final int TYPE_DRWX_10_ANCHOR                  = A_SID_DRWTYPE_A + 10;

   /**
    * {@link IBOPassMosaic}
    */
   public static final int TYPE_DRWX_11_MOSAIC                  = A_SID_DRWTYPE_A + 11;

   /**
    * {@link IBOPassSkewer}
    */
   public static final int TYPE_DRWX_12_SKEWER                  = A_SID_DRWTYPE_A + 12;

}
