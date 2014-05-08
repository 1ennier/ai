package strategy;

public class Context {

	private IStrategyPick strategyPick;
	private IStrategyPlaceArmies strategyPlaceArmies;
	private IStrategyAttack strategyAttack;

	public void setStrategyPick(IStrategyPick strategy) {
		this.strategyPick = strategy;
	}

	public IStrategyPick getStrategyPick() {
		return strategyPick;
	}

	public void setStrategyPlaceArmies(IStrategyPlaceArmies strategy) {
		this.strategyPlaceArmies = strategy;
	}

	public IStrategyPlaceArmies getStrategyPlaceArmies() {
		return strategyPlaceArmies;
	}

	public void setStrategyAttack(IStrategyAttack strategy) {
		this.strategyAttack = strategy;
	}

	public IStrategyAttack getStrategyAttack() {
		return strategyAttack;
	}

}
