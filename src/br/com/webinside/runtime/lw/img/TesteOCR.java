package br.com.webinside.runtime.lw.img;

import java.util.Date;

import br.com.webinside.runtime.util.Function;

public class TesteOCR {

	public static void main(String[] args) throws Exception {
		long dtIni = new Date().getTime();
		System.out.println("Iniciado");
		String folder = "/temp/xxxx";
		Function.removeFiles(folder, "*.png");
		Function.removeFiles(folder, "*.txt");
		Function.removeFiles(folder, "*.log");
		ImgUtil.executeOCR(folder, "contrato_portal_assinado");
        System.out.println("Finalizado");
        long time = (new Date().getTime() - dtIni) / 1000;
        System.out.printf("%02d:%02d",(time/60),(time%60));
	}

}
