package bank.infrastructure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import bank.domain.exceptions.PersistenceException;
import bank.domain.model.Bank;
import bank.domain.service.BankRepository;

/**
 * Implémentation de BankRepository stockant les données dans un fichier texte.
 * <p>
 * Cette classe gère exclusivement les opérations d'entrée/sortie (I/O) vers le disque.
 * La responsabilité du formatage des données (JSON, CSV, XML, etc.) est déléguée
 * à l'interface {@link BankSerializer}, permettant de changer de format sans modifier
 * la logique de stockage.
 * </p>
 */
public class FileBankRepository implements BankRepository {

    private final Path path;
    private final BankSerializer serializer;

    /**
     * Construit un nouveau repository fichier.
     *
     * @param path       Le chemin du fichier où les données seront stockées.
     * @param serializer Le sérialiseur responsable de la conversion Bank <-> String.
     */
    public FileBankRepository(Path path, BankSerializer serializer) {
        this.path = path;
        this.serializer = serializer;
    }

    @Override
    public void save(Bank bank) throws PersistenceException {
        try {
            // 1. Délégation de la transformation en String au serializer
            String data = serializer.serialize(bank);
            
            // 2. Écriture physique sur le disque
            Files.writeString(path, data);
            
        } catch (IOException e) {
            // 3. Encapsulation de l'erreur technique dans l'exception métier
            throw new PersistenceException("Impossible de sauvegarder la banque dans le fichier : " + path, e);
        }
    }

    @Override
    public Bank load() throws PersistenceException {
        try {
            // 1. Lecture physique depuis le disque
            String data = Files.readString(path);
            
            // 2. Délégation de la reconstruction de l'objet au serializer
            return serializer.deserialize(data);
            
        } catch (IOException e) {
            // 3. Encapsulation de l'erreur technique
            throw new PersistenceException("Impossible de charger la banque depuis le fichier : " + path, e);
        }
    }
}