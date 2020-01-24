package pasa.cbentley.framework.drawx.src4.ctx;

import pasa.cbentley.core.src4.logging.ToStringStaticBase;
import pasa.cbentley.framework.drawx.src4.style.IStyle;

public class ToStringStaticDrw extends ToStringStaticBase {

   public static String debugStyleAnchor(int i) {
      switch (i) {
         case IStyle.STYLE_ANC_0BORDER:
            return "AT_BORDER";
         case IStyle.STYLE_ANC_1MARGIN:
            return "AT_MARGIN";
         case IStyle.STYLE_ANC_2CONTENT:
            return "AT_CONTENT";
         case IStyle.STYLE_ANC_3PADDING:
            return "AT_PADDING";
         default:
            return "INVALID_ERROR";
      }
   }
}
