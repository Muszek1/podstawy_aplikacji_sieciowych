package pl.lodz.p.adapter;

import pl.lodz.p.model.domain.Client;

import java.util.List;

public interface GuestRepositoryAdapterInterface {
    Client saveGuest(Client client);
    void setGuestArchivedStatus(String personalId, boolean isArchived);
    Client findGuestByPersonalId(String personalId);
    List<Client> findAllGuests();
}