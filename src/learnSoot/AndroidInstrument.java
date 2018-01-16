package learnSoot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.InjectUtil;
import soot.Local;
import soot.PackManager;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;
import soot.options.Options;
import util.Logger;
import util.LoggerFactory;
import util.SignApk;
import util.Util;


public class AndroidInstrument {
	
	private static boolean DEBUG = true;
	public static boolean sign = false;
	
	public static void main(String[] args) throws FileNotFoundException {
		Logger logger = LoggerFactory.getLogger("LocalLog","Log/log.txt");
		
		//prefer Android APK files// -src-prec apk
		//Scene.v().setSootClassPath("F:/util.jar;C:/Program Files/Java/jdk1.7.0_80/jre/lib/rt.jar");
		//String path1 = Scene.v().getSootClassPath();
		//System.out.println(path1);
		Options.v().set_src_prec(Options.src_prec_apk);
		//output as APK, too//-f J
		Options.v().set_output_format(Options.output_format_dex);
		Options.v().set_soot_classpath("F:/Experiment/jars/util.jar;"
				+ "F:/Experiment/jars/rt.jar;F:/Experiment/jars/android-25.jar");
		
		try {
			InjectUtil.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        // resolve the PrintStream and System soot-classes
		Scene.v().addBasicClass("java.io.PrintStream",SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.System",SootClass.SIGNATURES);
        for(String s : Scene.v().getBasicClasses()){
        	System.out.println("Classes: " + s);
        }
        //logger.openFile();
        
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
							//logger.log("InvokeMethod: " + invokeExpr.toString() + "\n");
							// logger.log("InvokeName: " + invokeExpr.getMethod().getName().toString() + "\n");
							if(invokeExpr.toString().contains("Toast")){
								//logger.log("Toast: " + invokeExpr + "\n");
								logger.info("Toast: " + invokeExpr);
							}
							if(invokeExpr.getMethod().toString().contains("dalvik.system.DexClassLoader")) {
								System.out.println("InvokeExpr: " + invokeExpr);
								logger.info("ClassLoader: " + invokeExpr);
								logger.info("ClassLoader Args: " + invokeExpr.getArg(0));
								//logger.log("ClassLoader: " + invokeExpr + "\n");
								List<Unit> toInject = new ArrayList<Unit>();
								toInject.addAll(InjectUtil.loggerInfo(b.getLocals(), 
										"global","Loading Class: %s", invokeExpr.getArg(0)));
								units.insertAfter(toInject, u);
								
								b.validate();
						        
						        
							}
						}
						
					});
				}
				InjectUtil.attachFixEHInjectClasses();
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
