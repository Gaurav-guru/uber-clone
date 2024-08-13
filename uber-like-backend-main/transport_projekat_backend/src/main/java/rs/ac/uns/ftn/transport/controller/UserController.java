package rs.ac.uns.ftn.transport.controller;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.transport.dto.*;
import rs.ac.uns.ftn.transport.dto.User.LoginDTO;
import rs.ac.uns.ftn.transport.dto.ride.RidePage2DTO;
import rs.ac.uns.ftn.transport.mapper.RideDTOMapper;
import rs.ac.uns.ftn.transport.mapper.UserDTOMapper;
import rs.ac.uns.ftn.transport.model.*;
import rs.ac.uns.ftn.transport.model.enumerations.MessageType;
import rs.ac.uns.ftn.transport.service.interfaces.*;
import rs.ac.uns.ftn.transport.util.TokenUtils;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping(value = "/api/user")
public class UserController {
    private final IUserService userService;
    private final IDriverService driverService;
    private final IPassengerService passengerService;
    private final IRideService rideService;
    private final MessageSource messageSource;
    private final IMailService mailService;
    private final AuthenticationManager authenticationManager;

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final TokenUtils tokenUtils;

    private BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public UserController(IUserService userService,
                          IDriverService driverService,
                          IPassengerService passengerService,
                          IRideService rideService,
                          MessageSource messageSource,
                          IMailService mailService,
                          AuthenticationManager authenticationManager,
                          SimpMessagingTemplate simpMessagingTemplate, TokenUtils tokenUtils) {
        this.rideService = rideService;
        this.passengerService = passengerService;
        this.driverService = driverService;
        this.userService = userService;
        this.messageSource = messageSource;
        this.mailService = mailService;
        this.authenticationManager = authenticationManager;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.tokenUtils = tokenUtils;
    }

