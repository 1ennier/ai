package weight;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RegionWeightArmyPlace {

	private static Properties p;

	public enum PROP {
		opponentNear, freeBonus, possibleBonus
	}

	public static void initByFile() throws IOException {
		p = new Properties();
		InputStream fis = RegionWeightArmyPlace.class.getResourceAsStream("/regionarmyplace.properties");
		p.load(fis);
		fis.close();
	}

	public static void initManually() {
		p = new Properties();
		p.setProperty(PROP.opponentNear.name(), "8");
		p.setProperty(PROP.freeBonus.name(), "2");
		p.setProperty(PROP.possibleBonus.name(), "1");
	}

	public static int getProp(PROP prop) {
		return Integer.parseInt(p.getProperty(prop.name()));
	}

}
