package pasa.cbentley.framework.drawx.src4.factories.interfaces;

import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;

public interface IBOFigLosange extends IBOFigure {

   int FIG_LOSANGE_BASIC_SIZE             = IBOFigure.FIG__BASIC_SIZE + 6;
   /**
    * 
    */
   int FIG_LOSANGE_FLAG_1_HORIZ           = 1;
   int FIG_LOSANGE_FLAG_2_NEG_OVERSTEP    = 2;
   /**
    * Draws the inverse
    * 
    */
   int FIG_LOSANGE_FLAG_3_CONTOUR         = 4;
   /**
    * Instead of opposing bases, code oppose the point.
    */
   int FIG_LOSANGE_FLAG_4_NOED_PAPILLION  = 8;
   int FIG_LOSANGE_OFFSET_1_FLAG          = IBOFigure.FIG__BASIC_SIZE;
   /**
    * Defines overstep for the two triangels
    */
   int FIG_LOSANGE_OFFSET_2_OVERSTEP2     = IBOFigure.FIG__BASIC_SIZE + 1;
   /**
    * size of fill.
    * 0 = no fill.
    */
   int FIG_LOSANGE_OFFSET_3_FILL2         = IBOFigure.FIG__BASIC_SIZE + 3;
   /**
    * Type of base triangle.
    * <br>
    * <li> {@link ITechFigure#FIG_LOSANGE_TYPE_0_COLOR} 
    * <li> {@link ITechFigure#FIG_LOSANGE_TYPE_1_TRIANGLE} 
    * <li> {@link ITechFigure#FIG_LOSANGE_TYPE_2_TRIANGLES} 
    */
   int FIG_LOSANGE_OFFSET_4_TYPE1         = IBOFigure.FIG__BASIC_SIZE + 5;

}
