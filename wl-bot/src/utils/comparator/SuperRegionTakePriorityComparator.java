package utils.comparator;

import java.util.Comparator;
import java.util.LinkedList;

import main.Region;
import main.SuperRegion;

public class SuperRegionTakePriorityComparator implements Comparator<SuperRegion> {

	@Override
	public int compare(SuperRegion region1, SuperRegion region2) {
		//1. выбираем суперрегион с наименьшим кол-вом неизвестных регионов
		LinkedList<Region> region1UnknownSubRegions = region1.getUnknownSubRegions();
		LinkedList<Region> region2UnknownSubRegions = region2.getUnknownSubRegions();
		if (region1UnknownSubRegions.size() == region2UnknownSubRegions.size()) {
			//2. выбираем суперрегион с наибольшим перевесом сил
			double res1 = getAdvantage(region1);
			double res2 = getAdvantage(region2);
			if (res1 == res2) {
				//3. выбираем суперрегион с наименьшим супербонусом
				if (region1.getArmiesReward() == region2.getArmiesReward()) {
					return Integer.valueOf(getNeutralsCount(region1)).compareTo(Integer.valueOf(getNeutralsCount(region2)));
				}
				return Integer.valueOf(region1.getArmiesReward()).compareTo(Integer.valueOf(region2.getArmiesReward()));
			}
			return Double.valueOf(res2).compareTo(Double.valueOf(res1));
		}
		return Integer.valueOf(region1UnknownSubRegions.size()).compareTo(Integer.valueOf(region2UnknownSubRegions.size()));
	}

	private double getAdvantage(SuperRegion superRegion) {
		int opponentArmies = superRegion.getNeutralArmies();
		if (opponentArmies == 0) {
			return Double.MAX_VALUE;
		}
		int myFreeArmies = superRegion.getMyFreeArmiesNear();
		return myFreeArmies * 1.0 / opponentArmies;
	}

	private int getNeutralsCount(SuperRegion superRegion) {
		int neutrals = 0;
		for (Region region : superRegion.getSubRegions()) {
			if (region.isNeutral()) {
				neutrals += region.getArmies();
			}
		}
		return neutrals;
	}

}
