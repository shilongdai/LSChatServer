/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.core;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author sdai
 */
public class LSPayload implements Serializable, Comparable<LSPayload> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2007965376179027059L;
	public static final int LS_STATUS = 1;
	public static final int LS_MESSAGE = 2;

	private String source;
	private String target;
	private String data;
	private Map<String, String> attr;
	private Date timestamp;
	private int type;

	public LSPayload() {
		attr = new HashMap<>();
		timestamp = new Date();
		this.target = null;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public void setAttribute(String attrName, String attr) {
		this.attr.put(attrName, attr);
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getSource() {
		return source;
	}

	public String getTarget() {
		return target;
	}

	public String getData() {
		return data;
	}

	public Map<String, String> getAttr() {
		return attr;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setAttr(Map<String, String> attr) {
		this.attr = attr;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 53 * hash + Objects.hashCode(this.source);
		hash = 53 * hash + Objects.hashCode(this.target);
		hash = 53 * hash + Objects.hashCode(this.data);
		hash = 53 * hash + Objects.hashCode(this.attr);
		hash = 53 * hash + Objects.hashCode(this.timestamp);
		hash = 53 * hash + this.type;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final LSPayload other = (LSPayload) obj;
		if (this.type != other.type) {
			return false;
		}
		if (!Objects.equals(this.source, other.source)) {
			return false;
		}
		if (!Objects.equals(this.target, other.target)) {
			return false;
		}
		if (!Objects.equals(this.data, other.data)) {
			return false;
		}
		if (!Objects.equals(this.attr, other.attr)) {
			return false;
		}
		if (!Objects.equals(this.timestamp, other.timestamp)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(LSPayload o) {
		return this.timestamp.compareTo(o.getTimestamp());
	}

}
