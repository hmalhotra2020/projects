package com.example.imagic.service;

import com.example.imagic.service.store.BaseStore;
import lombok.Data;
import org.javatuples.Quartet;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Data
public class ClientDataService {

    private Map<String, Quartet> map = new HashMap<>();

    public Quartet getClientData(String clientId)   {
        Quartet clientData = null;

        if(!map.containsKey(clientId)) {
            clientData = new Quartet(clientId, BaseStore.preferedStore, null, null);
            map.put(clientId, clientData);
        } else
            clientData = map.get(clientId);

        return clientData;
    }

}
