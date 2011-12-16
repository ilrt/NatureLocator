package org.ilrt.nl.oauth;

import java.io.Serializable;

class OAuthUser implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    private String name;
    private String id;

    public String getName() { return name; }
    public String getNickname() { return name; }
    public String getUserId() { return id; }

    public void setName(String name) { this.name = name; }
    public void setId(String id) { this.id = id; }

    public String toString() {
        return String.format("name:%s, id:%s", name, id);
    }
}