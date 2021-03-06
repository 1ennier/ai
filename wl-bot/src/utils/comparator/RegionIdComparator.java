package utils.comparator;

import java.util.Comparator;

import main.Region;

public class RegionIdComparator implements Comparator<Region> {

	@Override
	public int compare(Region region1, Region region2) {
		return Integer.valueOf(region1.getId()).compareTo(Integer.valueOf(region2.getId()));
	}

}
