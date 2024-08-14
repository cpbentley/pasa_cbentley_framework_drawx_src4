package pasa.cbentley.framework.drawx.src4.style;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.interfaces.C;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrawX;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;
import pasa.cbentley.layouter.src4.engine.LayoutOperator;
import pasa.cbentley.layouter.src4.interfaces.ILayoutable;
import pasa.cbentley.layouter.src4.tech.IBOTblr;

/**
 * 
 * @author Charles Bentley
 *
 */
public class StyleCachePerf extends ObjectDrw implements IBOStyle, IBOTblr, IBOTypesDrawX, ITechStyleCache {

   private int[]         areas;

   private ILayoutable   ctx;

   private ByteObject    style;

   private int           data2;

   private int           data1;


   private StyleOperator styleOp;

   /**
    * 
    * @param gc
    * @param d
    * @param ctx
    */
   public StyleCachePerf(DrwCtx dc, ILayoutable ctx, ByteObject style) {
      super(dc);
      if (ctx == null) {
         throw new NullPointerException();
      }
      this.ctx = ctx;
      if (style == null) {
         throw new NullPointerException();
      }
      this.style = style;
      this.styleOp = dc.getStyleOperator();
   }

   public ByteObject getStyle() {
      return style;
   }

   /**
    * Code everything in a few bytes
    */
   public void compute() {
      LayoutOperator layOp = drc.getLayoutOperator();
      int borderTop = 0;
      int borderBot = 0;
      int borderLeft = 0;
      int borderRite = 0;
      int max = 0;
      ByteObject tblr = styleOp.getStyleElement(style, STYLE_FLAG_A_4_BORDER);
      if (tblr != null) {
         borderTop = layOp.getTBLRValue(tblr, C.POS_0_TOP, ctx);
         borderBot = layOp.getTBLRValue(tblr, C.POS_1_BOT, ctx);
         borderLeft = layOp.getTBLRValue(tblr, C.POS_2_LEFT, ctx);
         borderRite = layOp.getTBLRValue(tblr, C.POS_3_RIGHT, ctx);
      } else {
         
      }
      
      int marginTop = 0;
      int marginBot = 0;
      int marginLeft = 0;
      int marginRite = 0;
       tblr = styleOp.getStyleElement(style, STYLE_FLAG_A_5_MARGIN);
      if (tblr != null) {
         marginTop = layOp.getTBLRValue(tblr, C.POS_0_TOP, ctx);
         marginBot = layOp.getTBLRValue(tblr, C.POS_1_BOT, ctx);
         marginLeft = layOp.getTBLRValue(tblr, C.POS_2_LEFT, ctx);
         marginRite = layOp.getTBLRValue(tblr, C.POS_3_RIGHT, ctx);
      }
      
      int paddingTop = 0;
      int paddingBot = 0;
      int paddingLeft = 0;
      int paddingRite = 0;
       tblr = styleOp.getStyleElement(style, STYLE_FLAG_A_5_MARGIN);
      if (tblr != null) {
         paddingTop = layOp.getTBLRValue(tblr, C.POS_0_TOP, ctx);
         paddingBot = layOp.getTBLRValue(tblr, C.POS_1_BOT, ctx);
         paddingLeft = layOp.getTBLRValue(tblr, C.POS_2_LEFT, ctx);
         paddingRite = layOp.getTBLRValue(tblr, C.POS_3_RIGHT, ctx);
      }
   }
}
