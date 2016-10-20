package com.lzf.waterpolicy.bean;

import java.io.Serializable;

/**
 * 系统公告详情页
 */
public class Notice implements Serializable {

	private String Id;
	private String Ndes;
	private String Ndate;
	private String Nnumber;
	private String Ntitle;

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getNdes() {
		return Ndes;
	}

	public void setNdes(String ndes) {
		Ndes = ndes;
	}

	public String getNdate() {
		return Ndate;
	}

	public void setNdate(String ndate) {
		Ndate = ndate;
	}

	public String getNnumber() {
		return Nnumber;
	}

	public void setNnumber(String nnumber) {
		Nnumber = nnumber;
	}

	public String getNtitle() {
		return Ntitle;
	}

	public void setNtitle(String ntitle) {
		Ntitle = ntitle;
	}

	public Notice() {
		super();
	}

	public Notice(String id, String ndes, String ndate, String nnumber, String ntitle) {
		super();
		Id = id;
		Ndes = ndes;
		Ndate = ndate;
		Nnumber = nnumber;
		Ntitle = ntitle;
	}

}
