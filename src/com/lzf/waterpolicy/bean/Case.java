package com.lzf.waterpolicy.bean;

import java.io.Serializable;

/**
 * 案件详情
 */
public class Case implements Serializable {

	private String Cid;
	private String Cnumber;
	private String FK_CsId; // 案件状态
	private String Ctime;
	private String Cname;
	private String Caddress;
	private String Ctype;
	private String pic;
	private String video;

	public String getCid() {
		return Cid;
	}

	public void setCid(String cid) {
		Cid = cid;
	}

	public String getCnumber() {
		return Cnumber;
	}

	public void setCnumber(String cnumber) {
		Cnumber = cnumber;
	}

	public String getFK_CsId() {
		return FK_CsId;
	}

	public void setFK_CsId(String fK_CsId) {
		FK_CsId = fK_CsId;
	}

	public String getCtime() {
		return Ctime;
	}

	public void setCtime(String ctime) {
		Ctime = ctime;
	}

	public String getCname() {
		return Cname;
	}

	public void setCname(String cname) {
		Cname = cname;
	}

	public String getCaddress() {
		return Caddress;
	}

	public void setCaddress(String caddress) {
		Caddress = caddress;
	}

	public String getCtype() {
		return Ctype;
	}

	public void setCtype(String ctype) {
		Ctype = ctype;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getVideo() {
		return video;
	}

	public void setVideo(String video) {
		this.video = video;
	}

	public Case(String cid, String cnumber, String fK_CsId, String ctime, String cname, String caddress, String ctype,
			String pic, String video) {
		super();
		Cid = cid;
		Cnumber = cnumber;
		FK_CsId = fK_CsId;
		Ctime = ctime;
		Cname = cname;
		Caddress = caddress;
		Ctype = ctype;
		this.pic = pic;
		this.video = video;
	}

	public Case() {
		super();
	}

}
