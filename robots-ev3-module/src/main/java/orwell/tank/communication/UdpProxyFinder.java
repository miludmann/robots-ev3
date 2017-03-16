package orwell.tank.communication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.SimpleKeyListener;
import utils.BroadcastAddress;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

public class UdpProxyFinder {
    public static final String DISCOVER_PROXY_ROBOTS_REQUEST = "DISCOVER_PROXY-ROBOTS_REQUEST";
    private final static Logger logback = LoggerFactory.getLogger(UdpProxyFinder.class);
    private final int broadcastPort;
    private final int receiverBufferSize = 512;
    private final DatagramSocket datagramSocket;
    private final UdpBroadcastDataDecoder udpBroadcastDataDecoder;
    private int attemptsPerformed = 0;
    private String pushAddress;
    private String pullAddress;
    private SimpleKeyListener simpleKeyListener;

    public UdpProxyFinder(
            final DatagramSocket datagramSocket,
            final int broadcastPort,
            final UdpBroadcastDataDecoder udpBroadcastDataDecoder,
            SimpleKeyListener simpleKeyListener) {
        assert null != datagramSocket;
        assert null != udpBroadcastDataDecoder;
        this.datagramSocket = datagramSocket;
        this.broadcastPort = broadcastPort;
        this.udpBroadcastDataDecoder = udpBroadcastDataDecoder;
        this.simpleKeyListener = simpleKeyListener;
    }

    /**
     * @return true if we did not get any meaningful response from server
     * and have not reached max allowed attempts number
     */
    private boolean shouldTryToFindBeacon() {
        return (!simpleKeyListener.wasKeyPressed() &&
                !udpBroadcastDataDecoder.hasReceivedCorrectData());
    }

    /**
     * Find the server using UDP broadcast and fill data fields
     */
    public void broadcastAndGetServerAddress() {
        try {
            datagramSocket.setBroadcast(true);
            while (shouldTryToFindBeacon()) {
                logback.info("Trying to find UDP beacon on port " + broadcastPort + ", attempt [" + (attemptsPerformed + 1) + "]");
                // Broadcast the message over all the network interfaces
                final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

                while (interfaces.hasMoreElements()) {
                    final NetworkInterface networkInterface = interfaces.nextElement();
                    sendBroadcastToInterface(networkInterface);
                }

                logback.info("Done looping over all network interfaces. Now waiting for a reply!");
                waitForServerResponse(datagramSocket);

                attemptsPerformed++;
            }
            fillFoundAddressFields();
            datagramSocket.close();


        } catch (final Exception e) {
            logback.error(e.getMessage());
        }
    }

    private void sendBroadcastToInterface(final NetworkInterface networkInterface) throws SocketException {
        if (networkInterface.isLoopback() || !networkInterface.isUp()) {
            return; // Do not broadcast to the loopback interface
        }

        for (final InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
            logback.info("interface address = " + interfaceAddress.getAddress() + " ; broadcast = " + interfaceAddress.getBroadcast());
            final InetAddress broadcastAddress = BroadcastAddress.getBroadcastAddress(interfaceAddress.getAddress(), interfaceAddress.getBroadcast());
            if (null != broadcastAddress) {
                logback.info("Trying to send broadcast package on interface: " + networkInterface.getDisplayName());
                sendBroadcastPackageToAddress(broadcastAddress);
            }
        }
    }

    private void sendBroadcastPackageToAddress(final InetAddress broadcastAddress) {
        try {
            final String broadcastAddrString = broadcastAddress.getHostAddress();

            final byte[] requestBytes = DISCOVER_PROXY_ROBOTS_REQUEST.getBytes();
            //final DatagramPacket datagramPacket = new DatagramPacket(requestBytes, requestBytes.length, broadcastAddress, broadcastPort);
            final DatagramPacket datagramPacket = new DatagramPacket(requestBytes, requestBytes.length, InetAddress.getByName(broadcastAddrString), broadcastPort);
            datagramSocket.send(datagramPacket);
        } catch (final Exception e) {
            logback.error(e.getMessage());
        }

        logback.info("Request packet sent to: " + broadcastAddress.getHostAddress());
    }

    private void waitForServerResponse(final DatagramSocket datagramSocket) throws IOException {
        final byte[] recvBuf = new byte[receiverBufferSize];
        final DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);

        try {
            datagramSocket.receive(receivePacket);
            udpBroadcastDataDecoder.parseFrom(receivePacket);
        } catch (final SocketTimeoutException e) {
            logback.info("Datagram socket received timeout, continue...");
        }
    }

    private void fillFoundAddressFields() {
        if (hasFoundServer()) {
            this.pushAddress = udpBroadcastDataDecoder.getPushAddress();
            this.pullAddress = udpBroadcastDataDecoder.getPullAddress();
        }
    }

    /**
     * @return null if broadcast was not called first
     * otherwise return puller address of the server (push on proxy side)
     */
    public String getPushAddress() {
        return pushAddress;
    }

    /**
     * @return null if broadcast was not called first
     * otherwise return publisher address of the server (subscribe on proxy side)
     */
    public String getPullAddress() {
        return pullAddress;
    }

    /**
     * @return true if UdpProxyFinder has found the server and it returned correct data
     */
    public boolean hasFoundServer() {
        return udpBroadcastDataDecoder.hasReceivedCorrectData();
    }
}
