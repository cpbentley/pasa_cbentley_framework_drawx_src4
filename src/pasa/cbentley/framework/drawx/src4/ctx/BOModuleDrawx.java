/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.ctx;

import pasa.cbentley.byteobjects.src4.core.BOModuleAbstract;
import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesBOC;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesDrw;
import pasa.cbentley.byteobjects.src4.ctx.ToStringStaticBO;
import pasa.cbentley.byteobjects.src4.objects.color.ColorFunction;
import pasa.cbentley.byteobjects.src4.objects.color.GradientFunction;
import pasa.cbentley.byteobjects.src4.objects.color.IBOFilter;
import pasa.cbentley.byteobjects.src4.objects.function.ITechFunction;
import pasa.cbentley.core.src4.ctx.ToStringStaticUc;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.logging.IDebugStringable;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.engine.RgbCache;
import pasa.cbentley.framework.drawx.src4.interfaces.IToStringsDIDsDraw;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;
import pasa.cbentley.framework.drawx.src4.tech.ITechFunctionDraw;
import pasa.cbentley.framework.drawx.src4.utils.ShaderFunction;

/**
 * Drawing Parameter class encapsulates a byte array for declarative definitions of Drawables.
 * <li>Style</li>
 * <li>Figures (Rectangles,  Triangles, Ellipses) </li>
 * <li>Pixels</li>
 * 
 * <br>
 * Act as an aggregator of {@link GraphicsX} function calls.
 * <br>
 * <br>
 * <b>History</b>: <br>
 * ByteObject was created originally to have pointers on int literals to have a single increment/decrement function for
 * all int fields of an object.
 * <br>This method is {@link ByteObject#increment(int, int)}. <br>
 * Thus we could reference a field using an Integer and increment/decrement that field using this pointer integer. 
 * <br>
 * Java cannot abstract a field and apply a function to it. You can abstract the function, but you still have to call.
 * <br>
 * Schematically 
 * <li>With Java : class.field = function(class.field)
 * <li>With ByteObject : function(class,pointer). Pointer identifies field.
 * <br>
 * Now a function is able to work on a {@link IBOTypesDrw#TYPE_POINTER} defining an offset and a pointer byte size.
 * <br>
 * <b>Philosophy</b>. 
 * <li> ByteObject framework is designed for fast creation, many instances. Generally, create instead of modify
 * <li>Variable sized definitions. The array of ByteObject allows for a growing set of fields.<br>
 * <li>It packs data in a few bytes as possible. Designed to support styling of drawables. 
 * <br>
 * <br>
 * The philosophy is that since it is cheap on memory, dev code should never modifies a DrwParam, but create a new one.
 * <br>
 * Though in some cases it is efficient for automated code like animations to do so.
 * <br>
 * <br>
 * <b>Pooling</b>
 * <br>
 * {@link ByteObjectRepository} stores a byte[] array that contains all pooled ByteObject definitions.
 * <br>
 * Advantage is all ByteObject don't waste the 20 bytes on a java array size overhead.  the DrwParam
 * byte header takes more. (6 bytes), twice less than 12 bytes used by a Java object.
 * Drawables with extra children generate a ByteObject[] array.
 * {@link ByteObject#getRefID()} return pointer in reference repository.
 * <br>
 * <br>
 * For dynamic TBLR figures, it is best to clone blueprint, modify TBLR data, draw figure
 * <br>
 * <br>
 * <b>Declarative advantages</b>:
 * <br>
 * Easy transportation and storage.<br>
 * very high abstraction (just styleKey and ByteObject elements).
 * <br>
 * <br>
 * <b>Properties</b> :
 * <li>Support transparent/partial definitions. Flags that tell if a value is defined or not.
 * <li>DrwParam garbage collection will sweep away unused elements
 * <br>
 * <br>
 * <b>Technical</b>. <br>
 * All ByteObject share the same base 4 bytes header
 * <br>
 * <br>
 * <p>
 * First byte defines the type of Parameter encoding
 * <br>
 * <br>
 * There are 2 ways to get sub figures
 * <li>Read it the param array {@link ByteObject#param}
 * <li>Read an offset that is the pool memory pointers
 * <br>
 * <br>
 * <b>Mutation Discussion</b>:
 * <br>
 * Can business code modifies {@link ByteObject} objects? It is contrary to the philosophy of lightweight, create new one for each modifications.
 * <li> {@link ByteObject#increaseVersionCount()}
 * <li> {@link ByteObject#setMutable(boolean)}
 * <br>
 *  Drawable caching the content of ByteObject figure must be able to know if figure has been modified, so it can update the cache.
 * <br>
 * <br>
 * @author Charles-Philip Bentley
 *
 */
