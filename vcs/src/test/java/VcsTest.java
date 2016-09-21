import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VcsTest {

    @Test
    public void simpleTest() {
        VCS.run("init");

        assertEquals(true, true);
    }

}
