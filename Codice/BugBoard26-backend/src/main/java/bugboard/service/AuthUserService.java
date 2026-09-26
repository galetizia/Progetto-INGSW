package bugboard.service;

import bugboard.model.AuthUser;
import bugboard.repository.AuthUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestisce la logica di business relativa agli utenti (registrazione, autenticazione, gestione dell'account e statistiche).
 */
@Service
public class AuthUserService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    /**
     * Inizializza il servizio passando le dipendenze necessarie per l'accesso ai dati e la sicurezza.
     *
     * @param authUserRepository La repository per interagire con la tabella degli utenti.
     * @param passwordEncoder    L'encoder per cifrare e verificare le password.
     * @param jwtService         Il service per la creazione dei token di sessione.
     */
    public AuthUserService(AuthUserRepository authUserRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }


    /**
     * Registra un nuovo account nel database, verificando prima che l'indirizzo email non sia già in uso.
     *
     * @param email    L'indirizzo email del nuovo utente.
     * @param password La password in chiaro che verrà cifrata prima del salvataggio.
     * @param ruolo    Il ruolo da assegnare.
     * @throws IllegalArgumentException Se l'email fornita è già presente nel database.
     */
    public void registerAuthUser(String email, String password, String ruolo) {
        if (authUserRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email già in uso");
        }

        String hashedPass = passwordEncoder.encode(password);
        AuthUser newUser = new AuthUser(hashedPass, email);

        newUser.setRuolo(bugboard.enums.Ruolo.valueOf(ruolo));

        authUserRepository.save(newUser);
    }


    /**
     * Abilita o disabilita l'account di un utente specifico.
     *
     * @param id L'identificativo dell'utente.
     * @throws IllegalArgumentException Se l'ID fornito non corrisponde a nessun utente.
     */
    public void cambiaStatoUtente(int id) {
        AuthUser user = authUserRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        user.setStatoAccount(!user.getStatoAccount());

        authUserRepository.save(user);
    }


    /**
     * Verifica le credenziali di accesso e lo stato dell'account per consentire l'ingresso nel sistema.
     *
     * @param email    L'email dell'utente che tenta il login.
     * @param password La password in chiaro da confrontare con l'hash salvato nel database.
     * @return Il token JWT generato in caso di autenticazione riuscita.
     * @throws IllegalArgumentException Se le credenziali sono errate o se l'account è disabilitato.
     */
    public String login (String email, String password) {

        AuthUser user = authUserRepository.findByEmail(email).orElseThrow(() ->
                new IllegalArgumentException("Email non valida"));

        if(!passwordEncoder.matches(password,user.getPassword())) {
            throw new IllegalArgumentException("Password non valida");
        }
        if(!user.getStatoAccount()){
            throw new IllegalArgumentException("Account esistente ma non attivo");
        }

        return jwtService.generateToken(user, user.getRuolo().name());
    }


    /**
     * Aggiorna la password dell'utente assicurandosi che conosca quella attualmente impostata.
     *
     * @param email       L'email dell'utente che richiede il cambio.
     * @param oldPassword La password attuale.
     * @param newPassword La nuova password da impostare.
     * @throws IllegalArgumentException Se l'utente non esiste, la vecchia password è errata o la nuova coincide con la precedente.
     */
    public void changePassword (String email, String oldPassword, String newPassword) {

        AuthUser user = authUserRepository.findByEmail(email).orElseThrow(() ->
                new IllegalArgumentException("Email non valida"));

        if(!passwordEncoder.matches(oldPassword,user.getPassword())) {
            throw new IllegalArgumentException("Inserire la vecchia password corretta");
        }

        if(newPassword.equals(oldPassword)) {
            throw new IllegalArgumentException("La nuova password non può essere uguale alla precedente");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        authUserRepository.save(user);
    }


    /**
     * Calcola il tempo medio di risoluzione delle issue per ciascun utente.
     *
     * @return Una mappa contenente l'email come chiave e il tempo medio calcolato (in ore).
     */
    public Map<String, Double> getTimePerUser(){
        List<Object[]> timePerUser = authUserRepository.findAllTimePerUser();
        Map<String, Double> map = new HashMap<>();
        for(Object[] obj : timePerUser){
            String email = (String) obj[0];
            Double count = (obj[1] != null) ? ((Number) obj[1]).doubleValue() : 0.0;
            map.put(email, count);
        }
        return map;
    }


    /**
     * Recupera il numero totale di issue attualmente assegnate e in corso per ciascun utente.
     *
     * @return Una mappa avente l'email come chiave e il conteggio delle issue.
     */
    public Map<String, Integer> getIssuesPerUser(){
        List<Object[]> issuesPerUser = authUserRepository.findAllIssuesPerUser();
        return insertInMap(issuesPerUser);
    }


    /**
     * Recupera il numero totale di issue risolte da ciascun utente.
     *
     * @return Una mappa avente l'email come chiave e il conteggio delle issue risolte.
     */
    public Map<String, Integer> getRisoltePerUser(){
        List<Object[]> issuesPerUser = authUserRepository.findAllRisoltePerUser();
        return insertInMap(issuesPerUser);

    }


    /**
     * Metodo di supporto per convertire la lista di risultati grezzi dal database in una mappa strutturata.
     *
     * @param list La lista di array di oggetti restituita dalla query nativa.
     * @return La mappa elaborata e tipizzata per le statistiche numeriche.
     */
    private Map<String, Integer> insertInMap(List<Object[]> list){
        Map<String, Integer> map = new HashMap<>();
        for(Object[] obj : list){
            String email = (String) obj[0];
            Integer count = ((Number) obj[1]).intValue();
            map.put(email, count);
        }
        return map;
    }


    /**
     * Recupera l'elenco di tutti gli utenti salvati nel database.
     *
     * @return La lista completa degli utenti.
     */
    public List<AuthUser> getAllUsers() {return authUserRepository.findAll();}


    /**
     * Recupera un utente specifico tramite il suo indirizzo email.
     *
     * @param email L'indirizzo email da cercare.
     * @return L'oggetto AuthUser trovato.
     * @throws IllegalArgumentException Se non esiste alcun utente con l'email fornita.
     */
    public AuthUser getUserByEmail(String email) {
        return authUserRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
    }
}
