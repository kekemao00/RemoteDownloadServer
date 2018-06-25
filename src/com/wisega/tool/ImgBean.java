package com.wisega.tool;

public class ImgBean {

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getImageA() {
		return imageA;
	}

	public void setImageA(String imageA) {
		this.imageA = imageA;
	}

	public String getImageB() {
		return imageB;
	}

	public void setImageB(String imageB) {
		this.imageB = imageB;
	}

	@Override
	public String toString() {
		return "ImgBean [mode=" + mode + ", version=" + version + ", imageA=" + imageA + ", imageB=" + imageB + "]";
	}

	String mode;

	String version;

	String imageA;

	String imageB;

}
