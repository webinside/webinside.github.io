package br.com.webinside.runtime.lw.img;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.com.webinside.runtime.util.Function;

public class OcrThread extends Thread {

	private String folder = null;
	private String name = null;
	private int size = 0;
	private int pos = 0;
	
	public static List<Thread> execute(String folder, String name, int size) {
		List<Thread> threads = new ArrayList<Thread>();
		if (size < 2) size = 2;
		for (int i = 1; i <= size; i++) {
			Thread thread = new OcrThread(folder, name, size, i);
			threads.add(thread);
			thread.start();
		}
		return threads;
	}
	
	public static void join(List<Thread> threads) {
		try {
			for (Thread thread : threads) {
			  thread.join();
			}
		} catch (Exception e) {
			// ignorado
		}
	}
	
    public OcrThread(String folder, String name, int size, int seq) {
		super("OcrThread - " + seq + "/" + size);
		this.folder = folder;
		this.name = name;
		this.size = size;
		this.pos = seq - 1;
    }

    public void run() {
    	boolean loop = true;
    	while (loop) {
    		boolean exec = false;
    		File fProx = new File(folder, name + "-" + (pos + 1) + ".png");
    		File fEnd = new File(folder, name + "-end.log");
    		if (fProx.isFile() || fEnd.isFile()) exec = true;
    		if (exec) {
        		File fMe = new File(folder, name + "-" + pos + ".png");
    			if (fMe.isFile()) {
        			List<String> cmd = ImgUtil.cmdOcrPngToTxt(); 
            		ImgUtil.execute(cmd, folder, name + "-" + pos, null, false);
            		pos = pos + size;
    			} else {
        			loop = false;
    			}
    		} else {
    			Function.sleep(200);
    		}
    	}
    }
	
}
