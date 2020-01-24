package pasa.cbentley.framework.drawx.src4.param;
//package mordan.draw.param;
//
//import mordan.draw.interfaces.IDrw;
//import mordan.draw.interfaces.IDrwTypes;
//import mordan.memory.BOModule;
//import pasa.cbentley.byteobjects.core.ByteObject;
//import pasa.cbentley.core.src4.interfaces.C;
//
///**
// * Control creation of Triangle based Figures.
// * <br>
// * <li>Triangle
// * <li>Losange
// * @author Charles-Philip Bentley
// *
// */
//public class FigTriangleC {
//
//   /**
//    * Some flag draw a Noeud Pap
//    * Many different types of gradient since we have 2 triangles
//    * <br>
//    * <br>
//    * TODO add a losange proportion. 1 byte decide ratio for Top/Left, 1 byte for Bot/Right
//    * @param color
//    * @param overstep
//    * @param horiz
//    * @return
//    */
//   public static ByteObject getFigLosange(BOModule mod,int color, int overstep, boolean horiz, boolean pap, boolean contour, ByteObject grad) {
//	 ByteObject p = ByteObject.createByteObject(mod,IDrwTypes.TYPE_050_FIGURE, IDrw.FIG_LOSANGE_BASIC_SIZE);
//	 p.setValue(IDrw.FIG__OFFSET_01_TYPE1, IDrw.FIG_TYPE_06_LOSANGE, 1);
//	 p.setValue(IDrw.FIG__OFFSET_06_COLOR4, color, 4);
//	 p.setValue(IDrw.FIG_LOSANGE_OFFSET_2OVERSTEP2, overstep, 2);
//	 p.setFlag(IDrw.FIG_LOSANGE_OFFSET_1FLAG, IDrw.FIG_LOSANGE_FLAG_1HORIZ, horiz);
//	 p.setFlag(IDrw.FIG_LOSANGE_OFFSET_1FLAG, IDrw.FIG_LOSANGE_FLAG_2NEG_OVERSTEP, overstep < 0);
//	 p.setFlag(IDrw.FIG_LOSANGE_OFFSET_1FLAG, IDrw.FIG_LOSANGE_FLAG_3CONTOUR, contour);
//	 p.setFlag(IDrw.FIG_LOSANGE_OFFSET_1FLAG, IDrw.FIG_LOSANGE_FLAG_4NOED_PAPILLION, pap);
//	 DrwParamFig.setFigLinks(p, grad, null, null);
//	 return p;
//   }
//
//   /**
//    * A losange definition from a Triangle. 
//    * <br>
//    * <br>
//    * The opposite direction is used for the other part
//    * @param trig
//    * @return
//    */
//   public static ByteObject getFigLosange(BOModule mod,ByteObject trig, boolean pap, boolean contour) {
//	 ByteObject p = ByteObject.createByteObject(mod,IDrwTypes.TYPE_050_FIGURE, IDrw.FIG_LOSANGE_BASIC_SIZE);
//	 p.setFlag(IDrw.FIG_LOSANGE_OFFSET_1FLAG, IDrw.FIG_LOSANGE_FLAG_4NOED_PAPILLION, pap);
//	 p.addByteObject(trig);
//	 return p;
//   }
//
//   public static ByteObject getFigLosange(BOModule mod,ByteObject trig1, ByteObject trig2) {
//	 ByteObject p = ByteObject.createByteObject(mod,IDrwTypes.TYPE_050_FIGURE, IDrw.FIG_LOSANGE_BASIC_SIZE);
//
//	 return p;
//   }
//
//   /**
//    * 
//    * @param color
//    * @param angle
//    * @return
//    */
//   public static ByteObject getFigTriangle(BOModule mod,int color, int angle) {
//	 return getFigTriangle(mod,color, angle, 0, null, null);
//   }
//
//   public static ByteObject getFigTriangle(BOModule mod,int angle, int color, ByteObject grad, ByteObject anchor, ByteObject[] subs) {
//	 ByteObject p = ByteObject.createByteObject(mod,IDrwTypes.TYPE_050_FIGURE, IDrw.FIG_TRIANGLE_BASIC_SIZE);
//	 p.setValue(IDrw.FIG__OFFSET_01_TYPE1, IDrw.FIG_TYPE_3_TRIANGLE, 1);
//	 p.setValue(IDrw.FIG__OFFSET_06_COLOR4, color, 4);
//	 //custom values of triangle
//	 p.setValue(IDrw.FIG_TRIANGLE_OFFSET_2ANGLE2, angle, 2);
//	 DrwParamFig.setFigLinks(p, grad, anchor, subs);
//	 return p;
//   }
//
//   /**
//    * Basic Triangle with one color. no anchor
//    * Default is intuitive
//    * Top is 
//    * @param angle
//    * @param color
//    * @param percent
//    * @return
//    */
//   public static ByteObject getFigTriangle(BOModule mod,int color, int angle, int h) {
//	 return getFigTriangle(mod,color, angle, h, null, null);
//   }
//
//   /**
//    * 
//    * @param color
//    * @param angle when angle is 0 - 90 - 180 - 270, put to a single direction.
//    * @param h as a percentage
//    * @param isAngle
//    * @param grad
//    * @param anchor
//    * @param filter
//    * @param subs
//    * @return
//    */
//   public static ByteObject getFigTriangle(BOModule mod,int color, int angle, int h, boolean isAngle, ByteObject grad, ByteObject anchor, ByteObject filter, ByteObject[] subs) {
//	 ByteObject p = ByteObject.createByteObject(mod,IDrwTypes.TYPE_050_FIGURE, IDrw.FIG_TRIANGLE_BASIC_SIZE);
//	 p.setValue(IDrw.FIG__OFFSET_01_TYPE1, IDrw.FIG_TYPE_3_TRIANGLE, 1);
//	 p.setValue(IDrw.FIG__OFFSET_06_COLOR4, color, 4);
//	 p.setValue(IDrw.FIG_TRIANGLE_OFFSET_2ANGLE2, angle, 2);
//	 p.setValue(IDrw.FIG_TRIANGLE_OFFSET_3h4, h, 4);
//	 DrwParamFig.setFigLinks(p, grad, anchor, filter, subs);
//	 p.setFlag(IDrw.FIG_TRIANGLE_OFFSET_1FLAG1, IDrw.FIG_TRIANGLE_FLAG_2ANGLE, isAngle);
//	 return p;
//   }
//
//   /**
//    * 
//    * @param color
//    * @param angle 0-360 degree angle
//    * @param h
//    * @param grad
//    * @return
//    */
//   public static ByteObject getFigTriangle(BOModule mod,int color, int angle, int h, ByteObject grad) {
//	 return getFigTriangle(mod,color, angle, h, grad, null, null, null);
//   }
//
//   /**
//    * Create a triangle with an anchor
//    * @param anchor 32 bits anchor
//    * @param angle
//    * @param color
//    * @param h in percent of big H
//    * @return
//    */
//   public static ByteObject getFigTriangle(BOModule mod,int color, int angle, int h, ByteObject grad, ByteObject anchor) {
//	 return getFigTriangle(mod,color, angle, h, grad, anchor, null, null);
//   }
//
//   public static ByteObject getFigTriangle(BOModule mod,int color, int angle, int h, ByteObject grad, ByteObject anchor, ByteObject filter, ByteObject[] subs) {
//	 return getFigTriangle(mod,color, angle, h, true, grad, anchor, filter, subs);
//   }
//
//   /**
//    * 
//    * @param color
//    * @param type {@link C#TYPE_00TOP}
//    * @param grad
//    * @param anchor
//    * @return
//    */
//   public static ByteObject getFigTriangleType(BOModule mod,int color, int type, ByteObject grad, ByteObject anchor) {
//	 ByteObject p = ByteObject.createByteObject(mod,IDrwTypes.TYPE_050_FIGURE, IDrw.FIG_TRIANGLE_BASIC_SIZE);
//	 p.setValue(IDrw.FIG__OFFSET_01_TYPE1, IDrw.FIG_TYPE_3_TRIANGLE, 1);
//	 p.setValue(IDrw.FIG__OFFSET_06_COLOR4, color, 4);
//	 p.setValue(IDrw.FIG_TRIANGLE_OFFSET_2ANGLE2, type, 2);
//	 DrwParamFig.setFigLinks(p, grad, anchor, null, null);
//	 return p;
//   }
//
//   /**
//    * 
//    * @param color
//    * @param type {@link C#TYPE_00TOP} etc.
//    * @param grad
//    * @return
//    */
//   public static ByteObject getFigTriangleType(BOModule mod,int color, int type, ByteObject grad) {
//	 return getFigTriangleType(mod,color, type, grad, null);
//   }
//
//   public static ByteObject getFigTriangleType(BOModule mod,int color, int type) {
//	 return getFigTriangleType(mod,color, type, null);
//   }
//
//   public static ByteObject getFigTriangleDir(BOModule mod,int color, int dir, int h, ByteObject grad, ByteObject anchor, ByteObject filter, ByteObject[] subs) {
//	 return getFigTriangle(mod,color, dir, h, false, grad, anchor, filter, subs);
//   }
//
//}
