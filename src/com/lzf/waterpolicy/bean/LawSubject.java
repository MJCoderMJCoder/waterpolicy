package com.lzf.waterpolicy.bean;

/**
 * 法律法规主题类型
 */
public class LawSubject {
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

	public LawSubject(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public LawSubject() {
		super();
	}

}
