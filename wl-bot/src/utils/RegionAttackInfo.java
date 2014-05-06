package utils;

import main.Region;

public class RegionAttackInfo {

	private Region regionFrom;
	private int armies;

	public RegionAttackInfo(Region regionFrom, int armies) {
		super();
		this.regionFrom = regionFrom;
		this.armies = armies;
	}

	public Region getRegionFrom() {
		return regionFrom;
	}

	public int getArmies() {
		return armies;
	}

}
