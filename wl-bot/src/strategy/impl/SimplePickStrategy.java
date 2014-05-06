package strategy.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import main.Region;
import main.SuperRegion;
import state.GlobalState;
import strategy.IPickStrategy;
import temp.RegionPickWeight;
import temp.RegionPickWeight.PROP;
import utils.RegionUtils;
import utils.comparator.RegionIdComparator;
import utils.comparator.SuperRegionBonusSizeComparator;
import utils.comparator.desc.RegionWeightPickDescComparator;

public class SimplePickStrategy implements IPickStrategy {

	private ArrayList<Region> preferredStartingRegions = new ArrayList<Region>();

	@Override
	public void execute() {
		ArrayList<SuperRegion> superRegions = new ArrayList<SuperRegion>();
		for (Region region : GlobalState.getPickableStartingRegions()) {
			SuperRegion sr = region.getSuperRegion();
			if (!superRegions.contains(sr)) {
				superRegions.add(sr);
			}
		}

		// в зависимости от размера бонуса
		processBonusSize(superRegions);

		// если несколько регионов доступны в одном бонусе
		processSuperRegion(superRegions);

		// если регионы рядом
		processNeighbors();

		// соседние бонусы
		processNearBonuses();

		Collections.sort(GlobalState.getPickableStartingRegions(), new RegionWeightPickDescComparator());

		if (GlobalState.debug) {
			for (Region region : GlobalState.getPickableStartingRegions()) {
				System.out.println(region);
			}
		}

		while (preferredStartingRegions.size() < 6) {
			Region region = GlobalState.getPickableStartingRegions().remove(0);
			if (!preferredStartingRegions.contains(region)) {
				preferredStartingRegions.add(region);
			}
		}
	}

	private void processBonusSize(ArrayList<SuperRegion> superRegions) {
		ArrayList<Region> pickableRegions = GlobalState.getPickableStartingRegions();
		int minBonus = Integer.MAX_VALUE;
		for (SuperRegion superRegion : superRegions) {
			int bonus = superRegion.getArmiesReward();
			if (bonus < minBonus) {
				minBonus = bonus;
			}
		}

		for (Region region : pickableRegions) {
			SuperRegion sr = region.getSuperRegion();
			double coeff = 1 - (3.0 * (sr.getArmiesReward() - minBonus) / 10);
			region.setPickCoeff(coeff);
			region.incWeightPick(RegionPickWeight.getProp(PROP.superRegionBonus));
		}
	}

	private void processSuperRegion(ArrayList<SuperRegion> superRegions) {
		ArrayList<Region> pickableRegions = GlobalState.getPickableStartingRegions();
		Map<SuperRegion, List<Region>> thisSuperRegionAndNear = new HashMap<SuperRegion, List<Region>>();
		for (SuperRegion superRegion : superRegions) {
			ArrayList<Region> list = new ArrayList<Region>();
			thisSuperRegionAndNear.put(superRegion, list);
			for (Region region : pickableRegions) {
				if (region.getSuperRegion().getId() == superRegion.getId()) {
					list.add(region);
					continue;
				}
				LinkedList<Region> neighbors = region.getNeighbors();
				for (Region neighbor : neighbors) {
					if (neighbor.getSuperRegion().getId() == superRegion.getId()) {
						list.add(region);
						break;
					}
				}
			}
		}

		int maxSize = 0;
		List<Region> regions = null;
		for (List<Region> list : thisSuperRegionAndNear.values()) {
			int size = list.size();
			if (size > maxSize) {
				maxSize = size;
				regions = list;
			}
		}

		int minBonus = Integer.MAX_VALUE;
		for (SuperRegion superRegion : superRegions) {
			int bonus = superRegion.getArmiesReward();
			if (bonus < minBonus) {
				minBonus = bonus;
			}
		}

		if (regions != null) {
			for (Region region : regions) {
				region.incWeightPick(RegionPickWeight.getProp(PROP.sameSuperRegionAndNear));
			}
		}
	}

	private void processNeighbors() {
		Collections.sort(GlobalState.getPickableStartingRegions(), new RegionIdComparator());
		for (Region region : GlobalState.getPickableStartingRegions()) {
			for (Region otherRegion : GlobalState.getPickableStartingRegions()) {
				if (region.getId() >= otherRegion.getId()) {
					continue;
				}

				if (region.getNeighbors().contains(otherRegion)) {
					region.incWeightPick(RegionPickWeight.getProp(PROP.neighbor));
					otherRegion.incWeightPick(RegionPickWeight.getProp(PROP.neighbor));
				}
			}
		}
	}

	private void processNearBonuses() {
		Map<Region, SuperRegion> nears = new HashMap<Region, SuperRegion>();
		ArrayList<Region> pickableRegions = GlobalState.getPickableStartingRegions();
		for (Region region : pickableRegions) {
			SuperRegion superRegion = region.getSuperRegion();
			LinkedList<SuperRegion> neighborSuperRegions = RegionUtils.getNeighborSuperRegions(superRegion);
			Collections.sort(neighborSuperRegions, new SuperRegionBonusSizeComparator());
			SuperRegion minimal = neighborSuperRegions.getFirst();
			nears.put(region, minimal);
		}

		int minBonus = Integer.MAX_VALUE;
		for (Region region : nears.keySet()) {
			SuperRegion near = nears.get(region);
			int bonusNear = near.getArmiesReward();
			if (bonusNear < minBonus) {
				minBonus = bonusNear;
			}
		}

		for (Region region : nears.keySet()) {
			SuperRegion near = nears.get(region);
			if (near.getArmiesReward() == minBonus) {
				region.incWeightPick(RegionPickWeight.getProp(PROP.nearBonuses));
			}
		}
	}

	@Override
	public ArrayList<Region> getPrefferedStartingRegions() {
		return preferredStartingRegions;
	}

}
