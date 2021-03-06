package learnSoot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import soot.ArrayType;
import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Local;
import soot.PackManager;
import soot.PatchingChain;
import soot.Printer;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SourceLocator;
import soot.Transform;
import soot.Type;
import soot.Unit;
import soot.VoidType;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.JasminClass;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.StringConstant;
import soot.options.Options;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.util.Chain;
import soot.util.JasminOutputStream;
import util.Logger;
import util.LoggerFactory;
import util.SignApk;
import util.Util;

public class Main {

private static boolean DEBUG = false;
	
	public static void main(String[] args) throws FileNotFoundException {
		Logger logger = LoggerFactory.getLogger("LocalLog","Log/log.txt");
		
		//prefer Android APK files// -src-prec apk
		Options.v().set_src_prec(Options.src_prec_apk);
		
		//output as APK, too//-f J
		Options.v().set_output_format(Options.output_format_dex);
		Options.v().set_allow_phantom_refs(true);
        Options.v().set_process_multiple_dex(true);
		Options.v().set_soot_classpath(
				"F:/Experiment/jars/rt.jar;F:/Experiment/jars/android-25.jar;"
				+ "F:/Experiment/jars/httpcomponents-client-4.5.4/lib/httpclient-4.5.4.jar;"
				+ "F:/Experiment/jars/httpcomponents-client-4.5.4/lib/httpcore-4.4.7.jar;"
				+ "F:/Experiment/jars/httpcomponents-client-4.5.4/lib/commons-logging-1.2.jar");
		//Options.v().set_android_api_version(23);
		
        // resolve the PrintStream and System soot-classes
		Scene.v().addBasicClass("java.io.PrintStream",SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.System",SootClass.SIGNATURES);

        PackManager.v().getPack("jtp").add(new Transform("jtp.myInstrumenter", new BodyTransformer() {

			@Override
			protected void internalTransform(final Body b, String phaseName, @SuppressWarnings("rawtypes") Map options) {
				final PatchingChain<Unit> units = b.getUnits();
				
				//important to use snapshotIterator here
				for(Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext();) {
					final Unit u = iter.next();
					u.apply(new AbstractStmtSwitch() {
						
						public void caseInvokeStmt(InvokeStmt stmt) {
							InvokeExpr invokeExpr = stmt.getInvokeExpr();
							/*
							if(invokeExpr.getMethod().toString().contains("dalvik.system.DexClassLoader")) {
								//System.out.println("InvokeExpr: " + invokeExpr);
								logger.info("ClassLoader: " + invokeExpr);
								Local tmpRef = addTmpRef(b);
								Local tmpString = addTmpString(b);
								
								  // insert "tmpRef = java.lang.System.out;" 
						        units.insertBefore(Jimple.v().newAssignStmt( 
						                      tmpRef, Jimple.v().newStaticFieldRef( 
						                      Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef())), u);

						        // insert "tmpLong = 'HELLO';" 
						        units.insertBefore(Jimple.v().newAssignStmt(tmpString, 
						                      StringConstant.v("HELLO")), u);
						        
						        // insert "tmpRef.println(tmpString);" 
						        SootMethod toCall = Scene.v().getSootClass("java.io.PrintStream").getMethod("void println(java.lang.String)");                    
						        units.insertBefore(Jimple.v().newInvokeStmt(
						                      Jimple.v().newVirtualInvokeExpr(tmpRef, toCall.makeRef(), tmpString)), u);
						        
						        //check that we did not mess up the Jimple
						        b.validate();
							}*/
						}
						
					});
				}
			}


		}));
		
		soot.Main.main(args);
		Util pathUtil = new Util();
		if(DEBUG){
			for(int i = 0;i < args.length;i++){
				System.out.println("ARGS: " + args[i]);
			}
			System.out.println("apk:" + pathUtil.getApk(args[3]));
		}
		
		String sootOutApk = "sootOutput" + File.separator + pathUtil.getApk(args[3]);
		System.out.println("SOOTOUTAPK: " + sootOutApk);
		SignApk signApk = new SignApk();
		signApk.signApk(sootOutApk);
		
	}

    private static Local addTmpRef(Body body)
    {
        Local tmpRef = Jimple.v().newLocal("tmpRef", RefType.v("java.io.PrintStream"));
        body.getLocals().add(tmpRef);
        return tmpRef;
    }
    
    private static Local addTmpString(Body body)
    {
        Local tmpString = Jimple.v().newLocal("tmpString", RefType.v("java.lang.String")); 
        body.getLocals().add(tmpString);
        return tmpString;
    }

}