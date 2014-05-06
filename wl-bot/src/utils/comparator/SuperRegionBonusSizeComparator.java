package utils.comparator;

import java.util.Comparator;

import main.SuperRegion;

public class SuperRegionBonusSizeComparator implements Comparator<SuperRegion> {

	@Override
	public int compare(SuperRegion region1, SuperRegion region2) {
		return Integer.valueOf(region1.getArmiesReward()).compareTo(Integer.valueOf(region2.getArmiesReward()));
	}

}