public class BOModuleDrawx extends BOModuleAbstract implements ITechFigure, IBOTypesDrw, IDebugStringable, IToStringsDIDsDraw, IBOFilter {

   protected final DrwCtx drc;

   /**
    * Contructor for the .
    * Will be created in the {@link RgbCache}
    * <br>
    * @param rootMod
    */
   public BOModuleDrawx(DrwCtx drc) {
      super(drc.getBOC());
      this.drc = drc;
   }

   public ByteObject getFlagOrderedBO(ByteObject bo, int offset, int flag) {
      int type = bo.getType();
      switch (type) {
         case TYPE_071_STYLE:
            return drc.getStyleOperator().getStyleDrw(bo, offset, flag);
      }
      return null;
   }

   //#mdebug
   /**
    * Returns the String associated with the DID.
    */
   public String toStringGetDIDString(int did, int value) {
      switch (did) {
         case IToStringsDIDsDraw.DID_07_DRAWABLE_IMPLICIT_W_H:
            return ToStringStaticDrawx.toStringImplicit(value);
         case IToStringsDIDsDraw.DID_05_MASK_BLEND:
            return ToStringStaticDrawx.toStringMaskBlend(value);
         case IToStringsDIDsDraw.DID_06_MASK_PRESET:
            return ToStringStaticDrawx.toStringMaskPreset(value);
         case IToStringsDIDsDraw.DID_12_SKEW_EDGE_TYPES:
            return ToStringStaticDrawx.toStringEdge(value);
         case IToStringsDIDsDraw.DID_11_INTERPOLATION:
            return ToStringStaticDrawx.toStringInterpol(value);
         case IToStringsDIDsDraw.DID_14_PASS:
            return ToStringStaticDrawx.toStringPass(value);
         default:
            return null;
      }
   }
   //#enddebug

   public ByteObject merge(ByteObject root, ByteObject merge) {
      int type = merge.getType();
      switch (type) {
         case TYPE_050_FIGURE:
            return drc.getFigureOperator().mergeFigure(root, merge);
         case TYPE_051_BOX:
            return drc.getBoxFactory().mergeBox(root, merge);
         case TYPE_058_MASK:
            return drc.getMaskOperator().mergeMask(root, merge);
         case TYPE_069_ANCHOR:
            return drc.getAnchorFactory().mergeAnchor(root, merge);
         case TYPE_071_STYLE:
            return drc.getStyleOperator().mergeStyle(root, merge);
         case TYPE_070_TEXT_EFFECTS:
            return drc.getFxStringOperator().mergeTxtEffects(root, merge);
      }
      return null;
   }

   /**
    * Called by {@link FunctionCreator} when {@link IFunction#FUN_FLAG_6EXTENSION} is set.
    * <br>
    * Based on extension type, we create 
    * <li>{@link GradientFunction}
    * <li>{@link ColorFunction}
    * 
    */
   public Object createExtension(int type, ByteObject def) {
      switch (type) {
         case IBOTypesBOC.TYPE_021_FUNCTION:
            //check the def if gradient create
            int ftype = def.get2(ITechFunction.FUN_OFFSET_09_EXTENSION_TYPE2);
            switch (ftype) {
               case ITechFunctionDraw.TYPEX_FUN_400_SHADER:
                  return new ShaderFunction(drc,def);
               default:
                  return null;
            }
         default:
            break;
      }
      return null;
   }

   public int[] getArrayFrom(ByteObject bo, int[] param) {
      final int type = bo.getType();
      switch (type) {
         case IBOTypesDrw.TYPE_059_GRADIENT:
            GradientFunction gf = new GradientFunction(boc);
            int gradSize = param[0];
            int primaryColor = param[1];
            gf.init(primaryColor, gradSize, bo);
            return gf.getColors();
      }
      return null;
   }

   /**
    * Displays a name of the offset field. Reflection on the field.
    * <br>
    * @param type
    * @return
    */
   public String subToStringOffset(ByteObject o, int offset) {
      int type = o.getType();
      switch (type) {
         default:
            return null;
      }
   }

