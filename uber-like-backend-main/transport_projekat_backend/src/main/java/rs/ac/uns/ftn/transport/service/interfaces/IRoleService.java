package rs.ac.uns.ftn.transport.service.interfaces;

import rs.ac.uns.ftn.transport.model.Role;

import java.util.List;

public interface IRoleService {
	Role findById(Long id);
	List<Role> findByName(String name);
}
