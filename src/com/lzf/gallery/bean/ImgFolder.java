package com.lzf.gallery.bean;

public class ImgFolder {

	public ImgFolder(String dir, String firstImagePath, String name, int count) {
		super();
		this.dir = dir;
		this.firstImagePath = firstImagePath;
		this.name = name;
		this.count = count;
	}

	public ImgFolder() {
		super();
	}

	private String dir; // ͼƬ���ļ���·��

	private String firstImagePath; // ��һ��ͼƬ��·��

	private String name; // �ļ��е�����

	private int count; // ͼƬ������

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
		int lastIndexOf = this.dir.lastIndexOf("/");
		this.name = this.dir.substring(lastIndexOf + 1);
	}

	public String getFirstImagePath() {
		return firstImagePath;
	}

	public void setFirstImagePath(String firstImagePath) {
		this.firstImagePath = firstImagePath;
	}

	public String getName() {
		return name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
