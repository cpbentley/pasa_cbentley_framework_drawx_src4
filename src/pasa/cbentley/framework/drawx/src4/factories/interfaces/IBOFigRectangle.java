package pasa.cbentley.framework.drawx.src4.factories.interfaces;

public interface IBOFigRectangle extends IBOFigure {

   /**
    * uses all default Figure fields
    * 1 byte for flag
    * 1 byte for arcw
    * 1 byte for arch
    * 1 byte for fillSize
    */
   int FIG_RECTANGLE_BASIC_SIZE           = IBOFigure.FIG__BASIC_SIZE + 4;
   int FIG_RECTANGLE_FLAG_1_ROUND         = 1;
   /**
    * Round inside
    */
   int FIG_RECTANGLE_FLAG_2_ROUND_INSIDE  = 2;
   int FIG_RECTANGLE_FLAG_7_ARCW1         = 64;
   int FIG_RECTANGLE_FLAG_8_ARCH1         = 128;
   int FIG_RECTANGLE_OFFSET_1_FLAG        = IBOFigure.FIG__BASIC_SIZE;
   int FIG_RECTANGLE_OFFSET_2_ARCW1       = IBOFigure.FIG__BASIC_SIZE + 1;
   int FIG_RECTANGLE_OFFSET_3_ARCH1       = IBOFigure.FIG__BASIC_SIZE + 2;
   /**
    * When différent from 0, draws "Border" rectangle.
    */
   int FIG_RECTANGLE_OFFSET_4_SIZEF1      = IBOFigure.FIG__BASIC_SIZE + 3;

}
