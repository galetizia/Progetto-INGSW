package bugboard.service;

import bugboard.model.Feature;
import bugboard.repository.FeatureRepository;
import org.springframework.stereotype.Service;

@Service
public class FeatureService {
    private final FeatureRepository featureRepository;

    public FeatureService(FeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }

    // Creare eventuale metodo

}