package com.wisega.tool;

public class ImgBean {

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return this.mode.equals(((ImgBean)obj).mode);
	}
	
	public String mName = "unknow";
	
	public String mode = "unknow";

	public  String version;

	public  String imageA;

	public String imageB;

}
