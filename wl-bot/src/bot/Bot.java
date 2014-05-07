package bot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import main.Region;
import main.SuperRegion;
import move.AttackTransferMove;
import move.AttackTransferMove.MOVE_TYPE;
import move.PlaceArmiesMove;
import state.GlobalState;
import strategy.Context;
import strategy.impl.SimplePickStrategy;
import utils.ArmyPlaceUtils;
import utils.AttackUtils;
import utils.RegionAttackInfo;
import utils.RegionUtils;
import utils.comparator.RegionArmiesComparator;
import weight.RegionPickWeight;

public class Bot implements IBot {

	private Context context;

	public static void main(String[] args) {

		if (args != null && args.length > 0) {
			for (String arg : args) {
				if (arg.equals("debug")) {
					GlobalState.debug = true;
				}
			}
		}

		//		try {
		//			RegionPickWeight.initByFile();
		//		} catch (IOException e) {
		//			e.printStackTrace();
		//			System.exit(1);
		//		}

		RegionPickWeight.initManually();

		Bot bot = new Bot();
		Context ctx = new Context();
		ctx.setStrategyPick(new SimplePickStrategy());
		bot.setContext(ctx);
		BotParser parser = new BotParser(bot);
		parser.run();
	}

	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public ArrayList<Region> getPreferredStartingRegions(Long timeOut) {
		context.getStrategyPick().execute();
		return context.getStrategyPick().getPrefferedStartingRegions();
	}

	@Override
	public ArrayList<PlaceArmiesMove> getPlaceArmiesMoves(Long timeOut) {
		generateMoves();
		return GlobalState.getCurrentState().getPlaceArmiesMoves();
	}

	@Override
	public ArrayList<AttackTransferMove> getAttackTransferMoves(Long timeOut) {
		return GlobalState.getCurrentState().getAttackTransferMoves();
	}

	private void putRegionWeights() {
		LinkedList<Region> myRegions = RegionUtils.getMyRegions();

	}

	private void generateMoves() {

		putRegionWeights();

		// Если есть противник, то все войска кладем на границу (чуток оставляем если есть тема захватить бонус)
		if (!RegionUtils.getMyRegionsNearOpponent().isEmpty()) {
			ArmyPlaceUtils.placeArmiesIfHasOpponent();
		} else if (!RegionUtils.getMyBorderRegions().isEmpty()) {//Если нет противника, то захватываем бонусы
			ArmyPlaceUtils.placeArmiesIfHasNoOpponent();
		} else {//сюда не должно попадать, т.к. это означает, что нет ни противника ни нейтралов рядом
			RegionUtils.randomPlaceArmies();
		}

		LinkedList<Region> myBorderRegions = RegionUtils.getMyBorderRegions();
		LinkedList<Region> myRegionsNearOpponent = RegionUtils.getMyRegionsNearOpponent();

		attackOpponent(myRegionsNearOpponent);

		attackNeutrals(myBorderRegions);

		attackSuperRegions();

		combineFreeArmies();

		transferFromInnerRegions();

		tryToWin();
	}

	private void tryToWin() {
		LinkedList<Region> candidates = new LinkedList<Region>();
		LinkedList<Region> myRegions = RegionUtils.getMyRegionsNearOpponent();
		Collections.sort(myRegions, new RegionArmiesComparator());
		Iterator<Region> it = myRegions.iterator();
		while (it.hasNext()) {
			Region my = it.next();
			LinkedList<Region> opponents = my.getMyRegionOpponentNeighbors();
			for (Region opponent : opponents) {
				if (!candidates.contains(opponent)) {
					candidates.add(opponent);
				}
			}
		}

		for (Region opponent : candidates) {
			LinkedList<Region> attackers = RegionUtils.getMyNeighborsOfRegion(opponent);
			int myArmies = RegionUtils.getMyFreeArmies(attackers);
			if (AttackUtils.isEnoughToAttack(myArmies, opponent)) {
				for (Region attacker : attackers) {
					GlobalState.getCurrentState().addAttackTransferMove(RegionUtils.createMove(attacker.getFreeArmies(), attacker, opponent, MOVE_TYPE.LAST));
				}
			}
		}

	}

	private void attackNeutrals(LinkedList<Region> myBorderRegions) {
		attack(myBorderRegions, true);
	}

	private void attackOpponent(LinkedList<Region> myRegionsNearOpponent) {
		if (GlobalState.getCurrentState().getRoundNumber() > 1) {
			attack(myRegionsNearOpponent, false);
		}
	}

	private void attack(LinkedList<Region> regions, boolean isNeutral) {
		ArrayList<AttackTransferMove> moves = new ArrayList<AttackTransferMove>();
		for (Region region : regions) {
			LinkedList<Region> opponents = isNeutral ? region.getMyRegionNeutralNeighbors() : region.getMyRegionOpponentNeighbors();

			if (isNeutral) {
				opponents = sameSuperRegionFirst(region.getSuperRegion(), opponents);
			}

			Iterator<Region> it = opponents.iterator();
			while (it.hasNext()) {
				Region opponent = it.next();
				if (AttackUtils.needAttack(region, opponent, isNeutral)) {
					if (GlobalState.getCurrentState().getAttackedRegions().contains(opponent) && isNeutral) {
						continue;
					}
					int armiesToAttack = region.getFreeArmies();
					if ((!region.hasSingleOpponent() || opponent.hasOnlyMyNeighbors()) && it.hasNext()) {//если противник только один либо у противника нет других соседей кроме меня, и это не последний противник, то атакуем по минимуму
						int opponentArmiesCount = opponent.getArmies();
						armiesToAttack = isNeutral ? AttackUtils.getNeededArmiesToAttack(opponentArmiesCount) : region.getFreeArmies();
					}

					//иначе атакуем всем что есть
					moves.add(RegionUtils.createMove(armiesToAttack, region, opponent, MOVE_TYPE.LAST));
				}
			}
		}

		for (AttackTransferMove move : moves) {
			GlobalState.getCurrentState().addAttackTransferMove(move);
		}
	}

