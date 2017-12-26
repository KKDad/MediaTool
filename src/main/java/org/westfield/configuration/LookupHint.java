package org.westfield.configuration;

public class LookupHint
{
    public LookupHint()
    {
        // No parameter constructor needed by SnakeYaml
    }
    public LookupHint(String show, Integer id)
    {
        this.show = show;
        this.id = id;

    }
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