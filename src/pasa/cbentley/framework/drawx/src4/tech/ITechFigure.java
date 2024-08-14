/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.tech;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.core.interfaces.IByteObject;
import pasa.cbentley.core.src4.interfaces.C;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrawX;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigTriangle;

public interface ITechFigure extends IByteObject {

   public static final int EXTRA_0_CENTER                  = 0;

   public static final int EXTRA_1_TOP_LEFT                = 1;

   public static final int EXTRA_2_BOT_RITE                = 2;

   public static final int DIM_MASTER_0_NONE               = 0;

   public static final int DIM_MASTER_1_HORI               = 1;

   public static final int DIM_MASTER_2_VERT               = 2;

   public static final int DIM_MASTER_3_CLOCKWISE          = 3;

   public static final int DIM_MASTER_4_SPIRAL             = 4;

   public static final int FIG_LOSANGE_TYPE_0_COLOR        = 0;

   public static final int FIG_LOSANGE_TYPE_1_TRIANGLE     = 1;

   public static final int FIG_LOSANGE_TYPE_2_TRIANGLES    = 2;

   public static final int FIG_LOSANGE_TYPE_3_ANGLES       = 3;

   /**
    * When set, the value {@link IBOFigTriangle#FIG_TRIANGLE_OFFSET_03_ANGLE2} is a 360 degree.
    * 
    * <p>
    * range= [0,360]
    * </p>
    * Shortcuts 
    * <p>
    * <li> {@link C#ANGLE_RIGHT_0}
    * <li> {@link C#ANGLE_LEFT_180}
    * <li> {@link C#ANGLE_DOWN_270}
    * <li> {@link C#ANGLE_UP_90}
    * </p>
    */
   public static final int FIG_TRIANGLE_TYPE_0_DEGREE_360  = 0;

   /**
    * Angle gives one of the following
    * 
    * <li> {@link C#TYPE_00_TOP}
    * <li> {@link C#TYPE_01_BOTTOM}
    * <li> {@link C#TYPE_02_LEFT}
    * <li> {@link C#TYPE_03_RIGHT}
    * <li> {@link C#TYPE_04_TopLeft}
    * <li> {@link C#TYPE_05_TopRight}
    * <li> {@link C#TYPE_06_BotLeft}
    * <li> {@link C#TYPE_07_BotRight}
    * <li> {@link C#TYPE_08_MID_TopLeft}
    * <li> {@link C#TYPE_09_MID_TopRight}
    * <li> {@link C#TYPE_10_MID_BotLeft}
    * <li> {@link C#TYPE_11_MID_BotRight}
    * <li> {@link C#TYPE_12_TopLeftDiagBot}
    * <li> {@link C#TYPE_13_TopLeftDiagRight}
    * <li> {@link C#TYPE_14_TopRightDiagBot}
    * <li> {@link C#TYPE_15_TopRightDiagLeft}
    * <li> {@link C#TYPE_16_BotLeftDiagTop}
    * 
    */
   public static final int FIG_TRIANGLE_TYPE_1_DIRECTIONAL = 1;

   /**
    * 3 points of anchors are defined relative to the box in which the triangle is drawn
    * Each point is defined as a 0-200 float 0 being -1 and 200 being 1.0
    * 
    * So that's 6 bytes of data for the 3 points of the triangle
    * <p>
    * x1= 100, y1 = 100 means point is on center of rect area
    * x2=0 (Leftmost), y2=200 (BottomMost) means point is LeftBottom
    * x3=50 , y3=150 means 3rd point half way 
    * </p>
    * 
    * <p>
    * {@link IBOFigTriangle#FIG_TRIANGLE_OFFSET_04_h4} is irrelevant
    * </p>
    */
   public static final int FIG_TRIANGLE_TYPE_2_ANCHORS     = 2;

   /**
    * <li>π rad = 180 degree
    * <li>1 rad = 57 degree and 17' 44'' 48'''.
    * 
    * Not implemented
    */
   public static final int FIG_TRIANGLE_TYPE_3_RADIANS     = 3;

   /**
    * Most basic shape.
    * <br>
    * Without a gradient, rectangle shape is direction agnostic.
    * <br>
    * Direction depends on gradient. <br>
    * {@link C#DIR_0_TOP}
    * <br>
    * value 0
    */
   public static final int FIG_TYPE_01_RECTANGLE           = 1;

   /**
    * 2 definitions of BORDER.
    * <br>
    * Simple outer, coins, size, color, gradient color, maxGrad
    * <br>
    * Main Flag: outer, coins, tblr, arc, figure
    * <br>
    * Border has 1 TBLR {@link ByteObject} and 1 {@link ITechFigure#FIG_TYPE_01_RECTANGLE} {@link ByteObject}.
    * <br>
    */
   public static final int FIG_TYPE_02_BORDER              = 2;

   /**
    * Param has all the flags to define number of triangles, distance, type(iso, arrow shaped)
    * filters (gradient, trans).
    */
   public static final int FIG_TYPE_03_TRIANGLE            = 3;

   public static final int FIG_TYPE_04_CHAR                = 4;

   public static final int FIG_TYPE_05_LINE                = 5;

   /**
    * Two triangles upside down <br>
    * Top - Bot
    * Left - Right
    */
   public static final int FIG_TYPE_06_LOSANGE             = 6;

   /**
    * defines a cirle(w==h)/ellipse figure
    * May be filled o
    */
   public static final int FIG_TYPE_07_ELLIPSE             = 7;

   public static final int FIG_TYPE_08_GERMANCROSS         = 36;

   public static final int FIG_TYPE_09_PIXELS              = 9;

   /**
    * The single char figure with scaling to fit the figure's drawing area. <br>
    * More complex string of characters are possible with {@link IBOTypesDrawX#TYPE_RAW}.
    * How does it relate to a number 10 scaled up in big font?
    */
   public static final int FIG_TYPE_10_STRING              = 60;

   public static final int FIG_TYPE_11_GRID                = 44;

   public static final int FIG_TYPE_12_ARROW               = 16;

   public static final int FIG_TYPE_13_REPEATER            = 32;

   public static final int FIG_TYPE_15_RAYS                = 79;

   public static final int FIG_TYPE_16_SUPERLINES          = 23;

   /**
    * Arlequin draws many rectangle of different colors.
    * Used by Repeater for couverture
    */
   public static final int FIG_TYPE_17_ARLEQUIN            = 26;

   /**
    * A figure that draw blocks of colors
    */
   public static final int FIG_TYPE_18_DIAG_BLOCKS         = 18;

   /**
    * Grid of lines filled by colors. On intersection, an artifact may be drawn.
    * <br>
    * The lines are actually rectangles whose sizes are defined by a sizer.
    * <br>
    * The rectangles can be gradiented.
    * <br>
    * The artifacts are figures repeated. As small artifact shift is defined.
    * <br>
    * The base size of the artifact is the size of the vertical and horizontal line intersection.
    * <br>
    * The lines are drawn using primitives or a repeated pattern of an RGB array. each pixels being
    * blended using the figure blender.
    * <br>
    * When a color gradient is global
    */
   public static final int FIG_TYPE_20_CROSS               = 34;

   public static final int FIG_TYPE_30_COEUR               = 30;

   public static final int FIG_TYPE_31_CARREAU             = 31;

   public static final int FIG_TYPE_32_PIQUE               = 32;

   public static final int FIG_TYPE_33_TREFLE              = 33;

   public static final int FIG_TYPE_35_TESSON              = 35;

   public static final int STROKE_0_SOLID                  = 0;

   public static final int STROKE_1_SIMPLE_DOTS            = 1;

}
