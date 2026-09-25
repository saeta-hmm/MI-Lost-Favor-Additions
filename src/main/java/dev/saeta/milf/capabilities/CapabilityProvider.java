package dev.saeta.milf.capabilities;

import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public interface CapabilityProvider {
    void registerCapabilities(RegisterCapabilitiesEvent event);
}