   /**
    * Class outside the framework implement this method
    * @param type
    * @return null if not found
    */
   public String subToStringType(int type) {
      switch (type) {
         default:
            return null;
      }
   }

   public void toString(Dctx dc) {
      dc.root(this, "BOModuleDrawx");
      toStringPrivate(dc);
      super.toString(dc.sup());

      dc.appendVarWithNewLine(toStringType(TYPE_050_FIGURE), TYPE_050_FIGURE);
      dc.appendVarWithNewLine(toStringType(TYPE_051_BOX), TYPE_051_BOX);

   }

   //#mdebug

   public boolean toString(Dctx sb, ByteObject bo) {
      final int type = bo.getType();
      switch (type) {
         case IBOTypesDrw.TYPE_055_SCALE:
            drc.getScalerFactory().toStringScaler(bo, sb);
            break;
         case IBOTypesDrw.TYPE_052_ARTIFACT:
            drc.getArtifactFactory().toStringArtifact(bo, sb);
            break;
         case IBOTypesDrw.TYPE_050_FIGURE:
            drc.getFigureFactory().toStringFigure(bo, sb);
            break;
         case IBOTypesDrw.TYPE_051_BOX:
            //we need to give the context
            drc.getBoxFactory().toStringBox(bo, sb);
            break;
         case IBOTypesDrw.TYPE_058_MASK:
            drc.getMaskFactory().toStringMask(bo, sb);
            break;
         case IBOTypesDrw.TYPE_069_ANCHOR:
            drc.getAnchorFactory().toStringAnchor(bo, sb);
            break;
         case TYPE_071_STYLE:
            drc.getStyleOperator().toStringStyle(bo, sb);
            break;
         case TYPE_070_TEXT_EFFECTS:
            drc.getFxStringOperator().toStringTxtEffect(bo, sb);
            break;
         case TYPE_072_FX_APPLICATOR:
            drc.getFxStringOperator().toStringFxApplicator(bo, sb);
            break;
         default:
            return false;
      }
      return true;
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, "BOModuleDrawx");
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   public boolean toString1Line(Dctx dc, ByteObject bo) {
      final int type = bo.getType();
      switch (type) {
         case IBOTypesDrw.TYPE_050_FIGURE:
            drc.getFigureFactory().toStringFigure1Line(bo, dc);
            break;
         case IBOTypesDrw.TYPE_051_BOX:
            drc.getBoxFactory().toStringBox(bo, dc);
            break;
         case IBOTypesDrw.TYPE_052_ARTIFACT:
            drc.getArtifactFactory().toStringArtifact(bo, dc);
            break;
         case IBOTypesDrw.TYPE_055_SCALE:
            drc.getScalerFactory().toStringScaler(bo, dc);
            break;
         case IBOTypesDrw.TYPE_058_MASK:
            drc.getMaskFactory().toStringMask(bo, dc);
            break;
         case IBOTypesDrw.TYPE_069_ANCHOR:
            drc.getAnchorFactory().toStringAnchor(bo, dc);
            break;
         case TYPE_071_STYLE:
            drc.getStyleOperator().toString1LineStyle(bo, dc);
            break;
         case TYPE_070_TEXT_EFFECTS:
            drc.getFxStringOperator().toString1LineTxtEffect(bo, dc);
            break;
         case TYPE_072_FX_APPLICATOR:
            drc.getFxStringOperator().toString1LineFxApplicator(bo, dc);
            break;
         default:
            return false;
      }
      return true;
   }

   public String toStringOffset(ByteObject o, int offset) {
      // TODO Auto-generated method stub
      return null;
   }

   private void toStringPrivate(Dctx dc) {

   }

   public String toStringType(int type) {
      switch (type) {
         case TYPE_050_FIGURE:
            return "Figure";
         case TYPE_051_BOX:
            return "Box";
         case TYPE_052_ARTIFACT:
            return "Artifact";
         case TYPE_053_MOSAIC:
            return "Mosaic";
         case TYPE_059_GRADIENT:
            return "Gradient";
         case TYPE_061_COLOR_RANDOM:
            return "ColorRandomizer";
         case TYPE_062_BLENDER:
            return "Blender";
         case TYPE_063_PIX_STAR:
            return "PixStar";
         case TYPE_069_ANCHOR:
            return "Anchor";
         case TYPE_070_TEXT_EFFECTS:
            return "TextEffects";
         case TYPE_071_STYLE:
            return "Style";
      }
      return null;
   }
   //#enddebug
}
