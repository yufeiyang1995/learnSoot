package util;

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
}
