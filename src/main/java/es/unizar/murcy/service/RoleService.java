package es.unizar.murcy.service;

import es.unizar.murcy.model.Role;
import es.unizar.murcy.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RoleService {

    @Autowired
    RoleRepository roleRepository;

    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    public Optional<Role> findRoleById(long id) {
        return roleRepository.findById(id);
    }

    public Optional<Role> findRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    public Role createRole(Role r) {
        return roleRepository.save(r);
    }

    public Role updateRole(Role r) {
        return roleRepository.save(r);
    }

    public void deleteRole(long id) {
        roleRepository.deleteById(id);
    }
}