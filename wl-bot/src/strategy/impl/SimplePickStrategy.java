package strategy.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.Region;
import state.GlobalState;
import strategy.IPickStrategy;
import utils.Weight.RegionPick;
import utils.comparator.RegionIdComparator;
import utils.comparator.desc.RegionWeightPickDescComparator;

public class SimplePickStrategy implements IPickStrategy {

	private ArrayList<Region> preferredStartingRegions = new ArrayList<Region>();

	public SimplePickStrategy() {

	}

	@Override
	public void execute() {
		ArrayList<Region> pickableRegions = GlobalState.getPickableStartingRegions();
		Map<Integer, List<Region>> regionsBySuperRegion = new HashMap<Integer, List<Region>>();

		// вес в зависимости от размера бонуса
		for (Region region : pickableRegions) {
			int superRegionId = region.getSuperRegion().getId();
			List<Region> list = regionsBySuperRegion.get(superRegionId);
			if (list == null) {
				list = new ArrayList<Region>();
				regionsBySuperRegion.put(superRegionId, list);
			}
			list.add(region);
			region.incWeightPick(region.getSuperRegion().getWeightPick(), RegionPick.SUPERREGION_BONUS);
		}

		// вес если несколько регионов доступны в одном бонусе
		for (Integer superRegionId : regionsBySuperRegion.keySet()) {
			List<Region> list = regionsBySuperRegion.get(superRegionId);
			if (list.size() > 1) {
				for (Region region : list) {
					region.incWeightPick(list.size() - 1, RegionPick.SAME_SUPERREGION);
				}
			}
		}

		// если регионы рядом
		Collections.sort(pickableRegions, new RegionIdComparator());
		for (Region region : pickableRegions) {
			for (Region otherRegion : pickableRegions) {
				if (region.getId() >= otherRegion.getId()) {
					continue;
				}

				if (region.getNeighbors().contains(otherRegion)) {
					region.incWeightPick(1, RegionPick.NEIGHBOR);
					otherRegion.incWeightPick(1, RegionPick.NEIGHBOR);
				}
			}
		}

		Collections.sort(pickableRegions, new RegionWeightPickDescComparator());
		while (preferredStartingRegions.size() < 6) {
			Region region = pickableRegions.remove(0);
			if (!preferredStartingRegions.contains(region)) {
				preferredStartingRegions.add(region);
			}
		}
	}

	@Override
	public ArrayList<Region> getPrefferedStartingRegions() {
		return preferredStartingRegions;
	}

}
