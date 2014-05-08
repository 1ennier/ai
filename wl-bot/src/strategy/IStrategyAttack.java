package strategy;

import java.util.ArrayList;

import move.AttackTransferMove;

public interface IStrategyAttack extends IStrategy {

	public ArrayList<AttackTransferMove> getMoves();

}
