package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SignApk {
	public void signApk(String apk) {
		try {
			// jarsigner is part of the Java SDK
			System.out.println("Signing " + apk);
			String cmd = "jarsigner -verbose -digestalg SHA1 -sigalg MD5withRSA -storepass android -keystore "+System.getProperty("user.home")+"/.android/debug.keystore "
				+ apk + " androiddebugkey";
			System.out.println("Calling "+ cmd);
			Process p = Runtime.getRuntime().exec(cmd);
			printProcessOutput(p);

			// zipalign is part of the Android SDK
//			log.info("Zipalign ...", apk);
//			cmd = "zipalign -v 4 " + apk + " " + new File(apk).getName() + "_signed.apk";
//			log.debug(cmd);
//			p = Runtime.getRuntime().exec(cmd);
//			printProcessOutput(p);
		} catch (IOException e) {
			System.out.println(e.getMessage() + e);
		}
	}
	
	private void printProcessOutput(Process p) throws IOException{
		String line;
		BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		while ((line = input.readLine()) != null) {
		  System.out.println(line);
		}
		input.close();
		
		input = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		while ((line = input.readLine()) != null) {
		  System.out.println(line);
		}
		input.close();
	}
}
