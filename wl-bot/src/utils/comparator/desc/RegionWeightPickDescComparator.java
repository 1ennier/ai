package utils.comparator.desc;

import java.util.Comparator;

import main.Region;

public class RegionWeightPickDescComparator implements Comparator<Region> {

	@Override
	public int compare(Region region1, Region region2) {
		return Integer.valueOf(region2.getWeightPick()).compareTo(Integer.valueOf(region1.getWeightPick()));
	}

}
