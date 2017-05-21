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

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * a datagram from the server to the client. A {@link LSPayload} object can
 * either encapsulate a {@link LSResponse} object in its data section, or it can
 * contain a packet to another client processed by a handler. In transmission,
 * the {@link LSPayload} would be serialized to JSON format and transmitted to a
 * client via websocket. Currently, there are two type of payload, status
 * message and chat message. This class is not designed for thread safety.
 * 
 * @author sdai
 */
@Entity
@Table(name = "User_Message")
public class LSPayload implements Serializable, Comparable<LSPayload> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2007965376179027059L;
	/**
	 * message type for a status message
	 */
	public static final int LS_STATUS = 1;
	/**
	 * message type for a chat message
	 */
	public static final int LS_MESSAGE = 2;

	private String source;
	private String target;
	private String data;
	private Map<String, String> attr;
	private Date timestamp;
	private int type;
	private long id;

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

	@Basic
	@Column(name = "Source")
	public String getSource() {
		return source;
	}

	@Basic
	@Column(name = "Target")
	public String getTarget() {
		return target;
	}

	@Basic
	@Lob
	@Column(name = "Data")
	public String getData() {
		return data;
	}

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "User_Message_Attr", joinColumns = {
			@JoinColumn(referencedColumnName = "Id", name = "PayloadId") })
	@Column(name = "Value")
	@MapKeyColumn(name = "Key")
	public Map<String, String> getAttr() {
		return attr;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MessageTime")
	public Date getTimestamp() {
		return timestamp;
	}

	public void setAttr(Map<String, String> attr) {
		this.attr = attr;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Basic
	@Column(name = "Type")
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Id
	@JsonIgnore
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attr == null) ? 0 : attr.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		result = prime * result + type;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LSPayload other = (LSPayload) obj;
		if (attr == null) {
			if (other.attr != null)
				return false;
		} else if (!attr.equals(other.attr))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (id != other.id)
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public int compareTo(LSPayload o) {
		return this.timestamp.compareTo(o.getTimestamp());
	}

}
