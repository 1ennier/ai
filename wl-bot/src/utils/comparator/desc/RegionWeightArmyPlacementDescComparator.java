package utils.comparator.desc;

import java.util.Comparator;

import main.Region;

public class RegionWeightArmyPlacementDescComparator implements Comparator<Region> {

	@Override
	public int compare(Region region1, Region region2) {
		return Double.valueOf(region2.getWeightArmyPlace()).compareTo(Double.valueOf(region1.getWeightArmyPlace()));
	}

}
