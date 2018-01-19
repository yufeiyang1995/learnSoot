package soot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.reflect.ClassPath;

import soot.Scene;
import soot.SootClass;
import soot.jimple.AssignStmt;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;
import soot.jimple.VirtualInvokeExpr;
import soot.util.Chain;
import util.Logger;
import util.LoggerFactory;
import util.Util;

public class InjectUtil {
	private static List<String> sootClasses = new ArrayList<>();

    public static void load() throws IOException {
        // Load all classes in package fixeh.injectutils
        ClassPath classPath = ClassPath.from(ClassLoader.getSystemClassLoader());
        //System.out.println("Length:"+classPath.getTopLevelClasses("util").size());
        for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClasses("util")) {
            Scene.v().addBasicClass(classInfo.getName(), SootClass.BODIES);
            sootClasses.add(classInfo.getName());
        }
    }
    
    private static boolean attached = false;

    public static synchronized void attachFixEHInjectClasses() {
        if (attached) {
            return;
        }

        for (String className : sootClasses) {
            Scene.v().getSootClass(className).setApplicationClass();
        }

        attached = true;
    }
    
    private static SootClass getLoggerClass(){
    	return Scene.v().getSootClass(Logger.class.getName());
    }
    
    private static SootClass getLoggerFactoryClass(){
    	return Scene.v().getSootClass(LoggerFactory.class.getName());
    }
    
    private static SootClass getUtilClass(){
    	return Scene.v().getSootClass(Util.class.getName());
    }
    
    private static Type getObjectType(){
    	return Scene.v().getType("java.lang.Object");
    }
    
    private static Type getStringType(){
    	return Scene.v().getType("java.lang.String");
    }
    
    private static Type getBooleanType(){
    	return Scene.v().getType("java.lang.Boolean");
    }
    
    private static Local getLogger(String classname, List<Unit> result) {
        SootClass loggerFactoryClass = getLoggerFactoryClass();
        SootClass loggerClass = getLoggerClass();

        // Logger logger = LoggerFactory.getLogger(classname);
        Local logger = Jimple.v().newLocal("fixeh_logger", loggerClass.getType());
        AssignStmt assignStmt = Jimple.v().newAssignStmt(logger,
            Jimple.v().newStaticInvokeExpr(
                loggerFactoryClass
                    .getMethod("getLogger", Collections.singletonList(getStringType()))
                    .makeRef(),
                StringConstant.v(classname)));

        result.add(assignStmt);

        return logger;
    }
    
    private static Local getLogger(String classname, String filename, List<Unit> result) {
        SootClass loggerFactoryClass = getLoggerFactoryClass();
        SootClass loggerClass = getLoggerClass();

        // Logger logger = LoggerFactory.getLogger(classname);
        Local logger = Jimple.v().newLocal("fixeh_logger", loggerClass.getType());
        AssignStmt assignStmt = Jimple.v().newAssignStmt(logger,
            Jimple.v().newStaticInvokeExpr(
                loggerFactoryClass
                    .getMethod("getLogger", Arrays.asList(getStringType(), getStringType()))
                    .makeRef(),
                StringConstant.v(classname), StringConstant.v(filename)));

        result.add(assignStmt);

        return logger;
    }
    
    private static Local getUtil(List<Unit> result){
    	SootClass loggerFactoryClass = getLoggerFactoryClass();
    	SootClass utilClass = getUtilClass();
    	
    	Local util = Jimple.v().newLocal("fixeh_util", utilClass.getType());
    	AssignStmt assignStmt = Jimple.v().newAssignStmt(util, 
    			Jimple.v().newStaticInvokeExpr(
    					loggerFactoryClass.getMethod("getUtil",Arrays.asList())
    					.makeRef()));
    	result.add(assignStmt);
    	
    	return util;
    }
    
    public static List<Unit> moveFile(Chain<Local> locals,Value src,String tar){
		List<Unit> result = new ArrayList<>();

    	SootClass utilClass = getUtilClass();
    	
    	Local util = getUtil(result);
    	locals.add(util);
    	
    	Local source = Jimple.v().newLocal("source", getStringType());
    	AssignStmt assignStmt = Jimple.v().newAssignStmt(source, src);
    	result.add(assignStmt);
    	locals.add(source);
    	
    	VirtualInvokeExpr invokeExpr = Jimple.v().newVirtualInvokeExpr(util,
				utilClass
						.getMethod(SootMethod.getSubSignature("copyFileToDir",
								Arrays.asList(getStringType(), getStringType()), VoidType.v()))
						.makeRef(),
				Arrays.asList(source, StringConstant.v(tar)));
		result.add(Jimple.v().newInvokeStmt(invokeExpr));
    	return result;
    }
    
	public static List<Unit> loggerInfo(Chain<Local> locals, String classname, String format, Value... values) {
		List<Unit> result = new ArrayList<>();

		SootClass loggerClass = getLoggerClass();
		Local logger = getLogger(classname, LoggerFactory.LOGFILE, result);
		locals.add(logger);

		// logger.info(format, values)
		// <fixeh.injectutils.Logger: void
		// info(java.lang.String,java.lang.Object[])>

		Local args = Jimple.v().newLocal("fixeh_loggerArgs", getObjectType().getArrayType());
		locals.add(args);
		result.add(Jimple.v().newAssignStmt(args,
				Jimple.v().newNewArrayExpr(getObjectType(), IntConstant.v(values.length))));
		// Fill array
		for (int i = 0; i < values.length; ++i) {
			result.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(args, IntConstant.v(i)), values[i]));
		}

		VirtualInvokeExpr invokeExpr = Jimple.v().newVirtualInvokeExpr(logger,
				loggerClass
						.getMethod(SootMethod.getSubSignature("info",
								Arrays.asList(getStringType(), getObjectType().getArrayType()), VoidType.v()))
						.makeRef(),
				Arrays.asList(StringConstant.v(format), args));
		result.add(Jimple.v().newInvokeStmt(invokeExpr));
		return result;
	}
	
	
}
