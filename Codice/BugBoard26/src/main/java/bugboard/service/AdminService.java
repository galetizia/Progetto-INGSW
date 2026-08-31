package bugboard.service;

import bugboard.model.Admin;
import bugboard.model.AuthUser;
import bugboard.repository.AdminRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public void registerNewUser(AuthUser authUser) {
    }

}
