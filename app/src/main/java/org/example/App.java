/*
 * This source file was generated by the Gradle 'init' task
 */
package org.example;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.connections.impl.DBusConnectionBuilder;
import org.freedesktop.dbus.interfaces.DBusSigHandler;
import org.freedesktop.dbus.types.Variant;

import java.util.Map;

public class App {
    public DBusConnection receive() throws Exception {
        final var busType = DBusConnection.DBusBusType.SESSION;
        final var dBusConn = DBusConnectionBuilder.forType(busType).build();
        var signal = dBusConn.getRemoteObject("org.example.App", "/test", Signal.class);
        System.out.println("GOT " + signal.getObjectPath());

        final DBusSigHandler<Signal.MessageReceivedV2> dbusMsgHandler = messageReceived -> {
            System.out.println("Received: " + messageReceived.getMessage());
        };
        dBusConn.addSigHandler(Signal.MessageReceivedV2.class, signal, dbusMsgHandler);

        final byte[] bytesA = signal.test();
        System.out.println("A with " + bytesA.length + " bytes");

        final byte[] bytesB = signal.Get("Signal", "Id");
        System.out.println("B with " + bytesB.length + " bytes");

        final Map<String, Variant<?>> properties = signal.GetAll("Signal");
        final byte[] bytesC = (byte[]) properties.get("Id").getValue();
        System.out.println("C with " + bytesC.length + " bytes");

        return dBusConn;
    }

    public DBusConnection export() throws Exception {
        final var busType = DBusConnection.DBusBusType.SESSION;
        var dBusConn = DBusConnectionBuilder.forType(busType).build();
        dBusConn.requestBusName("org.example.App");
        dBusConn.exportObject(new Signal() {
            @Override
            public byte[] test() {
                return new byte[]{};
            }

            @Override
            public <A> A Get(String s, String s1) {
                return (A) new byte[]{};
            }

            @Override
            public <A> void Set(String s, String s1, A a) {
            }

            @Override
            public Map<String, Variant<?>> GetAll(String s) {
                return Map.of("Id", new Variant<>(new byte[]{}));
            }

            @Override
            public String getObjectPath() {
                return "/test";
            }
        });
        return dBusConn;
    }

    public static void main(String[] args) throws Exception {
        final var app = new App();
        try (var exportDBusConn = app.export();
             var receiveDBusConn = app.receive()) {

            exportDBusConn.sendMessage(new Signal.MessageReceivedV2("/test",
                    System.currentTimeMillis(),
                    "sender",
                    new byte[]{},
                    "message",
                    Map.of()));
            Thread.sleep(3000);
        }
    }
}
