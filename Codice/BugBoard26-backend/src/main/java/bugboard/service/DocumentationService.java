package bugboard.service;

import bugboard.model.Documentation;
import bugboard.repository.DocumentationRepository;
import org.springframework.stereotype.Service;

@Service
public class DocumentationService {
    private final DocumentationRepository documentationRepository;

    public DocumentationService(DocumentationRepository documentationRepository) {
        this.documentationRepository = documentationRepository;
    }

    // Creare eventuale metodo

}