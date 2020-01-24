package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesBOC;
import pasa.cbentley.byteobjects.src4.tech.ITechMergeMask;
import pasa.cbentley.core.src4.interfaces.C;
import pasa.cbentley.core.src4.utils.BitUtils;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.drawx.src4.string.IFxStr;
import pasa.cbentley.framework.drawx.src4.tech.ITechBox;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;
import pasa.cbentley.framework.drawx.src4.tech.ITechGradient;
import pasa.cbentley.framework.drawx.src4.tech.ITechMask;
import pasa.cbentley.framework.drawx.src4.tech.ITechMergeMaskFigure;
import pasa.cbentley.framework.drawx.src4.tech.ITechTblr;
import pasa.cbentley.framework.drawx.src4.utils.ToStringStaticDraw;

public class MergeMaskOperator extends AbstractDrwOperator implements ITechMergeMask, IFxStr, IBOTypesDrw {

   public MergeMaskOperator(DrwCtx drc) {
      super(drc);
   }

   public int getMergeColor(ByteObject root, ByteObject merge, ByteObject mm) {
      int rcolor = root.get4(ITechFigure.FIG__OFFSET_06_COLOR4);
      if (mm.hasFlag(MERGE_MASK_OFFSET_5VALUES1, MERGE_MASK_FLAG5_2)) {
         rcolor = merge.get4(ITechFigure.FIG__OFFSET_06_COLOR4);
      }
      return rcolor;
   }

   public ByteObject mergeAction(ByteObject root, ByteObject merge) {
      throw new RuntimeException();
   }

   /**
    * Merge over Root.
    * <br>
    * Merge transparency is valid.
    * <br>
    * Takes two ByteObject objects of same root type and merge them with the over operator.
    * If merge layer is opaque, that layer object is returned.
    * Main business of method is to deal with a translucent/incomplete merge DrwParam.
    * In that case both objects must be of the same type. 
    * <br>
    * <br>
    * if merge result is transparent
    * null if both null
    * The result may be incomplete
    * <br>
    * @param root complete or may be null
    * @param merge may be null or maybe an action in which case
    * @return merge if merge is opaque
    */
   public ByteObject mergeDrwParamOver(ByteObject root, ByteObject merge) {
      int type = merge.getType();
      switch (type) {
         case IBOTypesDrw.TYPE_060_TBLR:
            return mergeTBLR(root, merge);
         case IBOTypesDrw.TYPE_051_BOX:
            return mergeBox(root, merge);
         case IBOTypesDrw.TYPE_070_TEXT_EFFECTS:
            //incomplete
            return mergeTxtEffects(root, merge);
         case IBOTypesDrw.TYPE_059_GRADIENT:
            return mergeGradient(root, merge);
         case IBOTypesDrw.TYPE_058_MASK:
            return mergeMask(root, merge);
         case IBOTypesDrw.TYPE_050_FIGURE:
            return mergeFigure(root, merge);
         default:
            return null;
      }
   }

   public ByteObject mergeTBLR(ByteObject root, ByteObject merge) {
      int top = -1;
      if (merge.hasFlag(ITechTblr.TBLR_OFFSET_01_FLAG, ITechTblr.TBLR_FLAG_5_DEF_TOP)) {
         top = getTblrFactory().getTBLRValue(merge, C.POS_0_TOP);
      }
      int bot = -1;
      if (merge.hasFlag(ITechTblr.TBLR_OFFSET_01_FLAG, ITechTblr.TBLR_FLAG_6_DEF_BOT)) {
         bot = getTblrFactory().getTBLRValue(merge, C.POS_1_BOT);
      }
      int left = -1;
      if (merge.hasFlag(ITechTblr.TBLR_OFFSET_01_FLAG, ITechTblr.TBLR_FLAG_7_DEF_LEFT)) {
         left = getTblrFactory().getTBLRValue(merge, C.POS_2_LEFT);
      }
      int right = -1;
      if (merge.hasFlag(ITechTblr.TBLR_OFFSET_01_FLAG, ITechTblr.TBLR_FLAG_8_DEF_RIGHT)) {
         right = getTblrFactory().getTBLRValue(merge, C.POS_3_RIGHT);
      }
      ByteObject nt = getTblrFactory().getTBLR(top, bot, left, right);
      return nt;
   }

