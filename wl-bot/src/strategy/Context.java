package strategy;

public class Context {

	private IPickStrategy strategyPick;
	private IPlaceArmiesStrategy strategyPlaceArmies;

	public void setStrategyPick(IPickStrategy strategy) {
		this.strategyPick = strategy;
	}

	public IPickStrategy getStrategyPick() {
		return strategyPick;
	}

	public void setStrategyPlaceArmies(IPlaceArmiesStrategy strategy) {
		this.strategyPlaceArmies = strategy;
	}

	public IPlaceArmiesStrategy getStrategyPlaceArmies() {
		return strategyPlaceArmies;
	}

}