	private LinkedList<Region> sameSuperRegionFirst(SuperRegion superRegion, LinkedList<Region> regions) {
		LinkedList<Region> result = new LinkedList<Region>();
		for (Region region : regions) {
			if (region.getSuperRegion().getId() == superRegion.getId()) {
				result.addFirst(region);
			} else {
				result.addLast(region);
			}
		}
		return result;
	}

	private void combineFreeArmies() {
		ArrayList<AttackTransferMove> moves = new ArrayList<AttackTransferMove>();
		LinkedList<Region> myRegions = RegionUtils.getMyRegions();
		LinkedList<Region> myRegionsLeaved = new LinkedList<Region>();
		for (Region myRegion : myRegions) {
			if (myRegion.hasOpponentNeighbor() || myRegion.getFreeArmies() == 0) {
				continue;
			}

			LinkedList<Region> myNeighbors = myRegion.getMyRegionsNear();
			for (Region neighbor : myNeighbors) {
				if (neighbor.hasOpponentNeighbor()) {
					moves.add(RegionUtils.createMove(myRegion.getFreeArmies(), myRegion, neighbor, MOVE_TYPE.NO_MATTER));
					myRegionsLeaved.add(myRegion);
					break;
				}
				if (neighbor.hasNeutralNeighbor()) {
					moves.add(RegionUtils.createMove(myRegion.getFreeArmies(), myRegion, neighbor, MOVE_TYPE.NO_MATTER));
					myRegionsLeaved.add(myRegion);
					break;
				}
			}

			if (myRegionsLeaved.contains(myRegion)) {
				break;
			}

			Region nearestBorder = myRegion.getNearestBorder();
			if (nearestBorder == null) {
				break;
			}

			Region to = myRegion.getFirstInPath(nearestBorder);
			if (to != null) {
				moves.add(RegionUtils.createMove(myRegion.getFreeArmies(), myRegion, to, MOVE_TYPE.NO_MATTER));
			}

		}

		for (AttackTransferMove move : moves) {
			GlobalState.getCurrentState().addAttackTransferMove(move);
		}
	}

	private void transferFromInnerRegions() {
		ArrayList<AttackTransferMove> moves = new ArrayList<AttackTransferMove>();
		LinkedList<Region> innerRegions = RegionUtils.getMyInnerRegions();
		for (Region innerRegion : innerRegions) {
			if (innerRegion.getFreeArmies() == 0) {
				continue;
			}
			boolean transferred = false;
			LinkedList<Region> neighbors = innerRegion.getNeighbors();
			for (Region neighbor : neighbors) {
				if (neighbor.hasOpponentNeighbor()) {
					moves.add(RegionUtils.createMove(innerRegion.getFreeArmies(), innerRegion, neighbor, MOVE_TYPE.NO_MATTER));
					transferred = true;
				} else if (neighbor.hasNeutralNeighbor()) {
					moves.add(RegionUtils.createMove(innerRegion.getFreeArmies(), innerRegion, neighbor, MOVE_TYPE.NO_MATTER));
					transferred = true;
				}
				if (transferred) {
					break;
				}
			}
			if (!transferred) {
				Region to = innerRegion.getMyRegionToTransferFromInner();
				if (to != null) {
					moves.add(RegionUtils.createMove(innerRegion.getFreeArmies(), innerRegion, to, MOVE_TYPE.NO_MATTER));
				}
			}
		}

		for (AttackTransferMove move : moves) {
			GlobalState.getCurrentState().addAttackTransferMove(move);
		}
	}

	private void attackSuperRegions() {
		ArrayList<AttackTransferMove> moves = new ArrayList<AttackTransferMove>();
		SuperRegion superRegion = RegionUtils.getSuperRegionToTake();
		if (superRegion != null) {
			LinkedList<Region> regionsToTake = RegionUtils.getRegionsToTakeBySuperRegion(superRegion);
			for (Region regionToTake : regionsToTake) {
				if (GlobalState.getCurrentState().getAttackedRegions().contains(regionToTake)) {
					continue;
				}
				RegionAttackInfo ai = RegionUtils.getRegionAttackInfo(regionToTake);
				if (ai != null && !ai.getRegionFrom().hasOpponentNeighbor()) {
					moves.add(RegionUtils.createMove(ai.getArmies(), ai.getRegionFrom(), regionToTake, MOVE_TYPE.NO_MATTER));
				}
			}
		}

		for (AttackTransferMove move : moves) {
			GlobalState.getCurrentState().addAttackTransferMove(move);
		}
	}

}