    @PutMapping(value = "/{id}/changePassword", consumes = "application/json")
    public ResponseEntity<?> changePassword(@PathVariable Integer id,
                                            @Valid @RequestBody ChangePasswordDTO dto) throws ConstraintViolationException {
        User user;
        try {
            user = userService.findOne(id);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("user.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }
        BCryptPasswordEncoder passwordEncoder = this.passwordEncoder();
        if (!passwordEncoder.matches(dto.getOldPassword(),user.getPassword())) {
            System.out.println(dto);
            System.out.println("not matching");
            return new ResponseEntity<>(new ResponseMessage(messageSource.getMessage("user.passwordMatch", null, Locale.getDefault())), HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userService.save(user);

        return new ResponseEntity<>("Password successfully changed!", HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/{id}/resetPassword")
    public ResponseEntity<?> resetPassword(@PathVariable Integer id) throws MessagingException, UnsupportedEncodingException {
        User user;
        try {
            user = userService.findOne(id);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("user.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }

        String token = String.valueOf(new Random().nextInt(900000) + 100000); // 6-digit random number

        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiration(LocalDateTime.now().plusMinutes(10));
        userService.save(user);

        mailService.sendMail(user.getEmail(), token);

        return new ResponseEntity<>("Email with reset code has been sent!", HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = "/{id}/resetPassword", consumes = "application/json")
    public ResponseEntity<?> resetPassword(@PathVariable Integer id,
                                           @Valid @RequestBody ResetPasswordDTO dto) throws ConstraintViolationException {
        User user;
        try {
            user = userService.findOne(id);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("user.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }

        if (user.getResetPasswordToken() == null || user.getResetPasswordTokenExpiration().isBefore(LocalDateTime.now()) || !user.getResetPasswordToken().equals(dto.getCode())) {
            return new ResponseEntity<>(new ResponseMessage(messageSource.getMessage("user.resetToken", null, Locale.getDefault())), HttpStatus.BAD_REQUEST);
        }

        user.setPassword(dto.getNewPassword());
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiration(null);
        userService.save(user);

        return new ResponseEntity<>("Password successfully changed!", HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/{id}/ride")
    public ResponseEntity<?> findRides(@PathVariable Integer id, Pageable page) {
        try {
            User user = userService.findOne(id);
            Page<Ride> rides = rideService.findPassenger(id, (page));
            if (rides.isEmpty()) {
                rides = rideService.findAllByDriver_Id(id, (page));
            }
            Set<RideDTO> rideDTOs = rides.stream()
                    .map(RideDTOMapper::fromRidetoDTO)
                    .collect(Collectors.toSet());
            return new ResponseEntity<>(new RidePage2DTO(rides.getTotalElements(), rideDTOs), HttpStatus.OK);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("user.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<?> findUsers(Pageable page) {
        Page<User> users = userService.findAll(page);
        Set<UserDTO> userDTOS = new HashSet<>();
        if (users.getTotalElements() != 0)
            userDTOS = users.stream().map(UserDTOMapper::fromUsertoDTO).collect(Collectors.toSet());

        return new ResponseEntity<>(new UserPageDTO(users.getTotalElements(), userDTOS), HttpStatus.OK);
    }

    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<?> login(
            @RequestBody LoginDTO authenticationRequest, HttpServletResponse response) {
        try{Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getEmail(), authenticationRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
//        UserInfoMobileAppDTO userDTO = new UserInfoMobileAppDTO(user.getId(),
//                                                                user.getName(),
//                                                                user.getSurname(),
//                                                                user.getUsername(),
//                                                                user.getProfilePicture(),
//                                                                user.getRoles().get(0).getName()
//                );
//        return ResponseEntity.ok(userDTO);
        String jwt = tokenUtils.generateToken(user.getUsername(), (user.getRoles()).get(0));
        return ResponseEntity.ok(new TokenDTO(jwt, jwt));
        }
        catch(Exception ex) {
            System.out.println(ex);
        }
        return ResponseEntity.ok(new TokenDTO(null, null));
    }

    @GetMapping(value = "/{id}/message")
    public ResponseEntity<?> findUserMessages(@PathVariable Integer id) {
        try {
            Set<MessageDTO> messageDTOS = userService.findMessagesOfUser(id);
            return new ResponseEntity<>(new MessagePageDTO((long) messageDTOS.size(), messageDTOS), HttpStatus.OK);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("user.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/{id}/message")
    public ResponseEntity<?> createMessage(@PathVariable Integer id, @RequestBody MessageLightDTO messageLight) {
        try {
            Message message = new Message();
            message.setSender(userService.findOne(id));
            message.setReceiver(userService.findOne(messageLight.getReceiverId()));
            message.setMessage(messageLight.getMessage());
            message.setSentDateTime(LocalDateTime.now());
            message.setMessageType(MessageType.VOZNJA);
            message.setRide(rideService.findOne(id));
            message = userService.SaveMessage(message);

            messageLight.setId(message.getId());
            messageLight.setTimeOfSending(message.getSentDateTime());
            messageLight.setSenderId(id);
            return new ResponseEntity<>(messageLight, HttpStatus.CREATED);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("user.reciver.ride.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/{id}/block")
    public ResponseEntity<?> blockUser(@PathVariable Integer id) {
        try {
            userService.blockUser(id);
            return new ResponseEntity<>(new ResponseMessage("User is successfully blocked"), HttpStatus.NO_CONTENT);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(new ResponseMessage(messageSource.getMessage("user.alreadyBlocked", null, Locale.getDefault())), HttpStatus.BAD_REQUEST);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("user.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/{id}/unblock")
    public ResponseEntity<?> unblockUser(@PathVariable Integer id) {
        try {
            userService.unblockUser(id);
            return new ResponseEntity<>(new ResponseMessage("User is successfully unblocked"), HttpStatus.NO_CONTENT);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(new ResponseMessage(messageSource.getMessage("user.notBlocked", null, Locale.getDefault())), HttpStatus.BAD_REQUEST);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("user.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/{id}/note")
    public ResponseEntity<?> creatingNote(@PathVariable Integer id, @RequestBody Note note){
        try {
            note = userService.saveNote(id, note);
            return new ResponseEntity<>(new NoteLiteDTO(note), HttpStatus.OK);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("user.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/{id}/note")
    public ResponseEntity<?> findNotes(@PathVariable Integer id, Pageable page) {
        try {
            NotePageDTO notePageDTO = userService.findNotes(id, page);
            return new ResponseEntity<>(notePageDTO, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("user.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value="/{email}/id")
    public ResponseEntity<?> getUserFromEmail(@PathVariable String email){
        try {
            User retrieved = userService.findByEmail(email);
            return new ResponseEntity<>(UserDTOMapper.fromUsertoDTO(retrieved), HttpStatus.OK);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("user.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }
    }

    @MessageMapping("/message")
    public void transferMessages(MessageMostSimpleDTO msg) {
        Message message = new Message();
        User sender = this.userService.findOne(msg.getSender());
        message.setSender(sender);
        User receiver = this.userService.findOne(msg.getReceiver());
        message.setReceiver(receiver);
        Ride ride = this.rideService.findOne(msg.getRide());
        message.setRide(ride);
        message.setSentDateTime(msg.getSentDateTime());
        message.setMessageType(MessageType.VOZNJA);
        message.setMessage(msg.getMessage());
        this.userService.SaveMessage(message);
        this.simpMessagingTemplate.convertAndSend("/message/notification", msg);
    }
}