   /**
    * 
    * @param root
    * @param merge
    * @return
    */
   private ByteObject mergeBox(ByteObject root, ByteObject merge) {
      int rootHa = root.get1(ITechBox.BOX_OFFSET_02_HORIZ_ALIGN4);
      int rootVa = root.get1(ITechBox.BOX_OFFSET_03_VERTICAL_ALIGN4);
      int rootW = root.get1(ITechBox.BOX_OFFSET_04_WIDTH4);
      int rootH = root.get1(ITechBox.BOX_OFFSET_05_HEIGHT4);
      if (merge.hasFlag(ITechBox.BOX_OFFSET_01_FLAG, ITechBox.BOX_FLAG_7_DEF_HORIZ_ALIGN)) {
         rootHa = merge.get1(ITechBox.BOX_OFFSET_02_HORIZ_ALIGN4);
      }
      if (merge.hasFlag(ITechBox.BOX_OFFSET_01_FLAG, ITechBox.BOX_FLAG_8_DEF_VERT_ALIGN)) {
         rootVa = merge.get1(ITechBox.BOX_OFFSET_03_VERTICAL_ALIGN4);
      }
      if (merge.hasFlag(ITechBox.BOX_OFFSET_01_FLAG, ITechBox.BOX_FLAG_5_DEF_WIDTH)) {
         rootW = merge.get1(ITechBox.BOX_OFFSET_04_WIDTH4);
      }
      if (merge.hasFlag(ITechBox.BOX_OFFSET_01_FLAG, ITechBox.BOX_FLAG_6_DEF_HEIGHT)) {
         rootH = merge.get1(ITechBox.BOX_OFFSET_05_HEIGHT4);
      }
      ByteObject newAnchor = drc.getBoxEng().getAnchor(rootHa, rootVa, rootW, rootH);
      return newAnchor;
   }

   /**
    * non null
    * @param root
    * @param merge
    * @param mm
    * @return
    */
   public ByteObject mergeFigRectangle(ByteObject root, ByteObject merge, ByteObject mm) {
      int arcw = root.get1(ITechFigure.FIG_RECTANGLE_OFFSET_2ARCW1);
      int arch = root.get1(ITechFigure.FIG_RECTANGLE_OFFSET_3ARCH1);
      int size = root.get1(ITechFigure.FIG_RECTANGLE_OFFSET_4SIZEF1);
      if (mm.hasFlag(MERGE_MASK_OFFSET_6VALUES1, MERGE_MASK_FLAG5_1)) {
         arcw = merge.get1(ITechFigure.FIG_RECTANGLE_OFFSET_2ARCW1);
      }
      if (mm.hasFlag(MERGE_MASK_OFFSET_6VALUES1, MERGE_MASK_FLAG5_2)) {
         arch = merge.get1(ITechFigure.FIG_RECTANGLE_OFFSET_3ARCH1);
      }
      if (mm.hasFlag(MERGE_MASK_OFFSET_6VALUES1, MERGE_MASK_FLAG5_3)) {
         size = merge.get1(ITechFigure.FIG_RECTANGLE_OFFSET_4SIZEF1);
      }
      return drc.getFigureFactory().getFigRect(0, arcw, arch, size, null, null, null, null);

   }

