package com.entando.spid.service.dto;

public class MapperAttribute {

  String name;
  String attributeName;
  String userAttributeName;

  public MapperAttribute(String name, String attributeName, String userAttributeName) {
    this.name = name;
    this.attributeName = attributeName;
    this.userAttributeName = attributeName;
  }

  @Override
  public String toString() {
    return "[name: " + name+ ", attribute name: " + attributeName + ", user attribute name: " + userAttributeName + "]";
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAttributeName() {
    return attributeName;
  }

  public void setAttributeName(String attributeName) {
    this.attributeName = attributeName;
  }

  public String getUserAttributeName() {
    return userAttributeName;
  }

  public void setUserAttributeName(String userAttributeName) {
    this.userAttributeName = userAttributeName;
  }
}
