package temp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RegionPickWeight {

	private static Properties p;

	public enum PROP {
		superRegionBonus, sameSuperRegionAndNear, neighbor, nearBonuses
	}

	public static void initByFile() throws IOException {
		p = new Properties();
		InputStream fis = RegionPickWeight.class.getResourceAsStream("/regionpick.properties");
		p.load(fis);
		fis.close();
	}

	public static void initManually() {
		p = new Properties();
		p.setProperty(PROP.superRegionBonus.name(), "8");
		p.setProperty(PROP.sameSuperRegionAndNear.name(), "4");
		p.setProperty(PROP.neighbor.name(), "2");
		p.setProperty(PROP.nearBonuses.name(), "1");
	}

	public static int getProp(PROP prop) {
		return Integer.parseInt(p.getProperty(prop.name()));
	}

}
