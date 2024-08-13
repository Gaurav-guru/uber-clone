package rs.ac.uns.ftn.transport.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import rs.ac.uns.ftn.transport.dto.MessageDTO;
import rs.ac.uns.ftn.transport.dto.NotePageDTO;
import rs.ac.uns.ftn.transport.dto.TokenDTO;
import rs.ac.uns.ftn.transport.model.*;

import java.util.Set;

public interface IUserService extends UserDetailsService {

    User save(User user);
    User findOne(Integer id);
    Page<User> findAll(Pageable page);
    Passenger findByLogin(User user);
    Set<MessageDTO> findMessagesOfUser(Integer id);
    Set<Role> findRolesOfUser(String username);
    Message SaveMessage(Message message);

    void blockUser(Integer id);

    void unblockUser(Integer id);

    Note saveNote(Integer id, Note note);

    NotePageDTO findNotes(Integer id, Pageable page);

    User findByEmail(String email);
}
