package tool.updater;

import core.db.user.UserManager;
import module.series.statistics.DataDownloader;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

@Deprecated
public class AlltidLinkTest {
    private static void setEnvVariable(String name, String value) {
        try {
            Map<String, String> env = System.getenv();
            Field field = env.getClass().getDeclaredField("m");
            field.setAccessible(true);
            ((Map<String, String>) field.get(env)).put(name, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        setEnvVariable("AppData",
                new File("").getAbsoluteFile().getParentFile().getAbsolutePath());

        UserManager.instance().getCurrentUser();

        var res = DataDownloader.instance().fetchLeagueTeamPowerRatings(3193, 10, 76);
        System.out.print(res);

    }

}
