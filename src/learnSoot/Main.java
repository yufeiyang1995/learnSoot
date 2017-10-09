package learnSoot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import soot.PackManager;
import soot.Scene;
import soot.Transform;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.source.data.NullSourceSinkDefinitionProvider;
import soot.options.Options;

public class Main {
	public static void main(String[] args) {
		String apkPath = args[0];
		String AndroidJarPath = args[1];
		initialSoot(apkPath,AndroidJarPath);
		
		//PackManager.v().getPack("jtp").add(new Transform("jtp.myAnalysis", new MyAnalysis()));
	}
	
	public static void initialSoot(String apkPath,String androidJarPath){
		Options.v().set_allow_phantom_refs(true);
        Options.v().set_prepend_classpath(true);
        Options.v().set_validate(true);

//        Options.v().set_output_format(Options.output_format_jimple);
        Options.v().set_output_format(Options.output_format_dex);
        
        List<String> processDir = new ArrayList<String>();
        processDir.add(apkPath);
        //processDir.add("/additionalAppClassesBin");
        
        Options.v().set_process_dir(processDir);
        
        Options.v().set_force_android_jar(androidJarPath);
        Options.v().set_src_prec(Options.src_prec_apk);
        Options.v().set_soot_classpath(androidJarPath);
        Scene.v().loadNecessaryClasses();
        PackManager.v().runPacks();
	}
}
