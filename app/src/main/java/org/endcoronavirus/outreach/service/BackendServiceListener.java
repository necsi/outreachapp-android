package org.endcoronavirus.outreach.service;

public interface BackendServiceListener {

    void onServiceBound(BackendServiceInterface i);

    void onServiceUnbound(BackendServiceInterface i);
}
