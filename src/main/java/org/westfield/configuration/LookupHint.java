package org.westfield.configuration;

public class LookupHint
{
    public String getShow() {  return show; }
    public Integer getId() {  return id; }

    public void setId(Integer id) {  this.id = id; }
    public void setShow(String show) {  this.show = show; }

    Integer id;
    String show;

    @Override
    public String toString() {
        return String.format("LookupHint{id='%s', show='%s'}", id, show);
    }
}