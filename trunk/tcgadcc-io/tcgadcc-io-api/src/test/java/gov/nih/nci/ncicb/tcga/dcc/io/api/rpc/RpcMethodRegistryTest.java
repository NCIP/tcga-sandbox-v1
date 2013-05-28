package gov.nih.nci.ncicb.tcga.dcc.io.api.rpc;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.io.api.endpoint.RpcTestEnpoint;
import gov.nih.nci.ncicb.tcga.dcc.io.api.rpc.reflect.RpcMethodRegistry;

import java.util.Arrays;
import java.util.List;

import javax.xml.ws.Endpoint;

import junit.framework.Assert;

import org.junit.Test;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

public class RpcMethodRegistryTest {

    @Test
    public void testMethodCanBeCalled() {
        RpcTestEnpoint exampleObject = new RpcTestEnpoint();

        RpcMethodRegistry rpcMethodRegistry = new RpcMethodRegistry();
        rpcMethodRegistry.registerObject("endpoint", exampleObject);

        try {
            JsonObject result = rpcMethodRegistry.invokeTarget("endpoint.sum", null, 1, 2);
            assertEquals(3, result.getNumber("result"));
        }
        catch (RpcException e) {
            e.printStackTrace();
            Assert.fail("Should not throw exception [" + e.getMessage() + "]");
        }
    }

    @Test(expected=RpcException.class)
    public void testMethodCalledWithWrongNumberOfArguments() throws RpcException {
        RpcTestEnpoint exampleObject = new RpcTestEnpoint();

        RpcMethodRegistry rpcMethodRegistry = new RpcMethodRegistry();
        rpcMethodRegistry.registerObject("endpoint", exampleObject);

        rpcMethodRegistry.invokeTarget("endpoint.sum", null, 1, 2, 3);
    }

    @Test
    public void testNoSuchMethod() {
        RpcTestEnpoint exampleObject = new RpcTestEnpoint();

        RpcMethodRegistry rpcMethodRegistry = new RpcMethodRegistry();
        rpcMethodRegistry.registerObject("endpoint", exampleObject);

        try {
            rpcMethodRegistry.invokeTarget("endpoint.invalidmethod", null, 1, 2, 3);
        }
        catch (RpcException e) {
            assertEquals(NoSuchMethodException.class, e.getCause().getClass());
        }
    }

    @Test
    public void testNoSuchTarget() {
        RpcMethodRegistry rpcMethodRegistry = new RpcMethodRegistry();

        try {
            rpcMethodRegistry.invokeTarget("endpoint.sum", null, 1, 2);
        }
        catch (RpcException e) {
            assertEquals(RpcException.class, e.getClass());
            assertEquals("No such invocation target [endpoint]", e.getMessage());
        }
    }

    @Test
    public void testTargetListIsCorrect() {
        RpcTestEnpoint exampleObject = new RpcTestEnpoint();

        RpcMethodRegistry rpcMethodRegistry = new RpcMethodRegistry();
        rpcMethodRegistry.registerObject("endpoint", exampleObject);

        List<String> callableTargets = rpcMethodRegistry.callableTargets();

        for (String callableTarget : callableTargets) {
            System.out.println(callableTarget);
        }

        assertTrue(callableTargets.containsAll(
                Arrays.asList("endpoint.sum", "endpoint.diff", "system.listMethods")));

        try {
            JsonArray result = rpcMethodRegistry.invokeTarget(
                    "system.listMethods",
                    new Object[] { null }).getArray("methods");
            
            assertTrue(Arrays.asList(result.toArray()).containsAll(
                    Arrays.asList("endpoint.sum", "endpoint.diff", "system.listMethods")));
        }
        catch (RpcException e) {
            Assert.fail("Should not throw exception [" + e.getMessage() + "]");
        }
    }

}
