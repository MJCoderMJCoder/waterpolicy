package com.lzf.waterpolicy.bean;

import java.io.Serializable;

/**
 * 案件详情
 */
public class Case implements Serializable {

	private String Cid;
	private String Cnumber;
	private String FK_CsId; // 案件状态
	private String Ctime; // 案发时间
	private String crtime; // 服务端处理时间
	private String Cname;
	private String Caddress;
	private String Ctype;
	private String Cdes; // 简要案情
	private String pic;
	private String video;
	private String picurl; // 服务端保存的地址
	private String videourl;

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

	public String getCrtime() {
		return crtime;
	}

	public void setCrtime(String crtime) {
		this.crtime = crtime;
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

	public String getCdes() {
		return Cdes;
	}

	public void setCdes(String cdes) {
		Cdes = cdes;
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

	public String getPicurl() {
		return picurl;
	}

	public void setPicurl(String picurl) {
		this.picurl = picurl;
	}

	public String getVideourl() {
		return videourl;
	}

	public void setVideourl(String videourl) {
		this.videourl = videourl;
	}

	public Case(String cid, String cnumber, String fK_CsId, String ctime, String crtime, String cname, String caddress,
			String ctype, String cdes, String pic, String video, String picurl, String videourl) {
		super();
		Cid = cid;
		Cnumber = cnumber;
		FK_CsId = fK_CsId;
		Ctime = ctime;
		this.crtime = crtime;
		Cname = cname;
		Caddress = caddress;
		Ctype = ctype;
		Cdes = cdes;
		this.pic = pic;
		this.video = video;
		this.picurl = picurl;
		this.videourl = videourl;
	}

	public Case() {
		super();
	}

}
