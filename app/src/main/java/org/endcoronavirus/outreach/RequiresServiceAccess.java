package org.endcoronavirus.outreach;

import org.endcoronavirus.outreach.service.BackendServiceInterface;

public interface RequiresServiceAccess {
    void setService(BackendServiceInterface service);
}
