package strategy;

public class Context {

	private IPickStrategy strategyPick;

	public void setStrategyPick(IPickStrategy strategy) {
		this.strategyPick = strategy;
	}

	public IPickStrategy getStrategyPick() {
		return strategyPick;
	}

}
