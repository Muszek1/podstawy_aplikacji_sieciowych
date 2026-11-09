package pl.lodz.p.adapter;

import pl.lodz.p.model.domain.Client;

import java.util.List;

public interface GuestRepositoryAdapterInterface {
    Client saveGuest(Client client);
    void setGuestArchivedStatus(String personalId, boolean isArchived);
    Client findGuestByLogin(String personalId);
    List<Client> findAllGuests();
}