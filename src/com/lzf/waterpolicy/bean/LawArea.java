package com.lzf.waterpolicy.bean;

/**
 * 上报案件执法区域
 */
public class LawArea {
	private String id;
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LawArea() {
		super();
	}

	public LawArea(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

}
