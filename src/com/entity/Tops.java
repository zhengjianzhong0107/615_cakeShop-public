package com.entity;

public class Tops {
	
	
	/** 首页推荐类型 - 条幅 */
	public static final byte TYPE_SCROLL = 1;
	/** 首页推荐类型 - 大图 */
	public static final byte TYPE_LARGE = 2;
	/** 首页推荐类型 - 小图 */
	public static final byte TYPE_SMALL = 3;
	
	
    private Integer id;

    private Byte type;

    private Integer goodId;
    
	private Goods good;

    public Goods getGood() {
		return good;
	}

	public void setGood(Goods good) {
		this.good = good;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Integer getGoodId() {
        return goodId;
    }

    public void setGoodId(Integer goodId) {
        this.goodId = goodId;
    }
}