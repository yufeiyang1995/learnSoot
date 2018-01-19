package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Util {
	public static String apkPath = "sootOutput";
	public static String apkName = "";
	public static String apkNameDeployed = "";
	private static boolean DEBUG = false;
	
	public String getApk(String path){
		String[] pathList = path.split("/");
		
		if(DEBUG){
			for(int i = 0;i < pathList.length;i++){
				System.out.println("LIST: " + pathList[i]);
			}
		}
		
		if(pathList.length > 0){
			apkName = pathList[pathList.length - 1];
			apkNameDeployed = "deploy-" + apkName;
			return pathList[pathList.length - 1];
		}
		return null;
	}
	
	public void copyFileToDir(String srcFile, String destDir){
        File fileDir = new File(destDir);
        if (!fileDir.exists()) {
            System.out.println("creating " + destDir);
            fileDir.mkdirs();
        }
        String destFile = destDir +"/" + new File(srcFile).getName();
        System.out.println("Writing to " + destFile);
        try{
            InputStream streamFrom = new FileInputStream(srcFile);
            OutputStream streamTo = new FileOutputStream(destFile);

            byte buffer[]=new byte[1024];
            int len;
            while ((len= streamFrom.read(buffer)) > 0){
                streamTo.write(buffer, 0, len);
            }
            streamFrom.close();
            streamTo.close();
            //return true;
        } catch(Exception ex){
            //return false;
        }
    }
}
