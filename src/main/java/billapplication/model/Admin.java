package billapplication.model;

public class Admin {
private int id;
private int name;
private int address;
private String gstIn;
private String sac;
public String getGstIn() {
	return gstIn;
}
public void setGstIn(String gstIn) {
	this.gstIn = gstIn;
}
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public int getName() {
	return name;
}
public void setName(int name) {
	this.name = name;
}
public int getAddress() {
	return address;
}
public void setAddress(int address) {
	this.address = address;
}
public String getSac() {
	return sac;
}
public void setSac(String sac) {
	this.sac = sac;
}
}
