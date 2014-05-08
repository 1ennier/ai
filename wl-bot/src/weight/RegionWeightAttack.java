package weight;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RegionWeightAttack {

	private static Properties p;

	public enum PROP {
		opponent, neutralInFreeBonus, neutralInPossibleFreeBonus, neutral
	}

	public static void initByFile() throws IOException {
		p = new Properties();
		InputStream fis = RegionWeightAttack.class.getResourceAsStream("/regionattack.properties");
		p.load(fis);
		fis.close();
	}

	public static void initManually() {
		p = new Properties();
		p.setProperty(PROP.opponent.name(), "8");
		p.setProperty(PROP.neutralInFreeBonus.name(), "4");
		p.setProperty(PROP.neutralInPossibleFreeBonus.name(), "2");
		p.setProperty(PROP.neutral.name(), "1");
	}

	public static int getProp(PROP prop) {
		return Integer.parseInt(p.getProperty(prop.name()));
	}

}
