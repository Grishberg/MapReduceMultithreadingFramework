package com.grishberg.parser;

public class RdrRaw {
	public String dstHost;
	public String dstParam;
    public String serverIp;
    public String userId;
    public String userIp;
    public String info;

    public static RdrRaw getInstance(String string){
        if(string == null) return null;
    	String[] c = string.split(",");
    	if(c == null || c.length < 12){
    		return null;
    	}
    	return new RdrRaw(c);
    }
    
    private RdrRaw(String[] c) {
    	this.serverIp = checkNull(c[8]);
        this.userId = checkNull(c[3]);
        this.userIp = checkNull(c[12]);
        this.dstHost = checkNull(c[10]);
        this.dstParam = checkNull(c[11]);
    }
    
    @Override
    public String toString() {
        return "serverIp = [" + serverIp + "], userIp = [" + userIp + "], info = [" + info + "]";
    }
    private String checkNull(String src){
    	return src == null ? "" : src;
    }
}

