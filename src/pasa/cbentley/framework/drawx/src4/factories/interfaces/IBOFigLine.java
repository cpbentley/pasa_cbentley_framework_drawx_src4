package pasa.cbentley.framework.drawx.src4.factories.interfaces;

public interface IBOFigLine {

   int FIG_LINE_BASIC_SIZE                = IBOFigure.FIG__BASIC_SIZE + 2;
   int FIG_LINE_COLORED_SIZE              = 0;
   /**
    * Draw Extremity
    */
   int FIG_LINE_FLAG_EX                   = 64;
   /**
    * Apply Stick after drawing extremity
    */
   int FIG_LINE_FLAG_EX_STICK             = 128;
   int FIG_LINE_FLAG_HORIZ                = 0;
   int FIG_LINE_FLAGX_STICK_BOT           = 0;
   int FIG_LINE_FLAGX_STICK_LEFT          = 0;
   int FIG_LINE_FLAGX_STICK_RIGHT         = 0;
   int FIG_LINE_FLAGX_STICK_TOP           = 0;
   int FIG_LINE_OFFSET_1FLAG              = IBOFigure.FIG__BASIC_SIZE;
   int FIG_LINE_OFFSET_2SIZE1             = IBOFigure.FIG__BASIC_SIZE + 1;
   int FIG_LINE_OFFSET_EX_COLOR           = 0;
   int FIG_LINE_OFFSET_EX_SIZE            = 0;

}
