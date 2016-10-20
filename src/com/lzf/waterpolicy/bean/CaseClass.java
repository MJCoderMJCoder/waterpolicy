package com.lzf.waterpolicy.bean;

/**
 * 上报案件案件类型
 */
public class CaseClass {
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

	public CaseClass(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public CaseClass() {
		super();
	}

}
