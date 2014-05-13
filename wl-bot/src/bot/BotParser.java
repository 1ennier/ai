package bot;

import graph.EdgeWeightedDigraph;

import java.util.ArrayList;
import java.util.Scanner;

import main.Map;
import main.Region;
import main.SuperRegion;
import move.AttackTransferMove;
import move.AttackTransferMove.MOVE_TYPE;
import move.Move;
import move.PlaceArmiesMove;
import state.GlobalState;

public class BotParser {

	private final Scanner scan;
	private final IBot bot;

	private String line;

	public BotParser(IBot bot) {
		this.scan = new Scanner(System.in);
		this.bot = bot;
	}

	public void run() {
		while (scan.hasNextLine()) {
			line = scan.nextLine().trim();
			if (line.length() == 0) {
				continue;
			}
			String[] params = line.split(" ");
			String cmd = params[0];
			switch (cmd) {
				case "settings":
					settings(params);
					break;
				case "setup_map":
					setupMap(params);
					break;
				case "pick_starting_regions":
					pickStartingRegions(params);
					break;
				case "go":
					go(params);
					break;
				case "update_map":
					updateMap(params);
					break;
				case "opponent_moves":
					opponentMoves(params);
					break;
				default:
					parseError();
					break;
			}
		}
	}

	private void settings(String[] params) {
		if (params.length != 3) {
			parseError();
		}
		String key = params[1];
		String value = params[2];
		switch (key) {
			case "your_bot":
				GlobalState.setMyName(value);
				break;
			case "opponent_bot":
				GlobalState.setOpponentName(value);
				break;
			case "starting_armies":
				int armies = Integer.parseInt(value);
				GlobalState.getCurrentState().setStartingArmies(armies);
				break;
			default:
				break;
		}
	}

	private void setupMap(String[] params) {
		if (params.length < 3) {
			parseError();
		}
		String subCmd = params[1];
		switch (subCmd) {
			case "super_regions":
				setSuperRegions(params);
				break;
			case "regions":
				setRegions(params);
				break;
			case "neighbors":
				setNeighbors(params);
				break;
			default:
				break;
		}
	}

	private void setSuperRegions(String[] params) {
		for (int i = 2; i < params.length; i++) {
			try {
				int superRegionId = Integer.parseInt(params[i]);
				i++;
				int reward = Integer.parseInt(params[i]);
				SuperRegion superRegion = new SuperRegion(superRegionId, reward);
				GlobalState.getFullMap().add(superRegion);
			} catch (Exception e) {
				System.err.println("Unable to parse SuperRegions: " + e.getMessage());
			}
		}
	}

	private void setRegions(String[] params) {
		for (int i = 2; i < params.length; i++) {
			try {
				int regionId = Integer.parseInt(params[i]);
				i++;
				int superRegionId = Integer.parseInt(params[i]);
				SuperRegion superRegion = GlobalState.getFullMap().getSuperRegion(superRegionId);
				Region region = new Region(regionId, superRegion);
				GlobalState.getFullMap().add(region);
			} catch (Exception e) {
				System.err.println("Unable to parse Regions: " + e.getMessage());
			}
		}
	}

	private void setNeighbors(String[] params) {
		GlobalState.setGraph(new EdgeWeightedDigraph(GlobalState.getTotalRegionsCount()));
		for (int i = 2; i < params.length; i++) {
			try {
				Region region = GlobalState.getFullMap().getRegion(Integer.parseInt(params[i]));
				i++;
				String[] neighborIds = params[i].split(",");
				for (int j = 0; j < neighborIds.length; j++) {
					Region neighbor = GlobalState.getFullMap().getRegion(Integer.parseInt(neighborIds[j]));
					region.addNeighbor(neighbor);
				}
			} catch (Exception e) {
				System.err.println("Unable to parse Neighbors " + e.getMessage());
			}
		}

		for (Region region : GlobalState.getFullMap().getRegions()) {
			region.initShortestPaths();
		}

	}

	private void pickStartingRegions(String[] params) {
		long timeOut = Long.valueOf(params[1]);
		setPickableStartingRegions(params);
		ArrayList<Region> preferredStartingRegions = bot.getPreferredStartingRegions(timeOut);
		for (Region region : preferredStartingRegions) {
			GlobalState.addPreferredStartingRegionId(region.getId());
		}
		String output = "";
		for (Region region : preferredStartingRegions) {
			output = output.concat(region.getId() + " ");
		}
		System.out.println(output);
	}

	private void setPickableStartingRegions(String[] params) {
		for (int i = 2; i < params.length; i++) {
			try {
				int regionId = Integer.parseInt(params[i]);
				Region pickableRegion = GlobalState.getFullMap().getRegion(regionId);
				GlobalState.addPickableStartingRegion(pickableRegion);
			} catch (Exception e) {
				System.err.println("Unable to parse pickable regions " + e.getMessage());
			}
		}
	}

