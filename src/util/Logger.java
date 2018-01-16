package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	private String fileName = "Log/log.txt";
	private static final SimpleDateFormat timeFormat =
	        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	private PrintStream out;
	private String name;

	public Logger(String name, PrintStream out) {
	    this.name = name;
	    this.out = out;
	}

	public PrintStream getOut() {
        return out;
    }

    private String prefix(String level) {
        return String.format("[%s] %s %s ", timeFormat.format(new Date()), level, name);
    }

    public synchronized void info(String format, Object... args) {
        out.println(prefix("INFO") + String.format(format, args));
    }

    public synchronized void warn(String format, Object... args) {
        out.println(prefix("WARN") + String.format(format, args));
    }
	/*private File file = null;
	FileWriter fw = null;
    BufferedWriter writer = null;
	
	public void openFile(){
		file = new File(fileName);
		if(!file.exists()){
			try {
				file.createNewFile();
				fw = new FileWriter(file);
		        writer = new BufferedWriter(fw);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void log(String str){
		//System.out.println("LOG???");
		try {
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(str);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void closeFile(){
		try {
			writer.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void delFile(){
		File file = new File(fileName);
		try{
			if(file.exists()){
				file.delete();
			}
		} catch(Exception e){
			
		}
	}*/
}
