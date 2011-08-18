import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({InvestorsDataCollectorTests.class,
		StooqDataCollectorTests.class, AllegroCoinsDataCollectorTests.class,
		ArkaDataCollectorTests.class, DataUtilsTests.class,
		AllegroDataTests.class})
public class AllTests {

}
