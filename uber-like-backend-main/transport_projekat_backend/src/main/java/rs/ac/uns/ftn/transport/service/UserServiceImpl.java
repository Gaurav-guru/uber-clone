package rs.ac.uns.ftn.transport.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.transport.dto.MessageDTO;
import rs.ac.uns.ftn.transport.dto.NoteDTO;
import rs.ac.uns.ftn.transport.dto.NotePageDTO;
import rs.ac.uns.ftn.transport.dto.TokenDTO;
import rs.ac.uns.ftn.transport.mapper.MessageDTOMapper;
import rs.ac.uns.ftn.transport.mapper.NoteDTOMapper;
import rs.ac.uns.ftn.transport.model.*;
import rs.ac.uns.ftn.transport.repository.MessageRepository;
import rs.ac.uns.ftn.transport.repository.NoteRepository;
import rs.ac.uns.ftn.transport.repository.UserRepository;
import rs.ac.uns.ftn.transport.service.interfaces.IRoleService;
import rs.ac.uns.ftn.transport.service.interfaces.IUserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final MessageRepository messageRepository;
    private final IRoleService roleService;

    public UserServiceImpl(IRoleService roleService, UserRepository userRepository, MessageRepository messageRepository, NoteRepository noteRepository){
        this.roleService = roleService;
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }
    public User save(User user)
    {
        user.setRoles(roleService.findByName(user.getRoles().get(0).getName()));
        return userRepository.save(user);
    }

    @Override
    public User findOne(Integer id) {
        Optional<User> found = userRepository.findById(id);
        if (found.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return found.get();
    }

    @Override
    public Page<User> findAll(Pageable page) {
        return userRepository.findAll(page);
    }

    @Override
    public Passenger findByLogin(User user) {
        return (Passenger) userRepository.findByLogin(user.getEmail(), user.getPassword());
    }

    @Override
    public Set<MessageDTO> findMessagesOfUser(Integer id) {
        Optional<User> userO = userRepository.findById(id);
        if(userO.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        User user = userO.get();
        Set<Message> messages = messageRepository.findBySender(user);
        Set<MessageDTO> messageDTOS = messages.stream().map(MessageDTOMapper::fromMessagetoDTO).collect(Collectors.toSet());
        return messageDTOS;
    }

    @Override
    public Set<Role> findRolesOfUser(String username) {
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(1L, "ROLE_PASSENGER"));
        return roles;
    }

    @Override
    public Message SaveMessage(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public void blockUser(Integer id) {
        User user = findOne(id);
        if(user.getIsBlocked())
            throw new DataIntegrityViolationException("User is blocked!");
        user.setIsBlocked(true);
        save(user);
    }

    @Override
    public void unblockUser(Integer id) {
        User user = findOne(id);
        if(!user.getIsBlocked())
            throw new DataIntegrityViolationException("User is not blocked!");
        user.setIsBlocked(false);
        save(user);
    }

    @Override
    public Note saveNote(Integer id, Note note) {
        Optional<User> userO = userRepository.findById(id);
        if(userO.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        User user = userO.get();
        note.setUser(user);
        note.setDate(LocalDateTime.now());

        return noteRepository.save(note);
    }

    @Override
    public NotePageDTO findNotes(Integer id, Pageable page) {
        Optional<User> userO = userRepository.findById(id);
        if(userO.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        User user = userO.get();
        Page<Note> notes = noteRepository.findByUser(user, page);
        Set<NoteDTO> noteDTOS = notes.stream().map(NoteDTOMapper::fromNotetoDTO).collect(Collectors.toSet());
        return new NotePageDTO((long) noteDTOS.size(), noteDTOS);
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        } else {
            return user;
        }
    }
}