   /**
    * 
    * @param root
    * @param merge object on top being inprinted on root
    * @param mergeMask the {@link IMergeMask} definition.
    * @return
    */
   public ByteObject mergeFigString(ByteObject root, ByteObject merge, ByteObject mergeMask) {
      int rface = root.get1(ITechFigure.FIG_STRING_OFFSET_02_FACE1);
      int rstyle = root.get1(ITechFigure.FIG_STRING_OFFSET_3STYLE1);
      int rsize = root.get1(ITechFigure.FIG_STRING_OFFSET_4SIZE1);

      if (mergeMask.hasFlag(MERGE_MASK_OFFSET_6VALUES1, MERGE_MASK_FLAG6_1)) {
         rface = merge.get1(ITechFigure.FIG_STRING_OFFSET_02_FACE1);
      }
      if (mergeMask.hasFlag(MERGE_MASK_OFFSET_6VALUES1, MERGE_MASK_FLAG6_2)) {
         rstyle = merge.get1(ITechFigure.FIG_STRING_OFFSET_3STYLE1);
      }
      if (mergeMask.hasFlag(MERGE_MASK_OFFSET_6VALUES1, MERGE_MASK_FLAG6_3)) {
         rsize = merge.get1(ITechFigure.FIG_STRING_OFFSET_4SIZE1);
      }

      String str = null;
      if (root.hasFlag(ITechFigure.FIG_STRING_OFFSET_01_FLAG, ITechFigure.FIG_STRING_FLAG_6EXPLICIT)) {
         ByteObject raw = root.getSubFirst(IBOTypesBOC.TYPE_003_LIT_STRING);
         str = boc.getLitteralStringOperator().getLitteralString(raw);
      }
      if (merge.hasFlag(ITechFigure.FIG_STRING_OFFSET_01_FLAG, ITechFigure.FIG_STRING_FLAG_6EXPLICIT)) {
         ByteObject raw = merge.getSubFirst(IBOTypesBOC.TYPE_003_LIT_STRING);
         str = boc.getLitteralStringOperator().getLitteralString(raw);
      }

      ByteObject effects = root.getSubFirst(IBOTypesDrw.TYPE_070_TEXT_EFFECTS);
      ByteObject mask = root.getSubFirst(IBOTypesDrw.TYPE_058_MASK);
      ByteObject scale = root.getSubFirst(IBOTypesDrw.TYPE_055_SCALE);
      if (root.hasFlag(ITechFigure.FIG_STRING_OFFSET_01_FLAG, ITechFigure.FIG_STRING_FLAG_5EFFECT)) {

      }
      int rcolor = getMergeColor(root, merge, mergeMask);
      return drc.getFigureFactory().getFigString(str, rface, rstyle, rsize, rcolor, effects, mask, scale);

   }

