package org.gridgain.demo;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.gridgain.control.agent.processor.deployment.ManagedDeploymentSpi;
import org.gridgain.grid.configuration.GridGainConfiguration;

import java.io.Closeable;
import java.util.Collections;

public class ClusterClient implements Closeable {

    private Ignite ignite = null;

    public ClusterClient() {
        System.setProperty("IGNITE_EVENT_DRIVEN_SERVICE_PROCESSOR_ENABLED", "true");
    }

    public Ignite startClient() {
        IgniteConfiguration iConfig = new IgniteConfiguration();
        iConfig.setDeploymentSpi(new ManagedDeploymentSpi());
        iConfig.setClientMode(true);
        iConfig.setDiscoverySpi(new TcpDiscoverySpi()
                        .setIpFinder(new TcpDiscoveryVmIpFinder()
                                .setAddresses(Collections.singleton("127.0.0.1:47500..47509"))));
        iConfig.setPeerClassLoadingEnabled(true);
        GridGainConfiguration gConfig = new GridGainConfiguration();
        gConfig.setDataCenterId(Byte.valueOf("1"));
        gConfig.setLicenseUrl("C:\\Users\\peter.whitney\\license\\gridgain-ultimate-license.xml");
        gConfig.setRollingUpdatesEnabled(true);
        iConfig.setPluginConfigurations(gConfig);
        ignite = Ignition.start(iConfig);
        ignite.log().debug("org.gridgain.demo.ClusterClient ignite client started");
        return ignite;
    }

    public void close() {
        ignite.log().debug("org.gridgain.demo.ClusterClient closing ignite client");
        ignite.close();
    }

}
