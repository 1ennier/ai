package utils.comparator;

import java.util.Comparator;

import main.Region;

public class RegionInDangerComparator implements Comparator<Region> {

	@Override
	public int compare(Region region1, Region region2) {
		return Integer.valueOf(region2.getOpponentArmies() - region2.getArmies()).compareTo(Integer.valueOf(region1.getOpponentArmies() - region1.getArmies()));
	}

}
