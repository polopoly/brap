package no.tornado.brap;

import static org.hamcrest.core.DescribedAs.describedAs;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketImpl;
import java.net.SocketImplFactory;
import java.net.SocketOptions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matcher;
import org.hamcrest.core.Is;
import org.junit.Ignore;

import com.atex.common.collections.Pair;

/**
 * SocketHelpers
 *
 * @author mnova
 */
@Ignore
public class SocketHelpers {

    static List<SocketImpl> allServerSockets = Collections.synchronizedList(new ArrayList<>());
    static List<SocketImpl> allClientSockets = Collections.synchronizedList(new ArrayList<>());
    static Map<SocketImpl, String> stacktraces = new HashMap<>();

    public static void init() throws IOException {
        ServerSocket.setSocketFactory(new SpySocketImplFactory(allServerSockets));
        Socket.setSocketImplFactory(new SpySocketImplFactory(allClientSockets));
    }

    public static int checkOpenSockets(final String msg) {
        System.out.println("CHECK " + msg);
        System.out.println("Server Sockets: " + allServerSockets.size());
        System.out.println("Client Sockets: " + allClientSockets.size());

        int serverFailed = 0;
        int clientFailed = 0;
        for (SocketImpl impl : allServerSockets) {
            final Pair<ServerSocket, String> p = getServerSocket(impl);
            if (!assertIsClosed(p.left(), p.right())) {
                serverFailed += 1;
            }
        }
        for (SocketImpl impl : allClientSockets) {
            final Pair<Socket, String> p = getSocket(impl);
            if (!assertIsClosed(p.left(), p.right())) {
                clientFailed += 1;
            }
        }
        System.out.println("FAILED Server Sockets: " + serverFailed);
        System.out.println("FAILED Client Sockets: " + clientFailed);

        return serverFailed + clientFailed;
    }

    public static void checkOpenSockets() {
        for (SocketImpl impl : allServerSockets) {
            final Pair<ServerSocket, String> p = getServerSocket(impl);
            if (!p.left().isClosed()) {
                try {
                    impl.setOption(SocketOptions.SO_REUSEADDR, false);
                } catch (SocketException ignore) {
                }
            }
        }
        for (SocketImpl impl : allClientSockets) {
            final Pair<Socket, String> p = getSocket(impl);
            if (!p.left().isClosed()) {
                try {
                    impl.setOption(SocketOptions.SO_REUSEADDR, false);
                } catch (SocketException ignore) {
                }
            }
        }
    }

    // asserts

    public static boolean assertIsClosed(final Socket socket,
                                         final String stack) {
        try {
            assertThat(socket.isClosed(), isClosed(socket));
            return true;
        } catch (AssertionError e) {
            System.out.printf(
                    "Socket %s stacktrace: %s%n",
                    socket,
                    stack);
            //throw e;
            return false;
        }
    }

    public static boolean assertIsClosed(final ServerSocket socket,
                                         final String stack) {
        try {
            assertThat(socket.isClosed(), isClosed(socket));
            return true;
        } catch (AssertionError e) {
            System.out.printf(
                    "Socket %s stacktrace: %s%n",
                    socket,
                    stack);
            //throw e;
            return false;
        }
    }

    public static Matcher<Boolean> isClosed(final Object socket) {
        return describedAs("is closed: %0", Is.is(true), socket);
    }

    // socket helpers

    private static SocketImpl newSocketImpl() {
        try {
            Class<?> defaultSocketImpl = Class.forName("java.net.SocksSocketImpl");
            Constructor<?> constructor = defaultSocketImpl.getDeclaredConstructor();
            constructor.setAccessible(true);
            return (SocketImpl) constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Pair<Socket, String> getSocket(SocketImpl impl) {
        try {
            Method getSocket = SocketImpl.class.getDeclaredMethod("getSocket");
            getSocket.setAccessible(true);
            return Pair.of(
                    (Socket) getSocket.invoke(impl),
                    stacktraces.get(impl)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Pair<ServerSocket, String> getServerSocket(SocketImpl impl) {
        try {
            Method getServerSocket = SocketImpl.class.getDeclaredMethod("getServerSocket");
            getServerSocket.setAccessible(true);
            return Pair.of(
                    (ServerSocket) getServerSocket.invoke(impl),
                    stacktraces.get(impl)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class SpySocketImplFactory implements SocketImplFactory {

        private final List<SocketImpl> spy;

        public SpySocketImplFactory(List<SocketImpl> spy) {
            this.spy = spy;
        }

        @Override
        public SocketImpl createSocketImpl() {
            SocketImpl socket = newSocketImpl();
            spy.add(socket);
            StringWriter sw = new StringWriter();
            new Throwable("").printStackTrace(new PrintWriter(sw));
            String stackTrace = sw.toString();
            stacktraces.put(socket, stackTrace);
            return socket;
        }
    }


}
