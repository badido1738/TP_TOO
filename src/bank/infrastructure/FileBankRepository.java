package bank.infrastructure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import bank.domain.exceptions.PersistenceException;
import bank.domain.model.Bank;
import bank.domain.service.BankRepository;


public class FileBankRepository implements BankRepository {

    private final Path path;
    private final BankSerializer serializer;


    public FileBankRepository(Path path, BankSerializer serializer) {
        this.path = path;
        this.serializer = serializer;
    }

    @Override
    public void save(Bank bank) throws PersistenceException {
        try {
            String data = serializer.serialize(bank);
            
            Files.writeString(path, data);
            
        } catch (IOException e) {
            throw new PersistenceException("Impossible de sauvegarder la banque dans le fichier : " + path, e);
        }
    }

    @Override
    public Bank load() throws PersistenceException {
        try {
            // Lecture physique depuis le disque
            String data = Files.readString(path);
            
            // Délégation de la reconstruction de l'objet au serializer
            return serializer.deserialize(data);
            
        } catch (IOException e) {
            // Encapsulation de l'erreur technique
            throw new PersistenceException("Impossible de charger la banque depuis le fichier : " + path, e);
        }
    }
}