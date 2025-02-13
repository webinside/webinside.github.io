package br.com.webinside.runtime.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class CmdUtil {
	
	private static final boolean LINUX = 
			System.getProperty("os.name").toLowerCase().indexOf("windows") == -1;
	
	private static String cmdChrome = null;
	private static String cmdOffice = null;
	private static String cmdConvert = null;
	private static String cmdGhostScript = null;
	private static String cmdTesseract = null;

	public static int execute(String folder, List<String> cmd) {
		return execute(folder, cmd, false);
	}

	public static int execute(String folder, List<String> cmd, boolean log) {
		ProcessBuilder pb = new ProcessBuilder(cmd);
		pb.directory(new File(folder));
		if (log) {
			pb.redirectOutput(new File(folder, "cmd-out.log"));
			pb.redirectError(new File(folder, "cmd-err.log"));
		}
		int exitValue = -1;
		try {
			exitValue = pb.start().waitFor();
			if (exitValue != 0) throw new RuntimeException("CmdUtilError: " + cmd); 
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return exitValue;
	}

	private static String findChromePath() {
		if (cmdChrome == null) {
			cmdChrome = "";
			if (LINUX) {
				File execCmd = new File("/usr/bin/chromium-browser");
				if (execCmd.isFile()) cmdChrome = execCmd.getAbsolutePath();
			} else {
				String chrome = "/Google/Chrome/Application/chrome.exe";
				File execCmd = new File("/Program Files (x86)" + chrome);
				if (execCmd.isFile()) cmdChrome = execCmd.getAbsolutePath();
			}
		}
		return cmdChrome;
	}

	public static List<String> cmdChromeToPdf(String url, String pdf) {
		// https://peter.sh/experiments/chromium-command-line-switches/
		List<String> cmd = new ArrayList<String>();
		cmd.add(findChromePath());
		cmd.add("--headless=old");
		cmd.add("--no-sandbox");
		cmd.add("--disable-gpu");
		cmd.add("--disable-dev-shm-usage");
		cmd.add("--ignore-certificate-errors");				
		cmd.add("--no-header");
		cmd.add("--no-pdf-header-footer");		
		if (LINUX) {
			cmd.add("--print-to-pdf=" + new File(pdf).getName());
		} else {
			cmd.add("--print-to-pdf=\"" + pdf + "\"");
		}
		cmd.add(url);
		return cmd;
	}
	
	private static String findOfficePath() {
		if (cmdOffice == null) {
			cmdOffice = "";
			if (LINUX) {
				File execCmd = new File("/opt/libreoffice/program/soffice");
				if (execCmd.isFile()) cmdOffice = execCmd.getAbsolutePath();
			} else {
				String libre = "/LibreOffice/program/soffice.exe";
				File execCmd = new File("/Program Files" + libre);
				if (execCmd.isFile()) cmdOffice = execCmd.getAbsolutePath();
				execCmd = new File("/Program Files (x86)" + libre);
				if (execCmd.isFile()) cmdOffice = execCmd.getAbsolutePath();
			}
		}
		return cmdOffice;
	}

	public static List<String> cmdConvertFile(String file, String type) {
		List<String> cmd = new ArrayList<String>();
		cmd.add(findOfficePath());
		cmd.add("--headless");
		cmd.add("--convert-to");
		cmd.add(type);
		cmd.add(file);
		return cmd;
	}

	private static String findGhostScriptPath() {
		if (cmdGhostScript == null) {
			cmdGhostScript = "";			
			if (LINUX) {
				File execCmd = new File("/usr/bin/gs");
				if (execCmd.isFile()) cmdGhostScript = execCmd.getAbsolutePath();
			} else {
				File execCmd = new File("/Program Files/gs/gs10.00.0/bin/gswin64c.exe");
				if (execCmd.isFile()) cmdGhostScript = execCmd.getAbsolutePath();
			}
		}
		return cmdGhostScript;
	}

	public static List<String> cmdGsPdfToTxt(String file) {
		List<String> cmd = new ArrayList<String>();
		cmd.add(findGhostScriptPath());
		cmd.add("-dSAFER");
		cmd.add("-dBATCH");
		cmd.add("-dNOPAUSE");
		cmd.add("-sDEVICE=txtwrite");
		cmd.add("-sOutputFile=" + file.replace(".pdf", ".txt"));
		cmd.add(file);
		return cmd;
	}

	public static List<String> cmdGsPdfCompact(String file, String pageList) {
		// https://ghostscript.com/docs/9.54.0/Use.htm
		List<String> cmd = new ArrayList<String>();
		cmd.add(findGhostScriptPath());
		cmd.add("-dSAFER");
		cmd.add("-dBATCH");
		cmd.add("-dNOPAUSE");
		cmd.add("-sDEVICE=pdfwrite");
		if (pageList != null && !pageList.equals("")) {
			cmd.add("-sPageList=" + pageList);
		}
		cmd.add("-sOutputFile=" + file.replace(".pdf", "-gs.pdf"));
		cmd.add(file);
		return cmd;
	}
		
	private static String findGenericPath(String name) {
		String path = System.getenv("PATH");
		String[] folders = path.split(File.pathSeparator);
		for (String folder : folders) {
			if (folder.toLowerCase().indexOf("windows")>-1) continue;
			String exec = LINUX ? name: name + ".exe";
			File execCmd = new File(folder, exec);
			if (execCmd.isFile()) return execCmd.getAbsolutePath();
		}
		return "";
	}
	
	public static String getConvertPath() {
		if (cmdConvert == null) {
			cmdConvert = findGenericPath("convert");
		}
		return cmdConvert;
	}

	public static String getTesseractPath() {
		if (cmdTesseract == null) {
			cmdTesseract = findGenericPath("tesseract");
		}
		return cmdTesseract;
	}
		
}