   /**
    * Merges the two figures definition into a new Definition.
    * <br>
    * <br>
    * A change of type implies a completely different figure
    * <br>
    * <br>
    * Action will clone root figure and create a new one by applying a function on a pointer.
    * <br>
    * 
    * @param root FIGURE TYPE
    * @param merge FIGURE TYPE
    * @return
    */
   public ByteObject mergeFigure(ByteObject root, ByteObject merge) {
      if (merge.getType() == IBOTypesDrw.TYPE_050_FIGURE) {
         ByteObject mergeMask = merge.getSubFirst(IBOTypesBOC.TYPE_011_MERGE_MASK);
         int fig = root.get1(ITechFigure.FIG__OFFSET_01_TYPE1);
         int rcolor = root.get4(ITechFigure.FIG__OFFSET_06_COLOR4);
         int mainFigureFlag = mergeFlag(root, merge, mergeMask, ITechFigure.FIG__OFFSET_02_FLAG, MERGE_MASK_OFFSET_1FLAG1);
         int figurePerfFlags = mergeFlag(root, merge, mergeMask, ITechFigure.FIG__OFFSET_03_FLAGP, MERGE_MASK_OFFSET_2FLAG1);

         if (mergeMask.hasFlag(MERGE_MASK_OFFSET_5VALUES1, MERGE_MASK_FLAG5_1)) {
            fig = merge.get1(ITechFigure.FIG__OFFSET_01_TYPE1);
            //when this happens, the figure does a reverse stamping by only taking the main color and figure
            //attributes (Filter,Gradient,Mask)
            ByteObject newFigure = (ByteObject) merge.clone();
            newFigure.setValue(ITechFigure.FIG__OFFSET_06_COLOR4, rcolor, 4);
            newFigure.setValue(ITechFigure.FIG__OFFSET_02_FLAG, mainFigureFlag, 1);
            newFigure.setValue(ITechFigure.FIG__OFFSET_03_FLAGP, figurePerfFlags, 1);
            return newFigure;
         }
         if (mergeMask.hasFlag(MERGE_MASK_OFFSET_5VALUES1, ITechMergeMaskFigure.MM_VALUES5_FLAG_2_COLOR)) {
            rcolor = merge.get4(ITechFigure.FIG__OFFSET_06_COLOR4);
         }
         ByteObject newFigure = null;
         switch (fig) {
            case ITechFigure.FIG_TYPE_10_STRING:
               newFigure = mergeFigString(root, merge, mergeMask);
               break;
            case ITechFigure.FIG_TYPE_01_RECTANGLE:
               newFigure = mergeFigRectangle(root, merge, mergeMask);
               break;
            default:
               throw new RuntimeException("Not implemented Merge Method for Figure " + ToStringStaticDraw.debugFigType(fig));
         }
         ByteObject grad = root.getSubFirst(IBOTypesDrw.TYPE_059_GRADIENT);
         //TODO when merging figure has a gradient. what happens if root figure also has a gradient? override or merge gradients?
         if (merge.hasFlag(ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_2GRADIENT)) {
            grad = merge.getSubFirst(IBOTypesDrw.TYPE_059_GRADIENT);
         }
         //same for filters?
         ByteObject filter = root.getSubFirst(IBOTypesDrw.TYPE_056_COLOR_FILTER);
         if (merge.hasFlag(ITechFigure.FIG__OFFSET_02_FLAG, ITechFigure.FIG_FLAG_5FILTER)) {
            filter = merge.getSubFirst(IBOTypesDrw.TYPE_056_COLOR_FILTER);
         }
         drc.getFigureFactory().setFigLinks(newFigure, grad, filter, null);
         newFigure.setValue(ITechFigure.FIG__OFFSET_02_FLAG, mainFigureFlag, 1);
         newFigure.setValue(ITechFigure.FIG__OFFSET_03_FLAGP, figurePerfFlags, 1);
         return newFigure;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int mergeFlag(ByteObject root, ByteObject merge, ByteObject mm, int pointer, int mergePointer) {
      int flag = root.get1(pointer);
      int flagM = merge.get1(pointer);
      int flagMM = mm.get1(mergePointer);
      for (int i = 1; i <= 8; i++) {
         if (BitUtils.isBitSet(flagMM, i)) {
            flag = BitUtils.setBit(flag, i, BitUtils.getBit(i, flagM));
         }
      }
      return flag;
   }

   /**
    * Merge 2 gradients when a figure with a gradient is merged with another figure with a gradient.
    * <br>
    * <br>
    * 
    * @param root != null
    * @param merge != null
    * @return
    */
   public ByteObject mergeGradient(ByteObject root, ByteObject merge) {
      int scolor = root.get4(ITechGradient.GRADIENT_OFFSET_04_COLOR4);
      int sec = root.get1(ITechGradient.GRADIENT_OFFSET_05_SEC1);
      int type = root.get1(ITechGradient.GRADIENT_OFFSET_06_TYPE1);
      ByteObject tcolor = root.getSubFirst(IBOTypesBOC.TYPE_002_LIT_INT);
      //get merge mask from incomplete gradient
      ByteObject mm = merge.getSubFirst(IBOTypesBOC.TYPE_011_MERGE_MASK);
      if (mm.hasFlag(MERGE_MASK_OFFSET_5VALUES1, MERGE_MASK_FLAG5_4)) {
         scolor = merge.get4(ITechGradient.GRADIENT_OFFSET_04_COLOR4);
      }
      if (mm.hasFlag(MERGE_MASK_OFFSET_5VALUES1, MERGE_MASK_FLAG5_5)) {
         sec = merge.get1(ITechGradient.GRADIENT_OFFSET_05_SEC1);
      }
      if (mm.hasFlag(MERGE_MASK_OFFSET_5VALUES1, MERGE_MASK_FLAG5_6)) {
         type = merge.get1(ITechGradient.GRADIENT_OFFSET_06_TYPE1);
      }
      if (mm.hasFlag(MERGE_MASK_OFFSET_1FLAG1, ITechGradient.GRADIENT_FLAG_3_THIRD_COLOR)) {
         tcolor = merge.getSubFirst(IBOTypesBOC.TYPE_002_LIT_INT);
      }

      int mainFlag = mergeFlag(root, merge, mm, ITechGradient.GRADIENT_OFFSET_01_FLAG, MERGE_MASK_OFFSET_1FLAG1);
      int exludeFlags = mergeFlag(root, merge, mm, ITechGradient.GRADIENT_OFFSET_02_FLAGK_EXCLUDE, MERGE_MASK_OFFSET_2FLAG1);
      int channelFlags = mergeFlag(root, merge, mm, ITechGradient.GRADIENT_OFFSET_03_FLAGC_CHANNELS, MERGE_MASK_OFFSET_3FLAG1);

      ByteObject newGrad = drc.getGradientFactory().getGradient(scolor, sec, type, mainFlag, exludeFlags, channelFlags, tcolor);
      return newGrad;
   }

   /**
    * Merges two mask definitions.
    * <br>
    * <br>
    * @param root
    * @param merge
    * @return
    */
   public ByteObject mergeMask(ByteObject root, ByteObject merge) {
      int maskBgColor = root.get4(ITechMask.MASK_OFFSET_2COLOR_BG4);
      int maskMidColor = root.get4(ITechMask.MASK_OFFSET_3COLOR_MID4);
      int maskShapeColor = root.get4(ITechMask.MASK_OFFSET_4COLOR_SHAPE4);
      int blendBg = root.get1(ITechMask.MASK_OFFSET_5BLEND_BG1);
      int blendMid = root.get1(ITechMask.MASK_OFFSET_6BLEND_MID1);
      int blendShape = root.get1(ITechMask.MASK_OFFSET_7BLEND_SHAPE1);
      int bgAlpha = root.get1(ITechMask.MASK_OFFSET_8ALPHA_BG1);
      int shapeAlpha = root.get1(ITechMask.MASK_OFFSET_9ALPHA_SHAPE1);

      ByteObject mm = merge.getSubFirst(IBOTypesBOC.TYPE_011_MERGE_MASK);

      if (mm.hasFlag(MERGE_MASK_OFFSET_5VALUES1, MERGE_MASK_FLAG5_2)) {
         maskBgColor = merge.get4(ITechMask.MASK_OFFSET_2COLOR_BG4);
      }
      if (mm.hasFlag(MERGE_MASK_OFFSET_5VALUES1, MERGE_MASK_FLAG5_3)) {
         maskMidColor = merge.get4(ITechMask.MASK_OFFSET_3COLOR_MID4);
      }
      if (mm.hasFlag(MERGE_MASK_OFFSET_5VALUES1, MERGE_MASK_FLAG5_4)) {
         maskShapeColor = merge.get4(ITechMask.MASK_OFFSET_4COLOR_SHAPE4);
      }
      if (mm.hasFlag(MERGE_MASK_OFFSET_5VALUES1, MERGE_MASK_FLAG5_5)) {
         blendBg = merge.get4(ITechMask.MASK_OFFSET_5BLEND_BG1);
      }
      if (mm.hasFlag(MERGE_MASK_OFFSET_5VALUES1, MERGE_MASK_FLAG5_6)) {
         blendMid = merge.get4(ITechMask.MASK_OFFSET_6BLEND_MID1);
      }
      if (mm.hasFlag(MERGE_MASK_OFFSET_5VALUES1, MERGE_MASK_FLAG5_7)) {
         blendShape = merge.get4(ITechMask.MASK_OFFSET_7BLEND_SHAPE1);
      }
      if (mm.hasFlag(MERGE_MASK_OFFSET_5VALUES1, MERGE_MASK_FLAG5_8)) {
         bgAlpha = merge.get4(ITechMask.MASK_OFFSET_8ALPHA_BG1);
      }
      if (mm.hasFlag(MERGE_MASK_OFFSET_5VALUES1, MERGE_MASK_FLAG5_1)) {
         shapeAlpha = merge.get4(ITechMask.MASK_OFFSET_9ALPHA_SHAPE1);
      }
      ByteObject maskFilter = root.getSubFirst(IBOTypesDrw.TYPE_056_COLOR_FILTER);
      if (merge.hasFlag(ITechMask.MASK_OFFSET_1FLAG1, ITechMask.MASK_FLAG_1MASK_FILTER)) {
         maskFilter = merge.getSubFirst(IBOTypesDrw.TYPE_056_COLOR_FILTER);
      }
      ByteObject bgFigure = root.getSubFirst(IBOTypesDrw.TYPE_050_FIGURE);
      if (merge.hasFlag(ITechMask.MASK_OFFSET_1FLAG1, ITechMask.MASK_FLAG_2BG_FIGURE)) {
         bgFigure = merge.getSubFirst(IBOTypesDrw.TYPE_050_FIGURE);
      }
      ByteObject newMask = drc.getMaskFactory().getMask(maskBgColor, maskMidColor, maskShapeColor, bgAlpha, shapeAlpha, blendBg, blendMid, blendShape, maskFilter, bgFigure);
      return newMask;
   }

   /**
    * Merge definitions of text effects.
    * <br>
    * <br>
    * Must be of the same scope otherwise root is returned.
    * <br>
    * <br>
    * @param root
    * @param merge
    * @return
    */
   public ByteObject mergeTxtEffects(ByteObject root, ByteObject merge) {
      int scopeRoot = root.get1(IFxStr.FX_OFFSET_03_SCOPE1);
      if (scopeRoot != merge.get1(FX_OFFSET_03_SCOPE1)) {
         return root;
      }
      //merge the base
      ByteObject ntx = root.cloneCopyHeadRefParams();
      ntx.set1(FX_OFFSET_03_SCOPE1, scopeRoot);
      if (merge.hasFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_4_DEFINED_FONT)) {
         ntx.setFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_4_DEFINED_FONT, true);
         ntx.set1(FX_OFFSET_06_FACE1, merge.get1(FX_OFFSET_06_FACE1));
         ntx.set1(FX_OFFSET_07_STYLE1, merge.get1(FX_OFFSET_07_STYLE1));
         ntx.set1(FX_OFFSET_08_SIZE1, merge.get1(FX_OFFSET_08_SIZE1));

      }
      if (merge.hasFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_5_DEFINED_COLOR)) {
         ntx.set4(FX_OFFSET_09_COLOR4, merge.get4(FX_OFFSET_09_COLOR4));
      }

      if (merge.hasFlag(FX_OFFSET_02_FLAGX, FX_FLAGX_6_DEFINED_INDEX)) {
         ntx.set1(FX_OFFSET_05_INDEX_PATTERN1, merge.get2(FX_OFFSET_05_INDEX_PATTERN1));
         ntx.set2(FX_OFFSET_04_INDEX2, merge.get2(FX_OFFSET_04_INDEX2));
      }
      if (merge.hasFlag(FX_OFFSET_10_FLAGZ, FX_FLAGZ_2_FIGURE)) {
         ntx.addByteObject(merge.getSubAtIndex(TYPE_050_FIGURE));
         ntx.setFlag(FX_OFFSET_10_FLAGZ, FX_FLAGZ_2_FIGURE, true);

      }
      if (merge.hasFlag(FX_OFFSET_10_FLAGZ, FX_FLAGZ_3_MASK)) {
         ntx.addByteObject(merge.getSubAtIndex(TYPE_058_MASK));
         ntx.setFlag(FX_OFFSET_10_FLAGZ, FX_FLAGZ_3_MASK, true);
      }
      return ntx;
   }

}