	private void go(String[] params) {
		if (params.length != 3) {
			parseError();
		}
		String subCmd = params[1];
		long timeOut = Long.valueOf(params[2]);

		String output = "";
		if (subCmd.equals("place_armies")) {
			output = placeArmies(timeOut);
		} else if (subCmd.equals("attack/transfer")) {
			output = attackOrTransfer(timeOut);
		}

		System.out.println(output.length() > 0 ? output : "No moves");
	}

	private String placeArmies(long timeOut) {
		String output = "";
		ArrayList<PlaceArmiesMove> placeArmiesMoves = bot.getPlaceArmiesMoves(timeOut);
		for (PlaceArmiesMove move : placeArmiesMoves) {
			output = output.concat(move.getString() + ",");
		}
		return output;
	}

	private String attackOrTransfer(long timeOut) {
		String output = "";
		ArrayList<AttackTransferMove> attackTransferMoves = bot.getAttackTransferMoves(timeOut);
		for (AttackTransferMove move : attackTransferMoves) {
			output = output.concat(move.getString() + ",");
		}
		return output;
	}

	private void updateMap(String[] params) {
		Map visibleMap = GlobalState.getFullMap().getMapCopy();
		for (int i = 1; i < params.length; i++) {
			try {
				Region region = visibleMap.getRegion(Integer.parseInt(params[i]));
				String playerName = params[i + 1];
				int armies = Integer.parseInt(params[i + 2]);

				region.setPlayerName(playerName);
				region.setArmies(armies);

				i += 2;
			} catch (Exception e) {
				System.err.println("Unable to parse Map Update " + e.getMessage());
			}
		}
		ArrayList<Region> unknownRegions = new ArrayList<Region>();

		//remove regions which are unknown.
		for (Region region : visibleMap.regions) {
			if (region.getPlayerName().equals(GlobalState.getUnknownName())) {
				unknownRegions.add(region);
			}
		}
		for (Region unknownRegion : unknownRegions) {
			visibleMap.getRegions().remove(unknownRegion);
		}

		GlobalState.getCurrentState().setVisibleMap(visibleMap);

		if (GlobalState.getCurrentState().getRoundNumber() == 1) {
			checkOpponentPicks();
		}
	}

	private void checkOpponentPicks() {
		ArrayList<Integer> preferredIds = GlobalState.getPreferredStartingRegionIds();
		int myCount = 0;
		for (int id : preferredIds) {
			if (myCount == 3) {
				break;
			}
			Region region = GlobalState.getCurrentState().getVisibleMap().getRegion(id);
			if (region != null && region.isMy()) {
				myCount++;
				continue;
			}
			GlobalState.addOpponentPickId(id);
		}
	}

	private void opponentMoves(String[] params) {
		GlobalState.getCurrentState().clearOpponentMoves();
		for (int i = 1; i < params.length; i++) {
			try {
				Move move = null;
				String subCmd = params[i + 1];
				switch (subCmd) {
					case "place_armies":
						move = getOpponentPlaceArmyMove(params[i], Integer.parseInt(params[i + 2]), Integer.parseInt(params[i + 3]));
						i += 3;
						break;
					case "attack/transfer":
						move = getOpponentAttackTransferMove(params[i], Integer.parseInt(params[i + 2]), Integer.parseInt(params[i + 3]),
								Integer.parseInt(params[i + 4]));
						i += 4;
						break;
					default:
						break;
				}
				if (move != null) {
					GlobalState.getCurrentState().addOpponentMove(move);
				}
			} catch (Exception e) {
				System.err.println("Unable to parse Opponent moves " + e.getMessage());
			}
		}
	}

	private Move getOpponentPlaceArmyMove(String playerName, int regionId, int armies) {
		Region region = GlobalState.getCurrentState().getVisibleMap().getRegion(regionId);
		return new PlaceArmiesMove(playerName, region, armies);
	}

	private Move getOpponentAttackTransferMove(String playerName, int fromRegionId, int toRegionId, int armies) {
		Region fromRegion = GlobalState.getCurrentState().getVisibleMap().getRegion(fromRegionId);
		if (fromRegion == null) {
			fromRegion = GlobalState.getFullMap().getRegion(fromRegionId);
		}
		Region toRegion = GlobalState.getCurrentState().getVisibleMap().getRegion(toRegionId);
		if (toRegion == null) {
			toRegion = GlobalState.getFullMap().getRegion(toRegionId);
		}
		return new AttackTransferMove(playerName, fromRegion, toRegion, armies, MOVE_TYPE.UNKNOWN);
	}

	private void parseError() {
		System.err.printf("Unable to parse line \"%s\"\n", line);
	}

}
