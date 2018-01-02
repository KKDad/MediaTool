package org.westfield.configuration;

public class LookupHint
{
    public LookupHint()
    {
        // No parameter constructor needed by SnakeYaml
    }
    public LookupHint(String show, Integer id, String imdbId, String zap2itId)
    {
        this.show = show;
        this.id = id;
        this.imdbId = imdbId;
        this.zap2itId = zap2itId;
    }
    public String getShow() {  return show; }
    public Integer getId() {  return id; }
    public String getImdbId() {  return imdbId; }
    public String getZap2itId() {  return zap2itId; }

    public void setId(Integer id) {  this.id = id; }
    public void setShow(String show) {  this.show = show; }
    public void setImdbId(String imdbId) {  this.imdbId = imdbId; }
    public void setZap2itId(String zap2itId) {  this.zap2itId = zap2itId; }

    private Integer id;
    private String show;
    private String imdbId;
    private String zap2itId;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LookupHint{").append("id=").append(id);
        sb.append(", show='").append(show).append('\'');
        if (imdbId != null && !imdbId.isEmpty())
                sb.append(", imdbId='").append(imdbId).append('\'');
        if (zap2itId != null && !zap2itId.isEmpty())
            sb.append(", zap2itId='").append(zap2itId).append('\'');
        sb.append('}');

        return sb.toString();
    }
}