package net.doctorg.drgstimers.proxy;

import net.neoforged.fml.loading.FMLEnvironment;

public class ProxyDrGsTimers {

    private final IProxyDrGsTimers clientProxy = new ClientProxy();
    private final IProxyDrGsTimers serverProxy = new ServerProxy();


    public void init() {
        if (FMLEnvironment.dist.isClient()) {
            clientProxy.init();
        } else {
            serverProxy.init();
        }
    }
}
