package rs.ac.uns.ftn.transport.service;

import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.transport.model.Role;
import rs.ac.uns.ftn.transport.repository.RoleRepository;
import rs.ac.uns.ftn.transport.service.interfaces.IRoleService;

import java.util.List;

@Service
public class RoleServiceImpl implements IRoleService {

  private final RoleRepository roleRepository;

  public RoleServiceImpl(RoleRepository roleRepository){
    this.roleRepository = roleRepository;
  }

  @Override
  public Role findById(Long id) {
    Role auth = this.roleRepository.getOne(id);
    return auth;
  }

  @Override
  public List<Role> findByName(String name) {
    return this.roleRepository.findByName(name);
  }
}
