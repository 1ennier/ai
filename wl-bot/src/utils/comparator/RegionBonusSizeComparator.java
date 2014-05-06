package utils.comparator;

import java.util.Comparator;

import main.Region;

public class RegionBonusSizeComparator implements Comparator<Region> {

	@Override
	public int compare(Region region1, Region region2) {
		return Integer.valueOf(region1.getSuperRegion().getArmiesReward()).compareTo(Integer.valueOf(region2.getSuperRegion().getArmiesReward()));
	}

}
