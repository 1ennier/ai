package utils.comparator.desc;

import java.util.Comparator;

import main.SuperRegion;

public class SuperRegionBonusSizeDescComparator implements Comparator<SuperRegion> {

	@Override
	public int compare(SuperRegion region1, SuperRegion region2) {
		return Integer.valueOf(region2.getArmiesReward()).compareTo(Integer.valueOf(region1.getArmiesReward()));
	}

}
