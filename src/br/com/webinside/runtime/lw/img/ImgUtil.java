package br.com.webinside.runtime.lw.img;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;

import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;

public class ImgUtil {

	public static final int PNG_GRAY = 11;
	public static final int PNG_COLOR = 12;
	public static final int JPG_GRAY = 21;
	public static final int JPG_COLOR = 22;
	
	private static String cmdConvert = null;
	private static String cmdTesseract = null;

	public static String getCmdConvert() {
		if (cmdConvert == null) {
			cmdConvert = cmdFind("convert");
		}
		return cmdConvert;
	}

	public static String getCmdTesseract() {
		if (cmdTesseract == null) {
			cmdTesseract = cmdFind("tesseract");
		}
		return cmdTesseract;
	}
	
	private static String cmdFind(String name) {
		String osName = System.getProperty("os.name").toLowerCase();
		String path = System.getenv("PATH");
		String[] folders = path.split(File.pathSeparator);
		for (String folder : folders) {
			String exec = (osName.indexOf("windows") > -1) ? name + ".exe" : name;
			File execCmd = new File(folder, exec);
			if (execCmd.isFile()) return execCmd.getAbsolutePath();
		}
		if (osName.indexOf("windows") == -1) {
			File execCmd = new File("/usr/local/bin/", name);
			if (execCmd.isFile()) return execCmd.getAbsolutePath();
		}
		return "";
	}

	public static void executeOCR(String folder, String source)
	throws IOException {
		PDDocument pdfDoc = PDDocument.load(folder + "/" + source + ".pdf");
		int pages = pdfDoc.getNumberOfPages();		
		pdfDoc.close();
		if (pages > 10) pages = 10; // provisorio
		int from = 0;
		List<Thread> threads = OcrThread.execute(folder, "ocr", 4);
		while (from < pages) {
			String mask = "[" + from + "-" + (from + 19) + "]";
			execute(ImgUtil.cmdOcrPdfToPng(mask), folder, source, "ocr", true);
			from = from + 10;
		}
		File single = new File(folder, "ocr.png");
		if (single.isFile()) {
			String name =  "ocr-" + (pages-1) + ".png";
			single.renameTo(new File(folder, name));
		}
		try {
			Function.sleep(500);
			new File(folder, "ocr-end.log").createNewFile();
		} catch (IOException e) { }	
		OcrThread.join(threads);
	}
	
	public static int execute(List<String> cmd, String folder, String source, String target, boolean log) {
		if (target == null) target = source; 
		source = source.split("\\.")[0];
		target = target.split("\\.")[0];
		for (int i = 0; i < cmd.size(); i++) {
			String str = cmd.get(i);
			if (str.indexOf("#{source}") > -1) {
				cmd.set(i, StringA.change(str, "#{source}", source));
			}
			if (str.indexOf("#{target}") > -1) {
				cmd.set(i, StringA.change(str, "#{target}", target));
			}
		}
		new File(folder,target).getParentFile().mkdirs();
		ProcessBuilder pb = new ProcessBuilder(cmd);
		pb.directory(new File(folder));
//		System.out.println(pb.command());
		if (log) {
			pb.redirectOutput(new File(folder, target + "-out.log"));
			pb.redirectError(new File(folder, target + "-err.log"));
		} else pb.redirectErrorStream(true);	
		int exitValue = -1;
		try {
			exitValue = pb.start().waitFor();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return exitValue;
	}
	
	public static List<String> cmdOcrPngToTxt() {
		List<String> cmd = new ArrayList<String>();
		cmd.add(getCmdTesseract());
		cmd.add("#{source}.png");
		cmd.add("#{target}");
		cmd.add("-l");
		cmd.add("por");
		return cmd;
	}
	
	public static List<String> cmdOcrPdfToPng(String pages) {
		List<String> cmd = new ArrayList<String>();
		cmd.add(getCmdConvert());
		cmd.add("-density");
		cmd.add("300");
		cmd.add("-colorspace");
		cmd.add("Gray");
		cmd.add("-depth");
		cmd.add("8");
		cmd.add("-alpha");
		cmd.add("remove");
		if (pages == null) pages = "";
		cmd.add("#{source}.pdf" + pages.trim());
		cmd.add("#{target}.png");
		return cmd;
	}
	
	public static List<String> cmdPdfToImg(String pages, ImgEnum type) {
		return cmdPdfToImg(pages, type, 0, "");
	}

	public static List<String> cmdPdfToImg(String pages, ImgEnum type, int density) {
		return cmdPdfToImg(pages, type, density, "");
	}

	public static List<String> cmdPdfToImg(String pages, ImgEnum type, String resize) {
		return cmdPdfToImg(pages, type, 0, resize);
	}
	
	public static List<String> cmdPdfToImg(String pages, ImgEnum type, 
			int density, String resize) {
		List<String> cmd = new ArrayList<String>();
		cmd.add(getCmdConvert());
		int width = 0, height = 0;
		resize = (resize == null ? "" : resize.trim());
		if (resize != null && !resize.equals("")) {
			width = Function.parseInt(StringA.piece(resize,"x",1));
			height = Function.parseInt(StringA.piece(resize,"x",2));
		}
		if (density == 0) {
			density = 100;
			if (width > 400 || height > 500) density = 150;
			if (width > 800 || height > 1000) density = 200;
			if (width > 1200 || height > 1500) density = 250;
			if (width > 1600 || height > 2000) density = 300;
		}
		cmd.add("-density");
		cmd.add(density + "");
		if (!resize.equals("")) {
			cmd.add("-resize");
			cmd.add(resize);
		}
		cmd.add("-colorspace");
		cmd.add(type.isColor() ? "sRGB" : "Gray");
		cmd.add("-depth");
		cmd.add(type.getDepth() + "");
		cmd.add("-alpha");
		cmd.add("remove"); 
		if (pages == null) pages = "";
		cmd.add("#{source}.pdf" + pages.trim());
		cmd.add("#{target}." + type.getExt()); 
		return cmd;
	}
	
}
