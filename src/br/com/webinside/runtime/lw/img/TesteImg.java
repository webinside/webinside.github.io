package br.com.webinside.runtime.lw.img;

import java.util.Date;
import java.util.List;

public class TesteImg {

	public static void main(String[] args) throws Exception {
		System.out.println("Iniciado");
		long iniDate = new Date().getTime();
		String dir = "/temp/teste-diario";
        List<String> cmd = ImgUtil.cmdPdfToImg("[0]", ImgEnum.PNG_GRAY, "x1000");
		ImgUtil.execute(cmd, dir, "pma", "pma", false);
		System.out.println((new Date().getTime() - iniDate));
		System.out.println("Finalizado");
	}

}
