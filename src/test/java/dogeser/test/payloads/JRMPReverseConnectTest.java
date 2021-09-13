package dogeser.test.payloads;


import java.util.concurrent.Callable;

import javax.management.BadAttributeValueExpException;

import dogeser.exploit.JRMPListener;
import dogeser.test.CustomTest;
import org.junit.Assert;


/**
 * @author mbechler
 *
 */
public class JRMPReverseConnectTest implements CustomTest {

    private int port;


    /**
     *
     */
    public JRMPReverseConnectTest () {
        // some payloads cannot specify the port
        port = 1099;
    }


    public void run ( Callable<Object> payload ) throws Exception {
        dogeser.exploit.JRMPListener l = new JRMPListener(port, new BadAttributeValueExpException("foo"));
        Thread t = new Thread(l, "JRMP listener");
        try {
            t.start();
            try {
                payload.call();
            }
            catch ( Exception e ) {
                // ignore
            }
            Assert.assertTrue("Did not have connection", l.waitFor(1000));
        }
        finally {
            l.close();
            t.interrupt();
            t.join();
        }
    }


    public String getPayloadArgs () {
        return "rmi:localhost:" + port;
    }

}
