package org.activityinfo.geoadmin.model;

public class AdminEntity {
    private int id;
    private Integer parentId;
    private String name;
    private String code;
    private Bounds bounds;
    private boolean deleted;
    private String geometryText;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
    
    /**
     * 
     * @return the entity's geometry as Well Known Text (WKT)
     */
    public String getGeometryText() {
		return geometryText;
	}

	public void setGeometryText(String geometryText) {
		this.geometryText = geometryText;
	}

	public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }


    @Override
    public String toString() {
        if (code == null) {
            return name;
        } else {
            return name + " (" + code + ")";
        }
    }
}
