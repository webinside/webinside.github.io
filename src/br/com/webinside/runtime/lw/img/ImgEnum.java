package br.com.webinside.runtime.lw.img;

public enum ImgEnum {

	PNG_BW, PNG_GRAY, PNG_COLOR, JPG_BW, JPG_GRAY, JPG_COLOR; 
			
	public boolean isColor() {
		return (name().indexOf("COLOR") > -1);
	}

	public int getDepth() {
		return (name().indexOf("BW") > -1 ? 1 : 8);
	}
	
	public String getExt() {
		return name().toLowerCase().split("_")[0];
	}
	
}
