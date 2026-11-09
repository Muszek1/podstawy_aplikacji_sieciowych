package pl.lodz.p.adapter;

import pl.lodz.p.model.db.*;
import pl.lodz.p.model.domain.*;
import pl.lodz.p.repository.MongoGuestRepository;

import java.util.ArrayList;
import java.util.List;

public class MongoGuestRepositoryAdapter implements GuestRepositoryAdapterInterface {
    private final MongoGuestRepository guestRepository;

    public MongoGuestRepositoryAdapter(MongoGuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    @Override
    public Client saveGuest(Client client) {
        UserEnt guestDoc = GuestMapper.mapGuestFromDomainToDb(client);
        UserEnt savedGuest = guestRepository.saveGuest(guestDoc);
        return GuestMapper.mapGuestFromDbToDomain(savedGuest);
    }

    @Override
    public void setGuestArchivedStatus(String personalId, boolean isArchived) {
        guestRepository.setGuestArchivedStatus(personalId, isArchived);
    }

    @Override
    public Client findGuestByLogin(String personalId) {
        UserEnt guestDoc = guestRepository.findGuestByPersonalId(personalId);
        return GuestMapper.mapGuestFromDbToDomain(guestDoc);
    }

    @Override
    public List<Client> findAllGuests() {
        List<Client> allClients = new ArrayList<>();
        for (UserEnt guestDoc : guestRepository.findAllGuests()) {
            allClients.add(GuestMapper.mapGuestFromDbToDomain(guestDoc));
        }
        return allClients;
    }

}
